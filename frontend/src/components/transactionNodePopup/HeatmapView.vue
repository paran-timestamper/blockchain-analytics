<template>
  <div class="heatmap-page">
    <div class="controls">
      <input type="date" v-model="selectedDate" @change="fetchHeatmapData" />
    </div>

    <div class="heatmap-container">
      <div class="section-wrapper">
        <div class="section-title">Daily Heatmap</div>
        <div ref="dailyHeatmapContainer" class="heatmap-box"></div>
      </div>

      <div class="section-wrapper">
        <div class="section-title">Monthly Heatmap</div>
        <div ref="monthlyHeatmapContainer" class="heatmap-box"></div>
      </div>
    </div>

  </div>
</template>


<script>
import * as d3 from "d3";

export default {
  name: "HeatmapView",
  props: {
    address: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      selectedDate: new Date().toISOString().substr(0, 10),
      heatmapDataDaily: [],
      heatmapDataMonthly: [],
      maxValDaily: 1,
      maxValMonthly: 1
    };
  },
  methods: {
    async fetchHeatmapData() {
      if (!this.selectedDate) return;

      const dateOnly = this.selectedDate;
      const monthOnly = this.selectedDate.slice(0, 7);

      console.log("📅 fetchHeatmapData selectedDate:", this.selectedDate);

      // --- Daily fetch
      const resDaily = await fetch('http://localhost:8080/api/heatmap/daily', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fromAddress: this.address,
          date: dateOnly
        })
      });

      const resultDaily = await resDaily.json();
      const timeRanges = ["00-04", "04-08", "08-12", "12-16", "16-20", "20-24"];
      const blocks = resultDaily?.timeBlocks?.length
          ? resultDaily.timeBlocks
          : timeRanges.map(range => ({ range, txCount: 0 }));

      this.heatmapDataDaily = blocks.map(b => ({
        range: b.range,
        txCount: Number(b.txCount)
      }));

      this.maxValDaily = d3.max(this.heatmapDataDaily.map(b => b.txCount)) || 1;

      // --- Monthly fetch
      const resMonthly = await fetch('http://localhost:8080/api/heatmap/monthly', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fromAddress: this.address,
          date: monthOnly
        })
      });

      let resultMonthly = await resMonthly.json();
      if (!Array.isArray(resultMonthly)) resultMonthly = [resultMonthly];

      this.heatmapDataMonthly = resultMonthly.map(d => ({
        day: d.day,
        txCount: Number(d.txCount)
      }));

      this.maxValMonthly = d3.max(this.heatmapDataMonthly.map(d => d.txCount)) || 1;

      this.$nextTick(() => {
        this.drawDailyHeatmap();
        this.drawMonthlyHeatmap();
      });
    },

    drawDailyHeatmap() {
      const container = this.$refs.dailyHeatmapContainer;
      d3.select(container).select("svg").remove();

      const margin = { top: 40, right: 40, bottom: 30, left: 60 };  // ⬅ top 줄임
      const cellWidth = 80;
      const cellHeight = 40;
      const svg = d3.select(container)
          .append("svg")
          .attr("width", cellWidth * 6 + margin.left + margin.right)
          .attr("height", cellHeight + margin.top + margin.bottom);

      const colorScale = d3.scaleSequential(d3.interpolateReds).domain([0, 1000]);

      svg.selectAll(".cell")
          .data(this.heatmapDataDaily.map((d, i) => ({ ...d, index: i })))
          .enter()
          .append("rect")
          .attr("class", "cell")
          .attr("x", (_, i) => margin.left + i * cellWidth)
          .attr("y", margin.top)
          .attr("width", cellWidth)
          .attr("height", cellHeight)
          .attr("fill", d => colorScale(d.txCount))
          .attr("stroke", "#333")
          .on("click", (event, d) => {
            const clickedDate = this.formatDailyClickToDatetime(d.index);
            console.log("🟥 Daily cell clicked:", clickedDate);
            this.$emit("cell-click", { mode: 'daily', date: clickedDate });
          });

      svg.selectAll(".label")
          .data(this.heatmapDataDaily.map(b => b.range))
          .enter()
          .append("text")
          .attr("x", (_, i) => margin.left + i * cellWidth + cellWidth / 2)
          .attr("y", margin.top - 10)
          .attr("text-anchor", "middle")
          .attr("fill", "white")
          .text(d => d);
    },

    drawMonthlyHeatmap() {
      const container = this.$refs.monthlyHeatmapContainer;
      d3.select(container).select("svg").remove();

      const margin = { top: 80, right: 40, bottom: 40, left: 80 };
      const cellWidth = 80;
      const cellHeight = 40;

      const svg = d3.select(container)
          .append("svg")
          .attr("width", cellWidth * 7 + margin.left + margin.right)
          .attr("height", cellHeight * 6 + margin.top + margin.bottom);

      const colorScale = d3.scaleSequential(d3.interpolateReds).domain([0, 3000]);

      const [year, month] = this.selectedDate.split('-');
      const firstDay = new Date(year, month - 1, 1);
      const startWeekday = firstDay.getDay();
      const daysInMonth = new Date(year, month, 0).getDate();

      const calendarCells = [];
      for (let i = 0; i < startWeekday; i++) {
        calendarCells.push({ day: '', txCount: 0 });
      }

      for (let i = 1; i <= daysInMonth; i++) {
        const dayStr = `${i < 10 ? '0' : ''}${i}일`;
        const match = this.heatmapDataMonthly.find(d => d.day === dayStr) || { day: dayStr, txCount: 0 };
        const formattedDate = `${year}-${month}-${String(i).padStart(2, '0')}`;
        calendarCells.push({ ...match, formattedDate });
      }

      while (calendarCells.length % 7 !== 0) {
        calendarCells.push({ day: '', txCount: 0 });
      }

      const weekdays = ['일', '월', '화', '수', '목', '금', '토'];
      svg.selectAll(".weekday-label")
          .data(weekdays)
          .enter()
          .append("text")
          .attr("x", (_, i) => margin.left + i * cellWidth + cellWidth / 2)
          .attr("y", margin.top - 30)
          .attr("text-anchor", "middle")
          .attr("fill", "white")
          .attr("font-weight", "bold")
          .text(d => d);

      svg.selectAll(".cell")
          .data(calendarCells)
          .enter()
          .append("rect")
          .attr("class", "cell")
          .attr("x", (_, i) => margin.left + (i % 7) * cellWidth)
          .attr("y", (_, i) => margin.top + Math.floor(i / 7) * cellHeight)
          .attr("width", cellWidth)
          .attr("height", cellHeight)
          .attr("fill", d => colorScale(d.txCount))
          .attr("stroke", "#333")
          .on("click", (event, d) => {
            if (!d || !d.formattedDate) {
              console.warn("[HeatmapView] Skipping invalid cell:", d);
              return;
            }
            console.log("🟦 Monthly cell clicked:", d.formattedDate); // ✅ 핵심 로그
            this.$emit("cell-click", { mode: "monthly", date: d.formattedDate });
          });

      svg.selectAll(".cell-label")
          .data(calendarCells)
          .enter()
          .append("text")
          .attr("x", (_, i) => margin.left + (i % 7) * cellWidth + 4)
          .attr("y", (_, i) => margin.top + Math.floor(i / 7) * cellHeight + 14)
          .attr("fill", "white")
          .attr("font-size", "12px")
          .text(d => d.day.replace("일", ""));
    },

    formatDailyClickToDatetime(index) {
      const [year, month, day] = this.selectedDate.split('-');
      const hour = index * 4;
      if (!year || !month || !day) return null;
      return `${year}-${month}-${day}T${String(hour).padStart(2, '0')}:00:00`;
    }
  },
  mounted() {
    console.log("📌 HeatmapView mounted, initial date:", this.selectedDate);
    this.fetchHeatmapData();
  }
};
</script>

<style scoped>

input[type="date"] {
  background-color: #1e1e1e;
  color: white;
  border: 1px solid #444;
  border-radius: 6px;
  padding: 6px 10px;
}

.heatmap-page {
  padding: 24px;
  color: white;
}
.controls {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
  align-items: center;
}
.heatmap-container {
  display: flex;
  flex-direction: column;
  //gap: 10px;
}
svg {
  background-color: #1e1e1e;
  border-radius: 8px;
}
.cell:hover {
  stroke: yellow;
  stroke-width: 2px;
  cursor: pointer;
}
.section-title {
  text-align: center;
  font-weight: bold;
  font-size: 18px;
  margin-bottom: 8px;
}

.heatmap-box {
  display: flex;
  justify-content: center;
  align-items: center;
  //border: 1px solid #444;       /* ✅ 테두리 추가 */
  border-radius: 8px;
  padding: 12px;                /* 여백도 약간 */
}

.section-wrapper {
  border: 1px solid #444;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  background-color: #1e1e1e;
}


</style>
