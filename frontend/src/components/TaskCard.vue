<template>
  <el-card class="task-card" shadow="hover">
    <div class="task-card__header">
      <span class="task-card__title">{{ task.title }}</span>
      <!-- AC1: Status change button in top-right corner of task card -->
      <el-dropdown
        trigger="click"
        :disabled="!canChangeStatus"
        @command="handleStatusChange"
      >
        <el-tag
          :type="currentTagType"
          class="task-card__status-tag"
          :class="{ 'is-clickable': canChangeStatus }"
        >
          {{ currentStatusLabel }}
          <el-icon v-if="canChangeStatus" class="el-icon--right"><ArrowDown /></el-icon>
        </el-tag>
        <!-- AC2: Dropdown with 待办 / 进行中 / 已完成 -->
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="option in statusOptions"
              :key="option.value"
              :command="option.value"
              :class="{ 'is-active': option.value === task.status }"
            >
              <el-tag :type="option.tagType" size="small">{{ option.label }}</el-tag>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <p v-if="task.description" class="task-card__description">{{ task.description }}</p>

    <div class="task-card__footer">
      <span v-if="task.assigneeUsername" class="task-card__assignee">
        <el-icon><User /></el-icon>
        {{ task.assigneeUsername }}
      </span>
      <span v-else class="task-card__assignee task-card__assignee--none">未指派</span>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ElMessage } from 'element-plus';
import { ArrowDown, User } from '@element-plus/icons-vue';
import {
  taskApi,
  STATUS_LABELS,
  STATUS_TAG_TYPES,
  type TaskResponse,
  type TaskStatus,
} from '../api/task';

interface Props {
  task: TaskResponse;
  /** ID of the currently logged-in user */
  currentUserId: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  /** AC3: emitted when status has been updated so parent can refresh the board */
  (e: 'status-changed', updated: TaskResponse): void;
}>();

/** AC4: only creator or assignee may change status */
const canChangeStatus = computed(
  () =>
    props.currentUserId === props.task.creatorId ||
    props.currentUserId === props.task.assigneeId,
);

const currentStatusLabel = computed(() => STATUS_LABELS[props.task.status]);
const currentTagType = computed(() => STATUS_TAG_TYPES[props.task.status]);

const statusOptions = computed(() =>
  (Object.keys(STATUS_LABELS) as TaskStatus[]).map((value) => ({
    value,
    label: STATUS_LABELS[value],
    tagType: STATUS_TAG_TYPES[value],
  })),
);

/** AC2/AC3/AC5: send status update to API then notify parent and show toast */
async function handleStatusChange(newStatus: TaskStatus) {
  if (newStatus === props.task.status) return;

  try {
    const updated = await taskApi.updateTaskStatus(props.task.id, {
      requestUserId: props.currentUserId,
      status: newStatus,
    });
    // AC5: Toast notification
    ElMessage.success(`任务状态已更新为「${STATUS_LABELS[newStatus]}」`);
    // AC3: Notify parent to refresh the board
    emit('status-changed', updated);
  } catch (err: unknown) {
    const msg =
      err instanceof Error
        ? err.message
        : '状态更新失败，请稍后重试';
    ElMessage.error(msg);
  }
}
</script>

<style scoped>
.task-card {
  margin-bottom: 8px;
}

.task-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.task-card__title {
  font-weight: 600;
  font-size: 14px;
  flex: 1;
  line-height: 1.4;
}

.task-card__status-tag.is-clickable {
  cursor: pointer;
}

.task-card__description {
  font-size: 12px;
  color: #606266;
  margin: 6px 0 4px;
  line-height: 1.4;
}

.task-card__footer {
  display: flex;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.task-card__assignee {
  display: flex;
  align-items: center;
  gap: 4px;
}

.task-card__assignee--none {
  font-style: italic;
}
</style>
