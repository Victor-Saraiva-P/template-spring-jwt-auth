package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.exceptions.refresh_tokens.InvalidRefreshTokenException;
import com.victorsaraiva.auth_base_jwt.models.RefreshTokenEntity;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.RefreshTokenRepository;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
  @Value("${security.refresh-token.expiration}")
  private long EXPIRATION; // em milissegundos

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
  }

  public RefreshTokenEntity createRefreshToken(UserEntity user) {
    RefreshTokenEntity refreshToken =
        RefreshTokenEntity.builder()
            .token(java.util.UUID.randomUUID().toString())
            .user(user)
            .expiryDate(Instant.now().plusMillis(EXPIRATION))
            .build();

    return refreshTokenRepository.save(refreshToken);
  }

  public RefreshTokenEntity validateRefreshToken(String refreshToken) {
    RefreshTokenEntity refreshTokenEntity = findByToken(refreshToken);

    if (isRefreshTokenExpired(refreshTokenEntity)) {
      deleteByRefreshToken(refreshTokenEntity);
      throw new InvalidRefreshTokenException(refreshToken);
    }

    return refreshTokenEntity;
  }

  public RefreshTokenEntity findByToken(String token) {
    return refreshTokenRepository
        .findByToken(token)
        .orElseThrow(() -> new InvalidRefreshTokenException(token));
  }

  public boolean isRefreshTokenExpired(RefreshTokenEntity refreshToken) {
    return refreshToken.getExpiryDate().isBefore(Instant.now());
  }

  public void deleteByRefreshToken(RefreshTokenEntity refreshToken) {
    refreshTokenRepository.delete(refreshToken);
  }

  public void deleteByToken(String token) {
    RefreshTokenEntity refreshTokenEntity = findByToken(token);
    deleteByRefreshToken(refreshTokenEntity);
  }
}
