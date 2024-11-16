<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { useCommonStore } from "@/stores/counter";
import { AuthApi, type UserInsertRequest } from "api-client";
import { Icon } from "@iconify/vue";
import { useToast } from "primevue/usetoast";

const { t } = useI18n();
const commonStore = useCommonStore();
const router = useRouter();
const toast = useToast();

const authApi = new AuthApi();

const userRegisterForm = ref<UserInsertRequest>({
  username: "",
  password: "",
  email: ""
});

function handleSubmit() {
  const userRegisterRequest = userRegisterForm.value;
  authApi
    .apiUserSignupPost({ userInsertRequest: userRegisterRequest })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        router.push({ name: "login" });
      } else {
        toast.add({ severity: "warn", summary: "Register Failed", detail: resp.message, life: 3000 });
      }
    })
    .catch((error) => {
      console.error(error);
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
    });
}
</script>

<template>
  <div class="h-screen w-full flex justify-center items-center">
    <div class="mx-auto min-w-96">
      <h1 class="text-center text-2xl font-bold text-indigo-600 sm:text-3xl">{{ commonStore.title }}</h1>

      <form
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
              autocomplete="username"
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
              autocomplete="new-password"
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
          <a class="underline" href="#" @click="router.push({ name: 'login' })">{{
            t("message.registerLoginButton")
          }}</a>
        </p>
      </form>
    </div>
  </div>
</template>

<style scoped></style>
