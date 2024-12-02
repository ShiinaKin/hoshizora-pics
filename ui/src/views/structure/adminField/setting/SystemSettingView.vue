<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import {
  Configuration,
  GroupApi,
  type GroupApiApiGroupPageGetRequest,
  type GroupPageVO,
  SettingApi,
  type SystemSettingPatchRequest
} from "api-client";
import { useForm } from "vee-validate";
import * as yup from "yup";
import { debounce } from "lodash-es";
import Toolbar from "primevue/toolbar";
import IftaLabel from "primevue/iftalabel";
import Message from "primevue/message";
import Select from "primevue/select";
import Button from "primevue/button";
import type { VirtualScrollerLazyEvent } from "primevue";
import VeeToggleSwitch from "@/components/vee-input/VeeToggleSwitch.vue";
import LoadingDialog from "@/components/LoadingDialog.vue";

const { t } = useI18n();
const token = localStorage.getItem("token");
const toast = useToast();

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const settingApi = new SettingApi(configuration);
const groupApi = new GroupApi(configuration);

const systemSetting = ref();
const systemSettingDefaultGroupVO = ref();

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

const showLoadingDialog = ref(false);
const groupSelectLoading = ref(false);

const debounceHandleGroupSelectLazyLoading = debounce(
  (event: VirtualScrollerLazyEvent) => handleGroupSelectLazyLoading(event),
  200
);

function handleGroupSelectLazyLoading(event: VirtualScrollerLazyEvent) {
  if (groupPageData.value.length === 0) {
    pageGroup(groupPageRequest.value)
      .then((data) => {
        if (data instanceof Array) groupPageData.value = data;
      })
      .then(() => (groupSelectLoading.value = false));
    return;
  }
  const { last } = event;
  if (groupTotalRecord.value === last) return;
  groupPage.value += 1;
  pageGroup(groupPageRequest.value)
    .then((data) => {
      if (data) groupPageData.value.push(...data);
    })
    .then(() => (groupSelectLoading.value = false));
}

const systemSettingFormInitValue = {
  defaultGroup: undefined,
  allowSignup: undefined,
  allowRandomFetch: undefined
};

const systemSettingFormSchema = yup.object({
  defaultGroup: yup
    .object()
    .test("at-least-one-field", t("settingView.systemSetting.form.verify.atLeastOneField"), function () {
      return (
        (this.parent.defaultGroup && this.parent.defaultGroup.id !== systemSettingDefaultGroupVO.value.id) ||
        this.parent.allowSignup ||
        this.parent.allowRandomFetch
      );
    }),
  allowSignup: yup
    .boolean()
    .test("at-least-one-field", t("settingView.systemSetting.form.verify.atLeastOneField"), function () {
      return (
        (this.parent.defaultGroup && this.parent.defaultGroup.id !== systemSettingDefaultGroupVO.value.id) ||
        this.parent.allowSignup ||
        this.parent.allowRandomFetch
      );
    }),
  allowRandomFetch: yup
    .boolean()
    .test("at-least-one-field", t("settingView.systemSetting.form.verify.atLeastOneField"), function () {
      return (
        (this.parent.defaultGroup && this.parent.defaultGroup.id !== systemSettingDefaultGroupVO.value.id) ||
        this.parent.allowSignup ||
        this.parent.allowRandomFetch
      );
    })
});

const { handleSubmit, defineField, errors } = useForm({
  validationSchema: systemSettingFormSchema
});

const [defaultGroup, defaultGroupAttrs] = defineField("defaultGroup");

const onSystemSettingFormSubmit = handleSubmit((values) => {
  const patchRequest: SystemSettingPatchRequest = {
    defaultGroupId: values.defaultGroup?.id,
    allowSignup: values.allowSignup,
    allowRandomFetch: values.allowRandomFetch
  };
  patchSystemSetting(patchRequest);
});

onMounted(async () => {
  await init();
});

