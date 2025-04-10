package com.victorsaraiva.auth_base_jwt.controller;


import com.victorsaraiva.auth_base_jwt.dtos.user.CreateUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.LoginUserDTO;
import com.victorsaraiva.auth_base_jwt.jwt.JwtUtil;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody CreateUserDTO createUserDTO) {
        this.authService.register(createUserDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário cadastrado com sucesso");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        UserEntity userEntity = authService.login(loginUserDTO);

        // Gera o token JWT
        String token = jwtUtil.generateToken(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getRole()
        );

        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserEntity> findByEmail(@PathVariable String email) {
        UserEntity userEntity = authService.findUserByEmail(email);
        return ResponseEntity.ok(userEntity);
    }
}