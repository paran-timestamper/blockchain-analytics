//테스트용

<template>
  <div class="graph-container">
    <!-- D3 그래프 자리 -->
    <div ref="chart" class="chart-area"></div>

    <!-- 테스트용 팝업: 항상 표시, 이 밑줄 주석처리하면 안 뜸 -->
    <!-- <AddressPopup address="axelar19xj4ncc6h6y5ahpfqtspdx75y3dkrxj3zpah9k" @close="() => {}" /> -->
  </div>
</template>

<script>
import AddressPopup from "@/components/v3_popup/AddressPopup.vue"; // 경로 확인
export default {
  name: "AddressNodeGraph",
  components: {
    AddressPopup
  },
  mounted() {
    // 원래는 D3 그래프 초기화 위치
  }
};
</script>

<style scoped>
.graph-container {
  position: relative;
  width: 100%;
  height: 100vh;
}
.chart-area {
  width: 100%;
  height: 100%;
  background: black;
}
</style>
