<template>
  <div class="graph-wrapper" ref="wrapperRef">
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <div class="loading-text">⏳ Loading tokens...</div>
    </div>
    <svg
        class="graph-svg"
        :width="svgWidth"
        :height="svgHeight"
    >
      <g :transform="`translate(${centerX}, ${centerY})`">
        <circle
            v-for="(radius, idx) in monthRadii"
            :key="'ring-' + idx"
            :r="radius"
            cx="0"
            cy="0"
            fill="none"
            stroke="#ddd"
            stroke-width="1"
        />
        <circle
            v-for="(node, idx) in computedNodes"
            :key="'node-' + idx"
            :cx="node.xOffset"
            :cy="node.yOffset"
            :r="node.radius"
            :fill="node.fill"
            @click="handleClick(node)"
        />
      </g>
    </svg>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue'

const props = defineProps({
  currentSection: String
})

const wrapperRef = ref(null)
const svgWidth = ref(600)
const svgHeight = ref(600)
const margin = 40

const centerX = computed(() => svgWidth.value / 2)
const centerY = computed(() => svgHeight.value / 2)

const fullTokenData = ref({})
const isLoading = ref(true)

const monthRadii = computed(() => {
  const maxR = Math.min(svgWidth.value, svgHeight.value) / 2 - margin
  return [maxR * 0.3, maxR * 0.7, maxR * 1.2]
})

const ecosystemColor = {
  cosmos: '#a855f7',
  evm: '#7c3aed',
  terra: '#3b82f6',
  binance: '#facc15',
  unknown: '#9ca3af'
}
const brightnessMap = [1, 0.85, 0.7, 0.55, 0.4, 0.25, 0.1]

function getColorWithDarkening(hex, level) {
  const percent = brightnessMap[(+level || 1) - 1] || 1
  const rgb = hex.match(/\w\w/g).map(c => parseInt(c, 16))
  return `rgb(${rgb.map(c => Math.round(c * percent)).join(',')})`
}

function parseTimestampToRadius(timestamp) {
  const date = new Date(timestamp)
  const monthIdx = date.getMonth() % 3
  const baseRadius = monthRadii.value[monthIdx] || 180
  const day = date.getDate()
  return baseRadius + (day - 1) * (30 / 30) // 균일 반영
}

const computedNodes = computed(() => {
  const section = fullTokenData.value[props.currentSection]
  if (!section) return []

  const entries = Object.entries(section)
  const prices = entries.map(([_, t]) => Math.log10(parseFloat(t.price) + 1)).sort((a, b) => a - b)
  const thresholds = [
    prices[Math.floor(prices.length * 0.2)],
    prices[Math.floor(prices.length * 0.4)],
    prices[Math.floor(prices.length * 0.6)],
    prices[Math.floor(prices.length * 0.8)]
  ]

  return entries.map(([token, tokenData]) => {
    const price = Math.log10(parseFloat(tokenData.price) + 1)
    let priceGroup = 0
    while (priceGroup < thresholds.length && price > thresholds[priceGroup]) priceGroup++
    const angle = (priceGroup / 5) * 2 * Math.PI + Math.random() * (Math.PI / 20)
    const radius = parseTimestampToRadius(tokenData.timestamp)
    const x = radius * Math.cos(angle)
    const y = radius * Math.sin(angle)
    const baseColor = ecosystemColor[tokenData.ecosystem] || '#aaa'
    const fill = getColorWithDarkening(baseColor, tokenData.color_darkening)
    const size = 5 + 3 * ((+tokenData.Nodesize || 1) - 1)
    return { name: token, xOffset: x, yOffset: y, radius: size, fill }
  })
})

function handleClick(node) {
  alert(`클릭한 토큰: ${node.name}`)
}

async function fetchTokenData() {
  try {
    isLoading.value = true
    const res = await fetch('/api/tokenNode/graph', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    })
    const raw = await res.json()

    if (Array.isArray(raw.sections)) {
      fullTokenData.value = Object.fromEntries(
          raw.sections.map(entry => [entry.sectionDate, entry.tokens])
      )
      console.log("fullTokenData :", raw)
    } else {
      console.error('⚠️ 예상한 구조가 아님:', raw)
    }
    await nextTick()
  } catch (err) {
    console.error('API 오류:', err)
  } finally {
    isLoading.value = false
  }
}

fetchTokenData()

watch(() => props.currentSection, async (val) => {
  isLoading.value = true
  await nextTick()
  isLoading.value = false
})

onMounted(() => {
  nextTick(() => {
    const rect = wrapperRef.value?.getBoundingClientRect()
    svgWidth.value = rect?.width || 600
    svgHeight.value = rect?.height || 600
  })
})
</script>

<style scoped>
.graph-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}
.graph-svg {
  display: block;
}
.loading-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(255,255,255,0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 10;
}
.loading-spinner {
  border: 4px solid #ccc;
  border-top: 4px solid #6366f1;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  animation: spin 1s linear infinite;
}
.loading-text {
  margin-top: 8px;
  color: #4b5563;
  font-size: 14px;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
