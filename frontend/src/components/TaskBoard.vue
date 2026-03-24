<template>
  <div class="task-board">
    <el-row :gutter="16">
      <el-col
        v-for="column in columns"
        :key="column.status"
        :span="8"
      >
        <div class="task-board__column">
          <div class="task-board__column-header">
            <el-tag :type="column.tagType" size="large">
              {{ column.label }}
            </el-tag>
            <el-badge :value="column.tasks.length" class="task-count" />
          </div>

          <div class="task-board__column-body">
            <TaskCard
              v-for="task in column.tasks"
              :key="task.id"
              :task="task"
              :current-user-id="currentUserId"
              @status-changed="handleStatusChanged"
            />
            <el-empty
              v-if="column.tasks.length === 0"
              :image-size="60"
              description="暂无任务"
            />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import TaskCard from './TaskCard.vue';
import {
  taskApi,
  STATUS_LABELS,
  STATUS_TAG_TYPES,
  type TaskResponse,
  type TaskStatus,
} from '../api/task';

interface Props {
  projectId: number;
  currentUserId: number;
}

const props = defineProps<Props>();

const tasks = ref<TaskResponse[]>([]);

const COLUMN_ORDER: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];

/** AC3: Kanban columns grouped by status */
const columns = computed(() =>
  COLUMN_ORDER.map((status) => ({
    status,
    label: STATUS_LABELS[status],
    tagType: STATUS_TAG_TYPES[status],
    tasks: tasks.value.filter((t) => t.status === status),
  })),
);

/** AC3: Refresh a single task's entry after status change */
function handleStatusChanged(updated: TaskResponse) {
  const idx = tasks.value.findIndex((t) => t.id === updated.id);
  if (idx !== -1) {
    tasks.value[idx] = updated;
  }
}

async function loadTasks() {
  try {
    tasks.value = await taskApi.getTasksByProject(props.projectId);
  } catch {
    ElMessage.error('加载任务失败，请稍后重试');
  }
}

onMounted(loadTasks);
</script>

<style scoped>
.task-board {
  padding: 8px 0;
}

.task-board__column {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 12px;
  min-height: 300px;
}

.task-board__column-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.task-count {
  margin-left: 4px;
}

.task-board__column-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
