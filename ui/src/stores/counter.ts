import { ref, computed } from "vue";
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

  const allowSignup = ref(false);
  const setAllowSignup = (newAllowSignup: boolean) => {
    allowSignup.value = newAllowSignup;
  };

  return { title, setTitle, subTitle, setSubTitle, allowSignup, setAllowSignup };
});
});
