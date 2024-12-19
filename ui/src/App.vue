<script setup lang="ts">
import { inject, onMounted } from "vue";
import { RouterView, useRoute, useRouter } from "vue-router";
import { useCommonStore } from "@/stores/counter";
import { CommonApi } from "api-client";
import { useToast } from "primevue/usetoast";
import Toast from "primevue/toast";
import type { I18n } from "vue-i18n";

const route = useRoute();
const router = useRouter();
const commonStore = useCommonStore();
const commonApi = new CommonApi();

const i18n = inject<I18n>("i18n")!;
const toast = useToast();

const token = localStorage.getItem("token");
const local = localStorage.getItem("locale");

onMounted(() => {
  if (local) i18n.global.locale = local;
  else localStorage.setItem("locale", "zh_cn");

  commonApi
    .apiSiteSettingGet()
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        const commonSiteSetting = resp.data!;
        commonStore.setTitle(commonSiteSetting.siteTitle!);
        commonStore.setSubTitle(commonSiteSetting.siteSubTitle!);
        commonStore.setSiteDescription(commonSiteSetting.siteDescription!);
        commonStore.setAllowSignup(commonSiteSetting.siteAllowSignup!);

        document.title = commonStore.title + " - " + commonStore.subTitle;
        const metaDescription = document.querySelector('meta[name="description"]');
        if (metaDescription) metaDescription.setAttribute("content", commonStore.subTitle);

        const arr = ["login", "register", "siteInit", "home"];

        if (!commonSiteSetting.isSiteInit!) router.push({ name: "siteInit" });
        else if (!token) router.push({ name: "login" });
        else if (arr.includes(route.name as string)) router.push({ name: "userOverview" });
      } else {
        toast.add({ severity: "warn", summary: "Warn", detail: resp.message, life: 3000 });
      }
    })
    .catch((error) => {
      toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
      console.error(error);
    });
});
</script>

<template>
  <Toast />
  <RouterView />
</template>

<style scoped></style>
