<script setup lang="ts">
import Button from "primevue/button";
import Dialog from "primevue/dialog";

const { header, mainContent, subContent, cancelBtnMsg, submitBtnMsg } = defineProps({
  header: {
    type: String,
    required: true
  },
  mainContent: {
    type: String,
    required: true
  },
  subContent: {
    type: String,
    required: true
  },
  cancelBtnMsg: {
    type: String,
    default: "Cancel"
  },
  submitBtnMsg: {
    type: String,
    default: "Confirm"
  }
});
const emits = defineEmits(["cancel", "confirm"]);
const visible = defineModel<boolean>("visible");
</script>

<template>
  <Dialog v-model:visible="visible" modal :header="header" class="min-w-72">
    <div class="flex flex-col gap-4 mb-4">
      <h2 class="text-lg">{{ mainContent }}</h2>
      <p class="text-sm">{{ subContent }}</p>
    </div>
    <div class="flex justify-end gap-2">
      <Button type="button" :label="cancelBtnMsg" severity="secondary" @click="emits('cancel')"></Button>
      <Button type="button" :label="submitBtnMsg" severity="danger" @click="emits('confirm')"></Button>
    </div>
  </Dialog>
</template>

<style scoped></style>
