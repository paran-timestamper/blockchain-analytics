<template>
  <div class="transaction-table-wrapper">
    <h3 class="table-title">노드 트랜잭션 내역</h3>

    <div class="table-wrapper">
      <table class="tx-table">
        <thead>
        <tr>
          <th>TxHash</th>
          <th>Direction</th>
          <th>To Address</th>
          <th>Value (USDT)</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(tx, i) in transactions" :key="i">
          <!-- ✅ TxHash -->
          <td>
            <div class="cell-with-copy-row">
              <span>{{ tx.txHash.slice(0, 10) + '...' }}</span>
              <button class="copy-btn" @click="copyToClipboard(tx.txHash)">📋</button>
            </div>
          </td>

          <!-- Direction -->
          <td>{{ tx.direction }}</td>

          <!-- ✅ To Address -->
          <td>
            <div class="cell-with-copy-row">
              <span>{{ tx.toAddress.slice(0, 10) + '...' }}</span>
              <button class="copy-btn" @click="copyToClipboard(tx.toAddress)">📋</button>
            </div>
          </td>

          <!-- Value -->
          <td>{{ tx.totalValue.toFixed(2) }}</td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-controls">
      <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">‹</button>
      <button
        v-for="n in 10"
        :key="n"
        @click="changePage(n)"
        :class="{ active: currentPage === n }"
      >
        {{ n }}
      </button>
      <button @click="changePage(currentPage + 1)" :disabled="currentPage === 10">›</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TransactionTable',

  data() {
    return {
      transactions: [],
      currentPage: 1,
      rowsPerPage: 10,

      // ✅ 고정된 address & token
      address: "osmo17vz8hvg96lddwawdfygvu89ua4v7qgd6p3m9ry",
      token: "ATOM"
    };
  },

  computed: {
    truncatedAddress() {
      return this.address.slice(0, 10) + "...";
    }
  },

  mounted() {
    this.fetchTransactions();
  },

  methods: {
    async fetchTransactions() {
      console.log("📡 Fetching transactions", this.address, "page", this.currentPage);

      try {
        const res = await fetch("http://localhost:8080/api/node/nodeTransaction", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            page: this.currentPage,
            size: this.rowsPerPage,
            fromAddress: this.address,
            token: this.token
          })
        });

        const data = await res.json();
        console.log("🔥 전체 응답:", data);  // 🔍 이걸 추가
        console.log("📦 data.transactions:", data.transactions);


        this.transactions = data || [];
      } catch (err) {
        console.error("🚨 Failed to fetch transactions:", err);
        this.transactions = [];
      }
    },

    changePage(n) {
      if (n < 1 || n > 10) return;
      this.currentPage = n;
      this.fetchTransactions();
      },

    copyToClipboard(value) {
      navigator.clipboard.writeText(value);
    }
  }
};
</script>

<style scoped>
.transaction-table-wrapper {
  background: #1e1e1e;
  color: white;
  padding: 16px;
  border-radius: 8px;
  /* ✅ 이중 테두리 제거 */
  /* border: 1px solid #444; */
  height: auto;
  min-height: 420px; /* ✅ 최소 높이 확보하여 여백 문제 방지 */
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* 테이블 + 페이지네이션 간 여백 최소화 */
}

.table-title {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: bold;
}

.table-wrapper {
  overflow-x: auto;
  flex-grow: 1; /* ✅ 표가 공간 채우도록 */
}

.tx-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  table-layout: fixed;
}

.tx-table th, .tx-table td {
  padding: 10px;
  border: 1px solid #333;
  text-align: center;
  word-break: break-word;
}

.tx-table th {
  background: #333;
  color: #ddd;
  font-size: 14px;
  font-weight: 600;
}

.cell-with-copy-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.copy-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
}

.pagination-controls button {
  background: #111;
  color: white;
  border: none;
  padding: 6px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.pagination-controls button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.pagination-controls button.active {
  background: #ff9800;
  color: black;
  font-weight: bold;
}

</style>
