<template>
  <div class="transaction-chart">
    <h3>📦 트랜잭션 차트</h3>
    <table class="tx-table">
      <thead>
      <tr>
        <th>TxHash</th>
        <th>Direction</th>
        <th>ToAddress</th>
        <th>Value (USDT)</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(tx, index) in top10" :key="index">
        <td>{{ truncate(tx.txHash) }}</td>
        <td>{{ tx.direction }}</td>
        <td>{{ truncate(tx.toAddress) }}</td>
        <td>{{ tx.totalValue.toFixed(2) }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
const props = defineProps({
  transactions: {
    type: Array,
    required: true
  }
})

const top10 = props.transactions.slice(0, 10)

function truncate(str) {
  return str?.length > 10 ? str.slice(0, 6) + '...' : str
}
</script>

<style scoped>
.transaction-chart {
  color: white;
  padding: 10px;
  border-radius: 8px;
  background: #1e1e1e;
  border: 1px solid #444;
}

.tx-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.tx-table th,
.tx-table td {
  border: 1px solid #333;
  padding: 6px 8px;
  text-align: center;
}

.tx-table th {
  background: #333;
  color: #ddd;
}
</style>
