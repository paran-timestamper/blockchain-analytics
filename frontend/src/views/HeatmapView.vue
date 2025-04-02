<template>
  <div class="heatmap-page">
    <div class="controls">
      <div class="date-filter">
        <input type="date" v-model="startDate" @change="onDateChange" />
        <button @click="generateHeatmap" :disabled="!startDate">Generate Heatmap</button>
      </div>
      <div v-if="startDate && endDate" class="date-range-text">
        <p>{{ startDate }} ~ {{ endDate }} (7일간 데이터)</p>
      </div>
    </div>

    <div v-if="heatmapData.length" class="heatmap-wrapper">
      <div class="heatmap-container" ref="heatmap"></div>
      <div class="legend">
        <p>범례</p>
        <div class="legend-bar">
          <span v-for="i in 5" :key="i" class="legend-box" :style="{ backgroundColor: legendColor(i) }"></span>
        </div>
        <div class="legend-labels">
          <span>0</span>
          <span>{{ Math.floor(maxVal / 2) }}</span>
          <span>{{ maxVal }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as d3 from "d3";

export default {
  data() {
    return {
      startDate: '',
      endDate: '',
      rawTxs: [],
      heatmapData: [],
      maxVal: 1
    };
  },
  methods: {
    onDateChange() {
      if (this.startDate) {
        const start = new Date(this.startDate);
        const end = new Date(start);
        end.setDate(start.getDate() + 6);
        this.endDate = end.toISOString().split('T')[0];
      }
    },
    generateMockTxs() {
      const txs = [];
      for (let i = 0; i < 500; i++) {
        const start = new Date("2024-01-01T00:00:00").getTime();
        const end = new Date("2025-03-31T23:59:59").getTime();
        const ts = new Date(start + Math.random() * (end - start));
        txs.push({ timestamp: ts.toISOString() });
      }
      this.rawTxs = txs;
    },
    generateHeatmap() {
      this.generateMockTxs();

      const data = Array.from({ length: 24 }, () => Array(7).fill(0));

      const start = new Date(this.startDate);
      const end = new Date(this.endDate);

      this.rawTxs.forEach(tx => {
        const ts = new Date(tx.timestamp);
        if (ts >= start && ts <= end) {
          const hour = ts.getHours();
          const day = (ts.getDay() + 6) % 7; // Sunday = 0 -> 6 (월=0)
          data[hour][day]++;
        }
      });

      this.heatmapData = data;
      this.maxVal = d3.max(data.flat()) || 1;
      this.$nextTick(() => this.drawHeatmap());
    },
    drawHeatmap() {
      const margin = { top: 60, right: 40, bottom: 40, left: 80 };
      const cellSize = 35;
      const width = cellSize * 7 + margin.left + margin.right;
      const height = cellSize * 24 + margin.top + margin.bottom;

      d3.select(this.$refs.heatmap).select("svg").remove();

      const svg = d3.select(this.$refs.heatmap)
          .append("svg")
          .attr("width", width)
          .attr("height", height)
          .style("display", "block")
          .style("margin", "0 auto");

      const colorScale = d3.scaleSequential()
          .interpolator(d3.interpolateReds)
          .domain([0, this.maxVal]);

      const days = ['월', '화', '수', '목', '금', '토', '일'];

      // Draw cells
      this.heatmapData.forEach((row, hour) => {
        row.forEach((val, day) => {
          svg.append("rect")
              .attr("x", margin.left + day * cellSize)
              .attr("y", margin.top + hour * cellSize)
              .attr("width", cellSize)
              .attr("height", cellSize)
              .attr("fill", colorScale(val))
              .attr("stroke", "#222");
        });
      });

      // Day labels (X-axis)
      svg.selectAll(".day-label")
          .data(days)
          .enter()
          .append("text")
          .attr("x", (_, i) => margin.left + i * cellSize + cellSize / 2)
          .attr("y", margin.top - 10)
          .attr("text-anchor", "middle")
          .attr("fill", "white")
          .attr("font-size", "14px")
          .text(d => d);

      // Hour labels (Y-axis)
      svg.selectAll(".hour-label")
          .data(d3.range(24))
          .enter()
          .append("text")
          .attr("x", margin.left - 10)
          .attr("y", i => margin.top + i * cellSize + cellSize / 1.5)
          .attr("text-anchor", "end")
          .attr("fill", "white")
          .attr("font-size", "12px")
          .text(d => `${d}~${d + 1}`);
    },
    legendColor(i) {
      return d3.interpolateReds(i / 5);
    }
  }
};
</script>

<style scoped>
.heatmap-page {
  padding: 24px;
  color: white;
}
.controls {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
  justify-content: center;
}
.date-filter input[type="date"] {
  background: #2a2a2a;
  color: white;
  padding: 6px;
  border: none;
  border-radius: 4px;
}
.date-filter button {
  background: #2196f3;
  color: white;
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.date-filter button:disabled {
  background: #555;
  cursor: not-allowed;
}
.date-range-text {
  font-size: 14px;
  color: #aaa;
}
.heatmap-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 24px;
}
.heatmap-container {
  background: #111;
  padding: 20px;
  border-radius: 8px;
  max-width: fit-content;
}
.legend {
  color: white;
  font-size: 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.legend-bar {
  display: flex;
  gap: 4px;
}
.legend-box {
  width: 30px;
  height: 20px;
  border: 1px solid #333;
}
.legend-labels {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}
</style>
