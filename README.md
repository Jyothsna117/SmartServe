# SmartServe: A Real-Time Complaint and Service Management System

## Project Overview

SmartServe is a web-based complaint and service management system. It allows users to submit complaints, receive a unique tracking ID, and track the status of their complaints. Administrators can view submitted complaints and update their status through an admin dashboard.

## Technologies Used

- Java 21
- Spring Boot
- Maven
- Thymeleaf
- HTML, CSS, Bootstrap, and JavaScript
- MongoDB
- Git and GitHub
- Jenkins
- Docker
- Kubernetes

## Features

- Submit complaints through a web form.
- Generate a unique complaint tracking ID.
- Track complaint status.
- Manage complaints through an admin dashboard.
- Store complaint information in MongoDB.
- Build and deploy the application using DevOps tools.

## DevOps Workflow

GitHub stores the source code. Jenkins automates the build process using Maven. Docker packages the application into a container image, and Kubernetes manages the deployment.

## Database

SmartServe uses MongoDB to store complaint details and status information.

## Running the Application

Make sure MongoDB is running, then start the application using:

```bash
mvn spring-boot:run
```

Open the application using the localhost port configured in `application.properties`.

## Project Purpose

The purpose of SmartServe is to simplify complaint submission, tracking, and management while demonstrating the use of DevOps tools in application development and deployment.
