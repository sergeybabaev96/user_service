package school.faang.user_service.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "User API",
        description = "API для управления пользователями"
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Локальный сервер"
        )
    }
)
public class OpenApiConfig {
}
