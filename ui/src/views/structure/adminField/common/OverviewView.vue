<script setup lang="ts">
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { Configuration, SystemApi, type SystemStatisticsVO, type SystemOverviewVO } from "api-client";
import Divider from "primevue/divider";
import { Icon } from "@iconify/vue";
import { useToast } from "primevue/usetoast";
import { formatUTCStringToLocale } from "@/utils/DateTimeUtils";

const { t } = useI18n();
const token = localStorage.getItem("token");
const toast = useToast();

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const systemApi = new SystemApi(configuration);

const systemStatistics = ref<SystemStatisticsVO>();

const systemOverview = ref<SystemOverviewVO>();

fetchSystemStatistics();
fetchSystemOverview();

function fetchSystemStatistics() {
  systemApi
    .apiSystemStatisticsGet()
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        systemStatistics.value = resp.data!;
      } else {
        toast.add({ severity: "warn", summary: t("adminOverviewView.adminOverviewFetchStatisticsFailedTitle"), detail: resp.message });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message});
    });
}

function fetchSystemOverview() {
  systemApi
    .apiSystemOverviewGet()
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        systemOverview.value = resp.data!;
      } else {
        toast.add({ severity: "warn", summary: t("adminOverviewView.adminOverviewFetchSystemOverviewFailedTitle"), detail: resp.message });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message});
    });
}
</script>

<template>
  <div class="p-8 w-full flex flex-col items-center">
    <div class="flex w-full h-40 gap-6 justify-between">
      <div
        class="relative flex-1 block overflow-hidden rounded-lg border border-gray-100 p-4 sm:p-6 lg:p-8 xl:min-w-60"
      >
        <div class="lg:flex lg:justify-between lg:gap-4">
          <div class="hidden lg:block lg:shrink-0">
            <Icon icon="mdi:image-outline" class="size-16 rounded-lg object-cover" />
          </div>

          <div>
            <h3 class="text-lg font-bold text-gray-900 sm:text-xl">
              {{ t("adminOverviewView.adminOverviewImageCount") }}
            </h3>
            <div class="my-2"></div>
            <p class="text-pretty text-sm text-gray-500">
              {{ systemStatistics?.totalImageCount }} {{ t("adminOverviewView.adminOverviewImageCountUnit") }}
            </p>
          </div>
        </div>
      </div>
      <div
        class="relative flex-1 block overflow-hidden rounded-lg border border-gray-100 p-4 sm:p-6 lg:p-8 xl:min-w-60"
      >
        <div class="lg:flex lg:justify-between lg:gap-4">
          <div class="hidden lg:block lg:shrink-0">
            <Icon icon="mdi:image-multiple-outline" class="size-16 rounded-lg object-cover" />
          </div>

          <div>
            <h3 class="text-lg font-bold text-gray-900 sm:text-xl">
              {{ t("adminOverviewView.adminOverviewAlbumCount") }}
            </h3>
            <div class="my-2"></div>
            <p class="text-pretty text-sm text-gray-500">
              {{ systemStatistics?.totalAlbumCount }} {{ t("adminOverviewView.adminOverviewAlbumCountUnit") }}
            </p>
          </div>
        </div>
      </div>
      <div
        class="relative flex-1 block overflow-hidden rounded-lg border border-gray-100 p-4 sm:p-6 lg:p-8 xl:min-w-60"
      >
        <div class="lg:flex lg:justify-between lg:gap-4">
          <div class="hidden lg:block lg:shrink-0">
            <Icon icon="mdi:zip-box-outline" class="size-16 rounded-lg object-cover" />
          </div>

          <div>
            <h3 class="text-lg font-bold text-gray-900 sm:text-xl">
              {{ t("adminOverviewView.adminOverviewUserCount") }}
            </h3>
            <div class="my-2"></div>
            <p class="text-pretty text-sm text-gray-500">
              {{ systemStatistics?.totalUserCount }} {{ t("adminOverviewView.adminOverviewUserCountUnit") }}
            </p>
          </div>
        </div>
      </div>
      <div
        class="relative flex-1 block overflow-hidden rounded-lg border border-gray-100 p-4 sm:p-6 lg:p-8 xl:min-w-60"
      >
        <div class="lg:flex lg:justify-between lg:gap-4">
          <div class="hidden lg:block lg:shrink-0">
            <Icon icon="mdi:food-takeout-box-outline" class="size-16 rounded-lg object-cover" />
          </div>

          <div>
            <h3 class="text-lg font-bold text-gray-900 sm:text-xl">
              {{ t("adminOverviewView.adminOverviewUsedSpace") }}
            </h3>
            <div class="my-2"></div>
            <p class="text-pretty text-sm text-gray-500">
              {{ systemStatistics?.totalUsedSpace?.toFixed(2) }} {{ t("adminOverviewView.adminOverviewSpaceSizeUnit") }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <Divider />

    <div class="flex w-full gap-8 justify-center">
      <div class="w-full flex flex-col gap-4">
        <div class="flow-root rounded-lg border border-gray-100 py-3 shadow-sm">
          <dl class="-my-3 divide-y divide-gray-100 text-sm">
            <div class="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
              <dt class="font-medium text-gray-900">版本</dt>
              <dd class="text-gray-700 sm:col-span-2">{{ systemOverview?.hoshizoraStatus?.version }}</dd>
            </div>

            <div class="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
              <dt class="font-medium text-gray-900">构建时间</dt>
              <dd class="text-gray-700 sm:col-span-2">
                {{ formatUTCStringToLocale(systemOverview?.hoshizoraStatus?.buildTime) }}
              </dd>
            </div>

            <div class="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
              <dt class="font-medium text-gray-900">CommitID</dt>
              <dd class="text-gray-700 sm:col-span-2">{{ systemOverview?.hoshizoraStatus?.commitId }}</dd>
            </div>
          </dl>
        </div>
        <div class="flow-root rounded-lg border border-gray-100 py-3 shadow-sm">
          <dl class="-my-3 divide-y divide-gray-100 text-sm">
            <div class="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
              <dt class="font-medium text-gray-900">Java版本</dt>
              <dd class="text-gray-700 sm:col-span-2">{{ systemOverview?.systemStatus?.javaVersion }}</dd>
            </div>
            <div class="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
              <dt class="font-medium text-gray-900">数据库版本</dt>
              <dd class="text-gray-700 sm:col-span-2">{{ systemOverview?.systemStatus?.databaseVersion }}</dd>
            </div>
            <div class="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
              <dt class="font-medium text-gray-900">操作系统</dt>
              <dd class="text-gray-700 sm:col-span-2">{{ systemOverview?.systemStatus?.operatingSystem }}</dd>
            </div>
            <div class="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
              <dt class="font-medium text-gray-900">系统时区</dt>
              <dd class="text-gray-700 sm:col-span-2">{{ systemOverview?.systemStatus?.serverTimeZone }}</dd>
            </div>
            <div class="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
              <dt class="font-medium text-gray-900">系统语言</dt>
              <dd class="text-gray-700 sm:col-span-2">{{ systemOverview?.systemStatus?.serverLanguage }}</dd>
            </div>
          </dl>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
