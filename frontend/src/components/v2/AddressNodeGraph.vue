<template>
  <div class="address-node-graph">
    <div ref="graphContainer" class="graph-content">
      <!-- 검색바 (왼쪽 상단) -->
      <div class="search-bar-overlay">
        <input
            v-model="queryAddress"
            type="text"
            placeholder="Enter fromAddress"
            @keyup.enter="onSearch"
        />
        <button @click="onSearch">Search</button>
      </div>

      <!-- Force Simulation 기반 노드 그래프 -->
      <svg v-if="!loading" :width="svgWidth" :height="svgHeight">
        <!-- 필터링된 링크 렌더링 -->
        <line
            v-for="(link, idx) in filteredLinks"
            :key="idx"
            :x1="link.source.x"
            :y1="link.source.y"
            :x2="link.target.x"
            :y2="link.target.y"
            stroke="#999"
            stroke-opacity="0.6"
        />

        <!-- 필터링된 노드 및 라벨 렌더링 -->
        <g v-for="node in filteredNodes" :key="node.id">
          <circle
              :cx="node.x"
              :cy="node.y"
              :r="node.r"
              :fill="node.color"
              :class="{ 'muted-node': hiddenAddresses.has(node.id) }"
              @mouseenter="onCircleMouseEnter(node, $event)"
              @mousemove="onCircleMouseMove(node, $event)"
              @mouseleave="onCircleMouseLeave"
              @click="onCircleClick(node)"
              @contextmenu.prevent="onCircleRightClick(node)"
          />
          <text
              :x="node.x"
              :y="node.y"
              text-anchor="middle"
              alignment-baseline="middle"
              font-size="6"
              fill="#000"
              pointer-events="none"
          >
            {{ node.id.slice(0, 6) }}
          </text>
        </g>

        <!-- ► 설명란 (오른쪽 공백) -->
        <g :transform="`translate(${svgWidth - 250}, 40)`">
          <text x="0" y="0" font-size="14" font-weight="bold" fill="#374151">📘 설명</text>
          <!-- 1. 체인별 색상 -->
          <text x="0" y="28" font-size="12" fill="#6b7280">🔷 체인별 색상(트랜잭션에서 사용한 블록체인)</text>
          <text x="10" y="48" font-size="12" fill="#a855f7">cosmos</text>
          <text x="10" y="66" font-size="12" fill="#a8329e">evm</text>
          <text x="10" y="84" font-size="12" fill="#3b82f6">terra</text>
          <text x="10" y="102" font-size="12" fill="#facc15">binance</text>
          <text x="10" y="120" font-size="12" fill="#9ca3af">unknown</text>

          <!-- 2. 노드 크기 1~3단계 -->
          <text x="0" y="150" font-size="12" fill="#6b7280">🔵 노드 크기(거래대금에 따른 분류)</text>
          <text x="10" y="170" font-size="12" fill="#6b7280">1단계 (작음)</text>
          <text x="10" y="188" font-size="12" fill="#6b7280">... 중간 ...</text>
          <text x="10" y="206" font-size="12" fill="#6b7280">3단계 (큼)</text>

          <!-- 3. depth-2 기준 -->
          <text x="0" y="236" font-size="12" fill="#6b7280">📈 depth2 기준(거래대금에 따른 추가 노드)</text>
          <text x="10" y="256" font-size="12" fill="#6b7280">거래대금 상위 5개 계정</text>
        </g>
      </svg>

      <div v-else class="loading-indicator">Loading...</div>

      <!-- Tooltip -->
      <div
          v-if="tooltip.visible"
          class="node-tooltip"
          :style="{ top: tooltip.y, left: tooltip.x }"
      >
        <div><strong>{{ tooltip.address }}</strong></div>
        <div>Token: {{ tooltip.token }}</div>
        <div>Ecosystem: {{ tooltip.ecosystem }}</div>
        <div>Value: {{ tooltip.value }}</div>
      </div>

      <!-- AddressPopup 렌더링 -->
      <AddressPopup
          v-if="popup.visible"
          :address="popup.address"
          @close="popup.visible = false"
      />
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted, watch, computed, nextTick, inject } from 'vue'
import * as d3 from 'd3'
import AddressPopup from '@/components/v2/AddressPopup.vue'

