# Drockley - Phase 1

Sprint: Project Foundation, Security Config, and Auth Module Skeleton

How to run (Windows PowerShell):

1. Build the project with Gradle wrapper:

```powershell
.\gradlew.bat clean build
```

2. Run the application:

```powershell
.\gradlew.bat bootRun
```

Notes:
- The project uses JJWT for JWT handling. The default JWT secret is configured under `security.jwt.secret` in `application.properties` (or via `ApplicationProperties`).
- This sprint adds Auth endpoints under `/api/v1/auth` (register, login, refresh).
