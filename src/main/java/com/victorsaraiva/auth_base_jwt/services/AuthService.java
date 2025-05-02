package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.dtos.auth.LoginUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.auth.RegisterUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserResponseDTO;
import com.victorsaraiva.auth_base_jwt.exceptions.user.EmailAlreadyExistsException;
import com.victorsaraiva.auth_base_jwt.exceptions.user.InvalidCredentialsException;
import com.victorsaraiva.auth_base_jwt.exceptions.user.UserOperationException;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final Mapper<UserEntity, RegisterUserRequestDTO> createUserDTOMapper;
  private final Mapper<UserEntity, UserResponseDTO> userDTOMapper;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      Mapper<UserEntity, RegisterUserRequestDTO> createUserDTOMapper,
      Mapper<UserEntity, UserResponseDTO> userDTOMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.createUserDTOMapper = createUserDTOMapper;
    this.userDTOMapper = userDTOMapper;
  }

  public UserResponseDTO register(@Valid RegisterUserRequestDTO registerUserRequestDTO) {
    // Verifica se o e-mail já está cadastrado
    if (userRepository.findByEmail(registerUserRequestDTO.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(registerUserRequestDTO.getEmail());
    }

    try {
      // Converte o dto para entidade
      UserEntity user = createUserDTOMapper.mapFrom(registerUserRequestDTO);

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
