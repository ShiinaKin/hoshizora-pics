<script setup lang="ts">
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { useForm } from "vee-validate";
import * as yup from "yup";
import { CommonApi, type SiteInitRequest } from "api-client";
import VeeFloatInputText from "@/components/vee-input/VeeFloatInputText.vue";
import Button from "primevue/button";

const { t } = useI18n();
const router = useRouter();
const toast = useToast();

const commonApi = new CommonApi();

const siteInitFormSchema = yup.object({
  siteTitle: yup.string().trim().required(t("siteInitView.form.verify.siteTitle.required")),
  siteSubtitle: yup.string().trim().required(t("siteInitView.form.verify.siteSubtitle.required")),
  siteDescription: yup.string().trim().required(t("siteInitView.form.verify.siteDescription.required")),
  siteExternalUrl: yup
    .string()
    .trim()
    .url(t("siteInitView.form.verify.siteExternalUrl.invalid"))
    .required(t("siteInitView.form.verify.siteExternalUrl.required")),
  username: yup
    .string()
    .trim()
    .min(4, t("siteInitView.form.verify.username.min"))
    .max(20, t("siteInitView.form.verify.username.min"))
    .required(t("siteInitView.form.verify.username.required")),
  password: yup
    .string()
    .trim()
    .min(8, t("siteInitView.form.verify.password.min"))
    .max(32, t("siteInitView.form.verify.password.min"))
    .matches(
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,32}$/,
      t("siteInitView.form.verify.password.invalid")
    )
    .required(t("siteInitView.form.verify.password.required")),
  email: yup
    .string()
    .trim()
    .email(t("siteInitView.form.verify.email.invalid"))
    .required(t("siteInitView.form.verify.email.required"))
});

const { handleSubmit } = useForm({
  validationSchema: siteInitFormSchema
});

const onSubmit = handleSubmit((values) => {
  const initRequest: SiteInitRequest = {
    siteTitle: values.siteTitle,
    siteSubtitle: values.siteSubtitle,
    siteDescription: values.siteDescription,
    siteExternalUrl: values.siteExternalUrl,
    username: values.username,
    password: values.password,
    email: values.email
  };
  initSite(initRequest);
});

function initSite(siteInitRequest: SiteInitRequest) {
  commonApi
    .apiSiteInitPost({ siteInitRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({ severity: "success", summary: t("siteInitView.toast.successTitle"), life: 3000 });
        router.push({ name: "login" });
      } else {
        toast.add({ severity: "warn", summary: t("siteInitView.toast.failedTitle"), detail: resp.message, life: 3000 });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}
</script>

<template>
  <div class="h-screen flex flex-col flex-grow items-center justify-center gap-2">
    <div class="text-center">
      <h1 class="text-2xl font-bold sm:text-3xl">
        {{ t("siteInitView.title") }}
      </h1>

      <p class="mt-4 text-gray-500">
        {{ t("siteInitView.description") }}
      </p>
    </div>

    <form @submit="onSubmit" class="flex flex-col gap-4 m-4 w-96">
      <div class="flex flex-col gap-2.5 w-full">
        <div class="flex flex-col gap-1">
          <VeeFloatInputText
            id="initFormSiteTitle"
            name="siteTitle"
            :label="t('siteInitView.form.siteTitle')"
            end-icon="mdi:card-text-outline"
          />
        </div>
        <div class="flex flex-col gap-1">
          <VeeFloatInputText
            id="initFormSiteSubtitle"
            name="siteSubtitle"
            :label="t('siteInitView.form.siteSubtitle')"
            end-icon="mdi:subtitles-outline"
          />
        </div>
        <div class="flex flex-col gap-1">
          <VeeFloatInputText
            id="initFormSiteDescription"
            name="siteDescription"
            :label="t('siteInitView.form.siteDescription')"
            end-icon="mdi:file-document-box-outline"
          />
        </div>
        <div class="flex flex-col gap-1">
          <VeeFloatInputText
            id="initFormSiteExternalUrl"
            name="siteExternalUrl"
            :label="t('siteInitView.form.siteExternalUrl')"
            type="url"
            end-icon="mdi:link"
          />
        </div>
        <div class="flex flex-col gap-1">
          <VeeFloatInputText
            id="initFormUsername"
            name="username"
            :label="t('siteInitView.form.username')"
            end-icon="mdi:user"
          />
        </div>
        <div class="flex flex-col gap-1">
          <VeeFloatInputText
            id="initFormPassword"
            name="password"
            :label="t('siteInitView.form.password')"
            type="password"
            end-icon="mdi:password-outline"
          />
        </div>
        <div class="flex flex-col gap-1">
          <VeeFloatInputText
            id="initFormEmail"
            name="email"
            :label="t('siteInitView.form.email')"
            type="email"
            end-icon="mdi:alternate-email"
          />
        </div>
      </div>
      <Button type="submit" :label="t('siteInitView.form.submitButton')" fluid />
    </form>
  </div>
</template>

<style scoped></style>
