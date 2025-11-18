import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],

  // THÊM ĐOẠN NÀY – QUAN TRỌNG NHẤT
  server: {
    proxy: {
      // Tất cả request bắt đầu bằng /api sẽ được chuyển thẳng tới Spring Boot
      '/api': {
        target: 'http://localhost:8080',   // ← port backend Spring Boot của bạn (thường là 8080)
        changeOrigin: true,
        secure: false,
        // rewrite: (path) => path.replace(/^\/api/, '/api') // không cần nếu đã đúng
      },
    },
  },
})