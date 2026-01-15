# 🐳 Docker + JFrog Setup Guide

## Overview
This guide sets up Docker image builds and pushes to JFrog Artifactory (instead of Docker Hub).

---

## 1️⃣ Create Docker Repository in JFrog

### Step 1: Login to JFrog
- URL: `https://[your-server-name].jfrog.io`
- Use your JFrog credentials

### Step 2: Create Local Docker Repository
1. Navigate to: **Administration** → **Repositories** → **Repositories**
2. Click **Add Repositories** → **Local Repository**
3. Select **Docker** package type
4. Configure:
   - **Repository Key:** `taskmaster-docker-local`
   - **Docker API Version:** V2 (default)
   - Leave other settings as default
5. Click **Create Local Repository**

### Step 3: Create Virtual Docker Repository (Recommended)
1. Click **Add Repositories** → **Virtual Repository**
2. Select **Docker** package type
3. Configure:
   - **Repository Key:** `taskmaster-docker`
   - **Repositories:** Select `taskmaster-docker-local`
   - **Default Deployment Repository:** `taskmaster-docker-local`
4. Click **Create Virtual Repository**

---

## 2️⃣ Add GitHub Secret

Add one more secret to your GitHub repository:

### Navigate to GitHub:
`Settings` → `Secrets and variables` → `Actions` → `New repository secret`

### Add Secret:
- **Name:** `JFROG_DOCKER_REGISTRY`
- **Value:** `[your-server-name].jfrog.io/taskmaster-docker`
  - Example: `tusharkshahi.jfrog.io/taskmaster-docker`

**Note:** Use the virtual repository name (`taskmaster-docker`) for better flexibility.

---

## 3️⃣ Workflow Behavior

### What Happens on Every Push:

```
1. Build JAR
2. Run Tests  
3. Deploy JAR to JFrog (taskmaster-maven-local)
4. Build Docker Image (from JAR)
5. Push Docker Image to JFrog (taskmaster-docker)
```

### Docker Images Created:

Two tags are pushed for each build:
- `taskmaster:0.0.1-SNAPSHOT` (version-specific)
- `taskmaster:latest` (always points to latest build)

---

## 4️⃣ Using Your Docker Images

### Pull from JFrog

```bash
# Login to JFrog Docker Registry
echo "YOUR_JFROG_TOKEN" | docker login [your-server].jfrog.io/taskmaster-docker \
  -u YOUR_EMAIL \
  --password-stdin

# Pull specific version
docker pull [your-server].jfrog.io/taskmaster-docker/taskmaster:0.0.1-SNAPSHOT

# Pull latest
docker pull [your-server].jfrog.io/taskmaster-docker/taskmaster:latest
```

### Run Locally

```bash
# Run with port mapping
docker run -p 8080:8080 [your-server].jfrog.io/taskmaster-docker/taskmaster:latest

# Run with environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  [your-server].jfrog.io/taskmaster-docker/taskmaster:latest

# Run in detached mode
docker run -d -p 8080:8080 --name taskmaster-api \
  [your-server].jfrog.io/taskmaster-docker/taskmaster:latest
```

### View in JFrog UI

1. Login to JFrog Artifactory
2. Navigate to: **Artifacts** → **taskmaster-docker** → **taskmaster**
3. You'll see:
   - `0.0.1-SNAPSHOT/` folder
   - `latest/` folder
   - Each contains Docker image layers (manifests, blobs)

---

## 5️⃣ JFrog vs Docker Hub

| Feature | JFrog Artifactory | Docker Hub |
|---------|-------------------|------------|
| **Unified Storage** | ✅ JARs + Docker images | ❌ Separate systems |
| **Private by Default** | ✅ Yes | ❌ Need paid plan |
| **Security Scanning** | ✅ Xray (paid) | ✅ Snyk integration |
| **Free Storage** | 2GB total | 1 private repo |
| **Rate Limits** | Based on plan | 200 pulls/6hrs (free) |
| **Enterprise Features** | ✅ Promotion, RBAC | ❌ Limited |
| **Global CDN** | ❌ Single region | ✅ Worldwide |
| **Best For** | Private enterprise apps | Public open source |

---

## 6️⃣ Advanced: Using in Kubernetes

### Create Docker Registry Secret

```bash
kubectl create secret docker-registry jfrog-docker \
  --docker-server=[your-server].jfrog.io/taskmaster-docker \
  --docker-username=YOUR_EMAIL \
  --docker-password=YOUR_TOKEN \
  --docker-email=YOUR_EMAIL
```

### Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: taskmaster-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: taskmaster
  template:
    metadata:
      labels:
        app: taskmaster
    spec:
      imagePullSecrets:
        - name: jfrog-docker
      containers:
      - name: taskmaster
        image: [your-server].jfrog.io/taskmaster-docker/taskmaster:0.0.1-SNAPSHOT
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

---

## 7️⃣ Image Size Optimization

Current setup uses **Alpine Linux** base image, which is ~40MB smaller than standard images:
- `eclipse-temurin:21-jre` → ~270MB
- `eclipse-temurin:21-jre-alpine` → ~180MB ✅

Your final image will be: **~230MB** (180MB base + 51MB JAR)

---

## 8️⃣ Troubleshooting

### Issue: Docker login fails
```bash
# Solution: Use access token, not password
echo "YOUR_ACCESS_TOKEN" | docker login [server].jfrog.io/taskmaster-docker \
  -u YOUR_EMAIL --password-stdin
```

### Issue: Cannot push image
```bash
# Verify repository exists in JFrog
# Verify JFROG_DOCKER_REGISTRY secret is correct
# Format: [server].jfrog.io/taskmaster-docker (no https://)
```

### Issue: Image too large
```bash
# Check image size
docker images | grep taskmaster

# Optimize by removing build dependencies
# Use multi-stage builds (already implemented)
```

---

## 9️⃣ Next Steps

After this works:
1. ✅ **Set up remote repositories** in JFrog for Docker Hub (cache base images)
2. ✅ **Enable Xray scanning** for security vulnerabilities
3. ✅ **Create promotion pipeline:** dev → staging → prod
4. ✅ **Set up retention policies** (keep last 10 builds, delete old snapshots)
5. ✅ **Deploy to Kubernetes/AWS/Azure**

---

## 🎯 Quick Command Reference

```bash
# Build locally
docker build -t taskmaster:dev .

# Test locally
docker run -p 8080:8080 taskmaster:dev

# Check logs
docker logs -f taskmaster-api

# Stop container
docker stop taskmaster-api && docker rm taskmaster-api

# Clean up old images
docker image prune -a
```
