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
          <td>
            <div class="cell-with-copy-row">
              <span>{{ tx.txHash.slice(0, 10) + '...' }}</span>
              <button class="copy-btn" @click="copyToClipboard(tx.txHash)">📋</button>
            </div>
          </td>
          <td>{{ tx.direction }}</td>
          <td>
            <div class="cell-with-copy-row">
              <span>{{ tx.toAddress.slice(0, 10) + '...' }}</span>
              <button class="copy-btn" @click="copyToClipboard(tx.toAddress)">📋</button>
            </div>
          </td>
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

  props: {
    address: { type: String, required: true },
    token: { type: String, required: true }
  },

  data() {
    return {
      transactions: [],
      currentPage: 1,
      rowsPerPage: 10
    };
  },

  watch: {
    address: 'fetchTransactions',
    token: 'fetchTransactions'
  },

  mounted() {
    this.fetchTransactions();
  },

  methods: {
    async fetchTransactions() {
      try {
        const res = await fetch("http://localhost:8080/api/node/nodeTransaction", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            page: this.currentPage,
            size: this.rowsPerPage,
            fromAddress: 'osmo17vz8hvg96lddwawdfygvu89ua4v7qgd6p3m9ry',
            token: "ATOM",
          })
        });

        const data = await res.json();
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
  background-color: #1e1e1e;
  color: #fff;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
  max-width: 850px;
  margin: auto;
  font-family: 'Segoe UI', sans-serif;
}

.table-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  text-align: center;
}

.table-wrapper {
  overflow-x: auto;
  border-radius: 8px;
}

.tx-table {
  width: 100%;
  border-collapse: collapse;
}

.tx-table th,
.tx-table td {
  padding: 0.75rem 1rem;
  text-align: left;
  white-space: nowrap;
}

.tx-table th {
  background-color: #2e2e2e;
  font-weight: 600;
  font-size: 0.95rem;
  border-bottom: 1px solid #444;
}

.tx-table tr:nth-child(even) {
  background-color: #292929;
}

.tx-table tr:nth-child(odd) {
  background-color: #242424;
}

.cell-with-copy-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.copy-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #bbb;
  font-size: 1rem;
  transition: color 0.2s;
}

.copy-btn:hover {
  color: #fff;
}

.pagination-controls {
  margin-top: 1.2rem;
  display: flex;
  justify-content: center;
  gap: 0.4rem;
}

.pagination-controls button {
  background-color: #333;
  color: #ddd;
  border: none;
  padding: 0.4rem 0.75rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.pagination-controls button:hover {
  background-color: #444;
  color: #fff;
}

.pagination-controls button.active {
  background-color: #00c7a3;
  color: #fff;
  font-weight: bold;
}
</style>
