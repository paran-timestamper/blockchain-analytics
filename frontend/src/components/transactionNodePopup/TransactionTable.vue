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
      console.log("📡 Fetching transactions", this.address, this.token, "page", this.currentPage);

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
        this.transactions = data.transactions || [];
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
