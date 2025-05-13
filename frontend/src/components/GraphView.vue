<template>
  <div class="graph-wrapper">
    <div class="top-controls">
      <input type="date" v-model="selectedDate" class="calendar-input" />
      <button @click="graphType = 'line'">📈 Line Graph</button>
      <button @click="graphType = 'treemap'">🗺️ Treemap</button>
    </div>

    <!-- 조건부 하위 컨트롤 -->
    <div v-if="graphType === 'line'" class="line-controls">
      <button @click="metric = 'txCount'">🧮 트랜잭션 수</button>
      <button @click="metric = 'value'">💰 거래 대금</button>
    </div>

    <!-- 시각화 영역 -->
    <LineGraph
        v-if="graphType === 'line' && selectedDate"
        :address="address"
        :metric="metric"
        :date="selectedDate"
    />
    <Treemap
        v-if="graphType === 'treemap' && selectedDate"
        :address="address"
        :date="selectedDate"
    />
  </div>
</template>

<script>
import LineGraph from "@/components/BrokenLineGraph.vue"; // ✅ 실제 파일명 기준
import Treemap from "@/components/Treemap.vue";

export default {
  name: "GraphView",
  components: {
    LineGraph,
    Treemap
  },
  props: {
    address: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      selectedDate: '',      // 📆 캘린더에서 선택된 날짜
      graphType: 'line',     // 📈 or 🗺️
      metric: 'txCount'      // 트랜잭션 수 or 거래 대금
    };
  }
};
</script>

<style scoped>
.graph-wrapper {
  padding: 20px;
}
.top-controls,
.line-controls {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.calendar-input {
  padding: 6px 10px;
  border-radius: 6px;
  background: #1e1e1e;
  border: 1px solid #444;
  color: white;
}
</style>
