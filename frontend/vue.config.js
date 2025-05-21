module.exports = {
  transpileDependencies: [
    "chart.js"
  ],
  devServer: {
    port: 8082, // 프론트 포트
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 백엔드 포트
        changeOrigin: true,
      },
    },
  },
};
