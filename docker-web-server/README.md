# 🐳 Web Server using Docker

> **Code Alpha Internship Task** — A fully containerized, production-ready Nginx web server featuring an interactive DevOps dashboard with live container simulation, CLI cheatsheet, and lifecycle guides.

![Docker](https://img.shields.io/badge/Docker-29.x-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-1.25_Alpine-009639?style=for-the-badge&logo=nginx&logoColor=white)
![Status](https://img.shields.io/badge/Status-Running-10B981?style=for-the-badge)

---

## 📋 Table of Contents
- [Features](#-features)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Docker Commands Reference](#-docker-commands-reference)
- [Container Lifecycle](#-container-lifecycle)
- [Health Monitoring](#-health-monitoring)
- [Troubleshooting](#-troubleshooting)
- [Best Practices Applied](#-best-practices-applied)

---

## ✨ Features
- 🖥️ **Interactive Dashboard** — Simulate Docker container states (build, run, pause, stop) in the browser
- 📊 **Live Metrics** — Animated CPU, RAM, and health indicators
- 📋 **CLI Cheatsheet** — 18 essential Docker commands with one-click copy
- 🔄 **Lifecycle Guide** — Visual explanation of all container states
- 🏥 **Health Monitoring** — Built-in `/health` endpoint polled by Docker's native HEALTHCHECK
- 🗜️ **Gzip Compression** — Nginx compresses responses for faster load times
- 🔒 **Security Headers** — X-Frame-Options, X-Content-Type-Options, XSS Protection headers applied

---

## 📁 Project Structure

```
docker-web-server/
│
├── 📄 Dockerfile              # Multi-stage build using Nginx Alpine
├── 📄 docker-compose.yml      # Service definition with health checks & resource limits
├── 📄 nginx.conf              # Custom Nginx server configuration
│
└── 📂 src/                    # Static website source files
    ├── 📄 index.html          # Interactive DevOps Dashboard UI
    ├── 🎨 style.css           # Premium dark-mode glassmorphism styles
    └── ⚡ app.js              # Container simulation & cheatsheet logic
```

---

## 🛠️ Prerequisites

| Tool | Version | Install |
|------|---------|---------|
| Docker Desktop | 24.x+ | [docs.docker.com](https://docs.docker.com/get-docker/) |
| Docker Compose | V2 (bundled) | Included in Docker Desktop |

Verify installation:
```bash
docker --version
docker compose version
```

---

## 🚀 Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/code-alpha-internship.git
cd code-alpha-internship/docker-web-server

# 2. Build and start all services in detached mode
docker-compose up --build -d

# 3. Open the web server in your browser
# 👉 http://localhost:8080
```

### Option 2: Using Docker CLI

```bash
# Build the image
docker build -t devops-webserver:latest .

# Run the container
docker run -d -p 8080:80 --name devops-webserver devops-webserver:latest

# Open: http://localhost:8080
```

---

## 🐳 Docker Commands Reference

### Container Lifecycle

| Command | Description |
|---------|-------------|
| `docker build -t devops-webserver .` | Build image from Dockerfile |
| `docker run -d -p 8080:80 --name devops-webserver devops-webserver` | Start container in background |
| `docker stop devops-webserver` | Gracefully stop the container (SIGTERM) |
| `docker start devops-webserver` | Restart a stopped container |
| `docker pause devops-webserver` | Freeze all container processes |
| `docker unpause devops-webserver` | Resume a paused container |
| `docker rm devops-webserver` | Remove a stopped container |
| `docker rmi devops-webserver:latest` | Delete the Docker image |

### Monitoring & Management

```bash
# List all running containers
docker ps

# List all containers (including stopped)
docker ps -a

# View live resource usage (CPU, memory, network)
docker stats devops-webserver

# View detailed container metadata as JSON
docker inspect devops-webserver

# Stream container logs in real-time
docker logs -f devops-webserver

# Open a shell inside the running container
docker exec -it devops-webserver /bin/sh
```

---

## 🔄 Container Lifecycle

```
  ┌──────────┐  docker build   ┌──────────┐  docker run    ┌───────────┐
  │   Image  │ ───────────────▶│ Created  │──────────────▶│  Running  │
  └──────────┘                 └──────────┘                └─────┬─────┘
                                                                  │
                           docker stop ◀──────────── docker pause │
                                 │                         ┌──────▼─────┐
                          ┌──────▼─────┐                   │   Paused   │
                          │  Stopped   │                   └────────────┘
                          └──────┬─────┘
                                 │  docker rm
                          ┌──────▼─────┐
                          │  Removed   │
                          └────────────┘
```

---

## 🏥 Health Monitoring

This project configures Docker's native **HEALTHCHECK** to poll the Nginx server every 30 seconds.

**Dockerfile Health Check:**
```dockerfile
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl --fail http://localhost/health || exit 1
```

**Check container health status:**
```bash
# Quick status check
docker ps  # Shows "healthy" or "unhealthy" in STATUS column

# Detailed health log (last 5 checks)
docker inspect --format='{{json .State.Health}}' devops-webserver | python -m json.tool
```

**Health States:**
| Status | Meaning |
|--------|---------|
| `starting` | Container just launched, within `start_period` |
| `healthy` | All recent health checks passed ✅ |
| `unhealthy` | 3 or more consecutive checks failed ❌ |

---

## 🔧 Troubleshooting

**Container won't start / port conflict:**
```bash
# Check if port 8080 is already in use
netstat -ano | findstr :8080

# Use a different host port
docker run -d -p 9090:80 --name devops-webserver devops-webserver:latest
```

**View Nginx error logs:**
```bash
docker exec devops-webserver cat /var/log/nginx/error.log
```

**Clean up everything and rebuild:**
```bash
docker-compose down --rmi all --volumes
docker-compose up --build -d
```

---

## ✅ Best Practices Applied

| Practice | Implementation |
|----------|---------------|
| **Minimal Base Image** | `nginx:1.25-alpine` (~25MB vs ~180MB for Ubuntu) |
| **Health Check** | Native `HEALTHCHECK` in Dockerfile + Compose |
| **Restart Policy** | `restart: unless-stopped` in docker-compose.yml |
| **Resource Limits** | CPU cap 0.5, memory cap 128MB via `deploy.resources` |
| **Structured Logging** | JSON logging driver with size/file rotation |
| **Gzip Compression** | Enabled in nginx.conf for CSS, JS, JSON, text |
| **Security Headers** | X-Frame-Options, X-Content-Type-Options, XSS Protection |
| **Non-default Config** | Custom `nginx.conf` overrides Nginx defaults |

---

> 🎓 Built as part of **Code Alpha Internship — DevOps Track**, demonstrating container lifecycle management, health monitoring, and best practices.
