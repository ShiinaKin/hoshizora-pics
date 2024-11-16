<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import {
  Configuration,
  AlbumApi,
  type AlbumApiApiAlbumPageGetRequest,
  type AlbumPageVO,
  type AlbumVO,
  type AlbumSelfInsertRequest,
  type AlbumSelfPatchRequest
} from "api-client";
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import FloatLabel from "primevue/floatlabel";
import IftaLabel from "primevue/iftalabel";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Paginator from "primevue/paginator";
import Dialog from "primevue/dialog";
import { useToast } from "primevue/usetoast";
import { useField, useForm } from "vee-validate";
import { toTypedSchema } from "@vee-validate/yup";
import * as yup from "yup";
import { Icon } from "@iconify/vue";
import dayjs from "dayjs";

const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const albumApi = new AlbumApi(configuration);

const albumPage = ref(1);
const albumPageSize = ref(20);
const albumPageRequest = computed<AlbumApiApiAlbumPageGetRequest>(() => {
  return {
    page: albumPage.value,
    pageSize: albumPageSize.value
  };
});

watchEffect(() => {
  pageUserAlbum();
});

const albumCurPage = ref(-1);
const albumTotalPage = ref(-1);
const albumTotalRecord = ref(-1);
const albumPageData = ref<AlbumPageVO[]>([]);

const albumDetail = ref<AlbumVO>();

const curAlbumId = ref(-1);

const insertRequest = ref<AlbumSelfInsertRequest>({
  name: "",
  description: null
});

const albumCreateDialog = ref(false);
const albumDetailDialog = ref(false);
const albumEditDialog = ref(false);
const albumDeleteDialog = ref(false);

const albumEditValidationSchema = toTypedSchema(
  yup
    .object({
      patchAlbumName: yup
        .string()
        .trim()
        .test("not-both-empty", t("message.myAlbumEditDialogValidationFailedMessage"), (value) => {
          return (
            // afterMount, dont show error
            (value === undefined && patchAlbumDesc.value === undefined) ||
            // self is not empty
            (value !== undefined && value !== "") ||
            // self is empty, but another is not empty
            ((value === undefined || value === "") && patchAlbumDesc.value !== undefined && patchAlbumDesc.value !== "")
          );
        }),
      patchAlbumDesc: yup
        .string()
        .trim()
        .test("not-both-empty", t("message.myAlbumEditDialogValidationFailedMessage"), (value) => {
          return (
            (value === undefined && patchAlbumName.value === undefined) ||
            (value !== undefined && value !== "") ||
            ((value === undefined || value === "") && patchAlbumName.value !== undefined && patchAlbumName.value !== "")
          );
        })
    })
    .test("not-both-empty", t("message.myAlbumEditDialogValidationFailedMessage"), (values) => {
      return (
        // afterMount, dont show error and disable submit button
        (values.patchAlbumName !== undefined && values.patchAlbumName !== "") ||
        (values.patchAlbumDesc !== undefined && values.patchAlbumDesc !== "")
      );
    })
);

const { handleSubmit, errors, meta, validate } = useForm({
  validationSchema: albumEditValidationSchema,
  validateOnMount: false
});

const { value: patchAlbumName, handleBlur: nameBlur } = useField<string | undefined>("patchAlbumName");
const { value: patchAlbumDesc, handleBlur: descriptionBlur } = useField<string | undefined>("patchAlbumDesc");

const handleBlur = (field: "name" | "description") => {
  if (field === "name") {
    nameBlur();
  } else {
    descriptionBlur();
  }
  validate();
};

function showCreateAlbumDialog() {
  insertRequest.value.name = "";
  insertRequest.value.description = null;
  albumCreateDialog.value = true;
}

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

const handleEditAlbum = handleSubmit((values) => {
  const patchRequest: AlbumSelfPatchRequest = {
    name: values.patchAlbumName || albumDetail.value!.name,
    description: values.patchAlbumDesc || albumDetail.value!.description
  };
  patchAlbum(curAlbumId.value, patchRequest);
  albumEditDialog.value = false;
});

function showAlbumDeleteDialog(albumId: number) {
  curAlbumId.value = albumId;
  albumDeleteDialog.value = true;
}

