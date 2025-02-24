<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import {
  Configuration,
  StrategyApi,
  type StrategyApiApiStrategyPageGetRequest,
  type StrategyInsertRequest,
  type StrategyPageVO,
  type StrategyPatchRequest,
  type StrategyPatchRequestConfig,
  StrategyTypeEnum,
  type StrategyVO
} from "api-client";
import Toolbar from "primevue/toolbar";
import Button from "primevue/button";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Popover from "primevue/popover";
import Dialog from "primevue/dialog";
import { Icon } from "@iconify/vue";
import BottomPaginator from "@/components/BottomPaginator.vue";
import LoadingDialog from "@/components/LoadingDialog.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import { debounce } from "lodash-es";
import { formatUTCStringToLocale } from "@/utils/DateTimeUtils";
import StrategyCreateForm from "@/components/form/adminField/strategy/StrategyCreateForm.vue";
import StrategyEditForm from "@/components/form/adminField/strategy/StrategyEditForm.vue";

const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const strategyApi = new StrategyApi(configuration);

const strategyPage = ref(1);
const strategyPageSize = ref(15);
const strategyOrderBy = ref("createTime");
const strategyOrder = ref("DESC");
const strategySearchContent = ref("");
const strategyPageRequest = computed<StrategyApiApiStrategyPageGetRequest>(() => {
  return {
    page: strategyPage.value,
    pageSize: strategyPageSize.value,
    orderBy: strategyOrderBy.value === "" ? undefined : strategyOrderBy.value,
    order: strategyOrder.value === "" ? undefined : strategyOrder.value
    // search: strategySearchContent.value.trim() === "" ? undefined : strategySearchContent.value.trim()
  };
});
const isStrategyPreviousPageReqTakeSearch = ref(false);
const strategyCurPage = ref(1);
const strategyTotalRecord = ref(-1);
const strategyPageData = ref<StrategyPageVO[]>([]);

const debouncePageStrategy = debounce((request: StrategyApiApiStrategyPageGetRequest) => {
  pageStrategy(request);
}, 200);

watchEffect(() => {
  const imagePageReq = strategyPageRequest.value;
  debouncePageStrategy(imagePageReq);
});

const curUserId = ref(-1);
const strategyDetail = ref<StrategyVO>();

const strategyFilterRef = ref();

const activeImageFilter = ref("createTimeDESC");
const activeFilterClass = ref("bg-gray-200  hover:bg-gray-200");

const showLoadingDialog = ref(false);
const showStrategyDetailDialog = ref(false);
const showStrategyCreateDialog = ref(false);
const showStrategyEditDialog = ref(false);
const showStrategyDeleteConfirmDialog = ref(false);

function handleShowStrategyDetailDialog(userId: number) {
  curUserId.value = userId;
  showLoadingDialog.value = true;
  fetchStrategyDetail(userId).then(() => {
    showStrategyDetailDialog.value = true;
    showLoadingDialog.value = false;
  });
}

function handleShowStrategyEditDialog(userId: number) {
  curUserId.value = userId;
  showLoadingDialog.value = true;
  fetchStrategyDetail(userId).then(() => {
    showStrategyEditDialog.value = true;
    showLoadingDialog.value = false;
  });
}

function handleShowStrategyDeleteDialog(userId: number) {
  curUserId.value = userId;
  showStrategyDeleteConfirmDialog.value = true;
}

const handleStrategyCreateSubmit = (values: any) => {
  console.log(values);
  const insertReq: StrategyInsertRequest = {
    name: values.name,
    config: {
      type: values.strategyType.strategyTypeValue,
      strategyType: values.strategyType.strategyTypeValue,
      ...values.config
    }
  };
  createStrategy(insertReq).then(() => {
    showStrategyCreateDialog.value = false;
  });
};

