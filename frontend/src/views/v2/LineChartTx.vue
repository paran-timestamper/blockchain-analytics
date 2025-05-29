<template>
  <div class="chart-wrapper" ref="wrapperRef">
    <svg
        class="chart-svg"
        :viewBox="`0 0 ${svgWidth} ${svgHeight}`"
        preserveAspectRatio="none"
    >
      <!-- 중앙선 -->
      <line
          :x1="marginOffset"
          :x2="svgWidth - marginOffset"
          :y1="yBase"
          :y2="yBase"
          stroke="#d1d5db"
          stroke-width="1"
      />

      <!-- 상단 X축 + 레이블 -->
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

      <!-- Total Value 그래프 -->
      <text
          :x="marginOffset"
          y="20"
          font-size="12"
          fill="#374151"
          font-weight="bold"
      >Total Value</text>
      <template v-for="series in visibleUpperSeries" :key="series.address">
        <polyline
            :points="series.pointsUpper"
            fill="none"
            :stroke="series.color"
            stroke-width="2"
        />
        <circle
            v-for="(v, i) in series.upper"
            :key="series.address + '-up-' + i"
            :cx="i * stepWidth + marginOffset"
            :cy="yBase - (v / maxUpper) * yRange"
            r="3"
            :fill="series.color"
            @mouseenter="showTooltip(series.address, v, i, 'upper')"
            @mouseleave="hideTooltip"
        />
      </template>

      <!-- 하단 축선 -->
      <line
          :x1="marginOffset"
          :x2="svgWidth - marginOffset"
          :y1="yLowerBase"
          :y2="yLowerBase"
          stroke="#d1d5db"
          stroke-width="1"
      />

      <!-- 하단 X축 + 레이블 -->
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

      <!-- Transaction Amount 그래프 -->
      <text
          :x="marginOffset"
          :y="yBase + 20"
          font-size="12"
          fill="#374151"
          font-weight="bold"
      >Transaction Amount</text>
      <template v-for="series in visibleLowerSeries" :key="series.address">
        <polyline
            :points="series.pointsLower"
            fill="none"
            :stroke="series.color"
            stroke-width="2"
        />
        <circle
            v-for="(v, i) in series.lower"
            :key="series.address + '-low-' + i"
            :cx="i * stepWidth + marginOffset"
            :cy="yLowerBase - (v / maxLower) * lowerRange"
            r="3"
            :fill="series.color"
            @mouseenter="showTooltip(series.address, v, i, 'lower')"
            @mouseleave="hideTooltip"
        />
      </template>

      <!-- Tooltip -->
      <foreignObject
          v-if="tooltip.visible"
          pointer-events="none"
          :x="tooltip.x"
          :y="tooltip.y"
          width="200"
          height="60"
      >
        <div class="tooltip-box">
          <div class="tooltip-content">
            <strong>{{ tooltip.address }}</strong><br/>
            {{ tooltip.field }}: {{ tooltip.value }}
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
  inject,
  watch
} from 'vue'

// Props
const props = defineProps({
  sectionDates: {
    type: Array,
    required: true
  }
})

// 부모 제공값
const fromAddress      = inject('fromAddress', ref(null))
const visibleAddresses = inject('visibleAddresses', ref(new Set()))

// API 데이터 저장
const allSeriesData = ref({})

// SVG 크기
const wrapperRef = ref(null)
const svgWidth   = ref(600)
const svgHeight  = ref(240)

// margin
const TRACK_MARGIN = 80
const marginOffset = TRACK_MARGIN / 2

// y축 기준선
const yBase      = computed(() => svgHeight.value / 2)
const yLowerBase = computed(() => svgHeight.value - marginOffset)
const yRange     = computed(() => svgHeight.value / 2 - marginOffset)
const lowerRange = computed(() => yBase.value - marginOffset)

// x축 스텝 너비
const stepWidth = computed(() =>
    (svgWidth.value - TRACK_MARGIN) / (props.sectionDates.length - 1)
)

// max 값
const maxUpper = computed(() =>
    Math.max(...Object.values(allSeriesData.value).flatMap(s => s.upper), 1)
)
const maxLower = computed(() =>
    Math.max(...Object.values(allSeriesData.value).flatMap(s => s.lower), 1)
)

