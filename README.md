# AuthService

AuthService is a Spring Boot-based authentication service that handles user sign-up, login, and token management. It uses Spring Security and JWT for authentication and authorization. Additionally, it integrates with Kafka to publish user signup events for consumption by the UserService.

## Features

- User signup and login functionality
- JWT and Refresh Token-based authentication
- Kafka integration to send user signup details to UserService
- Secure authentication using Spring Security

## Tech Stack

- **Spring Boot**
- **Spring Security**
- **JWT (JSON Web Token)**
- **Kafka**
- **Spring Data JPA**
- ** MySQL**

## Kafka Integration
- When a new user signs up, an event is published to Kafka under the topic `user-signup-topic`.
- UserService consumes this event and registers the user in its database.

## Security
- Uses **Spring Security** for authentication and authorization.
- Implements **JWT** for stateless authentication.
- Supports **refresh tokens** for session extension.

