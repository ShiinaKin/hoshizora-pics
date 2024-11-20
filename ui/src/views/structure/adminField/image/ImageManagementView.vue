<script setup lang="ts">
import { computed, ref, watch, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import {
  Configuration,
  ImageApi,
  UserApi,
  type ImageApiApiImageManagePageGetRequest,
  type UserApiApiUserManagePageGetRequest,
  type ImageManagePageVO,
  type ImageManageVO,
  type UserPageVO
} from "api-client";
import Toolbar from "primevue/toolbar";
import InputText from "primevue/inputtext";
import Button from "primevue/button";
import InputGroupAddon from "primevue/inputgroupaddon";
import InputGroup from "primevue/inputgroup";
import Listbox from "primevue/listbox";
import Popover from "primevue/popover";
import Dialog from "primevue/dialog";
import type { VirtualScrollerLazyEvent } from "primevue";
import type { MenuItem } from "primevue/menuitem";
import { Icon } from "@iconify/vue";
import BottomPaginator from "@/components/BottomPaginator.vue";
import ManageImageCard from "@/components/ManageImageCard.vue";
import ContextMenu from "@/components/ContextMenu.vue";
import LoadingDialog from "@/components/LoadingDialog.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import type { ImageDisplay, ImageView } from "@/types/ImageType";
import { debounce } from "lodash-es";
import md5 from "crypto-js/md5";
import { convertImageToBlob } from "@/utils/ImageUtils";
import {
  transToBBCode,
  transToDirect,
  transToHTML,
  transToMarkdown,
  transToMarkdownWithLink
} from "@/utils/URLFormatUtils";
import { formatUTCStringToLocale } from "@/utils/DateTimeUtils";
import { api as viewerApi } from "v-viewer";

const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const imageApi = new ImageApi(configuration);
const userApi = new UserApi(configuration);

const imagePage = ref(1);
const imagePageSize = ref(15);
const imageOrderBy = ref("createTime");
const imageOrder = ref("DESC");
const userId = ref(-1);
const albumId = ref(-1);
const isPrivate = ref();
const imageSearchContent = ref("");
const imagePageRequest = computed<ImageApiApiImageManagePageGetRequest>(() => {
  return {
    page: imagePage.value,
    pageSize: imagePageSize.value,
    orderBy: imageOrderBy.value === "" ? undefined : imageOrderBy.value,
    order: imageOrderBy.value === "" ? undefined : imageOrder.value,
    userId: userId.value === -1 ? undefined : userId.value,
    albumId: albumId.value === -1 ? undefined : albumId.value,
    isPrivate: isPrivate.value === undefined ? undefined : isPrivate.value,
    search: imageSearchContent.value.trim() === "" ? undefined : imageSearchContent.value.trim()
  };
});
const isImagePreviousPageReqTakeSearch = ref(false);
const imageCurPage = ref(1);
const imageTotalRecord = ref(1);

const userPage = ref(1);
const userPageSize = ref(15);
const userOrderBy = ref("createTime");
const userOrder = ref("DESC");
const usernameSearchContent = ref("");
const userPageRequest = computed<ImageApiApiImageManagePageGetRequest>(() => {
  return {
    page: userPage.value,
    pageSize: userPageSize.value,
    orderBy: userOrderBy.value === "" ? undefined : userOrderBy.value,
    order: userOrder.value === "" ? undefined : userOrder.value,
    usernameSearch: usernameSearchContent.value.trim() === "" ? undefined : usernameSearchContent.value.trim()
  };
});
const isUserPreviousPageReqTakeUsernameSearch = ref(false);
const userCurPage = ref(1);
const userTotalRecord = ref(-1);

const imageList = ref<ImageManagePageVO[]>([]);
const imageDisplayList = ref<ImageDisplay[]>([]);
const userList = ref<UserPageVO[]>([]);

const debouncePageImage = debounce((request: ImageApiApiImageManagePageGetRequest) => {
  pageImage(request);
}, 200);
const debouncePageUser = debounce(() => {
  userPage.value = 1;
  userPageSize.value = 15;
  userFilterLoading.value = true;
  pageUser(userPageRequest.value)
    .then((data) => {
      if (data instanceof Array) userList.value = data;
    })
    .finally(() => (userFilterLoading.value = false));
}, 300);

watchEffect(() => {
  const imagePageReq = imagePageRequest.value;
  debouncePageImage(imagePageReq);
});

watch(usernameSearchContent, (newVal, oldVal) => {
  if (newVal.trim() === oldVal.trim() && newVal.trim() === "") return;
  debouncePageUser();
});

const imageInfo = ref<ImageManageVO>();

const userFilterRef = ref();
const userFilterLoading = ref(false);
const imageFilterRef = ref();
const visibleFilterRef = ref();

const selectedUser = ref();
const activeImageFilter = ref("createTimeDESC");
const activeVisibleFilter = ref("all");
const activeFilterClass = ref("bg-gray-200  hover:bg-gray-200");

const showLoadingDialog = ref(false);
const showImageDetailDialog = ref(false);
const showImageDeleteConfirmDialog = ref(false);

const isSingleImageDelete = ref(true);

const selectedImageIds = ref<number[]>([]);
const curRightClickImageId = ref<number>(-1);
const curRightClickImageIdx = ref<number>(-1);
const singleImageContextMenuRef = ref();
const multiImageContextMenuRef = ref();
const singleImageContextMenuItems = ref<MenuItem[]>([
  {
    label: "复制图片",
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
            summary: "Success",
            detail: t("myImageView.myImageDialogImageCopySuccessMessage"),
            life: 3000
          });
        })
        .catch((e) => {
          console.error(e);
          toast.add({ severity: "error", summary: "Error", detail: e.message, life: 3000 });
        });
    }
  },
  {
    label: "复制链接",
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
              summary: "Warning",
              detail: t("myImageView.myImageDialogImageCopyPrivateWarningMessage"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToDirect(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: "Success",
            detail: t("myImageView.myImageDialogImageCopySuccessMessage"),
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
              summary: "Warning",
              detail: t("myImageView.myImageDialogImageCopyPrivateWarningMessage"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToMarkdown(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: "Success",
            detail: t("myImageView.myImageDialogImageCopySuccessMessage"),
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
              summary: "Warning",
              detail: t("myImageView.myImageDialogImageCopyPrivateWarningMessage"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToMarkdownWithLink(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: "Success",
            detail: t("myImageView.myImageDialogImageCopySuccessMessage"),
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
              summary: "Warning",
              detail: t("myImageView.myImageDialogImageCopyPrivateWarningMessage"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToHTML(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: "Success",
            detail: t("myImageView.myImageDialogImageCopySuccessMessage"),
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
              summary: "Warning",
              detail: t("myImageView.myImageDialogImageCopyPrivateWarningMessage"),
              life: 3000
            });
            return;
          }
          navigator.clipboard.writeText(transToBBCode(image.displayName, image.externalUrl!));
          toast.add({
            severity: "success",
            summary: "Success",
            detail: t("myImageView.myImageDialogImageCopySuccessMessage"),
            life: 3000
          });
        }
      }
    ]
  },
  {
    label: "设为公开",
    icon: "mdi:visibility-outline",
    command: () => {
      handleSingleChangeVisibility(false);
    }
  },
  {
    label: "设为私密",
    icon: "mdi:visibility-off-outline",
    command: () => {
      handleSingleChangeVisibility(true);
    }
  },
  {
    label: "详细信息",
    icon: "mdi:information-slab-box-outline",
    command: () => {
      handleImageDetail();
    }
  },
  {
    separator: true
  },
  {
    label: "删除",
    icon: "mdi:delete-outline",
    command: () => {
      isSingleImageDelete.value = true;
      showImageDeleteConfirmDialog.value = true;
    }
  }
]);
const multiImageContextMenuItems = ref<MenuItem[]>([
  {
    label: "设为公开",
    icon: "mdi:visibility-outline",
    command: () => {
      handleMultiChangeVisibility(false);
    }
  },
  {
    label: "设为私密",
    icon: "mdi:visibility-off-outline",
    command: () => {
      handleMultiChangeVisibility(true);
    }
  },
  {
    separator: true
  },
  {
    label: "删除",
    icon: "mdi:delete-outline",
    command: () => {
      isSingleImageDelete.value = false;
      showImageDeleteConfirmDialog.value = true;
    }
  }
]);

