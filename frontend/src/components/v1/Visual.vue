<template>
  <div class="visual-container">
    <!-- 1) 컨트롤 바 -->
    <div class="controls">
      <!-- TOKEN -->
      <div class="dropdown">
        <button @click="showTokenDropdown = !showTokenDropdown"
                :class="{ active: selectedToken }">
          {{ selectedToken || "TOKEN" }}
        </button>
        <div v-if="showTokenDropdown" class="dropdown-content scrollable">
          <button @click="selectToken(null)">전체</button>
          <button v-for="t in tokenList" :key="t" @click="selectToken(t)">{{ t }}</button>
        </div>
      </div>
      <!-- FILTER -->
      <div class="dropdown">
        <button @click="showFilterDropdown = !showFilterDropdown"
                :class="{ active: selectedFilter }">
          {{ selectedFilter || "FILTER" }}
        </button>
        <div v-if="showFilterDropdown" class="dropdown-content">
          <button @click="selectFilter(null)">전체</button>
          <button @click="selectFilter('거래 대금')">거래 대금</button>
          <button @click="selectFilter('트랜잭션량')">트랜잭션량</button>
          <button @click="selectFilter('최신순')">최신순</button>
        </div>
      </div>
      <!-- 검색 -->
      <input
          v-model="searchQuery"
          @keyup.enter="onSearchEnter"
          type="text"
          placeholder="Address 또는 TxHash 검색"
          class="search-input"
      />
      <!-- 상세보기 토글 -->
      <button
          class="wide-button detail-btn"
          :class="{ active: detailMode }"
          @click="toggleDetail"
      >상세보기</button>
    </div>

    <!-- 2) D3 그래프 영역 -->
    <div ref="chart" class="chart"></div>

    <!-- 3) 우클릭 디테일 패널 -->
    <div v-if="showDetailPanel" class="node-detail-panel">
      <div class="panel-header">
        <h3>노드 트랜잭션 내역 ({{ truncatedNodeId }})</h3>
        <button class="close-btn" @click="showDetailPanel = false">X</button>
      </div>
      <div class="header-info">
        <button class="copy-btn" @click="copyToClipboard(clickedNodeId)">📋</button>
      </div>

      <!-- 테이블 -->
      <div class="table-wrapper">
        <table class="tx-table wide-table">
          <thead>
          <tr>
            <th>TxHash</th>
            <th>type</th>
            <th>toAddress</th>
            <th>totalValue</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(tx, i) in paginatedTransactions" :key="i">
            <td>
              <div class="cell-with-copy-row">
                <span>{{ tx.txHash.slice(0,10) + "..." }}</span>
                <button class="copy-btn" @click="copyToClipboard(tx.txHash)">📋</button>
              </div>
            </td>
            <td>{{ tx.type }}</td>
            <td>
              <div class="cell-with-copy-row">
                <span>{{ tx.toAddress.slice(0,10) + "..." }}</span>
                <button class="copy-btn" @click="copyToClipboard(tx.toAddress)">📋</button>
              </div>
            </td>
            <td>{{ tx.totalValue }}</td>
          </tr>
          </tbody>
        </table>
      </div>
      <!-- 페이지네이션 -->
      <div class="pagination-controls">
        <button @click="prevPage" :disabled="currentPage===1">‹ Prev</button>
        <span>{{ currentPage }} / {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage===totalPages">Next ›</button>
      </div>

      <!-- 차트 -->
      <div class="chart-buttons">
        <button :class="{ active: currentTab==='daily' }" @click="switchTab('daily')">일간 거래량</button>
        <button :class="{ active: currentTab==='monthly' }" @click="switchTab('monthly')">월간 거래량</button>
      </div>
      <div class="date-filter">
        <select v-model="selectedYear">
          <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}</option>
        </select>년
        <select v-model="selectedMonth">
          <option v-for="m in monthOptions" :key="m" :value="m">{{ m }}</option>
        </select>월
      </div>
      <canvas ref="detailChartCanvas" width="400" height="200"></canvas>

      <!-- ✅ 추가: 날짜별 그래프 및 트리맵 -->
      <DateGraphToggle v-if="clickedNodeId" :address="clickedNodeId" />
    </div>
  </div>
</template>

