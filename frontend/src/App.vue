<template>
  <div class="arkham-app">
    <!-- 상단 고정 바 -->
    <header class="header">
      <div class="header-top">
        <div class="logo">TimeStamper</div>
        <div class="page-title">Node Graph</div>
      </div>
    </header>

    <!-- 메인 콘텐츠 -->
    <main class="main-content">
      <!-- 우측 Token Price 컴포넌트 -->
      <div class="floating-token-price" :style="{ transform: `translateY(${floatingTop}px)` }">
        <TokenPrice />
      </div>

      <!-- Node Graph 영역 -->
      <section class="graph-section node-section">
        <div class="graph-wrapper">
          <div class="graph-inner">
            <div class="node-graph">
              <NodeGraph />
            </div>
          </div>
        </div>
      </section>

      <!-- 구분선 -->
      <div class="divider-line"></div>

      <!-- Chart 영역 -->
      <section class="graph-section chart-section">
        <div class="chart-header">
          <div class="header-top">
            <div class="logo">TimeStamper</div>
            <div class="page-title">Chart</div>
          </div>
        </div>
        <div class="graph-wrapper">
          <div class="graph-inner chart-rounded">
            <div class="node-graph">
              <Chart />
            </div>
          </div>
        </div>
      </section>

      <!-- 하단 여백 -->
      <div class="bottom-spacer"></div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import NodeGraph from './views/Nodegraph.vue';
import Chart from './views/Chart.vue';
import TokenPrice from './views/TokenPrices.vue';
const floatingTop = ref(140);

const handleScroll = () => {
  floatingTop.value = 140 + window.scrollY;
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style>
html, body {
  height: 100%;
  overflow-y: scroll;
  margin: 0;
  padding: 0;
}
</style>

<style scoped>
.arkham-app {
  background-color: #1A1D23;
  min-height: 100vh;
  color: white;
  font-family: 'Roboto Mono', monospace;
  overflow-x: hidden;
}

.header {
  background-color: #20242b;
  padding: 12px 24px;
  border-bottom: 1px solid #1f2630;
  margin-left: 0;
  width: calc(100% - 320px);
}

.chart-header {
  background-color: #20242b;
  padding: 12px 24px;
  border-bottom: 1px solid #1f2630;
  margin-left: 0;
  width: calc(100% - 85px);
}

.header-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 0;
}

.logo {
  font-size: 24px;
  color: #00bfff;
  font-weight: bold;
  cursor: pointer;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  color: #93c5fd;
}

.main-content {
  position: relative;
  min-height: 100vh;
  overflow-y: visible;
}

.floating-token-price {
  position: fixed;
  right: 32px;
  top: 140px;
  z-index: 999;
  width: 220px;
  transition: transform 0.3s ease;
}

.token-price-sidebar {
  background: #20242b;
  border: 1px solid #333;
  border-radius: 12px;
  padding: 20px;
  font-size: 16px;
}

.token-price-sidebar h3 {
  margin-bottom: 16px;
  font-size: 18px;
}

.token-box {
  background: #2b303a;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.token-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.token-icon {
  width: 20px;
  height: 20px;
}

.token-name {
  font-weight: bold;
}

.token-price {
  font-size: 18px;
  margin-top: 4px;
}

.token-change {
  font-size: 14px;
}

.token-change.positive {
  color: #4caf50;
}

.token-change.negative {
  color: #f44336;
}

.graph-section {
  background: #1A1D23;
  width: calc(100% - 240px);
  margin-left: 0;
  height: 700px;
  position: relative;
  overflow: hidden;
}

.graph-wrapper {
  display: flex;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}

.graph-inner {
  flex-grow: 1;
  margin: 24px;
  border-radius: 16px;
  background: black;
  box-shadow: inset 0 0 0 1px #222;
  position: relative;
  padding: 20px;
}

.chart-rounded {
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 16px;
}

.node-graph {
  width: 100%;
  height: 100%;
  border-radius: 16px;
}

.divider-line {
  height: 1px;
  background-color: #333;
  margin: 40px 0;
  width: calc(100% - 240px);
  margin-left: 0;
}

.bottom-spacer {
  height: 100px;
}

h3 {
  text-align: center;
  margin-bottom: 20px;
}
</style>
