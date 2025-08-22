package com.example.haus.service;

import com.example.haus.domain.entity.user.User;

public interface UserService {

    User findByUsername(String username);
}
