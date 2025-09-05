package com.example.haus.controller;

import com.example.haus.base.RestApiV1;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

@RestApiV1
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-CONTROLLER")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {


}
