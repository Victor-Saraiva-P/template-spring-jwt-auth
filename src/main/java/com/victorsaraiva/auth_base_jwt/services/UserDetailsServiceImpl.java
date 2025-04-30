package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import com.victorsaraiva.auth_base_jwt.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    logger.debug("Procurando usuário pelo identificador: {}", email);
    UserEntity userEntityEncontrado =
        userRepository
            .findByEmail(email) // busca o usuário pelo e-mail
            .orElseThrow(
                () -> {
                  logger.error("E-mail não encontrado: {}", email);
                  return new UsernameNotFoundException("Usuário não encontrado");
                });
    logger.info("Usuário encontrado: {}", userEntityEncontrado.getEmail());
    return new CustomUserDetails(userEntityEncontrado);
  }
}
