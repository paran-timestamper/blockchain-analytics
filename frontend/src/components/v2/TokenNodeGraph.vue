<template>
  <div class="graph-wrapper" ref="wrapperRef">
    <!-- Loading overlay -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <div class="loading-text">⏳ Loading tokens...</div>
    </div>

    <!-- SVG canvas -->
    <svg class="graph-svg" :width="svgWidth" :height="svgHeight">
      <!-- Left: radial layout group -->
      <g :transform="`translate(${centerX - 150}, ${centerY})`">
        <!-- Radial guide lines and labels -->
        <line
            v-for="i in 5" :key="'angle-line-' + i"
            x1="0" y1="0"
            :x2="Math.cos((i - 1) * 2 * Math.PI / 5) * maxRadius"
            :y2="Math.sin((i - 1) * 2 * Math.PI / 5) * maxRadius"
            stroke="#6b7280" stroke-dasharray="4 2" stroke-width="2"
        />
        <text
            v-for="i in 5" :key="'angle-label-' + i"
            :x="Math.cos((i - 1 + 0.5) * 2 * Math.PI / 5) * (maxRadius - 10)"
            :y="Math.sin((i - 1 + 0.5) * 2 * Math.PI / 5) * (maxRadius - 10)"
            font-size="10" text-anchor="middle" fill="#6b7280"
        >
          Section {{ i }}
        </text>

        <!-- Concentric rings and labels -->
        <circle
            v-for="i in 3" :key="'ring-' + i"
            :r="i * monthRadiusStep"
            fill="none" stroke="#ddd" stroke-width="1"
        />
        <text
            v-for="i in 3" :key="'ring-label-' + i"
            x="0" :y="-i * monthRadiusStep + 14"
            font-size="10" text-anchor="middle" fill="#6b7280"
        >
          {{ monthLabels[i - 1] }}
        </text>

        <!-- Token nodes -->
        <template v-for="node in animatedNodes" :key="node.name">
          <circle
              :cx="node.x"
              :cy="node.y"
              :r="node.r"
              :fill="node.fill"
              :opacity="node.alpha"
              :class="{ 'active-node': selectedTokens.has(node.name) }"
              @contextmenu.prevent="toggleSelect(node)"
          />
          <text
              v-if="node.r > 10"
              :x="node.x"
              :y="node.y + 4"
              font-size="10"
              text-anchor="middle"
              fill="white"
              style="pointer-events: none"
          >
            {{ node.name }}
          </text>
        </template>
      </g>

      <!-- Right: legend -->
      <g :transform="`translate(${svgWidth - 300}, 40)`">
        <text x="-10" y="0" font-size="14" font-weight="bold" fill="#374151">📘 설명</text>
        <text x="-10" y="28" font-size="12" fill="#6b7280">🔷 체인별 색상(트랜잭션 발생 시 어떤 블록체인을 이용했나)</text>
        <text x="10" y="48" font-size="12" fill="#a855f7">cosmos</text>
        <text x="10" y="66" font-size="12" fill="#7c3aed">evm</text>
        <text x="10" y="84" font-size="12" fill="#3b82f6">terra</text>
        <text x="10" y="102" font-size="12" fill="#facc15">binance</text>
        <text x="10" y="120" font-size="12" fill="#9ca3af">unknown</text>

        <text x="-10" y="150" font-size="12" fill="#6b7280">🔵 노드 크기 = 거래 대금(3개월동안 토큰 거래량)</text>
        <text x="10" y="170" font-size="12" fill="#6b7280">nodeSize 1 → 작음</text>
        <text x="10" y="188" font-size="12" fill="#6b7280">nodeSize 7 → 큼</text>

        <text x="0" y="218" font-size="12" fill="#6b7280">📈 Section 기준(현재 토큰 가격 기준)</text>
        <text x="10" y="238" font-size="12" fill="#6b7280">Section 1: 하위 20%</text>
        <text x="10" y="256" font-size="12" fill="#6b7280">Section 5: 상위 20%</text>

        <text x="0" y="284" font-size="12" fill="#6b7280">🌑 색상 진하기(트랜잭션량이 얼마나 많은지)</text>
        <text x="10" y="304" font-size="12" fill="#6b7280">1단계: 연함 … 7단계: 진함</text>
      </g>
    </svg>


    <!-- Compare popup -->

  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick, onMounted, inject, reactive as toReactive } from 'vue'
import gsap from 'gsap'
import TokenPopup from '@/components/v2/TokenPopup.vue'
import {useNodeStore} from "@/store/nodeStore";

// props & visible list
const emit = defineEmits(['show-token-popup'])
const props = defineProps({ currentSection: String })
const visibleTokens = inject('visibleTokens', ref([]))

// layout & loading state
const wrapperRef       = ref(null)
const svgWidth         = ref(600)
const svgHeight        = ref(680)
const isLoading        = ref(true)
const fullTokenData    = ref({})
const animatedNodes    = ref([])

// selected tokens for compare
const selectedTokens   = toReactive(new Set())
const showComparePopup = ref(false)
const comparePair      = ref(['',''])

// compute centers & radii
const centerX         = computed(() => svgWidth.value / 2)
const centerY         = computed(() => svgHeight.value / 2)
const maxRadius       = computed(() => svgHeight.value / 2 - 10)
const monthRadiusStep = computed(() => maxRadius.value / 3)
const monthLabels     = computed(() => {
  if (!props.currentSection) return []
  const [y,m] = props.currentSection.split('-').map(Number)
  return [2,1,0].map(i => {
    const d = new Date(y, m -1 + i, 1)
    const start = `${d.getFullYear()}년 ${d.getMonth()+1}월 1일`
    const end   = `${d.getFullYear()}년 ${d.getMonth()+1}월 ${new Date(d.getFullYear(), d.getMonth()+1,0).getDate()}일`
    return `${start} ~ ${end}`
  })
})

