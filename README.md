# 🎬 Video Streaming Microservices Backend

Welcome to the Backend Services for the Video Streaming Platform. This repository contains the complete microservices architecture designed to handle large-scale video uploads, metadata management, streaming, and user interactions.

---

## 🏗️ System Architecture

The backend is built as a distributed microservices system using **Spring Boot**, **Spring Cloud Netflix Eureka** for service discovery, and **Spring Cloud Gateway** as the API gateway.

```mermaid
graph TD
    Client[Angular Client] -->|HTTP Requests| Gateway[API Gateway - Port 8082]
    Gateway -->|Route & Auth check| AuthService[Auth Service - Port 8081]
    Gateway -->|User Profiles| UserService[User Service - Port 8083]
    Gateway -->|Video Catalog| VideoService[Video Service - Port 8084]
    Gateway -->|Video Uploads| UploadService[Upload Service - Port 8085]
    Gateway -->|Video Serving| StreamingService[Streaming Service - Port 8086]
    Gateway -->|User Engagement| AnalyticsService[Analytics Service - Port 8087]
    
    AuthService -.->|Register/Discover| Eureka[Eureka Server - Port 8761]
    UserService -.->|Register/Discover| Eureka
    VideoService -.->|Register/Discover| Eureka
    UploadService -.->|Register/Discover| Eureka
    StreamingService -.->|Register/Discover| Eureka
    AnalyticsService -.->|Register/Discover| Eureka
```

---

## 🛠️ Tech Stack & Dependencies

- **Java Version:** 21
- **Framework:** Spring Boot 4.x / Spring Cloud (Release Train `2025.1.2`)
- **Service Discovery:** Netflix Eureka Server
- **Routing & Gateway:** Spring Cloud Gateway MVC
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **Data Persistence:** Spring Data JPA, PostgreSQL
- **Build Tool:** Maven
- **Documentation:** OpenAPI/Swagger (Springdoc)

---

## 📁 Directory Structure & Microservices

Every microservice is a standalone Maven module located under the `backend/` directory:

| Service | Port | Description | Primary Dependencies |
| :--- | :--- | :--- | :--- |
| **`eureka-server`** | `8761` | Service Registry where all microservices register themselves. | `spring-cloud-starter-netflix-eureka-server` |
| **`api-gateway`** | `8082` | Entry point for clients. Handles routing, CORS, and Swagger aggregation. | `spring-cloud-starter-gateway-server-webmvc` |
| **`auth-service`** | `8081` | Handles user registration, login, JWT validation, and hashing. | `spring-boot-starter-security`, `jjwt`, `postgresql` |
| **`user-service`** | `8083` | Manages user profiles, custom channels, and subscriber lists. | `spring-boot-starter-data-jpa`, `postgresql` |
| **`video-service`** | `8084` | Manages video catalogs, categories, search, and metadata. | `spring-boot-starter-data-jpa`, `postgresql` |
| **`upload-service`** | `8085` | Handles file storage, chunk uploads, and video binaries. | `spring-boot-starter-web`, `eureka-client` |
| **`streaming-service`** | `8086` | Serves adaptive streams for video playback. | `spring-boot-starter-web`, `eureka-client` |
| **`analytics-service`** | `8087` | Tracks views, likes, comments, and engagement metrics. | `spring-boot-starter-data-jpa`, `postgresql` |

---

## 🚀 Getting Started

### Prerequisites

- **JDK 21** or higher installed.
- Running instance of **PostgreSQL** configured (default port `5432`, username `postgres`, password `Jiban@123`).

### Database Creation
Make sure you create the following databases in your PostgreSQL instance before starting:
* `video_stream_auth`
* `video_stream_user`
* `video_stream_video`
* `video_stream_analytics`

### Running Locally

To start the backend ecosystem in one command:

1. Open PowerShell in the `backend/` directory.
2. Run the startup script:
   ```powershell
   .\run-all.ps1
   ```

---

## ⚙️ Building the Code

To rebuild all microservices at once using the Maven wrapper:

```powershell
Get-ChildItem -Directory | ForEach-Object {
    if (Test-Path (Join-Path $_.FullName "mvnw.cmd")) {
        Write-Host "Building $_..." -ForegroundColor Green
        Push-Location $_.FullName
        .\mvnw.cmd clean install -DskipTests
        Pop-Location
    }
}
```

---

## 📖 API Documentation & Swagger UI
We have centralized Swagger UI at the Gateway. Once all microservices are running:
1. Open **`http://localhost:8082/swagger-ui/index.html`** in your browser.
2. Use the dropdown menu in the header to switch and view endpoints for specific services (e.g. `Auth Service`, `User Service`).
