package school.faang.user_service;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@TestConfiguration
public class PostgreSQLContainerConfiguration {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    static {
        postgres.start();
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(postgres.getJdbcUrl());
        dataSource.setUsername(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());

        printInto(postgres);
        return dataSource;
    }

    private void printInto(PostgreSQLContainer<?> postgres) {
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