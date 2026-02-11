<template>
  <div class="task-detail">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>Task Details</span>
          <!-- AC1: Click to enter edit mode (only if user has permission) -->
          <el-button
            v-if="!isEditMode && canEdit"
            type="primary"
            @click="enterEditMode"
          >
            Edit Task
          </el-button>
        </div>
      </template>

      <!-- View Mode: Display task information -->
      <div v-if="!isEditMode && task" class="task-view">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="Title">
            {{ task.title }}
          </el-descriptions-item>
          <el-descriptions-item label="Description">
            {{ task.description || 'No description' }}
          </el-descriptions-item>
          <el-descriptions-item label="Priority">
            <el-tag :type="getPriorityType(task.priority)">
              {{ task.priority }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Deadline">
            {{ task.deadline ? formatDate(task.deadline) : 'No deadline' }}
          </el-descriptions-item>
          <el-descriptions-item label="Creator ID">
            {{ task.creatorId }}
          </el-descriptions-item>
          <el-descriptions-item label="Assignee ID">
            {{ task.assigneeId || 'Unassigned' }}
          </el-descriptions-item>
          <!-- AC5: Show last modified time -->
          <el-descriptions-item label="Last Modified">
            {{ formatDate(task.updatedAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="Created At">
            {{ formatDate(task.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- AC4: Permission indicator -->
        <el-alert
          v-if="!canEdit"
          title="You can only view this task"
          type="info"
          :closable="false"
          style="margin-top: 20px"
        >
          Only the task creator or assignee can edit this task.
        </el-alert>
      </div>

      <!-- Edit Mode: Task edit form -->
      <!-- AC1: Enter edit mode -->
      <div v-if="isEditMode && task">
        <TaskEditForm
          :task="task"
          :user-id="currentUserId"
          @updated="handleTaskUpdated"
          @cancel="exitEditMode"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import TaskEditForm from '../components/TaskEditForm.vue';
import type { Task } from '../api/task';

interface Props {
  task: Task;
  currentUserId: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'taskUpdated', task: Task): void;
}>();

const isEditMode = ref(false);

// AC4: Check if current user can edit (creator or assignee)
const canEdit = computed(() => {
  if (!props.task) return false;
  return (
    props.task.creatorId === props.currentUserId ||
    props.task.assigneeId === props.currentUserId
  );
});

const enterEditMode = () => {
  if (canEdit.value) {
    isEditMode.value = true;
  }
};

const exitEditMode = () => {
  isEditMode.value = false;
};

// AC3: Handle task update - immediately update the view
const handleTaskUpdated = (updatedTask: Task) => {
  isEditMode.value = false;
  emit('taskUpdated', updatedTask);
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString();
};

const getPriorityType = (priority: string) => {
  switch (priority) {
    case 'HIGH':
      return 'danger';
    case 'MEDIUM':
      return 'warning';
    case 'LOW':
      return 'success';
    default:
      return 'info';
  }
};
</script>

<style scoped>
.task-detail {
  padding: 20px;
}

.box-card {
  max-width: 900px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-view {
  padding: 10px 0;
}
</style>
