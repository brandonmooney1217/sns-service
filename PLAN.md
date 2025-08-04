# SNS Subscription and Publish Web Application - Implementation Plan

## Project Overview

An async subscription and publish web application that allows users to:
- Subscribe email addresses to an SNS topic via a web form
- Publish messages to all subscribers via another web form

**Technology Stack:**
- **Frontend**: TypeScript React
- **Backend**: Java Spring Boot (MVC pattern)
- **Infrastructure**: AWS CDK
- **Services**: AWS SNS, DynamoDB, ECS Fargate

## System Architecture

**Components:**
- **Frontend**: TypeScript React app with subscription/publish forms
- **Backend**: Java Spring Boot REST API 
- **Infrastructure**: AWS SNS topic, API Gateway, ECS/Lambda for hosting
- **Storage**: DynamoDB for subscription management

**Flow:**
1. Frontend sends subscription requests to Spring Boot API
2. API validates email and subscribes to SNS topic
3. Frontend sends publish requests with message content
4. API publishes messages to SNS topic
5. SNS delivers messages to all subscribers

## AWS CDK Infrastructure

**Key Resources:**
- **SNS Topic**: For message distribution
- **DynamoDB Table**: Store subscription metadata and status
- **ECS Fargate**: Host Spring Boot application
- **Application Load Balancer**: Route traffic to ECS
- **VPC**: Secure networking
- **CloudFront**: Frontend distribution
- **S3 Bucket**: Static frontend hosting
- **IAM Roles**: SNS publish/subscribe permissions

**CDK Stacks:**
1. **NetworkStack**: VPC, subnets, security groups
2. **DatabaseStack**: DynamoDB table
3. **MessagingStack**: SNS topic with proper policies  
4. **ComputeStack**: ECS cluster, service, ALB
5. **FrontendStack**: S3, CloudFront distribution

## Java Spring Boot Backend - MVC Pattern

### Package Structure
```
com.example.sns/
├── SnsApplication.java          # Existing main application class
├── controller/
│   └── SnsController.java       # Single controller for all HTTP requests
├── service/
│   └── SnsService.java          # Service layer for SNS operations
├── model/
│   ├── SubscriptionRequest.java
│   ├── PublishRequest.java
│   └── ApiResponse.java
└── config/
    └── AwsConfig.java           # AWS SDK configuration
```

### SnsService Class
**Responsibilities:**
- AWS SNS topic management
- Email subscription operations
- Message publishing to subscribers
- Subscription validation and cleanup

**Key Methods:**
```java
@Service
public class SnsService {
    public String subscribeEmail(String email, String topicArn)
    public boolean unsubscribeEmail(String subscriptionArn)
    public String publishMessage(String message, String topicArn)
    public List<Subscription> listSubscriptions(String topicArn)
    public boolean validateSubscription(String subscriptionArn)
    private boolean isValidEmail(String email)
}
```

**Dependencies:**
- AWS SNS Client (SnsClient)
- Spring's @Value for configuration injection
- Logger for operation tracking

### SnsController Class
**HTTP Endpoints:**
```java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SnsController {
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse> subscribeEmail(@RequestBody SubscriptionRequest request)
    
    @DeleteMapping("/unsubscribe/{subscriptionArn}")
    public ResponseEntity<ApiResponse> unsubscribeEmail(@PathVariable String subscriptionArn)
    
    @PostMapping("/publish")
    public ResponseEntity<ApiResponse> publishMessage(@RequestBody PublishRequest request)
    
    @GetMapping("/subscriptions")
    public ResponseEntity<List<Subscription>> getSubscriptions()
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck()
}
```

**Controller Features:**
- Input validation using @Valid annotations
- Global exception handling with @ControllerAdvice
- Standardized API responses with ApiResponse wrapper
- Request/response logging
- Dependency injection of SnsService

### SnsApplication Class Integration
**Enhanced SnsApplication:**
```java
@SpringBootApplication
@ComponentScan(basePackages = "com.example.sns")
public class SnsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SnsApplication.class, args);
    }
    
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
            .region(Region.of(awsRegion))
            .build();
    }
}
```

**Configuration Integration:**
- application.yml for SNS topic ARN and AWS region
- Profile-specific configurations (dev, test, prod)
- Health check actuator endpoints
- CORS configuration for frontend integration

