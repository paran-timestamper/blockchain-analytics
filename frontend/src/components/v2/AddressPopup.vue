<script setup>
import TransactionTable from '@/components/v3_popup/TransactionTable.vue';
import MonthDayGraph from '@/components/v3_popup/MonthDayGraph.vue';
import Heatmap from '@/components/v3_popup/Heatmap.vue';
import GraphView from '@/components/v3_popup/GraphView.vue';
import Home from '@/components/v3_popup/Home.vue';
import { defineProps } from 'vue';

const props = defineProps({
  address: {
    type: String,
    required: true
  }
});

</script>


<template>
  <div class="address-popup">
    <div class="header">
      <h3>{{ props.address.slice(0, 10) }}... 트랜잭션 정보</h3>
      <button @click="$emit('close')">X</button>
    </div>

    <div class="popup-grid">
      <!-- 왼쪽 상단 -->
      <div class="left top">
        <TransactionTable :address="address" />
      </div>

      <!-- 왼쪽 하단 -->
      <div class="left bottom">
        <MonthDayGraph :address="address" :date="selectedDate" :mode="selectedMode" />
      </div>

      <!-- 가운데 상단 -->
      <div class="center top">
        <Heatmap :address="address" />
      </div>

      <!-- 가운데 하단 제거 -->

      <!-- 오른쪽 상단 -->
      <div class="right top">
        <GraphView :address="address" />
      </div>

      <!-- 오른쪽 하단 -->
      <div class="right bottom">
        <Home :keyword="bitcoin" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.address-popup {
  position: fixed;
  top: 60px;
  left: 60px;
  width: 90vw;
  height: 85vh;
  background: #1e1e1e;
  border: 1px solid #444;
  padding: 12px;
  color: white;
  z-index: 1000;

  display: flex;
  flex-direction: column;

  overflow: auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.popup-grid {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 12px;
  height: 100%;
}

.left.top     { grid-column: 1 / 2; grid-row: 1 / 2; }
.left.bottom  { grid-column: 1 / 2; grid-row: 2 / 3; }
.center.top   { grid-column: 2 / 3; grid-row: 1 / 2; }
.right.top    { grid-column: 3 / 4; grid-row: 1 / 2; }
.right.bottom { grid-column: 3 / 4; grid-row: 2 / 3; }

button {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
}
</style>
