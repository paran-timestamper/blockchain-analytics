오른쪽 상단에서 라인그래프

<template>
  <canvas ref="lineChart" width="600" height="300"></canvas>
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
    address: { type: String, required: true },
    token: { type: String, required: true }, // ✅ 추가
    date: { type: String, required: true },
    metric: { type: String, required: true } // 'txCount' or 'value'
  },
  data() {
    return {
      chartInstance: null
    };
  },
  watch: {
    date: 'fetchData',
    metric: 'fetchData'
  },
  methods: {
    async fetchData() {
      try {
        const endpoint = this.metric === 'txCount'
            ? "http://localhost:8080/api/brokenLineGraph/weeklyTransactionCount"
            : "http://localhost:8080/api/brokenLineGraph/weeklyTransactionVolume";

        const payload = {
          fromAddress: "axelar19xj4ncc6h6y5ahpfqtspdx75y3dkrxj3zpah9k", // ✅ 수정 //this.address
          token: "USDC.axl",         // ✅ 추가 this.token
          date: "2022-10-25"           // ✅ 'T00:00:00' 제거 this.date
        };

        console.log("payload = ", payload);

        const res = await fetch(endpoint, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        console.log("res = ", res);

        let data = await res.json();

        if (!Array.isArray(data) || data.length === 0) {
          console.warn("⚠️ 응답 데이터 없음, 기본 0값 사용");
          const baseDate = new Date(this.date);
          data = Array.from({ length: 7 }, (_, i) => {
            const d = new Date(baseDate);
            d.setDate(d.getDate() + i);
            return {
              day: `${d.getMonth() + 1}/${d.getDate()}`,
              txCount: 0,
              totalValue: 0
            };
          });
        } else {
          const baseDate = new Date(this.date);
          data = data.map((item, i) => {
            const offsetDate = new Date(baseDate);
            offsetDate.setDate(baseDate.getDate() + i);
            return {
              ...item,
              day: `${offsetDate.getMonth() + 1}/${offsetDate.getDate()}`
            };
          });
        }

        const labels = data.map(d => d.day);
        const values = this.metric === 'txCount'
            ? data.map(d => parseInt(d.txCount || 0))
            : data.map(d => parseFloat(d.totalValue || 0));

        this.renderLineChart(labels, values);
      } catch (err) {
        console.error("📉 라인 그래프 데이터 오류:", err);
      }
    },
    renderLineChart(labels, values) {
      if (this.chartInstance) {
        this.chartInstance.destroy();
      }

      const ctx = this.$refs.lineChart?.getContext("2d");
      if (!ctx) {
        console.error("❌ 캔버스 컨텍스트를 가져올 수 없습니다.");
        return;
      }

      this.chartInstance = new Chart(ctx, {
        type: "line",
        data: {
          labels,
          datasets: [{
            label: this.metric === 'txCount' ? "트랜잭션 수" : "총 거래 대금 (USDT)",
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
              text: this.metric === 'txCount'
                  ? "최근 1주일 간 트랜잭션 수 추이"
                  : "최근 1주일 간 거래 대금 추이",
              color: "#fff"
            },
            legend: {
              labels: { color: "#fff" }
            }
          },
          scales: {
            x: { ticks: { color: "#fff" } },
            y: {
              beginAtZero: true,
              ticks: {
                color: "#fff",
                callback: (val) => parseInt(val)
              }
            }
          }
        }
      });
    }
  },
  beforeUnmount() {
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  }
};
</script>

<style scoped>
canvas {
  background-color: #222;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.4);
}
</style>
