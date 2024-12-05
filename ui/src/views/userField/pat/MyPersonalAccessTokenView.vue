<script setup lang="ts">
import { computed, inject, ref, watchEffect } from "vue";
import { useI18n } from "vue-i18n";
import {
  Configuration,
  PersonalAccessTokenApi,
  RoleApi,
  type PersonalAccessTokenApiApiPersonalAccessTokenPageGetRequest,
  type PersonalAccessTokenInsertRequest,
  type PersonalAccessTokenPageVO,
  type PersonalAccessTokenPatchRequest,
  type RoleVO
} from "api-client";
import Toolbar from "primevue/toolbar";
import Button from "primevue/button";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Dialog from "primevue/dialog";
import PersonalAccessTokenCreateForm from "@/components/form/userField/pat/PersonalAccessTokenCreateForm.vue";
import PersonalAccessTokenEditForm from "@/components/form/userField/pat/PersonalAccessTokenEditForm.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import BottomPaginator from "@/components/BottomPaginator.vue";
import { useToast } from "primevue/usetoast";
import { Icon } from "@iconify/vue";
import { formatUTCStringToLocale } from "@/utils/DateTimeUtils";
import type { Dayjs } from "dayjs";

const { t } = useI18n();
const toast = useToast();
const dayjs = inject("dayjs") as (date?: string | number | Date) => Dayjs;

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

const patCreateDialog = ref(false);
const patCreatedTokenDisplayDialog = ref(false);
const patEditDialog = ref(false);
const patDeleteDialog = ref(false);

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
      summary: t("myPatView.createdDetail.toast.copySuccessTitle"),
      detail: t("myPatView.createdDetail.toast.copySuccessContent"),
      life: 3000
    });
    closeTokenDisplayDialog();
  });
}

const onCreateFormSubmit = (values: any) => {
  const insertRequest: PersonalAccessTokenInsertRequest = {
    name: values.name!,
    description: values.description || undefined,
    expireTime: dayjs(values.expireTime!).utc().format("YYYY-MM-DDTHH:mm:ss.SSS"),
    permissions: values.permissions || []
  };
  createPAT(insertRequest).then((isSuccessful: boolean) => {
    patCreateDialog.value = false;
    if (isSuccessful) patCreatedTokenDisplayDialog.value = true;
  });
};

const onEditFormSubmit = (values: any) => {
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
          summary: t("myPatView.create.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserPAT(pageRequest.value);
        curCreatedToken.value = resp.data!;
        return true;
      }
      toast.add({
        severity: "warn",
        summary: t("myPatView.create.toast.failedTitle"),
        detail: resp.message,
        life: 3000
      });
      return false;
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: "Error",
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
          summary: t("myPatView.delete.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserPAT(pageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myPatView.delete.toast.failedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({
        severity: "error",
        summary: "Error",
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
          summary: t("myPatView.edit.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
        pageUserPAT(pageRequest.value);
      } else {
        toast.add({
          severity: "warn",
          summary: t("myPatView.edit.toast.failedTitle"),
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
    <Toolbar class="rounded-2xl border-none text-sm">
      <template #start>
        <button
          class="flex items-center gap-1 border p-1.5 rounded-md shadow-sm text-gray-800 hover:bg-gray-50 text-sm"
          @click="showCreatePATDialog"
        >
          <Icon icon="mdi:key-plus" />
          {{ t("myPatView.toolbar.createButton") }}
        </button>
      </template>
    </Toolbar>
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
      <Column field="id" :header="t('myPatView.table.patId')"></Column>
      <Column field="name" :header="t('myPatView.table.patName')"></Column>
      <Column field="createTime" :header="t('myPatView.table.createTime')" :sortable="true">
        <template #body="{ data }">
          {{ formatUTCStringToLocale(data.createTime) }}
        </template>
      </Column>
      <Column field="expireTime" :header="t('myPatView.table.expireTime')" :sortable="true">
        <template #body="{ data }">
          {{ formatUTCStringToLocale(data.expireTime) }}
        </template>
      </Column>
      <Column :header="t('myPatView.table.ops.title')" class="w-48">
        <template #body="{ data }">
          <div class="flex gap-0.5 text-sm">
            <Button @click="showPATEditDialog(data)" severity="info" size="small">
              {{ t("myPatView.table.ops.edit") }}
            </Button>
            <Button @click="showPATDeleteDialog(data.id)" severity="danger" size="small">
              {{ t("myPatView.table.ops.delete") }}
            </Button>
          </div>
        </template>
      </Column>
    </DataTable>
    <!--create-->
    <Dialog v-model:visible="patCreateDialog" modal :header="t('myPatView.create.dialog.title')">
      <PersonalAccessTokenCreateForm :roles="roles" @cancel="patCreateDialog = false" @submit="onCreateFormSubmit" />
    </Dialog>
    <!--display created token-->
    <Dialog
      v-model:visible="patCreatedTokenDisplayDialog"
      modal
      :header="t('myPatView.createdDetail.dialog.title')"
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
          <h2 class="font-bold">{{ t("myPatView.createdDetail.dialog.token") }}</h2>
          <p class="text-sm overflow-auto break-all">{{ curCreatedToken }}</p>
        </div>
        <div class="flex gap-2 self-end">
          <Button
            type="button"
            :label="t('myPatView.createdDetail.dialog.copyButton')"
            @click="copyAndCloseTokenDisplayDialog"
          />
        </div>
      </div>
    </Dialog>
    <!--edit-->
    <Dialog v-model:visible="patEditDialog" modal :header="t('myPatView.edit.dialog.title')">
      <PersonalAccessTokenEditForm :pat="curPAT" @cancel="patEditDialog = false" @submit="onEditFormSubmit" />
    </Dialog>
    <!--delete confirm-->
    <ConfirmDialog
      v-model:visible="patDeleteDialog"
      :header="t('myPatView.delete.dialog.title')"
      :main-content="t('myPatView.delete.dialog.warningTitle')"
      :sub-content="t('myPatView.delete.dialog.warningContent')"
      :cancel-btn-msg="t('myPatView.delete.dialog.cancelButton')"
      :submit-btn-msg="t('myPatView.delete.dialog.submitButton')"
      @cancel="patDeleteDialog = false"
      @submit="deletePAT(curPATId)"
    />

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
