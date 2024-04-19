package com.nttdata.user.api.data.mapper;

import com.nttdata.user.api.data.dto.UserDto;
import com.nttdata.user.api.data.dto.UserDtoType;
import com.nttdata.user.api.data.entity.UserEntity;
import com.nttdata.user.api.data.entity.UserEntityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    @DisplayName("Criar uma entidade de usuário simulada")
    public void testToDto() {
        UserEntity userEntity = new UserEntity("1", "Fernanda", "123456789", "fernanda@example.com", "senha", UserEntityType.CLIENTE);
        UserDto userDto = userMapper.toDto(userEntity);
        assertEquals(userEntity.getId(), userDto.getId());
        assertEquals(userEntity.getNome(), userDto.getNome());
        assertEquals(userEntity.getCpf(), userDto.getCpf());
        assertEquals(userEntity.getEmail(), userDto.getEmail());
        assertEquals(userEntity.getSenha(), userDto.getSenha());
        Assertions.assertEquals(UserDtoType.CLIENTE, userDto.getTipoUsuario());
    }

    @Test
    @DisplayName("Testar conversão de tipo de entidade para tipo de DTO")
    public void testToDtoType() {
        assertEquals(UserDtoType.CLIENTE, userMapper.toDtoType(UserEntityType.CLIENTE));
        assertEquals(UserDtoType.FORNECEDOR, userMapper.toDtoType(UserEntityType.FORNECEDOR));
        assertEquals(UserDtoType.ADMIN, userMapper.toDtoType(UserEntityType.ADMIN));
    }

    @Test
    @DisplayName("Criar um DTO de usuário simulado")
    public void testToEntity() {
        UserDto userDto = new UserDto("1", "Fernanda J", "987654321", "fefe@example.com", "senha123", UserDtoType.FORNECEDOR);
        UserEntity userEntity = userMapper.toEntity(userDto);
        assertEquals(userDto.getId(), userEntity.getId());
        assertEquals(userDto.getNome(), userEntity.getNome());
        assertEquals(userDto.getCpf(), userEntity.getCpf());
        assertEquals(userDto.getEmail(), userEntity.getEmail());
        assertEquals(userDto.getSenha(), userEntity.getSenha());
        assertEquals(UserEntityType.FORNECEDOR, userEntity.getTipoUsuario());
    }

    @Test
    @DisplayName("Testar conversão de tipo de DTO para tipo de entidade")
    public void testToEntityType() {
        assertEquals(UserEntityType.CLIENTE, userMapper.toEntityType(UserDtoType.CLIENTE));
        assertEquals(UserEntityType.FORNECEDOR, userMapper.toEntityType(UserDtoType.FORNECEDOR));
        assertEquals(UserEntityType.ADMIN, userMapper.toEntityType(UserDtoType.ADMIN));
    }

    @Test
    @DisplayName("Testar conversão com tipo de DTO nulo")
    public void testToEntityType_WithNullType() {
        assertThrows(IllegalArgumentException.class, () -> userMapper.toEntityType(null));
    }

    @Test
    @DisplayName("Testar conversão com entidade nula")
    public void testToDto_NullEntity() {
        assertNull(userMapper.toDto(null));
    }

    @Test
    @DisplayName("Testar conversão com DTO nulo")
    public void testToEntity_NullDto() {
        assertNull(userMapper.toEntity(null));
    }

    @Test
    @DisplayName("Testar conversão de tipo de entidade desconhecido")
    public void testToDtoType_WithUnknownEntityType() {
        assertThrows(IllegalArgumentException.class, () -> userMapper.toDtoType(null));
    }

    @Test
    @DisplayName("Testar conversão de tipo de DTO desconhecido")
    public void testToEntityType_WithUnknownDtoType() {
        assertThrows(IllegalArgumentException.class, () -> userMapper.toEntityType(null));
    }
}
