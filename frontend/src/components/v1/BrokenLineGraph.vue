<template>
  <div class="broken-line-graph">
    <div class="address-line">
      <span class="address-text" :title="address">{{ truncateText(address) }}</span>
      <button @click="copyToClipboard(address)">📋</button>
    </div>
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
  },
  methods: {
    truncateText(text) {
      return text.length > 10 ? text.slice(0, 10) + "..." : text;
    },
    copyToClipboard(text) {
      navigator.clipboard.writeText(text).then(() => {
        alert("Copied to clipboard: " + text);
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

.address-line {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.address-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #ccc;
}

button {
  font-size: 12px;
  background: none;
  border: none;
  cursor: pointer;
  color: #ccc;
  padding: 0;
  margin: 0 0 0 5px;
  vertical-align: middle;
}

button:hover {
  color: #fff;
}

canvas {
  background: #222;
}
</style>
