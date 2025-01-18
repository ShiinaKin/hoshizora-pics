<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import {
  Configuration,
  ImageApi,
  AlbumApi,
  type AlbumApiApiAlbumPageGetRequest,
  type AlbumPageVO,
  type ImageApiApiImagePageGetRequest,
  type ImageApiApiImageImageIdPatchRequest,
  type ImagePageVO,
  type ImageVO
} from "api-client";
import { useUserFieldStore } from "@/stores/counter";
import { useToast } from "primevue/usetoast";
import { Icon } from "@iconify/vue";
import { api as viewerApi } from "v-viewer";
import ImageCard from "@/components/ImageCard.vue";
import ContextMenu from "@/components/ContextMenu.vue";
import BottomPaginator from "@/components/BottomPaginator.vue";
import LoadingDialog from "@/components/LoadingDialog.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import ImageEditForm from "@/components/form/userField/image/ImageEditForm.vue";
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import InputGroup from "primevue/inputgroup";
import InputGroupAddon from "primevue/inputgroupaddon";
import Toolbar from "primevue/toolbar";
import Drawer from "primevue/drawer";
import Popover from "primevue/popover";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Paginator from "primevue/paginator";
import Dialog from "primevue/dialog";
import type { MenuItem } from "primevue/menuitem";
import md5 from "crypto-js/md5";
import { debounce } from "lodash-es";
import {
  transToBBCode,
  transToDirect,
  transToHTML,
  transToMarkdown,
  transToMarkdownWithLink
} from "@/utils/URLFormatUtils";
import type { ImageDisplay, ImageView } from "@/types/ImageType";
import { convertImageToBlob } from "@/utils/ImageUtils";
import { formatUTCStringToLocale } from "@/utils/DateTimeUtils";

const userFieldStore = useUserFieldStore();
const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const albumApi = new AlbumApi(configuration);
const imageApi = new ImageApi(configuration);

const avatarUrl = computed(() => `https://gravatar.com/avatar/${md5(userFieldStore.email)}`);

const showAlbumDrawer = ref(false);

const filterRef = ref();
const visibleFilterRef = ref();

const isPreviousPageReqTakeSearch = ref(false);

const page = ref(1);
const pageSize = ref(20);
const orderBy = ref("createTime");
const order = ref("DESC");
const albumId = ref(-1);
const isPrivate = ref();
const searchContent = ref("");
const pageRequest = computed<ImageApiApiImagePageGetRequest>(() => {
  return {
    page: page.value,
    pageSize: pageSize.value,
    orderBy: orderBy.value === "" ? undefined : orderBy.value,
    order: orderBy.value === "" ? undefined : order.value,
    albumId: albumId.value === -1 ? undefined : albumId.value,
    isPrivate: isPrivate.value === undefined ? undefined : isPrivate.value,
    search: searchContent.value.trim() === "" ? undefined : searchContent.value.trim()
  };
});

const debouncePageUserImage = debounce((request: ImageApiApiImagePageGetRequest) => {
  pageUserImage(request);
}, 300);

watchEffect(() => {
  const pageReq = pageRequest.value;
  debouncePageUserImage(pageReq);
});

const curPage = ref(1);
const totalRecord = ref(1);
const totalPage = ref(1);
const imageList = ref<ImagePageVO[]>([]);
const imageDisplayList = ref<ImageDisplay[]>([]);

const selectedImageIds = ref<number[]>([]);

const imageInfo = ref<ImageVO>();

const activeFilter = ref("createTimeDESC");
const activeVisibleFilter = ref("all");
const activeFilterClass = ref("bg-gray-200  hover:bg-gray-200");

const imageRawUrlMap = ref<Map<number, string>>(new Map<number, string>());

const imageViewerList = ref<ImageView[]>([]);
const showLoadingDialog = ref(false);

const showRawImage = async (dbClickImageIdx: number) => {
  showLoadingDialog.value = true;
  await Promise.all(
    imageDisplayList.value.map(async (image) => {
      await fetchImage(image.id);
    })
  );

  imageViewerList.value = imageDisplayList.value.map((image) => {
    return {
      imageId: image.id,
      src: image.thumbnailUrl,
      alt: image.displayName,
      "data-source": imageRawUrlMap.value.get(image.id) || ""
    } as ImageView;
  });

  showLoadingDialog.value = false;
  viewerApi({
    images: imageViewerList.value,
    options: {
      url: "data-source",
      initialViewIndex: dbClickImageIdx
    }
  });
};

