# 💳 Spin Transaction Execution Engine

## 📌 Overview
An enterprise-grade, highly scalable RESTful API built to process high-volume financial transactions (CREDIT & DEBIT). 
Designed with Clean Architecture / Hexagonal Architecture principles, this service enforces strict domain validation rules before delegating execution to an external provider, guaranteeing resiliency, data integrity, and complete audit persistence.

## 🌟 Key Architecture & Technical Features
* **Domain-Driven Business Rules:** Standalone validation layer for transaction thresholds (Minimum $1.00 MXN, DEBIT limit <= $10,000.00 MXN, currency enforcement) prior to external provider interaction.
* **Resilient Integration:** Decoupled HTTP client equipped with Circuit Breaker, Retries, and Timeouts via Resilience4j to handle external provider failure scenarios gracefully.
* **Scalable Persistence & Querying:** PostgreSQL integration utilizing Spring Data JPA, optimized with database indexing and offset pagination for transaction history queries.
* **Comprehensive Testing Suite:** Robust TDD/BDD approach leveraging JUnit 5, Mockito, and WireMock for simulating external provider behavior (HTTP 200 OK & 4xx/5xx failures).
* **Container-First Setup:** Fully containerized setup via Docker & Docker Compose for rapid deployment and environment parity.
