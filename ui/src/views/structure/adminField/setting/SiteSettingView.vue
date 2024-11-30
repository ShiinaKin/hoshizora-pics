<script setup lang="ts">
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { Configuration, SettingApi, type SiteSettingPatchRequest } from "api-client";
import { useForm } from "vee-validate";
import * as yup from "yup";
import Toolbar from "primevue/toolbar";
import Button from "primevue/button";
import VeeIftaInputText from "@/components/vee-input/VeeIftaInputText.vue";

const { t } = useI18n();
const token = localStorage.getItem("token");
const toast = useToast();

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const settingApi = new SettingApi(configuration);

const siteSetting = ref();

const siteSettingFormInitValue = {
  siteTitle: undefined,
  siteSubtitle: undefined,
  siteDescription: undefined,
  siteExternalUrl: undefined
};

const siteSettingFormSchema = yup.object({
  siteTitle: yup
    .string()
    .trim()
    .test("at-least-one-field", t("settingView.siteSetting.form.verify.atLeastOneField"), function () {
      return !!(
        this.parent.siteTitle ||
        this.parent.siteSubtitle ||
        this.parent.siteDescription ||
        this.parent.siteExternalUrl
      );
    }),
  siteSubtitle: yup
    .string()
    .trim()
    .test("at-least-one-field", t("settingView.siteSetting.form.verify.atLeastOneField"), function () {
      return !!(
        this.parent.siteTitle ||
        this.parent.siteSubtitle ||
        this.parent.siteDescription ||
        this.parent.siteExternalUrl
      );
    }),
  siteDescription: yup
    .string()
    .trim()
    .test("at-least-one-field", t("settingView.siteSetting.form.verify.atLeastOneField"), function () {
      return !!(
        this.parent.siteTitle ||
        this.parent.siteSubtitle ||
        this.parent.siteDescription ||
        this.parent.siteExternalUrl
      );
    }),
  siteExternalUrl: yup
    .string()
    .trim()
    .url(t("settingView.siteSetting.form.verify.siteExternalUrl.invalid"))
    .matches(/^(?!.*\/$).*/, t("settingView.siteSetting.form.verify.siteExternalUrl.dontEndWithSlash"))
    .test("at-least-one-field", t("settingView.siteSetting.form.verify.atLeastOneField"), function () {
      return !!(
        this.parent.siteTitle ||
        this.parent.siteSubtitle ||
        this.parent.siteDescription ||
        this.parent.siteExternalUrl
      );
    })
});

const { handleSubmit } = useForm({
  validationSchema: siteSettingFormSchema
});

const onSiteSettingFormSubmit = handleSubmit((values) => {
  const patchRequest: SiteSettingPatchRequest = {
    siteTitle: values.siteTitle,
    siteSubtitle: values.siteSubtitle,
    siteDescription: values.siteDescription,
    siteExternalUrl: values.siteExternalUrl
  };
  patchSiteSetting(patchRequest);
});

fetchSiteSetting();

function patchSiteSetting(patchRequest: SiteSettingPatchRequest) {
  settingApi
    .apiSettingSitePatch({ siteSettingPatchRequest: patchRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("settingView.siteSetting.toast.patch.successTitle"),
          detail: resp.message,
          life: 3000
        });
        fetchSiteSetting();
      } else {
        toast.add({
          severity: "warn",
          summary: t("settingView.siteSetting.toast.patch.failedTitle"),
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

function fetchSiteSetting() {
  settingApi
    .apiSettingSettingTypeGet({ settingType: "site" })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        siteSetting.value = resp.data?.config;
      } else {
        toast.add({
          severity: "warn",
          summary: t("settingView.siteSetting.toast.fetch.failedTitle"),
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
  <div class="w-full m-8 flex flex-col items-center gap-4">
    <Toolbar class="w-full">
      <template #start>
        <h1 class="text-2xl font-bold">{{ t("settingView.siteSetting.title") }}</h1>
      </template>
    </Toolbar>
    <div class="w-full bg-white rounded flex-grow flex flex-col justify-center items-center">
      <form
        :initial-values="siteSettingFormInitValue"
        @submit="onSiteSettingFormSubmit"
        class="flex flex-col gap-4 w-1/3"
      >
        <div class="flex flex-col gap-2.5 w-full">
          <div class="flex flex-col gap-1">
            <VeeIftaInputText
              id="siteSettingFormSiteTitle"
              name="siteTitle"
              :label="t('settingView.siteSetting.form.siteTitle')"
              :placeholder="siteSetting?.siteTitle"
            />
          </div>
          <div class="flex flex-col gap-1">
            <VeeIftaInputText
              id="siteSettingFormSiteSubtitle"
              name="siteSubtitle"
              :label="t('settingView.siteSetting.form.siteSubtitle')"
              :placeholder="siteSetting?.siteSubtitle"
            />
          </div>
          <div class="flex flex-col gap-1">
            <VeeIftaInputText
              id="siteSettingFormSiteDescription"
              name="siteDescription"
              :label="t('settingView.siteSetting.form.siteDescription')"
              :placeholder="siteSetting?.siteDescription"
            />
          </div>
          <div class="flex flex-col gap-1">
            <VeeIftaInputText
              id="siteSettingFormSiteExternalUrl"
              name="siteExternalUrl"
              :label="t('settingView.siteSetting.form.siteExternalUrl')"
              :placeholder="siteSetting?.siteExternalUrl"
            />
          </div>
        </div>
        <div class="flex justify-end gap-2">
          <Button type="submit" :label="t('settingView.siteSetting.form.submitButton')" />
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped></style>
