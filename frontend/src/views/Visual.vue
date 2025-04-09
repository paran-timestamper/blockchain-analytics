<template>
  <div class="app-container">
    <!-- TOKEN & FILTER UI -->
    <div class="controls">
      <div class="dropdown">
        <button @click="toggleTokenDropdown" :class="{ active: selectedToken.name }">
          {{ selectedToken.name || "TOKEN" }}
        </button>
        <div v-if="showTokenDropdown" class="dropdown-content scrollable">
          <!-- TOKEN 리스트 -->
          <button v-for="token in tokenList" :key="token" @click="selectToken(token)">
            {{ token }}
          </button>
        </div>
      </div>
      <div class="dropdown">
        <button @click="checkTokenBeforeFilter" :class="{ active: selectedFilter.name }" class="wide-button">
          {{ selectedFilter.name || "FILTER" }}
        </button>
        <div v-if="showFilterDropdown" class="dropdown-content">
          <button class="wide-button" @click="selectFilter('거래 대금')">거래 대금</button>
          <button class="wide-button" @click="selectFilter('트랜잭션량')">트랜잭션량</button>
          <button class="wide-button" @click="selectFilter('최신순')">최신순</button>
        </div>
      </div>

      <!-- Address / TxHash 검색창 -->
      <input
          v-model="searchQuery"
          @keyup.enter="searchAddressOrTx"
          type="text"
          placeholder="Address 또는 TxHash 검색"
          class="search-input"
      />
    </div>

    <!-- D3 Graph -->
    <div ref="chart" class="chart"></div>

    <!-- 경고창 -->
    <div v-if="showWarning" class="warning-overlay">
      <div class="warning-message">
        <p>먼저 TOKEN을 선택해 주세요.</p>
        <button @click="closeWarning">확인</button>
      </div>
    </div>
  </div>
</template>

<script>
import * as d3 from "d3";

