<template>
  <div class="chart-page">
    <div class="controls">
      <!-- Token 선택 -->
      <div class="dropdown">
        <button class="token-button" @click="toggleTokenDropdown">
          {{ selectedToken || 'TOKEN' }}
        </button>
        <div v-if="showTokenDropdown" class="dropdown-content scrollable-dropdown">
          <button class="dropdown-option" v-for="token in tokenList" :key="token" @click="selectToken(token)">
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
          <button class="dropdown-option" @click="selectFilter('LATEST')">최신순</button>
          <button class="dropdown-option" @click="selectFilter('VALUE')">거래 대금</button>
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
          <td>{{ tx.txHash }}</td>
          <td>{{ tx.fromAddress }}</td>
          <td>{{ tx.toAddress }}</td>
          <td>{{ tx.value.toFixed(4) }}</td>
          <td>{{ tx.totalPrice.toFixed(2) }}</td>
          <td>{{ formatDate(tx.convertedDate) }}</td>
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
  name: 'Chart',
  data() {
    return {
      tokenList: [],
      selectedToken: null,        // ✅ default = null
      selectedFilter: 'LATEST',   // ✅ default = "Latest"
      fromAddress: '',
      showTokenDropdown: false,
      showFilterDropdown: false,
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
    this.fetchTokenList();
    this.fetchTransactions(); // ✅ 초기 요청
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
        const data = await res.json();
        this.tokenList = data || [];
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
      console.log("📦 요청 payload:", payload);


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
      const d = new Date(dateStr);
      return d.toLocaleString();
    }
  }
};
</script>

<style scoped>
.chart-page {
  padding: 20px;
  color: white;
  width: 100%;
  height: 100%;
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
  max-height: 300px;
  overflow-y: auto; /* ✅ 스크롤 가능 */
}
.dropdown-option {
  background-color: #1f2630;
  color: #93c5fd;
  border: none;
  padding: 10px 16px;
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
  margin-top: 20px;
  overflow-x: auto;
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
.styled-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px; /* ✅ 폰트 크기 줄임 */
  background-color: #1a1a1a;
  table-layout: fixed; /* ✅ 열 너비 고정 */
}

th, td {
  padding: 6px 8px; /* ✅ 셀 여백 최소화 */
  border: 1px solid #444;
  text-align: left;
  white-space: nowrap; /* ✅ 한 줄에 표시 */
  overflow: hidden;
  text-overflow: ellipsis;
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

.tx-table {
  margin-top: 16px;
  width: 100%;
  overflow: hidden; /* ✅ 스크롤 제거 */
  height: auto;     /* ✅ 높이 제한 없음 */
}

.chart-page {
  padding: 12px;
  color: white;
  width: 100%;
  height: auto;     /* ✅ 전체 내용 높이에 맞춤 */
}

</style>
