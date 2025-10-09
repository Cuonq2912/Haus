package com.example.haus.repository;

import com.example.haus.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findUserDetailsByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    boolean existsUserByUsernameAndIsDeletedFalse(String username);

    boolean existsUserByEmailAndIsDeletedFalse(String email);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByIdAndIsDeletedFalse(String id);

}
