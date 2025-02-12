# Cards Project v.1.0

This project is part of a microservice architecture responsible for managing cards linked to bank accounts. The service provides RESTful APIs for creating, fetching, updating, and deleting card information. The system leverages various AWS services and modern software development practices to ensure scalability, security, and maintainability.

## Tech Stack

### Languages & Frameworks:

- **Java 21**: Core programming language.
- **Micronaut 4.5.0+**: Framework used to build the microservice with lightweight, fast startup times and cloud-native capabilities.
- **GraphQL**: Used in the query-cards-service for efficient and flexible querying of card-related data, allowing clients to request only the data they need.

### Infrastructure:

- **Amazon DynamoDB**: NoSQL database used for storing card information.
- **AWS Lambda**: Serverless compute platform for running the process-service. It handles event-driven tasks, such as processing card-related operations from Amazon SQS, without the need to manage underlying servers.
- **Amazon ECS Fargate**: A fully managed container orchestration service used to run the management-service and query-service containers. It allows for secure and scalable execution of containers without managing underlying infrastructure, running in private subnets for improved security.
- **Application Load Balancer (ALB)**: Traffic routing between clients and backend services (ECS, EC2), ensuring high availability.
- **Amazon ElastiCache for Valkey**: Distributed caching for low-latency card retrieval.
- **Terraform**: Infrastructure as Code (IaC) tool used for managing AWS resources like DynamoDB, Lambda, API Gateway, ECS, ALB and ElastiCache.
- **LocalStack**: A fully functional local AWS cloud stack used for local testing of AWS services like DynamoDB, Lambda, and ECS.

### Authentication:

- No authentication is implemented in this version, keeping the API accessible for internal use. Future versions may include authentication and authorization mechanisms for enhanced security when used in external environments.

### CI/CD & Containerization:

- **GitHub Actions:** Automated pipeline for building, testing, and deploying the microservice.
- **Docker:** Used to containerize the application for local development and deployment.
- **Amazon ECR (Elastic Container Registry)**: Secure container registry for storing and managing Docker images, integrated with ECS Fargate.

### Testing:

- **JUnit 5:** Framework used for unit and integration testing of the microservice.
- **Mockito:** Library used for mocking dependencies to isolate and test components effectively.
- **AssertJ:** Library used for fluent and readable assertion statements, enhancing test clarity.
- **TestContainers:** Library used to spin up temporary containers for integration testing with services like DynamoDB, SQS, and LocalStack, ensuring tests run in realistic environments.

### Documentation:

- **OpenAPI (via Swagger UI)**: Provides API documentation and an interactive testing interface to explore the RESTful endpoints.

## Features

- **Card Management API**: Create, fetch, update, and delete cards linked to a bank account.
- **GraphQL Support**: card-query-service enables efficient querying with customized responses.
- **Event-Driven Processing**: cards-process-service (AWS Lambda) handles SQS messages for asynchronous card updates.
- **Distributed Caching**: ElastiCache for Valkey improves read performance.
- **Resilient Message Handling**: SQS DLQ for failed messages, with CloudWatch alerts for investigation.
- **Infrastructure as Code:** Full AWS deployment automation with Terraform.
- **Local Testing:** JUnit 5, Mockito, AssertJ, LocalStack, and TestContainers ensure robust local development.
- **CI/CD Pipeline:** GitHub Actions for continuous integration and deployment.
- **Secure, Scalable Deployment:** Services run on ECS Fargate (private subnets) with ALB for load balancing.

## Project Architecture

![Cards Project Architecture](architecture/cards-architecture.png)