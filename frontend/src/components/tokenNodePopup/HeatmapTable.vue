<template>
  <div class="heatmap-table">
    <h3>{{ date }} 거래 비교</h3>
    <table>
      <thead>
      <tr>
        <th></th>
        <th>{{ aToken }}</th>
        <th>{{ bToken }}</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>Transaction amount</td>
        <td>{{ aData?.transactionAmount || 0 }}</td>
        <td>{{ bData?.transactionAmount || 0 }}</td>
      </tr>
      <tr>
        <td>Total value (USDT)</td>
        <td>{{ aData?.totalValue?.toFixed(2) || '0.00' }}</td>
        <td>{{ bData?.totalValue?.toFixed(2) || '0.00' }}</td>
      </tr>
      <tr>
        <td>Current price (USDT)</td>
        <td>{{ aData?.currentPrice?.toFixed(4) || '-' }}</td>
        <td>{{ bData?.currentPrice?.toFixed(4) || '-' }}</td>
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
  bToken: String,
  date: String,
  mode: String
})

const aData = ref(null)
const bData = ref(null)

async function fetchDetails() {
  if (!props.date) {
    console.warn('[⛔️ fetchDetails] date is empty, skipping')
    return
  }

  const payload = {
    date: props.date,
    tokens: [props.aToken, props.bToken]
  }

  console.log('[📡 fetchDetails payload]', payload)

  const res = await fetch('http://localhost:8080/api/heatmap/compare/daily', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })

  const result = await res.json()
  console.log('[📨 fetchDetails result]', result)

  // ✅ 실제로 데이터에 반영
  aData.value = result.details.find(d => d.token === props.aToken)
  bData.value = result.details.find(d => d.token === props.bToken)

  console.log('[✅ 세팅 완료]', aData.value, bData.value)
}



watch(
    () => [props.date, props.mode],
    ([newDate, newMode]) => {
      console.log('[📩 HeatmapTable props changed]', newDate, newMode)
      fetchDetails()
    },
    { immediate: true }
)


</script>

<style scoped>
.heatmap-table {
  color: white;
  font-size: 13px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  border: none;
  padding: 8px;
  text-align: center;
}
th {
  background-color: #333;
}
</style>
