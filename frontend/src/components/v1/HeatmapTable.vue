<template>
  <div class="heatmap-table">
    <h3>Heatmap Table</h3>
    <table>
      <thead>
      <tr>
        <th>시간</th>
        <th>빈도</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(row, i) in dummyData" :key="i">
        <td>{{ row.hour }}시</td>
        <td>{{ row.value }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: "HeatmapTable",
  props: {
    address: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      dummyData: Array.from({ length: 24 }, (_, i) => ({
        hour: i,
        value: Math.floor(Math.random() * 100)
      }))
    };
  }
};
</script>

<style scoped>
.heatmap-table {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}
table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  color: white;
}
th, td {
  border: 1px solid #444;
  padding: 6px 10px;
}
th {
  background-color: #333;
}
</style>