const imageRawUrlMap = ref<Map<number, string>>(new Map<number, string>());
const imageViewerList = ref<ImageView[]>([]);

watch(selectedUser, (newVal) => {
  if (!newVal) return;
  if (newVal.id === userId.value) return;
  imagePage.value = 1;
  userId.value = newVal.id;
});

function handleOpenUserFilter(event: MouseEvent) {
  if (userList.value.length === 0) {
    userFilterLoading.value = true;
    pageUser(userPageRequest.value)
      .then((data) => {
        if (data instanceof Array) userList.value = data;
      })
      .then(() => (userFilterLoading.value = false));
  }
  userFilterRef.value.toggle(event);
}

const debounceHandleUserFilterLazyLoading = debounce(
  (event: VirtualScrollerLazyEvent) => handleUserFilterLazyLoading(event),
  200
);

function handleUserFilterLazyLoading(event: VirtualScrollerLazyEvent) {
  const { last } = event;
  if (userTotalRecord.value === last) return;
  userPage.value += 1;
  pageUser(userPageRequest.value)
    .then((data) => {
      if (data) userList.value.push(...data);
    })
    .then(() => (userFilterLoading.value = false));
}

function handleResetUserFilter() {
  userId.value = -1;
  selectedUser.value = undefined;
  userPage.value = 1;
}

