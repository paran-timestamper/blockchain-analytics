<template>
  <div class="graph-wrapper">
    <div class="graph-controls">
      <div class="dropdown">
        <button class="control-button" @click="toggleTokenDropdown">
          {{ selectedToken || 'TOKEN' }}
        </button>
        <div v-if="showTokenDropdown" class="dropdown-menu">
          <button v-for="token in tokenOptions" :key="token" @click="selectToken(token)">
            {{ token }}
          </button>
        </div>
      </div>
      <div class="dropdown">
        <button class="control-button" @click="toggleFilterDropdown">
          {{ selectedFilter || 'FILTER' }}
        </button>
        <div v-if="showFilterDropdown" class="dropdown-menu">
          <button v-for="filter in filterOptions" :key="filter" @click="selectFilter(filter)">
            {{ filter }}
          </button>
        </div>
      </div>
      <input class="search-input" v-model="searchQuery" @keyup.enter="onSearchEnter" placeholder="Search Address" />
    </div>

    <div ref="chart" class="chart"></div>

    <div v-if="popup.visible" class="popup-overlay">
      <div class="popup-content">
        <button class="close-button" @click="closePopup">×</button>
        <div class="popup-grid">
          <div class="popup-box popup-box1a">
            <TransactionTable :address="popup.nodeAddress" />
          </div>
          <div class="popup-box popup-box2a">
            <HeatmapView :address="popup.nodeAddress" />
          </div>
          <div class="popup-box popup-box3">
            <div class="toggle-buttons">
              <button @click="currentChart='broken'" :class="{ active: currentChart==='broken' }">Broken Line</button>
              <button @click="currentChart='tree'" :class="{ active: currentChart==='tree' }">Treemap</button>
            </div>
            <BrokenLineGraph v-if="currentChart==='broken'" :address="popup.nodeAddress" />
            <Treemap v-else :address="popup.nodeAddress" />
          </div>
          <div class="popup-box popup-box1b">
            <MonthDayGraph :address="popup.nodeAddress" />
          </div>
          <div class="popup-box popup-box2b">
            <div class="scrollable-table">
              <HeatmapTable :address="popup.nodeAddress" />
            </div>
          </div>
          <div class="popup-box popup-box4-news">
            <Home :address="popup.nodeAddress" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as d3 from "d3";
import TransactionTable from '@/components/TransactionTable.vue';
import MonthDayGraph from '@/components/MonthDayGraph.vue';
import HeatmapView from '@/components/HeatmapView.vue';
import HeatmapTable from '@/components/HeatmapTable.vue';
import BrokenLineGraph from '@/components/BrokenLineGraph.vue';
import Treemap from '@/components/Treemap.vue';
import Home from '@/components/Home.vue';