<script>
import * as d3 from "d3";
import { Chart, BarController, BarElement, CategoryScale, LinearScale, Tooltip, Title } from "chart.js";
import GraphView from './GraphView.vue';

Chart.register(BarController, BarElement, CategoryScale, LinearScale, Tooltip, Title);

export default {
  name: "VisualView",
  components: { DateGraphToggle: GraphView },
  data() {
    return {
      // UI state
      tokenList: [], selectedToken: null, showTokenDropdown: false,
      selectedFilter: null, showFilterDropdown: false,
      searchQuery: "", detailMode: false,

      // D3 Graph State
      graphNodes: [], graphLinks: [],
      lastTxs: [], lastCentral: null,

      // Detail Panel
      showDetailPanel: false, clickedNodeId: "",
      nodeTransactionList: [], currentPage:1, rowsPerPage:10,

      // Chart State
      currentTab: "daily",
      selectedYear: new Date().getFullYear(),
      selectedMonth: new Date().getMonth()+1,
      yearOptions: [2024,2025,2026],
      monthOptions: Array.from({length:12},(_,i)=>i+1),
      detailChart: null,

      // D3 internals
      svg: null, g: null, sim: null, linkSel: null, nodeSel: null,
      zoom: null
    };
  },
  computed: {
    truncatedNodeId() {
      return this.clickedNodeId
          ? this.clickedNodeId.slice(0,10) + "..."
          : "";
    },
    paginatedTransactions() {
      const start = (this.currentPage-1)*this.rowsPerPage;
      return this.nodeTransactionList.slice(start, start+this.rowsPerPage);
    },
    totalPages() {
      return Math.ceil(this.nodeTransactionList.length / this.rowsPerPage);
    }
  },
  mounted() {
    this.renderTop1Graph();
  },
  methods: {
    /* TOKEN/FILTER stubs */
    selectToken(t) { this.selectedToken=t; this.showTokenDropdown=false; },
    selectFilter(f){ this.selectedFilter=f; this.showFilterDropdown=false; },

    /* 상세보기 토글 */
    toggleDetail() {
      this.detailMode = !this.detailMode;
      if (this.detailMode) this.renderDetailGraph();
      else                 this.renderTop1Graph();
    },

    /* 최초 & 검색 더미 100개 */
    renderTop1Graph() {
      const cent="0xTOP1ADDRESS";
      const txs=Array.from({length:100},()=>( {
        fromAddress:cent,
        toAddress:  "0x"+Math.random().toString(16).substr(2,6)
      }));
      this.lastCentral=cent; this.lastTxs=txs;
      this.initGraph(txs, cent, false);
    },
    onSearchEnter() {
      if (!this.searchQuery) return;
      const cent=this.searchQuery;
      const txs=Array.from({length:100},()=>( {
        fromAddress:cent,
        toAddress:  "0x"+Math.random().toString(16).substr(2,6)
      }));
      this.lastCentral=cent; this.lastTxs=txs;
      this.initGraph(txs, cent, false);
    },

    /* 상세보기용: 상위 10개만 필터 */
    renderDetailGraph() {
      const cent=this.lastCentral;
      const top10=this.lastTxs.slice(0,10);
      this.initGraph(top10, cent, true);
    },

    /* 좌클릭: expand/collapse */
    onNodeClick(d) {
      const node = this.graphNodes.find(n=>n.id===d.id);
      if (!node) return;
      if (node.expanded) {
        // collapse
        this.graphLinks = this.graphLinks.filter(l=>l.source.id!==d.id);
        this.graphNodes = this.graphNodes.filter(n=>n.parent!==d.id);
        node.expanded = false;
      } else {
        // expand 10 children
        const children = Array.from({length:10},(_,i)=>( {
          id:         `${d.id}_${i}`,
          parent:     d.id,
          fromAddress:d.id,
          toAddress:  `${d.id}_${i}`,
          r:          14,
          expanded:   false
        }));
        children.forEach(c=> this.graphNodes.push(c));
        children.forEach(c=> this.graphLinks.push({ source:d.id, target:c.id }));
        node.expanded = true;
      }
      this.updateGraph();
    },

    /* 우클릭: 상세 패널 */
    onNodeContextMenu(d, event) {
      event.preventDefault();
      this.clickedNodeId = d.id;
      this.nodeTransactionList = Array.from({length:10},(_,i)=>( {
        txHash:    "0x"+Math.random().toString(16).substr(2,8),
        type:      i%2? "입금":"출금",
        toAddress: "0x"+Math.random().toString(16).substr(2,6),
        totalValue:(Math.random()*100).toFixed(2)
      }));
      this.currentPage = 1;
      this.showDetailPanel = true;
      this.$nextTick(this.loadDetailChart);
    },

    /* 페이징 */
    prevPage(){ if(this.currentPage>1) this.currentPage--; },
    nextPage(){ if(this.currentPage<this.totalPages) this.currentPage++; },

    /* 차트 토글 */
    switchTab(tab){ this.currentTab=tab; this.loadDetailChart(); },
    loadDetailChart(){
      const labels = this.currentTab==="daily"
          ? Array.from({length:31},(_,i)=>`${i+1}일`)
          : ["1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"];
      const data = labels.map(()=>Math.round(Math.random()*100));
      if (this.detailChart) this.detailChart.destroy();
      this.detailChart = new Chart(this.$refs.detailChartCanvas, {
        type: "bar",
        data: { labels, datasets:[{ label:this.currentTab==="daily"?"일간 거래량":"월간 거래량", data }] },
        options:{
          responsive:false,
          scales:{ x:{ ticks:{ color:"#fff" }}, y:{ beginAtZero:true, ticks:{ color:"#fff" } } },
          plugins:{
            title:{ display:true, text:`${this.selectedYear}년 ${this.selectedMonth}월`, color:"#fff" },
            legend:{ labels:{ color:"#fff" } }
          }
        }
      });
    },

    /* 초기/상세 뷰 공통: nodes & links 세팅 */
    initGraph(transactions, central, isDetail) {
      this.graphNodes = [{
        id: central,
        r: 20,
        fromAddress: central,
        toAddress: central,
        expanded: false
      }];
      this.graphLinks = [];
      transactions.forEach(tx=> {
        this.graphNodes.push({
          id: tx.toAddress,
          fromAddress: tx.fromAddress,
          toAddress:   tx.toAddress,
          r: isDetail? 18 : 14,
          expanded: false
        });
        this.graphLinks.push({ source: tx.fromAddress, target: tx.toAddress });
      });
      this.updateGraph();
    },

    /* D3 data-join + simulation start/restart + zoom fix */
    updateGraph() {
      const C = this.$refs.chart;
      const W = C.clientWidth, H = C.clientHeight;

      // first-time setup
      if (!this.svg) {
        this.svg = d3.select(C)
            .append("svg").attr("width",W).attr("height",H).style("background","#000");

        // store zoom behavior
        this.zoom = d3.zoom()
            .scaleExtent([0.3,5])
            .on("zoom", (e) => this.g.attr("transform", e.transform));

        this.g = this.svg.append("g");
        this.svg.call(this.zoom);

        this.sim = d3.forceSimulation()
            .force("link", d3.forceLink().id(d=>d.id).distance(100))
            .force("charge", d3.forceManyBody().strength(-800))
            .force("center", d3.forceCenter(W/2,H/2));
      } else {
        // re-apply zoom on every update
        this.svg.call(this.zoom);
      }

      // links join
      this.linkSel = this.g.selectAll("path.link")
          .data(this.graphLinks, d=>d.source.id+"→"+d.target.id);
      this.linkSel.exit().remove();
      this.linkSel = this.linkSel.enter()
          .append("path").classed("link",true)
          .attr("stroke","#999").attr("fill","none")
          .attr("marker-end","url(#arrowhead)")
          .merge(this.linkSel);

      // nodes join
      this.nodeSel = this.g.selectAll("g.node")
          .data(this.graphNodes, d=>d.id);
      this.nodeSel.exit().remove();
      const enter = this.nodeSel.enter()
          .append("g").classed("node",true)
          .style("cursor","pointer")
          .on("click",(e,d)=>this.onNodeClick(d))
          .on("contextmenu",(e,d)=>this.onNodeContextMenu(d,e));
      enter.append("circle");
      enter.append("text");
      this.nodeSel = enter.merge(this.nodeSel);

      // circle attrs
      this.nodeSel.select("circle")
          .attr("r", d=>d.r)
          .attr("fill", d=> d.id===this.lastCentral? "#FF9800":"#64b5f6");

      // labels
      this.nodeSel.select("text")
          .attr("text-anchor","middle")
          .attr("fill","#fff")
          .style("font-size","10px")
          .selectAll("*").remove();
      const txt = this.nodeSel.select("text");
      txt.append("tspan")
          .attr("x",0).attr("dy",d=>-(d.r+4))
          .text(d=>`from:${d.fromAddress.slice(0,6)}...`);
      txt.append("tspan")
          .attr("x",0).attr("dy","1.2em")
          .text(d=>`to:${d.toAddress.slice(0,6)}...`);

      // restart simulation
      this.sim.nodes(this.graphNodes);
      this.sim.force("link").links(this.graphLinks);
      this.sim.alpha(1).restart();
      this.sim.on("tick",()=> {
        this.linkSel.attr("d", d=>{
          const sx=d.source.x, sy=d.source.y;
          const tx=d.target.x, ty=d.target.y;
          const dx=tx-sx, dy=ty-sy, L=Math.hypot(dx,dy)||1;
          const ux=dx/L, uy=dy/L;
          const x1=sx+ux*d.source.r, y1=sy+uy*d.source.r;
          const x2=tx-ux*d.target.r, y2=ty-uy*d.target.r;
          return `M${x1},${y1}L${x2},${y2}`;
        });
        this.nodeSel.attr("transform", d=>`translate(${d.x},${d.y})`);
      });
    },

    copyToClipboard(v) {
      navigator.clipboard.writeText(v);
    }
  }
};
</script>

