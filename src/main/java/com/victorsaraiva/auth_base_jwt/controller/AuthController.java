package com.victorsaraiva.auth_base_jwt.controller;

import com.victorsaraiva.auth_base_jwt.dtos.auth.LoginUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.auth.SignupUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.jwt.AcessTokenDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserResponseDTO;
import com.victorsaraiva.auth_base_jwt.models.RefreshTokenEntity;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.services.AccessTokenService;
import com.victorsaraiva.auth_base_jwt.services.AuthService;
import com.victorsaraiva.auth_base_jwt.services.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-url}/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  private final AccessTokenService accessTokenService;

  private final RefreshTokenService refreshTokenService;

  public AuthController(
      AuthService authService,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService) {
    this.authService = authService;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
  }

  @PostMapping("/signup")
  public ResponseEntity<UserResponseDTO> signup(
      @Valid @RequestBody SignupUserRequestDTO signupUserRequestDTO) {
    UserResponseDTO registeredUser = this.authService.signup(signupUserRequestDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
  }

  @PostMapping("/login")
  public ResponseEntity<AcessTokenDTO> login(
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
        .body(new AcessTokenDTO(accessToken));
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<AcessTokenDTO> refreshToken(
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
        .body(new AcessTokenDTO(accessToken));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/ping")
  public String test() {
    return "Olá mundo";
  }
}
