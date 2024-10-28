import {
  createRouter,
  createWebHistory,
  type RouteLocationNormalizedGeneric,
  type RouteLocationNormalizedLoadedGeneric
} from "vue-router";

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
      ],
      beforeEnter: (to, from) => commonRouteGuard(to, from)
    },
    {
      path: "/:pathMatch(.*)",
      name: "notFound",
      component: () => import("@/views/error/NotFoundView.vue")
    }
  ]
});

export default router;

import { useCommonStore } from "@/stores/counter";
import { CommonApi } from "api-client";

const commonRouteGuard = async (to: RouteLocationNormalizedGeneric, from: RouteLocationNormalizedLoadedGeneric) => {
  if (to.name === from.name) return;

  if (to.name === "home" || (from.name === "siteInit" && to.name === "login")) {
    const commonApi = new CommonApi();
    const commonStore = useCommonStore();

    return commonApi
      .apiSiteSettingGet()
      .then((response) => {
        if (response.isSuccessful) {
          const commonSiteSetting = response.data!!;
          commonStore.setTitle(commonSiteSetting.siteTitle!);
          commonStore.setSubTitle(commonSiteSetting.siteSubTitle!);
          commonStore.setAllowSignup(commonSiteSetting.siteAllowSignup!);
          if (commonSiteSetting.isSiteInit!!) return { name: "login" };
          else return { name: "siteInit" };
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }
};
