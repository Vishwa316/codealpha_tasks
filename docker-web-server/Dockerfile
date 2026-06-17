# ============================================================
# Dockerfile for DevOps Web Server
# Task: Web Server using Docker - Code Alpha Internship
# ============================================================

# --- Stage 1: Builder Stage ---
# Use a tiny Alpine base to keep image size minimal
FROM nginx:1.25-alpine AS builder

# Install curl for the health check probe
RUN apk add --no-cache curl

# ============================================================
# --- Stage 2: Production Stage ---
FROM nginx:1.25-alpine

# Metadata labels following OCI image spec
LABEL maintainer="Code Alpha Internship"
LABEL description="Containerized Web Server with Nginx - DevOps Dashboard"
LABEL version="1.0.0"

# Install curl for HEALTHCHECK command
RUN apk add --no-cache curl

# Remove the default Nginx configuration
RUN rm /etc/nginx/conf.d/default.conf

# Copy our custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/webserver.conf

# Copy the static website files into the Nginx HTML root
COPY src/ /usr/share/nginx/html/

# Expose HTTP port
EXPOSE 80

# Docker Health Check: Polls the /health endpoint every 30 seconds.
# Marks container as UNHEALTHY after 3 consecutive failures.
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl --fail http://localhost/health || exit 1

# Start Nginx in the foreground (required for Docker - PID 1)
CMD ["nginx", "-g", "daemon off;"]
