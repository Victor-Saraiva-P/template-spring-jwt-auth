package com.victorsaraiva.auth_base_jwt.mappers.impl.user;


import com.victorsaraiva.auth_base_jwt.dtos.user.CreateUserDTO;
import com.victorsaraiva.auth_base_jwt.mappers.Mapper;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateUserMapperImpl implements Mapper<UserEntity, CreateUserDTO> {
    private final ModelMapper modelMapper;

    @Override
    public CreateUserDTO mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, CreateUserDTO.class);
    }

    @Override
    public UserEntity mapFrom(CreateUserDTO userDTO) {
        return modelMapper.map(userDTO, UserEntity.class);
    }
}
