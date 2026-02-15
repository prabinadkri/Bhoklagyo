// ============================================================
// Shared helper functions for k6 tests
// ============================================================
import http from 'k6/http';
import { check } from 'k6';

import { BASE_URL } from './config.js';

const JSON_HEADERS = { 'Content-Type': 'application/json' };

/**
 * Login as a regular user and return the JWT token.
 */
export function loginUser(email, password) {
    const res = http.post(
        `${BASE_URL}/auth/login`,
        JSON.stringify({ email, password }),
        { headers: JSON_HEADERS, tags: { name: 'POST /auth/login' } }
    );
    check(res, { 'login success': (r) => r.status === 200 });
    if (res.status === 200) {
        const body = res.json();
        return body.data ? body.data.token : body.token;
    }
    return null;
}

/**
 * Login as an admin and return the JWT token.
 */
export function loginAdmin(email, password) {
    const res = http.post(
        `${BASE_URL}/admin/login`,
        JSON.stringify({ email, password }),
        { headers: JSON_HEADERS, tags: { name: 'POST /admin/login' } }
    );
    check(res, { 'admin login success': (r) => r.status === 200 });
    if (res.status === 200) {
        const body = res.json();
        return body.data ? body.data.token : body.token;
    }
    return null;
}

/**
 * Build authorized headers with JWT token.
 */
export function authHeaders(token) {
    return {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
    };
}

/**
 * Construct a URL with query parameters.
 */
export function urlWithParams(path, params = {}) {
    const query = Object.entries(params)
        .filter(([, v]) => v !== undefined && v !== null)
        .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
        .join('&');
    return query ? `${BASE_URL}${path}?${query}` : `${BASE_URL}${path}`;
}

/**
 * Register a new user (for setup). Ignores 409 Conflict (already exists).
 */
export function registerUser(name, email, password, phone) {
    const res = http.post(
        `${BASE_URL}/auth/register`,
        JSON.stringify({ name, email, password, phoneNumber: phone }),
        { headers: JSON_HEADERS, tags: { name: 'POST /auth/register' } }
    );
    check(res, {
        'register success or conflict': (r) => r.status === 200 || r.status === 201 || r.status === 409,
    });
    return res;
}
