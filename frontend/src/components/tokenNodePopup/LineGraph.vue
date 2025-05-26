<template>
  <div class="line-graph">
    <div class="chart-controls">
      <div class="buttons">
        <button @click="switchChart('daily')" :class="{ active: currentChart === 'daily' }">일간 거래량</button>
        <button @click="switchChart('monthly')" :class="{ active: currentChart === 'monthly' }">월간 거래량</button>
        <button @click="switchChart('total')" :class="{ active: currentChart === 'total' }">전체 거래량</button>
      </div>
      <div class="calendar">
        <input v-if="currentChart === 'daily'" type="date" v-model="selectedDate" />
        <input v-else-if="currentChart === 'monthly'" type="month" v-model="selectedMonth" />
      </div>
    </div>
    <canvas v-if="showChart" :key="chartKey" ref="chartCanvas" width="600" height="300"></canvas>
  </div>
</template>

<script>
import {
  Chart, BarController, BarElement, CategoryScale,
  LinearScale, Title, Tooltip, Legend
} from 'chart.js';

Chart.register(BarController, BarElement, CategoryScale, LinearScale, Title, Tooltip, Legend);

export default {
  name: 'LineGraphCompare',
  props: {
    aToken: { type: String, required: true },
    bToken: { type: String, required: true }
  },
  data() {
    return {
      currentChart: 'daily',
      selectedDate: new Date().toISOString().slice(0, 10),
      selectedMonth: new Date().toISOString().slice(0, 7),
      chartKey: 0,
      showChart: true,
      chartInstance: null
    };
  },
  mounted() {
    this.fetchChartData();
  },
  watch: {
    selectedDate() {
      if (this.currentChart === 'daily') this.fetchChartData();
    },
    selectedMonth() {
      if (this.currentChart === 'monthly') this.fetchChartData();
    }
  },
  methods: {
    switchChart(type) {
      this.currentChart = type;
      this.showChart = false;
      this.$nextTick(() => {
        this.chartKey++;
        this.showChart = true;
        this.$nextTick(() => {
          this.fetchChartData();
        });
      });
    },
    async fetchChartData() {
      let url = '';
      const body = {
        tokens: [this.aToken, this.bToken]
      };

      if (this.currentChart === 'daily') {
        url = 'http://localhost:8080/api/tokenNode/dailyVolume';
        body.date = this.selectedDate;
      } else if (this.currentChart === 'monthly') {
        url = 'http://localhost:8080/api/tokenNode/monthlyVolume';
        body.date = this.selectedMonth;
      } else {
        url = 'http://localhost:8080/api/tokenNode/totalVolume';
      }

      try {
        const res = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body)
        });

        if (!res.ok) {
          const text = await res.text();
          throw new Error(`서버 응답 오류: ${res.status} - ${text}`);
        }

        const result = await res.json();
        const blocks = result.time_blocks || [];

        const labels = blocks.map(b => b.time_range || b.time || 'Unknown');
        const aData = blocks.map(b => b.values?.[this.aToken] || 0);
        const bData = blocks.map(b => b.values?.[this.bToken] || 0);

        this.renderChart(labels, aData, bData);
      } catch (e) {
        console.error('🔥 데이터 가져오기 실패', e);
      }
    },
    renderChart(labels, aValues, bValues) {
      const ctx = this.$refs.chartCanvas?.getContext('2d');
      if (!ctx) return;

      if (this.chartInstance) {
        this.chartInstance.destroy();
        this.chartInstance = null;
      }

      this.chartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
          labels,
          datasets: [
            {
              label: this.aToken,
              data: aValues,
              backgroundColor: '#4CAF50'
            },
            {
              label: this.bToken,
              data: bValues,
              backgroundColor: '#2196F3'
            }
          ]
        },
        options: {
          responsive: true,
          plugins: {
            legend: { labels: { color: 'white' } },
            title: {
              display: true,
              text: this.getChartTitle(),
              color: 'white'
            }
          },
          scales: {
            x: { ticks: { color: 'white' } },
            y: { ticks: { color: 'white' }, beginAtZero: true }
          }
        }
      });
    },
    getChartTitle() {
      if (this.currentChart === 'daily') return '일간 거래량 비교';
      if (this.currentChart === 'monthly') return '월간 거래량 비교';
      return '전체 거래량 비교';
    }
  }
};
</script>

<style scoped>
.line-graph {
  color: white;
}
.chart-controls {
  display: flex;
  flex-direction: column;
  gap: 10px;
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
input[type="date"],
input[type="month"] {
  background: #222;
  color: white;
  border: 1px solid #555;
  border-radius: 4px;
  padding: 5px;
}
canvas {
  background: #1e1e1e;
  border-radius: 8px;
}
</style>
