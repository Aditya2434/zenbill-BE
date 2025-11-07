#!/bin/bash
set -e

echo "================================"
echo "Starting Full-Stack Build"
echo "================================"

# Clone the frontend repository
echo "Cloning frontend repository..."
git clone -b ${FRONTEND_BRANCH:-main} ${FRONTEND_REPO_URL:-https://github.com/Aditya2434/ZenBill-FE.git} frontend-temp

# Build the frontend
echo "Building frontend..."
cd frontend-temp
npm install
npm run build

# Copy frontend build to backend static resources
echo "Copying frontend build to backend static folder..."
cd ..
mkdir -p src/main/resources/static
cp -r frontend-temp/dist/* src/main/resources/static/

# Clean up frontend temp folder
echo "Cleaning up temporary frontend files..."
rm -rf frontend-temp

# Build the backend
echo "Building backend with Maven..."
mvn clean package -DskipTests

echo "================================"
echo "Build completed successfully!"
echo "================================"

