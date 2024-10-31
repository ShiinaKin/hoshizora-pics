<script setup lang="ts">
import { ref, defineProps } from "vue";
import { Icon } from "@iconify/vue";

const emits = defineEmits(["handleSelect", "handleUnselect"]);
const { imageId, imageSrc, authorAvatarUrl, imageName, uploadTime } = defineProps([
  "imageId",
  "imageSrc",
  "authorAvatarUrl",
  "imageName",
  "uploadTime"
]);

const isSelected = ref(false);

const handleSelect = () => {
  if (isSelected.value) emits("handleUnselect", imageId);
  else emits("handleSelect", imageId);

  isSelected.value = !isSelected.value;
};
</script>

<template>
  <div
    class="relative h-72 bg-cover bg-center group cursor-pointer rounded-md hover:ring-2 ring-blue-500"
    :class="isSelected ? 'ring-2' : ''"
    @click="handleSelect"
  >
    <img :src="imageSrc" :alt="imageName" :key="imageSrc" class="object-contain rounded-md h-72" />

    <div class="absolute bottom-2 flex items-center justify-evenly w-full gap-2">
      <img :src="authorAvatarUrl" alt="Author Avatar" class="size-8 rounded-full" />

      <div class="text-left text-gray-600">
        <p class="text-sm">{{ imageName }}</p>
        <p class="text-xs">{{ uploadTime }}</p>
      </div>
    </div>

    <div
      class="absolute top-2 right-2 w-6 h-6 rounded-full bg-white bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200 hover:border-2 hover:border-blue-500"
      :class="isSelected ? 'border-2 border-blue-500' : ''"
    >
      <Icon v-if="isSelected" icon="mdi:check" fill="currentColor" class="w-4 h-4 text-blue-500" />
    </div>
  </div>
</template>

<style scoped></style>
