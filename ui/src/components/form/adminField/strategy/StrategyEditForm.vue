<script setup lang="ts">
import { type PropType } from "vue";
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import * as yup from "yup";
import { StrategyTypeEnum, type StrategyVO } from "api-client";
import Button from "primevue/button";
import VeeIftaInputText from "@/components/vee-input/VeeIftaInputText.vue";

const { t } = useI18n();

const strategyEditFormLocalSchema = yup.object({
  name: yup
    .string()
    .trim()
    .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function () {
      return !!(this.parent.name || this.parent.config?.uploadFolder || this.parent.config?.thumbnailFolder);
    }),
  config: yup.object({
    uploadFolder: yup
      .string()
      .trim()
      .matches(
        /^[^/].*$/,
        t("adminStrategyManageView.edit.dialog.form.verify.config.local.uploadFolder.dontStartWithSlash")
      )
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(formValues.name || formValues.config?.uploadFolder || formValues.config?.thumbnailFolder);
      }),
    thumbnailFolder: yup
      .string()
      .trim()
      .matches(
        /^[^/].*$/,
        t("adminStrategyManageView.edit.dialog.form.verify.config.local.uploadFolder.dontStartWithSlash")
      )
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(formValues.name || formValues.config?.uploadFolder || formValues.config?.thumbnailFolder);
      })
  })
});

const strategyEditFormS3Schema = yup.object({
  name: yup
    .string()
    .trim()
    .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function () {
      return !!(
        this.parent.name ||
        this.parent.config?.endpoint ||
        this.parent.config?.bucketName ||
        this.parent.config?.region ||
        this.parent.config?.accessKey ||
        this.parent.config?.secretKey ||
        this.parent.config?.publicUrl ||
        this.parent.config?.uploadFolder ||
        this.parent.config?.thumbnailFolder
      );
    }),
  config: yup.object({
    endpoint: yup
      .string()
      .trim()
      .url(t("adminStrategyManageView.edit.dialog.form.verify.config.s3.endpoint.invalid"))
      .matches(/^(?!.*\/$).*/, t("adminStrategyManageView.edit.dialog.form.verify.config.s3.endpoint.dontEndWithSlash"))
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(
          formValues.name ||
          formValues.config?.endpoint ||
          formValues.config?.bucketName ||
          formValues.config?.region ||
          formValues.config?.accessKey ||
          formValues.config?.secretKey ||
          formValues.config?.publicUrl ||
          formValues.config?.uploadFolder ||
          formValues.config?.thumbnailFolder
        );
      }),
    bucketName: yup
      .string()
      .trim()
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(
          formValues.name ||
          formValues.config?.endpoint ||
          formValues.config?.bucketName ||
          formValues.config?.region ||
          formValues.config?.accessKey ||
          formValues.config?.secretKey ||
          formValues.config?.publicUrl ||
          formValues.config?.uploadFolder ||
          formValues.config?.thumbnailFolder
        );
      }),
    region: yup
      .string()
      .trim()
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(
          formValues.name ||
          formValues.config?.endpoint ||
          formValues.config?.bucketName ||
          formValues.config?.region ||
          formValues.config?.accessKey ||
          formValues.config?.secretKey ||
          formValues.config?.publicUrl ||
          formValues.config?.uploadFolder ||
          formValues.config?.thumbnailFolder
        );
      }),
    accessKey: yup
      .string()
      .trim()
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(
          formValues.name ||
          formValues.config?.endpoint ||
          formValues.config?.bucketName ||
          formValues.config?.region ||
          formValues.config?.accessKey ||
          formValues.config?.secretKey ||
          formValues.config?.publicUrl ||
          formValues.config?.uploadFolder ||
          formValues.config?.thumbnailFolder
        );
      }),
    secretKey: yup
      .string()
      .trim()
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(
          formValues.name ||
          formValues.config?.endpoint ||
          formValues.config?.bucketName ||
          formValues.config?.region ||
          formValues.config?.accessKey ||
          formValues.config?.secretKey ||
          formValues.config?.publicUrl ||
          formValues.config?.uploadFolder ||
          formValues.config?.thumbnailFolder
        );
      }),
    publicUrl: yup
      .string()
      .trim()
      .url(t("adminStrategyManageView.edit.dialog.form.verify.config.s3.publicUrl.invalid"))
      .matches(
        /^(?!.*\/$).*/,
        t("adminStrategyManageView.edit.dialog.form.verify.config.s3.publicUrl.dontEndWithSlash")
      )
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(
          formValues.name ||
          formValues.config?.endpoint ||
          formValues.config?.bucketName ||
          formValues.config?.region ||
          formValues.config?.accessKey ||
          formValues.config?.secretKey ||
          formValues.config?.publicUrl ||
          formValues.config?.uploadFolder ||
          formValues.config?.thumbnailFolder
        );
      }),
    uploadFolder: yup
      .string()
      .trim()
      .matches(
        /^[^/].*$/,
        t("adminStrategyManageView.edit.dialog.form.verify.config.s3.uploadFolder.dontStartWithSlash")
      )
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(
          formValues.name ||
          formValues.config?.endpoint ||
          formValues.config?.bucketName ||
          formValues.config?.region ||
          formValues.config?.accessKey ||
          formValues.config?.secretKey ||
          formValues.config?.publicUrl ||
          formValues.config?.uploadFolder ||
          formValues.config?.thumbnailFolder
        );
      }),
    thumbnailFolder: yup
      .string()
      .trim()
      .matches(
        /^[^/].*$/,
        t("adminStrategyManageView.edit.dialog.form.verify.config.s3.uploadFolder.dontStartWithSlash")
      )
      .test("at-least-one-field", t("adminStrategyManageView.edit.dialog.form.verify.atLeastOneField"), function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[1].value : {};
        return !!(
          formValues.name ||
          formValues.config?.endpoint ||
          formValues.config?.bucketName ||
          formValues.config?.region ||
          formValues.config?.accessKey ||
          formValues.config?.secretKey ||
          formValues.config?.publicUrl ||
          formValues.config?.uploadFolder ||
          formValues.config?.thumbnailFolder
        );
      })
  })
});

