# User Management API

A Spring Boot application for user management with authentication, authorization, and team management features.

## Features

- Authentication & Authorization using Spring Security and JWT
- Role-based access control (Admin, Scrum Master, Product Owner, Team Member)
- User profiles and preferences
- Team management (create/update teams)

## Technologies Used

- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Token)

## Setup

1. Clone the repository
2. Configure the database in `application.properties`
3. Run the application

## API Endpoints

### Authentication

- `POST /api/auth/signin`: Authenticate a user and get a JWT token
- `POST /api/auth/signup`: Register a new user

### Users

- `GET /api/users`: Get all users (Admin, Scrum Master, Product Owner only)
- `GET /api/users/{id}`: Get user by ID (Admin, Scrum Master, Product Owner, or the user themselves)
- `GET /api/users/me`: Get current user
- `PUT /api/users/{id}/preferences`: Update user preferences
- `DELETE /api/users/{id}`: Delete a user (Admin, Scrum Master only)

### Teams

- `GET /api/teams`: Get all teams
- `GET /api/teams/{id}`: Get team by ID
- `POST /api/teams`: Create a new team (Admin, Scrum Master, Product Owner only)
- `PUT /api/teams/{id}`: Update a team (Admin, Scrum Master, Product Owner only)
- `DELETE /api/teams/{id}`: Delete a team (Admin, Scrum Master only)
- `PUT /api/teams/{teamId}/members/{userId}`: Add a user to a team (Admin, Scrum Master, Product Owner only)
- `DELETE /api/teams/{teamId}/members/{userId}`: Remove a user from a team (Admin, Scrum Master, Product Owner only)

### Test Endpoints

- `GET /api/test/all`: Public content
- `GET /api/test/user`: User content (authenticated users only)
- `GET /api/test/po`: Product Owner content
- `GET /api/test/sm`: Scrum Master content
- `GET /api/test/admin`: Admin content

## Role-Based Access

- **Admin**: Full access to all features and endpoints
- **Scrum Master**: Full access to most features
- **Product Owner**: Can manage teams and view all users
- **Team Member**: Limited access to their own data and team information

## Database Schema

- **User**: Stores user information, credentials, and preferences
- **Role**: Defines user roles (Admin, Scrum Master, Product Owner, Team Member)
- **Team**: Represents teams with members

## Security

- JWT-based authentication
- Password encryption using BCrypt
- Role-based authorization
