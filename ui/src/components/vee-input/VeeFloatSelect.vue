<script setup lang="ts">
import FloatLabel from "primevue/floatlabel";
import Select from "primevue/select";
import Message from "primevue/message";
import { useField } from "vee-validate";
import type { PropType } from "vue";

type VariantType = "over" | "in" | "on" | undefined;
const { variant, id, name, type, label, options, optionLabel } = defineProps({
  variant: {
    type: String as PropType<VariantType>,
    default: "on"
  },
  id: {
    type: String,
    required: true
  },
  name: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: "text"
  },
  options: {
    type: Array<unknown>,
    required: true
  },
  optionLabel: {
    type: String,
    required: true
  },
  label: {
    type: String,
    required: true
  }
});

const { value, errorMessage } = useField(() => name);
</script>

<template>
  <FloatLabel :variant>
    <Select
      :id
      :name
      :type
      :value="value"
      @update:modelValue="value = $event"
      :options
      :optionLabel
      :class="{ 'p-invalid': errorMessage!! }"
      fluid
    />
    <label :for="id">{{ label }}</label>
  </FloatLabel>
  <Message v-if="errorMessage" severity="error" size="small" variant="simple">
    {{ errorMessage }}
  </Message>
</template>

<style scoped></style>
