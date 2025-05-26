<!-- src/components/v2/nodefinder.vue -->
<template>
  <div class="token-type-box box">
    <h4 class="section-title">Node Type</h4>
    <label class="checkbox">
      <input type="checkbox" v-model="showDepth1" />
      smallvalue
    </label>
    <label class="checkbox">
      <input type="checkbox" v-model="showDepth2" />
      largevalue
    </label>
  </div>
</template>

<script setup>
import { ref,inject,provide } from 'vue'
const smallValue = ref(true)
const largeValue = ref(true)
// 상위(SidebarView.vue)에서 provide한 값을 inject
provide('showDepth1', smallValue)
provide('showDepth2', largeValue)
const showDepth1 = inject('showDepth1')
const showDepth2 = inject('showDepth2')
</script>

<style scoped>
.token-type-box {
  max-height: 220px;
}
.checkbox {
  font-size: 12px;
  margin: 4px 0;
  display: block;
}
.section-title {
  font-weight: bold;
  margin-bottom: 6px;
}
</style>
