<script setup>
import { ref, provide } from 'vue'
import SidebarView from './views/v2/SidebarView.vue'
import NodeGraphView from './views/v2/NodeGraphView.vue'
import LineChartView from './views/v2/LineChartView.vue'
import TimelineControl from './views/v2/TimelineControl.vue'

const selectedNodeType = ref('tx')
const currentSection = ref('2021-02')
    // 클릭된 주소 토글용 Set
const visibleAddresses = ref(new Set())
    // depth 필터
const showDepth1       = ref(true)
const showDepth2       = ref(true)
const visibleTokens = ref([]) // ✅ 여기 추가

provide('showDepth1', showDepth1)
provide('showDepth2', showDepth2)

provide('selectedNodeType', selectedNodeType)
provide('currentSection', currentSection)
// 새로 provide
provide('visibleAddresses', visibleAddresses)

provide('visibleTokens', visibleTokens) // ✅ 여기서 전역처럼 공유

const sectionDates = [
  '2021-02', '2021-05', '2021-08',
  '2021-11', '2022-02', '2022-05', '2022-08'
]

function handleSectionChange(val) {
  currentSection.value = val
}
// NodeGraphView로부터 받은 토글 이벤트 처리
function handleToggle(address) {
  if (visibleAddresses.value.has(address)) {
    visibleAddresses.value.delete(address)
  } else {
    visibleAddresses.value.add(address)
  }
}
</script>


<template>
  <div class="app-container">
    <div class="sidebar box">
      <SidebarView />
    </div>
    <div class="main-content">
      <div class="node-graph box">
        <!-- 클릭된 노드 주소를 토글하도록 handleToggle 바인딩 -->
        <NodeGraphView @toggleChart="handleToggle" />
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
  height: 520px;
}
.line-chart {
  height: 210px;
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
