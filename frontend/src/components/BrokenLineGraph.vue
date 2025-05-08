<template>
  <div class="broken-line-graph">
    <div class="toggle-buttons">
      <button @click="setMode('tx')" :class="{ active: mode === 'tx' }">트랜잭션 기준</button>
      <button @click="setMode('value')" :class="{ active: mode === 'value' }">거래대금 기준</button>
    </div>
    <h3>Broken Line Graph ({{ address }})</h3>
    <canvas ref="lineChart" width="300" height="150"></canvas>
  </div>
</template>

<script>
import {
  Chart,
  LineController,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale,
  Title,
  Tooltip
} from "chart.js";

Chart.register(
    LineController,
    LineElement,
    PointElement,
    CategoryScale,
    LinearScale,
    Title,
    Tooltip
);

export default {
  name: "BrokenLineGraph",
  props: {
    address: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      chart: null,
      mode: 'tx' // 'tx' or 'value'
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    setMode(type) {
      this.mode = type;
      this.fetchData();
    },
    async fetchData() {
      const endpoint = this.mode === 'tx'
          ? `http://localhost:8080/api/linegraph/tx?address=${this.address}`
          : `http://localhost:8080/api/linegraph/value?address=${this.address}`;

      try {
        const res = await fetch(endpoint);
        const data = await res.json();

        const labels = data.map(d => d.day);
        const values = data.map(d => this.mode === 'tx' ? d.txCount : d.totalValue);

        this.renderChart(labels, values);
      } catch (err) {
        console.error("데이터 불러오기 실패:", err);
      }
    },
    renderChart(labels, values) {
      const ctx = this.$refs.lineChart.getContext("2d");

      if (this.chart) {
        this.chart.destroy();
      }

      this.chart = new Chart(ctx, {
        type: "line",
        data: {
          labels,
          datasets: [{
            label: this.mode === 'tx' ? "트랜잭션 수" : "총 거래대금",
            data: values,
            fill: false,
            borderColor: "#42a5f5",
            tension: 0.3
          }]
        },
        options: {
          responsive: false,
          plugins: {
            title: {
              display: true,
              text: "Broken Line Graph",
              color: "#fff"
            },
            legend: {
              labels: {
                color: "#fff"
              }
            }
          },
          scales: {
            x: { ticks: { color: "#fff" } },
            y: { beginAtZero: true, ticks: { color: "#fff" } }
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.broken-line-graph {
  width: 100%;
  height: 100%;
}
canvas {
  background: #222;
}
.toggle-buttons {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}
.toggle-buttons button {
  background: #444;
  color: white;
  border: none;
  padding: 6px 12px;
  cursor: pointer;
  border-radius: 4px;
}
.toggle-buttons button.active {
  background: #2196f3;
}
</style>
