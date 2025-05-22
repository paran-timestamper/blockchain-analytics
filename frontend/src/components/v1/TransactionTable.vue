h<template>
  <div class="transaction-table">
    <h3>Transaction Table</h3>
    <div class="address-line">
      <span class="address-text" :title="address">{{ truncateText(address) }}</span>
      <button @click="copyToClipboard(address)">📋</button>
    </div>
    <table>
      <thead>
      <tr>
        <th>TxHash</th>
        <th>Type</th>
        <th>To</th>
        <th>Value</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(tx, i) in dummyData" :key="i">
        <td>
          <span :title="tx.txHash">{{ truncateText(tx.txHash) }}</span><button @click="copyToClipboard(tx.txHash)">📋</button>
        </td>
        <td>{{ tx.type }}</td>
        <td>
          <span :title="tx.toAddress">{{ truncateText(tx.toAddress) }}</span><button @click="copyToClipboard(tx.toAddress)">📋</button>
        </td>
        <td>{{ tx.value }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: "TransactionTable",
  props: {
    address: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      dummyData: Array.from({ length: 10 }, (_, i) => ({
        txHash: "0x" + Math.random().toString(16).substr(2, 64).padEnd(64, '0'),
        type: i % 2 === 0 ? "입금" : "출금",
        toAddress: '0x' + Math.random().toString(16).substr(2, 40).padEnd(40, '0'),
        value: (Math.random() * 100).toFixed(2)
      }))
    };
  },
  methods: {
    copyToClipboard(text) {
      navigator.clipboard.writeText(text).then(() => {
        alert("Copied to clipboard: " + text);
      });
    },
    truncateText(text) {
      return text.length > 10 ? text.slice(0, 10) + '...' : text;
    }
  }
};
</script>

<style scoped>
.transaction-table {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}

.address-line {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin: 6px 0 10px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.address-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #ccc;
}

table {
  width: 100%;
  border-collapse: collapse;
  color: white;
  font-size: 13px;
}
th, td {
  border: 1px solid #444;
  padding: 6px 10px;
  text-align: left;
  white-space: nowrap;
}
th {
  background-color: #333;
}
button {
  font-size: 12px;
  background: none;
  border: none;
  cursor: pointer;
  color: #ccc;
  padding: 0;
  margin: 0;
  vertical-align: middle;
}
button:hover {
  color: #fff;
}
</style>