// 시리즈 계산
const visibleUpperSeries = computed(() =>
    Object.entries(allSeriesData.value)
        .filter(([addr]) => visibleAddresses.value.has(addr))
        .map(([address, s]) => ({
          address,
          color: s.color,
          upper: s.upper,
          pointsUpper: s.upper
              .map((v, i) =>
                  `${i * stepWidth.value + marginOffset},${yBase.value - (v / maxUpper.value) * yRange.value}`
              )
              .join(' ')
        }))
)
const visibleLowerSeries = computed(() =>
    Object.entries(allSeriesData.value)
        .filter(([addr]) => visibleAddresses.value.has(addr))
        .map(([address, s]) => ({
          address,
          color: s.color,
          lower: s.lower,
          pointsLower: s.lower
              .map((v, i) =>
                  `${i * stepWidth.value + marginOffset},${yLowerBase.value - (v / maxLower.value) * lowerRange.value}`
              )
              .join(' ')
        }))
)

// Tooltip 상태에 field 추가
const tooltip = ref({
  visible: false,
  address: '',
  field: '',
  value: 0,
  x: 0,
  y: 0
})
function showTooltip(address, value, idx, type) {
  const field = type === 'upper' ? 'Total Value' : 'Transaction Amount'
  const x = idx * stepWidth.value + marginOffset - 70
  const y =
      (type === 'upper'
          ? yBase.value - (value / maxUpper.value) * yRange.value
          : yLowerBase.value - (value / maxLower.value) * lowerRange.value) -
      70  // 툴팁을 좀 더 위로
  tooltip.value = { visible: true, address, field, value, x, y }
}
function hideTooltip() {
  tooltip.value.visible = false
}

// 데이터 로드
async function loadData() {
  const res = await fetch('/api/transaction/graph', {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify({ fromAddress: fromAddress.value })
  })

  const raw = await res.json()
  const COLORS = {
    cosmos: '#a855f7',
    evm:    '#7c3aed',
    terra:  '#3b82f6',
    binance:'#facc15',
    unknown:'#9ca3af'
  }

  const temp = {}
  props.sectionDates.forEach((date, idx) => {
    const dateKey = date.replace('-', '_')
    const sec = raw.sections.find(s => s.sectionDate === dateKey)
    if (!sec) return
    sec.tokens.forEach(data => {
      const addr = data.toAddress
      if (!temp[addr]) {
        temp[addr] = {
          upper: Array(props.sectionDates.length).fill(0),
          lower: Array(props.sectionDates.length).fill(0),
          color: COLORS[data.ecosystem] || '#888'
        }
      }
      temp[addr].upper[idx] = +data.totalValue || 0
      temp[addr].lower[idx] = +data.transactionAmount || 0
    })
  })

  allSeriesData.value = temp

  // 모든 주소 기본 표시
  if (visibleAddresses.value.size === 0) {
    Object.keys(temp).forEach(addr => visibleAddresses.value.add(addr))
  }

  await nextTick()
}

// 초기화 & 감지
onMounted(async () => {
  await nextTick()
  svgWidth.value = wrapperRef.value.getBoundingClientRect().width
  await loadData()
})
watch(() => props.sectionDates, loadData)
</script>

<style scoped>
.chart-wrapper { width:100%; height:100%; overflow:visible; }
.chart-svg     { width:100%; height:100%; overflow:visible; }

.x-axis-checkpoints line { stroke-linecap:round; }

/* Tooltip */
.tooltip-box    { position:relative; pointer-events:none; }
.tooltip-content {
  background:rgba(0,0,0,0.75);
  color:#fff;
  padding:6px 8px;
  border-radius:4px;
  font-size:12px;
  white-space:nowrap;
}
.tooltip-arrow {
  position:absolute; bottom:-6px; left:50%;
  transform:translateX(-50%);
  width:0; height:0;
  border-left:6px solid transparent;
  border-right:6px solid transparent;
  border-top:6px solid rgba(0,0,0,0.75);
}
</style>
