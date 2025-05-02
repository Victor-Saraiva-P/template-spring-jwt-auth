package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.exceptions.refresh_tokens.InvalidRefreshTokenException;
import com.victorsaraiva.auth_base_jwt.models.RefreshTokenEntity;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.RefreshTokenRepository;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService {
  @Value("${security.refresh-token.expiration}")
  private long EXPIRATION; // em milissegundos

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
  }

  public RefreshTokenEntity createRefreshToken(UserEntity user) {
    RefreshTokenEntity refreshToken =
        RefreshTokenEntity.builder()
            .refreshToken(java.util.UUID.randomUUID().toString())
            .user(user)
            .expiryDate(Instant.now().plusMillis(EXPIRATION))
            .build();

    return refreshTokenRepository.save(refreshToken);
  }

  public RefreshTokenEntity validateRefreshToken(String refreshToken) {
    RefreshTokenEntity refreshTokenEntity = findByRefreshToken(refreshToken);

    if (isRefreshTokenExpired(refreshTokenEntity)) {
      deleteRefreshToken(refreshTokenEntity);
      throw new InvalidRefreshTokenException(refreshToken);
    }

    return refreshTokenEntity;
  }

  public RefreshTokenEntity findByRefreshToken(String refreshToken) {
    return refreshTokenRepository
        .findByRefreshToken(refreshToken)
        .orElseThrow(() -> new InvalidRefreshTokenException(refreshToken));
  }

  public boolean isRefreshTokenExpired(RefreshTokenEntity refreshToken) {
    return refreshToken.getExpiryDate().isBefore(Instant.now());
  }

  public void deleteRefreshToken(RefreshTokenEntity refreshToken) {
    refreshTokenRepository.delete(refreshToken);
  }
}
