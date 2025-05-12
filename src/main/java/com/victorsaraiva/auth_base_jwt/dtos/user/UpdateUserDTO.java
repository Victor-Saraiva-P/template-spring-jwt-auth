package com.victorsaraiva.auth_base_jwt.dtos.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {

  private String username;

  @Email(message = "Formato de email inválido")
  private String email;
}
