<script setup lang="ts">
import { ref } from "vue";
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import { StrategyTypeEnum } from "api-client";
import * as yup from "yup";
import Button from "primevue/button";
import VeeFloatInputText from "@/components/vee-input/VeeFloatInputText.vue";
import VeeFloatSelect from "@/components/vee-input/VeeFloatSelect.vue";

const { t } = useI18n();

interface StrategyType {
  name: string;
  strategyTypeValue: StrategyTypeEnum;
}

const strategyTypeArr = ref<StrategyType[]>([
  {
    name: t("adminStrategyManageView.create.dialog.form.typeOptions.local"),
    strategyTypeValue: StrategyTypeEnum.Local
  },
  {
    name: t("adminStrategyManageView.create.dialog.form.typeOptions.s3"),
    strategyTypeValue: StrategyTypeEnum.S3
  },
  {
    name: t("adminStrategyManageView.create.dialog.form.typeOptions.webdav"),
    strategyTypeValue: StrategyTypeEnum.Webdav
  }
]);

const strategyCreateFormSchema = yup.object({
  name: yup.string().trim().required(t("adminStrategyManageView.create.dialog.form.verify.name.required")),
  strategyType: yup.object({
    strategyTypeValue: yup
      .mixed<StrategyTypeEnum>()
      .oneOf(Object.values(StrategyTypeEnum), t("adminStrategyManageView.create.dialog.form.verify.type.invalid"))
      .required(t("adminStrategyManageView.create.dialog.form.verify.type.required"))
  }),
  config: yup
    .object()
    .when("strategyType", {
      is: (type: StrategyType) => type.strategyTypeValue === StrategyTypeEnum.Local,
      then: () => {
        return yup.object({
          uploadFolder: yup
            .string()
            .trim()
            .matches(
              /^[^/].*$/,
              t("adminStrategyManageView.create.dialog.form.verify.config.local.uploadFolder.dontStartWithSlash")
            )
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.local.uploadFolder.required")),
          thumbnailFolder: yup
            .string()
            .trim()
            .matches(
              /^[^/].*$/,
              t("adminStrategyManageView.create.dialog.form.verify.config.local.thumbnailFolder.dontStartWithSlash")
            )
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.local.thumbnailFolder.required"))
        });
      }
    })
    .when("strategyType", {
      is: (type: StrategyType) => type.strategyTypeValue === StrategyTypeEnum.S3,
      then: () =>
        yup.object({
          endpoint: yup
            .string()
            .trim()
            .url(t("adminStrategyManageView.create.dialog.form.verify.config.s3.endpoint.invalid"))
            .matches(
              /^(?!.*\/$).*/,
              t("adminStrategyManageView.create.dialog.form.verify.config.s3.endpoint.dontEndWithSlash")
            )
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.s3.endpoint.required")),
          bucketName: yup
            .string()
            .trim()
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.s3.bucketName.required")),
          region: yup
            .string()
            .trim()
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.s3.region.required")),
          accessKey: yup
            .string()
            .trim()
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.s3.accessKey.required")),
          secretKey: yup
            .string()
            .trim()
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.s3.secretKey.required")),
          publicUrl: yup
            .string()
            .trim()
            .url(t("adminStrategyManageView.create.dialog.form.verify.config.s3.publicUrl.invalid"))
            .matches(
              /^(?!.*\/$).*/,
              t("adminStrategyManageView.create.dialog.form.verify.config.s3.publicUrl.dontEndWithSlash")
            )
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.s3.publicUrl.required")),
          uploadFolder: yup
            .string()
            .trim()
            .matches(
              /^[^/].*$/,
              t("adminStrategyManageView.create.dialog.form.verify.config.s3.uploadFolder.dontStartWithSlash")
            )
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.s3.uploadFolder.required")),
          thumbnailFolder: yup
            .string()
            .trim()
            .matches(
              /^[^/].*$/,
              t("adminStrategyManageView.create.dialog.form.verify.config.s3.thumbnailFolder.dontStartWithSlash")
            )
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.s3.thumbnailFolder.required"))
        })
    })
    .when("strategyType", {
      is: (type: StrategyType) => type.strategyTypeValue === StrategyTypeEnum.Webdav,
      then: () =>
        yup.object({
          serverUrl: yup
            .string()
            .trim()
            .url(t("adminStrategyManageView.create.dialog.form.verify.config.webdav.serverUrl.invalid"))
            .matches(
              /^(?!.*\/$).*/,
              t("adminStrategyManageView.create.dialog.form.verify.config.webdav.serverUrl.dontEndWithSlash")
            )
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.webdav.serverUrl.required")),
          username: yup
            .string()
            .trim()
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.webdav.username.required")),
          password: yup
            .string()
            .trim()
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.webdav.password.required")),
          uploadFolder: yup
            .string()
            .trim()
            .matches(
              /^[^/].*$/,
              t("adminStrategyManageView.create.dialog.form.verify.config.webdav.uploadFolder.dontStartWithSlash")
            )
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.webdav.uploadFolder.required")),
          thumbnailFolder: yup
            .string()
            .trim()
            .matches(
              /^[^/].*$/,
              t("adminStrategyManageView.create.dialog.form.verify.config.webdav.thumbnailFolder.dontStartWithSlash")
            )
            .required(t("adminStrategyManageView.create.dialog.form.verify.config.webdav.thumbnailFolder.required"))
        })
    })
});

