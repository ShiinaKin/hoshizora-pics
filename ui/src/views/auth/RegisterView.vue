<script setup lang="ts">
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { useCommonStore } from "@/stores/counter";
import { useForm } from "vee-validate";
import * as yup from "yup";
import { AuthApi, type UserInsertRequest } from "api-client";
import VeeInputText from "@/components/vee-input/VeeInputText.vue";
import I18nSelect from "@/components/I18nSelect.vue";
import Button from "primevue/button";

const { t } = useI18n();
const commonStore = useCommonStore();
const router = useRouter();
const toast = useToast();

const authApi = new AuthApi();

const signupFormSchema = yup.object({
  username: yup
    .string()
    .trim()
    .min(4, t("registerView.form.verify.username.min"))
    .max(20, t("registerView.form.verify.username.min"))
    .required(t("registerView.form.verify.username.required")),
  password: yup
    .string()
    .trim()
    .min(8, t("registerView.form.verify.password.min"))
    .max(32, t("registerView.form.verify.password.min"))
    .matches(
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,32}$/,
      t("registerView.form.verify.password.invalid")
    )
    .required(t("registerView.form.verify.password.required")),
  email: yup
    .string()
    .trim()
    .email(t("registerView.form.verify.email.invalid"))
    .required(t("registerView.form.verify.email.required"))
});

const { handleSubmit } = useForm({
  validationSchema: signupFormSchema
});

const onSubmit = handleSubmit((values) => {
  const signupRequest: UserInsertRequest = {
    username: values.username,
    password: values.password,
    email: values.email
  };
  signup(signupRequest);
});

function signup(signupRequest: UserInsertRequest) {
  authApi
    .apiUserSignupPost({ userInsertRequest: signupRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        toast.add({ severity: "success", summary: t("registerView.toast.successTitle"), life: 3000 });
        router.push({ name: "login" });
      } else {
        toast.add({ severity: "warn", summary: t("registerView.toast.failedTitle"), detail: resp.message, life: 3000 });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}
</script>

<template>
  <div class="h-screen w-full flex justify-center items-center bg-gray-300">
    <div class="relative w-1/2 rounded-xl shadow-lg overflow-hidden">
      <img src="@/assets/authBackground.png" alt="authBackground" class="w-full object-cover" />
      <div
        class="absolute top-0 right-0 w-1/2 h-full bg-white bg-opacity-75 backdrop-blur-sm flex flex-col items-center justify-center gap-4"
      >
        <h2 class="text-3xl text-gray-700">{{ commonStore.title }}</h2>

        <h4 class="text-xl text-gray-700">{{ t("registerView.title") }}</h4>
        <form @submit="onSubmit" class="flex flex-col gap-4 w-96">
          <div class="flex flex-col gap-1">
            <VeeInputText
              id="signupFormUsername"
              name="username"
              :label="t('registerView.form.username')"
              start-icon="mdi:user"
            />
          </div>
          <div class="flex flex-col gap-1">
            <VeeInputText
              id="signupFormPassword"
              name="password"
              :label="t('registerView.form.password')"
              type="password"
              start-icon="mdi:password-outline"
            />
          </div>
          <div class="flex flex-col gap-1">
            <VeeInputText
              id="signupFormEmail"
              name="email"
              :label="t('siteInitView.form.email')"
              type="email"
              start-icon="mdi:alternate-email"
            />
          </div>
          <div class="flex flex-col gap-2">
            <Button type="submit" :label="t('registerView.form.signupButton')" fluid />
            <div class="flex justify-center">
              <a
                v-if="commonStore.allowSignup"
                class="text-gray-500 hover:underline hover:text-gray-700 hover:cursor-pointer"
                @click="router.push({ name: 'login' })"
              >
                {{ t("loginView.form.loginButton") }}
              </a>
            </div>
          </div>
        </form>
        <div class="absolute right-0 bottom-0 rounded-br-xl locale-changer">
          <I18nSelect />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
