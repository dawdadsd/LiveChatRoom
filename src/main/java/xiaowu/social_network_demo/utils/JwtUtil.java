package xiaowu.social_network_demo.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xiaowu.social_network_demo.enums.UserRole;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT工具类
 *
 * @author xiaowu
 * @since Java 21
 */
@Slf4j
@Component
@Getter
public class JwtUtil {

    private final String secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final SecretKey signingKey;

    public JwtUtil(@Value("${app.jwt.secret}") String secretKey,
                   @Value("${app.jwt.expirationMs:3600000}") long expirationMs) {
        this.secretKey = secretKey;
        this.accessTokenExpirationMs = expirationMs;
        this.refreshTokenExpirationMs = expirationMs * 24 * 7; // 7天

        // 确保密钥长度足够安全
        var keyBytes = secretKey.getBytes();
        if (keyBytes.length < 32) {
            // 如果密钥不够长，使用SHA-256哈希扩展
            try {
                var digest = java.security.MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(keyBytes);
            } catch (Exception e) {
                throw new RuntimeException("无法生成安全的JWT密钥", e);
            }
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 为用户创建访问令牌
     */
    public String createAccessToken(String userId, String username, UserRole role) {
        var now = Instant.now();
        var expiration = now.plus(accessTokenExpirationMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("role", role.name())
                .claim("type", "access")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 创建刷新令牌
     */
    public String createRefreshToken(String userId) {
        var now = Instant.now();
        var expiration = now.plus(refreshTokenExpirationMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setSubject(userId)
                .claim("type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("JWT解析失败: {}", e.getMessage());
            throw new IllegalArgumentException("无效的JWT令牌", e);
        }
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            var claims = parseToken(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            log.debug("令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中提取用户名
     */
    public String getUsernameFromToken(String token) {
        var claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从令牌中提取用户ID
     */
    public String getUserIdFromToken(String token) {
        var claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从令牌中提取用户角色
     */
    public UserRole getRoleFromToken(String token) {
        var claims = parseToken(token);
        var roleStr = claims.get("role", String.class);
        return UserRole.valueOf(roleStr);
    }

    /**
     * 检查令牌是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 检查是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        try {
            var claims = parseToken(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从请求头中提取Bearer令牌
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
