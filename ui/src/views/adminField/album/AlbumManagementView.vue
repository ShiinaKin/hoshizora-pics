<script setup lang="ts">
import { computed, ref, watch, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import {
  AlbumApi,
  type AlbumApiApiAlbumManagePageGetRequest,
  type AlbumManagePageVO,
  type AlbumManageVO,
  Configuration,
  UserApi,
  type UserApiApiUserManagePageGetRequest,
  type UserPageVO
} from "api-client";
import Toolbar from "primevue/toolbar";
import InputText from "primevue/inputtext";
import Button from "primevue/button";
import InputGroupAddon from "primevue/inputgroupaddon";
import InputGroup from "primevue/inputgroup";
import Listbox from "primevue/listbox";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Popover from "primevue/popover";
import Dialog from "primevue/dialog";
import type { VirtualScrollerLazyEvent } from "primevue";
import { Icon } from "@iconify/vue";
import BottomPaginator from "@/components/BottomPaginator.vue";
import LoadingDialog from "@/components/LoadingDialog.vue";
import { debounce } from "lodash-es";
import md5 from "crypto-js/md5";
import { formatUTCStringToLocale } from "@/utils/DateTimeUtils";
import type { AlbumManageDisplay } from "@/types/AlbumType";

const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const albumApi = new AlbumApi(configuration);
const userApi = new UserApi(configuration);

const albumPage = ref(1);
const albumPageSize = ref(15);
const albumOrderBy = ref("createTime");
const albumOrder = ref("DESC");
const userId = ref(-1);
const albumSearchContent = ref("");
const albumPageRequest = computed<AlbumApiApiAlbumManagePageGetRequest>(() => {
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
const albumPageData = ref<AlbumManagePageVO[]>([]);

const albumDisplayData = computed<AlbumManageDisplay[]>(() => {
  return albumPageData.value.map((album) => {
    return {
      id: album.id,
      name: album.name,
      userId: album.userId,
      username: album.username,
      userAvatarUrl: `https://gravatar.com/avatar/${md5(album.userEmail)}`,
      imageCount: album.imageCount,
      createTime: album.createTime
    };
  });
});

const userPage = ref(1);
const userPageSize = ref(15);
const userOrderBy = ref("createTime");
const userOrder = ref("DESC");
const usernameSearchContent = ref("");
const userPageRequest = computed<UserApiApiUserManagePageGetRequest>(() => {
  return {
    page: userPage.value,
    pageSize: userPageSize.value,
    orderBy: userOrderBy.value === "" ? undefined : userOrderBy.value,
    order: userOrder.value === "" ? undefined : userOrder.value,
    username: usernameSearchContent.value.trim() === "" ? undefined : usernameSearchContent.value.trim()
  };
});
const isUserPreviousPageReqTakeUsernameSearch = ref(false);
const userCurPage = ref(1);
const userTotalRecord = ref(-1);
const userPageData = ref<UserPageVO[]>([]);

const debouncePageAlbum = debounce((request: UserApiApiUserManagePageGetRequest) => {
  pageAlbum(request);
}, 200);
const debouncePageUser = debounce(() => {
  userPage.value = 1;
  userPageSize.value = 15;
  userFilterLoading.value = true;
  pageUser(userPageRequest.value)
    .then((data) => {
      if (data instanceof Array) userPageData.value = data;
    })
    .finally(() => (userFilterLoading.value = false));
}, 300);

watchEffect(() => {
  const imagePageReq = albumPageRequest.value;
  debouncePageAlbum(imagePageReq);
});

watch(usernameSearchContent, (newVal, oldVal) => {
  if (newVal.trim() === oldVal.trim() && newVal.trim() === "") return;
  debouncePageUser();
});

const albumDetail = ref<AlbumManageVO>();

const userFilterRef = ref();
const albumFilterRef = ref();
const userFilterLoading = ref(false);

const selectedUser = ref();
const activeAlbumFilter = ref("createTimeDESC");
const activeFilterClass = ref("bg-gray-200  hover:bg-gray-200");

const showLoadingDialog = ref(false);
const showAlbumDetailDialog = ref(false);

watch(selectedUser, (newVal) => {
  if (!newVal) return;
  if (newVal.id === userId.value) return;
  albumPage.value = 1;
  userId.value = newVal.id;
});

function handleOpenUserFilter(event: MouseEvent) {
  if (userPageData.value.length === 0) {
    userFilterLoading.value = true;
    pageUser(userPageRequest.value)
      .then((data) => {
        if (data instanceof Array) userPageData.value = data;
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
      if (data) userPageData.value.push(...data);
    })
    .then(() => (userFilterLoading.value = false));
}

function handleResetUserFilter() {
  userId.value = -1;
  selectedUser.value = undefined;
  userPage.value = 1;
}

function handleShowAlbumDetail(albumId: number) {
  fetchAlbumDetail(albumId).then(() => {
    showAlbumDetailDialog.value = true;
  });
}

async function pageUser(pageRequest: UserApiApiUserManagePageGetRequest): Promise<Array<UserPageVO> | void> {
  return userApi
    .apiUserManagePageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        isUserPreviousPageReqTakeUsernameSearch.value = pageRequest.username !== undefined;
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

function pageAlbum(pageRequest: AlbumApiApiAlbumManagePageGetRequest) {
  albumApi
    .apiAlbumManagePageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        isAlbumPreviousPageReqTakeSearch.value = pageRequest.albumName !== undefined;
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

async function fetchAlbumDetail(albumId: number) {
  return albumApi
    .apiAlbumManageAlbumIdGet({ albumId: albumId })
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
    <Toolbar>
      <template #start></template>
      <template #center>
        <InputGroup class="w-full">
          <InputGroupAddon>
            <Icon icon="mdi:search" class="size-4" />
          </InputGroupAddon>
          <InputText v-model="albumSearchContent" placeholder="Search" class="w-full" />
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
            @click="albumFilterRef.toggle"
          >
            <Icon icon="mdi:filter-multiple-outline" class="w-5 h-5 text-gray-800 dark:text-white" />
          </button>
        </div>
      </template>
    </Toolbar>

    <DataTable
      class="flex-grow overflow-y-auto"
      :value="albumDisplayData"
      dataKey="id"
      scrollable
      removableSort
      tableStyle="min-width: 50rem"
    >
      <Column field="id" :header="t('adminAlbumManageView.albumTable.albumId')"></Column>
      <Column field="name" :header="t('adminAlbumManageView.albumTable.albumName')"></Column>
      <Column field="username" :header="t('adminAlbumManageView.albumTable.albumOwnerName')">
        <template #body="{ data }">
          <div class="flex gap-1 items-center">
            <img :alt="data.username" :src="data.userAvatarUrl" class="rounded-full size-4" />
            <div>{{ data.username }}</div>
          </div>
        </template>
      </Column>
      <Column field="imageCount" :header="t('adminAlbumManageView.albumTable.imageCount')" :sortable="true"></Column>
      <Column field="createTime" :header="t('adminAlbumManageView.albumTable.createTime')" :sortable="true">
        <template #body="{ data }">
          {{ formatUTCStringToLocale(data.createTime) }}
        </template>
      </Column>
      <Column :header="t('adminAlbumManageView.albumTable.opsTitle')" class="min-w-20">
        <template #body="{ data }">
          <div class="flex gap-0.5 text-sm">
            <Button @click="handleShowAlbumDetail(data.id)" severity="secondary" size="small">
              {{ t("adminAlbumManageView.albumTable.opsDetail") }}
            </Button>
          </div>
        </template>
      </Column>
    </DataTable>

    <BottomPaginator
      v-model:page="albumPage"
      v-model:page-size="albumPageSize"
      :row-options="[10, 15, 20, 25, 30, 35, 40]"
      :total-record="albumTotalRecord"
      :cur-page="albumCurPage"
    />

    <LoadingDialog v-model:visible="showLoadingDialog" />
    <!--userFilter-->
    <Popover ref="userFilterRef">
      <Listbox
        v-model="selectedUser"
        :options="userPageData"
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
          <div class="w-full flex items-center justify-center">
            <div>{{ slotProps.option.username }}</div>
          </div>
        </template>
      </Listbox>
    </Popover>
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
            {{ t("adminAlbumManageView.albumFilter.createTimeASC") }}
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
            {{ t("adminAlbumManageView.albumFilter.createTimeDESC") }}
          </span>
        </button>
      </div>
    </Popover>
    <!--album detail-->
    <Dialog
      v-model:visible="showAlbumDetailDialog"
      modal
      :header="t('adminAlbumManageView.albumDetail.title')"
      class="min-w-96"
    >
      <div class="flow-root">
        <dl class="-my-3 divide-y divide-gray-100 text-sm">
          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminAlbumManageView.albumDetail.albumId") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.id }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminAlbumManageView.albumDetail.albumName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.name }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminAlbumManageView.albumDetail.albumDesc") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.description }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminAlbumManageView.albumDetail.albumOwnerId") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.userId }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminAlbumManageView.albumDetail.albumOwnerName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.username }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminAlbumManageView.albumDetail.albumImageCount") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ albumDetail?.imageCount }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminAlbumManageView.albumDetail.albumIsUncategorized") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                albumDetail?.isUncategorized
                  ? t("adminAlbumManageView.albumDetail.albumIsUncategorizedYes")
                  : t("adminAlbumManageView.albumDetail.albumIsUncategorizedNo")
              }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminAlbumManageView.albumDetail.albumIsDefault") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                albumDetail?.isDefault
                  ? t("adminAlbumManageView.albumDetail.albumIsDefaultYes")
                  : t("adminAlbumManageView.albumDetail.albumIsDefaultNo")
              }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("adminAlbumManageView.albumDetail.albumCreateTime") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ formatUTCStringToLocale(albumDetail?.createTime) }}
            </dd>
          </div>
        </dl>
      </div>
    </Dialog>
  </div>
</template>

<style scoped></style>
