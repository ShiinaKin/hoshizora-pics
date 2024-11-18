<script setup lang="ts">
import { ref } from "vue";
import { useUserFieldStore } from "@/stores/counter";
import { useI18n } from "vue-i18n";
import {
  Configuration,
  UserApi,
  type UserVO,
} from "api-client";
import { useToast } from "primevue/usetoast";
import { useField, useForm } from "vee-validate";
import { toTypedSchema } from "@vee-validate/yup";
import * as yup from "yup";
import ToggleSwitch from "primevue/toggleswitch";
import Divider from "primevue/divider";
import Button from "primevue/button";
import { Icon } from "@iconify/vue";
import Dialog from "primevue/dialog";
import IftaLabel from "primevue/iftalabel";
import InputText from "primevue/inputtext";
import dayjs from "dayjs";

const userFieldStore = useUserFieldStore();
const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({ baseOptions: { headers: { Authorization: `Bearer ${token}` } } });
const userApi = new UserApi(configuration);

const userVO = ref<UserVO>();

const passwordRegExp = RegExp("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,32}$");
const userEditValidationSchema = toTypedSchema(
  yup
    .object({
      patchEmail: yup.string().trim()
        .email(t("myProfileView.profileEditFormEmailValidationFailedMessage"))
        .nullable(),
      patchPassword: yup
        .string()
        .trim()
        .test("password", t("myProfileView.profileEditFormPasswordValidationFailedMessage"), (value) => {
          return (value === undefined || value === "" || passwordRegExp.test(value));
        }),
      patchIsDefaultImagePrivate: yup.boolean()
    })
    .test("at-least-one-field", t("myProfileView.profileEditFormValidationFailedMessage"), (values) => {
      return (
        values.patchEmail !== undefined && values.patchEmail !== "" ||
        values.patchPassword !== undefined && passwordRegExp.test(values.patchPassword) ||
        values.patchIsDefaultImagePrivate !== undefined
      );
    })
);

const { handleSubmit, meta, errors, validate } = useForm({
  validationSchema: userEditValidationSchema,
  validateOnMount: false
});

const { value: patchEmail, handleBlur: emailBlur } = useField<string | undefined>("patchEmail");
const { value: patchPassword, handleBlur: passwordBlur } = useField<string | undefined>("patchPassword");
const { value: patchIsDefaultImagePrivate } = useField<boolean | undefined>("patchIsDefaultImagePrivate");

const handleBlur = (field: "email" | "password") => {
  if (field === "email") {
    emailBlur();
  } else {
    passwordBlur();
  }
  validate();
};

const profileEditDialog = ref(false);

