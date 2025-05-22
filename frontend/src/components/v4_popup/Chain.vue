<template>
  <div class="chain-table">
    <h3>주로 사용한 체인</h3>
    <table>
      <thead>
      <tr>
        <th>토큰 순위</th>
        <th>{{ aToken }}</th>
        <th>{{ bToken }}</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="rank in 10" :key="rank">
        <td>{{ rank }}순위</td>
        <td>{{ aChains[rank - 1]?.chain || '-' }}</td>
        <td>{{ bChains[rank - 1]?.chain || '-' }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { defineProps } from 'vue';

const props = defineProps({
  aToken: String,
  bToken: String
})

const aChains = ref([])
const bChains = ref([])

async function fetchChains() {
  try {
    const res = await fetch('http://localhost:8080/api/tokenNode/chain', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        tokens: [props.aToken, props.bToken]
      })
    })

    const result = await res.json()
    aChains.value = result.tokenChains?.tokenA?.topChains || []
    bChains.value = result.tokenChains?.tokenB?.topChains || []

  } catch (err) {
    console.error('🚨 체인 정보 불러오기 실패:', err)
    aChains.value = []
    bChains.value = []
  }
}

watch(() => [props.aToken, props.bToken], fetchChains, { immediate: true })
</script>

<style scoped>
.chain-table {
  color: white;
  font-size: 13px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  border: 1px solid #444;
  padding: 8px;
  text-align: center;
}
th {
  background-color: #333;
  color: #ddd;
}
</style>