const handleStrategyEditSubmit = (values: any) => {
  let patchReqConfig: StrategyPatchRequestConfig;
  switch (strategyDetail.value?.type) {
    case StrategyTypeEnum.Local:
      patchReqConfig = {
        type: strategyDetail.value?.type,
        strategyType: strategyDetail.value?.type,
        uploadFolder: values.config.uploadFolder
          ? values.config.uploadFolder
          : strategyDetail.value?.config.uploadFolder,
        thumbnailFolder: values.config.thumbnailFolder
          ? values.config.thumbnailFolder
          : strategyDetail.value?.config.thumbnailFolder
      };
      break;
    case StrategyTypeEnum.S3:
      patchReqConfig = {
        type: strategyDetail.value?.type,
        strategyType: strategyDetail.value?.type,
        endpoint: values.config.endpoint ? values.config.endpoint : strategyDetail.value?.config.endpoint,
        bucketName: values.config.bucketName ? values.config.bucketName : strategyDetail.value?.config.bucketName,
        region: values.config.region ? values.config.region : strategyDetail.value?.config.region,
        accessKey: values.config.accessKey ? values.config.accessKey : strategyDetail.value?.config.accessKey,
        secretKey: values.config.secretKey ? values.config.secretKey : strategyDetail.value?.config.secretKey,
        publicUrl: values.config.publicUrl ? values.config.publicUrl : strategyDetail.value?.config.publicUrl,
        uploadFolder: values.config.uploadFolder
          ? values.config.uploadFolder
          : strategyDetail.value?.config.uploadFolder,
        thumbnailFolder: values.config.thumbnailFolder
          ? values.config.thumbnailFolder
          : strategyDetail.value?.config.thumbnailFolder
      };
      break;
    case StrategyTypeEnum.Webdav:
      patchReqConfig = {
        type: strategyDetail.value?.type,
        strategyType: strategyDetail.value?.type,
        serverUrl: values.config.serverUrl ? values.config.serverUrl : strategyDetail.value?.config.serverUrl,
        username: values.config.username ? values.config.username : strategyDetail.value?.config.username,
        password: values.config.password ? values.config.password : strategyDetail.value?.config.password,
        uploadFolder: values.config.uploadFolder
          ? values.config.uploadFolder
          : strategyDetail.value?.config.uploadFolder,
        thumbnailFolder: values.config.thumbnailFolder
          ? values.config.thumbnailFolder
          : strategyDetail.value?.config.thumbnailFolder
      };
      break;
    default:
      throw new Error("Unknown strategy type");
  }
  const patchReq: StrategyPatchRequest = {
    name: values.name ? values.name : strategyDetail.value?.name,
    config: patchReqConfig
  };
  patchStrategy(curUserId.value, patchReq).then(() => {
    showStrategyEditDialog.value = false;
  });
};

function handleStrategyDeleteConfirm() {
  showStrategyDeleteConfirmDialog.value = false;
  deleteStrategy(curUserId.value);
}

