# SmartServe: A Real-Time Complaint and Service Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-MongoDB%20Community%20Server-green.svg)](https://www.mongodb.com/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-Jenkins%20Pipeline-blue.svg)](https://www.jenkins.io/)
[![Container](https://img.shields.io/badge/Container-Docker-2496ED.svg)](https://www.docker.com/)
[![Orchestration](https://img.shields.io/badge/Orchestration-Kubernetes-326CE5.svg)](https://kubernetes.io/)

> **Academic Context:** Complete Computer Science & Engineering (CSE) DevOps Case Study Project integrating all five required syllabus technologies: **Git & GitHub**, **Apache Maven**, **Jenkins**, **Docker**, and **Kubernetes** with **Spring Boot 3** and **MongoDB**.

---

## 1. Project Abstract & Objectives

**SmartServe** is an enterprise-grade civic grievance and municipal service management platform designed for smart cities. Citizens register civic complaints across categories such as Sanitation, Water Supply, Electricity, Roads, and Public Infrastructure. Each complaint receives an automated, collision-free Tracking ID (`SMP-YYYYMMDD-XXXX`), an initial status of `SUBMITTED`, and an exact timestamp persisted in **MongoDB**. Citizens monitor real-time resolution progress and administrator remarks without requiring full system access. Authorized civic officials authenticate into a protected Administrative Dashboard to filter complaints, assign workloads, append resolution remarks, and advance complaints through lifecycle stages (`SUBMITTED` &rarr; `IN_PROGRESS` &rarr; `RESOLVED` / `REJECTED`).

### Core Objectives:
1. Deliver a responsive, full-stack Spring Boot 3 MVC web application with Thymeleaf and Bootstrap 5.
2. Use **MongoDB** as the single, authoritative persistent database (`smartserve_db.complaints`).
3. Enforce secure role-based access control and CSRF protection via **Spring Security 6**.
4. Implement a fully automated, declarative **Jenkins CI/CD pipeline** driving continuous integration, containerization, and deployment.
5. Provide high availability, self-healing, and scalability using **Kubernetes** multi-replica deployments and health probes.

---

## 2. Mandatory 5-Tier DevOps Architecture

```
Developer Workstation
        │
        ▼ (1. Git Commit & Push)
GitHub Remote Repository
        │
        ▼ (2. Webhook Trigger / Poll SCM)
Jenkins Automation Server (Port 9090)
        │
        ├─► Stage 1: Checkout SCM
        ├─► Stage 2: Maven Build & JUnit 5 Tests
        ├─► Stage 3: Package Executable Fat JAR
        ├─► Stage 4: Docker Multi-Stage Image Build (`smartserve:1.0.0`)
        ├─► Stage 5: Load Image into Kubernetes / Minikube
        ├─► Stage 6: Apply Kubernetes Manifests (`k8s/`)
        └─► Stage 7: Rollout Verification & Health Probing
        │
        ▼
Kubernetes Cluster (Minikube / MicroK8s)
        ├── SmartServe Pod Replica 1  <───┐
        ├── SmartServe Pod Replica 2  <───┼── NodePort Service (Port 30080 / 8080)
        └── MongoDB Pod + PVC (27017) <───┘   (DNS: mongodb-service)
```

---

## 3. Technology Stack & Role

| Technology | Version / Specification | Architectural Purpose |
| :--- | :--- | :--- |
| **Java** | OpenJDK 21 LTS | Core modern programming language supporting virtual threads and records |
| **Spring Boot** | 3.3.4 | Application framework with automated configuration and actuator health probes |
| **Spring Data MongoDB** | 4.3.x | Object-Document Mapping (ODM) layer interacting with MongoDB collections |
| **MongoDB Community** | 7.0.x / 6.0.x | NoSQL Document database storing complaint records (`smartserve_db`) |
| **MongoDB Compass** | Latest GUI | Visual inspection tool for collections, indexes, and document documents |
| **Thymeleaf & Bootstrap**| Thymeleaf 3, Bootstrap 5 | Server-rendered responsive UI templates with dynamic alerts and badges |
| **Apache Maven** | 3.9.x | Dependency management, compilation, automated test execution, packaging |
| **Git & GitHub** | 2.51.x | Distributed source-code version control and webhook CI triggers |
| **Jenkins** | Declarative Pipeline (Port 9090) | Automated Continuous Integration and Deployment orchestration server |
| **Docker** | 29.x Multi-stage | Lightweight alpine-based containerization and dependency isolation |
| **Kubernetes** | Minikube / K8s v1.36.x | Container orchestration, self-healing, load balancing, and scaling |

---

## 4. Project Directory Structure

```
SmartServe/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── k8s/
│   ├── configmap.yaml               # Runtime configuration (Mongo URI, Port)
│   ├── secret.yaml                  # Administrative credentials template
│   ├── mongodb-pvc.yaml             # PersistentVolumeClaim for MongoDB data
│   ├── mongodb-deployment.yaml      # Kubernetes Deployment for MongoDB 7.0
│   ├── mongodb-service.yaml         # Internal ClusterIP service for MongoDB
│   ├── deployment.yaml              # SmartServe 2-replica Deployment with probes
│   └── service.yaml                 # NodePort Service exposing SmartServe on port 30080
├── src/
│   ├── main/
│   │   ├── java/com/smartserve/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java         # Spring Security 6, BCrypt, RBAC
│   │   │   ├── controller/
│   │   │   │   ├── AdminController.java        # Protected admin dashboard & updates
│   │   │   │   ├── AdminLoginController.java   # Admin login views
│   │   │   │   ├── ComplaintController.java    # Citizen submission & tracking
│   │   │   │   ├── CustomErrorController.java  # User-friendly error handlers
│   │   │   │   └── HomeController.java         # Public landing page
│   │   │   ├── dto/
│   │   │   │   ├── ComplaintRequestDto.java    # Submission form validation rules
│   │   │   │   └── ComplaintStatusUpdateDto.java# Status update validation rules
│   │   │   ├── model/
│   │   │   │   ├── Complaint.java              # MongoDB Document (@Document)
│   │   │   │   ├── ComplaintCategory.java      # Enum: SANITATION, WATER, etc.
│   │   │   │   └── ComplaintStatus.java        # Enum: SUBMITTED, IN_PROGRESS, etc.
│   │   │   ├── repository/
│   │   │   │   └── ComplaintRepository.java    # MongoRepository queries & indexes
│   │   │   ├── service/
│   │   │   │   ├── ComplaintService.java       # Business contract interface
│   │   │   │   └── ComplaintServiceImpl.java   # Collision-free ID logic & CRUD
│   │   │   └── SmartServeApplication.java      # Spring Boot application entry point
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/styles.css              # Custom responsive stylesheet
│   │       │   └── js/main.js                  # Clipboard, auto-uppercase logic
│   │       ├── templates/
│   │       │   ├── fragments/
│   │       │   │   ├── footer.html             # Reusable footer fragment
│   │       │   │   └── navbar.html             # Reusable navbar fragment
│   │       │   ├── admin-dashboard.html        # Metrics, status filtering & list
│   │       │   ├── admin-login.html            # Admin login card
│   │       │   ├── admin-update.html           # Detail review & status form
│   │       │   ├── complaint-success.html      # Confirmation with tracking ID banner
│   │       │   ├── error.html                  # Error display template
│   │       │   ├── index.html                  # Public landing portal
│   │       │   ├── submit-complaint.html       # Citizen complaint form
│   │       │   └── track-complaint.html        # Public status tracking & timeline
│   │       └── application.properties          # Production properties & Mongo URI
│   └── test/
│       ├── java/com/smartserve/
│       │   ├── controller/
│       │   │   ├── AdminControllerSecurityTest.java# MockMvc auth enforcement
│       │   │   └── ComplaintControllerTest.java    # MockMvc submit/track tests
│       │   ├── model/
│       │   │   └── ComplaintValidationTest.java    # Jakarta validation tests
│       │   ├── repository/
│       │   │   └── ComplaintMongoIntegrationTest.java# Live MongoDB CRUD test
│       │   ├── service/
│       │   │   └── ComplaintTrackingIdAndServiceTest.java# Tracking ID regex & Mockito
│       │   └── SmartServeApplicationTests.java     # Spring Context verification
│       └── resources/
│           └── application-test.properties     # Isolated test properties
├── .dockerignore                            # Build exclusions for Docker
├── .gitignore                               # SCM exclusions for Git
├── Dockerfile                               # Multi-stage Java 21 build
├── Jenkinsfile                              # Declarative 7-stage CI/CD pipeline
├── mvnw                                     # Linux/macOS Maven wrapper script
├── mvnw.cmd                                 # Windows Maven wrapper script
├── pom.xml                                  # Maven project descriptor
└── README.md                                # Comprehensive documentation
```

---

## 5. Local Execution Guide (Windows PowerShell)

### Step 5.1: Prerequisites Check
Open **Windows PowerShell** as Administrator or regular user:
```powershell
# 1. Verify Java 21
java -version

# 2. Verify MongoDB Windows Service is listening on 27017
Test-NetConnection -ComputerName localhost -Port 27017
```

### Step 5.2: Launch Application via Maven
Navigate to the project root directory:
```powershell
cd C:\Users\madug\OneDrive\Documents\SmartServe

# Run unit tests and start the Spring Boot web application
mvn spring-boot:run
# Or use the included wrapper:
.\mvnw.cmd spring-boot:run
```

Expected startup log:
```
Tomcat started on port 8080 (http) with context path '/'
Started SmartServeApplication in 3.42 seconds
```

### Step 5.3: Access the Application in Your Browser
- **Citizen Home Portal:** `http://localhost:8080/`
- **Submit a Complaint:** `http://localhost:8080/complaints/new`
- **Track Status:** `http://localhost:8080/complaints/track`
- **Admin Portal Login:** `http://localhost:8080/admin/login`
- **Actuator Health Probe:** `http://localhost:8080/actuator/health`

### Step 5.4: Administrator Credentials
- **Username:** `admin`
- **Password:** `SmartServe@2026Admin`
*(Configurable via environment variables `SMARTSERVE_ADMIN_USER` and `SMARTSERVE_ADMIN_PASSWORD`)*

---

## 6. Inspecting MongoDB with MongoDB Compass

1. Open **MongoDB Compass**.
2. In the connection string bar, enter:
   ```
   mongodb://localhost:27017
   ```
3. Click **Connect**.
4. In the left database panel, look for **`smartserve_db`**.
5. Select the **`complaints`** collection.
6. Observe the persisted BSON document structure:
   ```json
   {
     "_id": { "$oid": "6720d1c4f5298418abf1092a" },
     "trackingId": "SMP-20261029-7B3K",
     "title": "Damaged streetlight near Community Hall",
     "category": "ELECTRICITY",
     "description": "Streetlight pole wiring damaged after heavy rain.",
     "status": "SUBMITTED",
     "submissionTime": { "$date": "2026-10-29T13:00:00.000Z" },
     "lastUpdatedTime": { "$date": "2026-10-29T13:00:00.000Z" },
     "adminRemarks": null,
     "_class": "com.smartserve.model.Complaint"
   }
   ```
7. Notice the unique index created on `trackingId`.

---

## 7. Running Automated Tests

Run the full automated test suite using Maven:
```powershell
mvn test
# Or using wrapper:
.\mvnw.cmd test
```

### What the Tests Verify:
1. **`ComplaintTrackingIdAndServiceTest`**: Validates collision-free `SMP-YYYYMMDD-XXXX` pattern, initial status assignment, status update transitions, and error handling for missing IDs.
2. **`ComplaintValidationTest`**: Enforces title size (5-100), required category, and description size (10-1000) using Hibernate Validator.
3. **`ComplaintControllerTest`**: Tests HTTP endpoints, model attributes, form error redisplay, and flash attributes via Spring MockMvc.
4. **`AdminControllerSecurityTest`**: Confirms unauthenticated access to `/admin/dashboard` is blocked with HTTP 302 redirect to `/admin/login`, while authenticated `ROLE_ADMIN` sessions are granted access.
5. **`SmartServeApplicationTests`**: Verifies that the Spring Boot ApplicationContext initializes cleanly.

To package the production JAR:
```powershell
mvn clean package
```
Output artifact: `target/smartserve-1.0.0.jar`

---

## 8. Docker Containerization

### Step 8.1: Understand MongoDB Connectivity in Docker
When Spring Boot runs inside a Docker container, `localhost:27017` refers to the **container itself**, not your Windows host!
To connect from Docker to your local Windows MongoDB service:
- Use host DNS: `host.docker.internal`
- Connection URI: `mongodb://host.docker.internal:27017/smartserve_db`

### Step 8.2: Build the Multi-Stage Docker Image
```powershell
# Build image tagged as smartserve:1.0.0 and smartserve:latest
docker build -t smartserve:1.0.0 -t smartserve:latest .
```

### Step 8.3: Run the Container
```powershell
# Run detached, map port 8080, inject MongoDB URI pointing to Windows host
docker run -d `
  --name smartserve-container `
  -p 8080:8080 `
  -e SPRING_DATA_MONGODB_URI="mongodb://host.docker.internal:27017/smartserve_db" `
  -e SMARTSERVE_ADMIN_USER="admin" `
  -e SMARTSERVE_ADMIN_PASSWORD="SmartServe@2026Admin" `
  smartserve:1.0.0
```

### Step 8.4: Inspect & Manage Container
```powershell
# View running container status
docker ps

# Stream live container logs
docker logs -f smartserve-container

# Inspect container health probe status
docker inspect --format='{{json .State.Health}}' smartserve-container

# Stop and remove container
docker stop smartserve-container
docker rm smartserve-container
```

---

## 9. Kubernetes Orchestration & Scaling

The `k8s/` folder contains production-ready manifests:
- `mongodb-pvc.yaml` & `mongodb-deployment.yaml`: Runs MongoDB 7.0 inside Kubernetes with persistent volume storage.
- `mongodb-service.yaml`: ClusterIP service establishing cluster-internal DNS `mongodb-service:27017`.
- `configmap.yaml` & `secret.yaml`: Injects application environment variables.
- `deployment.yaml`: Deploys 2 replicas of SmartServe with readiness and liveness probes.
- `service.yaml`: NodePort service exposing the application on port `30080`.

### Step 9.1: Start Minikube & Deploy
```powershell
# Start local Minikube cluster
minikube start

# Load your local Docker image into Minikube VM
minikube image load smartserve:1.0.0

# Apply all Kubernetes manifests in sequence
kubectl apply -f k8s/mongodb-pvc.yaml
kubectl apply -f k8s/mongodb-service.yaml
kubectl apply -f k8s/mongodb-deployment.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

### Step 9.2: Verify Deployments, Pods, and Services
```powershell
# Check Pod status
kubectl get pods -o wide

# Check Deployment status (should show 2/2 ready)
kubectl get deployments

# Check Services and NodePort assignment
kubectl get services
```

### Step 9.3: Access the Kubernetes Application
In Minikube on Windows:
```powershell
# Open browser automatically to the NodePort service
minikube service smartserve-service

# Or start a direct port-forward to localhost:
kubectl port-forward service/smartserve-service 8080:8080
```

### Step 9.4: Demonstrate Self-Healing & Scaling (College Viva Demo)
```powershell
# Demonstrate Pod replacement (Self-Healing):
$podName = (kubectl get pods -l app=smartserve -o jsonpath='{.items[0].metadata.name}')
kubectl delete pod $podName

# Watch Kubernetes immediately spin up a new replacement Pod to maintain 2 replicas:
kubectl get pods -w

# Demonstrate Horizontal Scaling from 2 to 4 replicas:
kubectl scale deployment/smartserve-deployment --replicas=4
kubectl get pods
```

---

## 10. Jenkins CI/CD Pipeline Configuration

Jenkins runs at `http://localhost:9090` (preserving your existing college lab port).

### Pipeline Stages Breakdown:
1. **Checkout SCM:** Fetches source code from Git repository.
2. **Maven Build & Test:** Runs `mvn clean test`, records JUnit XML reports.
3. **Package Executable JAR:** Compiles the fat JAR (`target/smartserve-1.0.0.jar`) and archives build artifacts.
4. **Docker Build & Tag:** Multi-stage Docker build tagged with `${BUILD_NUMBER}` and `latest`.
5. **Provision Image to Kubernetes:** Executes `minikube image load smartserve:${BUILD_NUMBER}`.
6. **Kubernetes Deployment:** Applies manifests in `k8s/` and updates the deployment image tag.
7. **Rollout Verification:** Probes rollout status with `kubectl rollout status deployment/smartserve-deployment` to verify pod health.

### How to Create the Job in Jenkins:
1. Open `http://localhost:9090` in your browser.
2. Click **New Item** &rarr; Select **Pipeline** &rarr; Name it `SmartServe-Pipeline`.
3. In **Pipeline Definition**, select **Pipeline script from SCM**.
4. SCM: **Git** &rarr; Repository URL: `https://github.com/<your-username>/SmartServe.git`.
5. Branch Specifier: `*/main`.
6. Script Path: `Jenkinsfile`.
7. Click **Save** &rarr; **Build Now**.

---

## 11. Git & GitHub Workflow Guide

### Step 11.1: Local Repository Initialization
Execute in Windows PowerShell from the `SmartServe` project folder:
```powershell
cd C:\Users\madug\OneDrive\Documents\SmartServe

# Initialize local git repository
git init

# Check untracked files
git status

# Stage all files
git add .

# Create initial commit
git commit -m "feat: complete SmartServe complaint management system with 5-tier DevOps pipeline"
```

### Step 11.2: Connect to GitHub
1. Go to [GitHub.com](https://github.com) &rarr; Click **New Repository**.
2. Name it **`SmartServe`**, choose **Public**, and leave "Initialize with README" **unchecked**.
3. Link your local repo and push:
```powershell
# Rename branch to main
git branch -M main

# Add remote GitHub repository (replace with your actual GitHub username)
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/SmartServe.git

# Push code to GitHub
git push -u origin main
```

### Step 11.3: Feature Branch & Pull Request Workflow
```powershell
# Create and switch to a feature branch
git checkout -b feature/analytics-dashboard

# Make code enhancements, then commit
git commit -am "feat: add departmental analytics metrics"

# Push feature branch to GitHub
git push -u origin feature/analytics-dashboard

# Switch back and merge into main
git checkout main
git merge feature/analytics-dashboard
git push origin main
```

### Step 11.4: GitHub Webhook to Jenkins
1. In your GitHub repository, navigate to **Settings** &rarr; **Webhooks** &rarr; **Add webhook**.
2. **Payload URL:** `http://<your-jenkins-ip-or-ngrok>:9090/github-webhook/`
3. **Content type:** `application/json`
4. **Which events:** Just the `push` event.
5. Click **Add webhook**. Now every git push automatically initiates the Jenkins pipeline!

---

## 12. Troubleshooting Guide

| Issue | Root Cause | Solution |
| :--- | :--- | :--- |
| `Connection refused: localhost/127.0.0.1:27017` | MongoDB service is stopped | Open `services.msc`, locate `MongoDB Server`, right-click &rarr; `Start`. Or run `net start MongoDB` in admin terminal. |
| `host.docker.internal` fails in Docker container | Running older Docker or Linux host without host-gateway | In `docker run`, add `--add-host=host.docker.internal:host-gateway`. |
| Minikube NodePort `http://localhost:30080` not opening | Windows networking does not route Minikube bridge directly | Run `minikube service smartserve-service` or execute `kubectl port-forward service/smartserve-service 8080:8080`. |
| Jenkins `docker: command not found` | Jenkins service running as SYSTEM user without Docker in PATH | Grant Jenkins user permissions to Docker socket or configure Docker tool in Jenkins Global Tool Configuration. |
| Pods stuck in `CrashLoopBackOff` | Application failed readiness or cannot reach MongoDB | Run `kubectl logs <pod-name>` to inspect Spring Boot stack trace. Verify `mongodb-service` is up using `kubectl get svc`. |

---

## 13. College Viva Voce Questions & Answers

### Q1: What are the five DevOps technologies used and how are they interconnected?
> **Answer:**
> 1. **Git & GitHub** manage source code version control and trigger automated CI builds via webhooks.
> 2. **Apache Maven** handles dependency resolution, compiles Java 21 bytecode, executes JUnit 5 test suites, and packages the executable fat JAR.
> 3. **Jenkins** acts as the automated CI/CD orchestrator via a declarative `Jenkinsfile`, executing tests, building Docker containers, and managing Kubernetes rollouts.
> 4. **Docker** packages the Spring Boot application and its dependencies into a reproducible, lightweight Alpine Linux container image.
> 5. **Kubernetes** orchestrates container deployment, ensuring high availability with 2 replicas, automated self-healing, rolling updates, and health monitoring.

### Q2: Why did you choose MongoDB instead of a relational database like MySQL?
> **Answer:**
> Municipal complaints often involve evolving attributes (differing civic categories, dynamic administrative remark threads, departmental routing, timestamps). MongoDB's document model provides schema flexibility, high-performance indexing on `trackingId`, and seamless integration with Spring Data MongoDB.

### Q3: How does Kubernetes achieve self-healing in this project?
> **Answer:**
> The `k8s/deployment.yaml` specifies `replicas: 2`. The Kubernetes Controller Manager continuously monitors the actual state against the desired state. If a Pod crashes or is manually terminated (`kubectl delete pod`), the ReplicaSet immediately schedules and deploys a replacement Pod to maintain desired availability without downtime.

### Q4: What is the purpose of Spring Boot Actuator in Docker and Kubernetes?
> **Answer:**
> Actuator exposes the `/actuator/health` endpoint, including `/actuator/health/liveness` and `/actuator/health/readiness`. Kubernetes uses readiness probes to prevent routing citizen traffic to a Pod until it has fully initialized its MongoDB connections. Liveness probes detect stalled or deadlocked containers and automatically restart them.

### Q5: How do Docker containers reach MongoDB running on the Windows host?
> **Answer:**
> Inside a Docker container, `localhost` refers to the container's isolated network namespace, not the host machine. On Windows, Docker Desktop provides a special internal DNS hostname, `host.docker.internal`, which maps to the host's internal IP address, allowing the container to connect to port 27017 of the Windows MongoDB service.

---

## 14. College Lab Demonstration Screenshot Checklist

Capture these screenshots to present an outstanding lab record:
- [ ] **Screenshot 1:** MongoDB Compass displaying `smartserve_db` database and `complaints` collection with documents.
- [ ] **Screenshot 2:** SmartServe Landing Page (`/`) with DevOps technology badges.
- [ ] **Screenshot 3:** Complaint Submission Form (`/complaints/new`) with category selection and validation.
- [ ] **Screenshot 4:** Complaint Success Page (`/complaints/success`) displaying unique Tracking ID.
- [ ] **Screenshot 5:** Tracking Page (`/complaints/track`) displaying progress stepper and admin remarks.
- [ ] **Screenshot 6:** Admin Login (`/admin/login`) and Admin Dashboard (`/admin/dashboard`) showing metrics and filters.
- [ ] **Screenshot 7:** PowerShell terminal showing successful `mvn test` execution (all 5 test suites passing).
- [ ] **Screenshot 8:** Docker terminal showing `docker images` and `docker ps` with running `smartserve-container`.
- [ ] **Screenshot 9:** Kubernetes terminal showing `kubectl get pods` (2/2 Running), `kubectl get deployments`, and `kubectl get services`.
- [ ] **Screenshot 10:** Jenkins Pipeline Stage View showing all 7 stages completed with green checkmarks.
