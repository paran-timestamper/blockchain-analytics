<template>
  <div class="heatmap-page">
    <!-- 📌 범례 표시 -->
    <div class="legend-row">
      <div class="legend-item">
        <span class="legend-box" :style="{ backgroundColor: '#FFD700' }"></span>
        <span>{{ props.aToken }}</span>
      </div>
      <div class="legend-item">
        <span class="legend-box" :style="{ backgroundColor: '#DC143C' }"></span>
        <span>{{ props.bToken }}</span>
      </div>
    </div>

    <div class="controls">
      <input type="month" v-model="selectedMonth" @change="fetchHeatmapData" />
    </div>

    <div class="heatmap-container" ref="heatmapContainer"></div>
  </div>
</template>

<script setup>
import * as d3 from 'd3'
import { ref, onMounted, defineEmits, defineProps } from 'vue'

const props = defineProps({
  aToken: String,
  bToken: String
})

const emit = defineEmits(['cell-click'])

const selectedMonth = ref(new Date().toISOString().slice(0, 7))
const heatmapData = ref([])
const heatmapContainer = ref(null)

async function fetchHeatmapData() {
  const res = await fetch('http://localhost:8080/api/heatmap/compare/monthly', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      tokens: [props.aToken, props.bToken],
      date: selectedMonth.value
    })
  })

  const result = await res.json()
  heatmapData.value = result.compareMonthlyTimeBlocks || []
  drawHeatmap()
}

function drawHeatmap() {
  const container = d3.select(heatmapContainer.value)
  container.selectAll('*').remove()

  const margin = { top: 0, left: 0, right: 0, bottom: 0 }
  const cellWidth = 80
  const cellHeight = 40
  const svg = container
      .append('svg')
      .attr('width', cellWidth * 7 + margin.left + margin.right)
      .attr('height', cellHeight * 6 + margin.top + margin.bottom)

  const colorA = d3.rgb(255, 215, 0)   // #FFD700
  const colorB = d3.rgb(220, 20, 60)   // #DC143C

  // 📌 툴팁 추가
  const tooltip = container
      .append('div')
      .attr('class', 'tooltip')
      .style('position', 'absolute')
      .style('pointer-events', 'none')
      .style('background', '#333')
      .style('color', 'white')
      .style('padding', '6px 10px')
      .style('font-size', '12px')
      .style('border-radius', '4px')
      .style('opacity', 0)

  // 날짜 셀 구성
  const [year, month] = selectedMonth.value.split('-').map(Number)
  const firstDay = new Date(year, month - 1, 1)
  const startWeekday = firstDay.getDay()
  const daysInMonth = new Date(year, month, 0).getDate()

  const calendarCells = []
  for (let i = 0; i < startWeekday; i++) {
    calendarCells.push({ day: '', values: {} })
  }
  for (let i = 1; i <= daysInMonth; i++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(i).padStart(2, '0')}`
    const match = heatmapData.value.find(d => d.timeRange.startsWith(dateStr)) || { values: {} }
    calendarCells.push({ day: `${i}`, values: match.values, timeRange: dateStr })
  }
  while (calendarCells.length < 35) {
    calendarCells.push({ day: '', values: {} })
  }

  svg.selectAll('rect')
      .data(calendarCells)
      .enter()
      .append('rect')
      .attr('x', (_, i) => margin.left + (i % 7) * cellWidth)
      .attr('y', (_, i) => margin.top + Math.floor(i / 7) * cellHeight)
      .attr('width', cellWidth)
      .attr('height', cellHeight)
      .attr('fill', d => {
        if (!d.day) return '#ffffff'
        const a = d.values[props.aToken] || 0
        const b = d.values[props.bToken] || 0
        const total = a + b || 1
        const ratio = b / total
        return d3.interpolateRgb(colorA, colorB)(ratio)
      })
      .attr('stroke', '#333')
      .on('click', (event, d) => {
        if (!d.day || !d.timeRange) return
        const emittedPayload = { date: d.timeRange, mode: 'monthly' }
        emit('cell-click', emittedPayload)
      })
      .on('mouseover', function (event, d) {
        if (!d.day || !d.values) return
        const a = d.values[props.aToken] || 0
        const b = d.values[props.bToken] || 0
        const total = a + b || 1
        const aRatio = ((a / total) * 100).toFixed(1)
        const bRatio = ((b / total) * 100).toFixed(1)

        tooltip
            .html(`
          <div><strong>${props.aToken}:</strong> ${aRatio}%</div>
          <div><strong>${props.bToken}:</strong> ${bRatio}%</div>
        `)
            .style('left', event.offsetX + 20 + 'px')
            .style('top', event.offsetY + 20 + 'px')
            .transition()
            .duration(100)
            .style('opacity', 0.9)
      })
      .on('mousemove', function (event) {
        tooltip
            .style('left', event.offsetX + 20 + 'px')
            .style('top', event.offsetY + 20 + 'px')
      })
      .on('mouseout', function () {
        tooltip
            .transition()
            .duration(100)
            .style('opacity', 0)
      })

  svg.selectAll('text')
      .data(calendarCells)
      .enter()
      .append('text')
      .attr('x', (_, i) => margin.left + (i % 7) * cellWidth + 6)
      .attr('y', (_, i) => margin.top + Math.floor(i / 7) * cellHeight + 16)
      .attr('fill', 'black')
      .attr('font-size', '12px')
      .text(d => d.day)
}

onMounted(fetchHeatmapData)
</script>

<style scoped>
.heatmap-page {
  padding: 24px;
  color: white;
}
.controls {
  margin-bottom: 16px;
}
.heatmap-container {
  width: fit-content;
  background-color: transparent;
  padding: 0;
  margin: auto;
  position: relative; /* tooltip absolute 기준 */
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
.legend-row {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-bottom: 16px;
  align-items: center;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: white;
}
.legend-box {
  width: 16px;
  height: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.tooltip {
  position: absolute;
  z-index: 10;
  background-color: #333;
  color: white;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 12px;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.2s;
}
</style>
