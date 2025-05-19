<template>
  <div class="chart-page">
    <div class="controls">
      <div class="dropdown">
        <button class="token-button" @click="toggleTokenCategory">{{ selectedToken || 'TOKEN' }}</button>
        <div v-if="showTokenCategory" class="dropdown-content">
          <button class="dropdown-option" @click="selectToken('ETH')">ETH</button>
          <button class="dropdown-option" @click="selectToken('BTC')">BTC</button>
          <button class="dropdown-option" @click="selectToken('USDT')">USDT</button>
        </div>
      </div>

      <div class="dropdown">
        <button class="token-button" @click="toggleFilterCategory">{{ selectedFilter || 'FILTER' }}</button>
        <div v-if="showFilterCategory" class="dropdown-content">
          <button class="dropdown-option" @click="selectFilter('거래 대금')">거래 대금</button>
          <button class="dropdown-option" @click="selectFilter('트랜잭션량')">트랜잭션량</button>
          <button class="dropdown-option" @click="selectFilter('최신순')">최신순</button>
        </div>
      </div>

      <div v-if="selectedToken && selectedFilter" class="date-filter">
        <input type="date" v-model="filterStart" /> ~
        <input type="date" v-model="filterEnd" />
        <button @click="applyDateFilter">Filter</button>
        <button @click="resetDateFilter">Reset</button>
      </div>
    </div>

    <div v-if="filteredTxs.length" class="tx-table">
      <table>
        <thead>
        <tr>
          <th>Tx Hash</th>
          <th>From</th>
          <th>To</th>
          <th>Value(ETH)</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="tx in paginatedTxs" :key="tx.hash">
          <td>{{ tx.hash }}</td>
          <td>{{ tx.from }}</td>
          <td>{{ tx.to }}</td>
          <td>{{ tx.value }}</td>
        </tr>
        </tbody>
      </table>

      <div class="pagination">
        <button :disabled="currentPage === 1" @click="currentPage--">Prev</button>
        <span>Page {{ currentPage }} / {{ totalPages }}</span>
        <button :disabled="currentPage === totalPages" @click="currentPage++">Next</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedToken: '',
      selectedFilter: '',
      showTokenCategory: false,
      showFilterCategory: false,
      filterStart: '',
      filterEnd: '',
      currentPage: 1,
      txs: [],
      rawTxs: []
    };
  },
  computed: {
    filteredTxs() {
      if (!this.filterStart || !this.filterEnd) return this.txs;
      const start = new Date(this.filterStart);
      const end = new Date(this.filterEnd);
      return this.txs.filter(tx => {
        const txDate = new Date(tx.timestamp);
        return txDate >= start && txDate <= end;
      });
    },
    paginatedTxs() {
      const start = (this.currentPage - 1) * 10;
      const end = start + 10;
      return this.filteredTxs.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.filteredTxs.length / 10);
    }
  },
  methods: {
    toggleTokenCategory() {
      this.showTokenCategory = !this.showTokenCategory;
    },
    toggleFilterCategory() {
      this.showFilterCategory = !this.showFilterCategory;
    },
    selectToken(token) {
      this.selectedToken = token;
      this.showTokenCategory = false;
      this.generateMockTxs();
    },
    selectFilter(filter) {
      this.selectedFilter = filter;
      this.showFilterCategory = false;
      this.generateMockTxs();
    },
    generateMockTxs() {
      if (!this.selectedToken || !this.selectedFilter) return;
      this.rawTxs = Array.from({ length: 100 }, () => ({
        hash: `0x${Math.random().toString(16).slice(2, 10)}`,
        from: `0x${Math.random().toString(16).slice(2, 12)}`,
        to: `0x${Math.random().toString(16).slice(2, 12)}`,
        value: (Math.random() * 10).toFixed(3),
        timestamp: this.generateRandomTimestamp()
      }));
      this.txs = [...this.rawTxs];
      this.currentPage = 1;
    },
    generateRandomTimestamp() {
      const start = new Date("2024-01-01T00:00:00").getTime();
      const end = new Date("2025-03-31T23:59:59").getTime();
      const randomTime = new Date(start + Math.random() * (end - start));
      const pad = (n) => (n < 10 ? '0' + n : n);
      return `${randomTime.getFullYear()}-${pad(randomTime.getMonth() + 1)}-${pad(randomTime.getDate())}`;
    },
    applyDateFilter() {
      this.currentPage = 1;
    },
    resetDateFilter() {
      this.filterStart = '';
      this.filterEnd = '';
      this.currentPage = 1;
    }
  }
};
</script>

<style scoped>
.chart-page {
  padding: 24px;
  color: white;
}
.controls {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
}
.dropdown {
  position: relative;
}
.token-button {
  background: #1f2630;
  color: #c7d2fe;
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  font-size: 16px;
  transition: 0.2s;
}
.token-button:hover {
  background: #2196f3;
  color: white;
}
.dropdown-content {
  position: absolute;
  top: 110%;
  left: 0;
  background: rgba(50, 50, 50, 0.85);
  backdrop-filter: blur(8px);
  border-radius: 8px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 100;
}
.dropdown-option {
  background-color: #1f2630;
  color: #93c5fd;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 15px;
}
.dropdown-option:hover {
  background-color: #2196f3;
  color: white;
}
.date-filter {
  display: flex;
  align-items: center;
  gap: 8px;
}
.tx-table table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  font-size: 14px;
}
th, td {
  padding: 8px;
  border: 1px solid #555;
  text-align: left;
}
.pagination {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}
button {
  background: #2196f3;
  color: white;
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled {
  background: #555;
  cursor: not-allowed;
}
</style>