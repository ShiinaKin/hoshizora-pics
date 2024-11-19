/* eslint-disable camelcase */

import "@/assets/base.css";

import { createApp } from "vue";
import { createPinia } from "pinia";
import { createI18n } from "vue-i18n";

import App from "./App.vue";
import router from "./router";

import PrimeVue from "primevue/config";
import ToastService from "primevue/toastservice";

import "viewerjs/dist/viewer.css";
import VueViewer from "v-viewer";

import zh_cn from "./locales/zh_cn.yaml";
import en_us from "./locales/en_us.yaml";

import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";

const i18n = createI18n({
  locale: "zh_cn",
  fallbackLocale: "en_us",
  messages: {
    zh_cn,
    en_us
  }
});

dayjs.extend(utc);

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(i18n);
app.use(PrimeVue, {
  theme: "none"
});
app.use(VueViewer);
app.use(ToastService);

app.provide("dayjs", dayjs);

app.mount("#app");
