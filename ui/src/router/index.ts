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
      path: "/user",
      name: "userField",
      component: () => import("@/views/structure/userField/UserField.vue"),
      redirect: { name: "userOverview" },
      children: [
        {
          path: "overview",
          name: "userOverview",
          component: () => import("@/views/structure/userField/common/OverviewView.vue")
        },
        {
          path: "upload",
          name: "imageUpload",
          component: () => import("@/views/structure/userField/image/ImageUploadView.vue")
        },
        {
          path: "images",
          name: "myImage",
          component: () => import("@/views/structure/userField/image/MyImageView.vue")
        },
        {
          path: "albums",
          name: "myAlbum",
          component: () => import("@/views/structure/userField/album/MyAlbumView.vue")
        },
        {
          path: "profile",
          name: "profile",
          component: () => import("@/views/structure/userField/user/UserProfileView.vue")
        },
        {
          path: "pat",
          name: "myPersonalAccessToken",
          component: () => import("@/views/structure/userField/pat/MyPersonalAccessTokenView.vue")
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