// Props & emit
const props = defineProps({
  currentSection: { type: String, default: '' }
})
const emit = defineEmits(['toggleChart', 'openPopup'])

// Inject depth filters
const showDepth1 = inject('showDepth1', ref(true))
const showDepth2 = inject('showDepth2', ref(true))

// 검색어 및 API 인자
const queryAddress = ref('')
const fromAddress  = ref(null)
const sections     = ref([])

// 그래프 데이터
const nodes   = ref([])
const links   = ref([])
const loading = ref(false)
const graphContainer = ref(null)
const svgWidth  = ref(0)
const svgHeight = ref(0)

// 숨김 상태 관리 (좌클릭 토글)
const hiddenAddresses = ref(new Set())
function onCircleClick(node) {
  if (hiddenAddresses.value.has(node.id)) {
    hiddenAddresses.value.delete(node.id)
  } else {
    hiddenAddresses.value.add(node.id)
  }
  emit('toggleChart', node.id)
}

// 팝업 상태 관리 (우클릭)
const popup = ref({ visible: false, address: null })
function onCircleRightClick(node) {
  popup.value.address = node.id
  popup.value.visible = true
}

// Tooltip 상태
const tooltip = ref({
  visible: false,
  x: '0px',
  y: '0px',
  address: '',
  token: '',
  ecosystem: '',
  value: null
})
function onCircleMouseEnter(node, event) { updateTooltip(node, event) }
function onCircleMouseMove(node, event)  { updateTooltip(node, event) }
function onCircleMouseLeave()            { tooltip.value.visible = false }
const TOOLTIP_HEIGHT = 40
function updateTooltip(node, event) {
  const rect = graphContainer.value.getBoundingClientRect()
  tooltip.value = {
    visible: true,
    x: `${event.clientX - rect.left + 8}px`,
    y: `${event.clientY - rect.top + 8}px`,
    address: node.id,
    token: node.token || 'N/A',
    ecosystem: node.ecosystem || 'N/A',
    value: node.value != null ? node.value : 'N/A'
  }
}

// 색상 매핑
const ecosystemColor = {
  cosmos: '#a855f7',
  evm:    '#a8329e',
  terra:  '#3b82f6',
  binance:'#facc15',
  unknown:'#9ca3af'
}

// 중앙 좌표 계산
const centerX = computed( () => svgWidth.value * 0.4)
const centerY = computed(() => svgHeight.value * 0.5)
const sectionKey = computed(() =>
    props.currentSection.replace(/-/g, '_')
)

// nodeSize 스케일 정의
const primaryScale = computed(() => {
  const sizes = sections.value.flatMap(s =>
      s.tokens.map(d => +d.nodeSize || 1)
  )
  const [min, max] = d3.extent(sizes)
  return d3.scaleLinear().domain([min, max]).range([4, 20])
})
const secondaryScale = computed(() => {
  const sizes = sections.value.flatMap(s =>
      s.tokens.flatMap(d =>
          d.secondToAddresses.map(s2 => +s2.nodeSize || 1)
      )
  )
  const [min, max] = d3.extent(sizes)
  return d3.scaleLinear().domain([min, max]).range([2, 10])
})

// 컨테이너 크기 업데이트
function updateSize() {
  const rect = graphContainer.value.getBoundingClientRect()
  svgWidth.value  = rect.width
  svgHeight.value = rect.height
}

