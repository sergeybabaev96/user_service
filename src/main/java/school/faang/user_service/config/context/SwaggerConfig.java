package school.faang.user_service.config.context;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .description("API для управления User Service")
                        .version("1.0.0")
                        .contact(
                                new Contact()
                                        .email("vitaliy.zhilitskiy.1998@gmail.com")
                                        .url("https://github.com/Zhltsk-V")
                                        .name("Vitaliy Zhilitskiy")
                        ))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server"));
    }
}