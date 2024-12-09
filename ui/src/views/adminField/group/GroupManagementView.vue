<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import {
  Configuration,
  GroupApi,
  type GroupApiApiGroupPageGetRequest,
  type GroupInsertRequest,
  type GroupPageVO,
  type GroupPutRequest,
  type GroupVO,
  RoleApi,
  type RolePageVO,
  StrategyApi
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
import GroupCreateForm from "@/components/form/adminField/group/GroupCreateForm.vue";
import GroupEditForm from "@/components/form/adminField/group/GroupEditForm.vue";

const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const groupApi = new GroupApi(configuration);
const strategyApi = new StrategyApi(configuration);
const roleApi = new RoleApi(configuration);

const groupPage = ref(1);
const groupPageSize = ref(15);
const groupOrderBy = ref("createTime");
const groupOrder = ref("DESC");
const groupPageRequest = computed<GroupApiApiGroupPageGetRequest>(() => {
  return {
    page: groupPage.value,
    pageSize: groupPageSize.value,
    orderBy: groupOrderBy.value === "" ? undefined : groupOrderBy.value,
    order: groupOrder.value === "" ? undefined : groupOrder.value
  };
});
const groupCurPage = ref(1);
const groupTotalRecord = ref(-1);
const groupPageData = ref<GroupPageVO[]>([]);

const debouncePageGroup = debounce((request: GroupApiApiGroupPageGetRequest) => {
  pageGroup(request);
}, 200);

watchEffect(() => {
  const groupPageReq = groupPageRequest.value;
  debouncePageGroup(groupPageReq);
});

const curGroupId = ref(-1);
const groupDetail = ref<GroupVO>();

const groupFilterRef = ref();

const activeGroupFilter = ref("createTimeDESC");
const activeFilterClass = ref("bg-gray-200  hover:bg-gray-200");

const showLoadingDialog = ref(false);
const showGroupDetailDialog = ref(false);
const showGroupCreateDialog = ref(false);
const showGroupEditDialog = ref(false);
const showGroupDeleteConfirmDialog = ref(false);

function handleShowGroupDetailDialog(groupId: number) {
  showLoadingDialog.value = true;
  fetchGroupDetail(groupId).then(() => {
    showLoadingDialog.value = false;
    showGroupDetailDialog.value = true;
  });
}

function handleShowGroupEditDialog(groupId: number) {
  curGroupId.value = groupId;
  showLoadingDialog.value = true;
  fetchGroupDetail(groupId).then(() => {
    showLoadingDialog.value = false;
    showGroupEditDialog.value = true;
  });
}

function handleShowDeleteDialog(groupId: number) {
  curGroupId.value = groupId;
  showGroupDeleteConfirmDialog.value = true;
}

const handleGroupCreateSubmit = (values: any) => {
  const insertReq: GroupInsertRequest = {
    name: values.name,
    description: values.description,
    strategyId: values.strategy.id,
    roles: values.roles.map((role: RolePageVO) => role.name),
    config: {
      groupStrategyConfig: {
        singleFileMaxSize: values.config.strategyConfig.singleFileMaxSize,
        maxSize: values.config.strategyConfig.maxSize,
        pathNamingRule: values.config.strategyConfig.pathNamingRule,
        fileNamingRule: values.config.strategyConfig.fileNamingRule,
        imageQuality: values.config.strategyConfig.imageQuality,
        imageAutoTransformTarget: values.config.strategyConfig.imageAutoTransformTarget?.value,
        allowedImageTypes: values.config.strategyConfig.allowedImageTypes.map((types: any) => types.value)
      }
    }
  };
  createGroup(insertReq).then(() => {
    showGroupCreateDialog.value = false;
  });
};

const handleGroupEditSubmit = (values: any) => {
  const putRequest: GroupPutRequest = {
    name: values.name,
    description: values.description,
    strategyId: values.strategy.id,
    roles: values.roles.map((role: any) => role.name),
    config: {
      groupStrategyConfig: {
        singleFileMaxSize: values.config.strategyConfig.singleFileMaxSize,
        maxSize: values.config.strategyConfig.maxSize,
        pathNamingRule: values.config.strategyConfig.pathNamingRule,
        fileNamingRule: values.config.strategyConfig.fileNamingRule,
        imageQuality: values.config.strategyConfig.imageQuality,
        imageAutoTransformTarget: values.config.strategyConfig.imageAutoTransformTarget?.value,
        allowedImageTypes: values.config.strategyConfig.allowedImageTypes.map((types: any) => types.value)
      }
    }
  };
  putGroup(putRequest).then(() => {
    showGroupEditDialog.value = false;
  });
};

function handleDeleteConfirm() {
  showGroupDeleteConfirmDialog.value = false;
  deleteGroup(curGroupId.value);
}

async function createGroup(insertRequest: GroupInsertRequest) {
  return groupApi
    .apiGroupPost({ groupInsertRequest: insertRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminGroupManageView.create.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageGroup(groupPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminGroupManageView.create.toast.failedTitle"),
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

function deleteGroup(groupId: number) {
  groupApi
    .apiGroupIdDelete({ id: groupId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminGroupManageView.delete.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageGroup(groupPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminGroupManageView.delete.toast.failedTitle"),
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

async function putGroup(putRequest: GroupPutRequest) {
  return groupApi
    .apiGroupIdPut({ id: curGroupId.value, groupPutRequest: putRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminGroupManageView.edit.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageGroup(groupPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminGroupManageView.edit.toast.failedTitle"),
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

async function fetchGroupDetail(groupId: number) {
  return groupApi
    .apiGroupIdGet({ id: groupId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        groupDetail.value = resp.data!;
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminGroupManageView.detail.toast.successTitle"),
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

function pageGroup(pageRequest: GroupApiApiGroupPageGetRequest) {
  groupApi
    .apiGroupPageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        const pageResult = resp.data!;
        groupCurPage.value = pageResult.page;
        groupTotalRecord.value = pageResult.total;
        groupPageData.value = pageResult.data;
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminGroupManageView.table.toast.successTitle"),
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
    <Toolbar>
      <template #start>
        <div class="flex gap-2">
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="showGroupCreateDialog = true"
          >
            <Icon icon="mdi:account-multiple-plus" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
      <template #center> </template>
      <template #end>
        <div class="flex gap-2">
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="groupFilterRef.toggle"
          >
            <Icon icon="mdi:filter-multiple-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
    </Toolbar>

    <DataTable
      class="flex-grow overflow-y-auto"
      :value="groupPageData"
      dataKey="id"
      scrollable
      removableSort
      tableStyle="min-width: 50rem"
    >
      <Column field="id" :header="t('adminGroupManageView.table.id')"></Column>
      <Column field="name" :header="t('adminGroupManageView.table.name')"></Column>
      <Column field="strategyName" :header="t('adminGroupManageView.table.strategyName')"></Column>
      <Column field="userCount" :header="t('adminGroupManageView.table.userCount')" :sortable="true"></Column>
      <Column field="imageCount" :header="t('adminGroupManageView.table.imageCount')" :sortable="true"></Column>
      <Column field="imageSize" :header="t('adminGroupManageView.table.imageSize')" :sortable="true">
        <template #body="{ data }">
          {{ data.imageSize.toFixed(2) }} {{ t("adminGroupManageView.table.imageSizeUnit") }}
        </template>
      </Column>
      <Column field="isSystemReserved" :header="t('adminGroupManageView.table.isSystemReserved.title')">
        <template #body="{ data }">
          {{
            data.isSystemReserved
              ? t("adminGroupManageView.table.isSystemReserved.true")
              : t("adminGroupManageView.table.isSystemReserved.false")
          }}
        </template>
      </Column>
      <Column field="createTime" :header="t('adminGroupManageView.table.createTime')" :sortable="true">
        <template #body="{ data }">
          {{ formatUTCStringToLocale(data.createTime) }}
        </template>
      </Column>
      <Column :header="t('adminGroupManageView.table.ops.title')" class="min-w-64">
        <template #body="{ data }">
          <div class="flex gap-0.5 text-sm">
            <Button @click="handleShowGroupDetailDialog(data.id)" severity="secondary" size="small">
              {{ t("adminGroupManageView.table.ops.detail") }}
            </Button>
            <Button @click="handleShowGroupEditDialog(data.id)" severity="info" size="small">
              {{ t("adminGroupManageView.table.ops.edit") }}
            </Button>
            <Button
              @click="handleShowDeleteDialog(data.id)"
              severity="danger"
              size="small"
              :disabled="data.isSystemReserved"
            >
              {{ t("adminGroupManageView.table.ops.delete") }}
            </Button>
          </div>
        </template>
      </Column>
    </DataTable>

    <BottomPaginator
      v-model:page="groupPage"
      v-model:page-size="groupPageSize"
      :row-options="[10, 15, 20, 25, 30, 35, 40]"
      :total-record="groupTotalRecord"
      :cur-page="groupCurPage"
    />

    <LoadingDialog v-model:visible="showLoadingDialog" />
    <!--group filter-->
    <Popover ref="groupFilterRef">
      <div class="w-40 flex flex-col">
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeGroupFilter === 'createTimeASC' ? activeFilterClass : ''"
          @click="
            () => {
              groupOrderBy = 'createTime';
              groupOrder = 'ASC';
              activeGroupFilter = 'createTimeASC';
              groupFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-ascending-outline" />
            {{ t("adminGroupManageView.filter.group.createTimeASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeGroupFilter === 'createTimeDESC' ? activeFilterClass : ''"
          @click="
            () => {
              groupOrderBy = 'createTime';
              groupOrder = 'DESC';
              activeGroupFilter = 'createTimeDESC';
              groupFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-descending-outline" />
            {{ t("adminGroupManageView.filter.group.createTimeDESC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeGroupFilter === 'userCountASC' ? activeFilterClass : ''"
          @click="
            () => {
              groupOrderBy = 'userCount';
              groupOrder = 'ASC';
              activeGroupFilter = 'userCountASC';
              groupFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-numeric-ascending" />
            {{ t("adminGroupManageView.filter.group.userCountASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeGroupFilter === 'userCountDESC' ? activeFilterClass : ''"
          @click="
            () => {
              groupOrderBy = 'userCount';
              groupOrder = 'DESC';
              activeGroupFilter = 'userCountDESC';
              groupFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-numeric-descending" />
            {{ t("adminGroupManageView.filter.group.userCountDESC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeGroupFilter === 'imageCountASC' ? activeFilterClass : ''"
          @click="
            () => {
              groupOrderBy = 'imageCount';
              groupOrder = 'ASC';
              activeGroupFilter = 'imageCountASC';
              groupFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-numeric-ascending" />
            {{ t("adminGroupManageView.filter.group.imageCountASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeGroupFilter === 'imageCountDESC' ? activeFilterClass : ''"
          @click="
            () => {
              groupOrderBy = 'imageCount';
              groupOrder = 'DESC';
              activeGroupFilter = 'imageCountDESC';
              groupFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-numeric-descending" />
            {{ t("adminGroupManageView.filter.group.imageCountDESC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeGroupFilter === 'imageSizeASC' ? activeFilterClass : ''"
          @click="
            () => {
              groupOrderBy = 'imageSize';
              groupOrder = 'ASC';
              activeGroupFilter = 'imageSizeASC';
              groupFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-ascending" />
            {{ t("adminGroupManageView.filter.group.imageSizeASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeGroupFilter === 'imageSizeDESC' ? activeFilterClass : ''"
          @click="
            () => {
              groupOrderBy = 'imageSize';
              groupOrder = 'DESC';
              activeGroupFilter = 'imageSizeDESC';
              groupFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-descending" />
            {{ t("adminGroupManageView.filter.group.imageSizeDESC") }}
          </span>
        </button>
      </div>
    </Popover>
    <!--group create-->
    <Dialog
      v-model:visible="showGroupCreateDialog"
      modal
      :header="t('adminGroupManageView.create.dialog.title')"
      class="min-w-96"
    >
      <GroupCreateForm
        :strategy-api="strategyApi"
        :role-api="roleApi"
        @submit="handleGroupCreateSubmit"
        @cancel="showGroupCreateDialog = false"
      />
    </Dialog>
    <!--group edit-->
    <Dialog
      v-model:visible="showGroupEditDialog"
      modal
      :header="t('adminGroupManageView.edit.dialog.title')"
      class="min-w-96"
    >
      <GroupEditForm
        :strategy-api="strategyApi"
        :role-api="roleApi"
        :group-detail="groupDetail!"
        @submit="handleGroupEditSubmit"
        @cancel="showGroupEditDialog = false"
      />
    </Dialog>
    <!--group detail-->
    <Dialog
      v-model:visible="showGroupDetailDialog"
      modal
      :header="t('adminGroupManageView.detail.dialog.title')"
      class="min-w-96"
    >
      <div class="flow-root">
        <dl class="-my-3 divide-y divide-gray-100 text-sm">
          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminGroupManageView.detail.dialog.id") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ groupDetail?.id }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminGroupManageView.detail.dialog.name") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ groupDetail?.name }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminGroupManageView.detail.dialog.description") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ groupDetail?.description }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminGroupManageView.detail.dialog.strategyId") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ groupDetail?.strategyId }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminGroupManageView.detail.dialog.strategyName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ groupDetail?.strategyName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminGroupManageView.detail.dialog.roles") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ groupDetail?.roles.join(", ") }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">
              {{ t("adminGroupManageView.detail.dialog.config.strategy.singleFileMaxSize") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ groupDetail?.groupConfig.groupStrategyConfig.singleFileMaxSize }}
              {{ t("adminGroupManageView.detail.dialog.config.strategy.spaceSizeUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">
              {{ t("adminGroupManageView.detail.dialog.config.strategy.maxSize") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ groupDetail?.groupConfig.groupStrategyConfig.maxSize }}
              {{ t("adminGroupManageView.detail.dialog.config.strategy.spaceSizeUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">
              {{ t("adminGroupManageView.detail.dialog.config.strategy.pathNamingRule") }}
            </dt>
            <dd
              class="text-gray-700 sm:col-span-2"
              v-tooltip="{
                value: t('adminGroupManageView.tips.namingRulePlaceholder'),
                pt: {
                  root: {
                    class: 'min-w-fit'
                  }
                }
              }"
            >
              {{ groupDetail?.groupConfig.groupStrategyConfig.pathNamingRule }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">
              {{ t("adminGroupManageView.detail.dialog.config.strategy.fileNamingRule") }}
            </dt>
            <dd
              class="text-gray-700 sm:col-span-2"
              v-tooltip="{
                value: t('adminGroupManageView.tips.namingRulePlaceholder'),
                pt: {
                  root: {
                    class: 'min-w-fit'
                  }
                }
              }"
            >
              {{ groupDetail?.groupConfig.groupStrategyConfig.fileNamingRule }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">
              {{ t("adminGroupManageView.detail.dialog.config.strategy.imageQuality") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ groupDetail?.groupConfig.groupStrategyConfig.imageQuality }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">
              {{ t("adminGroupManageView.detail.dialog.config.strategy.imageAutoTransformTarget") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                groupDetail?.groupConfig.groupStrategyConfig.imageAutoTransformTarget
                  ? groupDetail?.groupConfig.groupStrategyConfig.imageAutoTransformTarget
                  : t("adminGroupManageView.detail.dialog.config.strategy.imageAutoTransformTargetNull")
              }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">
              {{ t("adminGroupManageView.detail.dialog.config.strategy.allowedImageTypes") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ Array(groupDetail?.groupConfig.groupStrategyConfig.allowedImageTypes).join(", ") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">
              {{ t("adminGroupManageView.detail.dialog.isSystemReserved.title") }}
            </dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                groupDetail?.isSystemReserved
                  ? t("adminGroupManageView.detail.dialog.isSystemReserved.true")
                  : t("adminGroupManageView.detail.dialog.isSystemReserved.false")
              }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminGroupManageView.detail.dialog.createTime") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ formatUTCStringToLocale(groupDetail?.createTime) }}
            </dd>
          </div>
        </dl>
      </div>
    </Dialog>
    <!--delete confirm-->
    <ConfirmDialog
      v-model:visible="showGroupDeleteConfirmDialog"
      :header="t('adminGroupManageView.delete.confirmDialog.header')"
      :main-content="t('adminGroupManageView.delete.confirmDialog.mainContent')"
      :sub-content="t('adminGroupManageView.delete.confirmDialog.subContent')"
      :cancel-btn-msg="t('adminGroupManageView.delete.confirmDialog.cancelButton')"
      :submit-btn-msg="t('adminGroupManageView.delete.confirmDialog.submitButton')"
      @cancel="showGroupDeleteConfirmDialog = false"
      @confirm="handleDeleteConfirm"
    />
  </div>
</template>

<style scoped></style>
