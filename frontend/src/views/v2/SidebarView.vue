<template>
  <div class="sidebar-wrapper">
    <!-- 상단 -->
    <div class="top-section">
      <!-- 1. Choose Node Type -->
      <div class="box">
        <div class="box-content align-top">
          <h4 class="section-title">Choose Node Type</h4>
          <label class="checkbox">
            <input
                type="radio"
                name="nodeType"
                value="tx"
                v-model="selectedNodeType"
            />
            Tx Node
          </label>
          <label class="checkbox">
            <input
                type="radio"
                name="nodeType"
                value="token"
                v-model="selectedNodeType"
            />
            Token Node
          </label>
        </div>
      </div>

      <!-- 2. TokenType.vue or NodeFinder.vue -->
      <component
          :is="selectedNodeType === 'token' ? TokenType : NodeFinder"
      />

      <!-- 3. Token Price -->
      <div class="box token-price-box">
        <h4 class="section-title">Token Price</h4>
        <div
            class="token-card"
            v-for="token in tokenIcons"
            :key="token.name"
        >
          <img
              :src="token.icon"
              :alt="token.name"
              class="token-icon"
          />
          <div class="token-info">
            <div class="token-name">{{ token.name }}</div>
            <div class="token-value">{{ prices[token.name] }} USD</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 4. 보조 기능: Chart 버튼 -->
    <div class="box bottom-fixed">
      <div class="box-content align-top">
        <h4 class="section-title">보조기능</h4>
        <button class="chart-btn" @click="showChart = true">
          Chart
        </button>
      </div>
    </div>

    <!-- 5. Chart.vue 팝업 모달 -->
    <div
        v-if="showChart"
        class="modal-overlay"
        @click.self="showChart = false"
    >
      <div class="modal-content">
        <!-- 닫기 버튼 -->
        <button class="close-btn" @click="showChart = false">✕</button>
        <!-- 실제 Chart component -->
        <Chart :address="selectedAddress" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import TokenType from '@/components/v2/TokenType.vue'
import NodeFinder from '@/components/v2/nodefinder.vue'
// Chart.vue 를 모달 팝업으로 쓰기 위해 import
import Chart from '@/components/v2/Chart.vue'

// 1) App.vue 등에서 제공하는 selectedNodeType
const selectedNodeType = inject('selectedNodeType')

// 3) Chart 모달 표시 상태
const showChart = ref(false)

// (선택) Chart.vue 에 props로 넘겨줄 주소
const selectedAddress = ref(null)

// 4) Token Price 데이터
const tokenIcons = [
  { name: 'BTC', icon: 'https://assets.coingecko.com/coins/images/1/small/bitcoin.png' },
  { name: 'ETH', icon: 'https://assets.coingecko.com/coins/images/279/small/ethereum.png' },
  { name: 'SOL', icon: 'https://assets.coingecko.com/coins/images/4128/small/solana.png' }
]
const prices = ref({ BTC: '-', ETH: '-', SOL: '-' })

onMounted(async () => {
  try {
    const res = await fetch(
        'https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,solana&vs_currencies=usd'
    )
    const data = await res.json()
    prices.value = {
      BTC: data.bitcoin?.usd  ?? '-',
      ETH: data.ethereum?.usd ?? '-',
      SOL: data.solana?.usd   ?? '-'
    }
  } catch (err) {
    console.error('가격 요청 실패:', err)
  }
})
</script>

<style scoped>
.sidebar-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  gap: 12px;
}
.top-section { display: flex; flex-direction: column; gap: 12px; }
.box {
  border: 1px solid #ccc;
  border-radius: 16px;
  padding: 12px;
  background-color: white;
  width: 100%;
  box-sizing: border-box;
}
.box-content { display: flex; flex-direction: column; justify-content: flex-start; }
.align-top { justify-content: flex-start; }
.section-title { font-size: 13px; font-weight: bold; margin-bottom: 6px; }
.checkbox { font-size: 12px; margin: 4px 0; display: block; word-break: keep-all; }
.token-price-box { display: flex; flex-direction: column; gap: 8px; }
.token-card {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 12px;
  padding: 8px 10px;
  gap: 10px;
}
.token-icon { width:24px; height:24px; object-fit:contain; }
.token-info { display:flex; flex-direction:column; font-size:12px; }
.token-name { font-weight:bold; margin-bottom:2px; }
.token-value { font-family:monospace; color:#333; }
.bottom-fixed { margin-top:12px; }
.chart-btn {
  width:100%;
  padding:6px 0;
  font-size:12px;
  background:#1f2630;
  color:#c7d2fe;
  border:none;
  border-radius:10px;
  cursor:pointer;
}
.chart-btn:hover { background:#2196f3; color:white; }

/* 모달 오버레이 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  opacity : 0.9;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

/* 모달 박스 */
.modal-content {
  background: #1e1e1e;
  padding: 24px;
  border-radius: 8px;
  color: white;
  position: relative;
  /* ▶ 전체 화면 대비 90% 너비/높이로 확장 */
  width: 90vw;
  max-width: none;
  height: 70vh;
  max-height: none;
  overflow: auto;
}

/* 닫기 버튼 */
.close-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  background: transparent;
  border: none;
  font-size: 24px;
  color: white;
  cursor: pointer;
}
</style>
