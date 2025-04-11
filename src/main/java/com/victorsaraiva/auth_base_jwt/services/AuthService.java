package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.dtos.user.CreateUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.LoginUserDTO;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;

public interface AuthService {
    void register(CreateUserDTO createUserDTO);

    UserEntity login(LoginUserDTO loginUserDTO);

    UserEntity findUserByEmail(String email);
}
