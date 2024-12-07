<script setup lang="ts">
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { useCommonStore } from "@/stores/counter";
import { useForm } from "vee-validate";
import * as yup from "yup";
import { AuthApi, type UserLoginRequest } from "api-client";
import VeeInputText from "@/components/vee-input/VeeInputText.vue";
import Button from "primevue/button";

const { t } = useI18n();
const commonStore = useCommonStore();
const router = useRouter();
const toast = useToast();

const authApi = new AuthApi();

const loginFormSchema = yup.object({
  username: yup
    .string()
    .trim()
    .min(4, t("loginView.form.verify.username.min"))
    .max(20, t("loginView.form.verify.username.min"))
    .required(t("loginView.form.verify.username.required")),
  password: yup
    .string()
    .trim()
    .min(8, t("loginView.form.verify.password.min"))
    .max(32, t("loginView.form.verify.password.min"))
    .matches(
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,32}$/,
      t("loginView.form.verify.password.invalid")
    )
    .required(t("loginView.form.verify.password.required"))
});

const { handleSubmit } = useForm({
  validationSchema: loginFormSchema
});

const onSubmit = handleSubmit((values) => {
  const loginRequest: UserLoginRequest = {
    username: values.username,
    password: values.password
  };
  login(loginRequest);
});

function login(loginRequest: UserLoginRequest) {
  authApi
    .apiUserLoginPost({ userLoginRequest: loginRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        const token = resp.data!.token;
        localStorage.setItem("token", token!);
        toast.add({ severity: "success", summary: t("loginView.toast.successTitle"), life: 3000 });
        router.push({ name: "userField" });
      } else {
        toast.add({ severity: "warn", summary: t("loginView.toast.failedTitle"), detail: resp.message, life: 3000 });
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

        <h4 class="text-xl text-gray-700">{{ t("loginView.title") }}</h4>
        <form @submit="onSubmit" class="flex flex-col gap-4 w-96">
          <div class="flex flex-col gap-1">
            <VeeInputText
              id="loginFormUsername"
              name="username"
              :label="t('loginView.form.username')"
              start-icon="mdi:user"
            />
          </div>
          <div class="flex flex-col gap-1">
            <VeeInputText
              id="loginFormPassword"
              name="password"
              :label="t('loginView.form.password')"
              type="password"
              start-icon="mdi:password-outline"
            />
          </div>
          <div class="flex flex-col gap-2">
            <Button type="submit" :label="t('loginView.form.loginButton')" fluid />
            <div class="flex justify-center">
              <a
                v-if="commonStore.allowSignup"
                class="text-gray-500 hover:underline hover:text-gray-700 hover:cursor-pointer"
                @click="router.push({ name: 'register' })"
              >
                {{ t("loginView.form.signupButton") }}
              </a>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
