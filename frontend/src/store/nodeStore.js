import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNodeStore = defineStore('node', () => {
    const node = ref('')
    return { node }
})
