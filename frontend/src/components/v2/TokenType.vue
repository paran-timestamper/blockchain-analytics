<template>
  <div class="box token-type-box">
    <h4 class="section-title">Token Type</h4>
    <div class="scrollable-list">
      <label class="checkbox">
        <input type="checkbox" v-model="selectedTokens" value="All Token" />
        All Token
      </label>
      <label class="checkbox" v-for="token in tokenList" :key="token">
        <input type="checkbox" v-model="selectedTokens" :value="token" />
        {{ token }}
      </label>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const tokenList = ref([])
const selectedTokens = ref([])

onMounted(async () => {
  try {
    const res = await fetch('/api/common/tokens', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({})
    })
    const data = await res.json()
    tokenList.value = data // ✅ 변수명 수정
  } catch (err) {
    console.error('Token 목록을 불러오는 중 오류 발생:', err)
  }
})
</script>

<style scoped>
.token-type-box {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.scrollable-list {
  max-height: 200px;
  overflow-y: auto;
  padding-right: 4px;
  box-sizing: border-box;
}

.checkbox {
  font-size: 12px;
  margin: 4px 0;
  display: block;
}
</style>
