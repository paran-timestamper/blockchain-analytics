<template>
  <div class="sidebar-wrapper">
    <!-- 상단 -->
    <div class="top-section">
      <!-- 1. Choose Node Type -->
      <div class="box">
        <div class="box-content align-top">
          <h4 class="section-title">Choose Node Type</h4>
          <label class="checkbox">
            <input type="radio" name="nodeType" value="tx" v-model="selectedNodeType" />
            Tx Node
          </label>
          <label class="checkbox">
            <input type="radio" name="nodeType" value="token" v-model="selectedNodeType" />
            Token Node
          </label>
        </div>
      </div>

      <!-- 2. TokenType.vue or nodefinder.vue -->
      <component :is="selectedNodeType === 'token' ? TokenType : NodeFinder" />

      <!-- 3. Token Price -->
      <div class="box token-price-box">
        <h4 class="section-title">Token Price</h4>
        <div
            class="token-card"
            v-for="token in tokenIcons"
            :key="token.name"
        >
          <img :src="token.icon" :alt="token.name" class="token-icon" />
          <div class="token-info">
            <div class="token-name">{{ token.name }}</div>
            <div class="token-value">{{ prices[token.name] }} USD</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 4. 보조 기능 -->
    <div class="box bottom-fixed">
      <div class="box-content align-top">
        <h4 class="section-title">보조기능</h4>
        <button class="chart-btn" @click="showChart = true">Chart</button>
      </div>
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

<script>
import { ref, onMounted, watch, provide } from 'vue'
import TokenType from '@/components/v2/TokenType.vue'
import NodeFinder from '@/components/v2/nodefinder.vue'

export default {
  name: 'SidebarView',
  emits: ['update:nodeType'],
  components: {
    TokenType,
    NodeFinder
  },
  setup(props, { emit }) {
    const selectedNodeType = ref('tx')
    provide('selectedNodeType', selectedNodeType) // ✅ 반드시 setup() 내부에서 실행해야 함

    const tokenTypes = ref([])
    const selectedTokens = ref([])
    const prices = ref({ BTC: '-', ETH: '-', SOL: '-' })
    const showChart = ref(false)

    const tokenIcons = [
      {
        name: 'BTC',
        icon: 'https://assets.coingecko.com/coins/images/1/small/bitcoin.png'
      },
      {
        name: 'ETH',
        icon: 'https://assets.coingecko.com/coins/images/279/small/ethereum.png'
      },
      {
        name: 'SOL',
        icon: 'https://assets.coingecko.com/coins/images/4128/small/solana.png'
      }
    ]

    watch(selectedNodeType, (newValue) => {
      emit('update:nodeType', newValue)
    })

    onMounted(async () => {
      const tokenList = Array.from({ length: 105 }, (_, i) => `TOKEN-${i + 1}`)
      tokenTypes.value = ['All Token', ...tokenList]

      try {
        const res = await fetch('https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,solana&vs_currencies=usd')
        const data = await res.json()
        prices.value = {
          BTC: data.bitcoin?.usd ?? '-',
          ETH: data.ethereum?.usd ?? '-',
          SOL: data.solana?.usd ?? '-'
        }
      } catch (err) {
        console.error('가격 요청 실패:', err)
      }
    })

    return {
      selectedNodeType,
      tokenTypes,
      selectedTokens,
      prices,
      showChart,
      tokenIcons,
      TokenType,
      NodeFinder
    }
  }
}
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

.box-content {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  height: 100%;
}
.align-top {
  justify-content: flex-start;
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

.token-type-box {
  display: flex;
  flex-direction: column;
  overflow: hidden; /* ✅ 스크롤이 box를 벗어나지 않도록 */
}

.token-type-box .scrollable-list {
  flex: 1;
  max-height: 420px; /* 필요 시 더 길게 조절 가능 */
  overflow-y: auto;
  padding-right: 4px; /* ✅ 스크롤바 공간 확보 */
  box-sizing: border-box;
}

.token-price-box {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.token-card {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 12px;
  padding: 8px 10px;
  gap: 10px;
}

.token-icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.token-info {
  display: flex;
  flex-direction: column;
  font-size: 12px;
}

.token-name {
  font-weight: bold;
  margin-bottom: 2px;
}

.token-value {
  font-family: monospace;
  color: #333;
}

.bottom-fixed {
  margin-top: auto;
}

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

.popup-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.3);
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
