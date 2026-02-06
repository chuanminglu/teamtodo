<template>
  <div class="member-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>Member Management</span>
          <el-button
            v-if="!showUserSearch"
            type="primary"
            @click="showUserSearch = true"
          >
            Add Member
          </el-button>
        </div>
      </template>

      <!-- User Search Component (AC1) -->
      <UserSearch
        v-if="showUserSearch"
        :project-id="projectId"
        :existing-member-ids="existingMemberIds"
        @invite-user="handleInviteUser"
      />

      <!-- Member List (AC3) -->
      <el-table :data="members" stripe style="width: 100%">
        <el-table-column prop="username" label="Username" width="180" />
        <el-table-column prop="email" label="Email" width="250" />
        <el-table-column prop="role" label="Role" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'OWNER' ? 'danger' : 'primary'">
              {{ scope.row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinedAt" label="Joined At" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.joinedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="Actions" align="right">
          <template #default="scope">
            <!-- AC4: Owner can remove members but not themselves -->
            <el-button
              v-if="canRemoveMember(scope.row)"
              size="small"
              type="danger"
              @click="handleRemoveMember(scope.row)"
            >
              Remove
            </el-button>
            <el-text v-else type="info" size="small">
              {{ scope.row.userId === currentUserId ? 'You' : 'No Action' }}
            </el-text>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="members.length === 0" description="No members yet" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import UserSearch from './UserSearch.vue';
import { memberApi, type MemberResponse } from '../api/member';
import type { User } from './UserSearch.vue';

interface Props {
  projectId: number;
  currentUserId: number;
  isOwner: boolean;
}

const props = defineProps<Props>();

const members = ref<MemberResponse[]>([]);
const showUserSearch = ref(false);
const loading = ref(false);

const existingMemberIds = computed(() => members.value.map((m) => m.userId));

/**
 * AC3: Load and display project members
 */
const loadMembers = async () => {
  loading.value = true;
  try {
    members.value = await memberApi.getProjectMembers(props.projectId);
  } catch (error) {
    console.error('Error loading members:', error);
    ElMessage.error('Failed to load members');
  } finally {
    loading.value = false;
  }
};

/**
 * AC1 & AC2: Invite a user to the project
 */
const handleInviteUser = async (user: User) => {
  try {
    await memberApi.addMember({
      projectId: props.projectId,
      userId: user.id,
      role: 'MEMBER',
    });

    ElMessage.success(`${user.username} has been added to the project`);
    showUserSearch.value = false;
    
    // Reload members list
    await loadMembers();
  } catch (error: any) {
    console.error('Error adding member:', error);
    const errorMessage = error.response?.data?.error || 'Failed to add member';
    ElMessage.error(errorMessage);
  }
};

/**
 * AC4: Check if current user can remove a member
 * - Must be project owner
 * - Cannot remove yourself
 */
const canRemoveMember = (member: MemberResponse) => {
  return props.isOwner && member.userId !== props.currentUserId;
};

/**
 * AC4: Remove a member from the project
 */
const handleRemoveMember = async (member: MemberResponse) => {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to remove ${member.username} from this project?`,
      'Warning',
      {
        confirmButtonText: 'Yes',
        cancelButtonText: 'No',
        type: 'warning',
      }
    );

    await memberApi.removeMember(props.projectId, member.id, props.currentUserId);
    
    ElMessage.success(`${member.username} has been removed from the project`);
    
    // Reload members list
    await loadMembers();
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('Error removing member:', error);
      const errorMessage = error.response?.data?.error || 'Failed to remove member';
      ElMessage.error(errorMessage);
    }
  }
};

/**
 * Format date for display
 */
const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};

onMounted(() => {
  loadMembers();
});
</script>

<style scoped>
.member-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.box-card {
  margin-bottom: 20px;
}
</style>
