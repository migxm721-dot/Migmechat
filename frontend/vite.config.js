import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8000',
      '/ws': { target: 'http://localhost:8000', ws: true },
    },
  },
  build: {
    outDir: process.env.VITE_OUT_DIR || '../backend/src/main/resources/static',
    emptyOutDir: true,
  },
  base: process.env.VITE_BASE || '/',
});
