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

  @Value("${jwt.secret:defaultSecretKeyWhichShouldBeAtLeast32CharactersLong}")
  private String secret;

  @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
  private long jwtExpiration;

  // Generate token for user
  public String generateToken(String username, Role role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role.name());
    return createToken(claims, username);
  }

  // Create token with claims and subject
  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
      .setClaims(claims)
      .setSubject(subject)
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  // Get signing key
  private Key getSigningKey() {
    byte[] keyBytes = secret.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // Extract username from token
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // Extract expiration date from token
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  // Extract role from token
  public String extractRole(String token) {
    final Claims claims = extractAllClaims(token);
    return claims.get("role", String.class);
  }

  // Extract claim from token
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // Extract all claims from token
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  // Check if token is expired
  public Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // Validate token
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