<style scoped>
.visual-container {
  position: relative; width:100%; height:100vh;
  background:#000; overflow:hidden;
}
.controls {
  position:absolute; top:10px; left:10px; z-index:10;
  display:flex; gap:10px; align-items:center;
}
.dropdown { position:relative; }
.dropdown-content {
  position:absolute; top:100%; left:0;
  background:#222; color:#fff;
  border-radius:4px; padding:5px;
}
.scrollable { max-height:200px; overflow-y:auto; }
button {
  background:#444; color:#fff; border:none;
  padding:6px 12px; border-radius:4px; cursor:pointer;
}
button.active { background:#ff9800; }
.wide-button { width:80px; }
.search-input {
  background:#222; color:#fff; border:none;
  padding:6px 12px; border-radius:4px;
}
.detail-btn { margin-left:5px; }

/* 차트 영역 */
.chart {
  position:absolute; top:0; left:0;
  width:100%; height:100%; z-index:0;
}

/* 상세 패널 */
.node-detail-panel {
  position:absolute; top:60px; left:10px;
  z-index:20; background:#1e1e1e; color:#fff;
  padding:15px; width:800px; max-height:80%;
  overflow-y:auto; border:1px solid #333;
  border-radius:5px;
}
.panel-header {
  display:flex; justify-content:space-between;
  margin-bottom:8px;
}
.close-btn {
  background:none; border:none; color:#fff;
  font-size:16px; cursor:pointer;
}
.header-info {
  display:flex; align-items:center; gap:6px;
  margin-bottom:12px;
}

/* 표 */
.table-wrapper { overflow-x:auto; margin-bottom:8px; }
.tx-table { border-collapse:collapse; }
.wide-table { width:1200px; }
.tx-table th, .tx-table td {
  padding:8px 6px; border:1px solid #333;
  text-align:center;
}
.tx-table th { background:#333; }

/* 복사 버튼 inline */
.cell-with-copy-row {
  display:flex; align-items:center; gap:4px;
}

/* 페이지네이션 */
.pagination-controls {
  display:flex; align-items:center; gap:8px;
  margin:4px 0 12px;
}
.pagination-controls button {
  background:#444; color:#fff; border:none;
  padding:4px 8px; border-radius:3px; cursor:pointer;
}
.pagination-controls button:disabled {
  opacity:0.5; cursor:default;
}

/* 차트 탭 */
.chart-buttons {
  display:flex; gap:8px; margin-bottom:8px;
}
.chart-buttons button {
  padding:4px 8px; background:#444; border:none;
  color:#fff; border-radius:3px; cursor:pointer;
}
.chart-buttons button.active { background:#ff9800; }
.date-filter {
  margin-bottom:8px;
}
.date-filter select {
  margin-right:4px; padding:4px;
  background:#333; color:#fff; border:1px solid #666;
}
</style>
