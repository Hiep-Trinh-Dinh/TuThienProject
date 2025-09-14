# Charity Web Backend - Project Management

Spring Boot REST API for charity project management.

## Features

- **Project Management**: CRUD operations for charity projects
- **Search & Filter**: Search projects by keywords, filter by category
- **Sorting**: Sort projects by date, progress, urgency
- **Project Categories**: Support for different charity categories

## API Endpoints

### Projects
- `GET /api/projects` - Get all active projects
- `GET /api/projects/{id}` - Get project by ID
- `GET /api/projects/featured` - Get featured projects
- `GET /api/projects/category/{category}` - Get projects by category
- `GET /api/projects/search?q={keyword}` - Search projects
- `GET /api/projects/sort/{sortBy}` - Sort projects (newest, progress, ending-soon)
- `POST /api/projects` - Create new project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project


## Database Schema

The application uses MySQL with the following main table:
- `projects` - Charity projects with all necessary fields for project management

## Setup

1. **Database Setup**:
   ```sql
   CREATE DATABASE tuthiendb;
   ```

2. **Configuration**:
   Update `application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/tuthiendb?useSSL=false&serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access API**:
   - Base URL: `http://localhost:8080`
   - API Documentation: `http://localhost:8080/api/projects`

## Sample Data

The application includes sample data for testing:
- 8 sample charity projects with different categories and statuses

## CORS Configuration

The API is configured to accept requests from `http://localhost:5173` (React frontend).

## Technologies Used

- Spring Boot 3.5.5
- Spring Data JPA
- MySQL 8
- Lombok
- Maven
