<script setup lang="ts">
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import * as yup from "yup";
import VeeIftaInputText from "@/components/vee-input/VeeIftaInputText.vue";
import Button from "primevue/button";

const { t } = useI18n();

const patEditFormSchema = yup.object({
  name: yup
    .string()
    .trim()
    .test("at-least-one-field", t("myPatView.edit.dialog.form.verify.atLeastOneField"), function () {
      return !!(this.parent.name || this.parent.description);
    }),
  description: yup
    .string()
    .trim()
    .test("at-least-one-field", t("myPatView.edit.dialog.form.verify.atLeastOneField"), function () {
      return !!(this.parent.name || this.parent.description);
    }),
});

const { handleSubmit } = useForm({
  validationSchema: patEditFormSchema
});

const { pat } = defineProps(["pat"]);

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
        <VeeIftaInputText id="newPATName" name="name" :placeholder="pat.name" :label="t('myPatView.edit.dialog.form.patName')" />
      </div>
      <div class="flex flex-col gap-1">
        <VeeIftaInputText id="newPATDesc" name="description" :placeholder="pat.description" :label="t('myPatView.edit.dialog.form.patDescription')" />
      </div>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        :label="t('myPatView.edit.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button type="submit" :label="t('myPatView.edit.dialog.form.submitButton')" />
    </div>
  </form>
</template>

<style scoped></style>
