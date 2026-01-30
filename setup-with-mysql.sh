#!/bin/bash
# Database Setup and Connection Test Script

echo "=== TeamTodo Database Setup and Test ==="
echo ""

# Check if MySQL is running
echo "1. Checking MySQL status..."
if command -v mysql &> /dev/null; then
    echo "   ✓ MySQL client found"
else
    echo "   ✗ MySQL client not found. Please install MySQL first."
    exit 1
fi

# Test MySQL connection
echo ""
echo "2. Testing MySQL connection..."
echo "   Please enter MySQL root password when prompted"
if mysql -u root -p -e "SELECT VERSION();" &> /dev/null; then
    echo "   ✓ MySQL connection successful"
else
    echo "   ✗ Could not connect to MySQL. Please check your MySQL installation."
    exit 1
fi

# Create database and tables
echo ""
echo "3. Creating database and tables..."
mysql -u root -p < src/main/resources/db/schema.sql
if [ $? -eq 0 ]; then
    echo "   ✓ Database and tables created successfully"
else
    echo "   ✗ Failed to create database and tables"
    exit 1
fi

# Verify database creation
echo ""
echo "4. Verifying database..."
mysql -u root -p -e "USE teamtodo; SHOW TABLES;"
echo ""

# Build the application
echo "5. Building application..."
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    echo "   ✓ Application built successfully"
else
    echo "   ✗ Build failed"
    exit 1
fi

# Run the application
echo ""
echo "6. Starting application with MySQL connection..."
echo "   Application will start with default profile (MySQL enabled)"
echo "   Press Ctrl+C to stop the application"
echo ""
java -jar target/teamtodo-backend-1.0.0-SNAPSHOT.jar