async function handleShowRawImage(dbClickImageIdx: number) {
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

function handleImageDetail() {
  fetchImageInfo(curRightClickImageId.value).then(() => {
    showImageDetailDialog.value = true;
  });
}

function handleSingleChangeVisibility(isPrivate: boolean) {
  imageApi
    .apiImageManageImageIdPatch({
      imageId: curRightClickImageId.value,
      imageManagePatchRequest: {
        isPrivate: isPrivate
      }
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: "Success",
          detail: t("myImageView.myImageDialogImageChangeVisibleSuccessMessage"),
          life: 3000
        });
        pageImage(imagePageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myImageView.myImageDialogImageChangeVisibleFailedTitle"),
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
        .apiImageManageImageIdPatch({
          imageId: imageId,
          imageManagePatchRequest: {
            isPrivate: isPrivate
          }
        })
        .then((response) => {
          const resp = response.data;
          if (resp.isSuccessful) {
            toast.add({
              severity: "success",
              summary: "Success",
              detail: t("myImageView.myImageDialogImageChangeVisibleSuccessMessage"),
              life: 3000
            });
          } else {
            toast.add({
              severity: "warn",
              summary: t("myImageView.myImageDialogImageChangeVisibleFailedTitle"),
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
    pageImage(imagePageRequest.value);
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
          summary: "Success",
          detail: t("myImageView.myImageDialogImageDeleteConfirmSuccessTitle"),
          life: 3000
        });
        pageImage(imagePageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myImageView.myImageDialogImageDeleteConfirmFailedTitle"),
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
        summary: t("myImageView.myImageDialogImageDeleteConfirmFailedTitle"),
        detail: e.message,
        life: 3000
      });
    });
}

function handleDeleteMultiImage() {
  Promise.all(
    selectedImageIds.value.map(async (imageId) => {
      await imageApi
        .apiImageManageImageIdDelete({ imageId: imageId })
        .then((response) => {
          const resp = response.data;
          if (resp.isSuccessful) {
            toast.add({
              severity: "success",
              summary: "Success",
              detail: t("myImageView.myImageDialogImageDeleteConfirmSuccessTitle"),
              life: 3000
            });
          } else {
            toast.add({
              severity: "warn",
              summary: t("myImageView.myImageDialogImageDeleteConfirmFailedTitle"),
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
    pageImage(imagePageRequest.value);
  });
}

async function pageUser(pageRequest: UserApiApiUserManagePageGetRequest): Promise<Array<UserPageVO> | void> {
  return userApi
    .apiUserManagePageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        isUserPreviousPageReqTakeUsernameSearch.value = pageRequest.usernameSearch !== undefined;
        const pageResult = resp.data!;
        userCurPage.value = pageResult.page;
        userTotalRecord.value = pageResult.total;
        return pageResult.data;
      } else {
        toast.add({ severity: "warn", summary: "Warn", detail: resp.message, life: 3000 });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

function pageImage(pageRequest: ImageApiApiImageManagePageGetRequest) {
  imageApi
    .apiImageManagePageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        isImagePreviousPageReqTakeSearch.value = pageRequest.search !== undefined;
        const pageResult = resp.data!;
        imageCurPage.value = pageResult.page;
        imageTotalRecord.value = pageResult.total;
        imageList.value = pageResult.data;
        fetchImageThumbnails();
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchImageInfo(imageId: number): Promise<void> {
  return imageApi
    .apiImageManageImageIdInfoGet({ imageId })
    .then((response) => {
      const resp = response.data!;
      if (resp.isSuccessful) {
        imageInfo.value = resp.data!;
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchImageBlob(imageId: number): Promise<Blob | void> {
  return imageApi
    .apiImageManageImageIdGet({ imageId }, { responseType: "arraybuffer" })
    .then((response) => {
      const uIntArr = new Uint8Array(response.data);
      return new Blob([uIntArr], { type: "image/*" });
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
    .apiImageManageImageIdGet({ imageId }, { responseType: "arraybuffer" })
    .then((response) => {
      const uIntArr = new Uint8Array(response.data);
      const blob = new Blob([uIntArr], { type: "image/*" });
      const url = URL.createObjectURL(blob);
      imageRawUrlMap.value.set(imageId, url);
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchImageThumbnails() {
  const promises = imageList.value.map((imagePageVO) =>
    imageApi
      .apiImageManageImageIdThumbnailGet({ imageId: imagePageVO.id }, { responseType: "arraybuffer" })
      .then((response) => {
        const uIntArr = new Uint8Array(response.data);
        const blob = new Blob([uIntArr], { type: "image/*" });
        const url = URL.createObjectURL(blob);

        return {
          id: imagePageVO.id,
          displayName: imagePageVO.displayName,
          isPrivate: imagePageVO.isPrivate,
          createTime: formatUTCStringToLocale(imagePageVO.createTime),
          username: imagePageVO.username,
          userAvatarUrl: `https://gravatar.com/avatar/${md5(imagePageVO.userEmail)}`,
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
  const results = await Promise.all(promises);
  imageDisplayList.value = results.filter(item => item !== null);
}
</script>

<template>
  <div class="w-full flex flex-col gap-4 bg-gray-100 p-8">
    <Toolbar>
      <template #start></template>
      <template #center>
        <InputGroup class="w-full">
          <InputGroupAddon>
            <Icon icon="mdi:search" class="size-4" />
          </InputGroupAddon>
          <InputText v-model="imageSearchContent" placeholder="Search" class="w-full" />
        </InputGroup>
      </template>
      <template #end>
        <div class="flex gap-2">
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="handleOpenUserFilter"
          >
            <Icon icon="mdi:account-filter-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="imageFilterRef.toggle"
          >
            <Icon icon="mdi:filter-multiple-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="visibleFilterRef.toggle"
          >
            <Icon icon="mdi:visibility-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
    </Toolbar>

    <div class="flex-grow p-1 flex flex-wrap content-start justify-start gap-4 overflow-x-hidden overflow-y-auto">
      <ManageImageCard
        v-for="(imageDisplay, idx) in imageDisplayList"
        :key="idx"
        :imageId="imageDisplay.id"
        :imageSrc="imageDisplay.thumbnailUrl"
        :imageName="imageDisplay.displayName"
        :isPrivate="imageDisplay.isPrivate"
        :uploadTime="imageDisplay.createTime"
        :username="imageDisplay.username"
        :authorAvatarUrl="imageDisplay.userAvatarUrl"
        v-model:selectedIds="selectedImageIds"
        @dblclick="handleShowRawImage(idx)"
        @contextmenu="handleImageRightClick($event, imageDisplay.id, idx)"
      />
      <ContextMenu ref="singleImageContextMenuRef" :menu-items="singleImageContextMenuItems" />
      <ContextMenu ref="multiImageContextMenuRef" :menu-items="multiImageContextMenuItems" />
    </div>

    <BottomPaginator
      v-model:page="imagePage"
      v-model:page-size="imagePageSize"
      :row-options="[10, 15, 20, 25, 30, 35, 40]"
      :total-record="imageTotalRecord"
      :cur-page="imageCurPage"
    />

    <LoadingDialog v-model:visible="showLoadingDialog" />
    <!--userFilter-->
    <Popover ref="userFilterRef">
      <Listbox
        v-model="selectedUser"
        :options="userList"
        optionLabel="username"
        :virtualScrollerOptions="{
          lazy: true,
          showLoader: true,
          loading: userFilterLoading,
          onLazyLoad: debounceHandleUserFilterLazyLoading
        }"
        class="w-full md:w-56"
      >
        <template #header>
          <div class="flex gap-1">
            <InputText v-model="usernameSearchContent" placeholder="Search" class="w-full" />
            <Button severity="secondary" @click="handleResetUserFilter">
              <Icon icon="mdi:backup-restore" class="size-6" />
            </Button>
          </div>
        </template>
        <template #option="slotProps">
          <div class="flex items-center">
            <img
              :alt="slotProps.option.username"
              :src="`https://gravatar.com/avatar/${md5(slotProps.option.email)}`"
              :class="`rounded-full mr-2`"
              class="w-4"
            />
            <div>{{ slotProps.option.username }}</div>
          </div>
        </template>
      </Listbox>
    </Popover>
    <!--imageFilter-->
    <Popover ref="imageFilterRef">
      <div class="w-40 flex flex-col">
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'createTimeASC' ? activeFilterClass : ''"
          @click="
            () => {
              imageOrderBy = 'createTime';
              imageOrder = 'ASC';
              activeImageFilter = 'createTimeASC';
              imageFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-ascending-outline" />
            {{ t("adminImageManageView.imageFilter.uploadTimeASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'createTimeDESC' ? activeFilterClass : ''"
          @click="
            () => {
              imageOrderBy = 'createTime';
              imageOrder = 'DESC';
              activeImageFilter = 'createTimeDESC';
              imageFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-descending-outline" />
            {{ t("adminImageManageView.imageFilter.uploadTimeDESC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'sizeASC' ? activeFilterClass : ''"
          @click="
            () => {
              imageOrderBy = 'size';
              imageOrder = 'ASC';
              activeImageFilter = 'sizeASC';
              imageFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-ascending" />
            {{ t("adminImageManageView.imageFilter.fileSizeASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'sizeDESC' ? activeFilterClass : ''"
          @click="
            () => {
              imageOrderBy = 'size';
              imageOrder = 'DESC';
              activeImageFilter = 'sizeDESC';
              imageFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-descending" />
            {{ t("adminImageManageView.imageFilter.fileSizeDESC") }}
          </span>
        </button>
      </div>
    </Popover>
    <!--visibleFilter-->
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
            {{ t("adminImageManageView.visibleFilter.publicVisible") }}
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
            {{ t("adminImageManageView.visibleFilter.privateVisible") }}
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
            {{ t("adminImageManageView.visibleFilter.allVisible") }}
          </span>
        </button>
      </div>
    </Popover>
    <!--image detail-->
    <Dialog
      v-model:visible="showImageDetailDialog"
      modal
      :header="t('adminImageManageView.imageDetail.title')"
      class="min-w-96"
    >
      <div class="flow-root">
        <dl class="-my-3 divide-y divide-gray-100 text-sm">
          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.displayName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageRawName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.originName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageType") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.mimeType }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageAlbumName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.albumName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageOwnerName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.ownerName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageStrategyName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.strategyName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageStrategyType") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.strategyType }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageSize") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ imageInfo?.width }} * {{ imageInfo?.height }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageFileSize") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ imageInfo?.size.toFixed(2) }} {{ t("adminImageManageView.imageDetail.imageFileSizeUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageUploadTime") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ formatUTCStringToLocale(imageInfo?.createTime) }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminImageManageView.imageDetail.imageIsPublic") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                imageInfo?.isPrivate
                  ? t("adminImageManageView.imageDetail.imageIsPublicNo")
                  : t("adminImageManageView.imageDetail.imageIsPublicYes")
              }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">
              {{ t("adminImageManageView.imageDetail.imageDesc") }}
            </dt>
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
    <!--delete confirm-->
    <ConfirmDialog
      v-model:visible="showImageDeleteConfirmDialog"
      :header="t('adminImageManageView.imageDelete.confirmDialog.header')"
      :main-content="t('adminImageManageView.imageDelete.confirmDialog.mainContent')"
      :sub-content="t('adminImageManageView.imageDelete.confirmDialog.subContent')"
      :cancel-btn-msg="t('adminImageManageView.imageDelete.confirmDialog.cancelButton')"
      :submit-btn-msg="t('adminImageManageView.imageDelete.confirmDialog.submitButton')"
      @cancel="showImageDeleteConfirmDialog = false"
      @confirm="isSingleImageDelete ? handleDeleteSingleImage() : handleDeleteMultiImage()"
    />
  </div>
</template>

<style scoped></style>
