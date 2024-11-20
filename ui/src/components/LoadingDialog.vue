<script setup lang="ts">
import Dialog from "primevue/dialog";

const visible = defineModel<boolean>("visible");
</script>

<template>
  <Dialog
    id="rawImageLoadingDialog"
    v-model:visible="visible"
    modal
    class="bg-white bg-opacity-50 border-none"
  >
    <div class="flex justify-center items-center px-56 py-48">
      <div class="w-12 h-12 border-4 border-blue-200 border-t-blue-500 rounded-full animate-spin"></div>
    </div>
  </Dialog>
</template>

<style scoped>

</style>