function showAlbumEditDialog(albumId: number) {
  fetchUserAlbum(albumId).then(() => {
    curAlbumId.value = albumId;
    patchAlbumName.value = undefined;
    patchAlbumDesc.value = undefined;
    albumEditDialog.value = true;
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

function createAlbum() {
  albumApi
    .apiAlbumPost({ albumSelfInsertRequest: insertRequest.value })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("message.myAlbumCreateDialogSuccessTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserAlbum();
      } else {
        toast.add({
          severity: "warn",
          summary: t("message.myAlbumCreateDialogFailedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: t("message.myAlbumCreateDialogFailedTitle"),
        detail: error.message,
        life: 3000
      });
    });
  albumCreateDialog.value = false;
}

function deleteAlbum(albumId: number) {
  albumApi
    .apiAlbumAlbumIdDelete({ albumId: albumId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("message.myAlbumDeleteConfirmDialogSuccessTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserAlbum();
      } else {
        toast.add({
          severity: "warn",
          summary: t("message.myAlbumDeleteConfirmDialogFailedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: t("message.myAlbumDeleteConfirmDialogFailedTitle"),
        detail: error.message,
        life: 3000
      });
    });
  albumDeleteDialog.value = false;
}

function patchAlbum(albumId: number, patchRequest: AlbumSelfPatchRequest) {
  albumApi
    .apiAlbumAlbumIdPatch({
      albumId: albumId,
      albumSelfPatchRequest: patchRequest
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("message.myAlbumEditDialogSuccessTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserAlbum();
      } else {
        toast.add({
          severity: "warn",
          summary: t("message.myAlbumEditDialogFailedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: t("message.myAlbumEditDialogFailedTitle"),
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
        toast.add({ severity: "warn", summary: "Warning", detail: resp.message, life: 3000 });
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
    <div class="flex justify-between items-center bg-white rounded-2xl p-4">
      <div class="ml-auto">
        <button
          class="flex items-center gap-1 border p-1.5 rounded-md shadow-sm text-gray-800 hover:bg-gray-50 text-sm"
          @click="showCreateAlbumDialog"
        >
          <Icon icon="mdi:image-album" />
          {{ t("message.myAlbumCreateButton") }}
        </button>
      </div>
    </div>
    <DataTable
      class="flex-grow overflow-y-auto"
      :value="albumPageData"
      dataKey="id"
      scrollable
      removableSort
      tableStyle="min-width: 50rem"
    >
      <Column field="id" :header="t('message.myAlbumTableAlbumId')"></Column>
      <Column field="name" :header="t('message.myAlbumTableAlbumName')"></Column>
      <Column sortable field="imageCount" :header="t('message.myAlbumTableImageCount')"></Column>
      <Column field="isUncategorized" :header="t('message.myAlbumTableIsUncategorized')"></Column>
      <Column field="isDefault" :header="t('message.myAlbumTableIsDefault')"></Column>
      <Column sortable field="createTime" :header="t('message.myAlbumTableCreateTime')">
        <template #body="{ data }">
          {{ dayjs(String(data.createTime)).format("YYYY/MM/DD HH:mm:ss") }}
        </template>
      </Column>
      <Column :header="t('message.myAlbumTableOpsTitle')" class="w-72">
        <template #body="{ data }">
          <div class="flex gap-0.5 text-sm">
            <Button @click="handleFetchAlbumDetail(data.id)" severity="secondary" size="small">
              {{ t("message.myAlbumTableOpsDetail") }}
            </Button>
            <Button @click="handleSettingAsDefault(data.id)" size="small" :disabled="data.isDefault">
              {{ t("message.myAlbumTableOpsSettingAsDefault") }}
            </Button>
            <Button @click="showAlbumEditDialog(data.id)" severity="info" size="small">
              {{ t("message.myAlbumTableOpsEdit") }}
            </Button>
            <Button @click="showAlbumDeleteDialog(data.id)" severity="danger" size="small">
              {{ t("message.myAlbumTableOpsDelete") }}
            </Button>
          </div>
        </template>
      </Column>
    </DataTable>
    <!--      create-->
    <Dialog v-model:visible="albumCreateDialog" modal :header="t('message.myAlbumCreateDialogTitle')" class="w-96">
      <form @submit.prevent="createAlbum">
        <div class="flex flex-col gap-4 m-4">
          <div class="flex flex-col gap-2 w-full">
            <FloatLabel variant="on">
              <InputText id="newAlbumName" v-model="insertRequest.name" class="w-full" pattern="\S+" required />
              <label for="newAlbumName">{{ t("message.myAlbumCreateDialogAlbumName") }}</label>
            </FloatLabel>
            <FloatLabel variant="on">
              <InputText id="newAlbumDesc" v-model="insertRequest.description" class="w-full" />
              <label for="newAlbumDesc">{{ t("message.myAlbumCreateDialogAlbumDesc") }}</label>
            </FloatLabel>
          </div>
          <div class="flex justify-end gap-2">
            <Button
              type="button"
              :label="t('message.myAlbumCreateDialogCancelButton')"
              severity="secondary"
              @click="albumCreateDialog = false"
            />
            <Button type="submit" :label="t('message.myAlbumCreateDialogSubmitButton')" />
          </div>
        </div>
      </form>
    </Dialog>
    <!--      edit-->
    <Dialog v-model:visible="albumEditDialog" modal :header="t('message.myAlbumEditDialogTitle')">
      <form @submit="handleEditAlbum">
        <div class="flex flex-col gap-4 m-4 w-96">
          <div class="flex flex-col gap-2 w-full">
            <IftaLabel variant="on">
              <InputText
                id="editAlbumName"
                v-model="patchAlbumName"
                :placeholder="albumDetail!.name"
                class="w-full"
                @blur="handleBlur('name')"
              />
              <label for="editAlbumName">{{ t("message.myAlbumEditDialogAlbumName") }}</label>
              <p class="text-red-500 text-sm">{{ errors.patchAlbumName }}</p>
            </IftaLabel>

            <IftaLabel variant="on">
              <InputText
                id="editAlbumDesc"
                v-model="patchAlbumDesc"
                :placeholder="albumDetail!.description || ''"
                class="w-full"
                @blur="handleBlur('description')"
              />
              <label for="editAlbumDesc">{{ t("message.myAlbumEditDialogAlbumDesc") }}</label>
              <p class="text-red-500 text-sm">{{ errors.patchAlbumDesc }}</p>
            </IftaLabel>
          </div>
          <div class="flex justify-end gap-2">
            <Button
              type="button"
              :label="t('message.myAlbumEditDialogCancelButton')"
              severity="secondary"
              @click="albumEditDialog = false"
            />
            <Button type="submit" :label="t('message.myAlbumEditDialogSubmitButton')" :disabled="!meta.valid" />
          </div>
        </div>
      </form>
    </Dialog>
    <!--      delete confirm-->
    <Dialog
      v-model:visible="albumDeleteDialog"
      modal
      :header="t('message.myAlbumDeleteConfirmDialogTitle')"
      class="w-72"
    >
      <div class="flex flex-col gap-4 mb-4">
        <h2 class="text-lg">{{ t("message.myAlbumDeleteConfirmDialogWarningTitle") }}</h2>
        <p class="text-sm">{{ t("message.myAlbumDeleteConfirmDialogWarningContent") }}</p>
      </div>
      <div class="flex justify-end gap-2">
        <Button
          type="button"
          :label="t('message.myAlbumDeleteConfirmDialogCancelButton')"
          severity="secondary"
          @click="albumDeleteDialog = false"
        ></Button>
        <Button
          type="button"
          :label="t('message.myAlbumDeleteConfirmDialogSubmitButton')"
          severity="danger"
          @click="deleteAlbum(curAlbumId)"
        ></Button>
      </div>
    </Dialog>
    <!--      detail-->
    <Dialog v-model:visible="albumDetailDialog" modal :header="t('message.myImageDialogImageDetailTitle')" class="w-96">
      <div class="flow-root">
        <dl class="-my-3 divide-y divide-gray-100 text-sm">
          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("message.myAlbumDetailDialogAlbumName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.name }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("message.myAlbumDetailDialogAlbumDesc") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.description }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("message.myAlbumDetailDialogAlbumImageCount") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.imageCount }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("message.myAlbumDetailDialogAlbumIsUncategorized") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.isUncategorized }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("message.myAlbumDetailDialogAlbumIsDefault") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.isDefault }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("message.myAlbumDetailDialogAlbumCreateTime") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ dayjs(String(albumDetail?.createTime)).format("YYYY/MM/DD HH:mm:ss") }}
            </dd>
          </div>
        </dl>
      </div>
    </Dialog>

    <div class="rounded-2xl bg-white dark:bg-gray-800">
      <div class="flex justify-between items-center px-6">
        <Paginator
          @page="(it) => (albumPage = it.page + 1)"
          v-model:rows="albumPageSize"
          :totalRecords="albumTotalRecord"
          :rowsPerPageOptions="[20, 30, 40]"
        />
        <div class="text-gray-500 dark:text-gray-400">
          <span class="font-medium text-gray-700 dark:text-gray-100">
            {{ (albumCurPage - 1) * albumPageSize + 1 }} -
            {{ Math.min(albumCurPage * albumPageSize, albumTotalRecord) }}
          </span>
          of {{ albumTotalRecord }} records
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
