<script setup>
import Transaction from '@/components/v4_popup/Transaction.vue'
import LineGraph from '@/components/v4_popup/LineGraph.vue'
import HeatmapView from '@/components/v4_popup/HeatmapView.vue'
import HeatmapTable from '@/components/v4_popup/HeatmapTable.vue'
import Chain from '@/components/v4_popup/Chain.vue'
import { ref, computed } from 'vue'
import { defineProps } from 'vue'

const props = defineProps({
  aToken: { type: String, required: true },
  bToken: { type: String, required: true }
})

const selectedDate = ref('')
const selectedMode = ref('')

function handleHeatmapClick(payload) {
  console.log('[📥 TokenPopup handleHeatmapClick]', payload)
  selectedDate.value = payload.date
  selectedMode.value = payload.mode
}

const currentDate = computed(() => selectedDate.value)
const currentMode = computed(() => selectedMode.value)
</script>

<template>
  <div class="token-popup">
    <div class="header">
      <h3>{{ props.aToken }} vs {{ props.bToken }} Token 비교</h3>
      <button class="close-btn" @click="$emit('close')">X</button>
    </div>

    <div class="popup-grid">
      <div class="left top white-box">
        <Transaction :token="props.aToken" label="A Token" />
      </div>
      <div class="left bottom white-box">
        <Transaction :token="props.bToken" label="B Token" />
      </div>
      <div class="center top white-box">
        <LineGraph :a-token="props.aToken" :b-token="props.bToken" />
      </div>
      <div class="center bottom white-box">
        <HeatmapView
            :a-token="props.aToken"
            :b-token="props.bToken"
            @cell-click="handleHeatmapClick"
        />
      </div>
      <div class="right top white-box">
        <Chain :a-token="props.aToken" :b-token="props.bToken" />
      </div>
      <div class="right bottom white-box">
        <HeatmapTable
            :a-token="props.aToken"
            :b-token="props.bToken"
            :date="currentDate"
            :mode="currentMode"
            :key="currentDate + '-' + currentMode"
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
  background: rgba(30, 30, 30, 0.85);
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

.white-box {
  border: 1px solid #ffffff;
  border-radius: 12px;
  padding: 12px;
  background-color: #1e1e1e;
  box-sizing: border-box;
}
</style>
