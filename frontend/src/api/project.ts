import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface CreateProjectRequest {
  name: string;
  description?: string;
  ownerId: number;
}

export interface ProjectResponse {
  id: number;
  name: string;
  description?: string;
  ownerId: number;
  createdAt: string;
  updatedAt: string;
}

/**
 * API service for project operations
 */
export const projectApi = {
  /**
   * T004-02: Create a new project
   */
  async createProject(request: CreateProjectRequest): Promise<ProjectResponse> {
    const response = await apiClient.post('/projects', request);
    return response.data;
  },

  /**
   * T004-03: Get list of all projects (sorted by createdAt desc)
   */
  async listProjects(): Promise<ProjectResponse[]> {
    const response = await apiClient.get('/projects');
    return response.data;
  },

  /**
   * T004-04: Get project detail by ID
   */
  async getProject(id: number): Promise<ProjectResponse> {
    const response = await apiClient.get(`/projects/${id}`);
    return response.data;
  },
};
