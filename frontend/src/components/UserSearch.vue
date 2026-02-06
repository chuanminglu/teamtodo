<template>
  <div class="user-search">
    <el-form @submit.prevent="handleSearch" :inline="true">
      <el-form-item label="Search User">
        <el-input
          v-model="searchQuery"
          placeholder="Search by username or email"
          clearable
          @clear="handleClear"
        >
          <template #append>
            <el-button @click="handleSearch" type="primary">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </el-form-item>
    </el-form>

    <el-table
      v-if="searchResults.length > 0"
      :data="searchResults"
      stripe
      style="width: 100%; margin-top: 20px"
    >
      <el-table-column prop="username" label="Username" width="180" />
      <el-table-column prop="email" label="Email" width="250" />
      <el-table-column label="Actions" align="right">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            @click="handleInvite(scope.row)"
            :disabled="scope.row.isMember"
          >
            {{ scope.row.isMember ? 'Already Member' : 'Invite' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty
      v-else-if="hasSearched"
      description="No users found"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

export interface User {
  id: number;
  username: string;
  email: string;
  isMember?: boolean;
}

interface Props {
  projectId: number;
  existingMemberIds?: number[];
}

const props = defineProps<Props>();

const emit = defineEmits<{
  'invite-user': [user: User];
}>();

const searchQuery = ref('');
const searchResults = ref<User[]>([]);
const hasSearched = ref(false);

/**
 * AC1: Search users for invitation
 * In a real implementation, this would call a user search API
 * For now, this is a mock implementation
 */
const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('Please enter a search term');
    return;
  }

  try {
    // TODO: Replace with actual API call to search users
    // For demonstration, we'll use mock data
    const mockUsers: User[] = [
      { id: 2, username: 'john_doe', email: 'john@example.com' },
      { id: 3, username: 'jane_smith', email: 'jane@example.com' },
      { id: 4, username: 'bob_johnson', email: 'bob@example.com' },
    ];

    // Filter based on search query
    const filtered = mockUsers.filter(
      (user) =>
        user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        user.email.toLowerCase().includes(searchQuery.value.toLowerCase())
    );

    // Mark users who are already members
    searchResults.value = filtered.map((user) => ({
      ...user,
      isMember: props.existingMemberIds?.includes(user.id) || false,
    }));

    hasSearched.value = true;

    if (searchResults.value.length === 0) {
      ElMessage.info('No users found matching your search');
    }
  } catch (error) {
    console.error('Error searching users:', error);
    ElMessage.error('Failed to search users');
  }
};

const handleClear = () => {
  searchResults.value = [];
  hasSearched.value = false;
};

const handleInvite = (user: User) => {
  emit('invite-user', user);
};
</script>

<style scoped>
.user-search {
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
}
</style>
