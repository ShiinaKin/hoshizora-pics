<script lang="ts" setup>
import { ref, defineEmits, defineProps } from "vue";
import { useI18n } from "vue-i18n";
import { Icon } from "@iconify/vue";

const { t } = useI18n();

const { allowedImageTypes } = defineProps(["allowedImageTypes"]);
const emit = defineEmits(["filesAdded", "fileUpload"]);

const isDragging = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

const onDragOver = (event: DragEvent) => {
  event.preventDefault();
};

const onDragEnter = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = true;
};

const onDragLeave = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = false;
};

const onDrop = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = false;
  const files = event.dataTransfer?.files;
  if (files) handleFiles(files);
};

const openFileDialog = () => {
  fileInput.value?.click();
};

const onFileSelected = (event: Event) => {
  const files = (event.target as HTMLInputElement).files;
  if (files) handleFiles(files);
};

const handleFiles = (files: FileList) => {
  const fileArray = Array.from(files);
  emit("filesAdded", fileArray);
};
</script>

<template>
  <div
    @drop.prevent="onDrop"
    @dragover.prevent="onDragOver"
    @dragenter.prevent="onDragEnter"
    @dragleave.prevent="onDragLeave"
    @click="openFileDialog"
    class="min-h-64 border-2 border-dashed border-gray-300 rounded-lg p-8 text-center cursor-pointer transition-colors flex items-center justify-center"
    :class="{ 'border-blue-500 bg-blue-50': isDragging }"
  >
    <input type="file" ref="fileInput" @change="onFileSelected" multiple class="hidden" />
    <div class="text-gray-500">
      <Icon icon="mdi:cloud-upload-outline" class="mx-auto size-12 text-gray-400" />
      <p class="mt-2 text-sm">{{ t("message.dragUploaderUsageTip") }}</p>
      <p class="mt-1 text-sm">{{ t("message.dragUploaderAllowedFileTypeTip") }} {{ allowedImageTypes }}</p>
    </div>
  </div>
</template>
