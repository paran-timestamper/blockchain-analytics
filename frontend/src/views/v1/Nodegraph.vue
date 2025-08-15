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
import TransactionTable from '@/components/v1/TransactionTable.vue';
import MonthDayGraph from '@/components/v1/MonthDayGraph.vue';
import HeatmapView from '@/components/v1/HeatmapView.vue';
import HeatmapTable from '@/components/v1/HeatmapTable.vue';
import BrokenLineGraph from '@/components/v1/BrokenLineGraph.vue';
import Treemap from '@/components/v1/Treemap.vue';
import Home from '@/components/v1/Home.vue';

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
      centerNodeTemplate: {
        r: 40,
        color: "#FF9800",
        depth: 0,
        cluster: -1
      },
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
      currentChart: 'broken',
      simulation: null,
      allData: [],
      clusterCenters: [],
      expandedNodes: new Set()
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
    generateMockData(centerAddress = "0xCENTER") {
      const chart = this.$refs.chart;
      const width = chart.clientWidth || 800;
      const height = chart.clientHeight || 600;
      const colors = ['#FF6E6E', '#78EFAD', '#FFFA78'];

      const center = {
        ...this.centerNodeTemplate,
        toAddress: centerAddress,
        id: centerAddress,
        fx: width / 2,
        fy: height / 2
      };

      const nodes = [center];
      const links = [];

      const rawNodes = Array.from({ length: 100 }, (_, i) => {
        const toAddress = '0x' + Math.random().toString(16).substr(2, 40).padEnd(40, '0');
        return {
          id: toAddress,
          toAddress,
          fromAddress: center.toAddress,
          txHash: '0x' + Math.random().toString(16).substr(2, 64).padEnd(64, '0'),
          value: Math.floor(Math.random() * 10000),
          color: colors[i % 3],
          r: 18,
          depth: 1,
          cluster: Math.floor(i / 10)
        };
      }).sort((a, b) => b.value - a.value);

      rawNodes.forEach((node) => {
        nodes.push(node);
        links.push({ source: center.id, target: node.id });
      });

      const clusterCenters = [];
      const clusterCount = 10;
      const radius = 900;
      for (let i = 0; i < clusterCount; i++) {
        const angle = (2 * Math.PI * i) / clusterCount - Math.PI / 2;
        clusterCenters.push({
          x: width / 2 + radius * Math.cos(angle),
          y: height / 2 + radius * Math.sin(angle),
          label: `${i * 10 + 1}~${(i + 1) * 10}`
        });
      }

      this.clusterCenters = clusterCenters;
      this.allData = { nodes, links };
      this.drawForceGraph();
    },
    drawForceGraph() {
      const chart = this.$refs.chart;
      const width = chart.clientWidth;
      const height = chart.clientHeight;

      d3.select(chart).select("svg").remove();
      const svg = d3.select(chart)
          .append("svg")
          .attr("width", width)
          .attr("height", height)
          .style("background", "#000");

      const g = svg.append("g");
      svg.call(d3.zoom().on("zoom", (e) => g.attr("transform", e.transform)));

      const link = g.selectAll("line")
          .data(this.allData.links)
          .join("line")
          .attr("stroke", "#888")
          .attr("stroke-width", 1);

      const node = g.selectAll("circle")
          .data(this.allData.nodes)
          .join("circle")
          .attr("r", d => d.r)
          .attr("fill", d => d.color)
          .attr("stroke", "#222")
          .attr("stroke-width", 1)
          .on("click", this.onNodeClick)
          .on("contextmenu", (e, d) => this.onNodeContextMenu(e, d))
          .call(d3.drag()
              .on("start", this.dragstarted)
              .on("drag", this.dragged)
              .on("end", this.dragended));

      // 변경: 군집 중심을 중심에 더 가깝게 배치
      const clusterCount = 10;
      const radius = 600;  // 변경된 반지름
      this.clusterCenters = Array.from({ length: clusterCount }, (_, i) => {
        const angle = (2 * Math.PI * i) / clusterCount - Math.PI / 2;
        return {
          x: width / 2 + radius * Math.cos(angle),
          y: height / 2 + radius * Math.sin(angle),
          label: `${i * 10 + 1}~${(i + 1) * 10}`
        };
      });

      this.simulation = d3.forceSimulation(this.allData.nodes)
          .force("link", d3.forceLink(this.allData.links).id(d => d.id).distance(100))
          .force("charge", d3.forceManyBody().strength(-250))
          .force("center", d3.forceCenter(width / 2, height / 2))
          .force("collide", d3.forceCollide().radius(d => d.r + 2).strength(0.9))
          .force("clusterX", d3.forceX(d => this.clusterCenters[d.cluster]?.x || width / 2).strength(0.7))
          .force("clusterY", d3.forceY(d => this.clusterCenters[d.cluster]?.y || height / 2).strength(0.7))
          .on("tick", () => {
            link
                .attr("x1", d => d.source.x)
                .attr("y1", d => d.source.y)
                .attr("x2", d => d.target.x)
                .attr("y2", d => d.target.y);

            node
                .attr("cx", d => d.x)
                .attr("cy", d => d.y);
          });

      g.selectAll(".cluster-label").remove();
      g.selectAll(".cluster-label")
          .data(this.clusterCenters)
          .enter()
          .append("text")
          .attr("class", "cluster-label")
          .attr("x", d => d.x)
          .attr("y", d => d.y + 20)  // 변경: 더 아래쪽으로 라벨 표시
          .attr("fill", "#fff")
          .attr("text-anchor", "middle")
          .attr("font-size", "12px")
          .text(d => d.label);
    },


    onNodeClick(event, node) {
      const key = node.id;
      if (this.expandedNodes.has(key)) {
        this.allData.nodes = this.allData.nodes.filter(n => !(n.depth === 2 && n.fromAddress === key));
        this.allData.links = this.allData.links.filter(l => l.source !== key);
        this.expandedNodes.delete(key);
      } else {
        const newNodes = [], newLinks = [];
        for (let i = 0; i < 10; i++) {
          const id = '0x' + Math.random().toString(16).substr(2, 40).padEnd(40, '0');
          newNodes.push({
            id,
            toAddress: id,
            fromAddress: key,
            value: Math.floor(Math.random() * 10000),
            color: ['#FF6E6E', '#78EFAD', '#FFFA78'][i % 3],
            r: 18,
            depth: 2
          });
          newLinks.push({ source: key, target: id });
        }
        this.allData.nodes.push(...newNodes);
        this.allData.links.push(...newLinks);
        this.expandedNodes.add(key);
      }
      this.drawForceGraph();
    },
    dragstarted(event, d) {
      if (!event.active) this.simulation.alphaTarget(0.3).restart();
      d.fx = d.x;
      d.fy = d.y;
    },
    dragged(event, d) {
      d.fx = event.x;
      d.fy = event.y;
    },
    dragended(event, d) {
      if (!event.active) this.simulation.alphaTarget(0);
      d.fx = null;
      d.fy = null;
    },
    onNodeContextMenu(e, d) {
      e.preventDefault();
      this.popup.visible = true;
      this.popup.nodeAddress = d.toAddress || d.id;
    },
    closePopup() {
      this.popup.visible = false;
    }
  }
};
</script>




