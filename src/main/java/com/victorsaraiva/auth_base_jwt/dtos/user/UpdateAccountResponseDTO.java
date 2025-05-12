package com.victorsaraiva.auth_base_jwt.dtos.user;

import com.victorsaraiva.auth_base_jwt.dtos.jwt.AccessTokenDTO;

public record UpdateAccountResponseDTO(UserDTO user, AccessTokenDTO accessToken) {}
