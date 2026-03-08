/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './.vitepress/**/*.{vue,ts}',
    './src/**/*.{vue,ts}',
    './pages/**/*.vue',
    './**/*.md'
  ],
  theme: {
    extend: {}
  },
  plugins: []
}
