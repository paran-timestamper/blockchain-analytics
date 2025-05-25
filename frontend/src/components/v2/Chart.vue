<!-- src/components/v2/Chart.vue -->
<template>
  <div class="chart-popup">
    <!-- 차트 내용을 넣으시면 됩니다. 예시로 간단한 메시지와 닫기 버튼만 둡니다. -->
    <button class="close-btn" @click="$emit('close')">×</button>
    <h3>Chart Popup</h3>
    <p>여기에 차트를 렌더링하세요.</p>
  </div>
</template>

<script setup>
// 부모로부터 닫기 이벤트를 emit 받으므로 별도 스크립트는 없습니다.
</script>

<style scoped>
.chart-popup {
  position: fixed;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(0,0,0,0.3);
  z-index: 1000;
}
.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: transparent;
  border: none;
  font-size: 24px;
  cursor: pointer;
}
</style>
