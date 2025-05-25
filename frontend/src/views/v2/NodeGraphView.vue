<!-- src/views/v2/NodeGraphView.vue -->
<template>
  <div class="graph-pane">
    <component
        :is="selectedComponent"
        :currentSection="currentSection"
        @toggleChart="emitToggle"
    />
  </div>
</template>

<script setup>
import { inject, computed } from 'vue'
import TokenNodeGraph   from '@/components/v2/TokenNodeGraph.vue'
import AddressNodeGraph from '@/components/v2/AddressNodeGraph.vue'

const selectedNodeType = inject('selectedNodeType')
const currentSection   = inject('currentSection')

// 부모로 toggleChart 이벤트 올리기
const emit = defineEmits(['toggleChart'])
function emitToggle(address) {
  emit('toggleChart', address)
}

const selectedComponent = computed(() =>
    selectedNodeType.value === 'token'
        ? TokenNodeGraph
        : AddressNodeGraph
)
</script>

<style scoped>
.graph-pane { width:100%; height:100%; }
</style>
