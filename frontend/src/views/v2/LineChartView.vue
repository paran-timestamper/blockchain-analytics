<template>
  <div class="chart-wrapper" ref="wrapperRef">
    <svg class="chart-svg" :viewBox="`0 0 ${svgWidth} ${svgHeight}`">
      <!-- ✅ 중앙 기준선 (위/아래 공통 y=0) -->
      <line
          :x1="marginOffset"
          :x2="svgWidth - marginOffset"
          :y1="yBase"
          :y2="yBase"
          stroke="#d1d5db"
          stroke-width="1"
      />

      <!-- ✅ Y축 (첫 번째 체크포인트 기준) -->
      <line
          :x1="marginOffset"
          :x2="marginOffset"
          :y1="20"
          :y2="svgHeight - 20"
          stroke="#9ca3af"
          stroke-width="1"
      />

      <!-- ✅ 위쪽 꺾은선 -->
      <polyline
          :points="upperLinePoints"
          fill="none"
          stroke="#3b82f6"
          stroke-width="2"
      />
      <g>
        <circle
            v-for="(val, i) in dummyUpperData"
            :key="'upper-' + i"
            :cx="i * stepWidth + marginOffset"
            :cy="yBase - (val / maxUpper) * yRange"
            r="3"
            fill="#3b82f6"
        />
      </g>

      <!-- ✅ 아래쪽 꺾은선 -->
      <polyline
          :points="lowerLinePoints"
          fill="none"
          stroke="#10b981"
          stroke-width="2"
      />
      <g>
        <circle
            v-for="(val, i) in dummyLowerData"
            :key="'lower-' + i"
            :cx="i * stepWidth + marginOffset"
            :cy="yBase + (val / maxLower) * yRange"
            r="3"
            fill="#10b981"
        />
      </g>

      <!-- ✅ x축 체크포인트 및 날짜 라벨 -->
      <g class="x-axis-checkpoints">
        <g v-for="(date, i) in sectionDates" :key="i">
          <line
              :x1="i * stepWidth + marginOffset"
              :x2="i * stepWidth + marginOffset"
              :y1="svgHeight"
              :y2="svgHeight + 10"
              stroke="#9ca3af"
              stroke-width="1"
          />
          <text
              :x="i * stepWidth + marginOffset"
              :y="svgHeight - 2"
              font-size="10"
              text-anchor="middle"
              fill="#6b7280"
          >
            {{ date }}
          </text>
        </g>
      </g>
    </svg>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, toRefs } from 'vue'

const props = defineProps({
  sectionDates: {
    type: Array,
    required: true
  }
})

const { sectionDates } = toRefs(props)

// ✅ 예시 데이터 (상단/하단)
const dummyUpperData = [10, 20, 40, 35, 50, 55, 60]
const dummyLowerData = [5, 15, 12, 18, 22, 19, 25]

const wrapperRef = ref(null)
const svgWidth = ref(600)
const svgHeight = 240

const TRACK_MARGIN = 24
const marginOffset = TRACK_MARGIN / 2

const maxUpper = Math.max(...dummyUpperData)
const maxLower = Math.max(...dummyLowerData)

// ✅ 중앙선 기준 좌표 (위/아래 공통 y=0)
const yBase = svgHeight / 2
const yRange = (svgHeight / 2) - 40  // 위/아래 동일 거리

const stepWidth = computed(() =>
    (svgWidth.value - TRACK_MARGIN) / (sectionDates.value.length - 1)
)

const upperLinePoints = computed(() =>
    dummyUpperData.map((val, i) => {
      const x = i * stepWidth.value + marginOffset
      const y = yBase - (val / maxUpper) * yRange
      return `${x},${y}`
    }).join(' ')
)

const lowerLinePoints = computed(() =>
    dummyLowerData.map((val, i) => {
      const x = i * stepWidth.value + marginOffset
      const y = yBase + (val / maxLower) * yRange
      return `${x},${y}`
    }).join(' ')
)

onMounted(() => {
  nextTick(() => {
    const rect = wrapperRef.value.getBoundingClientRect()
    svgWidth.value = rect.width
  })
})
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
  height: 100%;
  overflow: visible;
}

.chart-svg {
  width: 100%;
  height: 100%;
  overflow: visible;
}

.x-axis-checkpoints line {
  stroke-linecap: round;
}

.x-axis-checkpoints text {
  font-family: sans-serif;
}
</style>
