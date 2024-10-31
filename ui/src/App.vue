<script setup lang="ts">
import { onMounted } from "vue";
import { RouterView, useRoute, useRouter } from "vue-router";
import { useCommonStore } from "@/stores/counter";
import { CommonApi } from "api-client";

const route = useRoute();
const router = useRouter();
const commonStore = useCommonStore();
const commonApi = new CommonApi();

const token = localStorage.getItem("token");

onMounted(() => {
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
        if (metaDescription) {
          metaDescription.setAttribute("content", commonStore.subTitle);
        }

        if (!commonSiteSetting.isSiteInit!!) router.push({ name: "siteInit" });
        if (!token) router.push({ name: "login" });
        if (route.name === "siteInit") router.push({ name: "overview" });
      }
    })
    .catch((error) => {
      console.error(error);
    });
});
</script>

<template>
  <RouterView />
</template>

<style scoped></style>
