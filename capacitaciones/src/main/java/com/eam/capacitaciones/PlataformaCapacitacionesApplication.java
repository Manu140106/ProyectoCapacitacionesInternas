package com.eam.capacitaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlataformaCapacitacionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlataformaCapacitacionesApplication.class, args);
        System.out.println("\n==============================================");
        System.out.println("🚀 Plataforma de Capacitaciones INICIADA");
        System.out.println("📚 Swagger UI: http://localhost:8080/api/v1/swagger-ui.html");
        System.out.println("📡 API Docs: http://localhost:8080/api/v1/api-docs");
        System.out.println("==============================================\n");
    }
}
