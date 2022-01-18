module.exports = {
    coverageThreshold: {
        global: {
            branches: 60,
            functions: 49,
            lines: 60,
            statements: 60
        }
    },
    modulePathIgnorePatterns: [
        "<rootDir>/dist/"
    ],
    coverageDirectory: "build_internal/test_results",
    reporters: ["jest-standard-reporter", "jest-junit"],
    collectCoverage: true,
    collectCoverageFrom:  [
        "src/**/*.{ts,tsx,js,jsx}",
        "lib/**/*.{ts,tsx,js,jsx}"
    ],
    transform: {
        "^.+\\.(ts|tsx|js|jsx)$": "ts-jest",
    },
}
