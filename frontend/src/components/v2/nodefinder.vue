<template>
  <div class="token-type-box box">
    <h4 class="section-title">Node Type</h4>
    <label class="checkbox">
      <input type="checkbox" v-model="smallValue" />
      smallvalue
    </label>
    <label class="checkbox">
      <input type="checkbox" v-model="largeValue" />
      largevalue
    </label>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const smallValue = ref(false)
const largeValue = ref(false)
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
