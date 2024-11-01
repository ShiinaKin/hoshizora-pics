/** @type {import("tailwindcss").Config} */
import primeUI from "tailwindcss-primeui";

export default {
  darkMode: ["class"],
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      borderRadius: {
        lg: "var(--radius)",
        md: "calc(var(--radius) - 2px)",
        sm: "calc(var(--radius) - 4px)"
      },
      colors: {}
    }
  },
  plugins: [primeUI]
};
