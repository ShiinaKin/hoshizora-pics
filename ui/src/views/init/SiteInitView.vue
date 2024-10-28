<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { Icon } from "@iconify/vue";
import { CommonApi, type SiteInitRequest } from "api-client";

const { t } = useI18n();
const router = useRouter();

const commonApi = new CommonApi();

const siteInitForm = ref<SiteInitRequest>({
  siteTitle: "",
  siteSubtitle: "",
  siteDescription: "",
  siteExternalUrl: "",
  username: "",
  password: "",
  email: ""
});

const showErrorAlert = ref(false);
const errorMessage = ref("");

function handleSubmit() {
  const siteInitRequest = siteInitForm.value;
  commonApi
    .apiSiteInitPost({ siteInitRequest })
    .then((resp) => {
      if (resp.isSuccessful) {
        router.push("/");
      }
    })
    .catch((error) => {
      console.error(error);
      errorMessage.value = error.message || "An error occurred";
      showErrorAlert.value = true;
      setTimeout(() => {
        showErrorAlert.value = false;
      }, 5000);
    });
}
</script>

<template>
  <div class="mx-auto max-w-screen-xl px-4 py-16 sm:px-6 lg:px-8">
    <div class="mx-auto max-w-lg text-center">
      <h1 class="text-2xl font-bold sm:text-3xl">
        {{ t("message.siteInitTitle") }}
      </h1>

      <p class="mt-4 text-gray-500">
        {{ t("message.siteInitDesc") }}
      </p>
    </div>

    <form action="#" @submit.prevent="handleSubmit" class="mx-auto mb-0 mt-8 max-w-md space-y-4">
      <div>
        <label for="siteTitle" class="sr-only">SiteTitle</label>

        <div class="relative">
          <input
            id="siteTitle"
            type="text"
            class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
            :placeholder="t('message.siteInitSiteTitlePlaceholder')"
            pattern="\S+.*"
            v-model="siteInitForm.siteTitle"
            :title="t('message.siteInitSiteTitleRequirements')"
            required
          />

          <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
            <Icon icon="mdi:card-text-outline" class="text-gray-400" />
          </span>
        </div>
      </div>

      <div>
        <label for="siteSubTitle" class="sr-only">SiteSubTitle</label>

        <div class="relative">
          <input
            id="siteSubTitle"
            type="text"
            class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
            :placeholder="t('message.siteInitSiteSubtitlePlaceholder')"
            v-model="siteInitForm.siteSubtitle"
            :title="t('message.siteInitSiteSubtitleRequirements')"
            required
          />

          <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
            <Icon icon="mdi:subtitles-outline" class="text-gray-400" />
          </span>
        </div>
      </div>

      <div>
        <label for="siteDescription" class="sr-only">SiteDescription</label>

        <div class="relative">
          <input
            id="siteDescription"
            type="text"
            class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
            :placeholder="t('message.siteInitSiteDescriptionPlaceholder')"
            v-model="siteInitForm.siteDescription"
            :title="t('message.siteInitSiteDescriptionRequirements')"
            required
          />

          <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
            <Icon icon="mdi:file-document-box-outline" class="text-gray-400" />
          </span>
        </div>
      </div>

      <div>
        <label for="siteExternalUrl" class="sr-only">SiteExternalUrl</label>

        <div class="relative">
          <input
            id="siteExternalUrl"
            type="url"
            class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
            :placeholder="t('message.siteInitSiteExternalUrlPlaceholder')"
            v-model="siteInitForm.siteExternalUrl"
            :title="t('message.siteInitSiteExternalUrlRequirements')"
            required
          />

          <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
            <Icon icon="mdi:link" class="text-gray-400" />
          </span>
        </div>
      </div>

      <div>
        <label for="username" class="sr-only">Username</label>

        <div class="relative">
          <input
            id="username"
            type="text"
            class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
            :placeholder="t('message.siteInitUsernamePlaceholder')"
            v-model="siteInitForm.username"
            pattern="^[a-zA-Z0-9]{4,20}$"
            :title="t('message.siteInitUsernameRequirements')"
            required
          />

          <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
            <Icon icon="mdi:user" class="text-gray-400" />
          </span>
        </div>
      </div>

      <div>
        <label for="password" class="sr-only">Password</label>

        <div class="relative">
          <input
            id="password"
            type="password"
            class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
            :placeholder="t('message.siteInitPasswordPlaceholder')"
            v-model="siteInitForm.password"
            pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,32}$"
            :title="t('message.siteInitPasswordRequirements')"
            required
          />

          <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
            <Icon icon="mdi:password-outline" class="text-gray-400" />
          </span>
        </div>
      </div>

      <div>
        <label for="email" class="sr-only">Email</label>

        <div class="relative">
          <input
            id="email"
            type="email"
            class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
            :placeholder="t('message.siteInitEmailPlaceholder')"
            v-model="siteInitForm.email"
            :title="t('message.siteInitEmailRequirements')"
            required
          />

          <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
            <Icon icon="mdi:alternate-email" class="text-gray-400" />
          </span>
        </div>
      </div>

      <div class="flex items-center justify-center">
        <button
          type="submit"
          class="w-1/2 inline-block rounded-lg bg-blue-500 px-5 py-3 text-sm font-medium text-white"
        >
          {{ t("message.siteInitSubmitButton") }}
        </button>
      </div>
    </form>
  </div>

  <div
    v-if="showErrorAlert"
    role="alert"
    class="fixed top-4 right-4 z-50 rounded border-s-4 border-red-500 bg-red-50 p-4"
  >
    <div class="flex items-center gap-2 text-red-800">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-5">
        <path
          fill-rule="evenodd"
          d="M9.401 3.003c1.155-2 4.043-2 5.197 0l7.355 12.748c1.154 2-.29 4.5-2.599 4.5H4.645c-2.309 0-3.752-2.5-2.598-4.5L9.4 3.003zM12 8.25a.75.75 0 01.75.75v3.75a.75.75 0 01-1.5 0V9a.75.75 0 01.75-.75zm0 8.25a.75.75 0 100-1.5.75.75 0 000 1.5z"
          clip-rule="evenodd"
        />
      </svg>

      <strong class="block font-medium"> Something went wrong </strong>
    </div>

    <p class="mt-2 text-sm text-red-700">
      {{ errorMessage }}
    </p>
  </div>
</template>

<style scoped></style>
