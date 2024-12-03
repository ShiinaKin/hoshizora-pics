<script setup lang="ts">
import IftaLabel from "primevue/iftalabel";
import InputText from "primevue/inputtext";
import Message from "primevue/message";
import { useField } from "vee-validate";

const { id, name, type, label, placeholder } = defineProps({
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
  label: {
    type: String,
    required: true
  },
  placeholder: {
    type: [String, Number]
  }
});

const { value, errorMessage } = useField(() => name);
</script>

<template>
  <IftaLabel>
    <InputText
      :id
      :name
      :type
      :value="value"
      @update:modelValue="value = $event"
      :placeholder="placeholder as any"
      :class="{ 'p-invalid': errorMessage!! }"
      fluid
    />
    <label :for="id">{{ label }}</label>
  </IftaLabel>
  <Message v-if="errorMessage" severity="error" size="small" variant="simple">
    {{ errorMessage }}
  </Message>
</template>

<style scoped></style>
