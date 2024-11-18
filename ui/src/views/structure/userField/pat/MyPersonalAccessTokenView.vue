<script setup lang="ts">
import { computed, ref, reactive, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import {
  Configuration,
  PersonalAccessTokenApi,
  RoleApi,
  type RoleVO,
  type PersonalAccessTokenApiApiPersonalAccessTokenPageGetRequest,
  type PersonalAccessTokenPageVO,
  type PersonalAccessTokenInsertRequest,
  type PersonalAccessTokenPatchRequest
} from "api-client";
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import FloatLabel from "primevue/floatlabel";
import IftaLabel from "primevue/iftalabel";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Dialog from "primevue/dialog";
import DatePicker from "primevue/datepicker";
import Message from "primevue/message";
import Checkbox from "primevue/checkbox";
import Accordion from "primevue/accordion";
import AccordionPanel from "primevue/accordionpanel";
import AccordionHeader from "primevue/accordionheader";
import AccordionContent from "primevue/accordioncontent";
import BottomPaginator from "@/components/BottomPaginator.vue";
import { Form, type FormSubmitEvent } from "@primevue/forms";
import { yupResolver } from "@primevue/forms/resolvers/yup";
import { useToast } from "primevue/usetoast";
import * as yup from "yup";
import { Icon } from "@iconify/vue";
import dayjs from "dayjs";

const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const patApi = new PersonalAccessTokenApi(configuration);
const roleApi = new RoleApi(configuration);

const page = ref(1);
const pageSize = ref(20);
const isExpired = ref<boolean | undefined>();
const pageRequest = computed<PersonalAccessTokenApiApiPersonalAccessTokenPageGetRequest>(() => {
  return {
    page: page.value,
    pageSize: pageSize.value,
    isExpired: isExpired.value
  };
});

watchEffect(() => {
  pageUserPAT(pageRequest.value);
});

const curPage = ref(-1);
const totalPage = ref(-1);
const totalRecord = ref(-1);
const pageData = ref<PersonalAccessTokenPageVO[]>([]);

const curCreatedToken = ref("");

const curPATId = ref(-1);
const curPAT = ref<PersonalAccessTokenPageVO>();

const roles = ref<RoleVO[]>([]);

interface PATInsertForm {
  name: string | undefined;
  description: string | undefined;
  expireTime: Date | undefined;
  permissions: string[];
}

const patCreateDialog = ref(false);
const patCreatedTokenDisplayDialog = ref(false);
const patEditDialog = ref(false);
const patDeleteDialog = ref(false);

const createFormInitialValues = reactive<PATInsertForm>({
  name: undefined,
  description: undefined,
  expireTime: undefined,
  permissions: []
});

const createFormResolver = yupResolver(
  yup.object({
    name: yup.string().trim().required(t("myPatView.myPATCreateDialogPATNameRequirements")),
    description: yup.string().trim(),
    expireTime: yup.date().required(t("myPatView.myPATCreateDialogPATExpireTimeRequirements")),
    permissions: yup.array().of(yup.string())
  })
);

const editFormInitialValues = reactive<PersonalAccessTokenPatchRequest>({
  name: undefined,
  description: undefined
});

const editFormResolver = yupResolver(
  yup.object({
    name: yup
      .string()
      .trim()
      .test("not-both-empty", t("myPatView.myPATEditDialogValidationFailedMessage"), function (value) {
        const { description } = this.parent;
        return !isEmpty(value) || !isEmpty(description);
      }),
    description: yup
      .string()
      .trim()
      .test("not-both-empty", t("myPatView.myPATEditDialogValidationFailedMessage"), function (value) {
        const { name } = this.parent;
        return !isEmpty(value) || !isEmpty(name);
      })
  })
);

function isEmpty(value: string | undefined): boolean {
  return value === undefined || value === "";
}

function showCreatePATDialog() {
  if (roles.value.length !== 0) {
    patCreateDialog.value = true;
  } else {
    fetchSelfRoles().then(() => {
      patCreateDialog.value = true;
    });
  }
}

function showPATDeleteDialog(patId: number) {
  curPATId.value = patId;
  patDeleteDialog.value = true;
}

function showPATEditDialog(pat: PersonalAccessTokenPageVO) {
  curPAT.value = pat;
  curPATId.value = pat.id;
  patEditDialog.value = true;
}

function closeTokenDisplayDialog() {
  curCreatedToken.value = "";
  patCreatedTokenDisplayDialog.value = false;
}

function copyAndCloseTokenDisplayDialog() {
  navigator.clipboard.writeText(curCreatedToken.value).then(() => {
    toast.add({
      severity: "success",
      summary: t("myPatView.myPATTokenDisplayDialogCopySuccessTitle"),
      detail: t("myPatView.myPATTokenDisplayDialogCopySuccessContent"),
      life: 3000
    });
    closeTokenDisplayDialog();
  });
}

const onCreateFormSubmit = ({ valid, values }: FormSubmitEvent) => {
  if (!valid) return;
  const insertRequest: PersonalAccessTokenInsertRequest = {
    name: values.name!,
    description: values.description || undefined,
    expireTime: dayjs(values.expireTime!).format("YYYY-MM-DDTHH:mm:ss.SSS"),
    permissions: values.permissions
  };
  createPAT(insertRequest).then((isSuccessful: boolean) => {
    patCreateDialog.value = false;
    if (isSuccessful) patCreatedTokenDisplayDialog.value = true;
  });
};

const onEditFormSubmit = ({ valid, values }: FormSubmitEvent) => {
  if (!valid) return;
  const patchRequest: PersonalAccessTokenPatchRequest = {
    name: values.name,
    description: values.description
  };
  patchPAT(curPATId.value, patchRequest).then(() => {
    patEditDialog.value = false;
  });
};

async function createPAT(insertRequest: PersonalAccessTokenInsertRequest): Promise<boolean> {
  return patApi
    .apiPersonalAccessTokenPost({ personalAccessTokenInsertRequest: insertRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myPatView.myPATCreateDialogSuccessTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserPAT(pageRequest.value);
        curCreatedToken.value = resp.data!;
        return true;
      }
      toast.add({
        severity: "warn",
        summary: t("myPatView.myPATCreateDialogFailedTitle"),
        detail: resp.message,
        life: 3000
      });
      return false;
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: t("myPatView.myPATCreateDialogFailedTitle"),
        detail: error.message,
        life: 3000
      });
      return false;
    });
}

