package com.example.Bhoklagyo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class InviteTokenUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${invite.token.expiration:172800000}") // default 48h
    private Long inviteExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + inviteExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateInviteToken(Long ownerId, Long restaurantId, String inviteeEmail) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "INVITE");
        claims.put("ownerId", ownerId);
        claims.put("restaurantId", restaurantId);
        claims.put("inviteeEmail", inviteeEmail);
        return createToken(claims, inviteeEmail);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public String getInviteeEmail(String token) { return extractAllClaims(token).get("inviteeEmail", String.class); }
    public Long getRestaurantId(String token) { return extractAllClaims(token).get("restaurantId", Long.class); }
    public Long getOwnerId(String token) { return extractAllClaims(token).get("ownerId", Long.class); }
    public boolean isInviteToken(String token) { return "INVITE".equals(extractAllClaims(token).get("type", String.class)); }
    public Date getExpiration(String token) { return extractAllClaims(token).getExpiration(); }
    public boolean isExpired(String token) { return getExpiration(token).before(new Date()); }
}
