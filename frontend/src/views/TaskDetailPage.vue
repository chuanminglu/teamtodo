<template>
  <div class="task-detail-page">
    <el-page-header @back="goBack" title="Back to Home">
      <template #content>
        <span class="text-large font-600 mr-3">Task Detail Demo (US010)</span>
      </template>
    </el-page-header>

    <div v-if="demoTask" style="margin-top: 20px;">
      <TaskDetail
        :task="demoTask"
        :current-user-id="currentUserId"
        @task-updated="handleTaskUpdated"
      />
    </div>

    <div v-else class="loading">
      <el-text>Loading demo task...</el-text>
    </div>

    <!-- Demo Instructions -->
    <el-card class="demo-info" style="margin-top: 20px; max-width: 900px; margin-left: auto; margin-right: auto;">
      <template #header>
        <span>Demo Instructions (US010 验收标准)</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="AC1">
          ✅ Click "Edit Task" button to enter edit mode
        </el-descriptions-item>
        <el-descriptions-item label="AC2">
          ✅ Editable fields: Title, Description, Priority, Deadline
        </el-descriptions-item>
        <el-descriptions-item label="AC3">
          ✅ Task details update immediately after saving
        </el-descriptions-item>
        <el-descriptions-item label="AC4">
          ✅ Only creator or assignee can edit (Current user ID: {{ currentUserId }}, Creator ID: {{ demoTask?.creatorId }})
        </el-descriptions-item>
        <el-descriptions-item label="AC5">
          ✅ Last modified time is displayed in both view and edit mode
        </el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <div>
        <p><strong>Test Scenarios:</strong></p>
        <ul>
          <li>
            <el-button size="small" @click="switchUser(100)">
              Switch to Creator (ID: 100)
            </el-button>
            - Can edit the task
          </li>
          <li>
            <el-button size="small" @click="switchUser(200)">
              Switch to Assignee (ID: 200)
            </el-button>
            - Can edit the task
          </li>
          <li>
            <el-button size="small" @click="switchUser(999)">
              Switch to Other User (ID: 999)
            </el-button>
            - Cannot edit (view only)
          </li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import TaskDetail from '../components/TaskDetail.vue';
import type { Task } from '../api/task';

const router = useRouter();

// Demo task data
const demoTask = ref<Task>({
  id: 1,
  projectId: 1,
  title: 'Implement User Authentication',
  description: 'Add JWT-based authentication to the backend API',
  priority: 'HIGH',
  deadline: '2026-03-15T18:00:00',
  creatorId: 100,
  assigneeId: 200,
  createdAt: '2026-02-01T10:00:00',
  updatedAt: '2026-02-10T15:30:00',
});

// Current user ID for demo (default: creator)
const currentUserId = ref(100);

const goBack = () => {
  router.push('/');
};

// AC3: Handle task update - refresh immediately
const handleTaskUpdated = (updatedTask: Task) => {
  demoTask.value = updatedTask;
};

// Switch user for testing AC4
const switchUser = (userId: number) => {
  currentUserId.value = userId;
};
</script>

<style scoped>
.task-detail-page {
  padding: 20px;
}

.loading {
  text-align: center;
  padding: 50px;
}

.demo-info {
  margin-top: 20px;
}

.demo-info ul {
  padding-left: 20px;
}

.demo-info li {
  margin: 10px 0;
}
</style>
