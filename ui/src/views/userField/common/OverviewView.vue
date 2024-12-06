<script setup lang="ts">
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { Configuration, UserApi } from "api-client";
import { Icon } from "@iconify/vue";

const { t } = useI18n();
const token = localStorage.getItem("token");
const toast = useToast();

const userApi = new UserApi(
  new Configuration({
    baseOptions: {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  })
);

const imageCount = ref(-1);
const albumCount = ref(-1);
const usedSpace = ref(-1.0);
const usableSpace = ref(-1.0);

userApi
  .apiUserSelfGet()
  .then((response) => {
    const resp = response.data;
    if (resp.isSuccessful) {
      const userVO = resp.data!;
      imageCount.value = userVO.imageCount!;
      albumCount.value = userVO.albumCount!;
      usedSpace.value = userVO.totalImageSize!;
      usableSpace.value = userVO.allSize! - userVO.totalImageSize;
    } else {
      toast.add({
        severity: "warn",
        summary: t("userOverviewView.toast.fetchStatisticsFailedTitle"),
        detail: resp.message,
        life: 3000
      });
    }
  })
  .catch((error) => {
    console.error(error);
    toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
  });
</script>

<template>
  <div class="p-8 w-full flex flex-col items-center gap-8">
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
              {{ t("userOverviewView.header.imageCount") }}
            </h3>
            <div class="my-2"></div>
            <p class="text-pretty text-sm text-gray-500">
              {{ imageCount }} {{ t("userOverviewView.header.imageCountUnit") }}
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
              {{ t("userOverviewView.header.albumCount") }}
            </h3>
            <div class="my-2"></div>
            <p class="text-pretty text-sm text-gray-500">
              {{ albumCount }} {{ t("userOverviewView.header.albumCountUnit") }}
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
              {{ t("userOverviewView.header.usedSpace") }}
            </h3>
            <div class="my-2"></div>
            <p class="text-pretty text-sm text-gray-500">
              {{ usedSpace.toFixed(2) }} {{ t("userOverviewView.header.spaceSizeUnit") }}
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
              {{ t("userOverviewView.header.usableSpace.title") }}
            </h3>
            <div class="my-2"></div>
            <p class="text-pretty text-sm text-gray-500">
              {{ usableSpace.toFixed(2) }} {{ t("userOverviewView.header.spaceSizeUnit") }}
            </p>
          </div>
        </div>

        <dl class="mt-6 flex gap-4 sm:gap-6 items-center justify-center">
          <div class="flex flex-col">
            <dd class="text-xs text-gray-500">{{ t("userOverviewView.header.usableSpace.tips") }}</dd>
          </div>
        </dl>
      </div>
    </div>

    <span class="relative w-full flex justify-center">
      <span
        class="absolute inset-x-0 top-1/2 h-px -translate-y-1/2 bg-transparent bg-gradient-to-r from-transparent via-gray-500 to-transparent opacity-75"
      ></span>
    </span>

    <div class="flex w-full gap-8 justify-between">
      <div class="w-1/2"></div>
      <div class="w-1/3 overflow-x-auto"></div>
    </div>
  </div>
</template>

<style scoped></style>
