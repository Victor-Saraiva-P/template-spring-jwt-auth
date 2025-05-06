package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.dtos.auth.LoginUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.auth.SignupUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserResponseDTO;
import com.victorsaraiva.auth_base_jwt.exceptions.user.EmailAlreadyExistsException;
import com.victorsaraiva.auth_base_jwt.exceptions.user.InvalidCredentialsException;
import com.victorsaraiva.auth_base_jwt.exceptions.user.UserOperationException;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final Mapper<UserEntity, SignupUserRequestDTO> createUserDTOMapper;
  private final Mapper<UserEntity, UserResponseDTO> userDTOMapper;

  public UserResponseDTO signup(@Valid SignupUserRequestDTO signupUserRequestDTO) {
    // Verifica se o e-mail já está cadastrado
    if (userRepository.findByEmail(signupUserRequestDTO.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(signupUserRequestDTO.getEmail());
    }

    try {
      // Converte o dto para entidade
      UserEntity user = createUserDTOMapper.mapFrom(signupUserRequestDTO);

      // Encode da senha
      user.setPassword(passwordEncoder.encode(user.getPassword()));

      // salva
      return userDTOMapper.mapTo(userRepository.save(user));
    } catch (Exception e) {
      throw new UserOperationException("Erro ao registrar usuario. Por favor, tente novamente", e);
    }
  }

  public UserEntity login(LoginUserRequestDTO loginUserRequestDTO) {
    UserEntity userFoundByEmail =
        userRepository
            .findByEmail(loginUserRequestDTO.getEmail())
            .orElseThrow(InvalidCredentialsException::new);

    // Verifica se a senha informada é a mesma que a senha cadastrada
    if (passwordEncoder.matches(
        loginUserRequestDTO.getPassword(), userFoundByEmail.getPassword())) {
      return userFoundByEmail;
    }
    // Senão encontrar a senha é inválida
    throw new InvalidCredentialsException();
  }
}
