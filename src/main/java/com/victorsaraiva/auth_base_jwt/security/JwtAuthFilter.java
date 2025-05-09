package com.victorsaraiva.auth_base_jwt.security;

import com.victorsaraiva.auth_base_jwt.services.security.AccessTokenService;
import com.victorsaraiva.auth_base_jwt.services.security.BlacklistService;
import com.victorsaraiva.auth_base_jwt.services.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final AccessTokenService accessTokenService;
  private final UserDetailsServiceImpl userDetailsService;
  private final BlacklistService blacklistService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    // Inicialização das variáveis
    String token = null;
    String email = null; // email pois é o identificador único do usuário
    String jti = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {

      token = authHeader.substring(7);
      email = accessTokenService.extractEmail(token);
      jti = accessTokenService.extractId(token);
    }

    // Verifica se o token foi invalidado
    if (jti != null && blacklistService.isBlacklisted(jti)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
      return;
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      UserDetails userDetails = userDetailsService.loadUserByUsername(email);

      if (accessTokenService.isTokenValid(token, userDetails)) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
