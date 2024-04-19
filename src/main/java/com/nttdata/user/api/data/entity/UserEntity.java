package com.nttdata.user.api.data.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Getter
@Data
@DynamoDBTable(tableName = "User")
public class UserEntity {

    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBAttribute
    private String nome;

    @DynamoDBAttribute
    private String cpf;

    @DynamoDBAttribute
    private String email;

    @DynamoDBAttribute
    private String senha;

    @DynamoDBAttribute
    @DynamoDBTypeConvertedEnum
    private UserEntityType tipoUsuario;

    public UserEntity(String id, String nome, String cpf, String email, String senha, UserEntityType tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    public UserEntity() {
    }

    public void setSenha(String senha) {
        this.senha = new BCryptPasswordEncoder().encode(senha);
    }
}