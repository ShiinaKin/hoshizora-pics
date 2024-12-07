<script setup lang="ts">
import InputGroup from "primevue/inputgroup";
import InputGroupAddon from "primevue/inputgroupaddon";
import InputText from "primevue/inputtext";
import Message from "primevue/message";
import { Icon } from "@iconify/vue";
import { useField } from "vee-validate";

const { id, name, type, label } = defineProps({
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
  startIcon: {
    type: String
  },
  endIcon: {
    type: String
  }
});

const { value, errorMessage } = useField(() => name);
</script>

<template>
  <InputGroup>
    <InputGroupAddon v-if="startIcon">
      <Icon :icon="startIcon" />
    </InputGroupAddon>
    <InputText
      :id
      :name
      :type
      :value="value"
      :placeholder="label"
      @update:modelValue="value = $event"
      :class="{ 'p-invalid': errorMessage!! }"
      fluid
    />
    <label class="hidden" :for="id">{{ label }}</label>
    <InputGroupAddon v-if="endIcon">
      <Icon :icon="endIcon" />
    </InputGroupAddon>
  </InputGroup>
  <Message v-if="errorMessage" severity="error" size="small" variant="simple">
    {{ errorMessage }}
  </Message>
</template>

<style scoped></style>
