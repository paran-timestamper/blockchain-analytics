// ✅ TokenType.vue (기존 토큰 타입 UI 분리)
<template>
  <div class="token-type-box box">
    <h4 class="section-title">Token Type</h4>
    <div class="scrollable-list">
      <label class="checkbox" v-for="token in tokenTypes" :key="token">
        <input type="checkbox" v-model="selectedTokens" :value="token" />
        {{ token }}
      </label>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const tokenTypes = ref([])
const selectedTokens = ref([])

onMounted(() => {
  const tokenList = Array.from({ length: 105 }, (_, i) => `TOKEN-${i + 1}`)
  tokenTypes.value = ['All Token', ...tokenList]
})
</script>

<style scoped>
.token-type-box {
  max-height: 220px;
}
.scrollable-list {
  max-height: 220px;
  overflow-y: auto;
}
.checkbox {
  font-size: 12px;
  margin: 4px 0;
  display: block;
}
.section-title {
  font-weight: bold;
  margin-bottom: 6px;
}
</style>
