package com.nttdata.user.api.service;

import com.nttdata.user.api.data.dto.UserDto;

public interface UserService {

    UserDto registerUser(UserDto user);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean findByEmail(UserDto loginRequest);

    UserDto findByEmailAndPassword(String email, String senha);
}
