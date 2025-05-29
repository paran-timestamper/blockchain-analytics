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
        <tr v-for="row in top3Data" :key="row.rank">
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
  data() {
    return {
      top3Data: []
    };
  },
  methods: {
    setData(data) {
      console.log(":bbb")
      this.top3Data = data?.dataTransactions?.length > 0
          ? data.dataTransactions
          : this.getDefaultRows();
    },
    getDefaultRows() {
      return [
        { rank: 1, token: '-', transactionCount: 0, totalAmount: 0, currentMarketPrice: 0 },
        { rank: 2, token: '-', transactionCount: 0, totalAmount: 0, currentMarketPrice: 0 },
        { rank: 3, token: '-', transactionCount: 0, totalAmount: 0, currentMarketPrice: 0 }
      ];
    }
  }
};
</script>

<style scoped>
.heatmap-table-wrapper {
  width: 100%;
  height: 100%;
  background-color: #1e1e1e;
  /* ✅ 테두리 제거 */
  /* border: 1px solid #ffffff; */
  border-radius: 12px;
  padding: 20px;
  box-sizing: border-box;
}

.heatmap-table {
  width: 100%;
  height: 100%;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.heatmap-table h3 {
  font-size: 18px;
  margin-bottom: 16px;
  font-weight: bold;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px; /* ✅ 글씨 크기 증가 */
  color: white;
}

th, td {
  border: 1px solid #444;
  padding: 10px 12px; /* ✅ 패딩 증가 */
  text-align: center;
}

th {
  background-color: #333;
  color: #ddd;
  font-size: 15px;
  font-weight: 600;
}

tbody tr {
  height: 48px; /* ✅ 줄 높이 늘리기 */
}

</style>
