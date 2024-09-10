<script setup lang="ts">
import { RouterLink, RouterView } from "vue-router";
import type { ApiImageIdGetRequest, DefaultApi } from "api-client";

const apiImageIdGetRequest: ApiImageIdGetRequest = {
  id: 1
};

const apiClient = new DefaultApi();

function fetchData() {
  apiClient.apiImageIdGet(apiImageIdGetRequest).then((response) => {
    console.log(response.data);
  });
}
</script>

<template>
  <RouterView />
  <button @click="fetchData">Send</button>
</template>

<style scoped></style>