function deletePAT(patId: number) {
  patApi
    .apiPersonalAccessTokenPatIdDelete({ patId: patId })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myPatView.myPATDeleteConfirmDialogSuccessTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserPAT(pageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myPatView.myPATDeleteConfirmDialogFailedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: t("myPatView.myPATDeleteConfirmDialogFailedTitle"),
        detail: error.message,
        life: 3000
      });
    });
  patDeleteDialog.value = false;
}

async function patchPAT(patId: number, patchRequest: PersonalAccessTokenPatchRequest): Promise<void> {
  return patApi
    .apiPersonalAccessTokenPatIdPatch({
      patId: patId,
      personalAccessTokenPatchRequest: patchRequest
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({
          severity: "success",
          summary: t("myPatView.myPATEditDialogSuccessTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserPAT(pageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myPatView.myPATEditDialogFailedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: t("myPatView.myPATEditDialogFailedTitle"),
        detail: error.message,
        life: 3000
      });
    });
}

async function pageUserPAT(pageRequest: PersonalAccessTokenApiApiPersonalAccessTokenPageGetRequest): Promise<void> {
  return patApi
    .apiPersonalAccessTokenPageGet(pageRequest)
    .then((response) => {
      const resp = response.data!;
      if (resp.isSuccessful) {
        const pageResult = resp.data!;
        curPage.value = pageResult.page;
        totalPage.value = pageResult.totalPage;
        totalRecord.value = pageResult.total;
        pageData.value = pageResult.data;
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}

async function fetchSelfRoles(): Promise<void> {
  return roleApi
    .apiRoleSelfGet()
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        roles.value = resp.data!;
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}
</script>

<template>
  <div class="w-full flex flex-col gap-4 bg-gray-100 p-8">
    <div class="flex justify-between items-center bg-white rounded-2xl p-4">
      <div class="ml-auto">
        <button
          class="flex items-center gap-1 border p-1.5 rounded-md shadow-sm text-gray-800 hover:bg-gray-50 text-sm"
          @click="showCreatePATDialog"
        >
          <Icon icon="mdi:key-plus" />
          {{ t("myPatView.myPATCreateButton") }}
        </button>
      </div>
    </div>
    <DataTable
      class="flex-grow overflow-y-auto"
      :value="pageData"
      dataKey="id"
      :row-class="(data) => (data.isExpired ? '!bg-red-100' : '')"
      scrollable
      sortField="createTime"
      :sortOrder="1"
      tableStyle="min-width: 50rem"
    >
      <Column field="id" :header="t('myPatView.myPATTablePATId')"></Column>
      <Column field="name" :header="t('myPatView.myPATTablePATName')"></Column>
      <Column sortable field="createTime" :header="t('myPatView.myPATTableCreateTime')">
        <template #body="{ data }">
          {{ dayjs(String(data.createTime)).format("YYYY/MM/DD HH:mm:ss") }}
        </template>
      </Column>
      <Column sortable field="expireTime" :header="t('myPatView.myPATTableExpireTime')">
        <template #body="{ data }">
          {{ dayjs(String(data.expireTime)).format("YYYY/MM/DD HH:mm:ss") }}
        </template>
      </Column>
      <Column :header="t('myPatView.myPATTableOpsTitle')" class="w-48">
        <template #body="{ data }">
          <div class="flex gap-0.5 text-sm">
            <Button @click="showPATEditDialog(data)" severity="info" size="small">
              {{ t("myPatView.myPATTableOpsEdit") }}
            </Button>
            <Button @click="showPATDeleteDialog(data.id)" severity="danger" size="small">
              {{ t("myPatView.myPATTableOpsDelete") }}
            </Button>
          </div>
        </template>
      </Column>
    </DataTable>
    <!--create-->
    <Dialog v-model:visible="patCreateDialog" modal :header="t('myPatView.myPATCreateDialogTitle')">
      <Form
        v-slot="$createForm"
        :initialValues="createFormInitialValues"
        :resolver="createFormResolver"
        @submit="onCreateFormSubmit"
        class="flex flex-col gap-4 m-4 w-96"
      >
        <div class="flex flex-col gap-2 w-full">
          <div class="flex flex-col gap-1">
            <FloatLabel variant="on">
              <InputText id="newPATName" name="name" pattern="\S+" fluid />
              <label for="newPATName">{{ t("myPatView.myPATCreateDialogPATName") }}</label>
            </FloatLabel>
            <Message v-if="($createForm as any).name?.invalid" severity="error" size="small" variant="simple">
              {{ ($createForm as any).name.error?.message }}
            </Message>
          </div>
          <div class="flex flex-col gap-1">
            <FloatLabel variant="on">
              <InputText id="newPATDesc" name="description" fluid />
              <label for="newPATDesc">{{ t("myPatView.myPATCreateDialogPATDesc") }}</label>
            </FloatLabel>
            <Message v-if="($createForm as any).description?.invalid" severity="error" size="small" variant="simple">
              {{ ($createForm as any).description.error?.message }}
            </Message>
          </div>
          <div class="flex flex-col gap-1">
            <FloatLabel variant="on">
              <DatePicker id="datepicker-24h" name="expireTime" showTime hourFormat="24" fluid />
              <label for="datepicker">{{ t("myPatView.myPATCreateDialogPATExpireTime") }}</label>
            </FloatLabel>
            <Message v-if="($createForm as any).expireTime?.invalid" severity="error" size="small" variant="simple">
              {{ ($createForm as any).expireTime.error?.message }}
            </Message>
          </div>
          <div class="flex flex-col gap-1">
            <Accordion>
              <AccordionPanel v-for="(role, idx) in roles" :value="idx" :key="idx">
                <AccordionHeader>{{ role.name }}</AccordionHeader>
                <AccordionContent>
                  <div v-for="(permission, pIdx) of role.permissions" :key="pIdx" class="flex items-center gap-2">
                    <Checkbox name="permissions" :inputId="'permission-' + pIdx" :value="permission.name" />
                    <label :for="'permission-' + pIdx">{{ permission.name }}</label>
                  </div>
                </AccordionContent>
              </AccordionPanel>
            </Accordion>
            <Message v-if="($createForm as any).permissions?.invalid" severity="error" size="small" variant="simple">
              {{ ($createForm as any).permissions.error?.message }}
            </Message>
          </div>
        </div>
        <div class="flex justify-end gap-2">
          <Button
            type="button"
            :label="t('myPatView.myPATCreateDialogCancelButton')"
            severity="secondary"
            @click="patCreateDialog = false"
          />
          <Button type="submit" :label="t('myPatView.myPATCreateDialogSubmitButton')" :disabled="!$createForm.valid" />
        </div>
      </Form>
    </Dialog>
    <!--display token-->
    <Dialog
      v-model:visible="patCreatedTokenDisplayDialog"
      modal
      :header="t('myPatView.myPATTokenDisplayDialogTitle')"
      :pt="{
        pcCloseButton: {
          root: {
            onClick: (event: Event) => {
              event.preventDefault();
              closeTokenDisplayDialog();
            }
          }
        }
      }"
    >
      <div class="w-96 flex flex-col gap-4 m-4">
        <div class="flex flex-col gap-2">
          <h2 class="font-bold">{{ t("myPatView.myPATTokenDisplayDialogToken") }}</h2>
          <p class="text-sm overflow-auto break-all">{{ curCreatedToken }}</p>
        </div>
        <div class="flex gap-2 self-end">
          <Button
            type="button"
            :label="t('myPatView.myPATTokenDisplayDialogCopyButton')"
            @click="copyAndCloseTokenDisplayDialog"
          />
        </div>
      </div>
    </Dialog>
    <!--edit-->
    <Dialog v-model:visible="patEditDialog" modal :header="t('myPatView.myPATEditDialogTitle')">
      <Form
        v-slot="$editForm"
        :initialValues="editFormInitialValues"
        :resolver="editFormResolver"
        @submit="onEditFormSubmit"
        :validateOnSubmit="true"
        class="flex flex-col gap-4 m-4 w-96"
      >
        <div class="flex flex-col gap-2 w-full">
          <div class="flex flex-col gap-1">
            <IftaLabel variant="on">
              <InputText id="editPATName" name="name" :placeholder="curPAT!.name" fluid />
              <label for="editPATName">{{ t("myPatView.myPATEditDialogPATName") }}</label>
            </IftaLabel>
            <Message v-if="($editForm as any).name?.invalid" severity="error" size="small" variant="simple">
              {{ ($editForm as any).name.error?.message }}
            </Message>
          </div>
          <div class="flex flex-col gap-1">
            <IftaLabel variant="on">
              <InputText id="editPATDesc" name="description" :placeholder="curPAT!.description || ''" fluid />
              <label for="editPATDesc">{{ t("myPatView.myPATEditDialogPATDesc") }}</label>
            </IftaLabel>
            <Message v-if="($editForm as any).description?.invalid" severity="error" size="small" variant="simple">
              {{ ($editForm as any).description.error?.message }}
            </Message>
          </div>
        </div>
        <div class="flex justify-end gap-2">
          <Button
            type="button"
            :label="t('myPatView.myPATEditDialogCancelButton')"
            severity="secondary"
            @click="patEditDialog = false"
          />
          <Button type="submit" :label="t('myPatView.myPATEditDialogSubmitButton')" />
        </div>
      </Form>
    </Dialog>
    <!--delete confirm-->
    <Dialog v-model:visible="patDeleteDialog" modal :header="t('myPatView.myPATDeleteConfirmDialogTitle')">
      <div class="flex flex-col gap-4 mb-4 min-w-64">
        <h2 class="text-lg">{{ t("myPatView.myPATDeleteConfirmDialogWarningTitle") }}</h2>
        <p class="text-sm">{{ t("myPatView.myPATDeleteConfirmDialogWarningContent") }}</p>
      </div>
      <div class="flex justify-end gap-2">
        <Button
          type="button"
          :label="t('myPatView.myPATDeleteConfirmDialogCancelButton')"
          severity="secondary"
          @click="patDeleteDialog = false"
        ></Button>
        <Button
          type="button"
          :label="t('myPatView.myPATDeleteConfirmDialogSubmitButton')"
          severity="danger"
          @click="deletePAT(curPATId)"
        ></Button>
      </div>
    </Dialog>

    <BottomPaginator
      v-model:page="page"
      v-model:page-size="pageSize"
      :row-options="[10, 15, 20, 25, 30]"
      :cur-page="curPage"
      :total-record="totalRecord"
    />
  </div>
</template>

<style scoped></style>
