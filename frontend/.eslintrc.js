module.exports = {
    root: true,
    env: {
        node: true
    },
    extends: [
        'eslint:recommended',
        'plugin:vue/vue3-recommended'
    ],
    parserOptions: {
        ecmaVersion: 2020,
        sourceType: 'module'
    },
    globals: {
        defineProps: 'readonly',
        defineEmits: 'readonly'
    },
    rules: {
        // 네 프로젝트에 맞게 규칙을 여기에 추가할 수 있음
        'no-unused-vars': 'warn',
        'vue/multi-word-component-names': 'off'
    }
}
