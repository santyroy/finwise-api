package com.roy.finwise.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Value("${application.security.jwt.issuer}")
    private String issuer;

    @Value("${application.security.jwt.audience}")
    private String audience;

    public String generateAccessToken(String userEmail, Map<String, Object> extraClaims) {
        return buildToken(userEmail, extraClaims, "access", accessTokenExpiration);
    }

    public String generateRefreshToken(String userEmail, Map<String, Object> extraClaims) {
        return buildToken(userEmail, extraClaims, "refresh", refreshTokenExpiration);
    }

    private String buildToken(String userEmail, Map<String, Object> extraClaims, String tokenType, long accessTokenExpiration) {
        final Date now = Date.from(Instant.now());
        final Date expiryDate = Date.from(Instant.now().plusMillis(accessTokenExpiration));
        return Jwts
                .builder()
                .subject(userEmail)
                .issuedAt(now)
                .expiration(expiryDate)
                .notBefore(now) // Add not-before claim
                .id(UUID.randomUUID().toString()) // Add JWT ID
                .issuer(issuer) // Add issuer
                .audience().add(audience).and() // Add audience
                .claims(extraClaims) // Add roles as a custom claim
                .claim("type", tokenType)
                .signWith(getSignInKey())
                .compact();
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String tokenType, UserDetails userDetails) {
        final String subject = extractSubject(token);
        final Claims claims = extractAllClaims(token);
        final String type = claims.get("type", String.class);
        final String iss = claims.getIssuer();
        if (iss == null || type == null) return false;
        return iss.equals(issuer) && tokenType.equals(type) && (subject.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenValid(String token, String tokenType) {
        final Claims claims = extractAllClaims(token);
        final String type = claims.get("type", String.class);
        final String iss = claims.getIssuer();
        if (iss == null || type == null) return false;
        return iss.equals(issuer) && tokenType.equals(type) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Expired JWT token");
        } catch (MalformedJwtException e) {
            throw new JwtException("Malformed JWT token");
        } catch (SignatureException e) {
            throw new JwtException("Invalid JWT signature");
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token");
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("The secret key must be at least 256 bits long.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
