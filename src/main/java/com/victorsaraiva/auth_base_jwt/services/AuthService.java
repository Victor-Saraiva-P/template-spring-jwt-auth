package com.victorsaraiva.auth_base_jwt.services;

import com.victorsaraiva.auth_base_jwt.dtos.user.CreateUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.LoginUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;
import com.victorsaraiva.auth_base_jwt.exceptions.user.EmailAlreadyExistsException;
import com.victorsaraiva.auth_base_jwt.exceptions.user.InvalidCredentialException;
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
  private final Mapper<UserEntity, CreateUserDTO> createUserDTOMapper;
  private final Mapper<UserEntity, UserDTO> userDTOMapper;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      Mapper<UserEntity, CreateUserDTO> createUserDTOMapper,
      Mapper<UserEntity, UserDTO> userDTOMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.createUserDTOMapper = createUserDTOMapper;
    this.userDTOMapper = userDTOMapper;
  }

  public UserDTO register(@Valid CreateUserDTO createUserDTO) {
    // Verifica se o e-mail já está cadastrado
    if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(createUserDTO.getEmail());
    }

    try {
      // Converte o dto para entidade
      UserEntity user = createUserDTOMapper.mapFrom(createUserDTO);

      // Encode da senha
      user.setPassword(passwordEncoder.encode(user.getPassword()));

      // salva
      return userDTOMapper.mapTo(userRepository.save(user));
    } catch (Exception e) {
      throw new UserOperationException("Erro ao registrar usuario. Por favor, tente novamente", e);
    }
  }

  public UserEntity login(LoginUserDTO loginUserDTO) {
    UserEntity userFoundByEmail =
        userRepository
            .findByEmail(loginUserDTO.getEmail())
            .orElseThrow(InvalidCredentialException::new);

    // Verifica se a senha informada é a mesma que a senha cadastrada
    if (passwordEncoder.matches(loginUserDTO.getPassword(), userFoundByEmail.getPassword())) {
      return userFoundByEmail;
    }
    // Senão encontrar a senha é inválida
    throw new InvalidCredentialException();
  }
}
