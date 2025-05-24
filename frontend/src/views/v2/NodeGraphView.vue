<script>
import { inject, computed } from 'vue'
import TokenNodeGraph from '@/components/v2/TokenNodeGraph.vue'
import AddressNodeGraph from '@/components/v2/AddressNodeGraph.vue'

export default {
  name: 'NodeGraphView',
  components: { TokenNodeGraph, AddressNodeGraph },
  setup() {
    const selectedNodeType = inject('selectedNodeType')
    const currentSection = inject('currentSection')

    const selectedComponent = computed(() =>
        selectedNodeType.value === 'token' ? TokenNodeGraph : AddressNodeGraph
    )

    return { selectedComponent, currentSection }
  }
}
</script>

<template>
  <div class="graph-container">
    <component :is="selectedComponent" :currentSection="currentSection" />
  </div>
</template>

<style scoped>
.graph-container {
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
