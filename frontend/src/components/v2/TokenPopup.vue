<!-- src/components/v2/TokenPopup.vue -->
<template>
  <div class="token-popup-overlay">
    <div class="token-popup">
      <button class="close-btn" @click="$emit('close')">×</button>
      <h3>Compare Tokens</h3>
      <p><strong>Token A:</strong> {{ tokenA }}</p>
      <p><strong>Token B:</strong> {{ tokenB }}</p>
      <!-- 여기에 두 토큰을 비교하는 내용을 추가하세요 -->
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  tokenA: { type: String, required: true },
  tokenB: { type: String, required: true }
})
</script>

<style scoped>
.token-popup-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.token-popup {
  background: white;
  padding: 20px;
  border-radius: 12px;
  position: relative;
  width: 320px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}
.close-btn {
  position: absolute;
  top: 8px;
  right: 10px;
  background: transparent;
  border: none;
  font-size: 18px;
  cursor: pointer;
}
</style>
