<template>
  <div class="graph-wrapper">
    <svg class="graph-svg" viewBox="0 0 600 600">
      <!-- 중심 이동 그룹 -->
      <g :transform="`translate(${center}, ${center})`">
        <!-- 1. 월별 라인 (3개의 원) -->
        <circle
            v-for="(radius, idx) in monthRadii"
            :key="idx"
            :r="radius"
            cx="0"
            cy="0"
            fill="none"
            stroke="#ddd"
            stroke-width="1"
        />

        <!-- 2. 더미 노드 -->
        <circle
            v-for="(node, idx) in dummyNodes"
            :key="idx"
            :cx="node.xOffset"
            :cy="node.yOffset"
            :r="node.radius"
            :fill="node.color"
            @click="handleClick(node)"
        />
      </g>
    </svg>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

//eslint-disable-next-line no-unused-vars
const svgRef = ref(null)
const center = 250 // 중심 좌표 (viewBox 기준)
const monthRadii = [90, 180, 270] // 원 반지름 (최근 → 과거)

// eslint-disable-next-line no-unused-vars
const daysInMonth = 30
const tokens = [
  { name: 'Token A', year: 2009, month: 2, day: 15, color: 'red' },
  { name: 'Token B', year: 2015, month: 3, day: 8, color: 'green' },
  { name: 'Token C', year: 2021, month: 4, day: 22, color: 'blue' }
]

// 생성일 기준 각도 계산 (12시 방향 시작)
const getAngleFromYearMonth = (year, month) => {
  const startYear = 2009
  const totalMonths = (year - startYear) * 12 + (month - 1)
  return (totalMonths * (360 / 168)) % 360
}

// 최신 → 과거 순서로 매핑
const monthToIndex = { 2: 2, 3: 1, 4: 0 }

const dummyNodes = tokens.map((token) => {
  const baseRadius = monthRadii[monthToIndex[token.month]]
  const nextRadius = monthRadii[monthToIndex[token.month] - 1] || baseRadius + 100 // fallback

  const radiusStep = (nextRadius - baseRadius) / daysInMonth
  const radius = baseRadius + radiusStep * (token.day - 1)

  const angle = getAngleFromYearMonth(token.year, token.month) // 여전히 month 기준 각도
  const rad = (angle * Math.PI) / 180
  const xOffset = radius * Math.cos(rad)
  const yOffset = radius * Math.sin(rad)
  return {
    xOffset,
    yOffset,
    color: token.color,
    radius: 10,
    name: token.name
  }
})

function handleClick(node) {
  alert(`클릭한 노드: ${node.name}`)
}

onMounted(() => {
  console.log('TokenNodeGraph mounted')
})
</script>

<style scoped>
.graph-wrapper {
  width: 100%;
  height: 100%;
  background: none;/*repeating-radial-gradient(
      circle at center,
      #f9f9f9,
      #f9f9f9 10px,
      #f0f0f0 10px,
      #f0f0f0 20px
  );*/
  display: flex;
  justify-content: center;
  align-items: center;
}

.graph-svg {
  width: 100%;
  height: 100%;

}
</style>
