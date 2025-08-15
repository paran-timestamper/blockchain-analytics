<template>
  <div class="heatmap-wrapper">
    <HeatmapView :address="address" @cell-click="handleClick" />
    <HeatmapTable ref="heatmapTable" />
  </div>
</template>

<script>
import HeatmapView from '@/components/transactionNodePopup/HeatmapView.vue';
import HeatmapTable from '@/components/transactionNodePopup/HeatmapTable.vue';

export default {
  name: 'Heatmap',
  props: {
    address: {
      type: String,
      required: true
    }
  },
  components: {
    HeatmapView,
    HeatmapTable
  },
  data() {
    return {
      // ref 역할을 이렇게 data에서 함
      heatmapTable: null
    };
  },
  mounted() {
    // ref 연결
    this.heatmapTable = this.$refs.heatmapTable;
  },
  methods: {
    async handleClick({ date, mode }) {
      console.log("🔥 handleClick fired from Heatmap.vue", date, mode);

      const endpoint = mode === 'monthly'
          ? 'http://localhost:8080/api/heatmap/monthly/detail'
          : 'http://localhost:8080/api/heatmap/daily/detail';

      const res = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fromAddress: this.address, date })
      });

      const result = await res.json();
      this.heatmapTable?.setData(result);
    }
  }
};
</script>

<style scoped>
.heatmap-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>
