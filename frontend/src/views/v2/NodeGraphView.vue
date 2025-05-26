<!-- src/views/v2/NodeGraphView.vue -->
<template>
  <div class="graph-pane">
    <!-- 실제 토큰/주소 그래프 -->
    <component
        :is="selectedComponent"
        :currentSection="currentSection"
        @toggleChart="emitToggle"
        @show-token-popup="handleTokenPopup"
    />

    <!-- TokenPopup: 토큰 두 개 선택 시 표시 -->
    <TokenPopup
        v-if="showPopup"
        :a-token="aToken"
        :b-token="bToken"
        @close="closePopup"
    />
  </div>
</template>

<script setup>
import { ref, inject, computed } from 'vue'
import TokenNodeGraph from '@/components/v2/TokenNodeGraph.vue'
import AddressNodeGraph from '@/components/v2/AddressNodeGraph.vue'
import TokenPopup from '@/components/v2/TokenPopup.vue'

// 상태 주입
const selectedNodeType = inject('selectedNodeType')
const currentSection = inject('currentSection')

// 부모에 toggleChart 전달
const emit = defineEmits(['toggleChart'])
function emitToggle(address) {
  emit('toggleChart', address)
}

// 선택된 컴포넌트 동적 분기
const selectedComponent = computed(() =>
    selectedNodeType.value === 'token' ? TokenNodeGraph : AddressNodeGraph
)

// ✅ 팝업 상태
const showPopup = ref(false)
const aToken = ref('')
const bToken = ref('')

// ✅ 토큰 팝업 열기 (TokenNodeGraph.vue에서 emit함)
function handleTokenPopup({ tokenA, tokenB }) {
  console.log('[NodeGraphView] 받은 토큰 A:', tokenA, 'B:', tokenB)
  aToken.value = tokenA
  bToken.value = tokenB

  showPopup.value = true
}

// ✅ 닫기 핸들러
function closePopup() {
  showPopup.value = false
}
</script>

<style scoped>
.graph-pane {
  width: 100%;
  height: 100%;
}
</style>
