<template>
  <div class="graph-wrapper">
    <!-- 📅 날짜 선택 -->
    <div class="calendar-controls">
      <input type="date" v-model="selectedDate" class="calendar-input" />
    </div>

    <!-- 🧮 metric 선택 -->
    <div class="metric-controls">
      <button @click="metric = 'txCount'" :class="{ active: metric === 'txCount' }">TransactionCount</button>
      <button @click="metric = 'value'" :class="{ active: metric === 'value' }">TotalPrice</button>
    </div>

    <!-- 📈 Line Graph -->
    <LineGraph
        v-if="selectedDate"
        :address="address"
        :date="selectedDate"
        :metric="metric"
    />

    <!-- 🏷️ Treemap 타이틀 -->
    <div class="treemap-title">『Treemap』</div>

    <!-- 🗺️ Treemap -->
    <Treemap
        v-if="selectedDate"
        :address="address"
        :date="selectedDate"
    />
  </div>
</template>


<script>
import LineGraph from "@/components/v3_popup/BrokenLineGraph.vue";
import Treemap from "@/components/v3_popup/Treemap.vue";

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
      selectedDate: '',
      metric: 'txCount'
    };
  }
};
</script>

<style scoped>
.graph-wrapper {
  padding: 12px;
}

.calendar-controls,
.metric-controls {
  margin-bottom: 12px;
  display: flex;
  gap: 10px;
}

.calendar-input {
  padding: 6px 10px;
  border-radius: 6px;
  background: #1e1e1e;
  border: 1px solid #444;
  color: white;
  width: fit-content;
}

button {
  padding: 6px 12px;
  background: #333;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

button.active {
  background: #4CAF50;
  font-weight: bold;
}
</style>
