<template>
  <div class="address-popup">
    <div class="popup-header">
      <h3>{{ toAddress }}</h3>
      <button class="close-btn" @click="$emit('close')">×</button>
    </div>
    <div class="popup-content">
      <!-- 여기에 추가 정보를 렌더링하세요 -->
      <p>여기에 {{ toAddress }}의 상세 정보를 표시할 수 있습니다.</p>
    </div>
  </div>
</template>

<script setup>
// toAddress prop만 받고, 다른 참조는 없습니다.
const props = defineProps({
  toAddress: {
    type: String,
    required: true
  }
})
defineEmits(['close'])
</script>

<style scoped>
.address-popup {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(50, 50, 50, 0.9);
  border-radius: 8px;
  padding: 16px;
  width: 280px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
  color: #fff;
  z-index: 20;
}
.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.close-btn {
  background: transparent;
  border: none;
  color: #fff;
  font-size: 1.2rem;
  cursor: pointer;
}
.popup-content {
  font-size: 0.9rem;
}
</style>
