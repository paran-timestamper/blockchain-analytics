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
        <LineChartView />
      </div>

      <div class="timeline-control box">
        <TimelineControl />
      </div>
    </div>
  </div>
</template>

<script setup>
import SidebarView from './views/v2/SidebarView.vue'
import NodeGraphView from './views/v2/NodeGraphView.vue'
import LineChartView from './views/v2/LineChartView.vue'
import TimelineControl from './views/v2/TimelineControl.vue'
</script>

<style scoped>
.app-container {
  display: flex;
  height: 100vh;
  gap: 8px;
  padding: 8px;
  box-sizing: border-box;
  background-color: #f9f9f9;
}

.sidebar {
  width: 200px;
  background-color: #ffffff;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.node-graph {
  flex: 2.8; /* 더 크게 */
}

.line-chart {
  flex: 1.0; /* 원래보다 줄임 */
}

.timeline-control {
  flex: 0.05; /* 더 줄임 */
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 공통 박스 스타일 */
.box {
  border: 1px solid #ccc;
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  background-color: white;
}
</style>
