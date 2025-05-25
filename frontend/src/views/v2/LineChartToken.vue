<template>
  <div class="chart-wrapper" ref="wrapperRef">
    <svg
        class="chart-svg"
        :viewBox="`0 0 ${svgWidth} ${svgHeight}`"
        preserveAspectRatio="none"
    >
      <!-- 1) 중앙선: Total Value 기준선 -->
      <line
          :x1="marginOffset"
          :x2="svgWidth - marginOffset"
          :y1="yBase"
          :y2="yBase"
          stroke="#d1d5db"
          stroke-width="1"
      />

      <!-- 2) 상단 X축 ticks + 레이블 -->
      <g class="x-axis-checkpoints top">
        <g v-for="(date, i) in sectionDates" :key="i">
          <line
              :x1="i * stepWidth + marginOffset"
              :x2="i * stepWidth + marginOffset"
              :y1="yBase"
              :y2="yBase - 6"
              stroke="#9ca3af"
              stroke-width="1"
          />
          <text
              :x="i * stepWidth + marginOffset"
              :y="yBase - 10"
              font-size="10"
              text-anchor="middle"
              fill="#6b7280"
          >{{ date }}</text>
        </g>
      </g>

      <!-- 3) Total Value 그래프 -->
      <text
          :x="marginOffset"
          y="20"
          font-size="12"
          fill="#374151"
          font-weight="bold"
      >Total Value</text>
      <template v-for="series in visibleUpperSeries" :key="series.name">
        <polyline
            :points="series.pointsUpper"
            fill="none"
            :stroke="series.color"
            stroke-width="2"
        />
        <circle
            v-for="(val, i) in series.values"
            :key="series.name + '-up-' + i"
            :cx="i * stepWidth + marginOffset"
            :cy="yBase - (val / maxUpper) * yRange"
            r="3"
            :fill="series.color"
            @mouseenter="showTooltip(series.name, val, i, 'upper')"
            @mouseleave="hideTooltip"
        />
      </template>

      <!-- 4) 하단 0축: Transaction Amount 기준선 -->
      <line
          :x1="marginOffset"
          :x2="svgWidth - marginOffset"
          :y1="yLowerBase"
          :y2="yLowerBase"
          stroke="#d1d5db"
          stroke-width="1"
      />

      <!-- 5) 하단 X축 ticks + 레이블 -->
      <g class="x-axis-checkpoints bottom">
        <g v-for="(date, i) in sectionDates" :key="i">
          <line
              :x1="i * stepWidth + marginOffset"
              :x2="i * stepWidth + marginOffset"
              :y1="yLowerBase"
              :y2="yLowerBase + 6"
              stroke="#9ca3af"
              stroke-width="1"
          />
          <text
              :x="i * stepWidth + marginOffset"
              :y="yLowerBase + 20"
              font-size="10"
              text-anchor="middle"
              fill="#6b7280"
          >{{ date }}</text>
        </g>
      </g>

      <!-- 6) Transaction Amount 그래프 -->
      <text
          :x="marginOffset"
          :y="yBase + 20"
          font-size="12"
          fill="#374151"
          font-weight="bold"
      >Transaction Amount</text>
      <template v-for="series in visibleLowerSeries" :key="series.name">
        <polyline
            :points="series.pointsLower"
            fill="none"
            :stroke="series.color"
            stroke-width="2"
        />
        <circle
            v-for="(val, i) in series.values"
            :key="series.name + '-low-' + i"
            :cx="i * stepWidth + marginOffset"
            :cy="yLowerBase - (val / maxLower) * lowerRange"
            r="3"
            :fill="series.color"
            @mouseenter="showTooltip(series.name, val, i, 'lower')"
            @mouseleave="hideTooltip"
        />
      </template>

      <!-- 개선된 Tooltip -->
      <foreignObject
          v-if="tooltip.visible"
          :x="tooltip.x"
          :y="tooltip.y"
          width="140"
          height="50"
      >
        <div class="tooltip-box">
          <div class="tooltip-content">
            <strong>{{ tooltip.token }}</strong><br />
            {{ tooltip.value }}
          </div>
          <div class="tooltip-arrow"></div>
        </div>
      </foreignObject>
    </svg>
  </div>
</template>

<script setup>
import {
  ref,
  computed,
  onMounted,
  nextTick,
  toRefs,
  inject,
  watch
} from 'vue'

const props = defineProps({
  sectionDates: {
    type: Array,
    required: true
  }
})
const { sectionDates } = toRefs(props)

const selectedNodeType = inject('selectedNodeType')
const visibleTokens = inject('visibleTokens', ref([]))
const allSeriesData = ref({})

const wrapperRef = ref(null)
const svgWidth = ref(600)
const svgHeight = ref(240)

