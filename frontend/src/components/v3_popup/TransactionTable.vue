<template>
  <div class="transaction-table-wrapper">
    <h3 class="table-title">노드 트랜잭션 내역 ({{ truncatedAddress }})</h3>

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
          <td>
            <div class="cell-with-copy-row">
              <span>{{ tx.txHash.slice(0, 10) + '...' }}</span>
              <button class="copy-btn" @click="copyToClipboard(tx.txHash)">📋</button>
            </div>
          </td>
          <td>{{ tx.direction }}</td>
          <td>{{ tx.toAddress.slice(0, 10) + '...' }}</td>
          <td>{{ tx.totalValue.toFixed(2) }}</td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-controls">
      <button @click="prevPage" :disabled="currentPage === 1">‹ Prev</button>
      <span>{{ currentPage }}</span>
      <button @click="nextPage">Next ›</button>
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

    nextPage() {
      this.currentPage++;
      this.fetchTransactions();
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.fetchTransactions();
      }
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
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #444;
  height: 100%;
  box-sizing: border-box;
}

.table-title {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: bold;
}

.table-wrapper {
  overflow-x: auto;
}

.tx-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.tx-table th, .tx-table td {
  padding: 8px 6px;
  border: 1px solid #333;
  text-align: center;
}

.tx-table th {
  background: #333;
  color: #ddd;
}

.cell-with-copy-row {
  display: flex;
  align-items: center;
  gap: 4px;
  justify-content: center;
}

.copy-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.pagination-controls button {
  background: #444;
  color: white;
  border: none;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
}

.pagination-controls button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
