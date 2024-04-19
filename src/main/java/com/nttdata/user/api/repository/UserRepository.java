package com.nttdata.user.api.repository;

import com.nttdata.user.api.data.entity.UserEntity;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface UserRepository extends CrudRepository<UserEntity,Long> {

    Optional<UserEntity> findByCpf(String cpf);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
