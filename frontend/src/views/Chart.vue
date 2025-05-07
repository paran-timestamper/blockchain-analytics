<template>
  <div class="chart-page">
    <!-- 필터 UI -->
    <div class="controls">
      <div class="dropdown">
        <button class="token-button" @click="toggleTokenCategory">{{ selectedToken || 'TOKEN' }}</button>
        <div v-if="showTokenCategory" class="dropdown-content">
          <button class="dropdown-option" @click="selectToken(t)" v-for="t in tokenList" :key="t">{{ t }}</button>
        </div>
      </div>

      <div class="dropdown">
        <button class="token-button" @click="toggleFilterCategory">{{ selectedFilter || 'FILTER' }}</button>
        <div v-if="showFilterCategory" class="dropdown-content">
          <button class="dropdown-option" @click="selectFilter(f)" v-for="f in filterList" :key="f">{{ f }}</button>
        </div>
      </div>
    </div>

    <!-- 트랜잭션 테이블 -->
    <div v-if="paginatedTxs.length" class="tx-table">
      <table>
        <thead>
        <tr>
          <th>Tx Hash</th>
          <th>From</th>
          <th>To</th>
          <th>Value</th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="tx in paginatedTxs" :key="tx.hash">
          <td><span class="clip">{{ truncate(tx.hash) }}</span></td>
          <td><span class="clip">{{ truncate(tx.from) }}</span></td>
          <td><span class="clip">{{ truncate(tx.to) }}</span></td>
          <td>{{ tx.value }}</td>
          <td><button class="copy-btn" @click="copy(tx)">📋</button></td>
        </tr>
        </tbody>
      </table>

      <!-- 페이지네이션 -->
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
      currentPage: 1,
      txs: [],
      tokenList: ['ETH', 'BTC', 'USDT'],
      filterList: ['거래 대금', '트랜잭션량', '최신순']
    };
  },
  computed: {
    paginatedTxs() {
      const start = (this.currentPage - 1) * 10;
      return this.txs.slice(start, start + 10);
    },
    totalPages() {
      return Math.ceil(this.txs.length / 10);
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
      this.txs = Array.from({ length: 100 }, () => ({
        hash: `0x${Math.random().toString(16).slice(2, 10).padEnd(64, '0')}`,
        from: `0x${Math.random().toString(16).slice(2, 12).padEnd(40, '0')}`,
        to: `0x${Math.random().toString(16).slice(2, 12).padEnd(40, '1')}`,
        value: (Math.random() * 100).toFixed(3)
      }));
      this.currentPage = 1;
    },
    truncate(val) {
      return val.length > 10 ? val.slice(0, 10) + '...' : val;
    },
    copy(tx) {
      const content = `TxHash: ${tx.hash}\nFrom: ${tx.from}\nTo: ${tx.to}\nValue: ${tx.value}`;
      navigator.clipboard.writeText(content);
      alert('Copied to clipboard!');
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
}
.dropdown-content {
  position: absolute;
  top: 110%;
  left: 0;
  background: rgba(50, 50, 50, 0.85);
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
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
}
.tx-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
th, td {
  padding: 8px;
  border: 1px solid #555;
  text-align: left;
}
.clip {
  display: inline-block;
  max-width: 140px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.copy-btn {
  background: #2196f3;
  color: white;
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.pagination {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}
button:disabled {
  background: #555;
  cursor: not-allowed;
}
</style>
