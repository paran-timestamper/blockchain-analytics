<template>
  <div class="heatmap-page">
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
  container.select('svg').remove()

  const margin = { top: 50, left: 40, right: 40, bottom: 40 }
  const cellWidth = 80
  const cellHeight = 40
  const svg = container
      .append('svg')
      .attr('width', cellWidth * 7 + margin.left + margin.right)
      .attr('height', cellHeight * 6 + margin.top + margin.bottom)

  const colorA = d3.rgb(70, 130, 180) // blue
  const colorB = d3.rgb(50, 205, 50)  // green

  svg.selectAll('rect')
      .data(heatmapData.value)
      .enter()
      .append('rect')
      .attr('x', (_, i) => margin.left + i % 7 * cellWidth)
      .attr('y', (_, i) => margin.top + Math.floor(i / 7) * cellHeight)
      .attr('width', cellWidth)
      .attr('height', cellHeight)
      .attr('fill', d => {
        const a = d.values[props.aToken] || 0
        const b = d.values[props.bToken] || 0
        const total = a + b || 1
        const ratio = b / total
        return d3.interpolateRgb(colorA, colorB)(ratio)
      })
      .attr('stroke', '#333')
      .on('click', (event, d) => {
        const emittedPayload = { date: d.timeRange, mode: 'monthly' }
        console.log('[📤 HeatmapView emit]', emittedPayload)
        emit('cell-click', emittedPayload)
      })

  svg.selectAll('text')
      .data(heatmapData.value)
      .enter()
      .append('text')
      .attr('x', (_, i) => margin.left + i % 7 * cellWidth + 6)
      .attr('y', (_, i) => margin.top + Math.floor(i / 7) * cellHeight + 16)
      .attr('fill', 'white')
      .attr('font-size', '12px')
      .text(d => {
        const parts = d.timeRange.split('-')
        return `${Number(parts[2])}일`
      })
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
  background-color: #1e1e1e;
  border-radius: 8px;
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
</style>
