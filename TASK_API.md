# Task Creation API (US008)

## Overview
This document describes the Task Creation API implemented for User Story 008 (US008).

## Endpoints

### 1. Create Task
Create a new task in a project.

**Endpoint:** `POST /api/tasks`

**Headers:**
- `X-User-Id`: The ID of the user creating the task (Long)
- `Content-Type`: application/json

**Request Body:**
```json
{
  "title": "Task Title",              // Required
  "description": "Task Description",   // Optional
  "priority": "high",                  // Optional (default: "medium")
  "projectId": 1,                      // Required
  "assigneeId": 2,                     // Optional
  "dueDate": "2026-02-10T10:00:00"    // Optional (ISO 8601 format)
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "title": "Task Title",
  "description": "Task Description",
  "priority": "high",
  "status": "todo",
  "projectId": 1,
  "creatorId": 1,
  "assigneeId": 2,
  "dueDate": "2026-02-10T10:00:00",
  "createdAt": "2026-02-04T10:00:00",
  "updatedAt": "2026-02-04T10:00:00"
}
```

**Error Responses:**
- `400 Bad Request` - Missing required fields (title or projectId)
- `403 Forbidden` - User is not a member of the project
- `404 Not Found` - Project not found

### 2. Get Task by ID
Retrieve a task's details by its ID.

**Endpoint:** `GET /api/tasks/{taskId}`

**Path Parameters:**
- `taskId`: The ID of the task (Long)

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Task Title",
  "description": "Task Description",
  "priority": "high",
  "status": "todo",
  "projectId": 1,
  "creatorId": 1,
  "assigneeId": 2,
  "dueDate": "2026-02-10T10:00:00",
  "createdAt": "2026-02-04T10:00:00",
  "updatedAt": "2026-02-04T10:00:00"
}
```

**Error Responses:**
- `404 Not Found` - Task not found

## Field Descriptions

### Task Fields
- `id`: Auto-generated task ID
- `title`: **Required** - Task title (max 200 chars)
- `description`: Optional - Detailed task description
- `priority`: Optional - Priority level: "low", "medium", "high" (default: "medium")
- `status`: Auto-set to "todo" on creation
- `projectId`: **Required** - ID of the project this task belongs to
- `creatorId`: Auto-set from X-User-Id header
- `assigneeId`: Optional - ID of user assigned to this task
- `dueDate`: Optional - Task due date in ISO 8601 format
- `createdAt`: Auto-generated creation timestamp
- `updatedAt`: Auto-updated modification timestamp

## Business Rules

1. **Required Fields**: Only `title` and `projectId` are required
2. **Default Status**: New tasks are created with status "todo" (AC3)
3. **Creator Assignment**: Creator is automatically set from the authenticated user (AC4)
4. **Permission Check**: Only project members can create tasks (AC5)
5. **Default Priority**: If not specified, priority defaults to "medium"

## Example Usage

### Using cURL

#### Create a Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "title": "Implement user authentication",
    "description": "Add JWT-based authentication to the API",
    "priority": "high",
    "projectId": 1,
    "assigneeId": 2,
    "dueDate": "2026-02-10T17:00:00"
  }'
```

#### Create a Minimal Task (Required Fields Only)
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "title": "Review pull request",
    "projectId": 1
  }'
```

#### Get Task by ID
```bash
curl http://localhost:8080/api/tasks/1
```

## Database Schema

### Tasks Table
```sql
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    priority VARCHAR(20) DEFAULT 'medium',
    status VARCHAR(20) DEFAULT 'todo',
    project_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    assignee_id BIGINT,
    due_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (creator_id) REFERENCES users(id),
    FOREIGN KEY (assignee_id) REFERENCES users(id)
);
```

## Testing

### Running Tests
```bash
# Run all unit tests
mvn test -Dtest=TaskTest,TaskServiceTest,TaskControllerTest

# Run specific test
mvn test -Dtest=TaskControllerTest
```

### Test Coverage
- **Entity Tests**: Task entity getters/setters
- **Service Tests**: Business logic, permission checks, error handling
- **Controller Tests**: HTTP endpoints, request/response validation
- **Integration Tests**: End-to-end workflows with database

## Acceptance Criteria Status

- ✅ **AC1**: Task creation endpoint available (POST /api/tasks)
- ✅ **AC2**: Required field (title) and optional fields (description, priority, assignee, due date) implemented
- ✅ **AC3**: Created tasks appear in "todo" status
- ✅ **AC4**: Creator automatically becomes the task creator
- ✅ **AC5**: Non-project members cannot create tasks

## Notes

1. **Authentication**: Currently using X-User-Id header for user identification. In production, this should be replaced with proper JWT or session-based authentication.

2. **Project Membership**: The API validates that the user is a member of the project before allowing task creation.

3. **Default Values**: 
   - Status defaults to "todo"
   - Priority defaults to "medium" if not specified
   - Timestamps are automatically managed by the database

4. **Error Handling**: The API provides meaningful error messages for validation failures and business rule violations.
