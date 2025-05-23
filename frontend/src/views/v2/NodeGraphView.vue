<template>
  <div class="graph-container">
    <div class="graph-placeholder">그래프 영역</div>

    <!-- ✅ 테스트용 팝업 항상 표시 -->

    <!-- ===== 사용하려면 아래 중에서 원하는 팝업 주석 해제 ===== -->

    <!-- 🔁 TokenPopup 테스트용 -->


    <TokenPopup
        :a-token="'ATOM'"
        :b-token="'INJ'"
        @close="() => {}"
    />

    <!-- 🔁 AddressPopup 테스트용 -->

<!--
    <AddressPopup
      :address="'axelar19xj4ncc6h6y5ahpfqtspdx75y3dkrxj3zpah9k'"
      @close="() => {}"
    />
-->


  </div>
</template>

<script setup>
import TokenPopup from "@/components/v2/TokenPopup.vue"
//import AddressPopup from "@/components/v2/AddressPopup.vue"
</script>

<style scoped>
.graph-container {
  position: relative;
  width: 100%;
  height: 100%;
}
.graph-placeholder {
  height: 100%;
  background: #f0f0f0;
}
</style>
