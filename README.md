# Library Management System

![Java CI](https://github.com/Ibraheem-Daas/software-project/workflows/Java%20CI%20with%20Maven%20and%20Testcontainers/badge.svg)
[![codecov](https://codecov.io/gh/Ibraheem-Daas/software-project/branch/main/graph/badge.svg)](https://codecov.io/gh/Ibraheem-Daas/software-project)

A comprehensive library management system built with Java, PostgreSQL, and Swing GUI.

## Features

- **User Management**: Register, login, and manage library members
- **Media Management**: Books, DVDs, and other media items
- **Loan System**: Checkout, return, and track borrowed items
- **Fine Calculation**: Automatic fine calculation with multiple strategies
- **Admin Dashboard**: Complete administrative controls
- **Email Notifications**: Automated reminders for overdue items

## Technology Stack

- **Java 17/21** - Core language
- **PostgreSQL 17** - Database
- **Maven 3.9+** - Build tool
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **Testcontainers** - Integration testing
- **JaCoCo** - Code coverage
- **Swing** - GUI framework

## Test Coverage

- **215 total tests** (161 unit tests + 54 integration tests)
- **85-90% code coverage** (with integration tests)
- **Repository layer**: 90-95% coverage
- **Service layer**: 85-90% coverage

## Quick Start

### Prerequisites

- Java 17 or 21
- PostgreSQL 17
- Maven 3.9+
- Docker (for integration tests)

### Setup Database

```sql
CREATE DATABASE library_db;
CREATE USER library_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE library_db TO library_user;
```

### Configure Application

Edit `src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/library_db
db.username=library_user
db.password=your_password
```

### Build and Run

```bash
# Build the project
mvn clean compile

# Run all tests
mvn test

# Generate coverage report
mvn jacoco:report

# Run the application
mvn exec:java -Dexec.mainClass="com.example.library.ui.AppMain"
```

## Testing

### Unit Tests Only
```bash
mvn test -Dtest="!Jdbc*IntegrationTest"
```

### Integration Tests Only
```bash
mvn test -Dtest="Jdbc*IntegrationTest"
```

### All Tests with Coverage
```bash
mvn clean test jacoco:report
```

View coverage report: `target/site/jacoco/index.html`

## CI/CD

The project uses GitHub Actions for continuous integration:

- **Automated testing** on every push and pull request
- **Code coverage** reporting
- **Test artifacts** archived for review

See `.github/workflows/ci.yml` for details.

## Project Structure

```
src/
├── main/
│   ├── java/com/example/library/
│   │   ├── domain/          # Domain models (User, MediaItem, Loan, Fine)
│   │   ├── repository/      # Data access layer (JDBC implementations)
│   │   ├── service/         # Business logic layer
│   │   ├── ui/              # Swing GUI components
│   │   ├── util/            # Database configuration
│   │   └── notification/    # Email notifications
│   └── resources/
│       └── db.properties    # Database configuration
└── test/
    ├── java/com/example/library/
    │   ├── domain/          # Domain tests
    │   ├── repository/      # Repository tests (unit + integration)
    │   ├── service/         # Service layer tests
    │   ├── testcontainers/  # Testcontainers setup
    │   └── util/            # Utility tests
    └── resources/
        └── schema.sql       # Test database schema
```

## Architecture

- **Repository Pattern**: Clean separation of data access
- **Service Layer**: Business logic encapsulation
- **Strategy Pattern**: Flexible fine calculation (Standard, Grace Period, Flat Rate)
- **Singleton Pattern**: Database connection management
- **MVC**: GUI follows Model-View-Controller pattern

## Testcontainers Integration

The project uses Testcontainers for integration testing:

- **Automatic PostgreSQL container** - No manual database setup needed
- **Isolated test environment** - Each test run is independent
- **Real database testing** - Tests run against actual PostgreSQL
- **CI/CD ready** - Works seamlessly in GitHub Actions

See `TESTCONTAINERS_GUIDE.md` for detailed information.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is for educational purposes.

## Contact

- Project Repository: https://github.com/Ibraheem-Daas/software-project
- Issue Tracker: https://github.com/Ibraheem-Daas/software-project/issues
