package com.victorsaraiva.auth_base_jwt.util;

import com.victorsaraiva.auth_base_jwt.dtos.user.CreateUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.LoginUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.models.enums.Role;

public class UserTestDataUtil {

    public static CreateUserDTO criarCreateUserDto(String nome) {
        return CreateUserDTO.builder()
                .username(nome)
                .email(nome + "@example.com")
                .password("123456")
                .role(Role.USER)
                .build();
    }

    public static UserEntity criarUserEntity(String nome) {
        return UserEntity.builder()
                .username(nome)
                .email(nome + "@example.com")
                .password("123456")
                .role(Role.USER)
                .build();
    }

    public static UserDTO criarUserDTO(String nome) {
        return UserDTO.builder()
                .username(nome)
                .email(nome + "@example.com")
                .role(Role.USER)
                .build();
    }

    public static LoginUserDTO criarLoginUserDTO(String nome) {
        return LoginUserDTO.builder()
                .email(nome + "@example.com")
                .password("123456")
                .build();
    }
}
