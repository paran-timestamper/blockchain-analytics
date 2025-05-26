<template>
  <div class="chart-page">
    <div class="controls">
      <!-- Token 선택 -->
      <div class="dropdown">
        <button class="token-button" @click="toggleTokenDropdown">
          {{ selectedToken || 'TOKEN' }}
        </button>
        <div v-if="showTokenDropdown" class="dropdown-content scrollable-dropdown">
          <button
              class="dropdown-option"
              v-for="token in tokenList"
              :key="token"
              @click="selectToken(token)"
          >
            {{ token }}
          </button>
        </div>
      </div>

      <!-- Filter 선택 -->
      <div class="dropdown">
        <button class="token-button" @click="toggleFilterDropdown">
          {{ selectedFilter || 'FILTER' }}
        </button>
        <div v-if="showFilterDropdown" class="dropdown-content">
          <button class="dropdown-option" @click="selectFilter('LATEST')">
            최신순
          </button>
          <button class="dropdown-option" @click="selectFilter('VALUE')">
            거래 대금
          </button>
        </div>
      </div>

      <!-- Address 입력 -->
      <div class="address-input">
        <input v-model="fromAddress" placeholder="주소 입력 (선택)" />
        <button @click="applySearch">Search</button>
        <button @click="resetSearch">Reset</button>
      </div>
    </div>

    <!-- 거래 테이블 -->
    <div v-if="transactions.length" class="tx-table">
      <table class="styled-table">
        <thead>
        <tr>
          <th>Tx Hash</th>
          <th>From</th>
          <th>To</th>
          <th>Value</th>
          <th>Total Price (USDT)</th>
          <th>Date</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="tx in transactions" :key="tx.txHash">
          <td>
            <div class="address-cell">
              {{ tx.txHash.slice(0, 32) + '...' }}
              <button class="copy-button" @click="copyToClipboard(tx.txHash)">📋</button>
            </div>
          </td>
          <td>
            <div class="address-cell">
              {{ tx.fromAddress.slice(0, 32) + '...' }}
              <button class="copy-button" @click="copyToClipboard(tx.fromAddress)">📋</button>
            </div>
          </td>
          <td>
            <div class="address-cell">
              {{ tx.toAddress.slice(0, 32) + '...' }}
              <button class="copy-button" @click="copyToClipboard(tx.toAddress)">📋</button>
            </div>
          </td>
          <td>{{ tx.value.toFixed(4) }}</td>
          <td>{{ tx.totalPrice.toFixed(2) }}</td>
          <td>{{ formatDate(tx.convertedDate) }}</td>
        </tr>
        </tbody>
      </table>

      <div class="pagination">
        <button :disabled="page === 1" @click="changePage(page - 1)">
          &lt;
        </button>
        <button
            v-for="n in totalPages"
            :key="n"
            :class="{ active: page === n }"
            @click="changePage(n)"
        >
          {{ n }}
        </button>
        <button :disabled="page === totalPages" @click="changePage(page + 1)">
          &gt;
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Chart',
  data() {
    return {
      tokenList: [],
      selectedToken: null,
      selectedFilter: 'LATEST',
      fromAddress: '',
      showTokenDropdown: false,
      showFilterDropdown: false,
      transactions: [],
      page: 1,
      size: 10,
      totalCount: 0
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.totalCount / this.size) || 1;
    }
  },
  mounted() {
    this.fetchTokenList();
    this.fetchTransactions();
  },
  methods: {
    toggleTokenDropdown() {
      this.showTokenDropdown = !this.showTokenDropdown;
    },
    toggleFilterDropdown() {
      this.showFilterDropdown = !this.showFilterDropdown;
    },
    selectToken(token) {
      this.selectedToken = token;
      this.showTokenDropdown = false;
      this.page = 1;
      this.fetchTransactions();
    },
    selectFilter(filter) {
      this.selectedFilter = filter.toUpperCase();
      this.showFilterDropdown = false;
      this.page = 1;
      this.fetchTransactions();
    },
    applySearch() {
      this.page = 1;
      this.fetchTransactions();
    },
    resetSearch() {
      this.fromAddress = '';
      this.page = 1;
      this.fetchTransactions();
    },
    changePage(newPage) {
      this.page = newPage;
      this.fetchTransactions();
    },
    async fetchTokenList() {
      try {
        const res = await fetch('http://localhost:8080/api/common/tokens', { method: 'POST' });
        this.tokenList = (await res.json()) || [];
      } catch (err) {
        console.error('🔴 토큰 목록 로드 실패:', err);
      }
    },
    async fetchTransactions() {
      const payload = {
        page: this.page,
        size: this.size,
        token: this.selectedToken ?? null,
        filter: this.selectedFilter.toUpperCase(),
        fromAddress: this.fromAddress || null
      };
      try {
        const res = await fetch('http://localhost:8080/api/chart/data', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
        const data = await res.json();
        this.transactions = data.transactions || [];
        this.totalCount = data.totalCount || 0;
      } catch (err) {
        console.error('🔴 트랜잭션 로드 실패:', err);
      }
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleString();
    },
    copyToClipboard(text) {
      navigator.clipboard.writeText(text).then(() => {
        alert('복사되었습니다: ' + text);
      });
    }
  }
};
</script>

<style scoped>
.chart-page {
  padding: 12px;
  color: white;
  width: 100%;
  height: 100vh;
  box-sizing: border-box;
}
.controls {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
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
}
.token-button:hover {
  background: #2196f3;
  color: white;
}
.dropdown-content {
  position: absolute;
  top: 110%;
  left: 0;
  background: rgba(50, 50, 50, 0.9);
  backdrop-filter: blur(8px);
  border-radius: 8px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  z-index: 100;
  max-height: 200px;
  overflow-y: auto;
}
.dropdown-option {
  background-color: #1f2630;
  color: #93c5fd;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
}
.dropdown-option:hover {
  background-color: #2196f3;
  color: white;
}
.address-input {
  display: flex;
  gap: 8px;
}
.address-input input {
  padding: 8px;
  border-radius: 6px;
  border: none;
}
.tx-table {
  margin-top: 16px;
  overflow: hidden;
}
.styled-table {
  width: 100%;
  table-layout: fixed;
  border-collapse: collapse;
  background-color: #1a1a1a;
}
th,
td {
  padding: 8px 10px;
  border: 1px solid #444;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
th:nth-child(1),
td:nth-child(1) {
  width: 20%;
}
th:nth-child(2),
td:nth-child(2) {
  width: 15%;
}
th:nth-child(3),
td:nth-child(3) {
  width: 15%;
}
th:nth-child(4),
td:nth-child(4) {
  width: 10%;
}
th:nth-child(5),
td:nth-child(5) {
  width: 15%;
}
th:nth-child(6),
td:nth-child(6) {
  width: 25%;
}
th {
  background-color: #222;
  color: #ddd;
  font-weight: bold;
}
tr:nth-child(even) {
  background-color: #2a2a2a;
}
tr:hover {
  background-color: #333;
}
.address-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}
.copy-button {
  background: none;
  border: none;
  color: #ccc;
  cursor: pointer;
}
.copy-button:hover {
  color: #fff;
}
.pagination {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.pagination button {
  padding: 6px 10px;
  border-radius: 4px;
  border: none;
  background: #111;
  color: white;
  cursor: pointer;
}
.pagination button.active {
  background: #ff9800;
  color: black;
  font-weight: bold;
}
.pagination button:disabled {
  background: #555;
  cursor: not-allowed;
}
</style>
