/** @type {import("tailwindcss").Config} */
import primeUI from "tailwindcss-primeui";

export default {
  darkMode: ["class"],
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      borderRadius: {},
      colors: {}
    }
  },
  plugins: [primeUI]
};
