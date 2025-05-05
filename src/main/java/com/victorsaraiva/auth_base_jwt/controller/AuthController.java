package com.victorsaraiva.auth_base_jwt.controller;

import com.victorsaraiva.auth_base_jwt.dtos.auth.LoginUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.auth.SignupUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.jwt.AccessTokenDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserResponseDTO;
import com.victorsaraiva.auth_base_jwt.models.RefreshTokenEntity;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.services.AccessTokenService;
import com.victorsaraiva.auth_base_jwt.services.AuthService;
import com.victorsaraiva.auth_base_jwt.services.BlacklistService;
import com.victorsaraiva.auth_base_jwt.services.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("${api.base-url}/auth")
@RequiredArgsConstructor
public class AuthController {

  @Value("${security.refresh-token.expiration}")
  private long REFRESH_TOKEN_EXPIRATION; // em milissegundos

  private final AuthService authService;

  private final AccessTokenService accessTokenService;

  private final RefreshTokenService refreshTokenService;

  private final BlacklistService blacklistService;

  @PostMapping("/signup")
  public ResponseEntity<UserResponseDTO> signup(
      @Valid @RequestBody SignupUserRequestDTO signupUserRequestDTO) {
    UserResponseDTO registeredUser = this.authService.signup(signupUserRequestDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
  }

  @PostMapping("/login")
  public ResponseEntity<AccessTokenDTO> login(
      @Valid @RequestBody LoginUserRequestDTO loginUserRequestDTO) {

    // Extrai o usuario do request
    UserEntity userEntity = authService.login(loginUserRequestDTO);

    // Gera o acessToken JWT
    String accessToken = accessTokenService.generateToken(userEntity);

    // Gera o refreshToken
    RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userEntity);

    // Adiciona o refreshToken no cookie
    ResponseCookie cookie =
        ResponseCookie.from("refreshToken", refreshToken.getToken())
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(REFRESH_TOKEN_EXPIRATION)
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new AccessTokenDTO(accessToken));
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<AccessTokenDTO> refreshToken(
      @CookieValue("refreshToken") String oldRefreshToken) {
    // Valida o refreshToken
    RefreshTokenEntity oldRt = refreshTokenService.validateRefreshToken(oldRefreshToken);

    // Estabelece quem é o usuario
    UserEntity user = oldRt.getUser();

    // Deleta o refreshToken usado
    refreshTokenService.deleteByRefreshToken(oldRt);

    // Gera um novo refreshToken
    RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(user);

    // Gera o accessToken JWT
    String accessToken = accessTokenService.generateToken(user);

    // Adiciona o refreshToken no cookie
    ResponseCookie cookie =
        ResponseCookie.from("refreshToken", newRefreshToken.getToken())
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(REFRESH_TOKEN_EXPIRATION)
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new AccessTokenDTO(accessToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @RequestHeader("Authorization") String authHeader,
      @CookieValue("refreshToken") String refreshToken) {

    String accessToken = authHeader.replace("Bearer ", "");

    // Extrai claims do access token
    String jti = accessTokenService.extractId(accessToken);
    Date exp = accessTokenService.extractExpiration(accessToken);

    // Adiciona o access token à blacklist
    blacklistService.blacklist(jti, exp.toInstant());

    // Deleta o refresh token
    refreshTokenService.deleteByToken(refreshToken);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/ping")
  public String test() {
    return "Olá mundo";
  }
}
