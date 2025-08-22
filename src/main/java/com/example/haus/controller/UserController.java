package com.example.haus.controller;

import com.example.haus.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;


}
