<script setup lang="ts">
import { ref } from "vue";
import { Icon } from "@iconify/vue";

const emits = defineEmits(["handleSelect", "handleUnselect"]);
const { imageId, imageSrc, isPrivate, authorAvatarUrl, username, imageName, uploadTime } = defineProps([
  "imageId",
  "imageSrc",
  "isPrivate",
  "authorAvatarUrl",
  "username",
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
    class="relative h-64 bg-cover bg-center group cursor-pointer rounded-2xl hover:ring-2 ring-blue-500"
    :class="isSelected ? 'ring-2' : ''"
    @click="handleSelect"
  >
    <img :src="imageSrc" :alt="imageName" :key="imageSrc" class="object-contain rounded-2xl h-full" />

    <div
      class="absolute inset-x-0 bottom-0 h-1/5 bg-gradient-to-t from-black via-black to-transparent opacity-30 rounded-b-2xl"
    ></div>

    <div class="absolute left-2 bottom-2 flex items-center justify-start w-full gap-2">
      <img :src="authorAvatarUrl" alt="Author Avatar" class="size-8 rounded-full" />

      <div class="text-left text-gray-100">
        <p class="text-sm">{{ username }}</p>
        <p class="text-xs">{{ uploadTime }}</p>
      </div>
    </div>

    <div
      class="absolute top-2 right-2 w-6 h-6 rounded-full bg-white bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200 hover:border-2 hover:border-blue-500"
      :class="isSelected ? 'border-2 border-blue-500' : ''"
    >
      <Icon v-if="isSelected" icon="mdi:check" fill="currentColor" class="w-4 h-4 text-blue-500" />
    </div>

    <div class="absolute top-2 left-2 flex items-center gap-2 text-gray-300">
      <div>
        <Icon v-if="isPrivate" icon="mdi:visibility-off-outline" class="size-6 text-gray-400" />
        <Icon v-else icon="mdi:visibility-outline" class="size-6 text-gray-400" />
      </div>
      <div>{{ imageName }}</div>
    </div>
  </div>
</template>

<style scoped></style>
