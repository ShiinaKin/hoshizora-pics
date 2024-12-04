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
      path: "/admin",
      name: "adminField",
      component: () => import("@/views/structure/adminField/AdminField.vue"),
      redirect: { name: "adminOverview" },
      children: [
        {
          path: "overview",
          name: "adminOverview",
          component: () => import("@/views/structure/adminField/common/OverviewView.vue")
        },
        {
          path: "image",
          name: "imageManagement",
          component: () => import("@/views/structure/adminField/image/ImageManagementView.vue")
        },
        {
          path: "album",
          name: "albumManagement",
          component: () => import("@/views/structure/adminField/album/AlbumManagementView.vue")
        },
        {
          path: "user",
          name: "userManagement",
          component: () => import("@/views/structure/adminField/user/UserManagementView.vue")
        },
        {
          path: "group",
          name: "groupManagement",
          component: () => import("@/views/structure/adminField/group/GroupManagementView.vue")
        },
        {
          path: "strategy",
          name: "strategyManagement",
          component: () => import("@/views/structure/adminField/strategy/StrategyManagementView.vue")
        },
        {
          path: "setting",
          name: "settingHome",
          component: () => import("@/views/structure/adminField/setting/SettingView.vue"),
          redirect: { name: "systemSetting" },
          children: [
            {
              path: "system",
              name: "systemSetting",
              component: () => import("@/views/structure/adminField/setting/SystemSettingView.vue")
            },
            {
              path: "site",
              name: "siteSetting",
              component: () => import("@/views/structure/adminField/setting/SiteSettingView.vue")
            }
          ]
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