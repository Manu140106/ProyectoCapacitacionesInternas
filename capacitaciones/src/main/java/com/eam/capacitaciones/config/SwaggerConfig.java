package com.eam.capacitaciones.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Plataforma de Capacitaciones Interna")
                        .version("1.0.0")
                        .description("API REST para la gestión de cursos, evaluaciones y certificaciones de empleados.\n\n" +
                                "## Autenticación\n" +
                                "La API utiliza JWT (JSON Web Tokens). Para autenticarte:\n" +
                                "1. Llama a POST /auth/login con tu email y contraseña\n" +
                                "2. Copia el token de la respuesta\n" +
                                "3. Click en 'Authorize' arriba\n" +
                                "4. Pega el token (sin 'Bearer')\n\n" +
                                "## Roles\n" +
                                "- **USER**: Acceso básico a cursos\n" +
                                "- **INSTRUCTOR**: Creación y gestión de cursos\n" +
                                "- **ADMIN**: Acceso total al sistema")
                        .contact(new Contact()
                                .name("Equipo EAM")
                                .email("soporte@eam.edu.co")
                                .url("https://www.eam.edu.co"))
                        .license(new License()
                                .name("Propietario")
                                .url("https://www.eam.edu.co")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + "/api/v1")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://capacitaciones.eam.edu.co/api/v1")
                                .description("Servidor de Producción")
                ))
                
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT obtenido en /auth/login\n\n" +
                                                "Ejemplo: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")))
                
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
}
