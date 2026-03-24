import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface TaskResponse {
  id: number;
  projectId: number;
  title: string;
  description: string;
  status: TaskStatus;
  creatorId: number;
  assigneeId: number | null;
  assigneeUsername: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateTaskStatusRequest {
  requestUserId: number;
  status: TaskStatus;
}

/**
 * API service for task operations (US015)
 */
export const taskApi = {
  /**
   * Get all tasks for a project (AC3: board refresh)
   */
  async getTasksByProject(projectId: number): Promise<TaskResponse[]> {
    const response = await apiClient.get(`/tasks/project/${projectId}`);
    return response.data;
  },

  /**
   * T015-01: Update task status (AC2, AC3, AC4, AC5)
   */
  async updateTaskStatus(taskId: number, request: UpdateTaskStatusRequest): Promise<TaskResponse> {
    const response = await apiClient.patch(`/tasks/${taskId}/status`, request);
    return response.data;
  },
};

/** Human-readable labels for task statuses */
export const STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: '待办',
  IN_PROGRESS: '进行中',
  DONE: '已完成',
};

/** Element Plus tag types for status badges */
export const STATUS_TAG_TYPES: Record<TaskStatus, string> = {
  TODO: 'info',
  IN_PROGRESS: 'warning',
  DONE: 'success',
};
