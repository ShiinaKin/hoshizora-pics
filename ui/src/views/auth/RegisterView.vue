<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { useCommonStore } from "@/stores/counter";
import { Icon } from "@iconify/vue";
import { AuthApi, type UserInsertRequest } from "api-client";

const { t } = useI18n();
const commonStore = useCommonStore();
const router = useRouter();

const authApi = new AuthApi();

const userRegisterForm = ref<UserInsertRequest>({
  username: "",
  password: "",
  email: ""
});

const showErrorAlert = ref(false);
const errorMessage = ref("");

function handleSubmit() {
  const userRegisterRequest = userRegisterForm.value;
  authApi
    .apiUserSignupPost({ userInsertRequest: userRegisterRequest })
    .then((resp) => {
      if (resp.isSuccessful) {
        router.push({ name: "login" });
      } else {
        errorMessage.value = resp.message || "An error occurred";
        showErrorAlert.value = true;
        setTimeout(() => {
          showErrorAlert.value = false;
        }, 5000);
      }
    })
    .catch((error) => {
      console.error(error);
      errorMessage.value = error.message || "An error occurred";
      showErrorAlert.value = true;
      setTimeout(() => {
        showErrorAlert.value = false;
      }, 5000);
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
        <p class="text-center text-lg font-medium">{{ t("message.registerTitle") }}</p>

        <div>
          <label for="username" class="sr-only">Username</label>

          <div class="relative">
            <input
              id="username"
              type="text"
              class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
              :placeholder="t('message.registerUsernamePlaceholder')"
              v-model="userRegisterForm.username"
              pattern="^[a-zA-Z0-9]{4,20}$"
              :title="t('message.registerUsernameRequirements')"
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
              :placeholder="t('message.registerPasswordPlaceholder')"
              v-model="userRegisterForm.password"
              pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,32}$"
              :title="t('message.registerPasswordRequirements')"
              required
            />

            <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
            <Icon icon="mdi:password-outline" class="text-gray-400" />
          </span>
          </div>
        </div>

        <div>
          <label for="email" class="sr-only">Email</label>

          <div class="relative">
            <input
              id="email"
              type="email"
              class="w-full rounded-lg border-gray-200 p-4 pe-12 text-sm shadow-sm"
              :placeholder="t('message.registerEmailPlaceholder')"
              v-model="userRegisterForm.email"
              :title="t('message.registerEmailRequirements')"
              required
            />

            <span class="absolute inset-y-0 end-0 grid place-content-center px-4">
            <Icon icon="mdi:alternate-email" class="text-gray-400" />
          </span>
          </div>
        </div>

        <button type="submit" class="block w-full rounded-lg bg-indigo-600 px-5 py-3 text-sm font-medium text-white">
          {{ t("message.registerSubmitButton") }}
        </button>

        <p class="text-center text-sm text-gray-500">
          <a class="underline" href="#" @click="router.push({name: 'login'})">{{ t("message.registerLoginButton") }}</a>
        </p>
      </form>
    </div>
  </div>
  <div
    v-if="showErrorAlert"
    role="alert"
    class="fixed top-4 right-4 z-50 rounded border-s-4 border-red-500 bg-red-50 p-4"
  >
    <div class="flex items-center gap-2 text-red-800">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-5">
        <path
          fill-rule="evenodd"
          d="M9.401 3.003c1.155-2 4.043-2 5.197 0l7.355 12.748c1.154 2-.29 4.5-2.599 4.5H4.645c-2.309 0-3.752-2.5-2.598-4.5L9.4 3.003zM12 8.25a.75.75 0 01.75.75v3.75a.75.75 0 01-1.5 0V9a.75.75 0 01.75-.75zm0 8.25a.75.75 0 100-1.5.75.75 0 000 1.5z"
          clip-rule="evenodd"
        />
      </svg>

      <strong class="block font-medium"> Something went wrong </strong>
    </div>

    <p class="mt-2 text-sm text-red-700">
      {{ errorMessage }}
    </p>
  </div>
</template>

<style scoped></style>
