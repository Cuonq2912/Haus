[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)](https://github.com/features/actions)

# Haus

Backend REST API for an e-commerce platform selling furniture and home decor.  
Live: https://haus.net.in/  
API Docs: https://haus.net.in/swagger-ui/index.html

---

## Tech Stack

| Layer | Technology |
| --- | --- |
| Framework | Spring Boot 3.5, Java 17 |
| Auth | Keycloak 26.1, JWT, OAuth2 |
| Database | MySQL 8.0, Spring Data JPA |
| Cache | Redis 7 |
| Storage | Cloudinary |
| Email | SendGrid |
| Payment | VNPay, MoMo, COD |
| Security | Rate limiting (Bucket4j), ClamAV scan |
| Docs | SpringDoc OpenAPI (Swagger) |
| Deploy | Docker, Docker Compose, Nginx |

---

## System Architecture

```mermaid
flowchart LR
    U[Web and Mobile Clients] --> N[Nginx]
    N --> A[Haus Backend API<br/>Spring Boot]

    A --> K[Keycloak]
    A --> M[(MySQL)]
    A --> R[(Redis)]
    A --> C[Cloudinary]
    A --> S[SendGrid]
    A --> P[Payment Gateways<br/>VNPay and MoMo]
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker

### Run Locally (Docker)

```bash
cp .env.example .env
# fill in .env values
docker compose up -d
```

### Run With Maven

```bash
mvn spring-boot:run -Pdev
```

---

## API Documentation

Swagger UI: https://haus.net.in/swagger-ui/index.html

---

## Contributors

- @Cuonq2912
- @quankane
- @Nguyenthithuhang0406
