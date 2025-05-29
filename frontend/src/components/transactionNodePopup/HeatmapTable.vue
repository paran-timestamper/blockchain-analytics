<template>
  <div class="heatmap-table-wrapper">
    <div class="heatmap-table">
      <h3>Heatmap Table</h3>
      <table>
        <thead>
        <tr>
          <th>순위</th>
          <th>토큰</th>
          <th>트랜잭션 수</th>
          <th>총 Value (USDT)</th>
          <th>현재가 (USDT)</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="row in tableData" :key="row.rank">
          <td>{{ row.rank }}</td>
          <td>{{ row.token }}</td>
          <td>{{ row.transactionCount }}</td>
          <td>{{ parseFloat(row.totalAmount).toFixed(2) }}</td>
          <td>{{ parseFloat(row.currentMarketPrice).toFixed(4) }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: "HeatmapTable",
  props: {
    tableData: {
      type: Array,
      required: true
    }
  }
};
</script>

<style scoped>
.heatmap-table-wrapper {
  width: 100%;
  height: 100%;
  background-color: #1e1e1e;
  border-radius: 12px;
  padding: 20px;
  box-sizing: border-box;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.6);
}

.heatmap-table {
  width: 100%;
  height: 100%;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.heatmap-table h3 {
  font-size: 18px;
  margin-bottom: 16px;
  font-weight: bold;
  color: #f0f0f0;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  background-color: #2a2a2a;
  border-radius: 8px;
  overflow: hidden;
}

th, td {
  border: 1px solid #444;
  padding: 12px;
  text-align: center;
}

th {
  background-color: #333;
  color: #ddd;
  font-size: 15px;
  font-weight: 600;
}

tbody tr:nth-child(even) {
  background-color: #262626;
}

tbody tr:nth-child(odd) {
  background-color: #1e1e1e;
}

tbody tr:hover {
  background-color: #3a3a3a;
  transition: background-color 0.2s ease-in-out;
}
</style>
