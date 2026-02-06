# US005 Member Management - Testing Guide

## Quick Start

This guide helps you test the member management feature locally.

## Prerequisites

1. **Java 17+** installed
2. **Node.js 16+** and npm installed
3. **MySQL 8.0+** running locally
4. **Maven** installed

## Setup Steps

### 1. Database Setup

```bash
# Login to MySQL
mysql -u root -p

# Create database and initialize schema
mysql -u root -p < src/main/resources/db/schema.sql
```

The schema will create:
- `users` table
- `projects` table
- `project_members` table

### 2. Insert Test Data

```sql
USE teamtodo;

-- Insert test users
INSERT INTO users (username, email) VALUES 
  ('alice', 'alice@example.com'),
  ('bob', 'bob@example.com'),
  ('charlie', 'charlie@example.com');

-- Insert test project
INSERT INTO projects (name, description, owner_id) VALUES 
  ('Test Project', 'A test project for member management', 1);

-- Insert project owner as member
INSERT INTO project_members (project_id, user_id, role) VALUES 
  (1, 1, 'OWNER');
```

### 3. Start Backend

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Backend will start at: http://localhost:8080/api

### 4. Start Frontend

```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

Frontend will start at: http://localhost:5173

## Testing the Features

### Option 1: Using the Web UI

1. Open browser and navigate to http://localhost:5173
2. Click "查看项目详情与成员管理" button
3. Click on "Members" tab
4. Test the following:
   - **View Members**: See the current project members
   - **Add Member**: Click "Add Member", search for users, and invite them
   - **Remove Member**: Click "Remove" button next to a member (only as owner)

### Option 2: Using API Test Script

```bash
# Make the script executable
chmod +x test-member-api.sh

# Run the API tests
./test-member-api.sh
```

### Option 3: Using cURL Commands

#### Add a Member
```bash
curl -X POST http://localhost:8080/api/projects/members \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "userId": 2,
    "role": "MEMBER"
  }'
```

#### Get Project Members
```bash
curl -X GET http://localhost:8080/api/projects/1/members
```

#### Check Membership
```bash
curl -X GET http://localhost:8080/api/projects/1/members/check?userId=2
```

#### Remove a Member
```bash
curl -X DELETE http://localhost:8080/api/projects/1/members/{memberId}?requestUserId=1
```

## Acceptance Criteria Verification

### AC1: Project admin can search and invite users
✅ Test: Click "Add Member" → Search for users → Click "Invite"

### AC2: Invited users can see new projects
✅ Test: After inviting user, check database or call GET /projects/{projectId}/members

### AC3: Member list displays username, role, and join time
✅ Test: View the Members tab and verify all columns are displayed

### AC4: Project Owner can remove members (cannot remove self)
✅ Test: 
- Try to remove another member as owner → Success
- Try to remove yourself → Button disabled/error message

### AC5: Non-members cannot access project content
✅ Test: Call GET /projects/{projectId}/members/check with non-member userId → Returns false

## Expected Behavior

### Adding Members
- ✅ Successfully adds member with valid data
- ❌ Returns error if member already exists
- ❌ Returns error if project not found
- ❌ Returns error if user not found

### Listing Members
- Shows username, email, role, and joined date
- Includes all members of the project
- Owner is identified with role "OWNER"

### Removing Members
- ✅ Owner can remove other members
- ❌ Non-owner cannot remove members
- ❌ Owner cannot remove themselves
- ❌ Returns error if member not found

## Troubleshooting

### Backend won't start
- Check MySQL is running: `mysql -u root -p`
- Verify database exists: `SHOW DATABASES;`
- Check application.yml has correct DB credentials

### Frontend build errors
- Run `npm install` again
- Check Node.js version: `node -v` (should be 16+)
- Clear cache: `rm -rf node_modules package-lock.json && npm install`

### API returns 404
- Verify backend is running on port 8080
- Check context path is /api
- Test health endpoint: `curl http://localhost:8080/api/health/ping`

### Cannot add members
- Check if member already exists in project
- Verify user ID exists in users table
- Check backend logs for detailed error messages

## Running Tests

### Backend Unit Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProjectMemberServiceTest
mvn test -Dtest=ProjectMemberControllerTest
```

Expected: 18 tests passing

### Frontend Build
```bash
cd frontend
npm run build
```

Expected: Build successful with no errors

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/projects/members | Add member to project |
| GET | /api/projects/{id}/members | Get project members |
| DELETE | /api/projects/{id}/members/{memberId} | Remove member |
| GET | /api/projects/{id}/members/check | Check membership |

## Support

For issues or questions:
1. Check backend logs
2. Check browser console for frontend errors
3. Verify database has test data
4. See IMPLEMENTATION_US005.md for detailed documentation