function showProfileEditDialog() {
  patchEmail.value = undefined;
  patchPassword.value = undefined;
  patchIsDefaultImagePrivate.value = undefined;
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
        email: values.patchEmail === "" ? undefined : values.patchEmail,
        password: values.patchPassword === "" ? undefined : values.patchPassword,
        isDefaultImagePrivate: values.patchIsDefaultImagePrivate
      }
    })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        fetchUser();
        toast.add({ severity: "success", summary: "Success", detail: resp.message, life: 3000 });
      } else {
        toast.add({ severity: "error", summary: "Error", detail: resp.message, life: 3000 });
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
    <div class="flex justify-between items-center bg-white rounded-2xl p-4">
      <h2>{{ t("myProfileView.profileTitle") }}</h2>

      <div class="ml-auto">
        <button
          class="flex items-center gap-1 border p-1.5 rounded-md shadow-sm text-gray-800 hover:bg-gray-50 text-sm"
          @click="showProfileEditDialog"
        >
          <Icon icon="mdi:account-edit-outline" />
          {{ t("myProfileView.profileUpdateButton") }}
        </button>
      </div>
    </div>

    <div class="w-full p-4 flex-grow bg-white rounded-2xl overflow-y-auto">
      <div class="flow-root">
        <dl class="-my-3 divide-y divide-gray-100 text-sm">
          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            {{ t("myProfileView.profileUserId") }}
            <dd class="text-gray-700 sm:col-span-2">{{ userVO?.id }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileUsername") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ userVO?.username }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileEmail") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.email }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileCreateTime") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ dayjs(String(userVO?.createTime)).format("YYYY/MM/DD HH:mm:ss") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileBanStatus") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.isBanned ? t("myProfileView.profileBanStatusBanned") : t("myProfileView.profileBanStatusNormal") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileUserGroupName") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">{{ userVO?.groupName }}</dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileImageCount") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.imageCount }} {{ t("myProfileView.profileImageCountUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileAlbumCount") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.albumCount }} {{ t("myProfileView.profileAlbumCountUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileUsedSpace") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.totalImageSize.toFixed(2) }}
              {{ t("myProfileView.profileSpaceSizeUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileUsableSpace") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{ userVO?.allSize.toFixed(2) }}
              {{ t("myProfileView.profileSpaceSizeUnit") }}
            </dd>
          </div>

          <div class="grid grid-cols-1 gap-1 py-3 sm:grid-cols-3 sm:gap-4">
            <dt class="font-medium text-gray-900">{{ t("myProfileView.profileIsDefaultImagePrivate") }}</dt>
            <dd class="text-gray-700 sm:col-span-2">
              {{
                userVO?.isDefaultImagePrivate
                  ? t("myProfileView.profileIsDefaultImagePrivateTrue")
                  : t("myProfileView.profileIsDefaultImagePrivateFalse")
              }}
            </dd>
          </div>
        </dl>
      </div>
      <Divider />
    </div>

    <Dialog v-model:visible="profileEditDialog" modal :header="t('myProfileView.profileEditDialogTitle')">
      <form @submit="editUser">
        <div class="flex flex-col gap-4 m-4 w-96">
          <div class="flex flex-col gap-4 w-full">
            <IftaLabel variant="on">
              <InputText
                id="editUserEmail"
                v-model="patchEmail"
                :placeholder="userVO!.email!"
                class="w-full"
                @blur="handleBlur('email')"
              />
              <label for="editUserEmail">{{ t("myProfileView.profileEditFormEmail") }}</label>
              <p class="text-red-500 text-sm">{{ errors.patchEmail }}</p>
            </IftaLabel>

            <IftaLabel variant="on">
              <InputText
                id="editUserPassword"
                v-model="patchPassword"
                :placeholder="t('myProfileView.profileEditFormPasswordPlaceholder')"
                class="w-full"
                type="password"
                @blur="handleBlur('password')"
              />
              <label for="editUserPassword">{{ t("myProfileView.profileEditFormPasswordPlaceholder") }}</label>
              <p class="text-red-500 text-sm">{{ errors.patchPassword }}</p>
            </IftaLabel>

            <div>
              <div class="w-full flex items-center px-2 gap-4">
                <label for="editDefaultImageVisible" class="text-sm"
                  >{{ t("myProfileView.profileEditFormIsDefaultImagePrivate") }}:
                </label>
                <ToggleSwitch
                  id="editDefaultImageVisible"
                  v-model="patchIsDefaultImagePrivate"
                  :default-value="userVO?.isDefaultImagePrivate"
                />
              </div>
              <p class="text-red-500 text-sm">{{ errors.patchIsDefaultImagePrivate }}</p>
            </div>
          </div>
          <div class="flex justify-end gap-2">
            <Button
              type="button"
              :label="t('myProfileView.profileEditFormCancelButton')"
              severity="secondary"
              @click="profileEditDialog = false"
            />
            <Button type="submit" :label="t('myProfileView.profileEditFormSubmitButton')" :disabled="!meta.valid" />
          </div>
        </div>
      </form>
    </Dialog>
  </div>
</template>

<style scoped></style>
