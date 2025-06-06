package com.victorsaraiva.auth_base_jwt.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

  @NotBlank(message = "O email é obrigatório")
  @Email(message = "Formato de email inválido")
  private String email;

  @NotBlank(message = "O password é obrigatória")
  @Size(min = 6, message = "A password deve ter no mínimo 6 caracteres")
  private String password;
}
