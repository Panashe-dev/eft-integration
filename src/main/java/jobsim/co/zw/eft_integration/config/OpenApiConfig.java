package jobsim.co.zw.eft_integration.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("EFT Integration")
                        .description("EFT Integration")
                        .termsOfService("terms")
                        .contact(new Contact().email("panashemugomba99@gmail.com"))
                        .license(new License().name("Jobsim"))
                        .version("v1.0")
                );
    }
}