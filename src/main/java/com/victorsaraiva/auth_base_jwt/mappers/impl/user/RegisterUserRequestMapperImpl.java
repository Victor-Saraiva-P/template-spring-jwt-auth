package com.victorsaraiva.auth_base_jwt.mappers.impl.user;

import com.victorsaraiva.auth_base_jwt.dtos.auth.RegisterUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RegisterUserRequestMapperImpl implements Mapper<UserEntity, RegisterUserRequestDTO> {
  private final ModelMapper modelMapper;

  @Override
  public RegisterUserRequestDTO mapTo(UserEntity userEntity) {
    return modelMapper.map(userEntity, RegisterUserRequestDTO.class);
  }

  @Override
  public UserEntity mapFrom(RegisterUserRequestDTO userDTO) {
    return modelMapper.map(userDTO, UserEntity.class);
  }
}
