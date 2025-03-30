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
    </div>

    <!-- 네트워크 그래프 -->
    <div v-if="selectedToken && selectedFilter" ref="chart" class="chart"></div>

    <!-- 노드 클릭 시 트랜잭션 테이블 -->
    <div v-if="selectedNode" class="transaction-container">
      <h3>노드 트랜잭션 내역 ({{ selectedNode.id }})</h3>

      <div class="transaction-filters">
        <button @click="showDateFilter = !showDateFilter">FILTER</button>
        <button @click="filterTransaction('daily')">일간 거래량</button>
        <button @click="filterTransaction('monthly')">월간 거래량</button>
      </div>

      <!-- 날짜 범위 필터 캘린더 -->
      <div v-if="showDateFilter" class="date-picker">
        <label>Start:</label>
        <input type="date" v-model="filterStart" />
        <label>End:</label>
        <input type="date" v-model="filterEnd" />
        <button @click="applyDateFilter">Apply</button>
        <button @click="resetDateFilter" style="color: #2196f3">Reset</button>
      </div>

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
        <tr v-for="(tx, index) in filteredTransactions" :key="index">
          <td>{{ tx.hash }}</td>
          <td>{{ tx.direction }}</td>
          <td>{{ tx.address }}</td>
          <td>{{ tx.value }}</td>
        </tr>
        </tbody>
      </table>

      <!-- 일간 거래량 차트 -->
      <div v-if="showDailyChart" class="daily-chart-container">
        <div class="chart-header">
          <label>날짜 선택:</label>
          <input type="date" v-model="selectedDailyDate" />
          <button @click="generateDailyChart">보기</button>
        </div>
        <div ref="dailyChart" class="daily-chart"></div>
      </div>

      <!-- 월간 거래량 차트 -->
      <div v-if="showMonthlyChart" class="daily-chart-container">
        <div class="chart-header">
          <label>월 선택:</label>
          <input type="month" v-model="selectedMonthlyDate" />
          <button @click="generateMonthlyChart">보기</button>
        </div>
        <div ref="monthlyChart" class="daily-chart"></div>
      </div>
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
      rawTransactions: [],
      svg: null,
      edgeLine: null,
      showDateFilter: false,
      filterStart: "",
      filterEnd: "",
      showDailyChart: false,
      selectedDailyDate: "",
      showMonthlyChart: false,
      selectedMonthlyDate: ""
    };
  },
  computed: {
    filteredTransactions() {
      if (!this.filterStart || !this.filterEnd) return this.transactions;
      const start = new Date(this.filterStart);
      const end = new Date(this.filterEnd);
      return this.transactions.filter(tx => {
        const txTime = new Date(tx.timestamp);
        return txTime >= start && txTime <= end;
      });
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
          .force("center", d3.forceCenter(width / 2 + 150, height / 2));

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
          .on("click", (event, d) => {
            this.nodeClicked(d);
          });

      simulation.on("tick", () => {
        link
            .attr("x1", d => d.source.x).attr("y1", d => d.source.y)
            .attr("x2", d => d.target.x).attr("y2", d => d.target.y);

        node.attr("cx", d => d.x).attr("cy", d => d.y);

        if (this.selectedNode && this.edgeLine) {
          const table = document.querySelector('.transaction-container');
          if (table && this.$refs.chart) {
            const tableRect = table.getBoundingClientRect();
            const chartRect = this.$refs.chart.getBoundingClientRect();
            const tableX = tableRect.left - chartRect.left + tableRect.width / 2;
            const tableY = tableRect.top - chartRect.top;

            this.edgeLine
                .attr("x1", this.selectedNode.x)
                .attr("y1", this.selectedNode.y)
                .attr("x2", tableX)
                .attr("y2", tableY);
          }
        }
      });
    },
    nodeClicked(node) {
      if (node.group === 0) return;

      this.selectedNode = { ...node };
      this.filterStart = "";
      this.filterEnd = "";
      this.showDateFilter = false;
      this.showDailyChart = false;
      this.showMonthlyChart = false;

      this.rawTransactions = Array.from({ length: 100 }, (_, i) => ({
        hash: `0x${Math.random().toString(16).slice(2, 10)}`,
        direction: i % 2 === 0 ? "입금" : "출금",
        address: `0x${Math.random().toString(16).slice(2, 12)}`,
        value: (Math.random() * 10).toFixed(3),
        timestamp: this.generateRandomTimestamp()
      }));

      this.transactions = [...this.rawTransactions]
          .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
          .slice(0, 10);

      this.$nextTick(() => {
        this.drawEdgeLineToTable(node);
      });
    },
    generateRandomTimestamp() {
      const start = new Date("2024-01-01T00:00:00").getTime();
      const end = new Date("2025-03-24T23:59:59").getTime();
      const randomTime = new Date(start + Math.random() * (end - start));
      const pad = (n) => (n < 10 ? '0' + n : n);
      return `${randomTime.getFullYear()}-${pad(randomTime.getMonth() + 1)}-${pad(randomTime.getDate())} ${pad(randomTime.getHours())}:${pad(randomTime.getMinutes())}:${pad(randomTime.getSeconds())}`;
    },
    drawEdgeLineToTable(node) {
      if (!this.svg) return;
      const chartRect = this.$refs.chart.getBoundingClientRect();
      const table = document.querySelector('.transaction-container');
      if (!table) return;
      const tableRect = table.getBoundingClientRect();
      const nodeX = node.x;
      const nodeY = node.y;
      const tableX = tableRect.left - chartRect.left + tableRect.width / 2;
      const tableY = tableRect.top - chartRect.top;

      if (this.edgeLine) this.edgeLine.remove();
      this.edgeLine = this.svg.append("line")
          .attr("x1", nodeX).attr("y1", nodeY)
          .attr("x2", tableX).attr("y2", tableY)
          .attr("stroke", "#ffcc00")
          .attr("stroke-width", 2)
          .attr("stroke-dasharray", "5,3");
    },
    applyDateFilter() {
      this.showDateFilter = false;
    },
    resetDateFilter() {
      this.filterStart = "";
      this.filterEnd = "";
      this.showDateFilter = false;
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
    },
    filterTransaction(type) {
      if (type === "daily") {
        this.showDailyChart = true;
        this.showMonthlyChart = false;
        this.selectedDailyDate = "";
        this.$nextTick(() => {
          d3.select(this.$refs.dailyChart).select("svg").remove();
        });
      } else if (type === "monthly") {
        this.showDailyChart = false;
        this.showMonthlyChart = true;
        this.selectedMonthlyDate = "";
        this.$nextTick(() => {
          d3.select(this.$refs.monthlyChart).select("svg").remove();
        });
      }
    },
    generateDailyChart() {
      if (!this.selectedDailyDate) return;
      const filtered = this.rawTransactions.filter(tx =>
          tx.timestamp.startsWith(this.selectedDailyDate)
      );
      const buckets = Array(6).fill(0);
      filtered.forEach(tx => {
        const hour = parseInt(tx.timestamp.split(" ")[1].split(":" )[0], 10);
        const bucketIndex = Math.floor(hour / 4);
        buckets[bucketIndex]++;
      });
      const labels = ["00:00", "04:00", "08:00", "12:00", "16:00", "20:00"];
      this.drawBarChart(labels, buckets, this.$refs.dailyChart);
    },
    generateMonthlyChart() {
      if (!this.selectedMonthlyDate) return;
      const filtered = this.rawTransactions.filter(tx =>
          tx.timestamp.startsWith(this.selectedMonthlyDate)
      );
      const buckets = Array(6).fill(0);
      filtered.forEach(tx => {
        const day = parseInt(tx.timestamp.split(" ")[0].split("-")[2], 10);
        const bucketIndex = Math.min(Math.floor((day - 1) / 5), 5);
        buckets[bucketIndex]++;
      });
      const labels = ["1~5일", "6~10일", "11~15일", "16~20일", "21~25일", "26~말일"];
      this.drawBarChart(labels, buckets, this.$refs.monthlyChart);
    },
    drawBarChart(labels, data, targetRef) {
      const width = 320;
      const height = 200;
      const margin = { top: 20, right: 20, bottom: 40, left: 40 };

      d3.select(targetRef).select("svg").remove();

      const svg = d3.select(targetRef)
          .append("svg")
          .attr("width", width)
          .attr("height", height);

      const x = d3.scaleBand()
          .domain(labels)
          .range([margin.left, width - margin.right])
          .padding(0.2);

      const y = d3.scaleLinear()
          .domain([0, d3.max(data)]).nice()
          .range([height - margin.bottom, margin.top]);

      svg.append("g")
          .attr("transform", `translate(0,${height - margin.bottom})`)
          .call(d3.axisBottom(x));

      svg.append("g")
          .attr("transform", `translate(${margin.left},0)`)
          .call(d3.axisLeft(y));

      svg.selectAll(".bar")
          .data(data)
          .enter()
          .append("rect")
          .attr("x", (_, i) => x(labels[i]))
          .attr("y", d => y(d))
          .attr("width", x.bandwidth())
          .attr("height", d => height - margin.bottom - y(d))
          .attr("fill", "#4caf50");
    }
  }
};
</script>



<style scoped>
/* 기존 스타일 유지 */
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
  left: 30px;
  background: #111;
  padding: 15px;
  border-radius: 5px;
  color: white;
  z-index: 10;
  width: 320px;
}

.transaction-filters {
  margin-top: 10px;
  margin-bottom: 10px;
  display: flex;
  gap: 8px;
}

.transaction-filters button {
  background: #333;
  color: white;
  border: none;
  padding: 6px 10px;
  font-size: 12px;
  border-radius: 4px;
  cursor: pointer;
}

.transaction-filters button:hover {
  background: #555;
}

.date-picker {
  background: #1c1c1c;
  padding: 10px;
  border-radius: 6px;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.date-picker input[type="date"] {
  background: #2a2a2a;
  border: none;
  color: white;
  padding: 6px;
  border-radius: 4px;
}

.date-picker button {
  background: #444;
  border: none;
  padding: 6px 10px;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.date-picker button:hover {
  background: #666;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

th, td {
  border: 1px solid #444;
  padding: 5px;
  text-align: left;
}

.daily-chart-container {
  margin-top: 15px;
}

.chart-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.daily-chart {
  background: #1e1e1e;
  padding: 10px;
  border-radius: 6px;
}
</style>