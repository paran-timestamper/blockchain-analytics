<template>
  <div class="heatmap-wrapper">
    <HeatmapView :a-token="aToken" :b-token="bToken" @cell-click="handleClick" />
    <HeatmapTable
        v-if="selectedDate"
        :a-token="aToken"
        :b-token="bToken"
        :date="selectedDate"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import HeatmapView from '@/components/v4_popup/HeatmapView.vue'
import HeatmapTable from '@/components/v4_popup/HeatmapTable.vue'
import { defineProps } from 'vue';

defineProps({
  aToken: String,
  bToken: String
})

const selectedDate = ref('')

function handleClick({ date }) {
  selectedDate.value = date
}
</script>

<style scoped>
.heatmap-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>
