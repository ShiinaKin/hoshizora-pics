<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import {
  AlbumApi,
  type AlbumApiApiAlbumPageGetRequest,
  type AlbumPageVO,
  type AlbumSelfInsertRequest,
  type AlbumSelfPatchRequest,
  type AlbumVO,
  Configuration
} from "api-client";
import Button from "primevue/button";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Toolbar from "primevue/toolbar";
import Dialog from "primevue/dialog";
import Popover from "primevue/popover";
import { Icon } from "@iconify/vue";
import BottomPaginator from "@/components/BottomPaginator.vue";
import AlbumCreateForm from "@/components/form/userField/album/AlbumCreateForm.vue";
import AlbumEditForm from "@/components/form/userField/album/AlbumEditForm.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import { formatUTCStringToLocale } from "@/utils/DateTimeUtils";

const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const albumApi = new AlbumApi(configuration);

const albumPage = ref(1);
const albumPageSize = ref(20);
const albumOrderBy = ref("createTime");
const albumOrder = ref("DESC");
const userId = ref(-1);
const albumSearchContent = ref("");
const albumPageRequest = computed<AlbumApiApiAlbumPageGetRequest>(() => {
  return {
    page: albumPage.value,
    pageSize: albumPageSize.value,
    orderBy: albumOrderBy.value === "" ? undefined : albumOrderBy.value,
    order: albumOrderBy.value === "" ? undefined : albumOrder.value,
    userId: userId.value === -1 ? undefined : userId.value,
    albumName: albumSearchContent.value.trim() === "" ? undefined : albumSearchContent.value.trim()
  };
});
const isAlbumPreviousPageReqTakeSearch = ref(false);
const albumCurPage = ref(1);
const albumTotalRecord = ref(1);
const albumPageData = ref<AlbumPageVO[]>([]);

watchEffect(() => {
  pageUserAlbum();
});

const albumFilterRef = ref();
const activeAlbumFilter = ref("createTimeDESC");
const activeFilterClass = ref("bg-gray-200  hover:bg-gray-200");

const albumDetail = ref<AlbumVO>();

const curAlbumId = ref(-1);

const albumCreateDialog = ref(false);
const albumDetailDialog = ref(false);
const albumEditDialog = ref(false);
const albumDeleteDialog = ref(false);

function handleFetchAlbumDetail(albumId: number) {
  fetchUserAlbum(albumId).then(() => {
    albumDetailDialog.value = true;
  });
}

function handleSettingAsDefault(albumId: number) {
  const patchRequest: AlbumSelfPatchRequest = {
    isDefault: true
  };
  patchAlbum(albumId, patchRequest);
}

function showAlbumDeleteDialog(albumId: number) {
  curAlbumId.value = albumId;
  albumDeleteDialog.value = true;
}

function showAlbumEditDialog(albumId: number) {
  fetchUserAlbum(albumId).then(() => {
    curAlbumId.value = albumId;
    albumEditDialog.value = true;
  });
}

const onCreateFormSubmit = (values: any) => {
  const insertRequest: AlbumSelfInsertRequest = {
    name: values.name,
    description: values.description
  };
  createAlbum(insertRequest).then(() => {
    albumCreateDialog.value = false;
  });
};

const onEditFormSubmit = (values: any) => {
  const patchRequest: AlbumSelfPatchRequest = {
    name: values.name,
    description: values.description
  };
  patchAlbum(curAlbumId.value, patchRequest).then(() => {
    albumEditDialog.value = false;
  });
};