export default {
  name: "NodeGraph",
  components: {
    TransactionTable,
    MonthDayGraph,
    HeatmapView,
    HeatmapTable,
    BrokenLineGraph,
    Treemap,
    Home
  },
  data() {
    return {
      svg: null,
      g: null,
      zoom: null,
      nodeRadius: 18,
      centerNode: { id: "0xCENTER", r: 40 },
      showTokenDropdown: false,
      showFilterDropdown: false,
      selectedToken: '',
      selectedFilter: '',
      searchQuery: '',
      tokenOptions: ['ETH', 'BTC', 'USDT'],
      filterOptions: ['거래 대금', '트랜잭션량', '최신순'],
      popup: {
        visible: false,
        nodeAddress: ''
      },
      currentChart: 'broken'
    };
  },
  mounted() {
    this.generateMockData();
  },
  methods: {
    toggleTokenDropdown() {
      this.showTokenDropdown = !this.showTokenDropdown;
      this.showFilterDropdown = false;
    },
    toggleFilterDropdown() {
      this.showFilterDropdown = !this.showFilterDropdown;
      this.showTokenDropdown = false;
    },
    selectToken(token) {
      this.selectedToken = token;
      this.showTokenDropdown = false;
      this.generateMockData();
    },
    selectFilter(filter) {
      this.selectedFilter = filter;
      this.showFilterDropdown = false;
      this.generateMockData();
    },
    onSearchEnter() {
      if (!this.searchQuery) return;
      this.generateMockData(this.searchQuery);
    },
    generateMockData(centerId = this.centerNode.id) {
      const colors = ['#FF6E6E', '#78EFAD', '#FFFA78'];
      const txs = Array.from({ length: 100 }, (_, i) => ({
        toAddress: "0x" + Math.random().toString(16).substr(2, 6),
        value: Math.floor(Math.random() * 10000),
        color: colors[i % 3]
      })).sort((a, b) => b.value - a.value);

      this.drawGraph(centerId, txs);
    },
    drawGraph(centerId, txList) {
      const C = this.$refs.chart;
      const W = C.clientWidth || 800;
      const H = C.clientHeight || 600;
      const centerX = W / 2;
      const centerY = H / 2;

      d3.select(C).select("svg").remove();
      const svg = d3.select(C).append("svg")
          .attr("width", W)
          .attr("height", H)
          .style("background", "#000");

      const g = svg.append("g");
      svg.call(d3.zoom().scaleExtent([0.3, 5]).on("zoom", (e) => g.attr("transform", e.transform)));

      const edgesGroup = g.append("g");
      const nodesGroup = g.append("g");

      nodesGroup.append("circle")
          .attr("cx", centerX)
          .attr("cy", centerY)
          .attr("r", this.centerNode.r)
          .attr("fill", "#FF9800")
          .attr("stroke", "white")
          .attr("stroke-width", 2);

      const clusterCount = 10;
      const nodesPerCluster = 10;
      const outerRadius = 500;

      for (let i = 0; i < clusterCount; i++) {
        const angle = (i / clusterCount) * 2 * Math.PI;
        const clusterCx = centerX + outerRadius * Math.cos(angle);
        const clusterCy = centerY + outerRadius * Math.sin(angle);

        const clusterNodes = Array.from({ length: nodesPerCluster }, (_, j) => ({
          id: `node-${i}-${j}`,
          ...txList[i * nodesPerCluster + j],
          x: clusterCx + Math.random() * 30,
          y: clusterCy + Math.random() * 30
        }));

        const simulation = d3.forceSimulation(clusterNodes)
            .force("collide", d3.forceCollide(this.nodeRadius * 1.5))
            .force("x", d3.forceX(clusterCx).strength(0.05))
            .force("y", d3.forceY(clusterCy).strength(0.05))
            .stop();

        for (let k = 0; k < 120; ++k) simulation.tick();

        clusterNodes.forEach(d => {
          edgesGroup.append("line")
              .attr("x1", centerX)
              .attr("y1", centerY)
              .attr("x2", d.x)
              .attr("y2", d.y)
              .attr("stroke", "#888")
              .attr("stroke-width", 1);

          nodesGroup.append("circle")
              .datum(d)
              .attr("cx", d.x)
              .attr("cy", d.y)
              .attr("r", this.nodeRadius)
              .attr("fill", d.color)
              .attr("stroke", "#222")
              .attr("stroke-width", 1)
              .on("click", () => this.onNodeClick(d))
              .on("contextmenu", (e) => this.onNodeContextMenu(e, d));
        });
      }
    },
    onNodeClick() {
      this.generateMockData();
    },
    onNodeContextMenu(e, d) {
      e.preventDefault();
      this.popup.visible = true;
      this.popup.nodeAddress = d.toAddress;
    },
    closePopup() {
      this.popup.visible = false;
    }
  }
};
</script>


<style scoped>
.graph-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}
.chart {
  width: 100%;
  height: 100%;
  background: #000;
}
.graph-controls {
  position: absolute;
  top: 20px;
  left: 20px;
  display: flex;
  gap: 10px;
  z-index: 100;
}
.control-button {
  background: #2b303a;
  color: #c7d2fe;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: #20242b;
  border: 1px solid #444;
  border-radius: 6px;
  margin-top: 4px;
  padding: 6px;
  display: flex;
  flex-direction: column;
  z-index: 999;
}
.search-input {
  background: #1e1e1e;
  border: 1px solid #555;
  color: #fff;
  padding: 8px 12px;
  border-radius: 6px;
}
.popup-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.2);
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  overflow: auto;
}
.popup-content {
  width: 100%;
  max-height: 100%;
  background: transparent;
  padding: 10px;
  box-sizing: border-box;
  position: relative;
  overflow-y: auto;
}
.close-button {
  position: absolute;
  top: 12px;
  right: 20px;
  background: none;
  border: none;
  font-size: 32px;
  color: #fff;
  cursor: pointer;
}
.popup-grid {
  display: grid;
  grid-template-areas:
    "area1a area2a area3"
    "area1b area2b area2b"
    "area4  area4  area4";
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: auto auto 1fr;
  gap: 10px;
  height: auto;
}
.popup-box {
  border-radius: 10px;
  padding: 10px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(42, 42, 42, 0.8);
}
.scrollable-table {
  max-height: 250px;
  overflow-y: auto;
  width: 100%;
}
.popup-box1a { grid-area: area1a; min-height: 350px; }
.popup-box2a { grid-area: area2a; }
.popup-box3  { grid-area: area3; }
.popup-box1b { grid-area: area1b; }
.popup-box2b { grid-area: area2b; }
.popup-box4-news {
  grid-area: area4;
  flex-direction: column;
  align-items: flex-start;
  overflow-y: auto;
  max-height: 100%;
  padding-right: 5px;
}
</style>