// margins 늘려서 그래프가 container를 넘지 않게
const TRACK_MARGIN = 80
const marginOffset = TRACK_MARGIN / 2

// 0축: 중앙선
const yBase = computed(() => svgHeight.value / 2)
// 0축: 하단선
const yLowerBase = computed(() => svgHeight.value - marginOffset)
// 공통 그래프 높이 범위 (중앙선 ↔ 상단/하단 마진)
const yRange = computed(() => svgHeight.value / 2 - marginOffset)
// 하단 범위 (하단선 ↔ 중앙선)
const lowerRange = computed(() => yBase.value - marginOffset)

// X축 간격
const stepWidth = computed(
    () => (svgWidth.value - TRACK_MARGIN) / (sectionDates.value.length - 1)
)

// 값 스케일링
const maxUpper = computed(() =>
    Math.max(
        ...Object.values(allSeriesData.value).flatMap(s => s.upper || []),
        1
    )
)
const maxLower = computed(() =>
    Math.max(
        ...Object.values(allSeriesData.value).flatMap(s => s.lower || []),
        1
    )
)

// 상단 시리즈
const visibleUpperSeries = computed(() =>
    visibleTokens.value
        .map(token => {
          const s = allSeriesData.value[token]
          if (!s) return null
          return {
            name: token,
            color: s.color,
            values: s.upper,
            pointsUpper: s.upper
                .map((val, i) => {
                  const x = i * stepWidth.value + marginOffset
                  const y = yBase.value - (val / maxUpper.value) * yRange.value
                  return `${x},${y}`
                })
                .join(' ')
          }
        })
        .filter(Boolean)
)

// 하단 시리즈
const visibleLowerSeries = computed(() =>
    visibleTokens.value
        .map(token => {
          const s = allSeriesData.value[token]
          if (!s) return null
          return {
            name: token,
            color: s.color,
            values: s.lower,
            pointsLower: s.lower
                .map((val, i) => {
                  const x = i * stepWidth.value + marginOffset
                  const y =
                      yLowerBase.value - (val / maxLower.value) * lowerRange.value
                  return `${x},${y}`
                })
                .join(' ')
          }
        })
        .filter(Boolean)
)

// Tooltip 상태
const tooltip = ref({
  visible: false,
  token: '',
  value: '',
  x: 0,
  y: 0
})
function showTooltip(token, value, idx, type) {
  // 살짝 위로, 좌우로 중간정렬
  const x = idx * stepWidth.value + marginOffset - 70
  const y =
      (type === 'upper'
          ? yBase.value - (value / maxUpper.value) * yRange.value
          : yLowerBase.value - (value / maxLower.value) * lowerRange.value) - 50
  tooltip.value = { visible: true, token, value, x, y }
}
function hideTooltip() {
  tooltip.value.visible = false
}

// 데이터 로드
async function loadData() {
  const endpoint =
      selectedNodeType.value === 'tx'
          ? '/api/txNode/graph'
          : '/api/tokenNode/graph'

  const res = await fetch(endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
  })
  const raw = await res.json()

  const chainColorMap = {
    cosmos: '#a855f7',
    evm: '#7c3aed',
    terra: '#3b82f6',
    binance: '#facc15',
    unknown: '#9ca3af'
  }

  const temp = {}
  sectionDates.value.forEach((date, idx) => {
    const sec = raw.sections.find(s => s.sectionDate === date)
    if (!sec) return
    sec.tokens.forEach(data => {
      const tk = data.token
      if (!temp[tk]) {
        temp[tk] = {
          upper: Array(sectionDates.value.length).fill(0),
          lower: Array(sectionDates.value.length).fill(0),
          color: chainColorMap[data.ecosystem] || '#888'
        }
      }
      temp[tk].upper[idx] = +data.totalValue || 0
      temp[tk].lower[idx] = +data.transactionAmount || 0
    })
  })

  allSeriesData.value = temp
  visibleTokens.value = Object.keys(temp)
  await nextTick()
}

// 마운트 & 감지
onMounted(async () => {
  await nextTick()
  svgWidth.value = wrapperRef.value.getBoundingClientRect().width
  await loadData()
})
watch(selectedNodeType, loadData)
watch(sectionDates, loadData)
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
.x-axis-checkpoints.top text,
.x-axis-checkpoints.bottom text {
  font-family: sans-serif;
}

/* 개선된 Tooltip 스타일 */
.tooltip-box {
  position: relative;
  pointer-events: none;
}
.tooltip-content {
  background: rgba(0, 0, 0, 0.75);
  color: #fff;
  padding: 6px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
}
.tooltip-arrow {
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 6px solid rgba(0, 0, 0, 0.75);
}
</style>
