<template>
  <div class="heatmap-page">
    <div class="controls">
      <input type="date" v-model="selectedDate" @change="fetchHeatmapData" />
      <select v-model="mode" @change="fetchHeatmapData">
        <option value="daily">일간</option>
        <option value="monthly">월간</option>
      </select>
    </div>

    <div ref="heatmapContainer"></div>


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
      selectedDate: '',
      mode: 'daily',
      maxVal: 1,
      heatmapData: []
    };
  },
  watch: {
    selectedDate(val) {
      this.$emit('update:selectedDate', val);
    },
    mode(val) {
      this.$emit('update:mode', val);
    }
  },
  methods: {
    async fetchHeatmapData() {
      if (!this.selectedDate) return;

      const endpoint = this.mode === 'daily'
          ? 'http://localhost:8080/api/heatmap/daily'
          : 'http://localhost:8080/api/heatmap/monthly';

      // ⛳ 수정된 날짜 포맷
      // const payloadDate = this.mode === 'monthly'
      //     ? this.selectedDate.slice(0, 7)  // "yyyy-MM"
      //     : this.selectedDate;             // "yyyy-MM-dd"

      const res = await fetch(endpoint, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({fromAddress: "axelar19xj4ncc6h6y5ahpfqtspdx75y3dkrxj3zpah9k", date: "2022-10-25"})
      });

      let result = await res.json();
      if (!Array.isArray(result)) result = [result];

      if (this.mode === 'daily') {
        const timeRanges = ["00시~04시", "04시~08시", "08시~12시", "12시~16시", "16시~20시", "20시~24시"];
        const dayObj = result[0] || {timeBlocks: []};
        const blocks = dayObj.timeBlocks?.length
            ? dayObj.timeBlocks
            : timeRanges.map(range => ({range, txCount: 0}));

        this.heatmapData = [{day: this.selectedDate, timeBlocks: blocks}];
        this.maxVal = d3.max(blocks.map(b => b.txCount)) || 1;
      } else {
        if (result.length === 0) {
          const daysInMonth = 31;
          this.heatmapData = Array.from({length: daysInMonth}, (_, i) => ({
            day: `${i + 1}일`,
            txCount: 0
          }));
        } else {
          this.heatmapData = result;
        }
        this.maxVal = d3.max(this.heatmapData.map(d => d.txCount)) || 1;
      }

      this.$nextTick(() => this.drawHeatmap());
    },

    drawHeatmap() {
      const container = this.$refs.heatmapContainer;
      d3.select(container).select("svg").remove();

      const margin = { top: 80, right: 40, bottom: 40, left: 80 };
      const cellWidth = 80;
      const cellHeight = 40;

      const svg = d3.select(container)
          .append("svg")
          .attr("width", cellWidth * 7 + margin.left + margin.right)
          .attr("height", cellHeight * 6 + margin.top + margin.bottom);

      const colorScale = d3.scaleSequential(d3.interpolateReds).domain([0, this.maxVal]);

      if (this.mode === 'daily') {
        const timeRanges = ["00-04", "04-08", "08-12", "12-16", "16-20", "20-24"];
        const blocks = this.heatmapData[0]?.timeBlocks || timeRanges.map(range => ({ range, txCount: 0 }));

        svg.selectAll(".cell")
            .data(blocks)
            .enter()
            .append("rect")
            .attr("x", (_, i) => margin.left + i * cellWidth)
            .attr("y", margin.top)
            .attr("width", cellWidth)
            .attr("height", cellHeight)
            .attr("fill", d => colorScale(d.txCount))
            .attr("stroke", "#333");

        svg.selectAll(".label")
            .data(timeRanges)
            .enter()
            .append("text")
            .attr("x", (_, i) => margin.left + i * cellWidth + cellWidth / 2)
            .attr("y", margin.top - 10)
            .attr("text-anchor", "middle")
            .attr("fill", "white")
            .text(d => d);
      } else {
        const yearMonth = this.selectedDate.slice(0, 7); // "YYYY-MM"
        const [year, month] = yearMonth.split('-').map(Number);

        const firstDay = new Date(year, month - 1, 1);
        const startWeekday = firstDay.getDay(); // 0 (Sun) ~ 6 (Sat)

        const daysInMonth = new Date(year, month, 0).getDate(); // 해당 월의 마지막 일
        const calendarCells = [];

        // 앞에 빈 칸
        for (let i = 0; i < startWeekday; i++) {
          calendarCells.push({ day: '', txCount: 0 });
        }

        // 날짜 채우기
        for (let i = 1; i <= daysInMonth; i++) {
          const targetDay = `${i}일`;
          const dataItem = this.heatmapData.find(d => d.day === targetDay) || { day: targetDay, txCount: 0 };
          calendarCells.push(dataItem);
        }

        // 7×5나 7×6 유지 위해 남은 칸 채움
        const totalCells = Math.ceil(calendarCells.length / 7) * 7;
        while (calendarCells.length < totalCells) {
          calendarCells.push({ day: '', txCount: 0 });
        }

        // 요일 헤더 추가
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

        // 셀 그리기
        svg.selectAll(".cell")
            .data(calendarCells)
            .enter()
            .append("rect")
            .attr("x", (_, i) => margin.left + (i % 7) * cellWidth)
            .attr("y", (_, i) => margin.top + Math.floor(i / 7) * cellHeight)
            .attr("width", cellWidth)
            .attr("height", cellHeight)
            .attr("fill", d => colorScale(d.txCount))
            .attr("stroke", "#333");

        // 날짜 텍스트 표시
        svg.selectAll(".cell-label")
            .data(calendarCells)
            .enter()
            .append("text")
            .attr("x", (_, i) => margin.left + (i % 7) * cellWidth + 4)
            .attr("y", (_, i) => margin.top + Math.floor(i / 7) * cellHeight + 14)
            .attr("fill", "white")
            .attr("font-size", "12px")
            .text(d => d.day.replace("일", ""));
      }
    }
  }
};
</script>

<style scoped>
.heatmap-page { padding: 24px; color: white; }
.controls { margin-bottom: 16px; display: flex; gap: 10px; align-items: center; }
</style>
