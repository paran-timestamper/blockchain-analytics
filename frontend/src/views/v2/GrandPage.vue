<!-- src/views/v2/GraphPage.vue -->
<template>
  <div class="node-graph-layout">
    <!-- 노드 그래프에 클릭 이벤트 바인딩 -->
    <NodeGraphView @toggleChart="onToggleChart" />

    <!-- 꺾은선 차트 -->
    <LineChartView :sectionDates="sectionDates" />
  </div>
</template>

<script setup>
import { ref, provide, inject, computed } from 'vue'
import NodeGraphView from '@/views/v2/NodeGraphView.vue'
import LineChartView from '@/views/v2/LineChartView.vue'

// 상위에서 App.vue 등으로부터 받는 값들
const currentSection   = inject('currentSection')
const selectedNodeType = inject('selectedNodeType')

// ① visibleAddresses는 여기서만 provide
const visibleAddresses = ref(new Set())
provide('visibleAddresses', visibleAddresses)

// ② 토글 핸들러 (NodeGraphView로부터 받아서 set을 업데이트)
function onToggleChart(address) {
  console.log('toggling', address)
  if (visibleAddresses.value.has(address)) {
    visibleAddresses.value.delete(address)
  } else {
    visibleAddresses.value.add(address)
  }
}

// ③ sectionDates 계산 (예: ["2021-01","2021-02","2021-03"])
const sectionDates = computed(() =>
    currentSection.value
        ? currentSection.value.split(',').map(d => d.replace('_','-'))
        : []
)
</script>

<style scoped>
.node-graph-layout {
  display: flex;
  height: 100vh;
}
.node-graph-layout > * {
  flex: 1;
  box-sizing: border-box;
}
</style>
