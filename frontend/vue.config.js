const { defineConfig } = require('@vue/cli-service')
module.exports = {
  outputDir: "../src/main/resources/static",
  devServer: {
    port: 8082,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true //cross origin 허용
      }
    }
  }
};
