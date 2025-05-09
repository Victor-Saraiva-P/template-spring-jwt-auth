package com.victorsaraiva.auth_base_jwt.services.security;

import com.victorsaraiva.auth_base_jwt.dtos.jwt.CookieRefreshTokenDTO;
import com.victorsaraiva.auth_base_jwt.dtos.jwt.RefreshTokenDTO;
import com.victorsaraiva.auth_base_jwt.exceptions.refresh_tokens.InvalidRefreshTokenException;
import com.victorsaraiva.auth_base_jwt.models.RefreshTokenEntity;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {

  @Value("${security.refresh-token.expiration}")
  private long refreshTokenExpiration; // em milissegundos

  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public RefreshTokenDTO createRefreshToken(UserEntity user) {
    String rawToken = UUID.randomUUID().toString();
    String hashedToken = passwordEncoder.encode(rawToken);

    RefreshTokenEntity refreshToken =
        RefreshTokenEntity.builder()
            .token(hashedToken)
            .user(user)
            .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
            .build();

    refreshToken = refreshTokenRepository.save(refreshToken);

    // Cliente recebe os dois pedaços
    return new RefreshTokenDTO(refreshToken.getId(), rawToken);
  }

  @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
  public RefreshTokenEntity validateRefreshToken(Long tokenId, String rawToken) {

    RefreshTokenEntity entity = findById(tokenId);

    if (isRefreshTokenExpired(entity)) {
      refreshTokenRepository.delete(entity);
      throw new InvalidRefreshTokenException(rawToken);
    }

    if (!passwordEncoder.matches(rawToken, entity.getToken())) {
      throw new InvalidRefreshTokenException(rawToken);
    }

    return entity;
  }

  public RefreshTokenEntity findById(Long tokenId) {
    return refreshTokenRepository.findById(tokenId).orElseThrow(InvalidRefreshTokenException::new);
  }

  public boolean isRefreshTokenExpired(RefreshTokenEntity refreshToken) {
    return refreshToken.getExpiryDate().isBefore(Instant.now());
  }

  public void deleteByRefreshTokenEntity(RefreshTokenEntity refreshToken) {
    refreshTokenRepository.delete(refreshToken);
  }

  @Transactional
  public void deleteRefreshToken(String refreshToken, Long tokenId, UUID userId) {
    RefreshTokenEntity rt =
        refreshTokenRepository
            .findByIdAndUserId(tokenId, userId)
            .orElseThrow(() -> new InvalidRefreshTokenException(refreshToken));

    // checar se já está expirado/revogado
    if (isRefreshTokenExpired(rt)) {
      refreshTokenRepository.delete(rt);
      throw new InvalidRefreshTokenException(refreshToken);
    }

    refreshTokenRepository.delete(rt);
  }

  public CookieRefreshTokenDTO toCookie(RefreshTokenDTO refreshToken) {
    return new CookieRefreshTokenDTO(
        ResponseCookie.from("refreshToken", refreshToken.token())
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMillis(refreshTokenExpiration))
            .build(),
        ResponseCookie.from("refreshTokenId", String.valueOf(refreshToken.id()))
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMillis(refreshTokenExpiration))
            .build());
  }
}
