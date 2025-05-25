import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useTokenStore = defineStore('tokenStore', () => {
    const tokenList = ref([]);
    const selectedTokens = ref([]);

    return { tokenList, selectedTokens };
});
