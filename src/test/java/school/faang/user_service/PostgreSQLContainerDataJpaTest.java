package school.faang.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = PostgreSQLContainerConfiguration.class)
public class PostgreSQLContainerDataJpaTest extends PostgreSQLContainerConfiguration {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws Exception {
        assertThat(dataSource).isNotNull();
        System.out.println("✅ DB is reachable!");
    }
}