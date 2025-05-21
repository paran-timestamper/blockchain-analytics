<script setup>
import { onMounted } from 'vue'

onMounted(async () => {
  try {
    const res = await fetch('/api/tokenNode/graph', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    const data = await res.json()
    console.log('✅ TokenNodeGraph 응답:', data)
  } catch (err) {
    console.error('❌ TokenNodeGraph API 오류:', err)
  }
})
</script>

<template>
  <div class="graph-wrapper">
    <svg class="graph-svg" viewBox="0 0 600 600">
      <g :transform="`translate(300, 300)`">
        <!-- 원이나 노드 렌더링은 이후 적용 -->
      </g>
    </svg>
  </div>
</template>

<style scoped>
.graph-wrapper {
  width: 100%;
  height: 100%;
  background: repeating-radial-gradient(
      circle at center,
      #f9f9f9,
      #f9f9f9 10px,
      #f0f0f0 10px,
      #f0f0f0 20px
  );
  display: flex;
  justify-content: center;
  align-items: center;
}

.graph-svg {
  width: 100%;
  height: 100%;
}
</style>
