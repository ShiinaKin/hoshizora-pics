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
      ],
    },
    {
      path: "/:pathMatch(.*)",
      name: "notFound",
      component: () => import("@/views/error/NotFoundView.vue")
    }
  ]
});

export default router;
