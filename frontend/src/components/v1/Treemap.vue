<template>
  <div ref="treemapContainer"></div>
</template>

<script>
import * as d3 from "d3";

export default {
  name: "Treemap",
  props: {
    address: {
      type: String,
      required: true
    },
    date: {
      type: String,
      required: true
    }
  },
  watch: {
    date: {
      immediate: true,
      handler() {
        this.fetchTreemapData();
      }
    }
  },
  methods: {
    async fetchTreemapData() {
      const endpoint = "http://localhost:8080/api/treemap/info";
      const payload = { fromAddress: "axelar19xj4ncc6h6y5ahpfqtspdx75y3dkrxj3zpah9k" };

      try {
        const res = await fetch(endpoint, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        if (!res.ok) {
          throw new Error(`HTTP 오류! 상태 코드: ${res.status}`);
        }

        let data = await res.json();
        if (!Array.isArray(data) || data.length === 0) {
          console.warn("⚠️ Treemap 데이터 없음. 기본값 사용.");
          data = [
            { token: "NO DATA", value: 1, percentage: "100%" }
          ];
        }

        this.drawTreemap(data.slice(0, 16));
      } catch (err) {
        console.error("🗺️ 트리맵 데이터 오류:", err);
      }
    },
    drawTreemap(data) {
      const container = this.$refs.treemapContainer;
      d3.select(container).select("svg").remove();

      const width = 600;
      const height = 400;

      const svg = d3.select(container)
          .append("svg")
          .attr("width", width)
          .attr("height", height);

      const root = d3.hierarchy({ children: data.map(d => ({ name: d.token, value: d.value })) })
          .sum(d => d.value);

      d3.treemap()
          .size([width, height])
          .padding(2)(root);

      const color = d3.scaleSequential(d3.interpolateOranges)
          .domain([0, d3.max(data, d => d.value)]);

      const nodes = svg.selectAll("g")
          .data(root.leaves())
          .enter()
          .append("g")
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
          .selectAll("tspan")
          .data(d => [d.data.name, `(${d.data.value}USDT)`])
          .enter()
          .append("tspan")
          .attr("x", 4)
          .attr("dy", (d, i) => i === 0 ? "0" : "1.2em")
          .text(d => d);

    }
  }
};
</script>

<style scoped>
div {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