const {
  handleSubmit,
  values: createFormValues,
  meta
} = useForm({
  validationSchema: strategyCreateFormSchema
});

const emits = defineEmits(["submit", "cancel"]);

const onSubmit = handleSubmit((values, ctx) => {
  emits("submit", values, ctx);
});

const onCancel = () => {
  emits("cancel");
};
</script>

<template>
  <form @submit="onSubmit" class="flex flex-col gap-4 m-4 w-96">
    <div class="flex flex-col gap-2.5 w-full">
      <div class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormName"
          name="name"
          :label="t('adminStrategyManageView.create.dialog.form.name')"
        />
      </div>
      <div class="flex flex-col gap-1">
        <VeeFloatSelect
          id="createFormType"
          name="strategyType"
          :options="strategyTypeArr"
          option-label="name"
          :label="t('adminStrategyManageView.create.dialog.form.type')"
        />
      </div>

      <!-- Local -->
      <div
        v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.Local"
        class="flex flex-col gap-1"
      >
        <VeeFloatInputText
          id="createFormUploadFolder"
          name="config.uploadFolder"
          :label="t('adminStrategyManageView.create.dialog.form.config.local.uploadFolder')"
        />
      </div>
      <div
        v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.Local"
        class="flex flex-col gap-1"
      >
        <VeeFloatInputText
          id="createFormThumbnailFolder"
          name="config.thumbnailFolder"
          :label="t('adminStrategyManageView.create.dialog.form.config.local.thumbnailFolder')"
        />
      </div>

      <!-- S3 -->
      <div v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormEndpoint"
          name="config.endpoint"
          :label="t('adminStrategyManageView.create.dialog.form.config.s3.endpoint')"
        />
      </div>
      <div v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormBucketName"
          name="config.bucketName"
          :label="t('adminStrategyManageView.create.dialog.form.config.s3.bucketName')"
        />
      </div>
      <div v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormRegion"
          name="config.region"
          :label="t('adminStrategyManageView.create.dialog.form.config.s3.region')"
        />
      </div>
      <div v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormAccessKey"
          name="config.accessKey"
          :label="t('adminStrategyManageView.create.dialog.form.config.s3.accessKey')"
          :autocomplete="'off'"
        />
      </div>
      <div v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormSecretKey"
          name="config.secretKey"
          :label="t('adminStrategyManageView.create.dialog.form.config.s3.secretKey')"
          :autocomplete="'off'"
        />
      </div>
      <div v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormPublicUrl"
          name="config.publicUrl"
          :label="t('adminStrategyManageView.create.dialog.form.config.s3.publicUrl')"
        />
      </div>
      <div v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormUploadFolder"
          name="config.uploadFolder"
          :label="t('adminStrategyManageView.create.dialog.form.config.s3.uploadFolder')"
        />
      </div>
      <div v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormThumbnailFolder"
          name="config.thumbnailFolder"
          :label="t('adminStrategyManageView.create.dialog.form.config.s3.thumbnailFolder')"
        />
      </div>

      <!-- WebDav -->
      <div
        v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.Webdav"
        class="flex flex-col gap-1"
      >
        <VeeFloatInputText
          id="createFormServerUrl"
          name="config.serverUrl"
          :label="t('adminStrategyManageView.create.dialog.form.config.webdav.serverUrl')"
        />
      </div>
      <div
        v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.Webdav"
        class="flex flex-col gap-1"
      >
        <VeeFloatInputText
          id="createFormUsername"
          name="config.username"
          :label="t('adminStrategyManageView.create.dialog.form.config.webdav.username')"
        />
      </div>
      <div
        v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.Webdav"
        class="flex flex-col gap-1"
      >
        <VeeFloatInputText
          id="createFormPassword"
          name="config.password"
          :label="t('adminStrategyManageView.create.dialog.form.config.webdav.password')"
          :autocomplete="'off'"
        />
      </div>
      <div
        v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.Webdav"
        class="flex flex-col gap-1"
      >
        <VeeFloatInputText
          id="createFormUploadFolder"
          name="config.uploadFolder"
          :label="t('adminStrategyManageView.create.dialog.form.config.webdav.uploadFolder')"
        />
      </div>
      <div
        v-if="createFormValues.strategyType?.strategyTypeValue === StrategyTypeEnum.Webdav"
        class="flex flex-col gap-1"
      >
        <VeeFloatInputText
          id="createFormThumbnailFolder"
          name="config.thumbnailFolder"
          :label="t('adminStrategyManageView.create.dialog.form.config.webdav.thumbnailFolder')"
        />
      </div>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        :label="t('adminStrategyManageView.create.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button
        type="submit"
        :label="t('adminStrategyManageView.create.dialog.form.submitButton')"
        :disabled="!meta.valid"
      />
    </div>
  </form>
</template>

<style scoped></style>
