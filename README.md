# Garden Manager

A comprehensive garden management system built with Spring Boot that enables collaborative garden management with role-based access control.

## Overview

Garden Manager is a RESTful backend application designed to help manage community gardens, private gardens, and agricultural spaces. It provides user management, garden creation and management, role-based access control, and participant collaboration features.

## Features

### User Management
- User registration and authentication with JWT tokens
- Secure password storage
- User profile management
- Role-based access control (ADMIN, CHIEF, EXECUTOR)
- Soft deletion support for data retention

### Garden Management
- Create and manage multiple gardens
- Associate gardens with owners
- Add garden participants with specific roles
- Track garden addresses and metadata
- Soft deletion with audit trail
- Timeline tracking (created, updated, deleted timestamps)

### Access Control
- **ADMIN**: Full system administration capabilities
- **CHIEF**: Garden management and oversight
- **EXECUTOR**: Task execution and participation
- Fine-grained permissions per garden via participant roles

### API Features
- RESTful API with OpenAPI/Swagger documentation
- JWT-based authentication
- Request/response tracking (rqUid, rqTm headers)
- Comprehensive error handling
- Database migration support with Liquibase

## Technology Stack

- **Java 21** - Modern Java LTS version
- **Spring Boot 4.0.2** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data** - Database access layer
- **jOOQ** - Type-safe SQL queries
- **PostgreSQL** - Primary database
- **Liquibase** - Database version control
- **Maven** - Build tool
- **Lombok** - Reduces boilerplate code
- **SpringDoc OpenAPI** - API documentation

## Project Structure

```
garden-manager/
├── core/              # Main application module with REST controllers
├── database/          # Database access layer with jOOQ and repositories
├── db-migrate/        # Liquibase database migrations
├── failure/           # Custom exception and error handling
└── code-coverage/     # Code coverage aggregation
```

## Database Schema

### Users Table
- User authentication and profile information
- Unique login and email
- JWT token management
- Soft deletion support

### Gardens Table
- Garden metadata (name, address)
- Owner reference
- Audit timestamps
- Soft deletion support

### Gardens Participants Table
- Many-to-many relationship between gardens and users
- Role assignment per garden
- Unique constraint per garden-participant pair

### User Roles Table
- Predefined system roles
- Role descriptions

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.x
- PostgreSQL database
- Git

### Building the Project

```bash
# Clone the repository
git clone git@github.com:kodacci/garden-manager.git
cd garden-manager

# Build with Maven
./mvnw clean install
```

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE garden_manager;
```

2. Configure database connection in your application properties

3. Run database migrations:
```bash
./mvnw liquibase:update -pl db-migrate
```

### Running the Application

```bash
# Run the main application
./mvnw spring-boot:run -pl core
```

The application will start on the default port (typically 8080).

### API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration

### Users
- `GET /api/v1/users` - List users
- `GET /api/v1/users/{id}` - Get user by ID
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user (soft)

### Gardens
- `POST /api/v1/gardens` - Create garden
- `GET /api/v1/gardens` - List gardens
- `GET /api/v1/gardens/{id}` - Get garden by ID
- `PUT /api/v1/gardens/{id}` - Update garden
- `DELETE /api/v1/gardens/{id}` - Delete garden (soft)

### User Roles
- `GET /api/v1/roles` - List available roles

## Development

### Running Tests

```bash
# Run all tests
./mvnw test

# Run integration tests
./mvnw verify
```

### Code Coverage

```bash
# Generate code coverage report
./mvnw clean verify
```

Coverage reports are aggregated in the `code-coverage` module.

### CI/CD

The project includes a comprehensive `Jenkinsfile` for continuous integration and deployment:

#### Automated Build Pipeline
- **Automated Testing**: All tests (unit and integration) run automatically on each build
- **Code Coverage Thresholds**: Build fails if coverage drops below configured thresholds
- **Quality Gates**: Integrated SonarQube analysis for code quality checks
- **Dependency Scanning**: Automated vulnerability checks for dependencies
- **Multi-stage Build**: Optimized Docker image creation with layer caching

#### Deployment

**Kubernetes Deployment**
The application is designed for cloud-native deployment on Kubernetes:

```yaml
# Deployment features
- Pod autoscaling based on CPU/memory metrics
- Rolling updates with zero downtime
- Health checks (liveness and readiness probes)
- Resource limits and requests
- ConfigMaps for external configuration
- Secrets management for sensitive data
```

**Service Mesh Integration**
- Compatible with Istio/Linkerd for advanced traffic management
- mTLS between services
- Circuit breakers and retry policies
- Distributed tracing support
- Traffic splitting for canary deployments

**Secrets Management**
- Kubernetes Secrets for database credentials
- JWT signing keys stored securely
- Support for external secret managers (HashiCorp Vault, AWS Secrets Manager)
- Automatic secret rotation capabilities

**Observability**
- **Logs Aggregation**: Structured JSON logging ready for ELK/EFK stack
- **Metrics**: Prometheus-compatible metrics endpoints
- **Distributed Tracing**: OpenTelemetry/Jaeger integration
- **Monitoring Dashboards**: Pre-configured Grafana dashboards
- **Alerting**: Alert rules for critical events and performance degradation

## Security

- All passwords are securely hashed
- JWT tokens for stateless authentication
- Role-based access control for all operations
- Request tracing with unique identifiers
- Comprehensive audit logging with timestamps

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Author

**Andrey Ryabtsev** (kodacci)

## Support

For issues, questions, or contributions, please open an issue on GitHub.