const chooseSchema = (strategyType: StrategyTypeEnum) => {
  switch (strategyType) {
    case StrategyTypeEnum.Local:
      return strategyEditFormLocalSchema;
    case StrategyTypeEnum.S3:
    default:
      return strategyEditFormS3Schema;
  }
};

const { strategyDetail } = defineProps({
  strategyDetail: {
    type: Object as PropType<StrategyVO>,
    required: true
  }
});

const emits = defineEmits(["submit", "cancel"]);

const { handleSubmit } = useForm({
  validationSchema: chooseSchema(strategyDetail.type)
});

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
        <VeeIftaInputText
          id="editFormName"
          name="name"
          :label="t('adminStrategyManageView.edit.dialog.form.name')"
          :placeholder="strategyDetail.name"
        />
      </div>
      <div v-if="strategyDetail.type === StrategyTypeEnum.Local" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormUploadFolder"
          name="config.uploadFolder"
          :label="t('adminStrategyManageView.edit.dialog.form.config.local.uploadFolder')"
          :placeholder="strategyDetail.config.uploadFolder"
        />
      </div>
      <div v-if="strategyDetail.type === StrategyTypeEnum.Local" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormThumbnailFolder"
          name="config.thumbnailFolder"
          :label="t('adminStrategyManageView.edit.dialog.form.config.local.thumbnailFolder')"
          :placeholder="strategyDetail.config.uploadFolder"
        />
      </div>

      <div v-if="strategyDetail.type === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormEndpoint"
          name="config.endpoint"
          :label="t('adminStrategyManageView.edit.dialog.form.config.s3.endpoint')"
          :placeholder="strategyDetail.config.endpoint"
        />
      </div>
      <div v-if="strategyDetail.type === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormBucketName"
          name="config.bucketName"
          :label="t('adminStrategyManageView.edit.dialog.form.config.s3.bucketName')"
          :placeholder="strategyDetail.config.bucketName"
        />
      </div>
      <div v-if="strategyDetail.type === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormRegion"
          name="config.region"
          :label="t('adminStrategyManageView.edit.dialog.form.config.s3.region')"
          :placeholder="strategyDetail.config.region"
        />
      </div>
      <div v-if="strategyDetail.type === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormAccessKey"
          name="config.accessKey"
          :label="t('adminStrategyManageView.edit.dialog.form.config.s3.accessKey')"
          :placeholder="strategyDetail.config.accessKey"
        />
      </div>
      <div v-if="strategyDetail.type === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormSecretKey"
          name="config.secretKey"
          :label="t('adminStrategyManageView.edit.dialog.form.config.s3.secretKey')"
          :placeholder="strategyDetail.config.secretKey"
        />
      </div>
      <div v-if="strategyDetail.type === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormPublicUrl"
          name="config.publicUrl"
          :label="t('adminStrategyManageView.edit.dialog.form.config.s3.publicUrl')"
          :placeholder="strategyDetail.config.publicUrl"
        />
      </div>
      <div v-if="strategyDetail.type === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormUploadFolder"
          name="config.uploadFolder"
          :label="t('adminStrategyManageView.edit.dialog.form.config.s3.uploadFolder')"
          :placeholder="strategyDetail.config.uploadFolder"
        />
      </div>
      <div v-if="strategyDetail.type === StrategyTypeEnum.S3" class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormThumbnailFolder"
          name="config.thumbnailFolder"
          :label="t('adminStrategyManageView.edit.dialog.form.config.s3.thumbnailFolder')"
          :placeholder="strategyDetail.config.thumbnailFolder"
        />
      </div>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        :label="t('adminStrategyManageView.edit.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button type="submit" :label="t('adminStrategyManageView.edit.dialog.form.submitButton')" />
    </div>
  </form>
</template>

<style scoped></style>
