# Parking Management API

A production-grade Spring Boot 3 REST API for managing parking spots and reservations in a single parking lot. Built with **Clean Architecture**, **SOLID principles**, and enterprise-level best practices.

## Architecture Overview

The application follows **Clean Architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│   Presentation Layer (Controllers)      │ ← HTTP handlers, routing
├─────────────────────────────────────────┤
│   Application Layer (Services, DTOs)    │ ← Business logic, transformations
├─────────────────────────────────────────┤
│   Domain Layer (Entities)               │ ← Business rules, core logic
├─────────────────────────────────────────┤
│   Infrastructure Layer (Repositories)   │ ← Database access, persistence
└─────────────────────────────────────────┘
```

**Key Principles Applied:**
- **Single Responsibility Principle**: Each class has one reason to change
- **Dependency Inversion**: High-level modules depend on abstractions
- **Open/Closed Principle**: Open for extension, closed for modification
- **Interface Segregation**: Lean, focused interfaces
- **Liskov Substitution**: Service implementations are interchangeable

## Features

✅ List all parking spots with pagination support  
✅ Filter spots by status (AVAILABLE/RESERVED) with pagination  
✅ Reserve available spots with vehicle plate validation  
✅ Release reserved spots back to available  
✅ Get parking lot occupancy statistics  
✅ MapStruct for type-safe entity-to-DTO mapping  
✅ Custom domain exceptions with error codes and HTTP status codes  
✅ Comprehensive error handling & validation  
✅ Transactional consistency (ACID compliance)  
✅ Swagger UI for API exploration  
✅ Structured logging with Logback  
✅ Docker & Docker Compose support  
✅ H2 (in-memory) and PostgreSQL database support  
✅ Unit tests with 100% service layer coverage  
✅ Pagination support on all list endpoints

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker & Docker Compose (optional)

### Option 1: Run Locally (H2 In-Memory Database)

```bash
# Clone and navigate to project
cd parking-api

# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Option 2: Run with Docker (PostgreSQL)

```bash
# Build and start all services
docker-compose up --build

# View logs
docker-compose logs -f parking-api
```

### Option 3: Build Docker Image Manually

```bash
# Build the Docker image
docker build -t parking-api:1.0.0 .

# Run the container
docker run -p 8080:8080 parking-api:1.0.0
```

## API Documentation

### Access Swagger UI

Navigate to: **http://localhost:8080/swagger-ui.html**

Interactive API documentation with request/response schemas.

### Endpoints

#### 1. List All Parking Spots

```bash
curl -X GET http://localhost:8080/api/spots \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Spots retrieved successfully",
  "data": [
    {
      "id": 1,
      "number": 1,
      "status": "AVAILABLE",
      "reservation": null
    },
    {
      "id": 2,
      "number": 2,
      "status": "RESERVED",
      "reservation": {
        "vehiclePlate": "ABC123",
        "reservedAt": "2025-01-15T10:30:00"
      }
    }
  ]
}
```

#### 2. Reserve a Parking Spot

```bash
curl -X POST http://localhost:8080/api/spots/1/reserve \
  -H "Content-Type: application/json" \
  -d '{
    "vehiclePlate": "XYZ789"
  }'
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Spot reserved successfully",
  "data": {
    "id": 1,
    "number": 1,
    "status": "RESERVED",
    "reservation": {
      "vehiclePlate": "XYZ789",
      "reservedAt": "2025-01-15T10:35:00"
    }
  }
}
```

**Error Response (400 Bad Request - Already Reserved):**
```json
{
  "success": false,
  "message": "Reservation failed",
  "error": "Spot is already reserved"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Resource not found",
  "error": "Spot with id 999 not found"
}
```

#### 3. Release a Parking Spot

```bash
curl -X POST http://localhost:8080/api/spots/1/release \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Spot released successfully",
  "data": {
    "id": 1,
    "number": 1,
    "status": "AVAILABLE",
    "reservation": null
  }
}
```

## Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=ParkingSpotServiceImplTest
```

### Test Coverage

The service layer has comprehensive test coverage including:
- ✅ Successful reservation scenarios
- ✅ Duplicate reservation prevention
- ✅ Non-existent spot handling
- ✅ Successful release scenarios
- ✅ Edge cases and error conditions

## Project Structure

```
parking-api/
├── src/main/java/com/parking/
│   ├── ParkingApiApplication.java          # Entry point & OpenAPI config
│   ├── domain/
│   │   └── entity/                         # Domain entities
│   │       ├── Spot.java
│   │       ├── Reservation.java
│   │       └── SpotStatus.java
│   ├── service/                        # Business logic layer
│   │   │   ├── ParkingSpotService.java
│   │   │   └── ParkingSpotServiceImpl.java
│   │   ├── dto/                            # Data transfer objects
│   │   │   ├── SpotResponseDto.java
│   │   │   ├── ReserveSpotRequestDto.java
│   │   │   └── ApiResponseDto.java
│   │   └── mapper/                         # Entity to DTO conversion
│   │       └── SpotMapper.java
└── controller/                     # REST endpoints
│   │       └── ParkingSpotController.java
│   └── repo/
│       ├── persistence/                    # Data access layer
│       │   ├── SpotRepository.java
│       │   └── ReservationRepository.java
    └── infrastructure/
│       └── bootstrap/                      # Application bootstrap
│           └── DataInitializer.java
├── src/main/resources/
│   └── application.yml                     # Configuration
├── src/test/java/com/parking/
│   └── application/service/
│       └── ParkingSpotServiceImplTest.java
├── Dockerfile                              # Container image
├── docker-compose.yml                      # Multi-container setup
└── pom.xml                                 # Maven configuration
```

## Configuration

### H2 Database (Default)

Auto-configured in-memory database. Access H2 console at:
```
http://localhost:8080/h2-console
```

**Credentials:**
- URL: `jdbc:h2:mem:parkingdb`
- Username: `sa`
- Password: (empty)

### PostgreSQL

Switch to PostgreSQL by setting the active profile:

```bash
# Via environment variable
export SPRING_PROFILES_ACTIVE=postgres
mvn spring-boot:run

# Or via application.properties
spring.profiles.active=postgres
```

**Default Configuration:**
- Host: `localhost`
- Port: `5432`
- Database: `parking_db`
- Username: `parking_user`
- Password: `parking_password`

## Key Design Decisions

### 1. Entity-DTO Separation
Entities are kept pure for database mapping. DTOs define the API contract, preventing internal changes from affecting clients.

### 2. Service Interface
`ParkingSpotService` interface allows multiple implementations and simplifies testing with mocks.

### 3. Transactional Consistency
`@Transactional` ensures atomic operations: reservation and status update either both succeed or both fail.

### 4. Structured Error Handling
- **Global exception handler** catches errors consistently
- **Specific exceptions** (`IllegalStateException`, `NoSuchElementException`) with clear messages
- **HTTP status codes** properly reflect error semantics

### 5. Logging Strategy
- INFO: Business-relevant operations (reserve, release)
- WARN: Validation failures (spot not found, already reserved)
- DEBUG: Detailed application behavior
- ERROR: System failures

### 6. Clean API Responses
All responses follow a consistent `ApiResponseDto` format:
json
{
  "success": boolean,
  "message": string,
  "data": object,
  "error": string (null on success)
}


## Performance Considerations

- **Database Indexing**: `spotNumber` is unique, `status` is indexed implicitly
- **Lazy Loading**: Relationships are loaded only when needed
- **Transactional Boundaries**: Minimized to reduce lock contention
- **Connection Pooling**: Configured via Spring Boot defaults (HikariCP)

## Future Enhancements

- **Reservation Duration**: Track check-in/check-out times
- **Pricing**: Dynamic pricing based on duration
- **Multiple Lots**: Extend to manage multiple parking lots
- **Availability Calendar**: Show availability trends
- **Payment Integration**: Process parking fees
- **WebSocket Notifications**: Real-time spot availability updates
- **Authentication/Authorization**: Secure endpoints with Spring Security
- **Rate Limiting**: Prevent API abuse
- **Metrics & Monitoring**: Integrate Micrometer for observability

## License

Proprietary - All rights reserved

---

**Built with  using Spring Boot 3, Clean Architecture, and Best Practices**