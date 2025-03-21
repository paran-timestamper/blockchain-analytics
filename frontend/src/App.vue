<template>
  <div class="app-container">
    <!-- 상단 UI 버튼 -->
    <div class="controls">
      <div class="dropdown">
        <button @click="toggleTokenCategory" :class="{ active: selectedToken !== '' }">TOKEN</button>
        <div v-if="showTokenCategory" class="dropdown-content vertical">
          <button @click="selectToken('ETH')">ETH</button>
          <button @click="selectToken('BTC')">BTC</button>
          <button @click="selectToken('USDT')">USDT</button>
        </div>
      </div>

      <div class="dropdown">
        <button @click="toggleFilterCategory" :class="{ active: selectedFilter !== '' }" style="width:105px">FILTER</button>
        <div v-if="showFilterCategory" class="dropdown-content vertical">
          <button @click="selectFilter('거래 대금')">거래 대금</button>
          <button @click="selectFilter('트랜잭션량')">트랜잭션량</button>
          <button @click="selectFilter('최신순')">최신순</button>
        </div>
      </div>

      <button @click="showTop10">Top 10 Address</button>
      <button @click="toggleHeatmap">HEATMAP</button>
    </div>

    <!-- 네트워크 그래프 -->
    <div v-if="selectedToken && selectedFilter" ref="chart" class="chart"></div>

    <!-- 🔥 노드 클릭 시 테이블 -->
    <div v-if="selectedNode" class="transaction-container">
      <h3>노드 트랜잭션 내역 ({{ selectedNode.id }})</h3>
      <table>
        <thead>
        <tr>
          <th>Tx Hash</th>
          <th>Direction</th>
          <th>상대 주소</th>
          <th>Value (ETH)</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(tx, index) in transactions" :key="index">
          <td>{{ tx.hash }}</td>
          <td>{{ tx.direction }}</td>
          <td>{{ tx.address }}</td>
          <td>{{ tx.value }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import * as d3 from "d3";

export default {
  data() {
    return {
      selectedToken: "",
      selectedFilter: "",
      showTokenCategory: false,
      showFilterCategory: false,
      nodes: [],
      links: [],
      selectedNode: null,
      transactions: [],
      svg: null,
    };
  },
  watch: {
    selectedToken() {
      this.checkGraphConditions();
    },
    selectedFilter() {
      this.checkGraphConditions();
    }
  },
  methods: {
    checkGraphConditions() {
      if (this.selectedToken && this.selectedFilter) {
        this.generateGraphData();
      }
    },
    generateGraphData() {
      this.nodes = [{ id: "Central", group: 0, size: 30 }];
      this.links = [];

      for (let i = 1; i <= 100; i++) {
        this.nodes.push({ id: `Wallet ${i}`, group: Math.floor(i / 10) + 1, size: 12 });
        this.links.push({ source: "Central", target: `Wallet ${i}` });
      }

      this.$nextTick(() => {
        this.createGraph();
      });
    },
    createGraph() {
      const width = window.innerWidth;
      const height = window.innerHeight;

      d3.select(this.$refs.chart).select("svg").remove();

      this.svg = d3.select(this.$refs.chart)
          .append("svg")
          .attr("width", width)
          .attr("height", height)
          .style("background", "#000");

      const simulation = d3.forceSimulation(this.nodes)
          .force("link", d3.forceLink(this.links).id(d => d.id).distance(150))
          .force("charge", d3.forceManyBody().strength(-1000))
          .force("center", d3.forceCenter(width / 2, height / 2));

      const link = this.svg.selectAll("line")
          .data(this.links)
          .enter().append("line")
          .attr("stroke", "#666")
          .attr("stroke-width", 1);

      const node = this.svg.selectAll("circle")
          .data(this.nodes)
          .enter().append("circle")
          .attr("r", d => d.size)
          .attr("fill", d => d.group === 0 ? "#FF5733" : "#64b5f6")
          .style("cursor", "pointer")
          .style("pointer-events", "all")
          .on("click", (event, d) => {
            console.log("클릭된 노드:", d);
            this.nodeClicked(d);
          });

      simulation.on("tick", () => {
        link.attr("x1", d => d.source.x).attr("y1", d => d.source.y)
            .attr("x2", d => d.target.x).attr("y2", d => d.target.y);
        node.attr("cx", d => d.x).attr("cy", d => d.y);
      });
    },
    nodeClicked(node) {
      if (node.group === 0) return;

      this.selectedNode = { ...node };

      this.transactions = [
        {
          hash: "0x1a2b3c4d",
          direction: "입금",
          address: "0x8e41...d97c",
          value: "2.45"
        },
        {
          hash: "0x5f6g7h8i",
          direction: "출금",
          address: "0x9a3c...b12e",
          value: "1.03"
        },
        {
          hash: "0xabc123ff",
          direction: "입금",
          address: "0x3f8e...ac19",
          value: "0.77"
        },
        {
          hash: "0xdeadbeef",
          direction: "입금",
          address: "0x10aa...bbcc",
          value: "4.31"
        },
        {
          hash: "0xcafe1234",
          direction: "출금",
          address: "0x44ff...a212",
          value: "3.65"
        }
      ];
    },
    toggleTokenCategory() {
      this.showTokenCategory = !this.showTokenCategory;
    },
    toggleFilterCategory() {
      this.showFilterCategory = !this.showFilterCategory;
    },
    selectToken(token) {
      this.selectedToken = token;
      this.showTokenCategory = false;
      this.checkGraphConditions();
    },
    selectFilter(filter) {
      this.selectedFilter = filter;
      this.showFilterCategory = false;
      this.checkGraphConditions();
    },
    showTop10() {
      console.log("Top 10 Address 표시");
    },
    toggleHeatmap() {
      console.log("HEATMAP 버튼 클릭");
    }
  }
};
</script>

<style scoped>
.app-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  background: black;
  overflow: hidden;
}

.chart {
  width: 100%;
  height: 100%;
}

.controls {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 10px;
}

.dropdown {
  position: relative;
}

.dropdown-content {
  position: absolute;
  top: 100%;
  left: 0;
  display: flex;
  flex-direction: column;
  background: #222;
  color: white;
  border-radius: 5px;
  padding: 5px;
  z-index: 100;
}

button {
  padding: 8px 12px;
  background: #444;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
  border-radius: 5px;
  width: 100%;
  text-align: left;
}

button.active {
  background: #ff9800;
}

.transaction-container {
  position: absolute;
  top: 60px;
  right: 30px;
  background: #111;
  padding: 15px;
  border-radius: 5px;
  color: white;
  z-index: 10;
  width: 300px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
  font-size: 13px;
}

th, td {
  border: 1px solid #444;
  padding: 5px;
  text-align: left;
}
</style>
