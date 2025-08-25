import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 5173,
    proxy: {
      // 代理知识库服务API
      '/api/test': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false
      },
      // 代理复习服务API
      '/api/review': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        secure: false
      },
      // 代理其他API到知识库服务
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false
      },
      // 代理健康检查端点
      '/actuator': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
