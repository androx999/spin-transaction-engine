# Spin Transactions API

API REST para el procesamiento de transacciones financieras desarrollada como parte de una prueba técnica.

La solución implementa el flujo principal de creación de transacciones, incluyendo validaciones de negocio, integración con un proveedor externo, persistencia en PostgreSQL, migraciones de base de datos y una estrategia de pruebas unitarias, web y de integración.

---

## 1. Objetivo

El objetivo del proyecto es desarrollar un servicio backend capaz de procesar transacciones financieras de tipo `CREDIT` y `DEBIT`.

El flujo implementado contempla:

1. Recepción de una solicitud de transacción.
2. Validación de los datos recibidos.
3. Aplicación de reglas de negocio antes de consumir servicios externos.
4. Comunicación con un proveedor externo.
5. Interpretación de la respuesta del proveedor.
6. Persistencia del resultado de la operación.
7. Exposición del resultado mediante una API REST.

---

## 2. Stack tecnológico

El proyecto utiliza principalmente:

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Spring Data JPA
- Hibernate
- Spring Cloud OpenFeign
- PostgreSQL
- Flyway
- Docker / Docker Compose
- Testcontainers
- JUnit 5
- Mockito
- MockMvc
- Maven
- Lombok
- Git

---

## 3. Arquitectura

La solución busca mantener una separación clara entre responsabilidades.

```text
src/main/java
└── io.javabrains.transaccionesspinprueba
    │
    ├── application
    │   ├── dto
    │   │   ├── request
    │   │   └── response
    │   └── service
    │
    ├── domain
    │   ├── enums
    │   ├── model
    │   └── repository
    │
    └── infrastructure
        ├── persistence
        │   ├── entity
        │   ├── mapper
        │   └── repository
        │
        ├── provider
        │   ├── client
        │   ├── config
        │   ├── dto
        │   ├── enums
        │   └── exception
        │
        └── web
            ├── controller
            └── exception
```

### Responsabilidades

**Domain**

Contiene los modelos y contratos principales del dominio, evitando dependencias directas hacia tecnologías de infraestructura.

**Application**

Orquesta los casos de uso y contiene la lógica necesaria para procesar una transacción.

**Infrastructure**

Implementa los detalles técnicos de la aplicación:

- Persistencia con JPA/Hibernate.
- PostgreSQL.
- Integración HTTP mediante OpenFeign.
- Controllers REST.
- Manejo de errores.
- Configuración del proveedor.

Esta separación permite reducir el acoplamiento entre la lógica de negocio y las tecnologías utilizadas para persistencia o comunicación externa.

---

## 4. Flujo de creación de una transacción

El flujo principal implementado es:

```text
POST /transactions
        │
        ▼
TransactionController
        │
        ▼
CreateTransactionRequest
        │
        ▼
TransactionService
        │
        ├── Validaciones de negocio
        │
        ▼
ProviderTransactionRequest
        │
        ▼
OpenFeign
        │
        ▼
Proveedor externo
        │
        ├───────────────┐
        │               │
    APPROVED         REJECTED
        │               │
        ▼               ▼
    EXECUTED         REJECTED
        │               │
        └───────┬───────┘
                ▼
       TransactionRepository
                │
                ▼
           PostgreSQL
                │
                ▼
       TransactionResponse
```

El estado utilizado por el proveedor externo se mantiene separado del estado interno de la aplicación.

Por ejemplo:

```text
ProviderStatus.APPROVED
          ↓
TransactionStatus.EXECUTED
```

Esto evita acoplar directamente el modelo de dominio al contrato del proveedor.

---

## 5. Reglas de negocio

Antes de realizar una llamada al proveedor externo se validan las reglas de negocio de la operación.

Actualmente se consideran:

- `accountId` obligatorio.
- Tipo de transacción obligatorio.
- El monto debe ser mayor a `$1.00`.
- Una operación `DEBIT` no puede superar `$10,000.00`.
- Las operaciones `CREDIT` no utilizan el límite anterior.
- La moneda permitida es `MXN`.

Una solicitud inválida es rechazada antes de realizar la llamada al proveedor.

---

## 6. Endpoint implementado

### Crear transacción

```http
POST /transactions
```

Ejemplo de request:

```json
{
  "accountId": "acc-123456",
  "type": "CREDIT",
  "amount": 1500.00,
  "currency": "MXN",
  "description": "Transferencia recibida"
}
```

Ejemplo de una transacción ejecutada:

