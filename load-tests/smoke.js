// ============================================================
// Smoke Test — Verify all critical endpoints are reachable
// ============================================================
// Purpose: Quick sanity check. 1-2 VUs for 30 seconds.
// Run:     k6 run load-tests/smoke.js
// Env:     BASE_URL, AUTH_EMAIL, AUTH_PASSWORD
// ============================================================
import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

import { BASE_URL, BASE_THRESHOLDS } from './config.js';
import { loginUser, authHeaders, urlWithParams } from './helpers.js';

// Custom metrics
const errorRate = new Rate('custom_error_rate');
const searchLatency = new Trend('custom_search_latency', true);

export const options = {
    vus: 2,
    duration: '30s',
    thresholds: {
        ...BASE_THRESHOLDS,
        custom_error_rate: ['rate<0.05'],       // < 5% errors for smoke
        custom_search_latency: ['p(95)<1000'],  // search < 1s p95
    },
};

export function setup() {
    const email = __ENV.AUTH_EMAIL || 'testuser@bhoklagyo.com';
    const password = __ENV.AUTH_PASSWORD || 'password123';
    const token = loginUser(email, password);
    return { token };
}

export default function (data) {
    // ---- Public Endpoints ----
    group('Public - Health', () => {
        const res = http.get(`${BASE_URL}/actuator/health`, {
            tags: { name: 'GET /actuator/health' },
        });
        check(res, { 'health UP': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    group('Public - Search', () => {
        const res = http.get(
            urlWithParams('/search', { keyword: 'pizza', limit: 5 }),
            { tags: { name: 'GET /search' } }
        );
        check(res, { 'search 200': (r) => r.status === 200 });
        searchLatency.add(res.timings.duration);
        errorRate.add(res.status !== 200);
    });

    group('Public - Restaurants List', () => {
        const res = http.get(
            urlWithParams('/restaurants', { limit: 5 }),
            { tags: { name: 'GET /restaurants' } }
        );
        check(res, { 'restaurants 200': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    group('Public - Search by Rating', () => {
        const res = http.get(
            urlWithParams('/search/rating', { keyword: 'momo', limit: 5 }),
            { tags: { name: 'GET /search/rating' } }
        );
        check(res, { 'search rating 200': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    group('Public - Search by Price', () => {
        const res = http.get(
            urlWithParams('/search/price', { keyword: 'thali', limit: 5 }),
            { tags: { name: 'GET /search/price' } }
        );
        check(res, { 'search price 200': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    group('Public - Swagger', () => {
        const res = http.get(`${BASE_URL}/v3/api-docs`, {
            tags: { name: 'GET /v3/api-docs' },
        });
        check(res, { 'api-docs 200': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    group('Public - Prometheus Metrics', () => {
        const res = http.get(`${BASE_URL}/actuator/prometheus`, {
            tags: { name: 'GET /actuator/prometheus' },
        });
        check(res, { 'prometheus 200': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    // ---- Authenticated Endpoints ----
    if (data.token) {
        const hdrs = authHeaders(data.token);

        group('Auth - My Orders', () => {
            const res = http.get(`${BASE_URL}/orders`, {
                headers: hdrs,
                tags: { name: 'GET /orders' },
            });
            check(res, { 'my orders 200': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });

        group('Auth - Notifications', () => {
            const res = http.get(`${BASE_URL}/notifications/user`, {
                headers: hdrs,
                tags: { name: 'GET /notifications/user' },
            });
            check(res, { 'notifications 200': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });

        group('Auth - User Profile', () => {
            const res = http.get(`${BASE_URL}/users`, {
                headers: hdrs,
                tags: { name: 'GET /users' },
            });
            check(res, { 'users 200': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });
    }

    sleep(1);
}
