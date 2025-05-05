package com.victorsaraiva.auth_base_jwt.dtos.user;

import com.victorsaraiva.auth_base_jwt.models.enums.Role;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
  private UUID id;

  private String username;

  private String email;

  private Role role = Role.USER; // valor padrão
}
