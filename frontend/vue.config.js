const path = require('path')

module.exports = {
  transpileDependencies: ['chart.js'],
  devServer: {
    port: 8082,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  configureWebpack: {
    module: {
      rules: [
        {
          test: /\.m?js$/,                  // ✅ .mjs 및 .js 둘 다 처리
          include: /node_modules/,
          type: 'javascript/auto',
          use: {
            loader: 'babel-loader',
            options: {
              presets: ['@babel/preset-env']
            }
          }
        }
      ]
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
      },
      extensions: ['.js', '.vue', '.json', '.mjs'],
    },
  },
}