export default {
  data() {
    return {
      selectedToken: { name: "", clicked: false },
      selectedFilter: { name: "", clicked: false },
      showTokenDropdown: false,
      showFilterDropdown: false,
      tokenList: [],
      nodes: [],
      links: [],
      svg: null,
      searchQuery: "",
      showWarning: false,
    };
  },
  mounted() {
    this.fetchTokenList(); // 초기 토큰 리스트 불러오기
  },
  methods: {
    async fetchTokenList() {
      try {
        const response = await fetch("http://localhost:8080/api/common/tokens", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({}),
        });
        if (!response.ok) throw new Error("Failed to fetch tokens");
        const data = await response.json();
        console.log("data", data);
        this.tokenList = data;
      } catch (error) {
        console.error("Error fetching tokens:", error);
        this.tokenList = [];
      }
    },
    toggleTokenDropdown() {
      this.showTokenDropdown = !this.showTokenDropdown;
      if (!this.tokenList.length) {
        this.fetchTokenList();
      }
    },
    toggleFilterDropdown() {
      this.showFilterDropdown = !this.showFilterDropdown;
    },
    selectToken(token) {
      this.selectedToken = { name: token, clicked: true };
      this.showTokenDropdown = false;
      this.checkAndDrawGraph();
    },
    selectFilter(filter) {
      this.selectedFilter = { name: filter, clicked: true };
      this.showFilterDropdown = false;
      this.checkAndDrawGraph();
    },
    checkTokenBeforeFilter() {
      if (!this.selectedToken.clicked) {
        this.showWarning = true;
      } else {
        this.toggleFilterDropdown();
      }
    },
    closeWarning() {
      this.showWarning = false;
    },
    async checkAndDrawGraph() {
      if (this.selectedToken.clicked && this.selectedFilter.clicked) {
        const requestBody = {
          token: this.selectedToken.name,
          address: "cosmos1t5u0jfg3ljsjrh2m9e47d4ny2hea7eehxrzdgd",//this.searchQuery || "",
        };


        if (this.selectedFilter.name === "거래 대금") {
          await this.Transaction_price({
            requestBody,
            //노션보고 경로 보면서 짜고 매개변수로 TOKEN과 address를 어떻게 넘길지 작성.
          })
        } else if (this.selectedFilter.name === "트랜잭션량") {
          await this.Transaction_volume( {
            requestBody,
          })
        } else if (this.selectedFilter.name === "최신순") {
          await this.up_to_date_order({
              requestBody,
            //노션보고 경로 보면서 짜고 매개변수로 TOKEN과 address를 어떻게 넘길지 작성.
            })
        }
      }
    },

    async Transaction_price({ requestBody }) {
      try {
        const response = await fetch('http://localhost:8080/api/visual/nodeInfos/latest', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            "token" : requestBody.token,
            "fromAddress" : requestBody.fromAddress,
          }),
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        console.log("data" , data);
        this.drawGraph(data);
      } catch (error) {
        console.error('Latest news fetch failed:', error);
      }
      // 거래대금 API 짜서 거래대금과 TOKEN 기준으로 address 100개 받아서 노드로 그려야 함.
      // const data = await res.json(); this.drawGraph(data);
    },
    async Transaction_volume({requestBody}) {
      try {
        const response = await fetch('http://localhost:8080/api/visual/nodeInfos/latest', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            "token" : requestBody.token,
            "fromAddress" : requestBody.fromAddress,
          }),
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        console.log("data" , data);
        this.drawGraph(data);

      } catch (error) {
        console.error('Latest news fetch failed:', error);
      }
      //트랜잭션량 API 쏴서 트랜잭션량과 TOKEN 기준으로 address 100개 받아서 노드로 그려야 함.
      // const data = await res.json(); this.drawGraph(data);
    },
    async up_to_date_order({requestBody}) {
      try {
        const response = await fetch('http://localhost:8080/api/visual/nodeInfos/latest', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            "token" : requestBody.token,
            "fromAddress" : requestBody.fromAddress,
          }),
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        console.log("data" , data);
        this.drawGraph(data);

      } catch (error) {
        console.error('Latest news fetch failed:', error);
      }
      //최신순 API 쏴서 최신순과 TOKEN 기준으로 address 100개 받아서 노드로 그려야 함.
      // const data = await res.json(); this.drawGraph(data);
    },




    drawGraph(data) {
      this.nodes = [];
      this.links = [];

      const centralNodeId = this.searchQuery || this.selectedToken.name;
      const central = {
        id: centralNodeId,
        label: centralNodeId,
        group: 0,
        size: 30,
      };
      this.nodes.push(central);

      const relatedNodes = data.slice(0, 100);
      for (let i = 0; i < relatedNodes.length; i++) {
        const tx = relatedNodes[i];
        if (tx.address === centralNodeId) continue;

        this.nodes.push({
          id: tx.address,
          label: tx.address,
          group: 1,
          size: 12,
          txhash: tx.txhash,
        });

        this.links.push({
          source: centralNodeId,
          target: tx.address,
        });
      }

      this.$nextTick(this.createD3Graph);
    },
    createD3Graph() {
      const container = this.$refs.chart;
      const width = container.clientWidth || window.innerWidth;
      const height = container.clientHeight || window.innerHeight;

      d3.select(container).select("svg").remove();

      this.svg = d3.select(container)
          .append("svg")
          .attr("width", width)
          .attr("height", height)
          .style("background", "#000");

      const simulation = d3.forceSimulation(this.nodes)
          .force("link", d3.forceLink(this.links).id(d => d.id).distance(150))
          .force("charge", d3.forceManyBody().strength(-800))
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
          .attr("fill", d => d.group === 0 ? "#FF9800" : "#64b5f6")
          .style("cursor", "pointer")
          .on("click", (event, d) => {
            if (d.group !== 0) {
              alert(`Tx Hash: ${d.txhash}\nAddress: ${d.id}`);
            }
          });

      const label = this.svg.selectAll("text")
          .data(this.nodes)
          .enter().append("text")
          .text(d => d.label)
          .attr("fill", "white")
          .attr("font-size", "10px")
          .attr("text-anchor", "middle");

      simulation.on("tick", () => {
        link
            .attr("x1", d => d.source.x)
            .attr("y1", d => d.source.y)
            .attr("x2", d => d.target.x)
            .attr("y2", d => d.target.y);

        node
            .attr("cx", d => d.x)
            .attr("cy", d => d.y);

        label
            .attr("x", d => d.x)
            .attr("y", d => d.y - d.size - 2);
      });
    },
    async searchAddressOrTx() {
      if (!this.searchQuery) return;
      alert(`검색된 값: ${this.searchQuery}`);
    },
  },
};
</script>



<style scoped>
.app-container {
  position: relative;
  width: 100%;         /* ✅ 100%로 수정 */
  height: 100vh;
  background: black;
  overflow: hidden;
}
.controls {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 10px;
  z-index: 100;
  align-items: center;
}
.dropdown {
  position: relative;
}
.dropdown-content {
  position: absolute;
  top: 100%;
  left: 0;
  background: #222;
  color: white;
  border-radius: 5px;
  padding: 5px;
  z-index: 100;
  display: flex;
  flex-direction: column;
}
.scrollable {
  max-height: 250px;
  overflow-y: auto;
}
button {
  padding: 8px 12px;
  background: #444;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
  border-radius: 5px;
  text-align: left;
}
button.active {
  background: #ff9800;
}
button.wide-button {
  width: 120px;
  text-align: center;
}
.search-input {
  padding: 8px 12px;
  font-size: 14px;
  border-radius: 5px;
  border: none;
  background: #222;
  color: white;
  width: 220px;
}

.chart {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.warning-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.warning-message {
  background: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  width: 300px; /* 경고창 크기 수정 */
  max-width: 90%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.warning-message p {
  font-size: 16px; /* 글씨 크기 수정 */
  margin-bottom: 20px;
  color: black; /* 글씨 색을 검정색으로 지정 */
}

.warning-message button {
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #FF9800; /* 버튼 색상 수정 */
  border: none;
  color: white;
  font-size: 16px; /* 글씨 크기 수정 */
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.warning-message button:hover {
  background-color: #f57c00; /* 버튼 호버 색상 수정 */
}

</style>
