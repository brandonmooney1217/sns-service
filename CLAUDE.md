# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application that provides REST API endpoints for AWS SNS (Simple Notification Service) operations. The application enables email subscription management and message publishing to SNS topics.

## Build and Development Commands

```bash
# Build the application
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Clean build artifacts
./mvnw clean
```

The application runs on port 8080 by default.

## Architecture

### Core Components

**SnsApplication.java**: Main Spring Boot application class that configures the SnsClient bean with US_EAST_1 region and default credential provider chain.

**SnsService.java**: Service layer containing business logic for SNS operations:
- `createTopic(String topicName)`: Creates SNS topics
- `subEmail(String email)`: Subscribes emails to the configured topic
- `publishMessage(String message, String subject)`: Publishes messages to subscribers

**Controller.java**: REST controller with three endpoints:
- `POST /subscribe?email=<email>`: Subscribe email to topic
- `POST /addMessage?body=<message>&subject=<subject>`: Publish message
- `POST /createTopic?topicName=<name>`: Create new topic

### Configuration Pattern

The application uses `@Value("${aws.sns.topic.arn}")` to inject the topic ARN from `application.properties`. The SnsClient is configured as a Spring bean with dependency injection throughout the service layer.

AWS credentials are resolved through the default provider chain (environment variables, AWS credentials file, or IAM roles).

## Key Dependencies

- **spring-boot-starter-web**: REST API functionality
- **software.amazon.awssdk:sns (v2.21.29)**: AWS SNS operations
- **testcontainers**: Integration testing with LocalStack (configured but not actively used)
- **Java 17**: Required runtime version

## Current Implementation Status

This is a minimal viable implementation with:
- Basic CRUD operations for SNS topics and subscriptions
- Direct parameter binding without DTOs
- Simple string responses instead of JSON
- No error handling or input validation
- Single hardcoded region (US_EAST_1)

The codebase includes dependencies for advanced features (DynamoDB, validation, actuator) that are not yet implemented, suggesting planned expansion beyond the current scope.

## Testing the Application

```bash
# Subscribe an email
curl -X POST "http://localhost:8080/subscribe" -d "email=test@example.com"

# Publish a message
curl -X POST "http://localhost:8080/addMessage" -d "body=Hello World" -d "subject=Test"

# Create a topic
curl -X POST "http://localhost:8080/createTopic" -d "topicName=MyTopic"
```

Email subscribers must confirm their subscription via the email sent by AWS SNS before receiving published messages.