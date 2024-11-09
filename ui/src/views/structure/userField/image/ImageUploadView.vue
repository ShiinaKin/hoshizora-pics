<script setup lang="ts">
import { ref, computed } from "vue";
import { useI18n } from "vue-i18n";
import { Configuration, ImageApi, GroupApi } from "api-client";
import { Icon } from "@iconify/vue";
import Toast from "primevue/toast";
import { useToast } from "primevue/usetoast";
import DrugUpLoader from "@/components/DragUploader.vue";
import {
  transToDirect,
  transToMarkdown,
  transToMarkdownWithLink,
  transToHTML,
  transToBBCode
} from "@/utils/URLFormatUtils";

const { t } = useI18n();
const toast = useToast();

const token = localStorage.getItem("token");

const configuration = new Configuration({
  baseOptions: {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
});
const imageApi = new ImageApi(configuration);
const groupApi = new GroupApi(configuration);

const allowedImageTypes = ref<Set<string>>(new Set<string>());

groupApi
  .apiGroupTypeGet()
  .then((response) => {
    const resp = response.data;
    if (resp.isSuccessful) {
      allowedImageTypes.value = new Set(resp.data!.allowedImageTypes);
    }
  })
  .catch((error) => {
    console.error(error);
    toast.add({ severity: "error", summary: "Error", detail: error.message, life: 3000 });
  });

interface UploadFile {
  file: File;
  status: string;
  externalUrl?: string;
  errorMessage: string;
}

const fileList = ref<UploadFile[]>([]);
const succeedPublicFileList = computed(() =>
  fileList.value.filter((uploadFile) => uploadFile.status === "success" && uploadFile.externalUrl)
);

function handleFilesAdded(files: File[]) {
  fileList.value.push(
    ...files
      .filter((file) => allowedImageTypes.value.has(file.type.substring(file.type.lastIndexOf("/") + 1).toUpperCase()))
      .map((file) => ({ file, status: "pending", errorMessage: "" }))
  );
}

function handleSingleFileUpload(uploadFile: UploadFile) {
  if (uploadFile.status !== "pending" && uploadFile.status !== "error") return;
  uploadFile.status = "progressing";
  const formData = new FormData();
  formData.append("image", uploadFile.file, uploadFile.file.name);
  imageApi
    .apiImagePost({ data: formData })
    .then((response) => {
      const resp = response.data;
      if (resp.isSuccessful) {
        uploadFile.status = "success";
        if (resp.data !== "") uploadFile.externalUrl = resp.data as string;
      } else {
        uploadFile.status = "error";
        toast.add({ severity: "warn", summary: "Login Failed", detail: resp.message, life: 3000 });
      }
    })
    .catch((error) => {
      uploadFile.status = "error";
      toast.add({ severity: "warn", summary: "Login Failed", detail: error.message, life: 3000 });
    });
}

const activeTab = ref<string>("direct");

const displayUrlType = ref<string>();
const displayUrl = computed<string[]>(() => {
  switch (displayUrlType.value) {
    case "markdown":
      return succeedPublicFileList.value.map((uploadFile) => {
        const fileName = uploadFile.file.name;
        const url = uploadFile.externalUrl!;
        return transToMarkdown(url, fileName);
      });
    case "markdownWithLink":
      return succeedPublicFileList.value.map((uploadFile) => {
        const fileName = uploadFile.file.name;
        const url = uploadFile.externalUrl!;
        return transToMarkdownWithLink(url, fileName);
      });
    case "html":
      return succeedPublicFileList.value.map((uploadFile) => {
        const fileName = uploadFile.file.name;
        const url = uploadFile.externalUrl!;
        return transToHTML(url, fileName);
      });
    case "bbcode":
      return succeedPublicFileList.value.map((uploadFile) => {
        const fileName = uploadFile.file.name;
        const url = uploadFile.externalUrl!;
        return transToBBCode(url, fileName);
      });
    case "direct":
    default:
      return succeedPublicFileList.value.map((uploadFile) => {
        const fileName = uploadFile.file.name;
        const url = uploadFile.externalUrl!;
        return transToDirect(url, fileName);
      });
  }
});

function handleAllFileUpload() {
  const needUploadFiles = fileList.value.filter((uploadFile) => uploadFile.status === "pending");
  needUploadFiles.forEach((uploadFile) => handleSingleFileUpload(uploadFile));
}

function handleDirectPattern() {
  activeTab.value = "direct";
  displayUrlType.value = "direct";
}

function handleMarkdownPattern() {
  activeTab.value = "markdown";
  displayUrlType.value = "markdown";
}

function handleMarkdownWithLinkPattern() {
  activeTab.value = "markdownWithLink";
  displayUrlType.value = "markdownWithLink";
}

function handleHtmlPattern() {
  activeTab.value = "html";
  displayUrlType.value = "html";
}

function handleBBCodePattern() {
  activeTab.value = "bbcode";
  displayUrlType.value = "bbcode";
}

function handleCopy(url: string) {
  navigator.clipboard.writeText(url);
}
</script>

<template>
  <div class="flex flex-col w-full h-screen p-8 items-center gap-4 bg-gray-100">
    <div class="w-2/3 bg-white p-4 rounded-xl">
      <div>
        <div class="text-2xl font-semibold text-gray-900 dark:text-gray-100">{{ t("message.imageUploadTitle") }}</div>
        <div class="mt-2 text-sm text-gray-600 dark:text-gray-400">{{ t("message.imageUploadDescription") }}</div>
      </div>
      <div class="my-2"></div>
      <DrugUpLoader @files-added="handleFilesAdded" :allowed-image-types="Array.from(allowedImageTypes).join(', ')" />
    </div>

    <div v-if="fileList.length > 0" class="w-2/3 bg-white p-4 rounded-xl flex-2 flex-grow">
      <div class="flex">
        <h2>{{ t("message.imageUploadUploadList") }}</h2>
        <button
          @click="handleAllFileUpload"
          class="ml-auto py-1 px-2 flex items-center gap-1 border rounded-xl hover:text-sky-700"
        >
          <span class="text-sm">{{ t("message.imageUploadUploadAllButton") }}</span>
          <Icon icon="mdi:upload-multiple" class="size-5" />
        </button>
      </div>
      <div class="h-px bg-black my-2"></div>
      <ul class="overflow-y-auto flex flex-col gap-2">
        <li
          v-for="(uploadFile, idx) in fileList"
          :key="idx"
          class="p-2 border rounded-2xl flex items-center bg-gray-50"
        >
          <Icon icon="mdi:image-outline" />
          <span class="ml-1 overflow-x-auto text-sm">{{ uploadFile.file.name }}</span>
          <span class="ml-auto">
            <span v-if="uploadFile.status === 'pending'" class="flex gap-2">
              <button @click="handleSingleFileUpload(uploadFile)" class="hover:text-sky-600">
                <Icon icon="mdi:upload" class="size-5" />
              </button>
              <button @click="fileList.splice(idx, 1)" class="hover:text-red-600">
                <Icon icon="mdi:close-circle-outline" class="size-5" />
              </button>
            </span>
            <Icon v-else-if="uploadFile.status === 'progressing'" icon="mdi:progress-upload" class="size-5" />
            <Icon v-else-if="uploadFile.status === 'success'" icon="mdi:check-circle-outline" class="size-5" />
            <span v-else class="flex gap-2">
              <button @click="handleSingleFileUpload(uploadFile)" class="hover:text-sky-600">
                <Icon icon="mdi:upload" class="size-5" />
              </button>
              <Icon icon="mdi:alert-circle-outline" class="text-red-600 size-5" :title="uploadFile.errorMessage" />
            </span>
          </span>
        </li>
      </ul>
    </div>

    <div v-if="succeedPublicFileList.length > 0" class="w-2/3 bg-white p-4 rounded-xl flex-1">
      <div
        class="flex overflow-x-auto overflow-y-hidden border-b border-gray-200 whitespace-nowrap dark:border-gray-700"
      >
        <button
          @click="handleDirectPattern"
          :class="[
            'inline-flex items-center h-10 px-4 -mb-px text-sm text-center sm:text-base whitespace-nowrap focus:outline-none',
            activeTab === 'direct'
              ? 'text-sky-600 bg-transparent border-b-2 border-sky-500 dark:border-sky-400 dark:text-sky-300'
              : 'text-gray-700 bg-transparent border-b-2 border-transparent dark:text-white cursor-base hover:border-gray-400'
          ]"
        >
          Direct
        </button>

        <button
          @click="handleMarkdownPattern"
          :class="[
            'inline-flex items-center h-10 px-4 -mb-px text-sm text-center sm:text-base whitespace-nowrap focus:outline-none',
            activeTab === 'markdown'
              ? 'text-sky-600 bg-transparent border-b-2 border-sky-500 dark:border-sky-400 dark:text-sky-300'
              : 'text-gray-700 bg-transparent border-b-2 border-transparent dark:text-white cursor-base hover:border-gray-400'
          ]"
        >
          Markdown
        </button>

        <button
          @click="handleMarkdownWithLinkPattern"
          :class="[
            'inline-flex items-center h-10 px-4 -mb-px text-sm text-center sm:text-base whitespace-nowrap focus:outline-none',
            activeTab === 'markdownWithLink'
              ? 'text-sky-600 bg-transparent border-b-2 border-sky-500 dark:border-sky-400 dark:text-sky-300'
              : 'text-gray-700 bg-transparent border-b-2 border-transparent dark:text-white cursor-base hover:border-gray-400'
          ]"
        >
          Markdown with link
        </button>

        <button
          @click="handleHtmlPattern"
          :class="[
            'inline-flex items-center h-10 px-4 -mb-px text-sm text-center sm:text-base whitespace-nowrap focus:outline-none',
            activeTab === 'html'
              ? 'text-sky-600 bg-transparent border-b-2 border-sky-500 dark:border-sky-400 dark:text-sky-300'
              : 'text-gray-700 bg-transparent border-b-2 border-transparent dark:text-white cursor-base hover:border-gray-400'
          ]"
        >
          HTML
        </button>

        <button
          @click="handleBBCodePattern"
          :class="[
            'inline-flex items-center h-10 px-4 -mb-px text-sm text-center sm:text-base whitespace-nowrap focus:outline-none',
            activeTab === 'bbcode'
              ? 'text-sky-600 bg-transparent border-b-2 border-sky-500 dark:border-sky-400 dark:text-sky-300'
              : 'text-gray-700 bg-transparent border-b-2 border-transparent dark:text-white cursor-base hover:border-gray-400'
          ]"
        >
          BBCode
        </button>
      </div>
      <ul class="pt-2">
        <li
          v-for="(uploadFile, idx) in succeedPublicFileList"
          :key="idx"
          class="px-4 py-2 border rounded-2xl flex items-center text-gray-800 bg-gray-50 hover:bg-gray-100 hover:cursor-pointer"
          @click="handleCopy(displayUrl[idx])"
          :title="uploadFile.file.name"
        >
          <span class="overflow-x-auto text-sm">{{ displayUrl[idx] }}</span>
          <span class="ml-auto hover:text-sky-600" title="点击复制">
            <Icon icon="mdi:content-paste" class="size-5" />
          </span>
        </li>
      </ul>
    </div>
    <Toast />
  </div>
</template>

<style scoped></style>
