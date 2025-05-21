<template>
  <div
      class="timeline-wrapper"
      ref="wrapperRef"
      @mousedown="startDrag"
      @mousemove="onDrag"
      @mouseup="endDrag"
      @mouseleave="endDrag"
  >
    <!-- 트랙 + 체크포인트 -->
    <div class="track">
      <template v-for="(d, i) in sectionDates" :key="i">
        <!-- 날짜 텍스트 -->
        <div
            class="checkpoint-label"
            :style="{ left: `${i * stepWidth}px` }"
        >
          {{ d }}
        </div>
        <!-- 체크포인트 막대 -->
        <div
            class="checkpoint"
            :style="{ left: `${i * stepWidth}px` }"
        />
      </template>

      <!-- 드래그 핸들 -->
      <div class="handle" :style="handleStyle">
        <div class="handle-line" />
        <div class="handle-line" />
        <div class="handle-line" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'

const sectionDates = [
  '2021-02', '2021-05', '2021-08',
  '2021-11', '2022-02', '2022-05', '2022-08'  // ✅ 7개만 유지
]

const emit = defineEmits(['update:section'])

const totalSteps = sectionDates.length
const dragging = ref(false)
const handleX = ref(0)
const containerWidth = ref(0)
const wrapperRef = ref(null)

const handleWidth = 40

// ✅ 트랙 내부 여백 고려 (12px + 12px)
const TRACK_MARGIN = 24
const stepWidth = computed(() => (containerWidth.value - TRACK_MARGIN) / (totalSteps - 1))

onMounted(() => {
  nextTick(() => {
    containerWidth.value = wrapperRef.value.offsetWidth
    handleX.value = 0
    emit('update:section', sectionDates[0])
  })
})

const getClosestSection = (x) => {
  const index = Math.round(x / stepWidth.value)
  return {
    index,
    date: sectionDates[Math.max(0, Math.min(index, totalSteps - 1))],
    xPos: index * stepWidth.value
  }
}

const handleStyle = computed(() => ({
  transform: `translateX(${handleX.value - handleWidth / 2}px)`
}))

const startDrag = (e) => {
  dragging.value = true
  containerWidth.value = wrapperRef.value.offsetWidth
  moveTo(e.clientX)
}

const endDrag = () => {
  if (!dragging.value) return
  dragging.value = false
  const { date, xPos } = getClosestSection(handleX.value)
  handleX.value = xPos
  emit('update:section', date)
}

const onDrag = (e) => {
  if (!dragging.value) return
  moveTo(e.clientX)
}

const moveTo = (clientX) => {
  const rect = wrapperRef.value.getBoundingClientRect()
  const x = clientX - rect.left
  const min = handleWidth / 2
  const max = containerWidth.value - handleWidth / 2
  handleX.value = Math.max(min, Math.min(x, max))
}

const goToIndex = (index) => {
  const xPos = index * stepWidth.value
  handleX.value = xPos
  emit('update:section', sectionDates[index])
}
</script>

<style scoped>
.timeline-wrapper {
  width: 100%;
  height: 100px;
  position: relative;
  user-select: none;
  cursor: pointer;
}

.track {
  position: absolute;
  top: 50%;
  left: 12px;
  right: 12px;
  height: 6px;
  background-color: #e5e7eb;
  border-radius: 3px;
  transform: translateY(-50%);
}

.checkpoint {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 4px;
  height: 14px;
  background-color: #9ca3af;
  border-radius: 1px;
  z-index: 1;
}

.checkpoint-label {
  position: absolute;
  bottom: 100%;
  transform: translateX(-50%);
  font-size: 10px;
  color: #4b5563;
  margin-bottom: 6px;
  white-space: nowrap;
}

.handle {
  position: absolute;
  top: calc(50% - 8px);
  transform: translateY(-50%);
  width: 40px;
  height: 18px;
  background-color: #6366f1;
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
  z-index: 2;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
}

.handle-line {
  width: 3px;
  height: 10px;
  background-color: white;
  border-radius: 1px;
}
</style>
