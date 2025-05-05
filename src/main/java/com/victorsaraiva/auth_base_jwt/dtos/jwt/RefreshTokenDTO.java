package com.victorsaraiva.auth_base_jwt.dtos.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDTO {
  @NotBlank(message = "O refresh token é obrigatório")
  private String refreshToken;
}
