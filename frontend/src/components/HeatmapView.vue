<template>
  <div class="heatmap-page">
    <!-- 월/일 토글 버튼 -->
    <div class="toggle-wrapper">
      <button @click="toggleView">
        {{ isMonthly ? '🔁 일 거래량 보기' : '🔁 월 거래량 보기' }}
      </button>
    </div>

    <!-- 날짜 선택 -->
    <div class="calendar-controls">
      <input type="month" v-if="isMonthly" v-model="selectedMonth" @change="fetchMonthlyTxs" />
      <input type="date" v-else v-model="startDate" @change="onDateChange" />
    </div>

    <!-- 날짜 표시 -->
    <div v-if="startDate && !isMonthly" class="date-range-text">
      <p>{{ startDate }} ~ {{ endDate }} (7일간 데이터)</p>
    </div>

    <!-- Heatmap 영역 -->
    <div v-if="hasData" class="heatmap-wrapper">
      <div class="heatmap-container" ref="heatmap"></div>
    </div>

    <!-- 범례 -->
    <div v-if="hasData" class="legend-wrapper">
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
</template>

<script>
import * as d3 from "d3";

export default {
  data() {
    return {
      isMonthly: false,
      startDate: '',
      endDate: '',
      selectedMonth: '',
      heatmapData: [],
      maxVal: 1
    };
  },
  computed: {
    hasData() {
      return this.isMonthly ? Object.keys(this.heatmapData).length > 0 : this.heatmapData.length > 0;
    }
  },
  methods: {
    toggleView() {
      this.isMonthly = !this.isMonthly;
      this.heatmapData = [];
    },
    onDateChange() {
      if (this.startDate) {
        const start = new Date(this.startDate);
        const end = new Date(start);
        end.setDate(start.getDate() + 6);
        this.endDate = end.toISOString().split('T')[0];
        this.fetchDailyTxs();
      }
    },
    async fetchDailyTxs() {
      try {
        const res = await fetch(`http://localhost:8080/api/heatmap/infos`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ startDate: `${this.startDate} 00:00:00` })
        });
        const result = await res.json();
        const list = result.dates || [];

        const matrix = Array.from({ length: 24 }, () => Array(7).fill(0));
        const baseDay = new Date(this.startDate).getDay();
        const dayToIndex = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

        list.forEach(({ day, hour, txCount }) => {
          const originalIdx = dayToIndex.indexOf(day);
          const rotatedIdx = (originalIdx - baseDay + 7) % 7;
          if (hour >= 0 && hour <= 23 && rotatedIdx >= 0 && rotatedIdx <= 6) {
            matrix[hour][rotatedIdx] += txCount;
          }
        });

        this.heatmapData = matrix;
        this.maxVal = d3.max(matrix.flat()) || 1;
        this.$nextTick(this.drawDailyHeatmap);
      } catch (err) {
        console.error("일 거래량 API 오류:", err);
        this.heatmapData = [];
      }
    },
    async fetchMonthlyTxs() {
      try {
        const [year, month] = this.selectedMonth.split('-').map(Number);
        const res = await fetch(`http://localhost:8080/api/heatmap/infos/monthly?year=${year}&month=${month}`);
        const list = await res.json();

        const data = {};
        for (const item of list) {
          const num = parseInt(item.day);
          data[num] = item.txCount;
        }

        if (Object.keys(data).length) {
          this.heatmapData = data;
          this.maxVal = Math.max(...Object.values(data), 1);
          this.$nextTick(() => this.drawMonthlyCalendar(year, month));
        } else {
          this.heatmapData = {};
        }
      } catch (err) {
        console.error("월 거래량 API 오류:", err);
        this.heatmapData = {};
      }
    },
    drawDailyHeatmap() {
      const margin = { top: 60, right: 40, bottom: 40, left: 80 };
      const cellSize = 30;
      const width = cellSize * 7 + margin.left + margin.right;
      const height = cellSize * 24 + margin.top + margin.bottom;

      d3.select(this.$refs.heatmap).select("svg").remove();

      const svg = d3.select(this.$refs.heatmap)
          .append("svg")
          .attr("width", width)
          .attr("height", height)
          .style("display", "block")
          .style("margin", "0 auto");

      const colorScale = d3.scaleSequential(d3.interpolateReds).domain([0, this.maxVal]);
      const days = ['월', '화', '수', '목', '금', '토', '일'];

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
    drawMonthlyCalendar(year, month) {
      const firstDay = new Date(year, month - 1, 1).getDay();
      const lastDate = new Date(year, month, 0).getDate();
      const cellSize = 50;
      const cols = 7;
      const rows = Math.ceil((firstDay + lastDate) / 7);

      d3.select(this.$refs.heatmap).select("svg").remove();

      const svg = d3.select(this.$refs.heatmap)
          .append("svg")
          .attr("width", cellSize * cols + 40)
          .attr("height", cellSize * rows + 40)
          .style("display", "block")
          .style("margin", "0 auto");

      const colorScale = d3.scaleSequential(d3.interpolateReds).domain([0, this.maxVal]);

      let dayCount = 1;
      for (let row = 0; row < rows; row++) {
        for (let col = 0; col < cols; col++) {
          const dayIndex = row * cols + col;
          const isInMonth = dayIndex >= firstDay && dayCount <= lastDate;

          if (isInMonth) {
            const val = this.heatmapData[dayCount] || 0;
            svg.append("rect")
                .attr("x", col * cellSize + 20)
                .attr("y", row * cellSize + 20)
                .attr("width", cellSize)
                .attr("height", cellSize)
                .attr("fill", colorScale(val))
                .attr("stroke", "#333");

            svg.append("text")
                .attr("x", col * cellSize + 45)
                .attr("y", row * cellSize + 45)
                .attr("text-anchor", "middle")
                .attr("fill", "white")
                .text(dayCount);

            dayCount++;
          }
        }
      }
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
.toggle-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 12px;
}
.calendar-controls {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 12px;
}
.legend-wrapper {
  text-align: center;
  margin-top: 16px;
  margin-bottom: 12px;
}
.legend-bar {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin: 4px 0;
}
.legend-box {
  width: 30px;
  height: 20px;
  border: 1px solid #333;
}
.legend-labels {
  display: flex;
  justify-content: space-between;
  max-width: 200px;
  margin: 0 auto;
  font-size: 12px;
}
.heatmap-wrapper {
  display: flex;
  justify-content: center;
}
.heatmap-container {
  background: #111;
  padding: 16px;
  border-radius: 8px;
  transform: scale(0.85);
  transform-origin: top center;
}
</style>
