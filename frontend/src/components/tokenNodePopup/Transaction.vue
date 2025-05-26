<template>
  <div class="transaction-table-wrapper">
    <h3 class="table-title">토큰 트랜잭션 내역 ({{ token }})</h3>

    <div class="table-wrapper">
      <table class="tx-table">
        <thead>
        <tr>
          <th>TxHash</th>
          <th>From → To</th>
          <th>Value</th>
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
          <td>
            <div class="cell-with-copy-row">
              <span>{{ tx.fromAddress.slice(0, 6) }} → {{ tx.toAddress.slice(0, 6) }}</span>
            </div>
          </td>
          <td>{{ tx.value }}</td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- ✅ 새 페이지네이션 -->
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

<script setup>
import {ref, watch, onMounted} from 'vue'
import {useNodeStore} from "@/store/nodeStore";

const nodeStore = useNodeStore()
const transactions = ref([])
const currentPage = ref(1)
const rowsPerPage = ref(10)

async function fetchTransactions() {
  console.log("📡 API 호출 준비 중:", {
    token: nodeStore.node,
    page: currentPage.value,
    size: rowsPerPage.value
  })



  try {

    console.log("token", props.token);

    const res = await fetch("http://localhost:8080/api/tokenNode/transaction", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        token: nodeStore.node,
        page: currentPage.value,
        size: rowsPerPage.value
      })
    })

    const data = await res.json()
    transactions.value = data.transactions || []
  } catch (err) {
    console.error("🚨 Failed to fetch token transactions:", err)
    transactions.value = []
  }
}

function changePage(n) {
  if (n < 1 || n > 10) return
  currentPage.value = n
  fetchTransactions()
}

function copyToClipboard(value) {
  navigator.clipboard.writeText(value)
}

watch(() => nodeStore.node, () => {
  currentPage.value = 1
  fetchTransactions()
}, { immediate: true })

onMounted(() => {
  fetchTransactions()
})
</script>

<style scoped>
.transaction-table-wrapper {
  background: #1e1e1e;
  color: white;
  padding: 10px;
  border-radius: 8px;
  height: 100%;
  box-sizing: border-box;
}

.tx-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.tx-table th,
.tx-table td {
  padding: 8px 6px;
  border: 1px solid #444;
  text-align: center;
}

.tx-table th {
  background: #333;
  color: #ddd;
}

.table-wrapper {
  overflow-x: auto;
}

.table-title {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: bold;
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
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.pagination-controls button {
  background: #111;
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

.pagination-controls button.active {
  background: #ff9800;
  color: black;
  font-weight: bold;
}
</style>