```json
{
  "id": "UUID",
  "accountId": "acc-123456",
  "type": "CREDIT",
  "amount": 1500.00,
  "currency": "MXN",
  "description": "Transferencia recibida",
  "status": "EXECUTED",
  "providerTransactionId": "txn-789",
  "balanceAfter": 5500.00,
  "createdAt": "2026-08-10T15:00:00Z"
}
```

La creación exitosa devuelve:

```http
HTTP 201 Created
```

---

## 7. Manejo de errores

La API utiliza un `GlobalExceptionHandler` para centralizar el tratamiento de errores.

Una respuesta de error mantiene una estructura consistente:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Debit amount cannot exceed 10000.00",
  "timestamp": "2026-08-10T15:00:00Z"
}
```

Se diferencian principalmente:

- Errores de validación del request.
- Violaciones de reglas de negocio.
- Errores inesperados de la aplicación.

Los detalles técnicos internos no son expuestos directamente al consumidor de la API.

---

## 8. Integración con proveedor externo

La comunicación con el proveedor se implementa mediante **Spring Cloud OpenFeign**.

El contrato del proveedor se encuentra desacoplado de los DTO utilizados por la API.

```text
API DTO
   ↓
Application
   ↓
Provider DTO
   ↓
Feign Client
   ↓