// base colors
const ecosystemColor = {
  cosmos: '#a855f7',
  evm:    '#7c3aed',
  terra:  '#3b82f6',
  binance:'#facc15',
  unknown:'#9ca3af'
}

// helper math
function hashToAngle(name) {
  return ([...name].reduce((a,c)=>a+c.charCodeAt(0),0) % 1000) / 1000
}
function daysDiff(a,b) {
  return Math.floor((b - a)/(1000*60*60*24))
}
function getRefDate(tokens) {
  return new Date(Math.max(...Object.values(tokens).map(t=>new Date(t.timestamp).getTime())))
}

// build target node array
function computeTargetNodes(tokens) {
  const refD    = getRefDate(tokens)
  const entries = Object.entries(tokens)
  const prices  = entries.map(([_,t])=>Math.log10(+t.price+1)).sort((a,b)=>a-b)
  const thr     = [0.2,0.4,0.6,0.8].map(f=>prices[Math.floor(prices.length*f)])
  return entries.map(([tk,d]) => {
    const lp    = Math.log10(+d.price+1)
    let grp     = thr.findIndex(t=>lp<=t)
    if (grp<0) grp = thr.length
    const ang   = ((grp + hashToAngle(tk)) / 5)*2*Math.PI
    const rad   = daysDiff(new Date(d.timestamp), refD)*(monthRadiusStep.value/30)
    // alpha ∈ [1/7 .. 7/7]
    const dark  = +d.colorDarkening || 7
    const alpha = Math.max(1, Math.min(dark,7)) / 7
    return {
      name: tk,
      x:    rad * Math.cos(ang),
      y:    rad * Math.sin(ang),
      r:    6 + (Math.max(1, Math.min(+d.nodeSize||1,7)) - 1)*6,
      fill: ecosystemColor[d.ecosystem] || ecosystemColor.unknown,
      alpha
    }
  })
}

// animate nodes in/out
function animateTo(tokens) {
  const all = computeTargetNodes(tokens)
      .filter(n => visibleTokens.value.includes(n.name))
  all.forEach(t => {
    let ex = animatedNodes.value.find(n=>n.name===t.name)
    if (!ex) {
      animatedNodes.value.push(toReactive(t))
    } else {
      gsap.to(ex, { x: t.x, y: t.y, r: t.r, opacity: t.alpha, duration: 1, ease: 'power2.out' })
    }
  })
  const keep = new Set(all.map(n=>n.name))
  animatedNodes.value = animatedNodes.value.filter(n=>keep.has(n.name))
}

// initial load
onMounted(async () => {
  await nextTick()
  const r = wrapperRef.value.getBoundingClientRect()
  svgWidth.value  = r.width
  svgHeight.value = r.height

  try {
    const res = await fetch('/api/tokenNode/graph', { method: 'POST' })
    const raw = await res.json()
    fullTokenData.value = Object.fromEntries(
        raw.sections.map(({ sectionDate, tokens }) =>
            [ sectionDate, Object.fromEntries(tokens.map(t=>[t.token,t])) ]
        )
    )
    const toks = fullTokenData.value[props.currentSection]
    if (toks) animateTo(toks)
  } catch(e) {
    console.error(e)
  } finally {
    isLoading.value = false
  }
})

// watch for section or visibility changes
watch(() => props.currentSection, async sec => {
  isLoading.value = true
  await nextTick()
  const toks = fullTokenData.value[sec]
  if (toks) animateTo(toks)
  isLoading.value = false
})
watch(visibleTokens, () => {
  const toks = fullTokenData.value[props.currentSection]
  if (toks) animateTo(toks)
})

// right-click selection toggle
function toggleSelect(node) {
  const name = node.name
  const nodeStore = useNodeStore()
  nodeStore.node = name

  if (selectedTokens.has(name)) {
    selectedTokens.delete(name)
  } else {
    selectedTokens.add(name)
  }
  if (selectedTokens.size === 2) {
    comparePair.value = Array.from(selectedTokens)
    showComparePopup.value = true
    emit('show-token-popup', {
      tokenA: comparePair.value[0],
      tokenB: comparePair.value[1]
    })
    selectedTokens.clear()
  }
}

// reset selection on popup close
function resetSelection() {
  selectedTokens.clear()
  showComparePopup.value = false
}
</script>

<style scoped>
.graph-wrapper {
  width: 100%; height: 100%; position: relative;
}
.graph-svg {
  display: block;
}
/* blink animation on active nodes */
@keyframes blink {
  0%,100% { opacity: 1; }
  50%     { opacity: 0.3; }
}
.active-node {
  animation: blink 1s infinite;
  stroke: #fff;
  stroke-width: 2;
}

/* loading overlay */
.loading-overlay {
  position: absolute; inset: 0;
  background: rgba(255,255,255,0.7);
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
}
.loading-spinner {
  border: 4px solid #ccc;
  border-top: 4px solid #6366f1;
  border-radius: 50%;
  width: 32px; height: 32px;
  animation: spin 1s linear infinite;
}
.loading-text {
  margin-top: 8px; color: #4b5563; font-size: 14px;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>
