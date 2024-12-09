<script setup lang="ts">
import { computed, type PropType, ref } from "vue";
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import * as yup from "yup";
import { GroupApi, type GroupApiApiGroupPageGetRequest, type GroupPageVO } from "api-client";
import { debounce } from "lodash-es";
import VeeFloatInputText from "@/components/vee-input/VeeFloatInputText.vue";
import VeeToggleSwitch from "@/components/vee-input/VeeToggleSwitch.vue";
import Button from "primevue/button";
import Message from "primevue/message";
import Select from "primevue/select";
import FloatLabel from "primevue/floatlabel";
import type { VirtualScrollerLazyEvent } from "primevue";

const { t } = useI18n();
const toast = useToast();

const userCreateFormSchema = yup.object({
  username: yup
    .string()
    .trim()
    .min(4, t("adminUserManageView.userCreate.dialog.form.verify.username.min"))
    .max(20, t("adminUserManageView.userCreate.dialog.form.verify.username.max"))
    .matches(/^[a-zA-Z0-9_]+$/, t("adminUserManageView.userCreate.dialog.form.verify.username.invalid"))
    .required(t("adminUserManageView.userCreate.dialog.form.verify.username.required")),
  password: yup
    .string()
    .trim()
    .min(8, t("adminUserManageView.userCreate.dialog.form.verify.password.min"))
    .max(32, t("adminUserManageView.userCreate.dialog.form.verify.password.max"))
    .matches(
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,32}$/,
      t("adminUserManageView.userCreate.dialog.form.verify.password.invalid")
    )
    .required(t("adminUserManageView.userCreate.dialog.form.verify.password.required")),
  email: yup
    .string()
    .trim()
    .email(t("adminUserManageView.userCreate.dialog.form.verify.email.invalid"))
    .required(t("adminUserManageView.userCreate.dialog.form.verify.email.required")),
  group: yup.object({
    id: yup.number().required(t("adminUserManageView.userCreate.dialog.form.verify.groupId.required"))
  }),
  isDefaultImagePrivate: yup
    .boolean()
    .default(true)
    .required(t("adminUserManageView.userCreate.dialog.form.verify.isDefaultImagePrivate.required"))
});

const { handleSubmit, defineField, errors } = useForm({
  validationSchema: userCreateFormSchema
});

const [group, groupAttrs] = defineField("group");

const { groupApi } = defineProps({
  groupApi: {
    type: Object as PropType<GroupApi>,
    required: true
  }
});

const emits = defineEmits(["submit", "cancel"]);

const onSubmit = handleSubmit((values, ctx) => {
  emits("submit", values, ctx);
});

const onCancel = () => {
  emits("cancel");
};

const groupPage = ref(1);
const groupPageSize = ref(15);
const groupOrderBy = ref("createTime");
const groupOrder = ref("DESC");
const groupPageRequest = computed<GroupApiApiGroupPageGetRequest>(() => {
  return {
    page: groupPage.value,
    pageSize: groupPageSize.value,
    orderBy: groupOrderBy.value === "" ? undefined : groupOrderBy.value,
    order: groupOrder.value === "" ? undefined : groupOrder.value
  };
});
const groupCurPage = ref(1);
const groupTotalRecord = ref(-1);
const groupPageData = ref<GroupPageVO[]>([]);

const userCreateFormGroupSelectLoading = ref(false);

const debounceHandleUserCreateFormGroupSelectLazyLoading = debounce(
  (event: VirtualScrollerLazyEvent) => handleUserCreateFormGroupSelectLazyLoading(event),
  200
);

function handleUserCreateFormGroupSelectLazyLoading(event: VirtualScrollerLazyEvent) {
  if (groupPageData.value.length === 0) {
    pageGroup(groupPageRequest.value)
      .then((data) => {
        if (data instanceof Array) groupPageData.value = data;
      })
      .then(() => (userCreateFormGroupSelectLoading.value = false));
    return;
  }
  const { last } = event;
  if (groupTotalRecord.value === last) return;
  groupPage.value += 1;
  pageGroup(groupPageRequest.value)
    .then((data) => {
      if (data) groupPageData.value.push(...data);
    })
    .then(() => (userCreateFormGroupSelectLoading.value = false));
}

async function pageGroup(pageRequest: GroupApiApiGroupPageGetRequest): Promise<GroupPageVO[] | void> {
  return groupApi
    .apiGroupPageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        const pageResult = resp.data!;
        groupCurPage.value = pageResult.page;
        groupTotalRecord.value = pageResult.total;
        return pageResult.data;
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminUserManageView.userCreate.groupPageFailedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}
</script>

<template>
  <form @submit="onSubmit" class="flex flex-col gap-4 m-4 w-96">
    <div class="flex flex-col gap-2.5 w-full">
      <div class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormUsername"
          name="username"
          :label="t('adminUserManageView.userCreate.dialog.form.username')"
        />
      </div>
      <div class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormPassword"
          name="password"
          type="password"
          :label="t('adminUserManageView.userCreate.dialog.form.password')"
        />
      </div>
      <div class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormEmail"
          name="email"
          :label="t('adminUserManageView.userCreate.dialog.form.email')"
        />
      </div>
      <div class="flex flex-col gap-1">
        <FloatLabel variant="on">
          <Select
            id="createFormGroup"
            name="group"
            :options="groupPageData"
            optionLabel="name"
            v-model="group"
            v-bind="groupAttrs"
            :virtualScrollerOptions="{
              lazy: true,
              showLoader: true,
              loading: userCreateFormGroupSelectLoading,
              onLazyLoad: debounceHandleUserCreateFormGroupSelectLazyLoading
            }"
            class="w-full md:w-56"
            fluid
          />
          <label for="createFormGroup">{{ t("adminUserManageView.userCreate.dialog.form.groupId") }}</label>
        </FloatLabel>
        <Message v-if="errors.group" severity="error" size="small" variant="simple">
          {{ errors.group }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <VeeToggleSwitch
          id="createFormDefaultImagePrivate"
          name="isDefaultImagePrivate"
          :label="t('adminUserManageView.userCreate.dialog.form.isDefaultImagePrivate')"
          :default-value="true"
        />
      </div>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        :label="t('adminUserManageView.userCreate.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button type="submit" :label="t('adminUserManageView.userCreate.dialog.form.submitButton')" />
    </div>
  </form>
</template>

<style scoped></style>