<style scoped>
.graph-wrapper { position: relative; width: 100%; height: 100%; }
.chart { width: 100%; height: 100%; background: #000; }
.graph-controls { position: absolute; top: 20px; left: 20px; display: flex; gap: 10px; z-index: 100; }
.control-button { background: #2b303a; color: #c7d2fe; padding: 8px 12px; border: none; border-radius: 6px; cursor: pointer; }
.dropdown-menu { position: absolute; top: 100%; left: 0; background: #20242b; border: 1px solid #444; border-radius: 6px; margin-top: 4px; padding: 6px; display: flex; flex-direction: column; z-index: 999; }
.search-input { background: #1e1e1e; border: 1px solid #555; color: #fff; padding: 8px 12px; border-radius: 6px; }
.popup-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.2); z-index: 1000; display: flex; justify-content: center; align-items: flex-start; overflow: auto; }
.popup-content { width: 100%; max-height: 100%; background: transparent; padding: 10px; box-sizing: border-box; position: relative; overflow-y: auto; }
.close-button { position: absolute; top: 12px; right: 20px; background: none; border: none; font-size: 32px; color: #fff; cursor: pointer; }
.popup-grid { display: grid; grid-template-areas: "area1a area2a area3" "area1b area2b area2b" "area4  area4  area4"; grid-template-columns: 1fr 1fr 1fr; grid-template-rows: auto auto 1fr; gap: 10px; height: auto; }
.popup-box { border-radius: 10px; padding: 10px; color: white; display: flex; align-items: center; justify-content: center; background-color: rgba(42, 42, 42, 0.8); }
.scrollable-table { max-height: 250px; overflow-y: auto; width: 100%; }
.popup-box1a { grid-area: area1a; min-height: 350px; }
.popup-box2a { grid-area: area2a; }
.popup-box3  { grid-area: area3; }
.popup-box1b { grid-area: area1b; }
.popup-box2b { grid-area: area2b; }
.popup-box4-news { grid-area: area4; flex-direction: column; align-items: flex-start; overflow-y: auto; max-height: 100%; padding-right: 5px; }
</style>
