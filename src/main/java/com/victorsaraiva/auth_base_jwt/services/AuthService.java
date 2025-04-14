package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.dtos.user.CreateUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.LoginUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;

public interface AuthService {
    void register(CreateUserDTO createUserDTO);

    UserDTO login(LoginUserDTO loginUserDTO);

}
