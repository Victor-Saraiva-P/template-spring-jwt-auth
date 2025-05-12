package com.victorsaraiva.auth_base_jwt.dtos.user;

import com.victorsaraiva.auth_base_jwt.models.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangeRoleRequestDTO {
  @NotBlank(message = "É necessário definir a role")
  @Pattern(regexp = "^(USER|ADMIN)$", message = "A role deve ser USER ou ADMIN")
  private String role;

  public Role getRoleEnum() {
    return Role.valueOf(role);
  }
}
