package com.example.server.util;

import com.example.server.entity.Permission;
import com.example.server.entity.Role;
import com.example.server.entity.User;
import com.example.server.repository.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret:EZKVeLs0KeNfiTkwUeWu2VSyKdqTToT7}")
    private String secret;

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    private Key getSignInKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user){
        return Jwts.builder()
                .setClaims(buildScope(user))
                .setSubject(user.getEmail())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean validateToken(String token){
        try{
            if((invalidatedTokenRepository.existsById(extractAllClaims(token).getId()))){
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }

    private Map<String, Object> buildScope(User user) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        for (Role role : user.getRoles()) {
            roles.add(role.getName());
            for (Permission permission : role.getPermissions()) {
                permissions.add(permission.getName());
            }
        }
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        return claims;
    }

}
