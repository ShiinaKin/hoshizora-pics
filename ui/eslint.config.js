import globals from "globals";
import pluginJs from "@eslint/js";
import tsEslint from "typescript-eslint";
import pluginVue from "eslint-plugin-vue";
import { includeIgnoreFile } from "@eslint/compat";
import { fileURLToPath } from "node:url";
import path from "node:path";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const gitignorePath = path.resolve(__dirname, ".gitignore");

export default [
  { files: ["**/*.{js,mjs,cjs,ts,vue}"] },
  { languageOptions: { globals: globals.browser } },
  pluginJs.configs.recommended,
  ...tsEslint.configs.recommended,
  ...pluginVue.configs["flat/essential"],
  { files: ["**/*.vue"], languageOptions: { parserOptions: { parser: tsEslint.parser } } },
  { ignores: [`packages/api-client/**`] },
  includeIgnoreFile(gitignorePath),
  {
    plugins: { tsEslint },
    rules: {
      eqeqeq: "warn",
      semi: "warn",
      "no-useless-return": "warn",
      "no-var": "warn",
      "@typescript-eslint/no-unused-vars": "warn",
      "no-duplicate-imports": "warn",
      "camelcase": "warn",
      "prefer-const": ["warn", { "ignoreReadBeforeAssign": true }]
    }
  }
];
