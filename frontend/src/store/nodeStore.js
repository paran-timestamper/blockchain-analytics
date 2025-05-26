import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNodeStore = defineStore('node', () => {
    const node = ref('')
    console.log("asdasd")
    console.log(node.value)
    return { node }
})
