# Plan de Implementación de Pruebas - Plataforma de Capacitaciones

## Estado Actual
- Solo existe la prueba básica de aplicación (PlataformaCapacitacionesApplicationTests.java).
- Carpetas de pruebas vacías para controller, service, repository.
- Jacoco configurado para cobertura del 80%.

## Objetivo
Implementar pruebas unitarias e integración para alcanzar cobertura del 80%, enfocándonos en servicios y controladores críticos.

## Pasos a Seguir

### 1. Preparación del Entorno de Pruebas
- [ ] Agregar dependencias de prueba en build.gradle (si faltan).
- [ ] Configurar application-test.properties para pruebas.
- [ ] Crear clases de prueba base (TestBase.java) para configuración común.

### 2. Pruebas Unitarias de Servicios
- [ ] UsuarioServiceTest.java: Probar métodos CRUD con Mockito.
- [ ] AuthServiceTest.java: Probar autenticación y JWT.
- [ ] CursoServiceTest.java: Probar lógica de cursos.
- [ ] InscripcionServiceTest.java: Probar inscripciones.
- [ ] EvaluacionServiceTest.java: Probar evaluaciones.
- [ ] BadgeServiceTest.java: Probar badges.
- [ ] CertificadoServiceTest.java: Probar certificados.

### 3. Pruebas de Integración de Controladores
- [ ] UsuarioControllerTest.java: Endpoints REST con Testcontainers.
- [ ] AuthControllerTest.java: Login/logout.
- [ ] CursoControllerTest.java: CRUD de cursos.
- [ ] InscripcionControllerTest.java: Inscripciones.
- [ ] EvaluacionControllerTest.java: Evaluaciones.
- [ ] BadgeControllerTest.java: Badges.

### 4. Pruebas de Repositories (si hay lógica personalizada)
- [ ] UsuarioRepositoryTest.java: Queries personalizadas.
- [ ] Otros repositories si aplican.

### 5. Pruebas de DAOs
- [ ] CustomUsuarioDAOImplTest.java: Lógica personalizada.
- [ ] Otros DAOs si aplican.

### 6. Verificación y Cobertura
- [ ] Ejecutar `./gradlew test jacocoTestReport`.
- [ ] Revisar reporte de cobertura (build/reports/jacoco).
- [ ] Ajustar pruebas para alcanzar 80%.

### 7. Pruebas de Edge Cases y Validación
- [ ] Casos de error (excepciones, validaciones).
- [ ] Pruebas de seguridad (JWT, roles).

## Notas
- Usar Mockito para mocks en pruebas unitarias.
- Usar Testcontainers para DB real en integración.
- Seguir patrón AAA (Arrange, Act, Assert).
- Crear datos de prueba con builders o fixtures.
