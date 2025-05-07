<template>
  <div class="month-day-graph">
    <h3>{{ address }} - 일간/월간 거래량</h3>
    <canvas ref="chartCanvas" width="300" height="150"></canvas>
  </div>
</template>

<script>
import {
  Chart, BarController, BarElement, CategoryScale,
  LinearScale, Title, Tooltip
} from "chart.js";

Chart.register(BarController, BarElement, CategoryScale, LinearScale, Title, Tooltip);

export default {
  name: "MonthDayGraph",
  props: {
    address: {
      type: String,
      required: true
    }
  },
  mounted() {
    const labels = Array.from({ length: 12 }, (_, i) => `${i + 1}월`);
    const values = Array.from({ length: 12 }, () => Math.floor(Math.random() * 100));

    const ctx = this.$refs.chartCanvas.getContext("2d");
    new Chart(ctx, {
      type: "bar",
      data: {
        labels,
        datasets: [{
          label: "월간 거래량",
          data: values,
          backgroundColor: "#64b5f6"
        }]
      },
      options: {
        responsive: false,
        plugins: {
          title: {
            display: true,
            text: "월간 거래량 (더미)",
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
          y: { ticks: { color: "#fff" }, beginAtZero: true }
        }
      }
    });
  }
};
</script>

<style scoped>
.month-day-graph {
  width: 100%;
  height: 100%;
}
canvas {
  background: #222;
}
</style>
