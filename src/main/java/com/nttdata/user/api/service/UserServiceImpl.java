package com.nttdata.user.api.service;

import com.nttdata.user.api.data.entity.UserEntity;
import com.nttdata.user.api.data.dto.UserDto;
import com.nttdata.user.api.data.mapper.UserMapper;
import com.nttdata.user.api.exception.UserRegistrationException;
import com.nttdata.user.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.nttdata.user.api.exception.CPFUtils.isValidCPF;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(UserDto user) {
        try {
            if (!isValidCPF(user.getCpf())) {
                throw new IllegalArgumentException("CPF inválido");
            }
            if (existsByCpf(user.getCpf())) {
                throw new UserRegistrationException("CPF já cadastrado");
            }
            user.setSenha(passwordEncoder.encode(user.getSenha()));
            UserEntity userEntity = userRepository.save(userMapper.toEntity(user));

            return userMapper.toDto(userEntity);
        } catch (Exception e) {
            throw new UserRegistrationException("Erro ao registrar usuário: " + e.getMessage());
        }
    }
    @Override
    public boolean existsByCpf(String cpf) {
        try {
            return userRepository.existsByCpf(cpf);
        } catch (Exception e) {
            throw new UserRegistrationException("Erro ao verificar CPF: " + e.getMessage());
        }
    }

    @Override
    public boolean findByEmail(UserDto loginRequest) {
        try {
            Optional<UserEntity> userEntityOptional = userRepository.findByEmail(loginRequest.getEmail());
            if (userEntityOptional.isPresent()) {
                UserEntity userEntity = userEntityOptional.get();
                if (passwordEncoder.matches(loginRequest.getSenha(), userEntity.getSenha())) {
                    return true; // Usuário encontrado e senha corresponde
                } else {
                    return false; // Senha não corresponde
                }
            } else {
                return false; // Usuário não encontrado com o e-mail fornecido
            }
        } catch (Exception e) {
            throw new UserRegistrationException("Erro ao buscar usuário por e-mail: " + e.getMessage());
        }
    }
    @Override
    public boolean existsByEmail(String email) {
        try {
            Optional<UserEntity> userEntity = userRepository.findByEmail(email);
            return userEntity.isPresent();
        } catch (Exception e) {
            throw new UserRegistrationException("Erro ao registrar e-mail: " + e.getMessage());
        }
    }
    @Override
    public UserDto findByEmailAndPassword(String email, String senha) {
        try {
            Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);
            if (userEntityOptional.isPresent()) {
                UserEntity userEntity = userEntityOptional.get();
                if (passwordEncoder.matches(senha, userEntity.getSenha())) {
                    return userMapper.toDto(userEntity);
                }
            }
            return null;
        } catch (Exception e) {
            throw new UserRegistrationException("Erro ao buscar usuário por e-mail e senha: " + e.getMessage());
        }
    }
}
