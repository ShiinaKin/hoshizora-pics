<script setup lang="ts">
import { RouterView, useRoute, useRouter } from "vue-router";
import { useCommonStore } from "@/stores/counter";
import { CommonApi } from "api-client";

const route = useRoute();
const router = useRouter();
const commonStore = useCommonStore();
const commonApi = new CommonApi();

const token = localStorage.getItem("token");

commonApi
  .apiSiteSettingGet()
  .then((response) => {
    const resp = response.data;
    if (resp.isSuccessful) {
      const commonSiteSetting = resp.data!!;
      commonStore.setTitle(commonSiteSetting.siteTitle!);
      commonStore.setSubTitle(commonSiteSetting.siteSubTitle!);
      commonStore.setAllowSignup(commonSiteSetting.siteAllowSignup!);

      if (!commonSiteSetting.isSiteInit!!) router.push({ name: "siteInit" });
      if (!token) router.push({ name: "login" });
      if (route.name === "siteInit") router.push({ name: "overview" });
    }
  })
  .catch((error) => {
    console.error(error);
  });
</script>

<template>
  <RouterView />
</template>

<style scoped></style>
