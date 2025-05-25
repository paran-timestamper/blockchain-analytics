<template>
  <div class="month-day-graph">
    <div class="chart-controls">
      <div class="buttons">
        <button @click="switchChart('daily')" :class="{ active: currentChart === 'daily' }">일간 거래량</button>
        <button @click="switchChart('monthly')" :class="{ active: currentChart === 'monthly' }">월간 거래량</button>
      </div>
      <div class="calendar">
        <input v-if="currentChart === 'daily'" type="date" v-model="selectedDate" />
        <input v-else type="month" v-model="selectedMonth" />
      </div>
    </div>
    <canvas v-if="showChart" :key="chartKey" ref="chartCanvas" width="600" height="300"></canvas>
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
  data() {
    return {
      currentChart: 'monthly',
      chartInstance: null,
      selectedDate: '2022-10-31',
      selectedMonth: '2022-10',
      chartKey: 0,
      showChart: true
    };
  },
  mounted() {
    this.renderMonthlyChart();
  },
  watch: {
    selectedDate() {
      if (this.currentChart === 'daily') this.renderDailyChart();
    },
    selectedMonth() {
      if (this.currentChart === 'monthly') this.renderMonthlyChart();
    }
  },
  methods: {
    switchChart(type) {
      this.currentChart = type;
      this.showChart = false;
      this.$nextTick(() => {
        this.chartKey += 1;
        this.showChart = true;
        this.$nextTick(() => {
          if (type === 'daily') {
            this.renderDailyChart();
          } else {
            this.renderMonthlyChart();
          }
        });
      });
    },
    async renderMonthlyChart() {
      const res = await fetch('http://localhost:8080/api/bargraph/monthlyVolume', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fromAddress: "osmo17vz8hvg96lddwawdfygvu89ua4v7qgd6p3m9ry",
          token: "ATOM",
          date: this.selectedMonth
        })
      });
      const data = await res.json();

      const labels = data.list.map(d => d.time);
      const values = data.list.map(d => d.transactionAmount);
      this.renderChart(labels, values, '월간 거래량');
    },
    async renderDailyChart() {
      const res = await fetch('http://localhost:8080/api/bargraph/dailyVolume', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fromAddress: "osmo17vz8hvg96lddwawdfygvu89ua4v7qgd6p3m9ry",
          token: "ATOM",
          date: this.selectedDate
        })
      });
      const data = await res.json();

      const timeBlocks = [
        "00:00 ~ 03:59",
        "04:00 ~ 07:59",
        "08:00 ~ 11:59",
        "12:00 ~ 15:59",
        "16:00 ~ 19:59",
        "20:00 ~ 23:59"
      ];

      const timeToValue = Object.fromEntries(timeBlocks.map(time => [time, 0]));

      // 서버 응답값 덮어쓰기
      data.list?.forEach(d => {
        if (Object.prototype.hasOwnProperty.call(timeToValue, d.time)) {
          timeToValue[d.time] = d.transactionAmount;
        }
      });

      const labels = timeBlocks;
      const values = timeBlocks.map(time => timeToValue[time]);

      this.renderChart(labels, values, '일간 거래량');
    },
    renderChart(labels, values, labelText) {
      const ctx = this.$refs.chartCanvas?.getContext("2d");
      if (!ctx) {
        console.error("Chart canvas context not available");
        return;
      }
      if (this.chartInstance) {
        this.chartInstance.destroy();
        this.chartInstance = null;
      }
      this.chartInstance = new Chart(ctx, {
        type: "bar",
        data: {
          labels,
          datasets: [{
            label: labelText,
            data: values,
            backgroundColor: "#4CAF50"
          }]
        },
        options: {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: labelText,
              color: "#fff"
            },
            tooltip: {
              enabled: true
            },
            legend: {
              labels: { color: "#fff" }
            }
          },
          scales: {
            x: {
              ticks: { color: "#fff" }
            },
            y: {
              ticks: {
                color: "#fff",
                beginAtZero: true,
                stepSize: 1,
                precision: 0,
                callback: function(value) {
                  return Number.isInteger(value) ? value : '';
                }
              }
            }
          }

        }
      });
    }
  }
};
</script>

<style scoped>
.month-day-graph {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}
canvas {
  background: #222;
  width: 100%;
  height: auto;
}
.chart-controls {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 10px;
}
.buttons {
  margin-bottom: 8px;
}
button {
  margin-right: 10px;
  padding: 6px 12px;
  background: #333;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button.active {
  background: #4CAF50;
}
input[type="date"], input[type="month"] {
  padding: 5px;
  background: #222;
  color: white;
  border: 1px solid #555;
  border-radius: 4px;
}

</style>