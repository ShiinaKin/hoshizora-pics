<script setup lang="ts">
import Paginator from "primevue/paginator";

const page = defineModel<number>("page", { default: 1 });
const pageSize = defineModel<number>("pageSize", { default: 15 });

const { curPage, totalRecord, rowOptions } = defineProps({
  curPage: {
    type: Number,
    required: true
  },
  totalRecord: {
    type: Number,
    required: true
  },
  rowOptions: {
    type: Array<number>,
    required: true,
    default: [10, 15, 20, 25, 30]
  }
});

const paginatorPT = {
  pcRowPerPageDropdown: {
    root: {
      class: "p-select-sm p-inputfield-sm"
    },
    pt: {
      listContainer: {
        class: 'text-sm'
      }
    }
  }
};
</script>

<template>
  <div class="rounded-2xl bg-white dark:bg-gray-800">
    <div class="flex justify-between items-center px-6">
      <Paginator
        @page="(it) => (page = it.page + 1)"
        v-model:rows="pageSize"
        :totalRecords="totalRecord"
        :rowsPerPageOptions="rowOptions"
        :pt="paginatorPT"
      />
      <div class="text-gray-500 dark:text-gray-400">
        <span class="font-medium text-gray-700 dark:text-gray-100">
          {{ (curPage - 1) * pageSize + 1 }}
          <span> - </span>
          {{ Math.min(curPage * pageSize, totalRecord) }}
        </span>
        of {{ totalRecord }} records
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
