import { ref } from "vue";
import { defineStore } from "pinia";

export const useCommonStore = defineStore("common", () => {
  const title = ref("HoshizoraPics");
  const setTitle = (newTitle: string) => {
    title.value = newTitle;
  };

  const subTitle = ref("A simple pic management");
  const setSubTitle = (newTitle: string) => {
    subTitle.value = newTitle;
  };

  const siteDescription = ref("A site for managing pictures");

  const setSiteDescription = (newSiteDescription: string) => {
    siteDescription.value = newSiteDescription;
  };

  const allowSignup = ref(false);
  const setAllowSignup = (newAllowSignup: boolean) => {
    allowSignup.value = newAllowSignup;
  };

  return { title, setTitle, subTitle, setSubTitle, allowSignup, setAllowSignup, siteDescription, setSiteDescription };
});

export const useUserFieldStore = defineStore("userField", () => {
  const userId = ref(-1);
  const setUserId = (newUserId: number) => {
    userId.value = newUserId;
  };

  const username = ref("");
  const setUsername = (newUsername: string) => {
    username.value = newUsername;
  };

  const email = ref("");
  const setEmail = (newEmail: string) => {
    email.value = newEmail;
  };

  const groupName = ref("");
  const setGroupName = (newGroupName: string) => {
    groupName.value = newGroupName;
  };

  return { userId, setUserId, username, setUsername, email, setEmail, groupName, setGroupName };
});
