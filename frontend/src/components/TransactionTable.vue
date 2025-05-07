<template>
  <div class="transaction-table">
    <h3>Transaction Table</h3>
    <table>
      <thead>
      <tr>
        <th>TxHash</th>
        <th>Type</th>
        <th>To</th>
        <th>Total Value</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(tx, i) in dummyData" :key="i">
        <td>{{ tx.txHash }}</td>
        <td>{{ tx.type }}</td>
        <td>{{ tx.toAddress }}</td>
        <td>{{ tx.totalValue }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: "TransactionTable",
  props: {
    address: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      dummyData: Array.from({ length: 5 }, (_, i) => ({
        txHash: "0x" + Math.random().toString(16).substr(2, 8),
        type: i % 2 === 0 ? "입금" : "출금",
        toAddress: this.address,
        totalValue: (Math.random() * 100).toFixed(2)
      }))
    };
  }
};
</script>

<style scoped>
.transaction-table {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}
table {
  width: 100%;
  border-collapse: collapse;
  color: white;
  font-size: 13px;
}
th, td {
  border: 1px solid #444;
  padding: 6px 10px;
}
th {
  background-color: #333;
}
</style>
