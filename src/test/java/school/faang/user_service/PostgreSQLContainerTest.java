package school.faang.user_service;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.Assert.assertTrue;

@Testcontainers
public class PostgreSQLContainerTest {
    @Container
    public PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>()
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    @Test
    void testDatabaseConnection() {
        System.out.println("PostgreSQL JDBC URL: " + postgres.getJdbcUrl());
        System.out.println("PostgreSQL Username: " + postgres.getUsername());
        System.out.println("PostgreSQL Password: " + postgres.getPassword());
        assertTrue(postgres.isRunning());
    }
}
