import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface Task {
  id: number;
  projectId: number;
  title: string;
  description?: string;
  priority: string;
  deadline?: string;
  creatorId: number;
  assigneeId?: number;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateTaskRequest {
  title?: string;
  description?: string;
  priority?: string;
  deadline?: string;
}

/**
 * API service for task operations
 * US010: Task Edit Feature
 */
export const taskApi = {
  /**
   * Update task information
   * AC2: Editable fields - title, description, priority, deadline
   * AC4: Only creator or assignee can edit
   * 
   * @param taskId Task ID
   * @param request Update request with fields to update
   * @param userId User requesting the update
   * @returns Updated task
   */
  async updateTask(taskId: number, request: UpdateTaskRequest, userId: number): Promise<Task> {
    const response = await apiClient.put(`/tasks/${taskId}`, request, {
      params: { userId },
    });
    return response.data;
  },
};
