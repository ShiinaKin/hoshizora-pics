/* eslint-disable camelcase */

import "@/assets/base.css";

import { createApp } from "vue";
import { createPinia } from "pinia";
import { createI18n } from "vue-i18n";

import App from "./App.vue";
import router from "./router";

import PrimeVue from "primevue/config";
import ToastService from 'primevue/toastservice';

import zh_cn from "./locales/zh_cn.yaml";
import en_us from "./locales/en_us.yaml";

const i18n = createI18n({
  locale: "zh_cn",
  fallbackLocale: "en_us",
  messages: {
    zh_cn,
    en_us
  }
});

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(i18n);
app.use(PrimeVue, {
  theme: "none"
});
app.use(ToastService);

app.mount("#app");
