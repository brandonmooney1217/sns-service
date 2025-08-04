# SNS Subscription and Publishing Application

A Spring Boot REST API application for managing AWS SNS (Simple Notification Service) email subscriptions and message publishing.

## Features

- **Email Subscription Management**: Subscribe email addresses to SNS topics
- **Message Publishing**: Send messages to all confirmed subscribers
- **Topic Management**: Create new SNS topics dynamically
- **AWS Integration**: Direct integration with AWS SNS using SDK v2

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use included Maven wrapper)
- AWS account with SNS access
- AWS credentials configured (environment variables, AWS CLI, or IAM roles)

## Quick Start

### 1. Configure AWS Credentials

```bash
# Option A: Environment variables
export AWS_ACCESS_KEY_ID=your-access-key-id
export AWS_SECRET_ACCESS_KEY=your-secret-access-key

# Option B: AWS CLI
aws configure
```

### 2. Set SNS Topic ARN

Edit `src/main/resources/application.properties`:
```properties
aws.sns.topic.arn=arn:aws:sns:us-east-1:YOUR-ACCOUNT-ID:YOUR-TOPIC-NAME
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Subscribe Email
```bash
curl -X POST "http://localhost:8080/subscribe" \
  -d "email=user@example.com"
```

### Publish Message
```bash
curl -X POST "http://localhost:8080/addMessage" \
  -d "body=Hello World!" \
  -d "subject=Test Notification"
```

### Create Topic
```bash
curl -X POST "http://localhost:8080/createTopic" \
  -d "topicName=MyNotificationTopic"
```

## How It Works

1. **Email Subscription**: When an email is subscribed, AWS SNS sends a confirmation email
2. **Email Confirmation**: User must click the confirmation link to receive messages
3. **Message Publishing**: Messages are sent to all confirmed subscribers
4. **Topic Management**: New topics can be created and their ARNs returned

## Project Structure

```
src/main/java/com/example/sns/
├── SnsApplication.java    # Main Spring Boot application
├── Controller.java        # REST API endpoints
└── SnsService.java       # Business logic for SNS operations
```

## Build Commands

```bash
# Build the project
./mvnw clean package

# Run tests
./mvnw test

# Run the application
./mvnw spring-boot:run
```

## Configuration

The application uses the following configuration:

- **AWS Region**: US_EAST_1 (hardcoded in SnsApplication.java)
- **Topic ARN**: Configured via `application.properties`
- **Port**: 8080 (Spring Boot default)

## Technology Stack

- **Spring Boot 3.5.4**: Web framework and dependency injection
- **AWS SDK for Java v2**: SNS integration
- **Maven**: Build and dependency management
- **Java 17**: Runtime requirement

## Development Notes

This is a minimal implementation focused on core SNS functionality. The application includes dependencies for advanced features (validation, actuator, testing) that can be implemented as needed for production use.