<script setup lang="ts">
import { computed, onMounted, type PropType, ref } from "vue";
import { useForm } from "vee-validate";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import {
  RoleApi,
  StrategyApi,
  ImageTypeEnum,
  type StrategyApiApiStrategyPageGetRequest,
  type RoleApiApiRolePageGetRequest,
  type GroupVO
} from "api-client";
import * as yup from "yup";
import Button from "primevue/button";
import Select from "primevue/select";
import MultiSelect from "primevue/multiselect";
import Message from "primevue/message";
import InputText from "primevue/inputtext";
import IftaLabel from "primevue/iftalabel";
import Accordion from "primevue/accordion";
import AccordionPanel from "primevue/accordionpanel";
import AccordionHeader from "primevue/accordionheader";
import AccordionContent from "primevue/accordioncontent";
import { debounce } from "lodash-es";
import type { VirtualScrollerLazyEvent } from "primevue";

const { t } = useI18n();
const toast = useToast();

const { roleApi, strategyApi, groupDetail } = defineProps({
  roleApi: {
    type: Object as PropType<RoleApi>,
    required: true
  },
  strategyApi: {
    type: Object as PropType<StrategyApi>,
    required: true
  },
  groupDetail: {
    type: Object as PropType<GroupVO>,
    required: true
  }
});

const emits = defineEmits(["submit", "cancel"]);

interface RoleSelectType {
  name: string;
}

