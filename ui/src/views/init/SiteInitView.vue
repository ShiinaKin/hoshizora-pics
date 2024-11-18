<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { CommonApi, type SiteInitRequest } from "api-client";
import { Icon } from "@iconify/vue";
import { useToast } from "primevue/usetoast";

const { t } = useI18n();
const router = useRouter();
const toast = useToast();

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

function handleSubmit() {
  const siteInitRequest = siteInitForm.value;
  commonApi
    .apiSiteInitPost({ siteInitRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        router.push({ name: "login" });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}
</script>

<template>
  <div class="mx-auto max-w-screen-xl px-4 py-16 sm:px-6 lg:px-8">
    <div class="mx-auto max-w-lg text-center">
      <h1 class="text-2xl font-bold sm:text-3xl">
        {{ t("siteInitView.siteInitTitle") }}
      </h1>

      <p class="mt-4 text-gray-500">
        {{ t("siteInitView.siteInitDesc") }}
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
            :placeholder="t('siteInitView.siteInitSiteTitlePlaceholder')"
            pattern="\S+.*"
            v-model="siteInitForm.siteTitle"
            :title="t('siteInitView.siteInitSiteTitleRequirements')"
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
            :placeholder="t('siteInitView.siteInitSiteSubtitlePlaceholder')"
            v-model="siteInitForm.siteSubtitle"
            :title="t('siteInitView.siteInitSiteSubtitleRequirements')"
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
            :placeholder="t('siteInitView.siteInitSiteDescriptionPlaceholder')"
            v-model="siteInitForm.siteDescription"
            :title="t('siteInitView.siteInitSiteDescriptionRequirements')"
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
            :placeholder="t('siteInitView.siteInitSiteExternalUrlPlaceholder')"
            v-model="siteInitForm.siteExternalUrl"
            :title="t('siteInitView.siteInitSiteExternalUrlRequirements')"
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
            autocomplete="username"
            class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
            :placeholder="t('siteInitView.siteInitUsernamePlaceholder')"
            v-model="siteInitForm.username"
            pattern="^[a-zA-Z0-9]{4,20}$"
            :title="t('siteInitView.siteInitUsernameRequirements')"
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
            autocomplete="new-password"
            class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
            :placeholder="t('siteInitView.siteInitPasswordPlaceholder')"
            v-model="siteInitForm.password"
            pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,32}$"
            :title="t('siteInitView.siteInitPasswordRequirements')"
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
            :placeholder="t('siteInitView.siteInitEmailPlaceholder')"
            v-model="siteInitForm.email"
            :title="t('siteInitView.siteInitEmailRequirements')"
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
          {{ t("siteInitView.siteInitSubmitButton") }}
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped></style>
