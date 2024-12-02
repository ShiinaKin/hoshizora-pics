<script setup lang="ts">
import { computed, type PropType, ref } from "vue";
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import {
  ImageTypeEnum,
  RoleApi,
  type RoleApiApiRolePageGetRequest,
  type RolePageVO,
  StrategyApi,
  type StrategyApiApiStrategyPageGetRequest,
  type StrategyPageVO
} from "api-client";
import * as yup from "yup";
import VeeFloatInputText from "@/components/vee-input/VeeFloatInputText.vue";
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import Select from "primevue/select";
import MultiSelect from "primevue/multiselect";
import Message from "primevue/message";
import FloatLabel from "primevue/floatlabel";
import Accordion from "primevue/accordion";
import AccordionPanel from "primevue/accordionpanel";
import AccordionHeader from "primevue/accordionheader";
import AccordionContent from "primevue/accordioncontent";
import { debounce } from "lodash-es";
import type { VirtualScrollerLazyEvent } from "primevue";

const { t } = useI18n();
const toast = useToast();

const { roleApi, strategyApi } = defineProps({
  roleApi: {
    type: Object as PropType<RoleApi>,
    required: true
  },
  strategyApi: {
    type: Object as PropType<StrategyApi>,
    required: true
  }
});

const emits = defineEmits(["submit", "cancel"]);

const allowedImageTypeArr = ref(Object.values(ImageTypeEnum).map((value) => ({ name: value, value: value })));

const strategyPage = ref(1);
const strategyPageSize = ref(15);
const strategyOrderBy = ref("createTime");
const strategyOrder = ref("DESC");
const strategyPageRequest = computed<StrategyApiApiStrategyPageGetRequest>(() => {
  return {
    page: strategyPage.value,
    pageSize: strategyPageSize.value,
    orderBy: strategyOrderBy.value === "" ? undefined : strategyOrderBy.value,
    order: strategyOrder.value === "" ? undefined : strategyOrder.value
  };
});
const strategyCurPage = ref(1);
const strategyTotalRecord = ref(-1);
const strategyData = ref<StrategyPageVO[]>([]);

const rolePage = ref(1);
const rolePageSize = ref(15);
const roleOrderBy = ref("createTime");
const roleOrder = ref("DESC");
const rolePageRequest = computed<RoleApiApiRolePageGetRequest>(() => {
  return {
    page: rolePage.value,
    pageSize: rolePageSize.value,
    orderBy: roleOrderBy.value === "" ? undefined : roleOrderBy.value,
    order: roleOrder.value === "" ? undefined : roleOrder.value
  };
});
const roleCurPage = ref(1);
const roleTotalRecord = ref(-1);
const roleData = ref<RolePageVO[]>([]);

const strategySelectLoading = ref(false);
const roleSelectLoading = ref(false);

const debounceHandleStrategySelectLazyLoading = debounce(
  (event: VirtualScrollerLazyEvent) => handleStrategySelectLazyLoading(event),
  200
);

function handleStrategySelectLazyLoading(event: VirtualScrollerLazyEvent) {
  if (strategyData.value.length === 0) {
    pageStrategy(strategyPageRequest.value)
      .then((data) => {
        if (data instanceof Array) strategyData.value = data;
      })
      .then(() => (strategySelectLoading.value = false));
    return;
  }
  const { last } = event;
  if (strategyTotalRecord.value === last) return;
  strategyPage.value += 1;
  pageStrategy(strategyPageRequest.value)
    .then((data) => {
      if (data) strategyData.value.push(...data);
    })
    .then(() => (strategySelectLoading.value = false));
}

const debounceHandleRoleSelectLazyLoading = debounce(
  (event: VirtualScrollerLazyEvent) => handleRoleSelectLazyLoading(event),
  200
);

function handleRoleSelectLazyLoading(event: VirtualScrollerLazyEvent) {
  if (roleData.value.length === 0) {
    pageRole(rolePageRequest.value)
      .then((data) => {
        if (data instanceof Array) roleData.value = data;
      })
      .then(() => (roleSelectLoading.value = false));
    return;
  }
  const { last } = event;
  if (roleTotalRecord.value === last) return;
  rolePage.value += 1;
  pageRole(rolePageRequest.value)
    .then((data) => {
      if (data) roleData.value.push(...data);
    })
    .then(() => (roleSelectLoading.value = false));
}

