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
  props: {
    address: { type: String, required: true },
    date: { type: String, required: true },
    mode: { type: String, required: true } // "daily" or "monthly"
  },
  data() {
    return {
      top3Data: []
    };
  },
  mounted() {
    if (!this.date || !this.mode) return; // ✅ 클릭 안 했으면 skip
    this.fetchTop3();
  },
  watch: {
    address() { this.fetchTop3(); },
    date() { this.fetchTop3(); },
    mode() { this.fetchTop3(); }
  },
  methods: {
    async fetchTop3() {
      try {
        const endpoint = this.mode === 'monthly'
            ? 'http://localhost:8080/api/heatmap/monthly/detail'
            : 'http://localhost:8080/api/heatmap/daily/detail';

        const payload = {
          fromAddress: this.address,
          date: this.date
        };

        const res = await fetch(endpoint, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        const result = await res.json();
        if (this.mode === "daily" && result.dataDailyTransactions?.length > 0) {
          this.top3Data = [...result.dataDailyTransactions]; // ✅ 배열 복사
        } else if (this.mode === "monthly" && result.dataTransactions?.length > 0) {
          this.top3Data = [...result.dataTransactions]; // ✅ 배열 복사
        } else {
          this.top3Data = this.getDefaultRows();
        }

      } catch (err) {
        console.error("🔥 Top 3 fetch error:", err);
        this.top3Data = this.getDefaultRows();
      }
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
