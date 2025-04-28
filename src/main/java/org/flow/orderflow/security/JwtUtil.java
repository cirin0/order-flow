package org.flow.orderflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.flow.orderflow.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}") // 24 hours
  private long jwtExpiration;

  @Value("${jwt.refresh.expiration}") // 30 days
  private long refreshExpiration;

  public String generateToken(String username, Role role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role.name());
    claims.put("tokenType", "ACCESS");
    return createToken(claims, username, jwtExpiration);
  }

  public String generateRefreshToken(String username, Role role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role.name());
    claims.put("tokenType", "REFRESH");
    return createToken(claims, username, refreshExpiration);
  }

  private String createToken(Map<String, Object> claims, String subject, long expiration) {
    return Jwts.builder()
      .setClaims(claims)
      .setSubject(subject)
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + expiration))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  private Key getSigningKey() {
    byte[] keyBytes = secret.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public String extractRole(String token) {
    final Claims claims = extractAllClaims(token);
    return claims.get("role", String.class);
  }

  public String extractTokenType(String token) {
    final Claims claims = extractAllClaims(token);
    return claims.get("tokenType", String.class);
  }

  public boolean isRefreshToken(String token) {
    return "REFRESH".equals(extractTokenType(token));
  }

  public boolean isAccessToken(String token) {
    return "ACCESS".equals(extractTokenType(token));
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  public Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
