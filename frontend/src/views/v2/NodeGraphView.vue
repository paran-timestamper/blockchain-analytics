<script setup>
import { inject, computed } from 'vue'
import TokenNodeGraph from '@/components/v2/TokenNodeGraph.vue'
import AddressNodeGraph from '@/components/v2/AddressNodeGraph.vue'

const selectedNodeType = inject('selectedNodeType')
const selectedComponent = computed(() =>
    selectedNodeType.value === 'token' ? TokenNodeGraph : AddressNodeGraph
)
</script>

<template>
  <div class="graph-container">
    <component :is="selectedComponent" />
  </div>
</template>

<style scoped>
.graph-container {
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
