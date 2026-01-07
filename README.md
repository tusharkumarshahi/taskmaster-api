# Taskmaster API

Simple Task Management REST API built with Spring Boot.

## Endpoints

- GET /api/tasks - list tasks
- GET /api/tasks/{id} - get task
- POST /api/tasks - create task
- PUT /api/tasks/{id} - update task
- DELETE /api/tasks/{id} - delete task
- PATCH /api/tasks/{id}/status - update task status
- GET /actuator/health - health

## Notes

- H2 console available at /h2-console
- Use application.yml for configuration
- Lombok is used to reduce boilerplate

## TODO

- Add more integration tests
- Improve complex method in TaskServiceImpl

