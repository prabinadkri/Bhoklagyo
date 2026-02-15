// ============================================================
// Spike Test — Sudden burst of traffic (flash crowd / DDoS-like)
// ============================================================
// Purpose: Validate the system handles sudden traffic spikes
//          and recovers gracefully after the spike subsides.
// Run:     k6 run load-tests/spike.js
// Env:     BASE_URL, AUTH_EMAIL, AUTH_PASSWORD
// ============================================================
import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

import { BASE_URL } from './config.js';
import { loginUser, authHeaders, urlWithParams } from './helpers.js';

const errorRate = new Rate('custom_error_rate');
const recoveryLatency = new Trend('custom_recovery_latency', true);

export const options = {
    stages: [
        { duration: '30s', target: 10 },     // Normal baseline
        { duration: '10s', target: 200 },    // SPIKE! 0→200 in 10s
        { duration: '1m', target: 200 },     // Hold spike
        { duration: '10s', target: 10 },     // Spike subsides
        { duration: '2m', target: 10 },      // Recovery period (monitor latency)
        { duration: '30s', target: 0 },      // Cool down
    ],
    thresholds: {
        // During spike, we expect some degradation
        http_req_duration: ['p(95)<3000', 'p(99)<8000'],
        http_req_failed: ['rate<0.15'],   // Up to 15% errors during spike
        custom_error_rate: ['rate<0.15'],
        // Recovery latency should normalize after spike
        custom_recovery_latency: ['p(95)<1000'],
    },
};

const SEARCH_KEYWORDS = ['pizza', 'momo', 'burger', 'thali', 'chowmein'];

function randomKeyword() {
    return SEARCH_KEYWORDS[Math.floor(Math.random() * SEARCH_KEYWORDS.length)];
}

export function setup() {
    const email = __ENV.AUTH_EMAIL || 'testuser@bhoklagyo.com';
    const password = __ENV.AUTH_PASSWORD || 'password123';
    const token = loginUser(email, password);
    return { token };
}

export default function (data) {
    const keyword = randomKeyword();

    // Simulate typical user behavior: browse → search → view restaurant

    group('Spike - Homepage Browse', () => {
        const res = http.get(
            urlWithParams('/restaurants', { limit: 10 }),
            { tags: { name: 'GET /restaurants' } }
        );
        check(res, { 'restaurants OK': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    group('Spike - Search', () => {
        const res = http.get(
            urlWithParams('/search', { keyword, limit: 10 }),
            { tags: { name: 'GET /search' } }
        );
        check(res, { 'search OK': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
        recoveryLatency.add(res.timings.duration);
    });

    if (data.token && Math.random() < 0.2) {
        group('Spike - Auth Orders', () => {
            const res = http.get(`${BASE_URL}/orders`, {
                headers: authHeaders(data.token),
                tags: { name: 'GET /orders' },
            });
            check(res, { 'orders OK': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });
    }

    group('Spike - Health', () => {
        const res = http.get(`${BASE_URL}/actuator/health`, {
            tags: { name: 'GET /actuator/health' },
        });
        check(res, { 'health OK': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    sleep(Math.random() * 0.5 + 0.1); // Very short think time during spike
}
