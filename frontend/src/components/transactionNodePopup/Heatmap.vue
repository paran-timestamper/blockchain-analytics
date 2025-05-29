<template>
  <div class="heatmap-wrapper">
    <HeatmapView :address="address" @cell-click="handleClick" />
    <HeatmapTable
        :address="address"
        :date="selectedDate"
        :mode="selectedMode"
        :key="selectedDate + '-' + selectedMode"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import HeatmapView from '@/components/transactionNodePopup/HeatmapView.vue';
import HeatmapTable from '@/components/transactionNodePopup/HeatmapTable.vue';

/* eslint-disable no-undef */
defineProps({
  address: {
    type: String,
    required: true
  }
});

const selectedDate = ref('');
const selectedMode = ref('');
const forceUpdate = ref(0);

function handleClick({ date, mode }) {
  forceUpdate.value++;
  selectedDate.value = date;
  selectedMode.value = mode;
}
</script>

<style scoped>
.heatmap-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>
