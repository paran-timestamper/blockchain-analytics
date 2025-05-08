<template>
  <div class="chart-page">
    <div class="controls">
      <div class="dropdown">
        <button class="token-button" @click="toggleTokenCategory">{{ selectedToken || 'TOKEN' }}</button>
        <div v-if="showTokenCategory" class="dropdown-content scrollable-dropdown">
          <button class="dropdown-option" v-for="token in tokenList" :key="token" @click="selectToken(token)">{{ token }}</button>
        </div>
      </div>

      <div class="dropdown">
        <button class="token-button" @click="toggleFilterCategory">{{ selectedFilter || 'FILTER' }}</button>
        <div v-if="showFilterCategory" class="dropdown-content">
          <button class="dropdown-option" @click="selectFilter('거래 대금')">거래 대금</button>
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

    <div v-if="transactions.length" class="tx-table">
      <table class="styled-table">
        <thead>
        <tr>
          <th>Tx Hash</th>
          <th>From</th>
          <th>To</th>
          <th>Value(USDT)</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="tx in transactions" :key="tx.txHash">
          <td>{{ tx.txHash }}</td>
          <td>{{ tx.fromAddress }}</td>
          <td>{{ tx.toAddress }}</td>
          <td>{{ tx.totalPrice.toFixed(2) }}</td>
        </tr>
        </tbody>
      </table>

      <div class="pagination">
        <button :disabled="page === 1" @click="changePage(page - 1)">Prev</button>
        <span>Page {{ page }} / {{ totalPages }}</span>
        <button :disabled="page === totalPages" @click="changePage(page + 1)">Next</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tokenList: [],
      selectedToken: '',
      selectedFilter: '',
      showTokenCategory: false,
      showFilterCategory: false,
      filterStart: '',
      filterEnd: '',
      transactions: [],
      page: 1,
      size: 10,
      totalCount: 0,
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.totalCount / this.size);
    }
  },
  mounted() {
    this.fetchTokens();
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
      this.page = 1;
      this.fetchTransactions();
    },
    selectFilter(filter) {
      this.selectedFilter = filter;
      this.showFilterCategory = false;
      this.page = 1;
      this.fetchTransactions();
    },
    applyDateFilter() {
      this.page = 1;
      this.fetchTransactions();
    },
    resetDateFilter() {
      this.filterStart = '';
      this.filterEnd = '';
      this.page = 1;
      this.fetchTransactions();
    },
    changePage(newPage) {
      this.page = newPage;
      this.fetchTransactions();
    },
    async fetchTokens() {
      try {
        const res = await fetch('http://localhost:8080/api/common/tokens', { method: 'POST' });
        const data = await res.json();
        this.tokenList = data || [];
      } catch (err) {
        console.error('토큰 목록 로드 실패:', err);
      }
    },
    async fetchTransactions() {
      if (!this.selectedToken || !this.selectedFilter) return;

      const payload = {
        token: this.selectedToken,
        page: this.page,
        size: this.size,
        startDate: this.filterStart || null,
        endDate: this.filterEnd || null
      };

      let endpoint = '';
      if (this.selectedFilter === '최신순') {
        endpoint = 'latest';
      } else if (this.selectedFilter === '거래 대금') {
        endpoint = 'transactionPrice';
      } else {
        endpoint = 'latest'; // 기본값 fallback
      }

      try {
        const res = await fetch(`http://localhost:8080/api/charts/infos/${endpoint}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        const data = await res.json();
        this.transactions = data.transactions || [];
        this.totalCount = data.totalCount || 0;
      } catch (err) {
        console.error('트랜잭션 로드 실패:', err);
      }
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
  max-height: 300px;
  overflow-y: auto;
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
.tx-table {
  overflow-x: auto;
  margin-top: 20px;
}
.styled-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  background-color: #1a1a1a;
}
th, td {
  padding: 12px 16px;
  border: 1px solid #444;
  text-align: left;
}
th {
  background-color: #222;
  color: #ddd;
}
tr:nth-child(even) {
  background-color: #2a2a2a;
}
tr:hover {
  background-color: #333;
}
.pagination {
  margin-top: 16px;
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
