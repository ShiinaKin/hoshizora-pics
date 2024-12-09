<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import {
  Configuration,
  GroupApi,
  UserApi,
  type UserApiApiUserManagePageGetRequest,
  type UserManageInsertRequest,
  type UserManagePatchRequest,
  type UserPageVO,
  type UserVO
} from "api-client";
import Toolbar from "primevue/toolbar";
import InputText from "primevue/inputtext";
import Button from "primevue/button";
import InputGroupAddon from "primevue/inputgroupaddon";
import InputGroup from "primevue/inputgroup";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Popover from "primevue/popover";
import Dialog from "primevue/dialog";
import { Icon } from "@iconify/vue";
import BottomPaginator from "@/components/BottomPaginator.vue";
import LoadingDialog from "@/components/LoadingDialog.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import UserCreateForm from "@/components/form/adminField/user/UserCreateForm.vue";
import UserEditForm from "@/components/form/adminField/user/UserEditForm.vue";
import { debounce } from "lodash-es";
import { formatUTCStringToLocale } from "@/utils/DateTimeUtils";
import md5 from "crypto-js/md5";

const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const userApi = new UserApi(configuration);
const groupApi = new GroupApi(configuration);

const userPage = ref(1);
const userPageSize = ref(15);
const userOrderBy = ref("createTime");
const userOrder = ref("DESC");
const isBanned = ref();
const userSearchContent = ref("");
const userPageRequest = computed<UserApiApiUserManagePageGetRequest>(() => {
  return {
    page: userPage.value,
    pageSize: userPageSize.value,
    orderBy: userOrderBy.value === "" ? undefined : userOrderBy.value,
    order: userOrder.value === "" ? undefined : userOrder.value,
    isBanned: isBanned.value,
    username: userSearchContent.value.trim() === "" ? undefined : userSearchContent.value.trim()
  };
});
const isUserPreviousPageReqTakeUsernameSearch = ref(false);
const userCurPage = ref(1);
const userTotalRecord = ref(-1);
const userPageData = ref<UserPageVO[]>([]);

const debouncePageUser = debounce((request: UserApiApiUserManagePageGetRequest) => {
  pageUser(request);
}, 200);

watchEffect(() => {
  const imagePageReq = userPageRequest.value;
  debouncePageUser(imagePageReq);
});

const curUserId = ref(-1);
const userDetail = ref<UserVO>();

const userFilterRef = ref();
const bannedStatusFilterRef = ref();

const activeImageFilter = ref("createTimeDESC");
const activeBannedStatusFilter = ref("all");
const activeFilterClass = ref("bg-gray-200  hover:bg-gray-200");

const showLoadingDialog = ref(false);
const showUserDetailDialog = ref(false);
const showUserCreateDialog = ref(false);
const showUserEditDialog = ref(false);
const showUserBanConfirmDialog = ref(false);
const showUserUnbanConfirmDialog = ref(false);
const showUserDeleteConfirmDialog = ref(false);

function handleShowUserDetailDialog(userId: number) {
  curUserId.value = userId;
  showLoadingDialog.value = true;
  fetchUserDetail(userId).then(() => {
    showUserDetailDialog.value = true;
    showLoadingDialog.value = false;
  });
}

function handleShowUserEditDialog(userId: number) {
  curUserId.value = userId;
  showLoadingDialog.value = true;
  fetchUserDetail(userId).then(() => {
    showUserEditDialog.value = true;
    showLoadingDialog.value = false;
  });
}

function handleShowBanDialog(userId: number) {
  curUserId.value = userId;
  showUserBanConfirmDialog.value = true;
}

function handleShowUnbanDialog(userId: number) {
  curUserId.value = userId;
  showUserUnbanConfirmDialog.value = true;
}

function handleShowDeleteDialog(userId: number) {
  curUserId.value = userId;
  showUserDeleteConfirmDialog.value = true;
}

