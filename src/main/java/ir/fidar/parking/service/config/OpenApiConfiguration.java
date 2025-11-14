package ir.fidar.parking.service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(components());
    }

    private Info apiInfo() {
        return new Info()
                .title("Parking Management API")
                .version("1.0.0")
                .description("""
                        REST API for managing parking spots and reservations.
                        - List parking spots
                        - Reserve an available spot
                        - Release a reserved spot
                        """)
                .contact(new Contact()
                        .name("Parking Platform Team")
                        .email("parking@company.com"))
                .license(new License()
                        .name("Proprietary License"));
    }

    private Components components() {
        return new Components()
                .addResponses("BadRequest", error("Invalid input"))
                .addResponses("NotFound", error("Resource not found"))
                .addResponses("InternalError", error("Unexpected server error"));
    }

    private ApiResponse error(String message) {
        return new ApiResponse()
                .description(message)
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))));
    }
}