const curRightClickImageId = ref<number>(-1);
const curRightClickImageIdx = ref<number>(-1);

const singleImageContextMenuRef = ref();
const multiImageContextMenuRef = ref();

const singleImageContextMenuItems = ref<MenuItem[]>([
  {
    label: t("myImageView.contextMenu.single.copyImage"),
    icon: "mdi:content-copy",
    command: () => {
      fetchImageBlob(curRightClickImageId.value)
        .then((blob) => {
          convertImageToBlob(blob as Blob).then((imageBlob) => {
            return navigator.clipboard.write([
              new ClipboardItem({
                ["image/png"]: imageBlob
              })
            ]);
          });
        })
        .then(() => {
          toast.add({
            severity: "success",
            summary: t("myImageView.copyImage.toast.successTitle"),
            life: 3000
          });
        })
        .catch((e) => {
          console.error(e);
          toast.add({
            severity: "error",
            summary: t("myImageView.copyImage.toast.failedTitle"),
            detail: e.message,
            life: 3000
          });
        });
    }
  },
  {
    label: t("myImageView.contextMenu.single.copyLink"),
    icon: "mdi:link",
    items: [
      {
        label: "Direct",
        icon: "mdi:link-variant",
        command: () => {
          const image = imageDisplayList.value[curRightClickImageIdx.value];
          if (image.isPrivate) {
            toast.add({
              severity: "warn",
              summary: t("myImageView.copyLink.toast.failedTitle"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToDirect(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: t("myImageView.copyLink.toast.successTitle"),
            life: 3000
          });
        }
      },
      {
        label: "Markdown",
        icon: "mdi:language-markdown-outline",
        command: () => {
          const image = imageDisplayList.value[curRightClickImageIdx.value];
          if (image.isPrivate) {
            toast.add({
              severity: "warn",
              summary: t("myImageView.copyLink.toast.failedTitle"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToMarkdown(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: t("myImageView.copyLink.toast.successTitle"),
            life: 3000
          });
        }
      },
      {
        label: "Markdown with link",
        icon: "mdi:language-markdown-outline",
        command: () => {
          const image = imageDisplayList.value[curRightClickImageIdx.value];
          if (image.isPrivate) {
            toast.add({
              severity: "warn",
              summary: t("myImageView.copyLink.toast.failedTitle"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToMarkdownWithLink(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: t("myImageView.copyLink.toast.successTitle"),
            life: 3000
          });
        }
      },
      {
        label: "HTML",
        icon: "mdi:language-html5",
        command: () => {
          const image = imageDisplayList.value[curRightClickImageIdx.value];
          if (image.isPrivate) {
            toast.add({
              severity: "warn",
              summary: t("myImageView.copyLink.toast.failedTitle"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToHTML(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: t("myImageView.copyLink.toast.successTitle"),
            life: 3000
          });
        }
      },
      {
        label: "BBCode",
        icon: "mdi:discussion-outline",
        command: () => {
          const image = imageDisplayList.value[curRightClickImageIdx.value];
          if (image.isPrivate) {
            toast.add({
              severity: "warn",
              summary: t("myImageView.copyLink.toast.failedTitle"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToBBCode(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: t("myImageView.copyLink.toast.successTitle"),
            life: 3000
          });
        }
      }
    ]
  },
  {
    label: t("myImageView.contextMenu.single.moveToAlbum"),
    icon: "mdi:move-to-inbox",
    command: () => {
      isSingleImageChangeAlbum.value = true;
      handleShowImageChangeAlbumDialog();
    }
  },
  {
    label: t("myImageView.contextMenu.single.settingAsPublic"),
    icon: "mdi:visibility-outline",
    command: () => {
      handleSingleChangeVisibility(false);
    }
  },
  {
    label: t("myImageView.contextMenu.single.settingAsPrivate"),
    icon: "mdi:visibility-off-outline",
    command: () => {
      handleSingleChangeVisibility(true);
    }
  },
  {
    label: t("myImageView.contextMenu.single.allowedRandomFetch"),
    icon: "el:random",
    command: () => {
      handleSingleAllowedRandomFetch(true);
    }
  },
  {
    label: t("myImageView.contextMenu.single.disallowedRandomFetch"),
    icon: "mdi:autorenew-off",
    command: () => {
      handleSingleAllowedRandomFetch(false);
    }
  },
  {
    label: t("myImageView.contextMenu.single.detail"),
    icon: "mdi:information-slab-box-outline",
    command: () => {
      handleImageDetail();
    }
  },
  {
    separator: true
  },
  {
    label: t("myImageView.contextMenu.single.edit"),
    icon: "mdi:rename-outline",
    command: () => {
      showImageEditDialog.value = true;
    }
  },
  {
    label: t("myImageView.contextMenu.single.delete"),
    icon: "mdi:delete-outline",
    command: () => {
      isSingleImageDelete.value = true;
      showImageDeleteConfirmDialog.value = true;
    }
  }
]);
const multiImageContextMenuItems = ref<MenuItem[]>([
  {
    label: t("myImageView.contextMenu.multi.moveToAlbum"),
    icon: "mdi:move-to-inbox",
    command: () => {
      isSingleImageChangeAlbum.value = false;
      handleShowImageChangeAlbumDialog();
    }
  },
  {
    label: t("myImageView.contextMenu.multi.settingAsPublic"),
    icon: "mdi:visibility-outline",
    command: () => {
      handleMultiChangeVisibility(false);
    }
  },
  {
    label: t("myImageView.contextMenu.multi.settingAsPrivate"),
    icon: "mdi:visibility-off-outline",
    command: () => {
      handleMultiChangeVisibility(true);
    }
  },
  {
    label: t("myImageView.contextMenu.multi.allowedRandomFetch"),
    icon: "el:random",
    command: () => {
      handleMultiAllowedRandomFetch(true);
    }
  },
  {
    label: t("myImageView.contextMenu.multi.disallowedRandomFetch"),
    icon: "mdi:autorenew-off",
    command: () => {
      handleMultiAllowedRandomFetch(false);
    }
  },
  {
    separator: true
  },
  {
    label: t("myImageView.contextMenu.multi.delete"),
    icon: "mdi:delete-outline",
    command: () => {
      isSingleImageDelete.value = false;
      showImageDeleteConfirmDialog.value = true;
    }
  }
]);

const showImageDetailDialog = ref(false);
const showImageChangeAlbumDialog = ref(false);
const showImageEditDialog = ref(false);
const showImageDeleteConfirmDialog = ref(false);

const albumPage = ref(1);
const albumPageSize = ref(10);
const albumPageRequest = computed<AlbumApiApiAlbumPageGetRequest>(() => {
  return {
    page: albumPage.value,
    pageSize: albumPageSize.value
  };
});

const albumCurPage = ref(-1);
const albumTotalPage = ref(-1);
const albumTotalRecord = ref(-1);
const albumPageData = ref<AlbumPageVO[]>([]);

const selectedAlbum = ref<AlbumPageVO>();

function handleShowAlbumDrawer() {
  albumPage.value = 1;
  albumPageSize.value = 10;
  pageUserAlbum().then(() => {
    showAlbumDrawer.value = true;
  });
}

function handleImageRightClick(event: MouseEvent, imageId: number, idx: number) {
  if (selectedImageIds.value.length > 1 && selectedImageIds.value.includes(imageId)) {
    multiImageContextMenuRef.value.show(event);
    singleImageContextMenuRef.value.hide();
  } else {
    curRightClickImageId.value = imageId;
    curRightClickImageIdx.value = idx;
    singleImageContextMenuRef.value.show(event);
    multiImageContextMenuRef.value.hide();
  }
}

const isSingleImageChangeAlbum = ref(true);

const isSingleImageDelete = ref(true);

function handleShowImageChangeAlbumDialog() {
  pageUserAlbum().then(() => {
    selectedAlbum.value = undefined;

    albumPage.value = 1;
    albumPageSize.value = 10;

    showImageChangeAlbumDialog.value = true;
  });
}

function handleAlbumFilterReset() {
  if (albumId.value !== -1) {
    albumId.value = -1;
    page.value = 1;
    pageSize.value = 20;
  }
  showAlbumDrawer.value = false;
}

function handleAlbumFilterSelect(inputAlbumId: number) {
  if (albumId.value === inputAlbumId) return;
  albumId.value = inputAlbumId;
  page.value = 1;
  pageSize.value = 20;
  showAlbumDrawer.value = false;
  pageUserImage(pageRequest.value);
}

function handleSingleImageChangeAlbumDialogSubmit() {
  if (selectedAlbum.value === undefined) {
    toast.add({
      severity: "warn",
      summary: t("myImageView.changeAlbum.dialog.form.verify.album.required"),
      life: 3000
    });
    return;
  }
  handleSingleChangeAlbum(curRightClickImageId.value, selectedAlbum.value.id).then(() => {
    selectedAlbum.value = undefined;
    showImageChangeAlbumDialog.value = false;
    pageUserImage(pageRequest.value);
  });
}

function handleMultiImageChangeAlbumDialogSubmit() {
  if (selectedAlbum.value === undefined) {
    toast.add({
      severity: "warn",
      summary: t("myImageView.changeAlbum.dialog.form.verify.album.required"),
      life: 3000
    });
    return;
  }
  Promise.all(selectedImageIds.value.map((imageId) => handleSingleChangeAlbum(imageId, selectedAlbum.value!.id))).then(
    () => {
      selectedAlbum.value = undefined;
      showImageChangeAlbumDialog.value = false;
      pageUserImage(pageRequest.value);
    }
  );
}

function handleImageDetail() {
  fetchImageInfo(curRightClickImageId.value).then(() => {
    showImageDetailDialog.value = true;
  });
}

async function handleSingleChangeAlbum(imageId: number, albumId: number) {
  return imageApi
    .apiImageImageIdPatch({
      imageId: imageId,
      imagePatchRequest: {
        albumId: albumId
      }
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myImageView.changeAlbum.toast.successTitle"),
          life: 3000
        });
      } else {
        toast.add({
          severity: "warn",
          summary: t("myImageView.changeAlbum.toast.failedTitle"),
          detail: `imageId: ${imageDisplayList.value[curRightClickImageIdx.value].id}, ${resp.message}`,
          life: 3000
        });
      }
    })
    .catch((e) => {
      console.error(e);
      toast.add({ severity: "error", summary: "Error", detail: e.message, life: 3000 });
    });
}

function handleSingleChangeVisibility(isPrivate: boolean) {
  imageApi
    .apiImageImageIdPatch({
      imageId: curRightClickImageId.value,
      imagePatchRequest: {
        isPrivate: isPrivate
      }
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myImageView.changeVisible.toast.successTitle"),
          life: 3000
        });
        pageUserImage(pageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myImageView.changeVisible.toast.failedTitle"),
          detail: `imageId: ${imageDisplayList.value[curRightClickImageIdx.value].id}, ${resp.message}`,
          life: 3000
        });
      }
    })
    .catch((e) => {
      console.error(e);
      toast.add({ severity: "error", summary: "Error", detail: e.message, life: 3000 });
    });
}

function handleMultiChangeVisibility(isPrivate: boolean) {
  Promise.all(
    selectedImageIds.value.map(async (imageId) => {
      await imageApi
        .apiImageImageIdPatch({
          imageId: imageId,
          imagePatchRequest: {
            isPrivate: isPrivate
          }
        })
        .then((response) => {
          const resp = response.data;
          if (resp.isSuccessful) {
            toast.add({
              severity: "success",
              summary: t("myImageView.changeVisible.toast.successTitle"),
              life: 3000
            });
          } else {
            toast.add({
              severity: "warn",
              summary: t("myImageView.changeVisible.toast.failedTitle"),
              detail: `imageId: ${imageDisplayList.value[curRightClickImageIdx.value].id}, ${resp.message}`,
              life: 3000
            });
          }
        })
        .catch((e) => {
          console.error(e);
          toast.add({ severity: "error", summary: "Error", detail: e.message, life: 3000 });
        });
    })
  ).then(() => {
    selectedImageIds.value = [];
    page.value = 1;
    pageSize.value = 20;
    pageUserImage(pageRequest.value);
  });
}

function handleSingleAllowedRandomFetch(isAllowed: boolean) {
  imageApi
    .apiImageImageIdPatch({
      imageId: curRightClickImageId.value,
      imagePatchRequest: {
        isAllowedRandomFetch: isAllowed
      }
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myImageView.changeAllowedRandomFetch.toast.successTitle"),
          life: 3000
        });
        pageUserImage(pageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myImageView.changeAllowedRandomFetch.toast.failedTitle"),
          detail: `imageId: ${imageDisplayList.value[curRightClickImageIdx.value].id}, ${resp.message}`,
          life: 3000
        });
      }
    })
    .catch((e) => {
      console.error(e);
      toast.add({ severity: "error", summary: "Error", detail: e.message, life: 3000 });
    });
}

function handleMultiAllowedRandomFetch(isAllowed: boolean) {
  Promise.all(
    selectedImageIds.value.map(async (imageId) => {
      await imageApi
        .apiImageImageIdPatch({
          imageId: imageId,
          imagePatchRequest: {
            isAllowedRandomFetch: isAllowed
          }
        })
        .then((response) => {
          const resp = response.data;
          if (resp.isSuccessful) {
            toast.add({
              severity: "success",
              summary: t("myImageView.changeAllowedRandomFetch.toast.successTitle"),
              life: 3000
            });
          } else {
            toast.add({
              severity: "warn",
              summary: t("myImageView.changeAllowedRandomFetch.toast.failedTitle"),
              detail: `imageId: ${imageDisplayList.value[curRightClickImageIdx.value].id}, ${resp.message}`,
              life: 3000
            });
          }
        })
        .catch((e) => {
          console.error(e);
          toast.add({ severity: "error", summary: "Error", detail: e.message, life: 3000 });
        });
    })
  ).then(() => {
    selectedImageIds.value = [];
    page.value = 1;
    pageSize.value = 20;
    pageUserImage(pageRequest.value);
  });
}

const onEditFormSubmit = (values: any) => {
  const patchRequest: ImageApiApiImageImageIdPatchRequest = {
    imageId: curRightClickImageId.value,
    imagePatchRequest: {
      displayName: values.name,
      description: values.description
    }
  };
  handleRenameImage(patchRequest).then(() => {
    showImageEditDialog.value = false;
  });
};

async function handleRenameImage(patchRequest: ImageApiApiImageImageIdPatchRequest) {
  return imageApi
    .apiImageImageIdPatch(patchRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myImageView.edit.toast.successTitle"),
          life: 3000
        });
        pageUserImage(pageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myImageView.edit.toast.failedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((e) => {
      console.error(e);
      toast.add({ severity: "error", summary: "Error", detail: e.message, life: 3000 });
    });
}

function handleDeleteSingleImage() {
  imageApi
    .apiImageImageIdDelete({ imageId: curRightClickImageId.value })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myImageView.delete.toast.successTitle"),
          life: 3000
        });
        pageUserImage(pageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myImageView.delete.toast.failedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
      showImageDeleteConfirmDialog.value = false;
    })
    .catch((e) => {
      console.error(e);
      toast.add({
        severity: "error",
        summary: "Error",
        detail: e.message,
        life: 3000
      });
    });
}

function handleDeleteMultiImage() {
  Promise.all(
    selectedImageIds.value.map(async (imageId) => {
      await imageApi
        .apiImageImageIdDelete({ imageId: imageId })
        .then((response) => {
          const resp = response.data;
          if (resp.isSuccessful) {
            toast.add({
              severity: "success",
              summary: t("myImageView.delete.toast.successTitle"),
              life: 3000
            });
          } else {
            toast.add({
              severity: "warn",
              summary: t("myImageView.delete.toast.failedTitle"),
              detail: resp.message,
              life: 3000
            });
          }
        })
        .catch((e) => {
          console.error(e);
          toast.add({ severity: "error", summary: "Error", detail: e.message, life: 3000 });
        });
    })
  ).then(() => {
    showImageDeleteConfirmDialog.value = false;
    selectedImageIds.value = [];
    page.value = 1;
    pageSize.value = 20;
    pageUserImage(pageRequest.value);
  });
}

function pageUserImage(pageRequest: ImageApiApiImagePageGetRequest) {
  imageApi
    .apiImagePageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        isPreviousPageReqTakeSearch.value = pageRequest.search !== undefined;
        const pageResult = resp.data!;
        curPage.value = pageResult.page;
        totalPage.value = pageResult.totalPage;
        totalRecord.value = pageResult.total;
        imageList.value = pageResult.data;
        singleImageContextMenuRef.value.hide();
        multiImageContextMenuRef.value.hide();
        selectedImageIds.value = [];
        fetchThumbnails();
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function pageUserAlbum(): Promise<void> {
  return albumApi
    .apiAlbumPageGet(albumPageRequest.value)
    .then((response) => {
      const resp = response.data!;
      if (resp.isSuccessful) {
        const pageResult = resp.data!;
        albumCurPage.value = pageResult.page;
        albumTotalPage.value = pageResult.totalPage;
        albumTotalRecord.value = pageResult.total;
        albumPageData.value = pageResult.data;
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchImageInfo(imageId: number): Promise<void> {
  return imageApi
    .apiImageImageIdInfoGet({ imageId })
    .then((response) => {
      const resp = response.data!;
      if (resp.isSuccessful) {
        imageInfo.value = resp.data!;
      } else {
        toast.add({
          severity: "warn",
          summary: t("myImageView.detail.toast.failedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchImage(imageId: number): Promise<string | void> {
  if (imageRawUrlMap.value.has(imageId)) {
    return;
  }
  return imageApi
    .apiImageImageIdFileGet({ imageId }, { responseType: "blob" })
    .then((response) => {
      const blob = response.data as unknown as Blob;
      let url = "";
      const contentType = response.headers["content-type"];
      if (contentType && contentType.includes("application/json")) {
        blob.text().then((json) => {
          const jsonResponse = JSON.parse(json);
          if (jsonResponse.isSuccessful === false)
            toast.add({ severity: "warn", summary: "Warn", detail: jsonResponse.message, life: 3000 });
        });
      } else {
        url = URL.createObjectURL(blob);
      }
      imageRawUrlMap.value.set(imageId, url);
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchImageBlob(imageId: number): Promise<Blob | void> {
  return imageApi
    .apiImageImageIdFileGet({ imageId }, { responseType: "blob" })
    .then((response) => {
      const blob = response.data as unknown as Blob;
      const contentType = response.headers["content-type"];
      if (contentType && contentType.includes("application/json")) {
        blob.text().then((json) => {
          const jsonResponse = JSON.parse(json);
          if (jsonResponse.isSuccessful === false)
            toast.add({ severity: "warn", summary: "Warn", detail: jsonResponse.message, life: 3000 });
        });
      }
      return blob;
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchThumbnails() {
  const promises = imageList.value.map((imagePageVO) =>
    imageApi
      .apiImageImageIdThumbnailGet({ imageId: imagePageVO.id }, { responseType: "blob" })
      .then((response) => {
        const blob = response.data as unknown as Blob;
        let url = "";
        const contentType = response.headers["content-type"];
        if (contentType && contentType.includes("application/json")) {
          blob.text().then((json) => {
            const jsonResponse = JSON.parse(json);
            if (jsonResponse.isSuccessful === false)
              toast.add({ severity: "warn", summary: "Warn", detail: jsonResponse.message, life: 3000 });
          });
        } else {
          url = URL.createObjectURL(blob);
        }

        return {
          id: imagePageVO.id,
          displayName: imagePageVO.displayName,
          isPrivate: imagePageVO.isPrivate,
          createTime: formatUTCStringToLocale(imagePageVO.createTime),
          username: userFieldStore.username,
          userAvatarUrl: avatarUrl.value,
          thumbnailUrl: url,
          externalUrl: imagePageVO.externalUrl === "" ? undefined : imagePageVO.externalUrl
        };
      })
      .catch((error) => {
        console.error(error);
        toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
        return null;
      })
  );
  showLoadingDialog.value = true;
  const results = await Promise.all(promises);
  imageDisplayList.value = results.filter((item) => item !== null);
  showLoadingDialog.value = false;
}
</script>

<template>
  <div class="w-full flex flex-col gap-4 bg-gray-100 p-8">
    <Toolbar class="rounded-2xl border-none text-sm">
      <template #start>
        <button
          class="flex items-center gap-1 border p-1.5 rounded-md shadow-sm text-gray-800 hover:bg-gray-50"
          @click="handleShowAlbumDrawer"
        >
          <Icon icon="mdi:image-album" />
          {{ t("myImageView.filter.album.button") }}
        </button>
      </template>

      <template #center>
        <InputGroup class="w-full">
          <InputGroupAddon>
            <Icon icon="mdi:search" class="size-4" />
          </InputGroupAddon>
          <InputText v-model="searchContent" placeholder="Search" class="w-full" />
        </InputGroup>
      </template>

      <template #end>
        <div class="flex justify-center items-center gap-2 z-30">
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="filterRef.toggle($event)"
          >
            <Icon icon="mdi:filter-multiple-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>

          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="visibleFilterRef.toggle($event)"
          >
            <Icon icon="mdi:visibility-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
    </Toolbar>

    <div class="flex-grow p-1 flex flex-wrap content-start justify-start gap-4 overflow-x-hidden overflow-y-auto">
      <ImageCard
        v-for="(imageDisplay, idx) in imageDisplayList"
        :key="idx"
        :imageId="imageDisplay.id"
        :imageSrc="imageDisplay.thumbnailUrl"
        :isPrivate="imageDisplay.isPrivate"
        :authorAvatarUrl="imageDisplay.userAvatarUrl"
        :imageName="imageDisplay.displayName"
        :uploadTime="imageDisplay.createTime"
        v-model:selected-ids="selectedImageIds"
        @dblclick="showRawImage(idx)"
        @contextmenu="handleImageRightClick($event, imageDisplay.id, idx)"
      />
      <ContextMenu
        ref="singleImageContextMenuRef"
        :menu-items="singleImageContextMenuItems"
        :pt="{
          submenu: {
            class: 'min-w-52'
          }
        }"
      />
      <ContextMenu ref="multiImageContextMenuRef" :menu-items="multiImageContextMenuItems" />
    </div>

    <BottomPaginator
      v-model:page="page"
      v-model:page-size="pageSize"
      :row-options="[10, 15, 20, 25, 30]"
      :cur-page="curPage"
      :total-record="totalRecord"
    />

    <!--image filter-->
    <Popover ref="filterRef">
      <div class="w-40 flex flex-col">
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeFilter === 'createTimeASC' ? activeFilterClass : ''"
          @click="
            () => {
              orderBy = 'createTime';
              order = 'ASC';
              activeFilter = 'createTimeASC';
              filterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-ascending-outline" />
            {{ t("myImageView.filter.image.uploadTimeASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeFilter === 'createTimeDESC' ? activeFilterClass : ''"
          @click="
            () => {
              orderBy = 'createTime';
              order = 'DESC';
              activeFilter = 'createTimeDESC';
              filterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-descending-outline" />
            {{ t("myImageView.filter.image.uploadTimeDESC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeFilter === 'sizeASC' ? activeFilterClass : ''"
          @click="
            () => {
              orderBy = 'size';
              order = 'ASC';
              activeFilter = 'sizeASC';
              filterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-ascending" />
            {{ t("myImageView.filter.image.fileSizeASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeFilter === 'sizeDESC' ? activeFilterClass : ''"
          @click="
            () => {
              orderBy = 'size';
              order = 'DESC';
              activeFilter = 'sizeDESC';
              filterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-descending" />
            {{ t("myImageView.filter.image.fileSizeDESC") }}
          </span>
        </button>
      </div>
    </Popover>
    <!--visible filter-->
    <Popover ref="visibleFilterRef">
      <div class="w-24 flex flex-col">
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeVisibleFilter === 'public' ? activeFilterClass : ''"
          @click="
            () => {
              isPrivate = false;
              activeVisibleFilter = 'public';
              visibleFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:visibility-outline" />
            {{ t("myImageView.filter.visible.public") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeVisibleFilter === 'private' ? activeFilterClass : ''"
          @click="
            () => {
              isPrivate = true;
              activeVisibleFilter = 'private';
              visibleFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:visibility-off-outline" />
            {{ t("myImageView.filter.visible.private") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeVisibleFilter === 'all' ? activeFilterClass : ''"
          @click="
            () => {
              isPrivate = undefined;
              activeVisibleFilter = 'all';
              visibleFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:visibility" />
            {{ t("myImageView.filter.visible.all") }}
          </span>
        </button>
      </div>
    </Popover>
    <!--album drawer-->
    <Drawer v-model:visible="showAlbumDrawer" position="right" class="bg-gray-100">
      <template #header>
        <div class="flex items-center gap-2 text-lg">
          <span class="font-bold">{{ t("myImageView.filter.album.drawer.title") }}</span>
          <Icon icon="mdi:restart" class="hover:cursor-pointer size-5" title="Reset" @click="handleAlbumFilterReset" />
        </div>
      </template>
      <div class="h-full flex flex-col">
        <div class="flex-grow flex flex-col gap-2 overflow-y-auto">
          <div
            v-for="(album, idx) in albumPageData"
            :key="idx"
            @click="handleAlbumFilterSelect(album.id)"
            class="p-2 flex items-center justify-between rounded-xl"
            :class="
              albumId === album.id
                ? 'bg-sky-300 hover:cursor-default'
                : 'bg-white hover:bg-gray-50 hover:cursor-pointer'
            "
          >
            <span>{{ album.id }}.</span>
            <span>{{ album.name }}</span>
            <span class="px-2 py-1 rounded-full bg-gray-100">{{ album.imageCount }}</span>
          </div>
        </div>
        <Paginator
          class="mt-auto"
          :rows="albumPageSize"
          :totalRecords="albumTotalRecord"
          template="FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
        />
      </div>
    </Drawer>
    <!--loading-->
    <LoadingDialog v-model:visible="showLoadingDialog" />
    <!--change album-->
    <Dialog
      v-model:visible="showImageChangeAlbumDialog"
      modal
      :header="t('myImageView.changeAlbum.dialog.title')"
      class="min-w-96 z-99"
    >
      <div class="flex flex-col gap-4">
        <div class="flex flex-col gap-2">
          <DataTable
            :value="albumPageData"
            paginator
            :rows="albumPageSize"
            :total-records="albumTotalRecord"
            @page="(it) => (albumPage = it.page + 1)"
            paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
            currentPageReportTemplate="{first} to {last} of {totalRecords}"
            scrollable
            scrollHeight="400px"
            selectionMode="single"
            dataKey="id"
            :metaKeySelection="false"
            v-model:selection="selectedAlbum"
          >
            <Column field="id" :header="t('myImageView.changeAlbum.dialog.form.table.albumId')"></Column>
            <Column field="name" :header="t('myImageView.changeAlbum.dialog.form.table.albumName')"></Column>
            <Column field="imageCount" :header="t('myImageView.changeAlbum.dialog.form.table.imageCount')"></Column>
            <Column
              field="isUncategorized"
              :header="t('myImageView.changeAlbum.dialog.form.table.isUncategorized.title')"
            >
              <template #body="{ data }">
                {{
                  data.isUncategorized
                    ? t("myImageView.changeAlbum.dialog.form.table.isUncategorized.true")
                    : t("myImageView.changeAlbum.dialog.form.table.isUncategorized.false")
                }}
              </template>
            </Column>
            <Column field="isDefault" :header="t('myImageView.changeAlbum.dialog.form.table.isDefault.title')">
              <template #body="{ data }">
                {{
                  data.isDefault
                    ? t("myImageView.changeAlbum.dialog.form.table.isDefault.true")
                    : t("myImageView.changeAlbum.dialog.form.table.isDefault.false")
                }}
              </template>
            </Column>
          </DataTable>
        </div>
        <div class="flex justify-end gap-2">
          <Button
            type="button"
            :label="t('myImageView.changeAlbum.dialog.form.cancelButton')"
            severity="secondary"
            @click="showImageChangeAlbumDialog = false"
          />
          <Button
            type="button"
            :label="t('myImageView.changeAlbum.dialog.form.submitButton')"
            @click="
              isSingleImageChangeAlbum
                ? handleSingleImageChangeAlbumDialogSubmit()
                : handleMultiImageChangeAlbumDialogSubmit()
            "
          />
        </div>
      </div>
    </Dialog>
    <!--image detail-->
    <Dialog
      v-model:visible="showImageDetailDialog"
      modal
      :header="t('myImageView.detail.dialog.title')"
      class="min-w-96"
    >
      <div class="flow-root">
        <dl class="-my-3 divide-y divide-gray-100 text-sm">
          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.name") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.displayName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.rawName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.originName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.type") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.mimeType }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.albumName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.albumName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.ownerName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.ownerName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.size") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.width }} * {{ imageInfo?.height }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.fileSize") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.size }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.uploadTime") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ formatUTCStringToLocale(imageInfo?.createTime) }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.isPrivate.title") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                imageInfo?.isPrivate
                  ? t("myImageView.detail.dialog.isPrivate.true")
                  : t("myImageView.detail.dialog.isPrivate.false")
              }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.isAllowedRandomFetch.title") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                imageInfo?.isAllowedRandomFetch
                  ? t("myImageView.detail.dialog.isAllowedRandomFetch.true")
                  : t("myImageView.detail.dialog.isAllowedRandomFetch.false")
              }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myImageView.detail.dialog.description") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.description }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">MD5</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.md5 }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">SHA256</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.sha256 }}</dd>
          </div>
        </dl>
      </div>
    </Dialog>
    <!--image edit-->
    <Dialog v-model:visible="showImageEditDialog" modal :header="t('myImageView.edit.dialog.title')" class="min-w-96">
      <ImageEditForm
        :imagePageVO="imageList[curRightClickImageIdx]"
        @cancel="showImageEditDialog = false"
        @submit="onEditFormSubmit"
      />
    </Dialog>
    <!--image delete confirm-->
    <ConfirmDialog
      v-model:visible="showImageDeleteConfirmDialog"
      :header="t('myImageView.delete.dialog.title')"
      :main-content="t('myImageView.delete.dialog.mainContent')"
      :sub-content="t('myImageView.delete.dialog.subContent')"
      :cancel-btn-msg="t('myImageView.delete.dialog.cancelButton')"
      :submit-btn-msg="t('myImageView.delete.dialog.submitButton')"
      @cancel="showImageDeleteConfirmDialog = false"
      @confirm="isSingleImageDelete ? handleDeleteSingleImage() : handleDeleteMultiImage()"
    />
  </div>
</template>

<style scoped></style>