const onUserCreateFormSubmit = (values: any) => {
  const insertReq: UserManageInsertRequest = {
    username: values.username,
    password: values.password,
    email: values.email,
    groupId: values.group.id,
    isDefaultImagePrivate: values.isDefaultImagePrivate
  };
  createUser(insertReq).then(() => {
    showUserCreateDialog.value = false;
  });
};

const onUserEditFormSubmit = (values: any) => {
  const patchReq: UserManagePatchRequest = {
    password: values.password,
    email: values.email,
    groupId: values.group?.id,
    isDefaultImagePrivate: values.isDefaultImagePrivate
  };
  patchUser(curUserId.value, patchReq).then(() => {
    showUserEditDialog.value = false;
  });
};

function handleBanOrUnbanConfirm(isBan: boolean) {
  showUserBanConfirmDialog.value = false;
  showUserUnbanConfirmDialog.value = false;
  banOrUnbanUser(curUserId.value, isBan);
}

function handleDeleteConfirm() {
  showUserDeleteConfirmDialog.value = false;
  deleteUser(curUserId.value);
}

async function createUser(insertRequest: UserManageInsertRequest): Promise<void> {
  return userApi
    .apiUserManagePost({ userManageInsertRequest: insertRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminUserManageView.userCreate.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUser(userPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminUserManageView.userCreate.toast.failedTitle"),
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

function deleteUser(userId: number) {
  userApi
    .apiUserManageIdDelete({ id: userId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminUserManageView.userDelete.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        showUserDeleteConfirmDialog.value = false;
        pageUser(userPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminUserManageView.userDelete.toast.failedTitle"),
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

async function patchUser(userId: number, patchRequest: UserManagePatchRequest): Promise<void> {
  return userApi
    .apiUserManageIdPatch({
      id: userId,
      userManagePatchRequest: patchRequest
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminUserManageView.userEdit.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUser(userPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminUserManageView.userEdit.toast.failedTitle"),
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

function banOrUnbanUser(userId: number, isBan: boolean) {
  let promise;
  if (isBan) promise = userApi.apiUserBanIdPatch({ id: userId });
  else promise = userApi.apiUserUnbanIdPatch({ id: userId });
  promise
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("adminUserManageView.userEdit.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUser(userPageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminUserManageView.userEdit.toast.failedTitle"),
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

function pageUser(pageRequest: UserApiApiUserManagePageGetRequest) {
  userApi
    .apiUserManagePageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        isUserPreviousPageReqTakeUsernameSearch.value = pageRequest.username !== undefined;
        const pageResult = resp.data!;
        userCurPage.value = pageResult.page;
        userTotalRecord.value = pageResult.total;
        userPageData.value = pageResult.data;
      } else {
        toast.add({ severity: "warn", summary: "Warn", detail: resp.message, life: 3000 });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchUserDetail(userId: number) {
  return userApi
    .apiUserManageIdGet({ id: userId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        userDetail.value = resp.data!;
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
            @click="showUserCreateDialog = true"
          >
            <Icon icon="mdi:user-add" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
      <template #center>
        <InputGroup class="w-full">
          <InputGroupAddon>
            <Icon icon="mdi:search" class="size-4" />
          </InputGroupAddon>
          <InputText v-model="userSearchContent" placeholder="Search" class="w-full" />
        </InputGroup>
      </template>
      <template #end>
        <div class="flex gap-2">
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="userFilterRef.toggle"
          >
            <Icon icon="mdi:filter-multiple-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
          <button
            class="inline-flex justify-center rounded-md border border-gray-300 shadow-sm p-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="bannedStatusFilterRef.toggle"
          >
            <Icon icon="mdi:user-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
    </Toolbar>

    <DataTable
      class="flex-grow overflow-y-auto"
      :value="userPageData"
      dataKey="id"
      scrollable
      removableSort
      tableStyle="min-width: 50rem"
    >
      <Column field="id" :header="t('adminUserManageView.userTable.userId')"></Column>
      <Column field="username" :header="t('adminUserManageView.userTable.username')">
        <template #body="{ data }">
          <div class="flex gap-1 items-center">
            <img
              :alt="data.username"
              :src="`https://gravatar.com/avatar/${md5(data.email)}`"
              class="rounded-full size-4"
            />
            <div>{{ data.username }}</div>
          </div>
        </template>
      </Column>
      <Column field="groupName" :header="t('adminUserManageView.userTable.userGroup')"></Column>
      <Column field="imageCount" :header="t('adminUserManageView.userTable.imageCount')" :sortable="true"></Column>
      <Column field="albumCount" :header="t('adminUserManageView.userTable.albumCount')" :sortable="true"></Column>
      <Column field="usedSpace" :header="t('adminUserManageView.userTable.usedSpace')" :sortable="true">
        <template #body="{ data }">
          {{ data.totalImageSize.toFixed(2) }} {{ t("adminUserManageView.userTable.spaceSizeUnit") }}
        </template>
      </Column>
      <Column field="createTime" :header="t('adminUserManageView.userTable.createTime')" :sortable="true">
        <template #body="{ data }">
          {{ formatUTCStringToLocale(data.createTime) }}
        </template>
      </Column>
      <Column field="isBanned" :header="t('adminUserManageView.userTable.banStatus')">
        <template #body="{ data }">
          {{
            !data.isBanned
              ? t("adminUserManageView.userTable.banStatusNormal")
              : t("adminUserManageView.userTable.banStatusBanned")
          }}
        </template>
      </Column>
      <Column :header="t('adminUserManageView.userTable.opsTitle')" class="min-w-64">
        <template #body="{ data }">
          <div class="flex gap-0.5 text-sm">
            <Button @click="handleShowUserDetailDialog(data.id)" severity="secondary" size="small">
              {{ t("adminUserManageView.userTable.opsDetail") }}
            </Button>
            <Button
              @click="handleShowUserEditDialog(data.id)"
              severity="info"
              size="small"
              :disabled="data.groupName === 'admin'"
            >
              {{ t("adminUserManageView.userTable.opsEdit") }}
            </Button>
            <Button
              v-if="!data.isBanned"
              @click="handleShowBanDialog(data.id)"
              severity="warn"
              size="small"
              :disabled="data.groupName === 'admin'"
            >
              {{ t("adminUserManageView.userTable.opsBan") }}
            </Button>
            <Button
              v-else
              @click="handleShowUnbanDialog(data.id)"
              severity="success"
              size="small"
              :disabled="data.groupName === 'admin'"
            >
              {{ t("adminUserManageView.userTable.opsUnban") }}
            </Button>
            <Button
              @click="handleShowDeleteDialog(data.id)"
              severity="danger"
              size="small"
              :disabled="data.groupName === 'admin'"
            >
              {{ t("adminUserManageView.userTable.opsDelete") }}
            </Button>
          </div>
        </template>
      </Column>
    </DataTable>

    <BottomPaginator
      v-model:page="userPage"
      v-model:page-size="userPageSize"
      :row-options="[10, 15, 20, 25, 30, 35, 40]"
      :total-record="userTotalRecord"
      :cur-page="userCurPage"
    />

    <LoadingDialog v-model:visible="showLoadingDialog" />
    <!--userFilter-->
    <Popover ref="userFilterRef">
      <div class="w-40 flex flex-col">
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'createTimeASC' ? activeFilterClass : ''"
          @click="
            () => {
              userOrderBy = 'createTime';
              userOrder = 'ASC';
              activeImageFilter = 'createTimeASC';
              userFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-ascending-outline" />
            {{ t("adminUserManageView.userFilter.createTimeASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'createTimeDESC' ? activeFilterClass : ''"
          @click="
            () => {
              userOrderBy = 'createTime';
              userOrder = 'DESC';
              activeImageFilter = 'createTimeDESC';
              userFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-clock-descending-outline" />
            {{ t("adminUserManageView.userFilter.createTimeDESC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'imageCountASC' ? activeFilterClass : ''"
          @click="
            () => {
              userOrderBy = 'imageCount';
              userOrder = 'ASC';
              activeImageFilter = 'imageCountASC';
              userFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-numeric-ascending" />
            {{ t("adminUserManageView.userFilter.imageCountASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'imageCountDESC' ? activeFilterClass : ''"
          @click="
            () => {
              userOrderBy = 'imageCount';
              userOrder = 'DESC';
              activeImageFilter = 'imageCountDESC';
              userFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-numeric-descending" />
            {{ t("adminUserManageView.userFilter.imageCountDESC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'albumCountASC' ? activeFilterClass : ''"
          @click="
            () => {
              userOrderBy = 'albumCount';
              userOrder = 'ASC';
              activeImageFilter = 'albumCountASC';
              userFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-numeric-ascending" />
            {{ t("adminUserManageView.userFilter.albumCountASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'albumCountDESC' ? activeFilterClass : ''"
          @click="
            () => {
              userOrderBy = 'albumCount';
              userOrder = 'DESC';
              activeImageFilter = 'albumCountDESC';
              userFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-numeric-descending" />
            {{ t("adminUserManageView.userFilter.albumCountDESC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'usedSpaceASC' ? activeFilterClass : ''"
          @click="
            () => {
              userOrderBy = 'totalImageSize';
              userOrder = 'ASC';
              activeImageFilter = 'usedSpaceASC';
              userFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-ascending" />
            {{ t("adminUserManageView.userFilter.usedSpaceASC") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeImageFilter === 'usedSpaceDESC' ? activeFilterClass : ''"
          @click="
            () => {
              userOrderBy = 'totalImageSize';
              userOrder = 'DESC';
              activeImageFilter = 'usedSpaceDESC';
              userFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:sort-descending" />
            {{ t("adminUserManageView.userFilter.usedSpaceDESC") }}
          </span>
        </button>
      </div>
    </Popover>
    <!--bannedStatusFilter-->
    <Popover ref="bannedStatusFilterRef">
      <div class="w-24 flex flex-col">
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeBannedStatusFilter === 'normal' ? activeFilterClass : ''"
          @click="
            () => {
              isBanned = false;
              activeBannedStatusFilter = 'normal';
              bannedStatusFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:user-check-outline" />
            {{ t("adminUserManageView.userFilter.bannedStatus.normal") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeBannedStatusFilter === 'banned' ? activeFilterClass : ''"
          @click="
            () => {
              isBanned = true;
              activeBannedStatusFilter = 'banned';
              bannedStatusFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:user-block-outline" />
            {{ t("adminUserManageView.userFilter.bannedStatus.banned") }}
          </span>
        </button>
        <button
          class="block px-4 py-3 text-sm text-gray-600 capitalize transition-colors duration-300 transform dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 dark:hover:text-white rounded-md"
          :class="activeBannedStatusFilter === 'all' ? activeFilterClass : ''"
          @click="
            () => {
              isBanned = undefined;
              activeBannedStatusFilter = 'all';
              bannedStatusFilterRef.hide();
            }
          "
        >
          <span class="flex gap-2 justify-center items-center">
            <Icon icon="mdi:user-online-outline" />
            {{ t("adminUserManageView.userFilter.bannedStatus.all") }}
          </span>
        </button>
      </div>
    </Popover>
    <!--user create-->
    <Dialog
      v-model:visible="showUserCreateDialog"
      modal
      :header="t('adminUserManageView.userCreate.dialog.title')"
      class="min-w-96"
    >
      <UserCreateForm :group-api="groupApi" @cancel="showUserCreateDialog = false" @submit="onUserCreateFormSubmit" />
    </Dialog>
    <!--user edit-->
    <Dialog
      v-model:visible="showUserEditDialog"
      modal
      :header="t('adminUserManageView.userEdit.dialog.title')"
      class="min-w-96"
    >
      <UserEditForm
        :user-detail="userDetail!"
        :group-api="groupApi"
        @cancel="showUserEditDialog = false"
        @submit="onUserEditFormSubmit"
      />
    </Dialog>
    <!--user detail-->
    <Dialog
      v-model:visible="showUserDetailDialog"
      modal
      :header="t('adminUserManageView.userDetail.title')"
      class="min-w-96"
    >
      <div class="flow-root">
        <dl class="-my-3 divide-y divide-gray-100 text-sm">
          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.userId") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ userDetail?.id }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.username") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ userDetail?.username }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.userGroupName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ userDetail?.groupName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.email") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ userDetail?.email }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.isDefaultImagePrivate") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                userDetail?.isDefaultImagePrivate
                  ? t("adminUserManageView.userDetail.isDefaultImagePrivateTrue")
                  : t("adminUserManageView.userDetail.isDefaultImagePrivateFalse")
              }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.imageCount") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userDetail?.imageCount }} {{ t("adminUserManageView.userDetail.imageCountUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.albumCount") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userDetail?.albumCount }} {{ t("adminUserManageView.userDetail.albumCountUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.usedSpace") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userDetail?.totalImageSize.toFixed(2) }} {{ t("adminUserManageView.userDetail.spaceSizeUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.totalSpace") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userDetail?.allSize.toFixed(2) }} {{ t("adminUserManageView.userDetail.spaceSizeUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.createTime") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ formatUTCStringToLocale(userDetail?.createTime) }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminUserManageView.userDetail.banStatus") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                userDetail?.isBanned
                  ? t("adminUserManageView.userDetail.banStatusBanned")
                  : t("adminUserManageView.userDetail.banStatusNormal")
              }}
            </dd>
          </div>
        </dl>
      </div>
    </Dialog>
    <!--ban confirm-->
    <ConfirmDialog
      v-model:visible="showUserBanConfirmDialog"
      :header="t('adminUserManageView.userBan.confirmDialog.header')"
      :main-content="t('adminUserManageView.userBan.confirmDialog.mainContent')"
      :sub-content="t('adminUserManageView.userBan.confirmDialog.subContent')"
      :cancel-btn-msg="t('adminUserManageView.userBan.confirmDialog.cancelButton')"
      :submit-btn-msg="t('adminUserManageView.userBan.confirmDialog.submitButton')"
      @cancel="showUserBanConfirmDialog = false"
      @confirm="handleBanOrUnbanConfirm(true)"
    />
    <!--unban confirm-->
    <ConfirmDialog
      v-model:visible="showUserUnbanConfirmDialog"
      :header="t('adminUserManageView.userUnban.confirmDialog.header')"
      :main-content="t('adminUserManageView.userUnban.confirmDialog.mainContent')"
      :sub-content="t('adminUserManageView.userUnban.confirmDialog.subContent')"
      :cancel-btn-msg="t('adminUserManageView.userUnban.confirmDialog.cancelButton')"
      :submit-btn-msg="t('adminUserManageView.userUnban.confirmDialog.submitButton')"
      @cancel="showUserUnbanConfirmDialog = false"
      @confirm="handleBanOrUnbanConfirm(false)"
    />
    <!--delete confirm-->
    <ConfirmDialog
      v-model:visible="showUserDeleteConfirmDialog"
      :header="t('adminUserManageView.userDelete.confirmDialog.header')"
      :main-content="t('adminUserManageView.userDelete.confirmDialog.mainContent')"
      :sub-content="t('adminUserManageView.userDelete.confirmDialog.subContent')"
      :cancel-btn-msg="t('adminUserManageView.userDelete.confirmDialog.cancelButton')"
      :submit-btn-msg="t('adminUserManageView.userDelete.confirmDialog.submitButton')"
      @cancel="showUserDeleteConfirmDialog = false"
      @confirm="handleDeleteConfirm"
    />
  </div>
</template>

<style scoped></style>
