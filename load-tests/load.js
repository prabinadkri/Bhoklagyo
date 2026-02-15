// ============================================================
// Load Test — Sustained traffic simulating normal production
// ============================================================
// Purpose: Validate the system handles expected daily peak load.
//          Ramp up to 50 VUs over 2 min, sustain for 5 min, ramp down.
// Run:     k6 run load-tests/load.js
// Env:     BASE_URL, AUTH_EMAIL, AUTH_PASSWORD
// ============================================================
import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

import { BASE_URL, BASE_THRESHOLDS } from './config.js';
import { loginUser, authHeaders, urlWithParams } from './helpers.js';

// Custom metrics
const errorRate = new Rate('custom_error_rate');
const searchLatency = new Trend('custom_search_latency', true);
const orderCreateLatency = new Trend('custom_order_create_latency', true);
const cacheHits = new Counter('custom_cache_hits');

export const options = {
    stages: [
        { duration: '1m', target: 10 },    // Warm up
        { duration: '1m', target: 30 },    // Ramp to medium
        { duration: '1m', target: 50 },    // Ramp to peak
        { duration: '5m', target: 50 },    // Sustain peak
        { duration: '1m', target: 30 },    // Scale down
        { duration: '1m', target: 0 },     // Cool down
    ],
    thresholds: {
        ...BASE_THRESHOLDS,
        custom_error_rate: ['rate<0.01'],           // < 1% errors
        custom_search_latency: ['p(95)<500'],       // search p95 < 500ms
        custom_order_create_latency: ['p(95)<1000'], // order create p95 < 1s
    },
};

const SEARCH_KEYWORDS = ['pizza', 'momo', 'burger', 'thali', 'chowmein', 'daal', 'biryani', 'noodles'];

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

    // ---- Read-heavy traffic (70% of iterations) ----

    group('Browse - Search', () => {
        const res = http.get(
            urlWithParams('/search', { keyword, limit: 10 }),
            { tags: { name: 'GET /search' } }
        );
        check(res, { 'search OK': (r) => r.status === 200 });
        searchLatency.add(res.timings.duration);
        errorRate.add(res.status !== 200);

        // Check if response came from cache (via response time heuristic)
        if (res.timings.duration < 10) {
            cacheHits.add(1);
        }
    });

    group('Browse - Search by Rating', () => {
        const res = http.get(
            urlWithParams('/search/rating', { keyword, limit: 10 }),
            { tags: { name: 'GET /search/rating' } }
        );
        check(res, { 'rating search OK': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    group('Browse - Restaurants List', () => {
        const res = http.get(
            urlWithParams('/restaurants', { limit: 10 }),
            { tags: { name: 'GET /restaurants' } }
        );
        check(res, { 'restaurants OK': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);

        // Drill into a specific restaurant if results exist
        if (res.status === 200) {
            try {
                const body = res.json();
                const restaurants = body.data?.restaurants || body.data || [];
                if (restaurants.length > 0) {
                    const id = restaurants[0].id;
                    const detail = http.get(`${BASE_URL}/restaurants/${id}`, {
                        tags: { name: 'GET /restaurants/{id}' },
                    });
                    check(detail, { 'restaurant detail OK': (r) => r.status === 200 });
                    errorRate.add(detail.status !== 200);
                }
            } catch (_) { /* ignore parse errors */ }
        }
    });

    group('Browse - Search by Price', () => {
        const res = http.get(
            urlWithParams('/search/price', { keyword, limit: 10 }),
            { tags: { name: 'GET /search/price' } }
        );
        check(res, { 'price search OK': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    // ---- Authenticated traffic (30% of iterations) ----
    if (data.token && Math.random() < 0.3) {
        const hdrs = authHeaders(data.token);

        group('Auth - My Orders', () => {
            const res = http.get(`${BASE_URL}/orders`, {
                headers: hdrs,
                tags: { name: 'GET /orders' },
            });
            check(res, { 'orders OK': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });

        group('Auth - Notifications', () => {
            const res = http.get(`${BASE_URL}/notifications/user`, {
                headers: hdrs,
                tags: { name: 'GET /notifications/user' },
            });
            check(res, { 'notifications OK': (r) => r.status === 200 });
            errorRate.add(res.status !== 200);
        });
    }

    // ---- Health check (always) ----
    group('Infra - Health', () => {
        const res = http.get(`${BASE_URL}/actuator/health`, {
            tags: { name: 'GET /actuator/health' },
        });
        check(res, { 'health OK': (r) => r.status === 200 });
        errorRate.add(res.status !== 200);
    });

    sleep(Math.random() * 2 + 0.5); // 0.5–2.5s think time
}
