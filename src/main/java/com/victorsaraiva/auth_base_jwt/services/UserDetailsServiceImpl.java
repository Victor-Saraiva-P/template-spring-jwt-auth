package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import com.victorsaraiva.auth_base_jwt.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity userEntityEncontrado =
        userRepository
            .findByEmail(email) // busca o usuário pelo e-mail
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    return new CustomUserDetails(userEntityEncontrado);
  }
}
