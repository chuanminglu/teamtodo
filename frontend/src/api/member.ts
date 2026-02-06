import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface AddMemberRequest {
  projectId: number;
  userId: number;
  role?: string;
}

export interface MemberResponse {
  id: number;
  userId: number;
  username: string;
  email: string;
  role: string;
  joinedAt: string;
}

export interface ProjectMember {
  id: number;
  projectId: number;
  userId: number;
  role: string;
  joinedAt: string;
}

/**
 * API service for project member operations
 */
export const memberApi = {
  /**
   * Add a member to a project
   */
  async addMember(request: AddMemberRequest): Promise<ProjectMember> {
    const response = await apiClient.post('/projects/members', request);
    return response.data;
  },

  /**
   * Get list of project members
   */
  async getProjectMembers(projectId: number): Promise<MemberResponse[]> {
    const response = await apiClient.get(`/projects/${projectId}/members`);
    return response.data;
  },

  /**
   * Remove a member from a project
   */
  async removeMember(projectId: number, memberId: number, requestUserId: number): Promise<void> {
    await apiClient.delete(`/projects/${projectId}/members/${memberId}`, {
      params: { requestUserId },
    });
  },

  /**
   * Check if user is a member of project
   */
  async checkMembership(projectId: number, userId: number): Promise<boolean> {
    const response = await apiClient.get(`/projects/${projectId}/members/check`, {
      params: { userId },
    });
    return response.data.isMember;
  },
};
