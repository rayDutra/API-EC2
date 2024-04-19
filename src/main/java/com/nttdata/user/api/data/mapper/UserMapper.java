package com.nttdata.user.api.data.mapper;

import com.nttdata.user.api.data.dto.UserDto;
import com.nttdata.user.api.data.dto.UserDtoType;
import com.nttdata.user.api.data.entity.UserEntity;
import com.nttdata.user.api.data.entity.UserEntityType;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return new UserDto(
            userEntity.getId(),
            userEntity.getNome(),
            userEntity.getCpf(),
            userEntity.getEmail(),

             userEntity.getSenha(),
            toDtoType(userEntity.getTipoUsuario())
        );
    }

    public UserDtoType toDtoType(UserEntityType userEntityType) {
        if (userEntityType == null) {
            throw new IllegalArgumentException("UserEntityType cannot be null");
        }

        return switch (userEntityType) {
            case CLIENTE -> UserDtoType.CLIENTE;
            case FORNECEDOR -> UserDtoType.FORNECEDOR;
            case ADMIN -> UserDtoType.ADMIN;
        };
    }

    public UserEntity toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return new UserEntity(
            userDto.getId(),
            userDto.getNome(),
            userDto.getCpf(),
            userDto.getEmail(),
            userDto.getSenha(),
            toEntityType(userDto.getTipoUsuario())
        );
    }

    public UserEntityType toEntityType(UserDtoType userDtoType) {
        if (userDtoType == null) {
            throw new IllegalArgumentException("UserDtoType cannot be null");
        }

        return switch (userDtoType) {
            case CLIENTE -> UserEntityType.CLIENTE;
            case FORNECEDOR -> UserEntityType.FORNECEDOR;
            case ADMIN -> UserEntityType.ADMIN;
        };
    }
}
