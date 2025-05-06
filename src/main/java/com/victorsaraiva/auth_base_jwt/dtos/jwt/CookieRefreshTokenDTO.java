package com.victorsaraiva.auth_base_jwt.dtos.jwt;

import org.springframework.http.ResponseCookie;

public record CookieRefreshTokenDTO(ResponseCookie tokenCookie, ResponseCookie idCookie) {}
