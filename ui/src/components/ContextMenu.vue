<script setup lang="ts">
import { ref } from "vue";
import ContextMenu from "primevue/contextmenu";
import { Icon } from "@iconify/vue";

const { menuItems } = defineProps(["menuItems"]);

const menuRef = ref();

const show = (event: Event) => {
  menuRef.value.show(event);
};

const hide = () => {
  menuRef.value.hide();
};

defineExpose({ show, hide });
</script>

<template>
  <ContextMenu class="text-sm rounded-xl" ref="menuRef" :model="menuItems">
    <template #item="{ item, props }">
      <a class="flex items-center overflow-hidden" v-bind="props.action">
        <Icon :icon="item.icon as string" class="size-4" />
        <span class="ml-2">{{ item.label }}</span>
        <Icon v-if="item.items" icon="mdi:keyboard-arrow-right" class="ml-auto" />
      </a>
    </template>
  </ContextMenu>
</template>

<style scoped></style>
