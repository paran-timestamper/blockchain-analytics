<script setup>
import { ref } from 'vue';
import TransactionTable from '@/components/transactionNodePopup/TransactionTable.vue';
import MonthDayGraph from '@/components/transactionNodePopup/MonthDayGraph.vue';
import HeatmapView from '@/components/transactionNodePopup/HeatmapView.vue';
import GraphView from '@/components/transactionNodePopup/GraphView.vue';
import HeatmapTable from '@/components/transactionNodePopup/HeatmapTable.vue';
import Home from '@/components/transactionNodePopup/Home.vue';

const props = defineProps({
  address: {
    type: String,
    required: true
  }
});

const selectedDate = ref('');
const selectedMode = ref('');
const top3Data = ref([]); // ✅ HeatmapTable에 넘길 데이터

function handleCellClick({ date, mode }) {
  console.log('[📥 addresspopup handleCellClick]', date, mode);
  selectedDate.value = date;
  selectedMode.value = mode;

  if (mode === 'monthly') {
    top3Data.value = [
      {
        rank: 1,
        token: "USDC.axl",
        transactionCount: 406,
        totalAmount: 4121795.942903,
        currentMarketPrice: 0.9999633305
      },
      {
        rank: 2,
        token: "WETH.axl",
        transactionCount: 41,
        totalAmount: 23.4893420134,
        currentMarketPrice: 2469.9995732076
      },
      {
        rank: 3,
        token: "DOT.axl",
        transactionCount: 14,
        totalAmount: 6924.9949233661,
        currentMarketPrice: 5.1172303046
      }
    ];
  } else {
    top3Data.value = [
      {
        rank: 1,
        token: "USDC.axl",
        transactionCount: 114,
        totalAmount: 584408.09546,
        currentMarketPrice: 0.9999633305
      },
      {
        rank: 2,
        token: "WETH.axl",
        transactionCount: 39,
        totalAmount: 3.7908549584,
        currentMarketPrice: 2469.9995732076
      },
      {
        rank: 3,
        token: "WBTC.axl",
        transactionCount: 24,
        totalAmount: 0.55377021,
        currentMarketPrice: 103430.7697558647
      }
    ];
  }
}

</script>

<template>
  <div class="address-popup">
    <!-- 상단 헤더 -->
    <div class="header">
      <h3>{{ props.address }}'s Transaction Info</h3>
      <button @click="$emit('close')">X</button>
    </div>

    <!-- 전체 스크롤 영역 -->
    <div class="scroll-area">
      <div class="popup-grid">
        <!-- 왼쪽 상단 -->
        <div class="left top white-box">
          <TransactionTable :address="address" />
        </div>

        <!-- 왼쪽 하단 -->
        <div class="left bottom white-box vertical-box">
          <MonthDayGraph :address="address" :date="selectedDate" :mode="selectedMode" />
        </div>

        <!-- 가운데 상단: heatmapView -->
        <div class="center top white-box">
          <HeatmapView :address="address" @cell-click="handleCellClick" />
        </div>

        <!-- 오른쪽 상단 -->
        <div class="right top white-box">
          <GraphView :address="address" />
        </div>

        <!-- 가운데 하단: heatmapTable -->
        <div class="center bottom white-box">
          <HeatmapTable
              :address="address"
              :date="selectedDate"
              :mode="selectedMode"
              :tableData="top3Data"
          />
        </div>

      </div>

      <!-- 뉴스 피드 -->
      <div class="news-section">
        <div class="news-box">
          <Home :keyword="'bitcoin'" />
        </div>
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
  background: rgba(30, 30, 30, 0.85);
  border: 1px solid #444;
  padding: 12px;
  color: white;
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.scroll-area {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.popup-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: auto auto auto;
  gap: 12px;
}

.left.top      { grid-column: 1 / 2; grid-row: 1 / 2; }
.left.bottom   { grid-column: 1 / 2; grid-row: 2 / 3; }
.center.top    { grid-column: 2 / 3; grid-row: 1 / 2; }
.right.top     { grid-column: 3 / 4; grid-row: 1 / 2; }
.center.bottom { grid-column: 2 / 4; grid-row: 2 / 3; }

.vertical-box {
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: auto;
}

.news-section {
  width: 100%;
  margin-top: 20px;
  flex-shrink: 0;
}

.news-box {
  border: 1px solid #ffffff;
  border-radius: 8px;
  background-color: #0d1117;
  padding: 16px;
}

.white-box {
  border: 1px solid #ffffff;
  border-radius: 12px;
  padding: 12px;
  background-color: #1e1e1e;
  box-sizing: border-box;
}

button {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
}

.full-height {
  grid-row: 1 / 3; /* ✅ 위에서 아래로 2줄 차지 */
}

</style>
