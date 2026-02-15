// ============================================================
// Bhoklagyo — k6 Configuration & Shared Helpers
// ============================================================
//
// Usage:
//   k6 run load-tests/smoke.js
//   k6 run load-tests/load.js
//   k6 run load-tests/stress.js
//   k6 run load-tests/spike.js
//
// Environment variables:
//   BASE_URL       - Target URL (default: http://localhost:8080)
//   AUTH_EMAIL     - Test user email for login
//   AUTH_PASSWORD  - Test user password
//   ADMIN_EMAIL    - Admin email for admin endpoints
//   ADMIN_PASSWORD - Admin password
// ============================================================

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Standard SLOs (Service Level Objectives)
export const SLO = {
    // 95th percentile response time < 500ms
    p95ResponseTime: 500,
    // 99th percentile response time < 1500ms
    p99ResponseTime: 1500,
    // Error rate < 1%
    errorRate: 0.01,
    // Min request rate (rps) under load
    minRps: 50,
};

// Shared thresholds used across all test types
export const BASE_THRESHOLDS = {
    http_req_duration: [
        { threshold: `p(95)<${SLO.p95ResponseTime}`, abortOnFail: false },
        { threshold: `p(99)<${SLO.p99ResponseTime}`, abortOnFail: false },
    ],
    http_req_failed: [
        { threshold: `rate<${SLO.errorRate}`, abortOnFail: false },
    ],
    http_reqs: [
        { threshold: `rate>${SLO.minRps}`, abortOnFail: false },
    ],
};

// Custom thresholds per scenario
export function withScenarioThresholds(scenarioName, extraThresholds = {}) {
    return {
        ...BASE_THRESHOLDS,
        [`http_req_duration{scenario:${scenarioName}}`]: [
            `p(95)<${SLO.p95ResponseTime}`,
        ],
        ...extraThresholds,
    };
}
