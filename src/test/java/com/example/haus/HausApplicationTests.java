package com.example.haus;

import com.example.haus.config.DotenvInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ContextConfiguration(initializers = DotenvInitializer.class)
class HausApplicationTests {

    @Test
    void contextLoads() {
    }

}
