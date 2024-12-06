<script setup lang="ts">
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import * as yup from "yup";
import VeeIftaInputText from "@/components/vee-input/VeeIftaInputText.vue";
import Button from "primevue/button";

const { t } = useI18n();

const albumEditFormSchema = yup.object({
  name: yup
    .string()
    .trim()
    .test("at-least-one-field", t("myAlbumView.edit.dialog.form.verify.atLeastOneField"), function () {
      return !!(this.parent.name || this.parent.description);
    }),
  description: yup
    .string()
    .trim()
    .test("at-least-one-field", t("myAlbumView.edit.dialog.form.verify.atLeastOneField"), function () {
      return !!(this.parent.name || this.parent.description);
    })
});

const { handleSubmit } = useForm({
  validationSchema: albumEditFormSchema
});

const { albumDetail } = defineProps(["albumDetail"]);

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
        <VeeIftaInputText
          id="editAlbumName"
          name="name"
          :placeholder="albumDetail?.name"
          :label="t('myAlbumView.edit.dialog.form.name')"
        />
      </div>
      <div class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editAlbumDesc"
          name="description"
          :placeholder="albumDetail?.description"
          :label="t('myAlbumView.edit.dialog.form.description')"
        />
      </div>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        :label="t('myAlbumView.edit.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button type="submit" :label="t('myAlbumView.edit.dialog.form.submitButton')" />
    </div>
  </form>
</template>

<style scoped></style>
