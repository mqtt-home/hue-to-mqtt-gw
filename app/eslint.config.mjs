import { parser, plugin } from "typescript-eslint"

export default [
    {
        files: ["**/*.ts"],
        languageOptions: {
            parser,
            ecmaVersion: "latest",
            sourceType: "module"
        },
        plugins: {
            "@typescript-eslint": plugin
        },

        rules: {
            "@typescript-eslint/no-require-imports": "error",
            "@typescript-eslint/no-unused-vars": "error",
            "brace-style": [
                "error",
                "stroustrup"
            ],
            curly: [
                "error",
                "all"
            ],
            eqeqeq: "warn",
            indent: [
                "warn",
                4
            ],
            "no-throw-literal": "warn",
            "no-unexpected-multiline": "error",
            "no-unused-vars": "off",
            quotes: [
                "error",
                "double"
            ],
            semi: [
                "error",
                "never"
            ]
        }
    }
]
