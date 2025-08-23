package com.example.haus.repository;

import com.example.haus.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findUserDetailByUsername(String username);

}
