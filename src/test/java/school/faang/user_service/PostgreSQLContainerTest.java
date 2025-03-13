package school.faang.user_service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostgreSQLContainerTest {

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    static {
        System.out.println("✅ Запуска POSTGRES");
        POSTGRES.start();

        System.setProperty("spring.datasource.url", POSTGRES.getJdbcUrl());
        System.out.println("✅ spring.datasource.url: " + POSTGRES.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRES.getUsername());
        System.out.println("✅ spring.datasource.username: " + POSTGRES.getUsername());
        System.setProperty("spring.datasource.password", POSTGRES.getPassword());
        System.out.println("✅ spring.datasource.password: " + POSTGRES.getPassword());
    }
    @BeforeAll
    static void init() {
        System.out.println("✅ Запуска POSTGRES");
        POSTGRES.start();
        POSTGRES.waitingFor(Wait.forListeningPort());
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        int mappedPort = POSTGRES.getMappedPort(5432);
        String host = POSTGRES.getHost();
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, mappedPort, POSTGRES.getDatabaseName());

        System.out.println("✅ Используемый хост: " + host);
        System.out.println("✅ Используемый порт: " + mappedPort);
        System.out.println("✅ Оригинальный JDBC URL: " +  POSTGRES.getJdbcUrl());
        System.out.println("✅ Используемый JDBC URL: " + jdbcUrl);
        System.out.println("✅ POSTGRES.getUsername(): " + POSTGRES.getUsername());
        System.out.println("✅ POSTGRES.getPassword(): " + POSTGRES.getPassword());

        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        System.out.println("-------------------------------------");
        System.out.format("getJdbcUrl(): %s%n", POSTGRES.getJdbcUrl());
        System.out.println("-------------------------------------");
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

//    private final DataSource dataSource;
//
//    public PostgreSQLContainerTest(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

    @Test
    void testDatabaseIsRunning() {
        System.out.println("✅ PostgreSQL JDBC URL: " + POSTGRES.getJdbcUrl());
        System.out.println("✅ PostgreSQL Port: " + POSTGRES.getFirstMappedPort());
        System.out.println("✅ PostgreSQL Username: " + POSTGRES.getUsername());
        System.out.println("✅ PostgreSQL Password: " + POSTGRES.getPassword());
        assertTrue(POSTGRES.isRunning());
    }

//    @Test
//    void testDatabaseConnection() throws SQLException {
//        try (Connection conn = dataSource.getConnection()) {
//            assertFalse(conn.isClosed());
//        }
//    }
}