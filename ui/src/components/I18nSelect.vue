<script setup lang="ts">
import { inject, ref } from "vue";
import type { I18n } from "vue-i18n";
import { Icon } from "@iconify/vue";
import Popover from "primevue/popover";

const i18n = inject<I18n>("i18n")!;

const selectPopover = ref();

const toggle = (event: MouseEvent) => {
  selectPopover.value.toggle(event);
};

const handleLocaleChange = (locale: string) => {
  i18n.global.locale = locale;
  localStorage.setItem("locale", locale);
  selectPopover.value.hide();
};
</script>

<template>
  <button class="p-2" @click="toggle">
    <Icon icon="mdi:translate-variant" class="size-4" />
  </button>
  <Popover
    ref="selectPopover"
    :pt="{
      root: {
        class: 'rounded-none'
      },
      content: {
        class: 'p-0'
      }
    }"
  >
    <ul>
      <li
        v-for="locale in i18n.global.availableLocales"
        :key="`locale-${locale}`"
        class="px-1.5 text-center"
        :class="i18n.global.locale === locale ? 'bg-blue-400 cursor-default' : 'hover:bg-gray-300 cursor-pointer'"
      >
        <span @click="handleLocaleChange(locale)">{{ locale }}</span>
      </li>
    </ul>
  </Popover>
</template>

<style scoped></style>
