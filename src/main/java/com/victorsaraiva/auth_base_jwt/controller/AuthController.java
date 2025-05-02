package com.victorsaraiva.auth_base_jwt.controller;

import com.victorsaraiva.auth_base_jwt.dtos.auth.LoginUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.auth.RegisterUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.jwt.JwtResponseDTO;
import com.victorsaraiva.auth_base_jwt.dtos.jwt.RefreshTokenDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserResponseDTO;
import com.victorsaraiva.auth_base_jwt.models.RefreshTokenEntity;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.services.AcessTokenService;
import com.victorsaraiva.auth_base_jwt.services.AuthService;
import com.victorsaraiva.auth_base_jwt.services.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-url}/auth")
public class AuthController {

  private final AuthService authService;

  private final AcessTokenService acessTokenService;

  private final RefreshTokenService refreshTokenService;

  public AuthController(
      AuthService authService,
      AcessTokenService acessTokenService,
      RefreshTokenService refreshTokenService) {
    this.authService = authService;
    this.acessTokenService = acessTokenService;
    this.refreshTokenService = refreshTokenService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponseDTO> register(
      @Valid @RequestBody RegisterUserRequestDTO registerUserRequestDTO) {
    UserResponseDTO registeredUser = this.authService.register(registerUserRequestDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponseDTO> login(
      @Valid @RequestBody LoginUserRequestDTO loginUserRequestDTO) {
    UserEntity userEntity = authService.login(loginUserRequestDTO);

    // Gera o acessToken JWT
    String accessToken = acessTokenService.generateToken(userEntity);

    // Gera o refreshToken
    RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userEntity);

    return ResponseEntity.ok(new JwtResponseDTO(accessToken, refreshToken.getRefreshToken()));
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<JwtResponseDTO> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
    // Valida o refreshToken
    RefreshTokenEntity refreshTokenUsed =
        refreshTokenService.validateRefreshToken(refreshTokenDTO.getRefreshToken());

    // Deleta o refreshToken usado
    refreshTokenService.deleteRefreshToken(refreshTokenUsed);

    // Gera um novo refreshToken
    RefreshTokenEntity newRefreshToken =
        refreshTokenUsed = refreshTokenService.createRefreshToken(refreshTokenUsed.getUser());

    // Gera o acessToken JWT
    String accessToken = acessTokenService.generateToken(refreshTokenUsed.getUser());

    return ResponseEntity.ok(new JwtResponseDTO(accessToken, newRefreshToken.getRefreshToken()));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/ping")
  public String test() {
    return "Olá mundo";
  }
}
