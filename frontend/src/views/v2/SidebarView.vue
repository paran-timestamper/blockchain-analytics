<template>
  <div class="sidebar-wrapper">
    <!-- 상단 영역 -->
    <div class="top-section">
      <!-- 1. Choose Node Type -->
      <div class="box">
        <h4 class="section-title">Choose Node Type</h4>
        <label class="checkbox">
          <input type="checkbox" v-model="nodeType.transaction" />
          Tx Node
        </label>
        <label class="checkbox">
          <input type="checkbox" v-model="nodeType.token" />
          Token Node
        </label>
      </div>

      <!-- 2. Token Type -->
      <div class="box token-type-box">
        <h4 class="section-title">Token Type</h4>
        <div class="scrollable-list">
          <label class="checkbox" v-for="token in tokenTypes" :key="token">
            <input type="checkbox" v-model="selectedTokens" :value="token" />
            {{ token }}
          </label>
        </div>
      </div>

      <!-- 3. Token Price -->
      <div class="box token-price-box">
        <h4 class="section-title">Token Price</h4>
        <div class="price-entry">
          <span class="label">BTC</span>
          <span class="value">{{ prices.BTC }} USD</span>
        </div>
        <div class="price-entry">
          <span class="label">ETH</span>
          <span class="value">{{ prices.ETH }} USD</span>
        </div>
        <div class="price-entry">
          <span class="label">SOL</span>
          <span class="value">{{ prices.SOL }} USD</span>
        </div>
      </div>
    </div>

    <!-- 4. 보조 기능 (하단 고정) -->
    <div class="box bottom-fixed">
      <h4 class="section-title">보조기능</h4>
      <button class="chart-btn" @click="showChart = true">Chart</button>
    </div>

    <!-- 팝업 -->
    <div v-if="showChart" class="popup-overlay">
      <div class="popup">
        <button class="close-btn" @click="showChart = false">×</button>
        <p>📊 차트 영역입니다</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// Node type toggle
const nodeType = ref({ transaction: false, token: true })

// Token Types: API로 불러오기
const tokenTypes = ref([])
const selectedTokens = ref([])

// Token Price
const prices = ref({ BTC: '-', ETH: '-', SOL: '-' })

// Chart popup
const showChart = ref(false)

onMounted(async () => {
  // 1. 토큰 목록 (임시로 105개 모의)
  const tokenList = Array.from({ length: 105 }, (_, i) => `TOKEN-${i + 1}`)
  tokenTypes.value = ['All Token', ...tokenList]

  // 2. 가격 API 예시 (CoinGecko, 가상 호출)
  const fetched = await fetch('https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,solana&vs_currencies=usd')
  const data = await fetched.json()
  prices.value = {
    BTC: data.bitcoin?.usd ?? '-',
    ETH: data.ethereum?.usd ?? '-',
    SOL: data.solana?.usd ?? '-',
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
  justify-content: space-between;
  gap: 12px;
}

.top-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.box {
  border: 1px solid #ccc;
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  background-color: white;
  width: 100%;
  box-sizing: border-box;
}

.section-title {
  font-size: 13px;
  font-weight: bold;
  margin-bottom: 6px;
}

.checkbox {
  font-size: 12px;
  margin: 4px 0;
  display: block;
  word-break: keep-all;
}

/* ✅ Token Type Scroll 제한 */
.token-type-box .scrollable-list {
  max-height: 180px; /* 12개 정도 보이게 */
  overflow-y: auto;
}

/* ✅ Token Price 디자인 */
.token-price-box .price-entry {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  padding: 6px 0;
  border-bottom: 1px solid #eee;
}
.token-price-box .price-entry:last-child {
  border-bottom: none;
}
.label {
  font-weight: bold;
}
.value {
  font-family: monospace;
}

/* ✅ 보조기능 박스 아래 고정 */
.bottom-fixed {
  margin-top: auto;
}

/* Chart 버튼 */
.chart-btn {
  width: 100%;
  margin-top: 4px;
  padding: 6px 0;
  font-size: 12px;
  background-color: #f0f0f0;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}
.chart-btn:hover {
  background-color: #e0e0e0;
}

/* 팝업 */
.popup-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0,0,0,0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.popup {
  background: white;
  padding: 20px;
  border-radius: 16px;
  text-align: center;
  position: relative;
  width: 300px;
}
.close-btn {
  position: absolute;
  top: 6px;
  right: 10px;
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
}
</style>
