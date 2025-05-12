package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.dtos.auth.LoginRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.auth.SignupRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;
import com.victorsaraiva.auth_base_jwt.exceptions.user.EmailAlreadyExistsException;
import com.victorsaraiva.auth_base_jwt.exceptions.user.InvalidCredentialsException;
import com.victorsaraiva.auth_base_jwt.exceptions.user.UserOperationException;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final Mapper<UserEntity, SignupRequestDTO> createUserDTOMapper;
  private final Mapper<UserEntity, UserDTO> userMapper;

  @Transactional
  public UserDTO signup(@Valid SignupRequestDTO signupRequestDTO) {
    log.info("Tentando registrar o usuário com o e-mail {}", signupRequestDTO.getEmail());
    // Verifica se o e-mail já está cadastrado
    if (userRepository.findByEmail(signupRequestDTO.getEmail()).isPresent()) {
      log.error("E-mail já cadastrado {}", signupRequestDTO.getEmail());
      throw new EmailAlreadyExistsException(signupRequestDTO.getEmail());
    }

    try {

      // Converte o dto para entidade
      UserEntity user = createUserDTOMapper.mapFrom(signupRequestDTO);

      // Encode da senha
      user.setPassword(passwordEncoder.encode(user.getPassword()));

      // salva
      return userMapper.mapTo(userRepository.save(user));
    } catch (Exception e) {
      throw new UserOperationException("Erro ao registrar usuario. Por favor, tente novamente", e);
    }
  }

  @Transactional(readOnly = true)
  public UserEntity login(LoginRequestDTO loginRequestDTO) {

    log.info("Tentando fazer login com o e-mail {}", loginRequestDTO.getEmail());

    UserEntity userFoundByEmail =
        userRepository
            .findByEmail(loginRequestDTO.getEmail())
            .orElseThrow(InvalidCredentialsException::new);

    // Verifica se a senha informada é a mesma que a senha cadastrada
    if (passwordEncoder.matches(loginRequestDTO.getPassword(), userFoundByEmail.getPassword())) {
      return userFoundByEmail;
    }
    // Senão encontrar a senha é inválida
    log.error("Senha inválida para o e-mail {}", loginRequestDTO.getEmail());
    throw new InvalidCredentialsException();
  }
}
