package com.victorsaraiva.auth_base_jwt.controller;

import com.victorsaraiva.auth_base_jwt.dtos.user.CreateUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.LoginUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.services.AuthService;
import com.victorsaraiva.auth_base_jwt.services.JwtService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-url}/auth")
public class AuthController {

  private final AuthService authService;

  private final JwtService jwtService;

  public AuthController(AuthService authService, JwtService jwtService) {
    this.authService = authService;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@Valid @RequestBody CreateUserDTO createUserDTO) {
    UserDTO registeredUser = this.authService.register(createUserDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginUserDTO loginUserDTO) {
    UserEntity userEntity = authService.login(loginUserDTO);

    // Gera o token JWT
    String accessToken = jwtService.generateToken(userEntity);

    return ResponseEntity.ok(Map.of("accessToken", accessToken));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/ping")
  public String test() {
    return "Olá mundo";
  }
}