// Graph 구성 (raw nodes + links)
function buildGraph() {
  const sec = sections.value.find(s => s.sectionDate === sectionKey.value)
      || { tokens: [], fromAddress: null }
  if (sec.fromAddress && !fromAddress.value) {
    fromAddress.value = sec.fromAddress
  }

  nodes.value = []
  links.value = []

  // 중앙 노드
  nodes.value.push({
    id: fromAddress.value || 'GLOBAL',
    token: '',
    r: primaryScale.value(1),
    color: '#3b82f6',
    x: centerX.value,
    y: centerY.value,
    ecosystem: '',
    value: null
  })

  // Primary & Secondary with depth filters
  sec.tokens.slice(0, 100).forEach(d => {
    // depth1 노드
    if (showDepth1.value) {
      const p = {
        id: d.toAddress,
        token: d.token,
        r: primaryScale.value(+d.nodeSize || 1),
        color: ecosystemColor[d.ecosystem] || '#999',
        x: centerX.value,
        y: centerY.value,
        ecosystem: d.ecosystem,
        value: d.totalValue
      }
      nodes.value.push(p)
      links.value.push({ source: nodes.value[0], target: p })

      // depth2 노드
      if (showDepth2.value) {
        d.secondToAddresses.slice(0, 10).forEach((s2, i) => {
          const sNode = {
            id: `${d.toAddress}-${i}`,
            token: s2.token,
            r: secondaryScale.value(+s2.nodeSize || 1),
            color: ecosystemColor[s2.ecosystem] || '#999',
            x: centerX.value,
            y: centerY.value,
            ecosystem: s2.ecosystem,
            value: s2.value
          }
          nodes.value.push(sNode)
          links.value.push({ source: p, target: sNode })
        })
      }
    }
  })
}

// depth 필터된 computed
const filteredNodes = computed(() =>
    nodes.value.filter(n => {
      // 항상 중앙 노드
      if (n.id === fromAddress.value || n.id === 'GLOBAL') return true
      const parts = n.id.split('-')
      // depth1인지 확인
      if (parts.length === 1) {
        return showDepth1.value
      }
      // depth2
      return showDepth2.value
    })
)
const filteredLinks = computed(() =>
    links.value.filter(l =>
        filteredNodes.value.includes(l.source) &&
        filteredNodes.value.includes(l.target)
    )
)

// D3 Simulation
let simulation
function startSimulation() {
  if (simulation) simulation.stop()
  simulation = d3.forceSimulation(nodes.value)
      .force('link',
          d3.forceLink(links.value).id(d => d.id)
              .distance(l =>
                  l.source.id === nodes.value[0].id ? 60 : 5
              )
              .strength(0.5)
      )
      .force('charge', d3.forceManyBody().strength(-150))
      .force('center', d3.forceCenter(centerX.value, centerY.value))
      .force('collide', d3.forceCollide().radius(d => d.r + 2))
      .on('tick', () => {
        nodes.value.forEach(n => {
          n.x = Math.max(n.r, Math.min(svgWidth.value - n.r, n.x))
          n.y = Math.max(n.r, Math.min(svgHeight.value - n.r, n.y))
        })
        nodes.value = [...nodes.value]
        links.value = [...links.value]
      })

  const svg = d3.select(graphContainer.value).select('svg')
  svg.selectAll('circle')
      .call(
          d3.drag()
              .on('start', (e, d) => {
                if (!e.active) simulation.alphaTarget(0.3).restart()
                d.fx = d.x; d.fy = d.y
              })
              .on('drag', (e, d) => { d.fx = e.x; d.fy = e.y })
              .on('end', (e, d) => {
                if (!e.active) simulation.alphaTarget(0)
                d.fx = null; d.fy = null
              })
      )
}

// Load Data
async function loadData() {
  loading.value = true
  try {
    const res = await fetch('/api/transaction/graph', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fromAddress: fromAddress.value })
    })

    const data = await res.json()
    if (data.fromAddress) fromAddress.value = data.fromAddress
    sections.value = data.sections || []
    await nextTick()
    updateSize()
    buildGraph()
    startSimulation()
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

function onSearch() {
  fromAddress.value = queryAddress.value.trim() || null
  loadData()
}

// 초기화 & 필터 변경 감지
onMounted(() => {
  updateSize()
  loadData()
  window.addEventListener('resize', updateSize)
})
watch([showDepth1, showDepth2, sectionKey], () => {
  buildGraph()
  startSimulation()
})
</script>

<style scoped>
.address-node-graph {
  width: 100%;
  height: 100%;
}

.graph-content {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.search-bar-overlay {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  gap: 6px;
  background: rgba(255, 255, 255, 0.9);
  padding: 6px;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.search-bar-overlay input {
  flex: 1;
  padding: 4px 6px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}

.search-bar-overlay button {
  padding: 4px 8px;
  background: #f0f0f0;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.loading-indicator {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 16px;
  color: #666;
}

.node-tooltip {
  position: absolute;
  pointer-events: none;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 8px;
  border-radius: 6px;
  font-size: 12px;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.muted-node {
  opacity: 0.3;
}
</style>
