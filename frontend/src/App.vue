<script setup>
import { ref, provide } from 'vue'
import SidebarView from './views/v2/SidebarView.vue'
import NodeGraphView from './views/v2/NodeGraphView.vue'
import LineChartView from './views/v2/LineChartView.vue'
import TimelineControl from './views/v2/TimelineControl.vue'

// ✅ 중앙 상태관리
const selectedNodeType = ref('tx')
const currentSection = ref('2021-02')

provide('selectedNodeType', selectedNodeType)
provide('currentSection', currentSection)

function handleSectionChange(newVal) {
  currentSection.value = newVal  // ✅ .value로 설정
}

const sectionDates = [
  '2021-02', '2021-05', '2021-08',
  '2021-11', '2022-02', '2022-05', '2022-08'
]
</script>

<template>
  <div class="app-container">
    <div class="sidebar box">
      <SidebarView />
    </div>
    <div class="main-content">
      <div class="node-graph box">
        <NodeGraphView />
      </div>
      <div class="line-chart box">
        <LineChartView :sectionDates="sectionDates" />
      </div>
      <div class="timeline-control box">
        <TimelineControl
            :sectionDates="sectionDates"
            @update:section="handleSectionChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  height: 100vh;
  gap: 8px;
  padding: 8px;
  background-color: #f9f9f9;
}
.sidebar {
  width: 200px;
}
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.node-graph {
  height: 470px;
}
.line-chart {
  height: 260px;
}
.timeline-control {
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.box {
  border: 1px solid #ccc;
  border-radius: 16px;
  padding: 12px 12px 0 12px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  overflow: visible;
}
</style>
