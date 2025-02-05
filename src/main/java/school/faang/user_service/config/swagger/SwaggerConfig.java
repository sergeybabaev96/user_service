package school.faang.user_service.config.swagger;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI userServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service")
                        .version("1.0.0")
                        .description("User management service")
                        .contact(new Contact()
                                .name("Basilisk8 team")
                                .email("basilisk_eight@gmail.com")
                                .url("https://basilisk.eight.com")
                        )
                        .license(new License()
                                .name("license: Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("User service documentation")
                        .url("https://basilisk.eight.com")
                );
    }
}
