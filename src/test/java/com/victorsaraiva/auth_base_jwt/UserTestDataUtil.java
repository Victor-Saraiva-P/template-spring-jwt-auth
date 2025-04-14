package com.victorsaraiva.auth_base_jwt;

import com.victorsaraiva.auth_base_jwt.dtos.user.CreateUserDTO;
import com.victorsaraiva.auth_base_jwt.models.enums.Role;

public class UserTestDataUtil {

    public static CreateUserDTO criarCreateUserDto(String nome) {
        return CreateUserDTO.builder()
                .username(nome)
                .email(nome + "@gmail.com")
                .password("123456")
                .role(Role.USER)
                .build();
    }
}