async function init() {
  showLoadingDialog.value = true;
  try {
    await fetchSystemSetting();
    if (systemSetting.value?.defaultGroupId) {
      systemSettingDefaultGroupVO.value = await fetchGroup(systemSetting.value.defaultGroupId);
    }
  } finally {
    showLoadingDialog.value = false;
  }
}

async function patchSystemSetting(patchRequest: SystemSettingPatchRequest) {
  return settingApi
    .apiSettingSystemPatch({ systemSettingPatchRequest: patchRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("settingView.systemSetting.toast.patch.successTitle"),
          detail: resp.message,
          life: 3000
        });
        init();
      } else {
        toast.add({
          severity: "warn",
          summary: t("settingView.systemSetting.toast.patch.failedTitle"),
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

async function fetchSystemSetting() {
  return settingApi
    .apiSettingSettingTypeGet({ settingType: "system" })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        systemSetting.value = resp.data?.config;
      } else {
        toast.add({
          severity: "warn",
          summary: t("settingView.systemSetting.toast.fetch.failedTitle"),
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

async function fetchGroup(groupId: number) {
  return groupApi
    .apiGroupIdGet({ id: groupId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        return resp.data;
      } else {
        toast.add({
          severity: "warn",
          summary: t("settingView.systemSetting.toast.fetch.groupFailedTitle"),
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
          summary: t("settingView.systemSetting.toast.fetch.groupFailedTitle"),
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
  <div class="w-full m-8 flex flex-col items-center gap-4">
    <Toolbar class="w-full">
      <template #start>
        <h1 class="text-2xl font-bold">{{ t("settingView.systemSetting.title") }}</h1>
      </template>
    </Toolbar>
    <div class="w-full bg-white rounded flex-grow flex flex-col justify-center items-center">
      <form
        :initial-values="systemSettingFormInitValue"
        @submit="onSystemSettingFormSubmit"
        class="flex flex-col gap-4 w-1/3"
      >
        <div class="flex flex-col gap-2.5 w-full">
          <div class="flex flex-col gap-1">
            <IftaLabel>
              <Select
                id="systemSettingFormDefaultGroup"
                name="defaultGroup"
                :options="groupPageData"
                optionLabel="name"
                :placeholder="systemSettingDefaultGroupVO?.name"
                v-model="defaultGroup"
                v-bind="defaultGroupAttrs"
                :class="{ 'p-invalid': !!errors.defaultGroup }"
                :virtualScrollerOptions="{
                  lazy: true,
                  showLoader: true,
                  loading: groupSelectLoading,
                  onLazyLoad: debounceHandleGroupSelectLazyLoading
                }"
                fluid
              />
              <label for="systemSettingFormDefaultGroup">
                {{ t("settingView.systemSetting.form.defaultGroupId") }}
              </label>
            </IftaLabel>
            <Message v-if="errors.defaultGroup" severity="error" size="small" variant="simple">
              {{ errors.defaultGroup }}
            </Message>
          </div>
          <div class="flex flex-col gap-1">
            <VeeToggleSwitch
              id="systemSettingFormAllowSignup"
              name="allowSignup"
              :label="t('settingView.systemSetting.form.allowSignup')"
              :defaultValue="systemSetting?.allowSignup as boolean"
            />
          </div>
          <div class="flex flex-col gap-1">
            <VeeToggleSwitch
              id="systemSettingFormAllowRandomFetch"
              name="allowRandomFetch"
              :label="t('settingView.systemSetting.form.allowRandomFetch')"
              :defaultValue="systemSetting?.allowRandomFetch as boolean"
            />
          </div>
        </div>
        <div class="flex justify-end gap-2">
          <Button type="submit" :label="t('settingView.systemSetting.form.submitButton')" />
        </div>
      </form>
    </div>

    <LoadingDialog v-model:visible="showLoadingDialog" />
  </div>
</template>

<style scoped></style>
