<script setup lang="ts">
import ToggleSwitch from 'primevue/toggleswitch';
import Message from "primevue/message";
import { useField } from "vee-validate";

const { id, name, label, defaultValue } = defineProps({
  id: {
    type: String,
    required: true
  },
  name: {
    type: String,
    required: true
  },
  label: {
    type: String,
    required: true
  },
  defaultValue: {
    type: Boolean,
    default: false,
    required: true
  }
});

const { value, errorMessage } = useField(() => name);
</script>

<template>
  <div class="flex gap-4">
    <label :for="id">{{ label }}</label>
    <ToggleSwitch
      :id
      :name
      :value="value"
      @update:modelValue="value = $event"
      :default-value="defaultValue"
      :class="{ 'p-invalid': errorMessage!! }"
      fluid
    />
  </div>
  <Message v-if="errorMessage" severity="error" size="small" variant="simple">
    {{ errorMessage }}
  </Message>
</template>

<style scoped></style>
