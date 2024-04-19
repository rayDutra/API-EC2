package com.nttdata.user.api.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    private String id;

    private String nome;

    private String cpf;

    private String email;

    private String senha;

    private UserDtoType tipoUsuario;


}
