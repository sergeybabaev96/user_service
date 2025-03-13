package school.faang.user_service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
public class PostgreSQLContainerTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    private final DataSource dataSource;

    public PostgreSQLContainerTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Test
    void testDatabaseIsRunning() {
        System.out.println("PostgreSQL JDBC URL: " + postgres.getJdbcUrl());
        System.out.println("PostgreSQL Username: " + postgres.getUsername());
        System.out.println("PostgreSQL Password: " + postgres.getPassword());
        assertTrue(postgres.isRunning());
    }

    @Test
    void testDatabaseConnection() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            assertFalse(conn.isClosed());
        }
    }
}