package co.irond.crediya.api.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Loan applications service for CrediYa .",
        version = "1.0",
        description = "swagger documentation using open api."
))
public class SwaggerConfig {
}