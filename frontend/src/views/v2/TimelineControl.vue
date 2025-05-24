<template>
  <div class="timeline-wrapper" ref="wrapperRef"
       @mousedown="startDrag"
       @mousemove="onDrag"
       @mouseup="endDrag"
       @mouseleave="endDrag"
  >
    <div class="track">
      <template v-for="(d, i) in sectionDates" :key="i">
        <div class="checkpoint-label" :style="{ left: `${i * stepWidth}px` }">
          {{ d }}
        </div>
        <div class="checkpoint" :style="{ left: `${i * stepWidth}px` }" />
      </template>
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

const props = defineProps({
  sectionDates: {
    type: Array,
    required: true,
    default: () => []
  }
})
const emit = defineEmits(['update:section'])

const wrapperRef = ref(null)
const dragging = ref(false)
const containerWidth = ref(0)
const handleX = ref(0)

const handleWidth = 40
const TRACK_MARGIN = 24

const totalSteps = computed(() => props.sectionDates.length)
const stepWidth = computed(() =>
    (containerWidth.value - TRACK_MARGIN) / (totalSteps.value - 1)
)

onMounted(() => {
  nextTick(() => {
    containerWidth.value = wrapperRef.value.offsetWidth
    handleX.value = 0
    emit('update:section', props.sectionDates[0])
  })
})

const handleStyle = computed(() => ({
  transform: `translateX(${handleX.value - handleWidth / 2}px)`
}))

const getClosestSection = (x) => {
  const index = Math.round(x / stepWidth.value)
  return {
    index,
    date: props.sectionDates[Math.max(0, Math.min(index, totalSteps.value - 1))],
    xPos: index * stepWidth.value
  }
}

const moveTo = (clientX) => {
  const rect = wrapperRef.value.getBoundingClientRect()
  const x = clientX - rect.left
  const min = handleWidth / 2
  const max = containerWidth.value - handleWidth / 2
  handleX.value = Math.max(min, Math.min(x, max))
}

const startDrag = (e) => {
  dragging.value = true
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
