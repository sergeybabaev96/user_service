package school.faang.user_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Import(PostgreSQLContainerConfiguration.class)
public class PostgreSQLContainerSpringBootTest extends PostgreSQLContainerConfiguration {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws Exception {
        System.out.println("⭐ testDatabaseConnection() in @SpringBootTest.");
        assertThat(dataSource).isNotNull();
        System.out.println("✅ DB is reachable!");
    }
}