<template>
  <div class="treemap-wrapper">
    <div ref="treemapContainer" class="treemap-container"></div>
  </div>
</template>

<script>
import * as d3 from "d3";

export default {
  name: "Treemap",
  props: {
    address: { type: String, required: true },
    date: { type: String, required: true }
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

        let data = await res.json();

        if (!Array.isArray(data) || data.length === 0) {
          data = [{ token: "NO DATA", value: 1, percentage: "100%" }];
        }

        this.drawTreemap(data.slice(0, 16)); // ✅ 상위 16개만 사용
      } catch (err) {
        console.error("Treemap Error:", err);
      }
    },

    drawTreemap(data) {
      const container = this.$refs.treemapContainer;
      const width = (container.clientWidth || 600) * 0.95;
      const height = 340;

      d3.select(container).select("svg").remove();

      const svg = d3.select(container)
          .append("svg")
          .attr("width", width)
          .attr("height", height);

      const root = d3.hierarchy({
        children: data.map(d => ({
          name: d.token,
          value: d.value
        }))
      }).sum(d => Math.log(d.value + 1));

      d3.treemap()
          .size([width, height])
          .paddingOuter(6)
          .paddingInner(4)(root);

      // ✅ 순위 기반 색상 그룹 (4개씩)
      const colorByRank = index => {
        if (index < 4) return "#a6e22e";   // 1–4위
        if (index < 8) return "#66d9ef";   // 5–8위
        if (index < 12) return "#fd971f";  // 9–12위
        return "#f92672";                  // 13–16위
      };

      const nodes = svg.selectAll("g")
          .data(root.leaves())
          .enter()
          .append("g")
          .attr("transform", d => `translate(${d.x0},${d.y0})`);

      nodes.append("rect")
          .attr("width", d => d.x1 - d.x0)
          .attr("height", d => d.y1 - d.y0)
          .attr("fill", (d, i) => colorByRank(i))
          .attr("rx", 8)
          .attr("ry", 8)
          .attr("stroke", "#1e1e1e");

      // ✅ 텍스트: 사각형 작으면 생략
      nodes
          .filter(d => (d.x1 - d.x0) > 40 && (d.y1 - d.y0) > 24)
          .append("text")
          .attr("x", 6)
          .attr("y", 10)
          .attr("fill", "white")
          .attr("font-size", "6px")
          .attr("font-weight", "bold")
          .style("text-shadow", "0 1px 2px rgba(0,0,0,0.6)")
          .selectAll("tspan")
          .data(d => {
            const label = d.data.name;
            const value = `${d.data.value.toLocaleString()} USDT`;
            return [label, value];
          })
          .enter()
          .append("tspan")
          .attr("x", 6)
          .attr("dy", (d, i) => i === 0 ? 0 : "1.1em")
          .text(d => d);
    }
  }
};
</script>

<style scoped>
.treemap-wrapper {
  width: 100%;
  padding: 12px;
  background-color: #1e1e1e;
  border-radius: 8px;
  position: relative;
}

.treemap-container {
  width: 100%;
  height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
