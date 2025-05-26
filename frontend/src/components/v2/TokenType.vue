<template>
  <div class="box token-type-box">
    <h4 class="section-title">Token Type</h4>
    <div class="scrollable-list">
      <!-- All Token 토글 -->
      <label class="checkbox">
        <input
            type="checkbox"
            :checked="allSelected"
            @change="toggleAll"
        />
        All Token
      </label>
      <!-- 개별 토큰 -->
      <label
          class="checkbox"
          v-for="tk in tokenList"
          :key="tk"
      >
        <input
            type="checkbox"
            :value="tk"
            v-model="visibleTokens"
        />
        {{ tk }}
      </label>
    </div>
  </div>
</template>

<script setup>
import { inject, ref, computed, onMounted } from 'vue'

// App.vue에서 provide된 ref of Array<string>
const visibleTokens = inject('visibleTokens')
if (!visibleTokens) {
  throw new Error('visibleTokens가 제공되지 않았습니다!')
}

// 전체 토큰 목록
const tokenList = ref([])

// All Token 체크 상태: 선택된 수 === 전체 토큰 수
const allSelected = computed(() =>
    visibleTokens.value.length === tokenList.value.length
)

// All 토글 핸들러
function toggleAll(e) {
  if (e.target.checked) {
    visibleTokens.value = [...tokenList.value]
  } else {
    visibleTokens.value = []
  }
}

// 마운트 시 API 호출
onMounted(async () => {
  try {
    // 원래 POST를 쓰셨다면 method: 'POST' 로 바꾸세요
    const res  = await fetch('/api/common/tokens', { method: 'POST' })
    const data = await res.json()

    // data 형태에 따라 토큰 이름만 뽑아내기
    let list = []
    if (Array.isArray(data)) {
      // ['BTC','ETH', ...] 또는 [{token:'BTC'}, …]
      if (data.length && typeof data[0] === 'string') {
        list = data
      } else if (data.length && typeof data[0] === 'object') {
        if ('token' in data[0]) list = data.map(x => x.token)
        else if ('name' in data[0])  list = data.map(x => x.name)
      }
    } else if (data.tokens) {
      list = data.tokens
    }

    tokenList.value = list

    // 최초 렌더링 시 전체 선택
    if (visibleTokens.value.length === 0) {
      visibleTokens.value = [...tokenList.value]
    }
  } catch (err) {
    console.error('토큰 목록 로드 실패:', err)
  }
})
</script>

<style scoped>
.token-type-box {
  display: flex;
  flex-direction: column;
}
.scrollable-list {
  max-height: 200px;
  overflow-y: auto;
  padding-right: 4px;
  box-sizing: border-box;
}
.checkbox {
  font-size: 12px;
  margin: 4px 0;
  display: block;
}
</style>
