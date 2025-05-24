<template>
  <div class="treemap-wrapper">
    <div ref="treemapContainer" class="treemap-container"></div>
    <div ref="tooltip" class="tooltip" v-show="tooltipVisible" :style="tooltipStyle">
      {{ tooltipContent }}
    </div>
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
  data() {
    return {
      tooltipVisible: false,
      tooltipContent: '',
      tooltipStyle: {
        position: 'absolute',
        top: '0px',
        left: '0px',
        opacity: 0
      }
    };
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
      const payload = { fromAddress: this.address };

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

        this.drawTreemap(data.slice(0, 16));
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

      const root = d3.hierarchy({ children: data.map(d => ({ name: d.token, value: d.value })) })
          .sum(d => Math.log(d.value + 1)); // ✅ 로그 스케일

      d3.treemap()
          .size([width, height])
          .paddingOuter(6)
          .paddingInner(4)(root);

      const color = d3.scaleSequential()
          .domain([0, d3.max(data, d => d.value)])
          .interpolator(d3.interpolateWarm);

      const nodes = svg.selectAll("g")
          .data(root.leaves())
          .enter()
          .append("g")
          .attr("transform", d => `translate(${d.x0},${d.y0})`);

      // rect
      nodes.append("rect")
          .attr("width", d => d.x1 - d.x0)
          .attr("height", d => d.y1 - d.y0)
          .attr("fill", d => color(d.data.value))
          .attr("rx", 8)
          .attr("ry", 8)
          .attr("stroke", "#1e1e1e")
          .on("mouseover", (event, d) => {
            this.tooltipContent = `${d.data.name}: ${d.data.value.toLocaleString()} USDT`;
            this.tooltipStyle.left = event.pageX + 10 + "px";
            this.tooltipStyle.top = event.pageY + 10 + "px";
            this.tooltipVisible = true;
            this.tooltipStyle.opacity = 1;
          })
          .on("mousemove", (event) => {
            this.tooltipStyle.left = event.pageX + 10 + "px";
            this.tooltipStyle.top = event.pageY + 10 + "px";
          })
          .on("mouseout", () => {
            this.tooltipVisible = false;
            this.tooltipStyle.opacity = 0;
          });

      // 상위 3개만 내부 텍스트 표시
      const top3 = new Set(data.slice(0, 3).map(d => d.token));

      nodes.filter(d => top3.has(d.data.name))
          .append("text")
          .attr("x", 8)
          .attr("y", 18)
          .attr("fill", "white")
          .attr("font-size", "11px")
          .attr("font-weight", "bold")
          .style("text-shadow", "0 1px 3px rgba(0,0,0,0.8)")
          .selectAll("tspan")
          .data(d => {
            const label = d.data.name;
            const value = `${d.data.value.toLocaleString()} USDT`;
            return [label, value];
          })
          .enter()
          .append("tspan")
          .attr("x", 8)
          .attr("dy", (d, i) => i === 0 ? 0 : "1.2em")
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

.tooltip {
  pointer-events: none;
  position: absolute;
  background-color: #222;
  color: white;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  white-space: nowrap;
  z-index: 100;
  opacity: 0;
  transition: opacity 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}
</style>