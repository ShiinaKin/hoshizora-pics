<script setup lang="ts">
import { computed, type PropType, ref } from "vue";
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import * as yup from "yup";
import { GroupApi, type GroupApiApiGroupPageGetRequest, type GroupPageVO, type UserVO } from "api-client";
import { debounce } from "lodash-es";
import VeeIftaInputText from "@/components/vee-input/VeeIftaInputText.vue";
import VeeToggleSwitch from "@/components/vee-input/VeeToggleSwitch.vue";
import Button from "primevue/button";
import Divider from "primevue/divider";
import Message from "primevue/message";
import Select from "primevue/select";
import IftaLabel from "primevue/iftalabel";
import type { VirtualScrollerLazyEvent } from "primevue";

const { t } = useI18n();
const toast = useToast();

const userEditFormSchema = yup.object({
  password: yup
    .string()
    .trim()
    .min(8, t("adminUserManageView.userEdit.dialog.form.verify.password.min"))
    .max(32, t("adminUserManageView.userEdit.dialog.form.verify.password.max"))
    .matches(
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,32}$/,
      t("adminUserManageView.userEdit.dialog.form.verify.password.invalid")
    )
    .test("at-least-one-field", t("adminUserManageView.userEdit.dialog.form.verify.atLeastOneField"), function () {
      return (
        this.parent.password ||
        (!!this.parent.email && this.parent.email !== userDetail.email) ||
        (!!this.parent.group?.id && this.parent.group?.id !== userDetail.groupId) ||
        (this.parent.isDefaultImagePrivate !== undefined &&
          this.parent.isDefaultImagePrivate !== userDetail.isDefaultImagePrivate)
      );
    }),
  email: yup
    .string()
    .trim()
    .email(t("adminUserManageView.userEdit.dialog.form.verify.email.invalid"))
    .test("at-least-one-field", t("adminUserManageView.userEdit.dialog.form.verify.atLeastOneField"), function () {
      return (
        this.parent.password ||
        (!!this.parent.email && this.parent.email !== userDetail.email) ||
        (!!this.parent.group?.id && this.parent.group?.id !== userDetail.groupId) ||
        (this.parent.isDefaultImagePrivate !== undefined &&
          this.parent.isDefaultImagePrivate !== userDetail.isDefaultImagePrivate)
      );
    }),
  group: yup
    .object({
      id: yup.number()
    })
    .test("at-least-one-field", t("adminUserManageView.userEdit.dialog.form.verify.atLeastOneField"), function () {
      return (
        this.parent.password ||
        (!!this.parent.email && this.parent.email !== userDetail.email) ||
        (!!this.parent.group?.id && this.parent.group?.id !== userDetail.groupId) ||
        (this.parent.isDefaultImagePrivate !== undefined &&
          this.parent.isDefaultImagePrivate !== userDetail.isDefaultImagePrivate)
      );
    }),
  isDefaultImagePrivate: yup
    .boolean()
    .test("at-least-one-field", t("adminUserManageView.userEdit.dialog.form.verify.atLeastOneField"), function () {
      return (
        this.parent.password ||
        (!!this.parent.email && this.parent.email !== userDetail.email) ||
        (!!this.parent.group?.id && this.parent.group?.id !== userDetail.groupId) ||
        (this.parent.isDefaultImagePrivate !== undefined &&
          this.parent.isDefaultImagePrivate !== userDetail.isDefaultImagePrivate)
      );
    })
});

const { handleSubmit, defineField, errors } = useForm({
  validationSchema: userEditFormSchema
});

const [group, groupAttrs] = defineField("group");

const { groupApi, userDetail } = defineProps({
  groupApi: {
    type: Object as PropType<GroupApi>,
    required: true
  },
  userDetail: {
    type: Object as PropType<UserVO>,
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
          summary: t("adminUserManageView.userEdit.groupPageFailedTitle"),
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
      <div class="flex gap-2">
        <span>{{ t("adminUserManageView.userEdit.dialog.form.userId") }}:</span>
        <span>{{ userDetail.id }}</span>
      </div>
      <div class="flex gap-2">
        <span>{{ t("adminUserManageView.userEdit.dialog.form.username") }}:</span>
        <span>{{ userDetail.username }}</span>
      </div>
    </div>
    <Divider />
    <div class="flex flex-col gap-2.5 w-full">
      <div class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormPassword"
          name="password"
          type="password"
          :label="t('adminUserManageView.userEdit.dialog.form.password')"
        />
      </div>
      <div class="flex flex-col gap-1">
        <VeeIftaInputText
          id="editFormEmail"
          name="email"
          :placeholder="userDetail?.email"
          :label="t('adminUserManageView.userEdit.dialog.form.email')"
        />
      </div>
      <div class="flex flex-col gap-1">
        <IftaLabel>
          <Select
            id="createFormGroup"
            name="group"
            :options="groupPageData"
            optionLabel="name"
            v-model="group"
            v-bind="groupAttrs"
            :placeholder="userDetail.groupName"
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
        </IftaLabel>
        <Message v-if="errors.group" severity="error" size="small" variant="simple">
          {{ errors.group }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <VeeToggleSwitch
          id="createFormDefaultImagePrivate"
          name="isDefaultImagePrivate"
          :label="t('adminUserManageView.userEdit.dialog.form.isDefaultImagePrivate')"
          :default-value="userDetail.isDefaultImagePrivate"
        />
      </div>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        :label="t('adminUserManageView.userEdit.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button type="submit" :label="t('adminUserManageView.userEdit.dialog.form.submitButton')" />
    </div>
  </form>
</template>

<style scoped></style>