async function pageUserAlbum(): Promise<void> {
  return albumApi
    .apiAlbumPageGet(albumPageRequest.value)
    .then((response) => {
      const resp = response.data!;
      if (resp.isSuccessful) {
        const pageResult = resp.data!;
        albumCurPage.value = pageResult.page;
        albumTotalRecord.value = pageResult.total;
        albumPageData.value = pageResult.data;
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function createAlbum(insertRequest: AlbumSelfInsertRequest) {
  return albumApi
    .apiAlbumPost({ albumSelfInsertRequest: insertRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myAlbumView.create.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserAlbum();
      } else {
        toast.add({
          severity: "warn",
          summary: t("myAlbumView.create.toast.failedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: "Error",
        detail: error.message,
        life: 3000
      });
    });
}

function deleteAlbum(albumId: number) {
  albumApi
    .apiAlbumAlbumIdDelete({ albumId: albumId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myAlbumView.delete.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserAlbum();
      } else {
        toast.add({
          severity: "warn",
          summary: t("myAlbumView.delete.toast.failedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: "Error",
        detail: error.message,
        life: 3000
      });
    });
  albumDeleteDialog.value = false;
}

async function patchAlbum(albumId: number, patchRequest: AlbumSelfPatchRequest) {
  return albumApi
    .apiAlbumAlbumIdPatch({
      albumId: albumId,
      albumSelfPatchRequest: patchRequest
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myAlbumView.edit.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserAlbum();
      } else {
        toast.add({
          severity: "warn",
          summary: t("myAlbumView.edit.toast.failedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: "Error",
        detail: error.message,
        life: 3000
      });
    });
}

async function fetchUserAlbum(albumId: number) {
  return albumApi
    .apiAlbumAlbumIdGet({ albumId: albumId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        albumDetail.value = resp.data!;
      } else {
        toast.add({
          severity: "warn",
          summary: t("myAlbumView.detail.toast.failedTitle"),
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
</script>

<template>
  <div class="w-full flex flex-col gap-4 bg-gray-100 p-8">
    <Toolbar class="rounded-2xl border-none text-sm">
      <template #start>
        <button
          class="flex items-center gap-1 border p-1.5 rounded-md shadow-sm text-gray-800 hover:bg-gray-50"
          @click="albumCreateDialog = true"
        >
          <Icon icon="mdi:image-album" />
          {{ t("myAlbumView.toolbar.createButton") }}
        </button>
      </template>
      <template #end>
        <div class="flex gap-2">
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="albumFilterRef.toggle"
          >
            <Icon icon="mdi:filter-multiple-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
    </Toolbar>

    <DataTable class="flex-grow overflow-y-auto" :value="albumPageData" dataKey="id" scrollable removableSort>
      <Column field="id" :header="t('myAlbumView.table.albumId')"></Column>
      <Column field="name" :header="t('myAlbumView.table.albumName')"></Column>
      <Column field="imageCount" :header="t('myAlbumView.table.imageCount')" :sortable="true"></Column>
      <Column field="isUncategorized" :header="t('myAlbumView.table.isUncategorized.title')">
        <template #body="{ data }">
          {{
            data.isUncategorized
              ? t("myAlbumView.table.isUncategorized.true")
              : t("myAlbumView.table.isUncategorized.false")
          }}
        </template>
      </Column>
      <Column field="isDefault" :header="t('myAlbumView.table.isDefault.title')">
        <template #body="{ data }">
          {{ data.isDefault ? t("myAlbumView.table.isDefault.true") : t("myAlbumView.table.isDefault.false") }}
        </template>
      </Column>
      <Column field="createTime" :header="t('myAlbumView.table.createTime')" :sortable="true">
        <template #body="{ data }">
          {{ formatUTCStringToLocale(data.createTime) }}
        </template>
      </Column>
      <Column :header="t('myAlbumView.table.ops.title')" class="w-72">
        <template #body="{ data }">
          <div class="flex gap-0.5 text-sm">
            <Button @click="handleFetchAlbumDetail(data.id)" severity="secondary" size="small">
              {{ t("myAlbumView.table.ops.detail") }}
            </Button>
            <Button @click="handleSettingAsDefault(data.id)" size="small" :disabled="data.isDefault">
              {{ t("myAlbumView.table.ops.settingAsDefault") }}
            </Button>
            <Button @click="showAlbumEditDialog(data.id)" severity="info" size="small">
              {{ t("myAlbumView.table.ops.edit") }}
            </Button>
            <Button @click="showAlbumDeleteDialog(data.id)" severity="danger" size="small">
              {{ t("myAlbumView.table.ops.delete") }}
            </Button>
          </div>
        </template>
      </Column>
    </DataTable>

    <BottomPaginator
      v-model:page="albumPage"
      v-model:page-size="albumPageSize"
      :row-options="[10, 15, 20, 25, 30]"
      :cur-page="albumCurPage"
      :total-record="albumTotalRecord"
    />
  </div>
  <!--      create-->
  <Dialog v-model:visible="albumCreateDialog" modal :header="t('myAlbumView.create.dialog.title')" class="min-w-96">
    <AlbumCreateForm @submit="onCreateFormSubmit" @cancel="albumCreateDialog = false" />
  </Dialog>
  <!--      edit-->
  <Dialog v-model:visible="albumEditDialog" modal :header="t('myAlbumView.edit.dialog.title')">
    <AlbumEditForm :album-detail="albumDetail" @submit="onEditFormSubmit" @cancel="albumEditDialog = false" />
  </Dialog>
  <!--      delete confirm-->
  <Dialog v-model:visible="albumDeleteDialog" modal :header="t('myAlbumView.delete.dialog.title')" class="min-w-96">
    <ConfirmDialog
      :header="t('myAlbumView.delete.dialog.title')"
      :main-content="t('myAlbumView.delete.dialog.warningTitle')"
      :sub-content="t('myAlbumView.delete.dialog.warningContent')"
      @cancel="albumDeleteDialog = false"
      @submit="deleteAlbum(curAlbumId)"
    />
  </Dialog>
  <!--      detail-->
  <Dialog v-model:visible="albumDetailDialog" modal :header="t('myAlbumView.detail.dialog.title')" class="min-w-96">
    <div class="flow-root">
      <dl class="-my-3 divide-y divide-gray-100 text-sm">
        <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
          <dt class="font-medium text-gray-900">{{ t("myAlbumView.detail.dialog.name") }}</dt>
          <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.name }}</dd>
        </div>

        <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
          <dt class="font-medium text-gray-900">{{ t("myAlbumView.detail.dialog.description") }}</dt>
          <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.description }}</dd>
        </div>

        <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
          <dt class="font-medium text-gray-900">{{ t("myAlbumView.detail.dialog.imageCount") }}</dt>
          <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.imageCount }}</dd>
        </div>

        <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
          <dt class="font-medium text-gray-900">{{ t("myAlbumView.detail.dialog.isUncategorized.title") }}</dt>
          <dd class="text-gray-700 sm:col-span-2">
            {{
              albumDetail?.isUncategorized
                ? t("myAlbumView.detail.dialog.isUncategorized.true")
                : t("myAlbumView.detail.dialog.isUncategorized.false")
            }}
          </dd>
        </div>

        <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
          <dt class="font-medium text-gray-900">{{ t("myAlbumView.detail.dialog.isDefault.title") }}</dt>
          <dd class="text-gray-700 sm:col-span-2">
            {{
              albumDetail?.isDefault
                ? t("myAlbumView.detail.dialog.isDefault.true")
                : t("myAlbumView.detail.dialog.isDefault.false")
            }}
          </dd>
        </div>

        <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
          <dt class="font-medium text-gray-900">{{ t("myAlbumView.detail.dialog.createTime") }}</dt>
          <dd class="text-gray-700 sm:col-span-2">
            {{ formatUTCStringToLocale(albumDetail?.createTime) }}
          </dd>
        </div>
      </dl>
    </div>
  </Dialog>
  <!--albumFilter-->
  <Popover ref="albumFilterRef">
    <div class="w-40 flex flex-col">
      <button
        class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
        :class="activeAlbumFilter === 'createTimeASC' ? activeFilterClass : ''"
        @click="
          () => {
            albumOrderBy = 'createTime';
            albumOrder = 'ASC';
            activeAlbumFilter = 'createTimeASC';
            albumFilterRef.hide();
          }
        "
      >
        <span class="flex gap-2 justify-center items-center">
          <Icon icon="mdi:sort-clock-ascending-outline" />
          {{ t("myAlbumView.filter.album.createTimeASC") }}
        </span>
      </button>
      <button
        class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
        :class="activeAlbumFilter === 'createTimeDESC' ? activeFilterClass : ''"
        @click="
          () => {
            albumOrderBy = 'createTime';
            albumOrder = 'DESC';
            activeAlbumFilter = 'createTimeDESC';
            albumFilterRef.hide();
          }
        "
      >
        <span class="flex gap-2 justify-center items-center">
          <Icon icon="mdi:sort-clock-descending-outline" />
          {{ t("myAlbumView.filter.album.createTimeDESC") }}
        </span>
      </button>
      <button
        class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
        :class="activeAlbumFilter === 'imageCountASC' ? activeFilterClass : ''"
        @click="
          () => {
            albumOrderBy = 'imageCount';
            albumOrder = 'ASC';
            activeAlbumFilter = 'imageCountASC';
            albumFilterRef.hide();
          }
        "
      >
        <span class="flex gap-2 justify-center items-center">
          <Icon icon="mdi:sort-ascending" />
          {{ t("myAlbumView.filter.album.imageCountASC") }}
        </span>
      </button>
      <button
        class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
        :class="activeAlbumFilter === 'imageCountDESC' ? activeFilterClass : ''"
        @click="
          () => {
            albumOrderBy = 'imageCount';
            albumOrder = 'DESC';
            activeAlbumFilter = 'imageCountDESC';
            albumFilterRef.hide();
          }
        "
      >
        <span class="flex gap-2 justify-center items-center">
          <Icon icon="mdi:sort-descending" />
          {{ t("myAlbumView.filter.album.imageCountDESC") }}
        </span>
      </button>
    </div>
  </Popover>
</template>

<style scoped></style>
