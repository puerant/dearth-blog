package com.dearblog.security;

import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * JWT 工具类
 * Token 结构：header.payload.signature
 * Payload 包含：sub（用户名）、jti（唯一 ID，用于黑名单）、iat、exp
 */
@Slf4j
@Component
public class JwtUtil {

    /** Redis 黑名单前缀，Key = blog:jwt:blacklist:{jti} */
    private static final String BLACKLIST_PREFIX = "blog:jwt:blacklist:";

    @Value("${blog.jwt.secret}")
    private String secret;

    @Value("${blog.jwt.expiration}")
    private long expiration;

    private final StringRedisTemplate stringRedisTemplate;

    public JwtUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // ── Token 生成 ─────────────────────────────────────────

    /**
     * 生成访问 Token
     *
     * @param username 用户名
     * @return JWT Token 字符串
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expiration * 1000L);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(getSigningKey())
                .compact();
    }

    // ── Token 解析 ─────────────────────────────────────────

    /**
     * 从 Token 中提取用户名
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 从 Token 中提取 JTI（唯一 ID）
     */
    public String extractJti(String token) {
        return parseClaims(token).getId();
    }

    /**
     * 校验 Token 是否有效（未过期且不在黑名单）
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            String jti = claims.getId();
            // 检查黑名单
            Boolean inBlacklist = stringRedisTemplate.hasKey(BLACKLIST_PREFIX + jti);
            return !Boolean.TRUE.equals(inBlacklist);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将 Token 加入黑名单（用于退出登录）
     * TTL 设置为 Token 剩余有效期，过期后自动清理
     */
    public void invalidateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            long remainingMs = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (remainingMs > 0) {
                stringRedisTemplate.opsForValue().set(
                        BLACKLIST_PREFIX + claims.getId(),
                        "1",
                        remainingMs,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            log.warn("退出登录时加入黑名单失败，token 可能已过期", e);
        }
    }

    // ── 内部方法 ───────────────────────────────────────────

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