async function pageStrategy(pageRequest: StrategyApiApiStrategyPageGetRequest) {
  return strategyApi
    .apiStrategyPageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        const pageResult = resp.data!;
        strategyCurPage.value = pageResult.page;
        strategyTotalRecord.value = pageResult.total;
        return pageResult.data;
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminGroupManageView.create.toast.pageStrategyFailedTitle"),
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

async function pageRole(pageRequest: RoleApiApiRolePageGetRequest) {
  return roleApi
    .apiRolePageGet(pageRequest)
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        const pageResult = resp.data!;
        roleCurPage.value = pageResult.page;
        roleTotalRecord.value = pageResult.total;
        return pageResult.data;
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminGroupManageView.create.toast.pageRoleFailedTitle"),
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

const strategyCreateFormSchema = yup.object({
  name: yup.string().trim().required(t("adminGroupManageView.create.dialog.form.verify.groupName.required")),
  description: yup.string().trim(),
  config: yup.object({
    strategyConfig: yup.object({
      singleFileMaxSize: yup
        .number()
        .typeError(t("adminGroupManageView.create.dialog.form.verify.config.strategy.singleFileMaxSize.invalid"))
        .required(t("adminGroupManageView.create.dialog.form.verify.config.strategy.singleFileMaxSize.required")),
      maxSize: yup
        .number()
        .typeError(t("adminGroupManageView.create.dialog.form.verify.config.strategy.maxSize.invalid"))
        .required(t("adminGroupManageView.create.dialog.form.verify.config.strategy.maxSize.required")),
      pathNamingRule: yup
        .string()
        .trim()
        .required(t("adminGroupManageView.create.dialog.form.verify.config.strategy.pathNamingRule.required")),
      fileNamingRule: yup
        .string()
        .trim()
        .required(t("adminGroupManageView.create.dialog.form.verify.config.strategy.fileNamingRule.required")),
      imageQuality: yup
        .number()
        .typeError(t("adminGroupManageView.create.dialog.form.verify.config.strategy.imageQuality.invalid"))
        .min(0, t("adminGroupManageView.create.dialog.form.verify.config.strategy.imageQuality.min"))
        .max(100, t("adminGroupManageView.create.dialog.form.verify.config.strategy.imageQuality.max"))
        .required(t("adminGroupManageView.create.dialog.form.verify.config.strategy.imageQuality.required")),
      imageAutoTransformTarget: yup
        .object({
          name: yup.string(),
          value: yup
            .mixed()
            .oneOf(
              Object.values(ImageTypeEnum),
              t("adminGroupManageView.create.dialog.form.verify.config.strategy.imageAutoTransformTarget.invalid")
            )
        })
        .nullable(),
      allowedImageTypes: yup
        .array()
        .of(
          yup.object({
            value: yup
              .mixed()
              .oneOf(
                Object.values(ImageTypeEnum),
                t("adminGroupManageView.create.dialog.form.verify.config.strategy.allowedImageTypes.invalid")
              )
          })
        )
        .required()
    })
  }),
  strategy: yup.object({
    id: yup.number().required(t("adminGroupManageView.create.dialog.form.verify.strategy.required"))
  }),
  roles: yup
    .array()
    .of(
      yup.object({
        name: yup.string().required(t("adminGroupManageView.create.dialog.form.verify.roles.invalid"))
      })
    )
    .min(1)
    .required(t("adminGroupManageView.create.dialog.form.verify.roles.required"))
});

const { handleSubmit, errors, defineField } = useForm({
  validationSchema: strategyCreateFormSchema
});

const [strategy, strategyAttrs] = defineField("strategy");
const [roles, rolesAttrs] = defineField("roles");
const [pathNamingRule, pathNamingRuleAttrs] = defineField("config.strategyConfig.pathNamingRule");
const [fileNamingRule, fileNamingRuleAttrs] = defineField("config.strategyConfig.fileNamingRule");
const [imageQuality, imageQualityAttrs] = defineField("config.strategyConfig.imageQuality");
const [imageAutoTransformTarget, imageAutoTransformTargetAttrs] = defineField(
  "config.strategyConfig.imageAutoTransformTarget"
);
const [allowedImageTypes, allowedImageTypesAttrs] = defineField("config.strategyConfig.allowedImageTypes");

pathNamingRule.value = "{yyyy}/{MM}/{dd}";
fileNamingRule.value = "{uniq}";
imageQuality.value = 100;
allowedImageTypes.value = [
  { name: ImageTypeEnum.Jpeg, value: ImageTypeEnum.Jpeg },
  { name: ImageTypeEnum.Jpg, value: ImageTypeEnum.Jpg },
  { name: ImageTypeEnum.Png, value: ImageTypeEnum.Png },
  { name: ImageTypeEnum.Gif, value: ImageTypeEnum.Gif },
  { name: ImageTypeEnum.Webp, value: ImageTypeEnum.Webp }
];

const onSubmit = handleSubmit((values, ctx) => {
  emits("submit", values, ctx);
});

const onCancel = () => {
  emits("cancel");
};
</script>

<template>
  <form @submit="onSubmit" class="flex flex-col gap-4 m-4 w-96">
    <div class="flex flex-col gap-2.5 py-2 w-full max-h-[42rem] overflow-y-auto">
      <div class="flex flex-col gap-1">
        <VeeFloatInputText id="createFormName" name="name" :label="t('adminGroupManageView.create.dialog.form.name')" />
      </div>
      <div class="flex flex-col gap-1">
        <VeeFloatInputText
          id="createFormDesc"
          name="description"
          :label="t('adminGroupManageView.create.dialog.form.description')"
        />
      </div>
      <div class="flex flex-col gap-1">
        <FloatLabel variant="on">
          <Select
            id="createFormStrategy"
            name="strategy"
            v-model="strategy"
            v-bind="strategyAttrs"
            :options="strategyData"
            optionLabel="name"
            :class="{ 'p-invalid': !!errors.strategy }"
            :virtualScrollerOptions="{
              lazy: true,
              showLoader: true,
              loading: strategySelectLoading,
              onLazyLoad: debounceHandleStrategySelectLazyLoading
            }"
            fluid
          />
          <label for="createFormStrategy">
            {{ t("adminGroupManageView.create.dialog.form.strategy") }}
          </label>
        </FloatLabel>
        <Message v-if="errors.strategy" severity="error" size="small" variant="simple">
          {{ errors.strategy }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <FloatLabel variant="on">
          <MultiSelect
            id="createFormRole"
            name="roles"
            v-model="roles"
            v-bind="rolesAttrs"
            :options="roleData"
            optionLabel="name"
            :class="{ 'p-invalid': !!errors.roles }"
            :virtualScrollerOptions="{
              lazy: true,
              showLoader: true,
              loading: roleSelectLoading,
              onLazyLoad: debounceHandleRoleSelectLazyLoading
            }"
            :pt="{
              header: {
                class: 'hidden'
              }
            }"
            fluid
          />
          <label for="createFormRole">
            {{ t("adminGroupManageView.create.dialog.form.roles") }}
          </label>
        </FloatLabel>
        <Message v-if="errors.roles" severity="error" size="small" variant="simple">
          {{ errors.roles }}
        </Message>
      </div>
      <Accordion value="0">
        <AccordionPanel value="0">
          <AccordionHeader>{{ t("adminGroupManageView.create.dialog.form.config.strategy.title") }}</AccordionHeader>
          <AccordionContent>
            <div class="flex flex-col gap-2.5 w-full">
              <div class="flex flex-col gap-1">
                <VeeFloatInputText
                  id="createFormStrategyConfigSingleFileMaxSize"
                  name="config.strategyConfig.singleFileMaxSize"
                  type="number"
                  :label="t('adminGroupManageView.create.dialog.form.config.strategy.singleFileMaxSize')"
                />
              </div>
              <div class="flex flex-col gap-1">
                <VeeFloatInputText
                  id="createFormStrategyConfigMaxSize"
                  name="config.strategyConfig.maxSize"
                  type="number"
                  :label="t('adminGroupManageView.create.dialog.form.config.strategy.maxSize')"
                />
              </div>
              <div class="flex flex-col gap-1">
                <FloatLabel variant="on">
                  <InputText
                    id="createFormStrategyConfigPathNamingRule"
                    name="config.strategyConfig.pathNamingRule"
                    v-model="pathNamingRule"
                    v-bind="pathNamingRuleAttrs"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.pathNamingRule'] }"
                    fluid
                  />
                  <label for="createFormStrategyConfigPathNamingRule">
                    {{ t("adminGroupManageView.create.dialog.form.config.strategy.pathNamingRule") }}
                  </label>
                </FloatLabel>
                <Message
                  v-if="errors['config.strategyConfig.pathNamingRule']"
                  severity="error"
                  size="small"
                  variant="simple"
                >
                  {{ errors["config.strategyConfig.pathNamingRule"] }}
                </Message>
              </div>
              <div class="flex flex-col gap-1">
                <FloatLabel variant="on">
                  <InputText
                    id="createFormStrategyConfigFileNamingRule"
                    name="config.strategyConfig.fileNamingRule"
                    v-model="fileNamingRule"
                    v-bind="fileNamingRuleAttrs"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.fileNamingRule'] }"
                    fluid
                  />
                  <label for="createFormStrategyConfigFileNamingRule">
                    {{ t("adminGroupManageView.create.dialog.form.config.strategy.fileNamingRule") }}
                  </label>
                </FloatLabel>
                <Message
                  v-if="errors['config.strategyConfig.fileNamingRule']"
                  severity="error"
                  size="small"
                  variant="simple"
                >
                  {{ errors["config.strategyConfig.fileNamingRule"] }}
                </Message>
              </div>
              <div class="flex flex-col gap-1">
                <FloatLabel variant="on">
                  <InputText
                    id="createFormStrategyConfigImageQuality"
                    name="config.strategyConfig.imageQuality"
                    v-model="imageQuality"
                    v-bind="imageQualityAttrs"
                    type="number"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.imageQuality'] }"
                    fluid
                  />
                  <label for="createFormStrategyConfigImageQuality">
                    {{ t("adminGroupManageView.create.dialog.form.config.strategy.imageQuality") }}
                  </label>
                </FloatLabel>
                <Message
                  v-if="errors['config.strategyConfig.imageQuality']"
                  severity="error"
                  size="small"
                  variant="simple"
                >
                  {{ errors["config.strategyConfig.imageQuality"] }}
                </Message>
              </div>
              <div class="flex flex-col gap-1">
                <FloatLabel variant="on">
                  <Select
                    id="createFormStrategyConfigImageAutoTransformTarget"
                    name="config.strategyConfig.imageAutoTransformTarget"
                    :options="allowedImageTypeArr"
                    optionLabel="name"
                    showClear
                    v-model="imageAutoTransformTarget"
                    v-bind="imageAutoTransformTargetAttrs"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.imageAutoTransformTarget'] }"
                    :pt="{
                      header: {
                        class: 'hidden'
                      }
                    }"
                    fluid
                  />
                  <label for="createFormStrategyConfigImageAutoTransformTarget">
                    {{ t("adminGroupManageView.create.dialog.form.config.strategy.imageAutoTransformTarget") }}
                  </label>
                </FloatLabel>
                <Message
                  v-if="errors['config.strategyConfig.imageAutoTransformTarget']"
                  severity="error"
                  size="small"
                  variant="simple"
                >
                  {{ errors["config.strategyConfig.imageAutoTransformTarget"] }}
                </Message>
              </div>
              <div class="flex flex-col gap-1">
                <FloatLabel variant="on">
                  <MultiSelect
                    id="createFormAllowedImageTypes"
                    name="config.strategyConfig.allowedImageTypes"
                    v-model="allowedImageTypes"
                    v-bind="allowedImageTypesAttrs"
                    :options="allowedImageTypeArr"
                    optionLabel="name"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.allowedImageTypes'] }"
                    :pt="{
                      header: {
                        class: 'hidden'
                      }
                    }"
                    fluid
                  />
                  <label for="createFormRole">
                    {{ t("adminGroupManageView.create.dialog.form.config.strategy.allowedImageTypes") }}
                  </label>
                </FloatLabel>
                <Message
                  v-if="errors['config.strategyConfig.allowedImageTypes']"
                  severity="error"
                  size="small"
                  variant="simple"
                >
                  {{ errors["config.strategyConfig.allowedImageTypes"] }}
                </Message>
              </div>
            </div>
          </AccordionContent>
        </AccordionPanel>
      </Accordion>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        :label="t('adminGroupManageView.create.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button type="submit" :label="t('adminGroupManageView.create.dialog.form.submitButton')" />
    </div>
  </form>
</template>

<style scoped></style>
