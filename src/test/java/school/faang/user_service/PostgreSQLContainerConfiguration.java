package school.faang.user_service;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public class PostgreSQLContainerConfiguration {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    static {
        POSTGRES_CONTAINER.start();
        System.out.println("✅ Запуска POSTGRES");
        printInto(POSTGRES_CONTAINER);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        System.out.println("✅ spring.datasource.url: " + POSTGRES_CONTAINER.getJdbcUrl());
        System.out.println("✅ spring.datasource.username: " + POSTGRES_CONTAINER.getUsername());
        System.out.println("✅ spring.datasource.password: " + POSTGRES_CONTAINER.getPassword());

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

//        System.setProperty("spring.datasource.url", POSTGRES_CONTAINER.getJdbcUrl());
//        System.setProperty("spring.datasource.username", POSTGRES_CONTAINER.getUsername());
//        System.setProperty("spring.datasource.password", POSTGRES_CONTAINER.getPassword());

    }

//    @Bean
//    public DataSource dataSource() {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl(POSTGRES_CONTAINER.getJdbcUrl());
//        dataSource.setUsername(POSTGRES_CONTAINER.getUsername());
//        dataSource.setPassword(POSTGRES_CONTAINER.getPassword());
//
//        printInto(POSTGRES_CONTAINER);
//        return dataSource;
//    }

    private static void printInto(PostgreSQLContainer<?> postgres) {
        int mappedPort = postgres.getMappedPort(5432);
        String host = postgres.getHost();
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, mappedPort, postgres.getDatabaseName());

        System.out.println("▶ PostgreSQLContainerConfiguration ");
        System.out.println("✅ Используемый хост: " + host);
        System.out.println("✅ Используемый порт: " + mappedPort);
        System.out.println("✅ Оригинальный JDBC URL: " +  postgres.getJdbcUrl());
        System.out.println("✅ Используемый JDBC URL: " + jdbcUrl);
        System.out.println("✅ POSTGRES.getUsername(): " + postgres.getUsername());
        System.out.println("✅ POSTGRES.getPassword(): " + postgres.getPassword());

        System.out.println("-------------------------------------");
        System.out.format("getJdbcUrl(): %s%n", postgres.getJdbcUrl());
        System.out.println("-------------------------------------");
    }
}