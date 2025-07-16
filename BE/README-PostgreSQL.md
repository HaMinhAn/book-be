# Candy Store Application

## Database Setup with PostgreSQL

This application uses PostgreSQL for the database in production.

### Running with Docker Compose

The easiest way to run the application with PostgreSQL is using Docker Compose:

```bash
# From the root directory (where docker-compose.yaml is located)
docker-compose up -d
```

This will:
1. Start a PostgreSQL database container
2. Initialize the database with the required schema
3. Start the Spring Boot application container

### Accessing the Database

- PostgreSQL will be available at: `localhost:5432`
- Database name: `candydb`
- Username: `postgres`
- Password: `postgres`

You can connect to the database using a tool like pgAdmin or DBeaver.

### Stopping the Application

```bash
docker-compose down
```

If you want to clear all data and start fresh:

```bash
docker-compose down -v
```

## Development Setup

### Running the Backend Locally

If you want to run the application locally with PostgreSQL:

1. Make sure PostgreSQL is installed and running
2. Create a database named `candydb`
3. Run the application with the prod profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## Environment Variables

The following environment variables can be set to customize the PostgreSQL connection:

- `SPRING_DATASOURCE_URL` - The JDBC URL for PostgreSQL (default: `jdbc:postgresql://localhost:5432/candydb`)
- `SPRING_DATASOURCE_USERNAME` - PostgreSQL username (default: `postgres`)
- `SPRING_DATASOURCE_PASSWORD` - PostgreSQL password (default: `postgres`)
- `SPRING_JPA_HIBERNATE_DDL_AUTO` - Hibernate DDL auto strategy (default: `update`)
- `SPRING_PROFILES_ACTIVE` - Set to `prod` to use PostgreSQL configuration
