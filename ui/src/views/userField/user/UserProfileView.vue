<script setup lang="ts">
import { ref } from "vue";
import { useUserFieldStore } from "@/stores/counter";
import { useI18n } from "vue-i18n";
import { Configuration, UserApi, type UserVO } from "api-client";
import { useToast } from "primevue/usetoast";
import { useForm } from "vee-validate";
import * as yup from "yup";
import VeeIftaInputText from "@/components/vee-input/VeeIftaInputText.vue";
import VeeToggleSwitch from "@/components/vee-input/VeeToggleSwitch.vue";
import Divider from "primevue/divider";
import Button from "primevue/button";
import { Icon } from "@iconify/vue";
import Dialog from "primevue/dialog";
import Toolbar from "primevue/toolbar";
import { formatUTCStringToLocale } from "@/utils/DateTimeUtils";

const userFieldStore = useUserFieldStore();
const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const userApi = new UserApi(configuration);

const userVO = ref<UserVO>();

const userEditValidationSchema = yup.object({
  email: yup
    .string()
    .trim()
    .email(t("myProfileView.edit.dialog.form.verify.email.invalid"))
    .test("at-least-one-field", t("myProfileView.edit.dialog.form.verify.atLeastOneField"), function () {
      return !!(
        this.parent.email ||
        this.parent.password ||
        (this.parent.isDefaultImagePrivate !== undefined && this.parent.isDefaultImagePrivate !== userVO.value?.isDefaultImagePrivate)
      );
    }),
  password: yup
    .string()
    .trim()
    .min(8, t("myProfileView.edit.dialog.form.verify.password.min"))
    .max(32, t("myProfileView.edit.dialog.form.verify.password.max"))
    .matches(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]+$/, t("myProfileView.edit.dialog.form.verify.password.invalid"))
    .test("at-least-one-field", t("myProfileView.edit.dialog.form.verify.atLeastOneField"), function () {
      return !!(
        this.parent.email ||
        this.parent.password ||
        (this.parent.isDefaultImagePrivate !== undefined && this.parent.isDefaultImagePrivate !== userVO.value?.isDefaultImagePrivate)
      );
    }),
  isDefaultImagePrivate: yup
    .boolean()
    .test("at-least-one-field", t("myProfileView.edit.dialog.form.verify.atLeastOneField"), function () {
      return !!(
        this.parent.email ||
        this.parent.password ||
        (this.parent.isDefaultImagePrivate !== undefined && this.parent.isDefaultImagePrivate !== userVO.value?.isDefaultImagePrivate)
      );
    })
});
const { handleSubmit } = useForm({
  validationSchema: userEditValidationSchema
});

const profileEditDialog = ref(false);

function showProfileEditDialog() {
  profileEditDialog.value = true;
}

fetchUser();

function fetchUser() {
  userApi
    .apiUserSelfGet()
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        userVO.value = resp.data!;
        userFieldStore.setEmail(userVO.value.email!);
      } else {
        toast.add({
          severity: "success",
          summary: t("myProfileView.detail.toast.failedTitle"),
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

const editUser = handleSubmit((values) => {
  profileEditDialog.value = false;
  userApi
    .apiUserSelfPatch({
      userSelfPatchRequest: {
        email: values.email === "" ? undefined : values.email,
        password: values.password === "" ? undefined : values.password,
        isDefaultImagePrivate: values.isDefaultImagePrivate
      }
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        fetchUser();
        toast.add({
          severity: "success",
          summary: t("myProfileView.edit.toast.successTitle"),
          detail: resp.message,
          life: 3000
        });
      } else {
        toast.add({
          severity: "error",
          summary: t("myProfileView.edit.toast.failedTitle"),
          detail: resp.message,
          life: 3000
        });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
});
</script>

<template>
  <div class="w-full flex flex-col gap-4 bg-gray-100 p-8">
    <Toolbar class="rounded-2xl border-none text-sm px-6">
      <template #start>
        <h2 class="text-lg">{{ t("myProfileView.toolbar.title") }}</h2>
      </template>
      <template #end>
        <button
          class="flex items-center gap-1 border p-1.5 rounded-md shadow-sm text-gray-800 hover:bg-gray-50 text-sm"
          @click="showProfileEditDialog"
        >
          <Icon icon="mdi:account-edit-outline" />
          {{ t("myProfileView.toolbar.updateButton") }}
        </button>
      </template>
    </Toolbar>

    <div class="w-full p-4 flex-grow bg-white rounded-2xl overflow-y-auto">
      <div class="flow-root">
        <dl class="-my-3 divide-y divide-gray-100 text-sm">
          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            {{ t("myProfileView.detail.userId") }}
            <dd class="text-gray-700 sm:col-span-2">{{ userVO?.id }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.username") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ userVO?.username }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.email") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.email }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.createTime") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ formatUTCStringToLocale(userVO?.createTime) }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.banStatus.title") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                userVO?.isBanned
                  ? t("myProfileView.detail.banStatus.banned")
                  : t("myProfileView.detail.banStatus.normal")
              }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.groupName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ userVO?.groupName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.imageCount") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.imageCount }} {{ t("myProfileView.detail.imageCountUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.albumCount") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.albumCount }} {{ t("myProfileView.detail.albumCountUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.usedSpace") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.totalImageSize.toFixed(2) }}
              {{ t("myProfileView.detail.spaceSizeUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.usableSpace") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.allSize.toFixed(2) }}
              {{ t("myProfileView.detail.spaceSizeUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.detail.isDefaultImagePrivate.title") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                userVO?.isDefaultImagePrivate
                  ? t("myProfileView.detail.isDefaultImagePrivate.true")
                  : t("myProfileView.detail.isDefaultImagePrivate.false")
              }}
            </dd>
          </div>
        </dl>
      </div>
      <Divider />
    </div>

    <Dialog v-model:visible="profileEditDialog" modal :header="t('myProfileView.edit.dialog.title')">
      <form @submit="editUser">
        <div class="flex flex-col gap-4 m-4 w-96">
          <div class="flex flex-col gap-4 w-full">
            <div class="flex flex-col gap-1">
              <VeeIftaInputText
                id="editUserEmail"
                name="email"
                :placeholder="userVO?.email!"
                :label="t('myProfileView.edit.dialog.form.email')"
              />
            </div>
            <div class="flex flex-col gap-1">
              <VeeIftaInputText
                id="editUserPassword"
                name="password"
                type="password"
                :label="t('myProfileView.edit.dialog.form.password')"
              />
            </div>
            <div class="flex flex-col gap-1">
              <VeeToggleSwitch
                id="editDefaultImageVisible"
                name="isDefaultImagePrivate"
                :default-value="userVO?.isDefaultImagePrivate!"
                :label="t('myProfileView.edit.dialog.form.isDefaultImagePrivate')"
              />
            </div>
          </div>
          <div class="flex justify-end gap-2">
            <Button
              type="button"
              :label="t('myProfileView.edit.dialog.form.cancelButton')"
              severity="secondary"
              @click="profileEditDialog = false"
            />
            <Button type="submit" :label="t('myProfileView.edit.dialog.form.submitButton')" />
          </div>
        </div>
      </form>
    </Dialog>
  </div>
</template>

<style scoped></style>
