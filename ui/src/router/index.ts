import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      children: [
        {
          path: "init",
          name: "siteInit",
          component: () => import("@/views/init/SiteInitView.vue")
        },
        {
          path: "login",
          name: "login",
          component: () => import("@/views/auth/LoginView.vue")
        },
        {
          path: "register",
          name: "register",
          component: () => import("@/views/auth/RegisterView.vue")
        }
      ]
    },
    {
      path: "/:pathMatch(.*)",
      name: "notFound",
      component: () => import("@/views/error/NotFoundView.vue")
    }
  ]
});

export default router;

import axios from "axios";

axios.interceptors.response.use(
  (response) => {
    const resp = response.data;
    if (resp?.code === "401") router.push({ name: "login" });
    return response;
  },
  (error) => {
    if (error.status === 401) {
      router.push({ name: "login" });
    } else {
      Promise.reject(error);
    }
  }
);