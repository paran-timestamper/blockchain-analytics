<script setup>
import Transaction from '@/components/v4_popup/Transaction.vue'
import LineGraph from '@/components/v4_popup/LineGraph.vue'
import HeatmapView from '@/components/v4_popup/HeatmapView.vue'
import HeatmapTable from '@/components/v4_popup/HeatmapTable.vue'
import Chain from '@/components/v4_popup/Chain.vue'
import { ref } from 'vue'
import { defineProps } from 'vue';

// ✅ props 선언
const props = defineProps({
  aToken: {
    type: String,
    required: true
  },
  bToken: {
    type: String,
    required: true
  }
})

const selectedDate = ref('')
const selectedMode = ref('')

function handleHeatmapClick({ date, mode }) {
  selectedDate.value = date
  selectedMode.value = mode
}
</script>

<template>
  <div class="token-popup">
    <div class="header">
      <h3>{{ props.aToken }} vs {{ props.bToken }} Token 비교</h3>
      <button class="close-btn" @click="$emit('close')">X</button>
    </div>

    <div class="popup-grid">
      <div class="left top">
        <Transaction :token="props.aToken" label="A Token" />
      </div>
      <div class="left bottom">
        <Transaction :token="props.bToken" label="B Token" />
      </div>
      <div class="center top">
        <LineGraph :a-token="props.aToken" :b-token="props.bToken" />
      </div>
      <div class="center bottom">
        <HeatmapView
            :a-token="props.aToken"
            :b-token="props.bToken"
            @cell-click="handleHeatmapClick"
        />
      </div>
      <div class="right top">
        <Chain :a-token="props.aToken" :b-token="props.bToken" />
      </div>
      <div class="right bottom">
        <HeatmapTable
            :a-token="props.aToken"
            :b-token="props.bToken"
            :date="selectedDate"
            :mode="selectedMode"
            :key="selectedDate + '-' + selectedMode"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.token-popup {
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

  overflow-y: auto;
  overflow-x: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
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
.center.bottom{ grid-column: 2 / 3; grid-row: 2 / 3; }
.right.top    { grid-column: 3 / 4; grid-row: 1 / 2; }
.right.bottom { grid-column: 3 / 4; grid-row: 2 / 3; }
</style>
