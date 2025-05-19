<template>
  <div class="treemap-container">
    <p>여긴 트리맵입니다</p>
    <canvas id="treemapCanvas"></canvas>
  </div>
</template>

<script>
export default {
  name: "Treemap",
  props: {
    address: {
      type: String,
      required: true
    }
  },
  mounted() {
    // 여기서 실제 treemap을 그리는 로직을 추가할 수 있습니다
    console.log("트리맵 address:", this.address);
    const canvas = document.getElementById("treemapCanvas");
    const ctx = canvas.getContext("2d");
    ctx.fillStyle = "#FF9800";
    ctx.fillRect(10, 10, 100, 100);
  }
};
</script>

<style scoped>
.treemap-container {
  width: 100%;
  height: 100%;
}
canvas {
  width: 100%;
  height: 100%;
  background: #222;
}
</style>
