// @ts-check

import js from "@eslint/js";
import vuePlugin from "eslint-plugin-vue";
import globals from "globals";
import * as tseslint from "@typescript-eslint/eslint-plugin";
import tseslintParser from "@typescript-eslint/parser";
import vueParser from "vue-eslint-parser";
import prettierConfig from "eslint-config-prettier";

export default [
    { ignores: ["dist"] },
    js.configs.recommended,
    prettierConfig,
    {
        files: ["**/*.{js,mjs,cjs}"],
        languageOptions: {
            ecmaVersion: 2020,
            globals: {
                ...globals.browser,
                ...globals.node,
            },
        },
    },
    {
        files: ["**/*.{ts,mts,cts}"],
        languageOptions: {
            ecmaVersion: 2020,
            globals: {
                ...globals.browser,
                ...globals.node,
            },
            parser: tseslintParser,
            parserOptions: {
                projectService: true,
            },
        },
        plugins: {
            "@typescript-eslint": tseslint,
        },
        rules: {
            ...tseslint.configs.recommended.rules,
            "@typescript-eslint/no-unused-vars": "warn",
            "@typescript-eslint/no-explicit-any": "warn",
            "@typescript-eslint/ban-ts-comment": "warn",
        },
    },
    {
        files: ["**/*.vue"],
        languageOptions: {
            ecmaVersion: 2020,
            globals: {
                ...globals.browser,
                ...globals.node,
            },
            parser: vueParser,
            parserOptions: {
                parser: tseslintParser,
                extraFileExtensions: [".vue"],
                projectService: true,
            },
        },
        plugins: {
            vue: vuePlugin,
            "@typescript-eslint": tseslint,
        },
        rules: {
            // Vue specific rules
            "vue/multi-word-component-names": "off",
            "vue/no-v-html": "warn",
            "vue/require-default-prop": "off",
            "vue/max-attributes-per-line": "off",
            "vue/html-indent": ["error", 2],
            "vue/html-self-closing": "error",
            "vue/component-name-in-template-casing": ["error", "PascalCase"],
            "vue/no-unused-components": "warn",

            // TypeScript
            "@typescript-eslint/no-unused-vars": "warn",
            "@typescript-eslint/no-explicit-any": "warn",
            "@typescript-eslint/ban-ts-comment": "warn",
        },
    },
];
