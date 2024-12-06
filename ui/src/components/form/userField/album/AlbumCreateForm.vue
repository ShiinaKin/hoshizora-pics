<script setup lang="ts">
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import * as yup from "yup";
import VeeFloatInputText from "@/components/vee-input/VeeFloatInputText.vue";
import Button from "primevue/button";

const { t } = useI18n();

const albumCreateFormSchema = yup.object({
  name: yup.string().trim().required(t("myAlbumView.create.dialog.form.verify.name.required")),
  description: yup.string().trim(),
});

const { handleSubmit } = useForm({
  validationSchema: albumCreateFormSchema
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
        <VeeFloatInputText id="newAlbumName" name="name" :label="t('myAlbumView.create.dialog.form.name')" />
      </div>
      <div class="flex flex-col gap-1">
        <VeeFloatInputText id="newAlbumDesc" name="description" :label="t('myAlbumView.create.dialog.form.description')" />
      </div>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        :label="t('myAlbumView.create.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button type="submit" :label="t('myAlbumView.create.dialog.form.submitButton')" />
    </div>
  </form>
</template>

<style scoped>

</style>