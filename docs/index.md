#Taskmaster API

Welcome to the Taskmaster API documentation!

## Overview

Taskmaster API is a microservice for managing tasks and projects.

## Features

- Task creation and management
- RESTful API design
- Integrated with GitHub Actions for CI/CD
- Container deployment to JFrog
- Security scanning with SonarQube and Snyk

## Architecture
```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Taskmaster  │
│     API     │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  PostgreSQL │
└─────────────┘
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven/Gradle
- PostgreSQL database

### Local Development
```bash
# Clone the repository
git clone https://github.com/your-org/taskmaster-api.git

# Navigate to project
cd taskmaster-api

# Run the application
./mvnw spring-boot:run
```

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

#### GET /tasks
Retrieve all tasks

**Response:**
```json
{
  "tasks": [
    {
      "id": 1,
      "title": "Sample Task",
      "status": "pending"
    }
  ]
}
```

#### POST /tasks
Create a new task

**Request Body:**
```json
{
  "title": "New Task",
  "description": "Task description",
  "priority": "high"
}
```

## CI/CD Pipeline

Our GitHub Actions workflow includes:

1. **Linting** - Code quality checks
2. **Testing** - Unit and integration tests
3. **Security Scanning** - SonarQube and Snyk
4. **Build** - Create JAR and container image
5. **Deploy** - Push artifacts to JFrog

## Contributing

Please read our contributing guidelines before submitting PRs.
