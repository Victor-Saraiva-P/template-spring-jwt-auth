package com.victorsaraiva.auth_base_jwt.services;

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

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  @Value("${security.refresh-token.expiration}")
  private long refreshTokenExpiration; // em milissegundos

  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshTokenDTO createRefreshToken(UserEntity user) {
    String rawToken = UUID.randomUUID().toString();
    String hashedToken = passwordEncoder.encode(rawToken);

    RefreshTokenEntity entity =
        RefreshTokenEntity.builder()
            .token(hashedToken)
            .user(user)
            .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
            .build();

    entity = refreshTokenRepository.save(entity); // agora tem ID!

    // Cliente recebe os dois pedaços
    return new RefreshTokenDTO(entity.getId(), rawToken);
  }

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

  public void deleteRefreshToken(String refreshToken, Long tokenId, UserEntity loggedUser) {
    RefreshTokenEntity rt = validateRefreshToken(tokenId, refreshToken);

    if (!rt.getUser().equals(loggedUser)) {
      throw new InvalidRefreshTokenException(refreshToken);
    }

    deleteByRefreshTokenEntity(rt);
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
