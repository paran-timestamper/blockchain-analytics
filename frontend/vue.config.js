module.exports = {
  devServer: {
    port: 8081, // 프론트 포트
    proxy: {
      '/api': {
        target: 'http://localhost:8083', // 백엔드 포트
        changeOrigin: true,
      },
    },
  },
};
