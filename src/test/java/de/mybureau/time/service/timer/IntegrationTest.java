package de.mybureau.time.service.timer;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class IntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:13.1")
            .withDatabaseName("mybureau_test")
            .withUsername("mybureau_test")
            .withPassword("mybureau_test");

    @DynamicPropertySource
    static void dbConnectionProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> db.getJdbcUrl());
        registry.add("spring.datasource.username", () -> "mybureau_test");
        registry.add("spring.datasource.password", () -> "mybureau_test");
        registry.add("spring.datasource.driver-class-name", () -> db.getDriverClassName());
    }
}
