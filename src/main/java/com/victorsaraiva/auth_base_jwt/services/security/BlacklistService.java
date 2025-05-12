package com.victorsaraiva.auth_base_jwt.services.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlacklistService {

  private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
  private final RedisTemplate<String, String> redisTemplate;
  private final AccessTokenService accessTokenService;

  public void blacklist(String jti, Instant exp) {

    log.debug("Tentando invalidar o token {}", jti);
    long ttl = Duration.between(Instant.now(), exp).getSeconds();

    if (ttl <= 0) return; // token já expirou — não faz sentido guardar

    String key = BLACKLIST_PREFIX + jti;
    redisTemplate.opsForValue().set(key, "1", ttl, TimeUnit.SECONDS);
  }

  public void blacklistByAuthHeader(String authHeader) {

    log.debug("Tentando invalidar o pelo authHeader {}", authHeader);
    // Extrai o access token do header
    String accessToken = authHeader.replace("Bearer ", "");

    // Extrai claims do access token
    String jti = accessTokenService.extractId(accessToken);
    Date exp = accessTokenService.extractExpiration(accessToken);

    blacklist(jti, exp.toInstant());
  }

  public boolean isBlacklisted(String jti) {

    log.debug("Verificando se o token {} está na blacklist", jti);
    String key = BLACKLIST_PREFIX + jti;
    return redisTemplate.hasKey(key);
  }
}
