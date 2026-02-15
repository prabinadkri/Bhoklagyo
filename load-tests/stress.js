// ============================================================
// Stress Test — Push beyond normal limits to find breaking point
// ============================================================
// Purpose: Find the system's ceiling. Ramp to 200 VUs.
//          Expect some errors at peak — goal is to identify
//          at what load the system degrades.
// Run:     k6 run load-tests/stress.js
// Env:     BASE_URL, AUTH_EMAIL, AUTH_PASSWORD
// ============================================================
import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

import { BASE_URL } from './config.js';
import { loginUser, authHeaders, urlWithParams } from './helpers.js';

const errorRate = new Rate('custom_error_rate');
const searchLatency = new Trend('custom_search_latency', true);

export const options = {
    stages: [
        { duration: '2m', target: 50 },     // Normal load
        { duration: '2m', target: 100 },    // High load
        { duration: '3m', target: 150 },    // Stress zone
        { duration: '3m', target: 200 },    // Breaking point
        { duration: '2m', target: 100 },    // Recovery
        { duration: '1m', target: 0 },      // Cool down
    ],
    thresholds: {
        // Relaxed thresholds for stress testing
        http_req_duration: ['p(95)<2000', 'p(99)<5000'],
        http_req_failed: ['rate<0.10'],              // Allow up to 10% errors
        custom_error_rate: ['rate<0.10'],
        custom_search_latency: ['p(95)<2000'],
    },
};

const SEARCH_KEYWORDS = ['pizza', 'momo', 'burger', 'thali', 'chowmein', 'daal', 'biryani', 'chicken', 'rice', 'curry'];

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

    // Mix of endpoints to stress-test the full stack
    const scenario = Math.random();

    if (scenario < 0.30) {
        // 30% — Search (hits DB + potentially Redis cache)
        group('Stress - Search', () => {
            const res = http.get(
                urlWithParams('/search', { keyword, limit: 20 }),
                { tags: { name: 'GET /search' } }
            );
            check(res, { 'search OK': (r) => r.status === 200 });
            searchLatency.add(res.timings.duration);
            errorRate.add(res.status !== 200);
        });
    } else if (scenario < 0.50) {
        // 20% — Search by rating
        group('Stress - Search Rating', () => {
            const res = http.get(
                urlWithParams('/search/rating', { keyword, limit: 20 }),
                { tags: { name: 'GET /search/rating' } }
            );
            check(res, { 'rating search OK': (r) => r.status === 200 });
            searchLatency.add(res.timings.duration);
            errorRate.add(res.status !== 200);
        });
    } else if (scenario < 0.65) {
        // 15% — Restaurant listing (cached)
        group('Stress - Restaurants', () => {
            const res = http.get(
                urlWithParams('/restaurants', { limit: 20 }),
                { tags: { name: 'GET /restaurants' } }
            );
            check(res, { 'restaurants OK': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });
    } else if (scenario < 0.80) {
        // 15% — Search by price
        group('Stress - Search Price', () => {
            const res = http.get(
                urlWithParams('/search/price', { keyword, limit: 20 }),
                { tags: { name: 'GET /search/price' } }
            );
            check(res, { 'price search OK': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });
    } else if (scenario < 0.90 && data.token) {
        // 10% — Authenticated: orders
        group('Stress - My Orders', () => {
            const res = http.get(`${BASE_URL}/orders`, {
                headers: authHeaders(data.token),
                tags: { name: 'GET /orders' },
            });
            check(res, { 'orders OK': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });
    } else {
        // 10% — Health and metrics (observability under stress)
        group('Stress - Health', () => {
            const res = http.get(`${BASE_URL}/actuator/health`, {
                tags: { name: 'GET /actuator/health' },
            });
            check(res, { 'health OK': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });
    }

    sleep(Math.random() * 1 + 0.2); // 0.2–1.2s think time (aggressive)
}
