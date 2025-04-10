package com.victorsaraiva.auth_base_jwt.services.Impl;

import com.victorsaraiva.auth_base_jwt.dtos.user.CreateUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.LoginUserDTO;
import com.victorsaraiva.auth_base_jwt.exceptions.user.*;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.repositories.UserRepository;
import com.victorsaraiva.auth_base_jwt.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper<UserEntity, CreateUserDTO> createUserDTOMapper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Mapper<UserEntity, CreateUserDTO> createUserDTOMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.createUserDTOMapper = createUserDTOMapper;
    }

    @Override
    public UserEntity register(@Valid CreateUserDTO createUserDTO) {
        // Verifica se o e-mail já está cadastrado
        if (userRepository.findByEmail(createUserDTO.getEmail())
                .isPresent()) {
            throw new EmailAlreadyExistsException(createUserDTO.getEmail());
        }
        // Verifica se o username já está cadastrado
        if (userRepository.findByUsername(createUserDTO.getUsername())
                .isPresent()) {
            throw new UsernameAlreadyExistsException(createUserDTO.getUsername());
        }

        try {
            // Converte o dto para entidade
            UserEntity user = createUserDTOMapper.mapFrom(createUserDTO);

            // Encode da senha
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // salva
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserOperationException("Erro ao registrar usuario. Por favor, tente novamente", e);
        }
    }


    @Override
    public UserEntity login(LoginUserDTO loginUserDTO) {
        try {
            UserEntity userFoundByEmail = userRepository.findByEmail(loginUserDTO.getEmail())
                    .orElseThrow(() -> new EmailNotFoundException(loginUserDTO.getEmail()));

            // Verifica se a senha informada é a mesma que a senha cadastrada
            if (passwordEncoder.matches(loginUserDTO.getPassword(), userFoundByEmail.getPassword())) {
                return userFoundByEmail;
            }
            // Senão encontrar a senha é inválida
            throw new InvalidPasswordException();

        } catch (EmailNotFoundException e) {
            throw new EmailNotFoundException("Email não encontrado. Por favor, verifique se o email está correto.");
        } catch (InvalidPasswordException e) {
            throw new InvalidPasswordException();
        } catch (Exception e) {
            throw new UserOperationException("Erro ao logar usuario. Por favor, tente novamente", e);
        }
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));
    }
}