External Provider
```

Los rechazos HTTP del proveedor son procesados mediante un `ErrorDecoder`, que los transforma en una excepción propia de infraestructura.

Esto evita introducir directamente detalles de Feign dentro de la lógica principal del caso de uso.

La URL del proveedor puede configurarse mediante una variable de entorno:

```properties
provider.transaction.url=${PROVIDER_TRANSACTION_URL:http://localhost:8081}
```

---

## 9. Base de datos

Se utiliza **PostgreSQL** como base de datos relacional.

La configuración local utiliza las siguientes propiedades:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/spin_transactions
spring.datasource.username=spin_user
spring.datasource.password=spin_password
```

> Las credenciales anteriores corresponden únicamente al entorno local de desarrollo.

Hibernate utiliza:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Por lo tanto, Hibernate valida el modelo contra el esquema existente, pero no es responsable de crear o modificar las tablas.

---

## 10. Migraciones con Flyway

La administración del esquema se realiza mediante **Flyway**.

Las migraciones se encuentran en:

```text
src/main/resources/db/migration/
```

La migración inicial crea la tabla:

```text
transactions
```

junto con índices utilizados para facilitar consultas posteriores por campos relevantes.

La estrategia utilizada es:

```text
Flyway
   ↓
crea / actualiza esquema
   ↓
Hibernate
   ↓
valida esquema
   ↓
Aplicación
```

Esto permite mantener cambios de base de datos versionados junto con el código fuente.

---

## 11. Docker

Para el ambiente local se utiliza Docker Desktop y Docker Compose.

No es necesario instalar PostgreSQL manualmente si se utiliza el contenedor definido en `compose.yaml`.

### Levantar PostgreSQL

Desde la raíz del proyecto:

```bash
docker compose up -d
```

Verificar los contenedores:

```bash
docker ps
```

También puede consultarse el estado mediante:

```bash
docker compose ps
```

### Detener los contenedores

```bash
docker compose down
```

Para eliminar también los volúmenes:

```bash
docker compose down -v
```

> El uso de `-v` elimina los datos persistidos en los volúmenes asociados al entorno local.

---

# 12. Compilación y ejecución

## Requisitos

Antes de ejecutar el proyecto se recomienda contar con:

- JDK 21
- Maven
- Docker Desktop
- Git

Comprobar Java:

```bash
java -version
```

Comprobar Maven:

```bash
mvn -version
```

Comprobar Docker:

```bash
docker --version
```

---

## 12.1 Levantar la infraestructura

Desde la raíz del proyecto:

```bash
docker compose up -d
```

Verificar:

```bash
docker compose ps
```

---

## 12.2 Compilar el proyecto

Para validar únicamente la compilación:

```bash
mvn clean compile
```

Este comando:

1. Elimina compilaciones anteriores.
2. Compila el código fuente.
3. Verifica errores de compilación.

---

## 12.3 Ejecutar pruebas

Para ejecutar los tests:

```bash
mvn clean test
```

Esto ejecuta las pruebas configuradas mediante JUnit, Mockito, MockMvc y las pruebas de integración correspondientes.

---

## 12.4 Construir el proyecto completo

Antes de entregar o desplegar la aplicación:

```bash
mvn clean install
```

Un resultado correcto debe finalizar con:

```text
BUILD SUCCESS
```

Este proceso compila el proyecto, ejecuta las pruebas y genera el artefacto correspondiente dentro de:

```text
target/
```

---

## 12.5 Ejecutar la aplicación

Con PostgreSQL disponible:

```bash
mvn spring-boot:run
```

También puede ejecutarse desde el IDE utilizando:

```text
TransaccionesspinpruebaApplication
```

Por defecto la aplicación utiliza:

```text
http://localhost:8080
```

---

# 13. Estrategia de testing

Se utilizaron diferentes niveles de pruebas para evitar depender exclusivamente de tests end-to-end.

## Unit Tests

`TransactionServiceTest`

Utiliza:

- JUnit 5
- Mockito

Se prueban escenarios como:

```text
Proveedor APPROVED
        ↓
Transaction EXECUTED

Proveedor REJECTED
        ↓
Transaction REJECTED

Amount <= 1
        ↓
Provider NO invocado

DEBIT > 10000
        ↓
Provider NO invocado

Currency != MXN
        ↓
Provider NO invocado
```

Las dependencias externas son simuladas mediante mocks para probar exclusivamente la lógica del servicio.

---

## Web / Controller Tests

`TransactionControllerTest`

Utiliza **MockMvc** para probar la capa HTTP sin necesidad de levantar todo el entorno.

Actualmente se validan escenarios como:

- Request válido → `201 Created`.
- Request estructuralmente inválido → `400 Bad Request`.
- Violación de regla de negocio → `400 Bad Request`.

---

## Integration Tests

`TransactionJpaRepositoryIT`

Utiliza **Testcontainers + PostgreSQL 16**.

Durante la prueba se crea automáticamente un contenedor PostgreSQL aislado:

```text
JUnit
   ↓
Testcontainers
   ↓
PostgreSQL 16 temporal
   ↓
Flyway
   ↓
Hibernate / JPA
   ↓
Repository
```

Esto permite validar la persistencia contra PostgreSQL real sin depender de la base de datos local del desarrollador.

Al finalizar las pruebas, el contenedor temporal es eliminado.

---

# 14. Decisiones técnicas

### Separación entre dominio y persistencia

El dominio utiliza `Transaction`, mientras que JPA utiliza `TransactionEntity`.

La conversión se realiza mediante un mapper y la infraestructura implementa la abstracción:

```text
TransactionRepository
        ↑
TransactionRepositoryAdapter
        ↓
TransactionJpaRepository
```

Esto evita que la capa de aplicación dependa directamente de JPA.

### DTOs separados del proveedor

Los contratos del proveedor no son reutilizados como modelos de dominio ni como DTOs públicos de la API.

Esto permite modificar una integración externa con menor impacto sobre el resto de la aplicación.

### Validación antes del proveedor

Las reglas de negocio se ejecutan antes de cualquier llamada externa.

Esto evita solicitudes innecesarias y mantiene las reglas principales dentro de la aplicación.

### Migraciones versionadas

Flyway administra el esquema y Hibernate únicamente lo valida.

### Pruebas aisladas

La estrategia diferencia:

```text
Unit Test
   → lógica de negocio

Web Test
   → contrato HTTP

Integration Test
   → persistencia real
```

---

# 15. Estado actual y mejoras pendientes

El proyecto prioriza la implementación completa y testeada del flujo principal de creación de transacciones.

Actualmente se encuentra implementado:

- Creación de transacciones.
- Validaciones de entrada.
- Reglas de negocio.
- Integración con proveedor mediante OpenFeign.
- Manejo de operaciones aprobadas y rechazadas.
- Persistencia PostgreSQL.
- Migraciones Flyway.
- Manejo global de errores.
- Unit testing con Mockito.
- Controller testing con MockMvc.
- Integration testing con Testcontainers.

Como mejoras posteriores se contempla:

- Implementar `GET /transactions`.
- Filtros opcionales por `accountId`, `status` y `type`.
- Paginación mediante `page` y `limit`.
- Incrementar cobertura de pruebas.
- Agregar documentación OpenAPI/Swagger.
- Incorporar pipeline CI/CD.
- Agregar análisis estático de código.
- Mejorar observabilidad y métricas.

---

# 16. Ejecución rápida

Para levantar el proyecto desde cero:

```bash
# 1. Clonar el repositorio
git clone https://github.com/androx999/spin-transaction-engine.git

# 2. Entrar al proyecto
cd transaccionesspinprueba

# 3. Levantar PostgreSQL
docker compose up -d

# 4. Ejecutar pruebas
mvn clean test

# 5. Construir
mvn clean install

# 6. Ejecutar
mvn spring-boot:run
```

---

## Autor

**Andrés Briseño**

Software Developer

Proyecto desarrollado como prueba técnica backend.