interface StrategySelectType {
  id: number;
  name: string;
}

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
const strategyData = ref<StrategySelectType[]>([]);

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
const roleData = ref<RoleSelectType[]>([]);

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
        const pageData = pageResult.data;
        return pageData.map((strategy) => ({ id: strategy.id, name: strategy.name }));
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminGroupManageView.edit.toast.pageStrategyFailedTitle"),
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
        const pageData = pageResult.data;
        return pageData.map((role) => ({ name: role.name }));
      } else {
        toast.add({
          severity: "warn",
          summary: t("adminGroupManageView.edit.toast.pageRoleFailedTitle"),
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

const strategyEditFormSchema = yup.object({
  name: yup
    .string()
    .trim()
    .required(t("adminGroupManageView.edit.dialog.form.verify.groupName.required"))
    .test(
      "at-least-one-field",
      t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
      function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[0].value : {};
        return (
          formValues.name !== groupDetail.name ||
          formValues.description !== groupDetail.description ||
          formValues.strategy.id !== groupDetail.strategyId ||
          isRoleModified() ||
          formValues.config.strategyConfig.singleFileMaxSize !==
            groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
          formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
          formValues.config.strategyConfig.pathNamingRule !==
            groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
          formValues.config.strategyConfig.fileNamingRule !==
            groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
          formValues.config.strategyConfig.imageQuality !== groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
          formValues.config.strategyConfig.imageAutoTransformTarget !==
            groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
          isAllowedImageTypesModified()
        );
      }
    ),
  description: yup
    .string()
    .trim()
    .test(
      "at-least-one-field",
      t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
      function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[0].value : {};
        return (
          formValues.name !== groupDetail.name ||
          formValues.description !== groupDetail.description ||
          formValues.strategy.id !== groupDetail.strategyId ||
          isRoleModified() ||
          formValues.config.strategyConfig.singleFileMaxSize !==
            groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
          formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
          formValues.config.strategyConfig.pathNamingRule !==
            groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
          formValues.config.strategyConfig.fileNamingRule !==
            groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
          formValues.config.strategyConfig.imageQuality !== groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
          formValues.config.strategyConfig.imageAutoTransformTarget !==
            groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
          isAllowedImageTypesModified()
        );
      }
    ),
  config: yup.object({
    strategyConfig: yup.object({
      singleFileMaxSize: yup
        .number()
        .typeError(t("adminGroupManageView.edit.dialog.form.verify.config.strategy.singleFileMaxSize.invalid"))
        .required(t("adminGroupManageView.edit.dialog.form.verify.config.strategy.singleFileMaxSize.required"))
        .test(
          "at-least-one-field",
          t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
          function (_, context) {
            const { from } = context;
            const formValues = from && from.length > 0 ? from[2].value : {};
            return (
              formValues.name !== groupDetail.name ||
              formValues.description !== groupDetail.description ||
              formValues.strategy.id !== groupDetail.strategyId ||
              isRoleModified() ||
              formValues.config.strategyConfig.singleFileMaxSize !==
                groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
              formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
              formValues.config.strategyConfig.pathNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
              formValues.config.strategyConfig.fileNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
              formValues.config.strategyConfig.imageQuality !==
                groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
              formValues.config.strategyConfig.imageAutoTransformTarget !==
                groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
              isAllowedImageTypesModified()
            );
          }
        ),
      maxSize: yup
        .number()
        .typeError(t("adminGroupManageView.edit.dialog.form.verify.config.strategy.maxSize.invalid"))
        .required(t("adminGroupManageView.edit.dialog.form.verify.config.strategy.maxSize.required"))
        .test(
          "at-least-one-field",
          t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
          function (_, context) {
            const { from } = context;
            const formValues = from && from.length > 0 ? from[2].value : {};
            return (
              formValues.name !== groupDetail.name ||
              formValues.description !== groupDetail.description ||
              formValues.strategy.id !== groupDetail.strategyId ||
              isRoleModified() ||
              formValues.config.strategyConfig.singleFileMaxSize !==
                groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
              formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
              formValues.config.strategyConfig.pathNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
              formValues.config.strategyConfig.fileNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
              formValues.config.strategyConfig.imageQuality !==
                groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
              formValues.config.strategyConfig.imageAutoTransformTarget !==
                groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
              isAllowedImageTypesModified()
            );
          }
        ),
      pathNamingRule: yup
        .string()
        .trim()
        .required(t("adminGroupManageView.edit.dialog.form.verify.config.strategy.pathNamingRule.required"))
        .test(
          "at-least-one-field",
          t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
          function (_, context) {
            const { from } = context;
            const formValues = from && from.length > 0 ? from[2].value : {};
            return (
              formValues.name !== groupDetail.name ||
              formValues.description !== groupDetail.description ||
              formValues.strategy.id !== groupDetail.strategyId ||
              isRoleModified() ||
              formValues.config.strategyConfig.singleFileMaxSize !==
                groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
              formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
              formValues.config.strategyConfig.pathNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
              formValues.config.strategyConfig.fileNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
              formValues.config.strategyConfig.imageQuality !==
                groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
              formValues.config.strategyConfig.imageAutoTransformTarget !==
                groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
              isAllowedImageTypesModified()
            );
          }
        ),
      fileNamingRule: yup
        .string()
        .trim()
        .required(t("adminGroupManageView.edit.dialog.form.verify.config.strategy.fileNamingRule.required"))
        .test(
          "at-least-one-field",
          t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
          function (_, context) {
            const { from } = context;
            const formValues = from && from.length > 0 ? from[2].value : {};
            return (
              formValues.name !== groupDetail.name ||
              formValues.description !== groupDetail.description ||
              formValues.strategy.id !== groupDetail.strategyId ||
              isRoleModified() ||
              formValues.config.strategyConfig.singleFileMaxSize !==
                groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
              formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
              formValues.config.strategyConfig.pathNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
              formValues.config.strategyConfig.fileNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
              formValues.config.strategyConfig.imageQuality !==
                groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
              formValues.config.strategyConfig.imageAutoTransformTarget !==
                groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
              isAllowedImageTypesModified()
            );
          }
        ),
      imageQuality: yup
        .number()
        .typeError(t("adminGroupManageView.edit.dialog.form.verify.config.strategy.imageQuality.invalid"))
        .min(0, t("adminGroupManageView.edit.dialog.form.verify.config.strategy.imageQuality.min"))
        .max(100, t("adminGroupManageView.edit.dialog.form.verify.config.strategy.imageQuality.max"))
        .required(t("adminGroupManageView.edit.dialog.form.verify.config.strategy.imageQuality.required"))
        .test(
          "at-least-one-field",
          t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
          function (_, context) {
            const { from } = context;
            const formValues = from && from.length > 0 ? from[2].value : {};
            return (
              formValues.name !== groupDetail.name ||
              formValues.description !== groupDetail.description ||
              formValues.strategy.id !== groupDetail.strategyId ||
              isRoleModified() ||
              formValues.config.strategyConfig.singleFileMaxSize !==
                groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
              formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
              formValues.config.strategyConfig.pathNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
              formValues.config.strategyConfig.fileNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
              formValues.config.strategyConfig.imageQuality !==
                groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
              formValues.config.strategyConfig.imageAutoTransformTarget !==
                groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
              isAllowedImageTypesModified()
            );
          }
        ),
      imageAutoTransformTarget: yup
        .object({
          name: yup.string(),
          value: yup
            .mixed()
            .oneOf(
              Object.values(ImageTypeEnum),
              t("adminGroupManageView.edit.dialog.form.verify.config.strategy.imageAutoTransformTarget.invalid")
            )
        })
        .nullable()
        .test(
          "at-least-one-field",
          t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
          function (_, context) {
            const { from } = context;
            const formValues = from && from.length > 0 ? from[3].value : {};
            return (
              formValues.name !== groupDetail.name ||
              formValues.description !== groupDetail.description ||
              formValues.strategy.id !== groupDetail.strategyId ||
              isRoleModified() ||
              formValues.config.strategyConfig.singleFileMaxSize !==
                groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
              formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
              formValues.config.strategyConfig.pathNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
              formValues.config.strategyConfig.fileNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
              formValues.config.strategyConfig.imageQuality !==
                groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
              formValues.config.strategyConfig.imageAutoTransformTarget !==
                groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
              isAllowedImageTypesModified()
            );
          }
        ),
      allowedImageTypes: yup
        .array()
        .of(
          yup.object({
            value: yup
              .mixed()
              .oneOf(
                Object.values(ImageTypeEnum),
                t("adminGroupManageView.edit.dialog.form.verify.config.strategy.allowedImageTypes.invalid")
              )
          })
        )
        .required()
        .test(
          "at-least-one-field",
          t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
          function (_, context) {
            const { from } = context;
            const formValues = from && from.length > 0 ? from[2].value : {};
            return (
              formValues.name !== groupDetail.name ||
              formValues.description !== groupDetail.description ||
              formValues.strategy.id !== groupDetail.strategyId ||
              isRoleModified() ||
              formValues.config.strategyConfig.singleFileMaxSize !==
                groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
              formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
              formValues.config.strategyConfig.pathNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
              formValues.config.strategyConfig.fileNamingRule !==
                groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
              formValues.config.strategyConfig.imageQuality !==
                groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
              formValues.config.strategyConfig.imageAutoTransformTarget !==
                groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
              isAllowedImageTypesModified()
            );
          }
        )
    })
  }),
  strategy: yup.object({
    id: yup
      .number()
      .required(t("adminGroupManageView.edit.dialog.form.verify.strategy.required"))
      .test(
        "at-least-one-field",
        t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
        function (_, context) {
          const { from } = context;
          const formValues = from && from.length > 0 ? from[1].value : {};
          return (
            formValues.name !== groupDetail.name ||
            formValues.description !== groupDetail.description ||
            formValues.strategy.id !== groupDetail.strategyId ||
            isRoleModified() ||
            formValues.config.strategyConfig.singleFileMaxSize !==
              groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
            formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
            formValues.config.strategyConfig.pathNamingRule !==
              groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
            formValues.config.strategyConfig.fileNamingRule !==
              groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
            formValues.config.strategyConfig.imageQuality !==
              groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
            formValues.config.strategyConfig.imageAutoTransformTarget !==
              groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
            isAllowedImageTypesModified()
          );
        }
      )
  }),
  roles: yup
    .array()
    .of(
      yup.object({
        name: yup.string().required(t("adminGroupManageView.edit.dialog.form.verify.roles.invalid"))
      })
    )
    .min(1)
    .required(t("adminGroupManageView.edit.dialog.form.verify.roles.required"))
    .test(
      "at-least-one-field",
      t("adminGroupManageView.edit.dialog.form.verify.atLeastOneField"),
      function (_, context) {
        const { from } = context;
        const formValues = from && from.length > 0 ? from[0].value : {};
        return (
          formValues.name !== groupDetail.name ||
          formValues.description !== groupDetail.description ||
          formValues.strategy.id !== groupDetail.strategyId ||
          isRoleModified() ||
          formValues.config.strategyConfig.singleFileMaxSize !==
            groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize ||
          formValues.config.strategyConfig.maxSize !== groupDetail.groupConfig.groupStrategyConfig.maxSize ||
          formValues.config.strategyConfig.pathNamingRule !==
            groupDetail.groupConfig.groupStrategyConfig.pathNamingRule ||
          formValues.config.strategyConfig.fileNamingRule !==
            groupDetail.groupConfig.groupStrategyConfig.fileNamingRule ||
          formValues.config.strategyConfig.imageQuality !== groupDetail.groupConfig.groupStrategyConfig.imageQuality ||
          formValues.config.strategyConfig.imageAutoTransformTarget !==
            groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget ||
          isAllowedImageTypesModified()
        );
      }
    )
});

const { handleSubmit, errors, defineField } = useForm({
  validationSchema: strategyEditFormSchema
});

const [name, nameAttrs] = defineField("name");
const [description, descriptionAttrs] = defineField("description");
const [strategy, strategyAttrs] = defineField("strategy");
const [roles, rolesAttrs] = defineField("roles");

const [singleFileMaxSize, singleFileMaxSizeAttrs] = defineField("config.strategyConfig.singleFileMaxSize");
const [maxSize, maxSizeAttrs] = defineField("config.strategyConfig.maxSize");
const [pathNamingRule, pathNamingRuleAttrs] = defineField("config.strategyConfig.pathNamingRule");
const [fileNamingRule, fileNamingRuleAttrs] = defineField("config.strategyConfig.fileNamingRule");
const [imageQuality, imageQualityAttrs] = defineField("config.strategyConfig.imageQuality");
const [imageAutoTransformTarget, imageAutoTransformTargetAttrs] = defineField(
  "config.strategyConfig.imageAutoTransformTarget"
);
const [allowedImageTypes, allowedImageTypesAttrs] = defineField("config.strategyConfig.allowedImageTypes");

const groupDetailRoles = (groupDetail.roles as Array<string>).map((role) => ({ name: role }));

onMounted(async () => {
  while (!groupDetailRoles.every((needRole) => roleData.value.some((role) => role.name === needRole.name))) {
    await pageRole(rolePageRequest.value).then((data) => {
      rolePage.value++;
      if (data) roleData.value.push(...data);
    });
  }
});

name.value = groupDetail.name;
description.value = groupDetail.description;
strategy.value = { id: groupDetail.strategyId, name: groupDetail.strategyName };
roles.value = groupDetailRoles;

singleFileMaxSize.value = groupDetail.groupConfig.groupStrategyConfig.singleFileMaxSize;
maxSize.value = groupDetail.groupConfig.groupStrategyConfig.maxSize;
pathNamingRule.value = groupDetail.groupConfig.groupStrategyConfig.pathNamingRule;
fileNamingRule.value = groupDetail.groupConfig.groupStrategyConfig.fileNamingRule;
imageQuality.value = groupDetail.groupConfig.groupStrategyConfig.imageQuality;
imageAutoTransformTarget.value = groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget
  ? {
      name: groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget,
      value: groupDetail.groupConfig.groupStrategyConfig.imageAutoTransformTarget
    }
  : null;
allowedImageTypes.value = (
  groupDetail.groupConfig.groupStrategyConfig.allowedImageTypes as unknown as Array<ImageTypeEnum>
).map((type) => ({ name: type, value: type }));

function isRoleModified(): boolean {
  const oldRoles = groupDetail.roles;
  const newRoles = roles.value.map((role: RoleSelectType) => role.name);
  return oldRoles.length !== newRoles.length || oldRoles.some((role) => !newRoles.includes(role));
}

function isAllowedImageTypesModified(): boolean {
  const oldAllowedImageTypes = groupDetail.groupConfig.groupStrategyConfig
    .allowedImageTypes as unknown as Array<ImageTypeEnum>;
  const newAllowedImageTypes = allowedImageTypes.value.map((type: any) => type.name);
  return (
    oldAllowedImageTypes.length !== newAllowedImageTypes.length ||
    oldAllowedImageTypes.some((type) => !newAllowedImageTypes.includes(type))
  );
}

const onSubmit = handleSubmit((values, ctx) => {
  emits("submit", values, ctx);
});

const onCancel = () => {
  emits("cancel");
};
</script>

<template>
  <form @submit="onSubmit" class="flex flex-col gap-4 m-4 w-96">
    <div class="flex flex-col gap-2.5 w-full max-h-[42rem] overflow-y-auto">
      <div class="flex flex-col gap-1">
        <IftaLabel>
          <InputText
            id="editFormName"
            name="name"
            v-model="name"
            v-bind="nameAttrs"
            :class="{ 'p-invalid': !!errors.name }"
            :disabled="groupDetail.isSystemReserved"
            fluid
          />
          <label for="editFormName">{{ t("adminGroupManageView.edit.dialog.form.name") }}</label>
        </IftaLabel>
        <Message v-if="errors.name" severity="error" size="small" variant="simple">
          {{ errors.name }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <IftaLabel>
          <InputText
            id="editFormDesc"
            name="description"
            v-model="description"
            v-bind="descriptionAttrs"
            :class="{ 'p-invalid': !!errors.description }"
            fluid
          />
          <label for="editFormDesc">{{ t("adminGroupManageView.edit.dialog.form.description") }}</label>
        </IftaLabel>
        <Message v-if="errors.description" severity="error" size="small" variant="simple">
          {{ errors.description }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <IftaLabel>
          <Select
            id="editFormStrategy"
            name="strategy"
            v-model="strategy"
            v-bind="strategyAttrs"
            :options="strategyData"
            optionLabel="name"
            :placeholder="groupDetail.strategyName"
            :class="{ 'p-invalid': !!errors.strategy }"
            :virtualScrollerOptions="{
              lazy: true,
              showLoader: true,
              loading: strategySelectLoading,
              onLazyLoad: debounceHandleStrategySelectLazyLoading
            }"
            fluid
          />
          <label for="editFormStrategy">
            {{ t("adminGroupManageView.edit.dialog.form.strategy") }}
          </label>
        </IftaLabel>
        <Message v-if="errors.strategy" severity="error" size="small" variant="simple">
          {{ errors.strategy }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <IftaLabel>
          <MultiSelect
            id="editFormRole"
            name="roles"
            v-model="roles"
            v-bind="rolesAttrs"
            :options="roleData"
            optionLabel="name"
            :class="{ 'p-invalid': !!errors.roles }"
            :disabled="groupDetail.isSystemReserved"
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
          <label for="editFormRole">
            {{ t("adminGroupManageView.edit.dialog.form.roles") }}
          </label>
        </IftaLabel>
        <Message v-if="errors.roles" severity="error" size="small" variant="simple">
          {{ errors.roles }}
        </Message>
      </div>
      <Accordion value="0">
        <AccordionPanel value="0">
          <AccordionHeader>{{ t("adminGroupManageView.edit.dialog.form.config.strategy.title") }}</AccordionHeader>
          <AccordionContent>
            <div class="flex flex-col gap-2.5 w-full">
              <div class="flex flex-col gap-1">
                <IftaLabel>
                  <InputText
                    id="editFormStrategyConfigSingleFileMaxSize"
                    name="config.strategyConfig.singleFileMaxSize"
                    type="number"
                    v-model="singleFileMaxSize"
                    v-bind="singleFileMaxSizeAttrs"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.singleFileMaxSize'] }"
                    fluid
                  />
                  <label for="editFormStrategyConfigSingleFileMaxSize">{{
                    t("adminGroupManageView.edit.dialog.form.config.strategy.singleFileMaxSize")
                  }}</label>
                </IftaLabel>
                <Message
                  v-if="errors['config.strategyConfig.singleFileMaxSize']"
                  severity="error"
                  size="small"
                  variant="simple"
                >
                  {{ errors["config.strategyConfig.singleFileMaxSize"] }}
                </Message>
              </div>
              <div class="flex flex-col gap-1">
                <IftaLabel>
                  <InputText
                    id="editFormStrategyConfigMaxSize"
                    name="config.strategyConfig.maxSize"
                    type="number"
                    v-model="maxSize"
                    v-bind="maxSizeAttrs"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.maxSize'] }"
                    fluid
                  />
                  <label for="editFormStrategyConfigMaxSize">{{
                    t("adminGroupManageView.edit.dialog.form.config.strategy.maxSize")
                  }}</label>
                </IftaLabel>
                <Message v-if="errors['config.strategyConfig.maxSize']" severity="error" size="small" variant="simple">
                  {{ errors["config.strategyConfig.maxSize"] }}
                </Message>
              </div>
              <div class="flex flex-col gap-1">
                <IftaLabel>
                  <InputText
                    id="editFormStrategyConfigPathNamingRule"
                    name="config.strategyConfig.pathNamingRule"
                    v-model="pathNamingRule"
                    v-bind="pathNamingRuleAttrs"
                    v-tooltip="{
                      value: t('adminGroupManageView.tips.namingRulePlaceholder'),
                      autoHide: false,
                      pt: {
                        root: {
                          class: 'min-w-fit'
                        }
                      }
                    }"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.pathNamingRule'] }"
                    fluid
                  />
                  <label for="editFormStrategyConfigPathNamingRule">
                    {{ t("adminGroupManageView.edit.dialog.form.config.strategy.pathNamingRule") }}
                  </label>
                </IftaLabel>
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
                <IftaLabel>
                  <InputText
                    id="editFormStrategyConfigFileNamingRule"
                    name="config.strategyConfig.fileNamingRule"
                    v-model="fileNamingRule"
                    v-bind="fileNamingRuleAttrs"
                    v-tooltip="{
                      value: t('adminGroupManageView.tips.namingRulePlaceholder'),
                      pt: {
                        root: {
                          class: 'min-w-fit'
                        }
                      }
                    }"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.fileNamingRule'] }"
                    fluid
                  />
                  <label for="editFormStrategyConfigFileNamingRule">
                    {{ t("adminGroupManageView.edit.dialog.form.config.strategy.fileNamingRule") }}
                  </label>
                </IftaLabel>
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
                <IftaLabel>
                  <InputText
                    id="editFormStrategyConfigImageQuality"
                    name="config.strategyConfig.imageQuality"
                    v-model="imageQuality"
                    v-bind="imageQualityAttrs"
                    type="number"
                    :class="{ 'p-invalid': !!errors['config.strategyConfig.imageQuality'] }"
                    fluid
                  />
                  <label for="editFormStrategyConfigImageQuality">
                    {{ t("adminGroupManageView.edit.dialog.form.config.strategy.imageQuality") }}
                  </label>
                </IftaLabel>
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
                <IftaLabel>
                  <Select
                    id="editFormStrategyConfigImageAutoTransformTarget"
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
                  <label for="editFormStrategyConfigImageAutoTransformTarget">
                    {{ t("adminGroupManageView.edit.dialog.form.config.strategy.imageAutoTransformTarget") }}
                  </label>
                </IftaLabel>
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
                <IftaLabel>
                  <MultiSelect
                    id="editFormAllowedImageTypes"
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
                  <label for="editFormRole">
                    {{ t("adminGroupManageView.edit.dialog.form.config.strategy.allowedImageTypes") }}
                  </label>
                </IftaLabel>
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
        :label="t('adminGroupManageView.edit.dialog.form.cancelButton')"
        severity="secondary"
        @click="onCancel"
      />
      <Button type="submit" :label="t('adminGroupManageView.edit.dialog.form.submitButton')" />
    </div>
  </form>
</template>

<style scoped></style>
