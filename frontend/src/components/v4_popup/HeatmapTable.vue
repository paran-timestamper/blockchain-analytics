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
  date: String
})

const aData = ref(null)
const bData = ref(null)

async function fetchDetails() {
  if (!props.date) return

  const res = await fetch('http://localhost:8080/api/tokenNode/heatmap/detail', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      clickedHour: props.date,
      tokens: [props.aToken, props.bToken]
    })
  })

  const result = await res.json()
  const details = result.details || []

  aData.value = details.find(d => d.token === props.aToken)
  bData.value = details.find(d => d.token === props.bToken)
}

watch(() => props.date, fetchDetails, { immediate: true })
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
  border: 1px solid #444;
  padding: 8px;
  text-align: center;
}
th {
  background-color: #333;
}
</style>
