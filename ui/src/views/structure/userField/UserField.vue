<script setup lang="ts">
import { computed } from "vue";
import { RouterView, useRoute, useRouter } from "vue-router";
import { useCommonStore, useUserFieldStore } from "@/stores/counter";
import { useI18n } from "vue-i18n";
import { Configuration, UserApi } from "api-client";
import { Icon } from "@iconify/vue";
import { useToast } from "primevue/usetoast";
import md5 from "crypto-js/md5";

const route = useRoute();
const router = useRouter();
const commonStore = useCommonStore();
const userFieldStore = useUserFieldStore();
const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const userApi = new UserApi(configuration);

userApi
  .apiUserSelfGet()
  .then((response) => {
    const resp = response.data;
    if (resp.isSuccessful) {
      const userVO = resp.data!;
      userFieldStore.setUserId(userVO.id!);
      userFieldStore.setUsername(userVO.username!);
      userFieldStore.setEmail(userVO.email!);
      userFieldStore.setGroupName(userVO.groupName!);
    }
  })
  .catch((error) => {
    console.error(error);
    toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
  });

const isAdmin = computed(() => userFieldStore.groupName === "admin");
const isActive = computed(() => (routeName: string) => route.name === routeName);
const activeCSS = "block rounded-lg bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700";
const inactiveCSS =
  "block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700";

function handleLogout() {
  localStorage.removeItem("token");
  toast.add({ severity: "success", summary: "Success", detail: t("userField.userMenuLogoutSuccess"), life: 3000 });
  router.push({ name: "login" });
}
</script>

<template>
  <div class="flex h-screen">
    <div class="flex h-screen w-full min-w-48 max-w-64 flex-col justify-between border-e bg-white">
      <div class="px-4 py-6">
        <span class="grid h-12 w-full overflow-x-hidden place-content-center rounded-lg bg-gray-50 text-lg text-gray-700">
          {{ commonStore.title }}
        </span>

        <ul class="mt-6 space-y-1">
          <li>
            <button @click="router.push({ name: 'overview' })" :class="isActive('overview') ? activeCSS : inactiveCSS">
              <div class="flex items-center gap-1">
                <Icon icon="mdi:home" class="size-5" />
                <span>{{ t("userField.userMenuOverview") }}</span>
              </div>
            </button>
          </li>

          <li>
            <button
              @click="router.push({ name: 'imageUpload' })"
              :class="isActive('imageUpload') ? activeCSS : inactiveCSS"
            >
              <div class="flex items-center gap-1">
                <Icon icon="mdi:upload" class="size-5" />
                <span>{{ t("userField.userMenuUploadImage") }}</span>
              </div>
            </button>
          </li>

          <li>
            <button @click="router.push({ name: 'myImage' })" :class="isActive('myImage') ? activeCSS : inactiveCSS">
              <div class="flex items-center gap-1">
                <Icon icon="mdi:image" class="size-5" />
                <span>{{ t("userField.userMenuMyImage") }}</span>
              </div>
            </button>
          </li>

          <li>
            <button @click="router.push({ name: 'myAlbum' })" :class="isActive('myAlbum') ? activeCSS : inactiveCSS">
              <div class="flex items-center gap-1">
                <Icon icon="mdi:image-album" class="size-5" />
                <span>{{ t("userField.userMenuMyAlbum") }}</span>
              </div>
            </button>
          </li>

          <li>
            <details class="group [&_summary::-webkit-details-marker]:hidden">
              <summary
                class="flex cursor-pointer items-center justify-between rounded-lg px-4 py-2 text-gray-500 hover:bg-gray-100 hover:text-gray-700"
              >
                <span class="flex items-center gap-1 text-sm font-medium">
                  <Icon icon="mdi:account" class="size-5" />
                  <span>{{ t("userField.userMenuAccount") }}</span>
                </span>

                <span class="shrink-0 transition duration-300 group-open:-rotate-180">
                  <Icon icon="mdi:chevron-down" class="size-5" />
                </span>
              </summary>

              <ul class="mt-2 space-y-1 px-4">
                <li>
                  <button
                    @click="router.push({ name: 'profile' })"
                    :class="isActive('profile') ? activeCSS : inactiveCSS"
                  >
                    <div class="flex items-center gap-1">
                      <Icon icon="mdi:account-details" class="size-5" />
                      <span>{{ t("userField.userMenuProfile") }}</span>
                    </div>
                  </button>
                </li>

                <li>
                  <button
                    @click="router.push({ name: 'myPersonalAccessToken' })"
                    :class="isActive('myPersonalAccessToken') ? activeCSS : inactiveCSS"
                  >
                    <div class="flex items-center gap-1">
                      <Icon icon="mdi:account-key" class="size-5" />
                      <span>{{ t("userField.userMenuMyAccessToken") }}</span>
                    </div>
                  </button>
                </li>

                <li v-if="isAdmin">
                  <button @click="router.push({ name: 'adminField' })" :class="inactiveCSS">
                    <div class="flex items-center gap-1">
                      <Icon icon="mdi:controller-outline" class="size-5" />
                      <span>{{ t("userField.userMenuAdminField") }}</span>
                    </div>
                  </button>
                </li>

                <li>
                  <button
                    @click="handleLogout"
                    class="w-full rounded-lg px-4 py-2 text-sm font-medium text-gray-500 [text-align:_inherit] hover:bg-gray-100 hover:text-gray-700"
                  >
                    <div class="flex items-center gap-1">
                      <Icon icon="mdi:logout-variant" class="size-5" />
                      <span>{{ t("userField.userMenuLogout") }}</span>
                    </div>
                  </button>
                </li>
              </ul>
            </details>
          </li>
        </ul>
      </div>

      <div class="sticky inset-x-0 bottom-0 border-t border-gray-100">
        <div class="flex items-center gap-3 bg-white p-4">
          <img
            alt="avatar"
            :src="`https://gravatar.com/avatar/${md5(userFieldStore.email)}`"
            class="size-10 rounded-full object-cover"
          />

          <div>
            <div>
              <div class="flex gap-2">
                <strong class="block font-medium text-sm">{{ userFieldStore.username }}</strong>
                <span>
                  <span
                    class="inline-flex items-center justify-center rounded-full px-2.5 py-0.5"
                    :class="isAdmin ? 'bg-purple-200 text-purple-700' : 'bg-gray-100 text-gray-700'"
                  >
                    <Icon icon="mdi:account-group" class="-ms-1 me-1.5 size-4" />
                    <span class="whitespace-nowrap text-xs">{{ userFieldStore.groupName }}</span>
                  </span>
                </span>
              </div>

              <span class="text-xs">{{ userFieldStore.email }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <RouterView class="overflow-auto" />
  </div>
</template>

<style scoped></style>
