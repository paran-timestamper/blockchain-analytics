<template>
  <div class="graph-container">
    <!-- 조건에 따라 컴포넌트 동적 렌더링 -->
    <component :is="selectedComponent" />
  </div>
</template>

<script>
import { inject, computed } from 'vue'
import TokenNodeGraph from '@/components/v2/TokenNodeGraph.vue'
import AddressNodeGraph from '@/components/v2/AddressNodeGraph.vue'

export default {
  name: 'NodeGraphView',
  components: {
    TokenNodeGraph,
    AddressNodeGraph
  },
  setup() {
    const selectedNodeType = inject('selectedNodeType')

    // 선택된 타입에 따라 동적으로 컴포넌트 지정
    const selectedComponent = computed(() =>
        selectedNodeType.value === 'token' ? TokenNodeGraph : AddressNodeGraph
    )

    return {
      selectedComponent
    }
  }
}
</script>

<style scoped>
.graph-container {
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
