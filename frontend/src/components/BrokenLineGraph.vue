<template>
  <div class="broken-line-graph">
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
  mounted() {
    const ctx = this.$refs.lineChart.getContext("2d");
    const labels = Array.from({ length: 12 }, (_, i) => `${i + 1}월`);
    const values = Array.from({ length: 12 }, () => Math.floor(Math.random() * 100));

    new Chart(ctx, {
      type: "line",
      data: {
        labels,
        datasets: [{
          label: "월별 변화",
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
</style>
