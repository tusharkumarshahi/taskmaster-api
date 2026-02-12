# Kubernetes Setup Guide (Minikube)

This guide helps you deploy the Taskmaster API to Minikube for learning Backstage + Kubernetes.

## Prerequisites

- Minikube installed
- kubectl installed
- Docker installed

## Step 1: Start Minikube

```bash
# Start Minikube
minikube start

# Enable ingress addon (for external access)
minikube addons enable ingress

# Verify Minikube is running
kubectl cluster-info
```

## Step 2: Build Docker Image for Minikube

**Important:** Build the image inside Minikube's Docker daemon so it's available to K8s.

```bash
# Point your Docker CLI to Minikube's Docker daemon
eval $(minikube docker-env)

# Build the Docker image (multi-stage build - builds JAR inside Docker)
docker build -t taskmaster-api:latest .

# Verify image is built
docker images | grep taskmaster-api
```

**Note:** If you open a new terminal, run `eval $(minikube docker-env)` again.

## Step 3: Deploy to Kubernetes

```bash
# Create namespace
kubectl apply -f k8s/namespace.yml

# Deploy the application
kubectl apply -f k8s/deployment.yml

# Create service
kubectl apply -f k8s/service.yml

# Create ingress
kubectl apply -f k8s/ingress.yml
```

## Step 4: Verify Deployment

```bash
# Check if pods are running
kubectl get pods -n taskmaster

# Check service
kubectl get svc -n taskmaster

# Check ingress
kubectl get ingress -n taskmaster

# View logs
kubectl logs -n taskmaster -l app=taskmaster-api

# Describe pod (if issues)
kubectl describe pod -n taskmaster -l app=taskmaster-api
```

## Step 5: Access the Application

### Option 1: Using Ingress (Recommended)

```bash
# Get Minikube IP
minikube ip

# Add to /etc/hosts (replace <MINIKUBE_IP> with actual IP)
echo "<MINIKUBE_IP> taskmaster-api.local" | sudo tee -a /etc/hosts

# Access the app
curl http://taskmaster-api.local/actuator/health
```

### Option 2: Using Port Forward (Quick Test)

```bash
# Forward port 8080 from pod to localhost:8080
kubectl port-forward -n taskmaster svc/taskmaster-api 8080:8080

# In another terminal, test
curl http://localhost:8080/actuator/health
```

### Option 3: Using Minikube Tunnel (For LoadBalancer)

```bash
# In a separate terminal, keep this running
minikube tunnel

# Access via service external IP
kubectl get svc -n taskmaster
```

## Step 6: Backstage Integration

The `catalog-info.yaml` already has Kubernetes annotations:

```yaml
backstage.io/kubernetes-id: taskmaster-api
backstage.io/kubernetes-namespace: taskmaster
```

**In Backstage:**
1. Import your repository into Backstage catalog
2. Go to the component page
3. Click "Kubernetes" tab
4. You should see your pods, deployments, and services

## Troubleshooting

### Pods not starting?

```bash
# Check pod events
kubectl describe pod -n taskmaster -l app=taskmaster-api

# Check logs
kubectl logs -n taskmaster -l app=taskmaster-api

# Common issues:
# - ImagePullBackOff: Image not found (rebuild with eval $(minikube docker-env))
# - CrashLoopBackOff: App crashing (check logs)
```

### Image not found (ImagePullBackOff)?

```bash
# Make sure you built in Minikube's Docker context
eval $(minikube docker-env)
docker build -t taskmaster-api:latest .

# Update deployment
kubectl rollout restart deployment/taskmaster-api -n taskmaster
```

### Ingress not working?

```bash
# Check if ingress addon is enabled
minikube addons list | grep ingress

# Enable if not
minikube addons enable ingress

# Check ingress controller
kubectl get pods -n ingress-nginx
```

### Health check failing?

The deployment expects Spring Boot Actuator endpoints:
- `/actuator/health/liveness`
- `/actuator/health/readiness`

Make sure your application has `spring-boot-starter-actuator` dependency.

## Quick Commands Reference

```bash
# Rebuild and redeploy
eval $(minikube docker-env)
docker build -t taskmaster-api:latest .
kubectl rollout restart deployment/taskmaster-api -n taskmaster

# Watch pods
kubectl get pods -n taskmaster -w

# Follow logs
kubectl logs -n taskmaster -l app=taskmaster-api -f

# Delete everything
kubectl delete namespace taskmaster

# Stop Minikube
minikube stop

# Delete Minikube cluster
minikube delete
```

## Simplifications Made

This setup is simplified for learning:
- ✅ Multi-stage Dockerfile (no need to build JAR separately)
- ✅ Local development (no external registry)
- ✅ Simple namespace structure
- ✅ Basic health checks
- ✅ Ingress for external access

## Next Steps for Learning

1. **Scale the deployment:**
   ```bash
   kubectl scale deployment/taskmaster-api -n taskmaster --replicas=3
   ```

2. **Update the image:**
   Make code changes, rebuild, and rollout:
   ```bash
   eval $(minikube docker-env)
   docker build -t taskmaster-api:v2 .
   kubectl set image deployment/taskmaster-api -n taskmaster taskmaster-api=taskmaster-api:v2
   ```

3. **Add ConfigMaps/Secrets:**
   Create ConfigMap for application.properties

4. **Monitor in Backstage:**
   See real-time pod status, logs, and resources in Backstage UI

5. **Add more K8s resources:**
   - HorizontalPodAutoscaler
   - PersistentVolumeClaim (if you add database)
   - NetworkPolicy
