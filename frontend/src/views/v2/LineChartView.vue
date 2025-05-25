<template>
  <div class="chart-container">
    <component
        :is="selectedComponent"
        :sectionDates="sectionDates"
    />
  </div>
</template>

<script setup>
import { inject, computed, toRefs } from 'vue'
import LineChartToken from '@/views/v2/LineChartToken.vue'
import LineChartTx    from '@/views/v2/LineChartTx.vue'

const props = defineProps({
  sectionDates: {
    type: Array,
    required: true
  }
})
const { sectionDates } = toRefs(props)

const selectedNodeType = inject('selectedNodeType')
if (!selectedNodeType) throw new Error('selectedNodeType missing')

const selectedComponent = computed(() =>
    selectedNodeType.value === 'tx'
        ? LineChartTx
        : LineChartToken
)
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
