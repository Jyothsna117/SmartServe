package com.smartserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SmartServe: A Real-Time Complaint and Service Management System.
 *
 * College DevOps Case Study Application integrating:
 * 1. Git and GitHub (Version control)
 * 2. Apache Maven (Build and dependency management)
 * 3. Jenkins (Declarative CI/CD pipeline)
 * 4. Docker (Containerization)
 * 5. Kubernetes (Container orchestration and scaling)
 *
 * Backend Stack: Java 21, Spring Boot 3.3.4, Spring MVC, Thymeleaf, Spring Data MongoDB.
 */
@SpringBootApplication
public class SmartServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartServeApplication.class, args);
    }
}