### Maven Dependencies (pom.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>sns-subscription-app</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>SNS Subscription App</name>
    <description>SNS subscription and publishing web application</description>
    
    <properties>
        <java.version>17</java.version>
        <aws.java.sdk.version>2.21.29</aws.java.sdk.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- AWS SDK v2 -->
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>sns</artifactId>
            <version>${aws.java.sdk.version}</version>
        </dependency>
        
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb</artifactId>
            <version>${aws.java.sdk.version}</version>
        </dependency>
        
        <!-- JSON Processing -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        
        <!-- Logging -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </dependency>
        
        <!-- Testing Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>localstack</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>1.19.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                        <include>**/*Tests.java</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**Key Dependencies Explained:**
- **spring-boot-starter-web**: REST API endpoints and embedded Tomcat
- **spring-boot-starter-actuator**: Health checks and monitoring
- **spring-boot-starter-validation**: Request validation with @Valid
- **aws-java-sdk-sns**: AWS SNS client for subscriptions and publishing
- **aws-java-sdk-dynamodb**: DynamoDB client for subscription metadata
- **jackson-databind**: JSON serialization/deserialization
- **spring-boot-starter-test**: JUnit 5, Mockito, and Spring Test
- **testcontainers**: Integration testing with real AWS services via LocalStack

### MVC Architecture Summary

**Model Layer:**
- `SubscriptionRequest.java` - Email subscription payload
- `PublishRequest.java` - Message publishing payload  
- `ApiResponse.java` - Standardized response wrapper

**View Layer:**
- RESTful JSON responses
- HTTP status codes for operation results
- CORS-enabled for frontend integration

**Controller Layer:**
- `SnsController.java` - Single controller handling all HTTP requests
- Request validation and response formatting
- Exception handling and logging

**Service Layer:**
- `SnsService.java` - Business logic for SNS operations
- AWS SDK integration
- Email validation and subscription management

**Application Layer:**
- `SnsApplication.java` - Existing main class with added AWS configuration
- Bean definitions and component scanning
- Application-wide configurations

## TypeScript Frontend

**Components:**
- **App**: Main application component
- **SubscriptionForm**: Email input with validation and submit
- **PublishForm**: Message textarea with character limit and submit
- **NotificationToast**: Success/error feedback
- **SubscriptionStatus**: Display current subscription count

**State Management:**
- React Context for global state
- Custom hooks for API calls (`useSubscription`, `usePublish`)
- Form state with validation (React Hook Form)
- Loading and error states

**Features:**
- Real-time form validation
- Optimistic UI updates
- Error handling with user-friendly messages
- Responsive design (mobile-first)
- Accessibility (ARIA labels, keyboard navigation)

## Testing Strategy

### Backend Testing (Java)
- **Unit Tests**: JUnit 5 + Mockito for services and controllers
- **Integration Tests**: TestContainers for DynamoDB, Testcontainers Localstack for SNS
- **API Tests**: MockMvc for endpoint testing
- **Contract Tests**: Spring Cloud Contract

### Frontend Testing (TypeScript)
- **Unit Tests**: Jest + React Testing Library for components
- **Integration Tests**: MSW (Mock Service Worker) for API mocking
- **E2E Tests**: Playwright for full user workflows

### Infrastructure Testing
- **CDK Unit Tests**: Jest for CDK construct testing
- **Integration Tests**: Deploy to test environment and validate

### Test Automation
- GitHub Actions CI/CD pipeline
- Automated testing on PR creation
- Integration tests against deployed test environment

## Project Structure
```
sns-subscription-app/
├── infrastructure/          # AWS CDK
│   ├── lib/
│   │   ├── network-stack.ts
│   │   ├── database-stack.ts
│   │   ├── messaging-stack.ts
│   │   ├── compute-stack.ts
│   │   └── frontend-stack.ts
│   └── test/
├── backend/                 # Java Spring Boot
│   ├── src/main/java/
│   │   └── com/example/sns/
│   │       ├── SnsApplication.java
│   │       ├── controller/
│   │       │   └── SnsController.java
│   │       ├── service/
│   │       │   └── SnsService.java
│   │       ├── model/
│   │       │   ├── SubscriptionRequest.java
│   │       │   ├── PublishRequest.java
│   │       │   └── ApiResponse.java
│   │       └── config/
│   │           └── AwsConfig.java
│   └── src/test/
└── frontend/               # TypeScript React
    ├── src/
    │   ├── components/
    │   ├── hooks/
    │   ├── services/
    │   └── types/
    └── tests/
```

## Implementation Order

1. **Set up CDK infrastructure stacks**
   - NetworkStack: VPC and networking
   - DatabaseStack: DynamoDB table
   - MessagingStack: SNS topic
   - ComputeStack: ECS Fargate service
   - FrontendStack: S3 and CloudFront

2. **Implement Spring Boot backend with SNS integration**
   - Configure SnsApplication with AWS beans
   - Implement SnsService for AWS operations
   - Create SnsController for HTTP endpoints
   - Add model classes and validation
   - Configure CORS and security

3. **Build React frontend with TypeScript**
   - Set up project structure and dependencies
   - Create subscription and publish forms
   - Implement API integration hooks
   - Add state management and error handling
   - Style with responsive design

4. **Add comprehensive testing suite**
   - Backend unit and integration tests
   - Frontend component and E2E tests
   - Infrastructure testing with CDK
   - API contract testing

5. **Set up CI/CD pipeline**
   - GitHub Actions workflows
   - Automated testing and deployment
   - Environment-specific configurations

6. **Deploy to AWS and validate end-to-end functionality**
   - Deploy infrastructure via CDK
   - Deploy backend to ECS
   - Deploy frontend to S3/CloudFront
   - Validate complete user workflows

This architecture provides a scalable, maintainable solution with proper separation of concerns and comprehensive testing coverage.