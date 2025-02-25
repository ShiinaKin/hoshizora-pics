<script setup lang="ts">
import { useCommonStore } from "@/stores/counter";
import I18nSelect from "@/components/I18nSelect.vue";

const commonStore = useCommonStore();
</script>

<template>
  <div class="h-screen w-full flex justify-center items-center bg-gray-300">
    <div class="relative w-full h-full md:w-2/3 lg:w-1/2 md:h-fit md:rounded-xl shadow-lg overflow-hidden">
      <img src="@/assets/authBackground.png" alt="authBackground" class="hidden md:block w-full object-cover" />
      <div
        class="absolute top-0 right-0 w-full xl:w-2/3 2xl:w-1/2 h-full bg-white bg-opacity-75 backdrop-blur-sm flex flex-col items-center justify-center gap-4"
      >
        <h2 class="text-3xl text-gray-700">{{ commonStore.title }}</h2>

        <router-view />

        <div class="absolute right-0 bottom-0 rounded-br-xl locale-changer">
          <I18nSelect />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
