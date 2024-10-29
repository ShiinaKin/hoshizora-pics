<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { useCommonStore } from "@/stores/counter";
import { Icon } from "@iconify/vue";
import { AuthApi, type UserLoginRequest } from "api-client";
import ErrorMessage from "@/components/ErrorMessage.vue";

const { t } = useI18n();
const commonStore = useCommonStore();
const router = useRouter();

const authApi = new AuthApi();

const userLoginForm = ref<UserLoginRequest>({
  password: "",
  username: ""
});

const errorAlertRef = ref();
const errorMessage = ref("");

function handleSubmit() {
  const userLoginRequest = userLoginForm.value;
  console.log(userLoginRequest);
  authApi
    .apiUserLoginPost({ userLoginRequest })
    .then((resp) => {
      if (resp.isSuccessful) {
        const token = resp.data!!.token;
        localStorage.setItem("token", token!!);
        router.push({ name: "userField" });
      } else {
        errorMessage.value = resp.message || "An error occurred";
        errorAlertRef.value.showError();
      }
    })
    .catch((error) => {
      console.error(error);
      errorMessage.value = error.message || "An error occurred";
      errorAlertRef.value.showError();
    });
}
</script>

<template>
  <div class="mx-auto max-w-screen-xl px-4 py-16 sm:px-6 lg:px-8">
    <div class="mx-auto max-w-lg">
      <h1 class="text-center text-2xl font-bold text-indigo-600 sm:text-3xl">{{ commonStore.title }}</h1>

      <form
        action="#"
        @submit.prevent="handleSubmit"
        class="mb-0 mt-6 space-y-4 rounded-lg p-4 shadow-lg sm:p-6 lg:p-8"
      >
        <p class="text-center text-lg font-medium">{{ t("message.loginTitle") }}</p>

        <div>
          <label for="username" class="sr-only">Username</label>

          <div class="relative">
            <input
              id="username"
              type="text"
              class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
              :placeholder="t('message.loginUsernamePlaceholder')"
              v-model="userLoginForm.username"
              pattern="^[a-zA-Z0-9]{4,20}$"
              :title="t('message.loginUsernameRequirements')"
              required
            />

            <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
              <Icon icon="mdi:user" class="text-gray-400" />
            </span>
          </div>
        </div>

        <div>
          <label for="password" class="sr-only">Password</label>

          <div class="relative">
            <input
              id="password"
              type="password"
              class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
              :placeholder="t('message.loginPasswordPlaceholder')"
              v-model="userLoginForm.password"
              pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,32}$"
              :title="t('message.loginPasswordRequirements')"
              required
            />

            <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
              <Icon icon="mdi:password-outline" class="text-gray-400" />
            </span>
          </div>
        </div>

        <button type="submit" class="block w-full rounded-lg bg-indigo-600 px-5 py-3 text-sm font-medium text-white">
          {{ t("message.loginSubmitButton") }}
        </button>

        <p v-if="commonStore.allowSignup" class="text-center text-sm text-gray-500">
          <a class="underline" href="#" @click="router.push({ name: 'register' })">{{
            t("message.loginSignupButton")
          }}</a>
        </p>
      </form>
    </div>
    <ErrorMessage ref="errorAlertRef" :error-message="errorMessage" />
  </div>
</template>

<style scoped></style>