async function createStrategy(insertRequest: StrategyInsertRequest) {
  return strategyApi
    .apiStrategyPost({ strategyInsertRequest: insertRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminStrategyManageView.create.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageStrategy(strategyPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminStrategyManageView.create.toast.failedTitle"),
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

function deleteStrategy(strategyId: number) {
  strategyApi
    .apiStrategyIdDelete({ id: strategyId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminStrategyManageView.delete.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        showStrategyDeleteConfirmDialog.value = false;
        pageStrategy(strategyPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminStrategyManageView.delete.toast.failedTitle"),
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

async function patchStrategy(strategyId: number, patchRequest: StrategyPatchRequest): Promise<void> {
  return strategyApi
    .apiStrategyIdPatch({
      id: strategyId,
      strategyPatchRequest: patchRequest
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminStrategyManageView.edit.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageStrategy(strategyPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminStrategyManageView.edit.toast.failedTitle"),
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

function pageStrategy(pageRequest: StrategyApiApiStrategyPageGetRequest) {
  strategyApi
    .apiStrategyPageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        // isStrategyPreviousPageReqTakeSearch.value = pageRequest.username !== undefined;
        const pageResult = resp.data!;
        strategyCurPage.value = pageResult.page;
        strategyTotalRecord.value = pageResult.total;
        strategyPageData.value = pageResult.data;
      } else {
        toast.add({ severity: "warn", summary: "Warn", detail: resp.message, life: 3000 });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchStrategyDetail(strategyId: number) {
  return strategyApi
    .apiStrategyIdGet({ id: strategyId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        strategyDetail.value = resp.data!;
      } else {
        toast.add({ severity: "warn", summary: "Warn", detail: resp.message, life: 3000 });
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
    <Toolbar>
      <template #start>
        <div class="flex gap-2">
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="showStrategyCreateDialog = true"
          >
            <Icon icon="mdi:add-circle-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
      <template #center>
<!--        <InputGroup class="w-full">-->
<!--          <InputGroupAddon>-->
<!--            <Icon icon="mdi:search" class="size-4" />-->
<!--          </InputGroupAddon>-->
<!--          <InputText v-model="strategySearchContent" placeholder="Search" class="w-full" />-->
<!--        </InputGroup>-->
      </template>
      <template #end>
        <div class="flex gap-2">
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="strategyFilterRef.toggle"
          >
            <Icon icon="mdi:filter-multiple-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
    </Toolbar>

    <DataTable
      class="flex-grow overflow-y-auto"
      :value="strategyPageData"
      dataKey="id"
      scrollable
      removableSort
      tableStyle="min-width: 50rem"
    >
      <Column field="id" :header="t('adminStrategyManageView.table.id')"></Column>
      <Column field="name" :header="t('adminStrategyManageView.table.name')"></Column>
      <Column field="type" :header="t('adminStrategyManageView.table.type')" :sortable="true"></Column>
      <Column field="createTime" :header="t('adminStrategyManageView.table.createTime')" :sortable="true">
        <template #body="{ data }">
          {{ formatUTCStringToLocale(data.createTime) }}
        </template>
      </Column>
      <Column :header="t('adminStrategyManageView.table.ops.title')" class="min-w-64">
        <template #body="{ data }">
          <div class="flex gap-0.5 text-sm">
            <Button @click="handleShowStrategyDetailDialog(data.id)" severity="secondary" size="small">
              {{ t("adminStrategyManageView.table.ops.detail") }}
            </Button>
            <Button @click="handleShowStrategyEditDialog(data.id)" severity="info" size="small">
              {{ t("adminStrategyManageView.table.ops.edit") }}
            </Button>
            <Button
              @click="handleShowStrategyDeleteDialog(data.id)"
              severity="danger"
              size="small"
              :disabled="data.isSystemReserved"
            >
              {{ t("adminStrategyManageView.table.ops.delete") }}
            </Button>
          </div>
        </template>
      </Column>
    </DataTable>

    <BottomPaginator
      v-model:page="strategyPage"
      v-model:page-size="strategyPageSize"
      :row-options="[10, 15, 20, 25, 30, 35, 40]"
      :total-record="strategyTotalRecord"
      :cur-page="strategyCurPage"
    />

    <LoadingDialog v-model:visible="showLoadingDialog" />
    <!--strategy filter-->
    <Popover ref="strategyFilterRef">
      <div class="w-40 flex flex-col">
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'createTimeASC' ? activeFilterClass : ''"
          @click="
            () => {
              strategyOrderBy = 'createTime';
              strategyOrder = 'ASC';
              activeImageFilter = 'createTimeASC';
              strategyFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-ascending-outline" />
            {{ t("adminStrategyManageView.strategyFilter.createTimeASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'createTimeDESC' ? activeFilterClass : ''"
          @click="
            () => {
              strategyOrderBy = 'createTime';
              strategyOrder = 'DESC';
              activeImageFilter = 'createTimeDESC';
              strategyFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-descending-outline" />
            {{ t("adminStrategyManageView.strategyFilter.createTimeDESC") }}
          </span>
        </button>
      </div>
    </Popover>
    <!--strategy create-->
    <Dialog
      v-model:visible="showStrategyCreateDialog"
      modal
      :header="t('adminStrategyManageView.create.dialog.title')"
      class="min-w-96"
    >
      <StrategyCreateForm @submit="handleStrategyCreateSubmit" @cancel="showStrategyCreateDialog = false" />
    </Dialog>
    <!--strategy edit-->
    <Dialog
      v-model:visible="showStrategyEditDialog"
      modal
      :header="t('adminStrategyManageView.edit.dialog.title')"
      class="min-w-96"
    >
      <StrategyEditForm
        :strategy-detail="strategyDetail!"
        @submit="handleStrategyEditSubmit"
        @cancel="showStrategyEditDialog = false"
      />
    </Dialog>
    <!--strategy detail-->
    <Dialog
      v-model:visible="showStrategyDetailDialog"
      modal
      :header="t('adminStrategyManageView.detail.title')"
      class="min-w-96"
    >
      <div class="flow-root">
        <dl class="-my-3 divide-y divide-gray-100 text-sm">
          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.id") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.id }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.name") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.name }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.type") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.type }}</dd>
          </div>

          <!-- Local -->
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.Local"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">
              {{ t("adminStrategyManageView.detail.config.local.uploadFolder") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">/{{ strategyDetail?.config.uploadFolder }}</dd>
          </div>
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.Local"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">
              {{ t("adminStrategyManageView.detail.config.local.thumbnailFolder") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">/{{ strategyDetail?.config.thumbnailFolder }}</dd>
          </div>

          <!-- S3 -->
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.S3"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.config.s3.endpoint") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.endpoint }}</dd>
          </div>
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.S3"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.config.s3.bucketName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.bucketName }}</dd>
          </div>
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.S3"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.config.s3.region") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.region }}</dd>
          </div>
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.S3"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.config.s3.publicUrl") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.publicUrl }}</dd>
          </div>
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.S3"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.config.s3.uploadFolder") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.uploadFolder }}</dd>
          </div>
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.S3"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">
              {{ t("adminStrategyManageView.detail.config.s3.thumbnailFolder") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.thumbnailFolder }}</dd>
          </div>

          <!-- WebDav -->
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.Webdav"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.config.webdav.serverUrl") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.serverUrl }}</dd>
          </div>
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.Webdav"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.config.webdav.username") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.username }}</dd>
          </div>
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.Webdav"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.config.webdav.uploadFolder") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.uploadFolder }}</dd>
          </div>
          <div
            v-if="strategyDetail?.type === StrategyTypeEnum.Webdav"
            class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4"
          >
            <dt class="font-medium text-gray-900">
              {{ t("adminStrategyManageView.detail.config.webdav.thumbnailFolder") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">{{ strategyDetail?.config.thumbnailFolder }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminStrategyManageView.detail.createTime") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ formatUTCStringToLocale(strategyDetail?.createTime) }}
            </dd>
          </div>
        </dl>
      </div>
    </Dialog>
    <!--delete confirm-->
    <ConfirmDialog
      v-model:visible="showStrategyDeleteConfirmDialog"
      :header="t('adminStrategyManageView.delete.confirmDialog.header')"
      :main-content="t('adminStrategyManageView.delete.confirmDialog.mainContent')"
      :sub-content="t('adminStrategyManageView.delete.confirmDialog.subContent')"
      :cancel-btn-msg="t('adminStrategyManageView.delete.confirmDialog.cancelButton')"
      :submit-btn-msg="t('adminStrategyManageView.delete.confirmDialog.submitButton')"
      @cancel="showStrategyDeleteConfirmDialog = false"
      @confirm="handleStrategyDeleteConfirm"
    />
  </div>
</template>

<style scoped></style>
