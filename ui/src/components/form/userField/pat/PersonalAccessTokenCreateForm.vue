<script setup lang="ts">
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import * as yup from "yup";
import VeeFloatInputText from "@/components/vee-input/VeeFloatInputText.vue";
import Button from "primevue/button";
import Message from "primevue/message";
import Checkbox from "primevue/checkbox";
import AccordionContent from "primevue/accordioncontent";
import AccordionHeader from "primevue/accordionheader";
import Accordion from "primevue/accordion";
import DatePicker from "primevue/datepicker";
import AccordionPanel from "primevue/accordionpanel";
import FloatLabel from "primevue/floatlabel";

const { t } = useI18n();

const patCreateFormSchema = yup.object({
  name: yup.string().trim().required(t("myPatView.create.dialog.form.verify.patName.required")),
  description: yup.string().trim(),
  expireTime: yup.date().required(t("myPatView.create.dialog.form.verify.expireTime.required")),
  permissions: yup.array().of(yup.string())
});

const { handleSubmit, defineField, errors } = useForm({
  validationSchema: patCreateFormSchema
});

const [expireTime, expireTimeAttrs] = defineField("expireTime");

const { roles } = defineProps(["roles"]);

const emits = defineEmits(["submit", "cancel"]);

const onSubmit = handleSubmit((values, ctx) => {
  emits("submit", values, ctx);
});

const onCancel = () => {
  emits("cancel");
};
</script>

<template>
  <form @submit="onSubmit" class="flex flex-col gap-4 m-4 w-96">
    <div class="flex flex-col gap-2.5 w-full">
      <div class="flex flex-col gap-1">
        <VeeFloatInputText id="newPATName" name="name" :label="t('myPatView.create.dialog.form.patName')" />
      </div>
      <div class="flex flex-col gap-1">
        <VeeFloatInputText id="newPATDesc" name="description" :label="t('myPatView.create.dialog.form.description')" />
      </div>
      <div class="flex flex-col gap-1">
        <FloatLabel variant="on">
          <DatePicker
            id="newPATDatepicker"
            name="expireTime"
            v-model="expireTime"
            v-bind="expireTimeAttrs"
            showTime
            hourFormat="24"
            fluid
          />
          <label for="newPATDatepicker">{{ t("myPatView.create.dialog.form.expireTime") }}</label>
        </FloatLabel>
        <Message v-if="errors.expireTime" severity="error" size="small" variant="simple">
          {{ errors.expireTime }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <Accordion>
          <AccordionPanel v-for="(role, idx) in roles" :value="idx" :key="idx">
            <AccordionHeader>{{ role.name + " - " + role.displayName }}</AccordionHeader>
            <AccordionContent>
              <div v-for="(permission, pIdx) of role.permissions" :key="pIdx" class="flex items-center gap-2">
                <Checkbox name="permissions" :inputId="'permission-' + pIdx" :value="permission.name" />
                <label :for="'permission-' + pIdx">{{ permission.name }}</label>
              </div>
            </AccordionContent>
          </AccordionPanel>
        </Accordion>
        <Message v-if="errors.permissions" severity="error" size="small" variant="simple">
          {{ errors.permissions }}
        </Message>
      </div>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        :label="t('myPatView.create.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button type="submit" :label="t('myPatView.create.dialog.form.submitButton')" />
    </div>
  </form>
</template>

<style scoped></style>
