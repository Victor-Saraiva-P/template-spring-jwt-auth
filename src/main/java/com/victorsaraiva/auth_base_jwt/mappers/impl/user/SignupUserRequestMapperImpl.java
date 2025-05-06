package com.victorsaraiva.auth_base_jwt.mappers.impl.user;

import com.victorsaraiva.auth_base_jwt.dtos.auth.SignupUserRequestDTO;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SignupUserRequestMapperImpl implements Mapper<UserEntity, SignupUserRequestDTO> {

  private final ModelMapper modelMapper;

  @Override
  public SignupUserRequestDTO mapTo(UserEntity userEntity) {
    return modelMapper.map(userEntity, SignupUserRequestDTO.class);
  }

  @Override
  public UserEntity mapFrom(SignupUserRequestDTO userDTO) {
    return modelMapper.map(userDTO, UserEntity.class);
  }
}
