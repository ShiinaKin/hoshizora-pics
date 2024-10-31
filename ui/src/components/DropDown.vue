<script setup lang="ts">
import { onMounted, onUnmounted, ref, defineProps } from "vue";

const { dropDownId } = defineProps({
  dropDownId: {
    type: String
  }
});

const isOpen = ref<boolean>(false);

const toggle = () => {
  isOpen.value = !isOpen.value;
};

const closeDropdown = (e: MouseEvent) => {
  const target = e.target as HTMLElement;
  if (!target.closest(`#${dropDownId}`)) {
    isOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", closeDropdown);
});

onUnmounted(() => {
  document.removeEventListener("click", closeDropdown);
});
</script>

<template>
  <div :id="dropDownId" class="relative inline-block text-center">
    <div @click="toggle">
      <slot name="trigger"></slot>
    </div>

    <transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    >
      <div
        v-if="isOpen"
        class="origin-top-right absolute right-0 mt-2 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
      >
        <div class="p-2">
          <slot name="content"></slot>
        </div>
      </div>
    </transition>
  </div>
</template>
