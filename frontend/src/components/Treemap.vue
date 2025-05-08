<template>
  <div class="treemap-container">
    <h3>Top 16 Token Treemap ({{ address }})</h3>
    <div ref="chart" class="treemap-chart"></div>
  </div>
</template>

<script>
import * as d3 from "d3";

export default {
  name: "Treemap",
  props: {
    address: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      tokenData: []
    };
  },
  mounted() {
    this.fetchTreemapData();
  },
  methods: {
    async fetchTreemapData() {
      try {
        const res = await fetch(`http://localhost:8080/api/treemap?address=${this.address}`);
        const data = await res.json();

        this.tokenData = data.slice(0, 16);
        this.drawTreemap();
      } catch (err) {
        console.error("Treemap 데이터 로딩 실패:", err);
      }
    },
    drawTreemap() {
      const width = 600;
      const height = 400;
      d3.select(this.$refs.chart).select("svg").remove();

      const svg = d3.select(this.$refs.chart)
          .append("svg")
          .attr("width", width)
          .attr("height", height);

      const root = d3.hierarchy({ children: this.tokenData })
          .sum(d => d.value);

      d3.treemap()
          .size([width, height])
          .padding(2)(root);

      const color = d3.scaleSequential(d3.interpolateOranges)
          .domain([0, d3.max(this.tokenData, d => d.value)]);

      const nodes = svg.selectAll("g")
          .data(root.leaves())
          .enter().append("g")
          .attr("transform", d => `translate(${d.x0},${d.y0})`);

      nodes.append("rect")
          .attr("width", d => d.x1 - d.x0)
          .attr("height", d => d.y1 - d.y0)
          .attr("fill", d => color(d.data.value))
          .attr("stroke", "#333");

      nodes.append("text")
          .attr("x", 4)
          .attr("y", 16)
          .attr("fill", "white")
          .attr("font-size", "12px")
          .text(d => d.data.name);
    }
  }
};
</script>

<style scoped>
.treemap-container {
  width: 100%;
  height: auto;
  color: white;
  padding: 16px;
}
.treemap-chart {
  background: #1e1e1e;
  border-radius: 8px;
  padding: 12px;
  overflow-x: auto;
}
</style>