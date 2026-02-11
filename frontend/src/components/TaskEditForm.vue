<template>
  <div class="task-edit-form">
    <el-form 
      ref="formRef" 
      :model="formData" 
      :rules="rules" 
      label-width="120px"
    >
      <!-- AC2: Editable field - Title -->
      <el-form-item label="Title" prop="title">
        <el-input 
          v-model="formData.title" 
          placeholder="Enter task title"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <!-- AC2: Editable field - Description -->
      <el-form-item label="Description" prop="description">
        <el-input 
          v-model="formData.description" 
          type="textarea"
          :rows="4"
          placeholder="Enter task description"
        />
      </el-form-item>

      <!-- AC2: Editable field - Priority -->
      <el-form-item label="Priority" prop="priority">
        <el-select v-model="formData.priority" placeholder="Select priority">
          <el-option label="Low" value="LOW" />
          <el-option label="Medium" value="MEDIUM" />
          <el-option label="High" value="HIGH" />
        </el-select>
      </el-form-item>

      <!-- AC2: Editable field - Deadline -->
      <el-form-item label="Deadline" prop="deadline">
        <el-date-picker
          v-model="formData.deadline"
          type="datetime"
          placeholder="Select deadline"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:ss"
        />
      </el-form-item>

      <!-- AC5: Display last modified time -->
      <el-form-item label="Last Modified" v-if="task?.updatedAt">
        <el-text type="info">{{ formatDate(task.updatedAt) }}</el-text>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          Save Changes
        </el-button>
        <el-button @click="handleCancel">
          Cancel
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { taskApi, type Task } from '../api/task';

interface Props {
  task: Task;
  userId: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'updated', task: Task): void;
  (e: 'cancel'): void;
}>();

const formRef = ref<FormInstance>();
const loading = ref(false);

const formData = reactive({
  title: props.task.title,
  description: props.task.description || '',
  priority: props.task.priority,
  deadline: props.task.deadline || null,
});

// Watch for task changes to update form
watch(() => props.task, (newTask) => {
  formData.title = newTask.title;
  formData.description = newTask.description || '';
  formData.priority = newTask.priority;
  formData.deadline = newTask.deadline || null;
}, { deep: true });

const rules: FormRules = {
  title: [
    { required: true, message: 'Please enter task title', trigger: 'blur' },
    { max: 200, message: 'Title cannot exceed 200 characters', trigger: 'blur' },
  ],
  priority: [
    { required: true, message: 'Please select priority', trigger: 'change' },
  ],
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // AC3: Save changes
        const updatedTask = await taskApi.updateTask(
          props.task.id,
          {
            title: formData.title,
            description: formData.description,
            priority: formData.priority,
            deadline: formData.deadline || undefined,
          },
          props.userId
        );
        
        ElMessage.success('Task updated successfully');
        // AC3: Emit updated event to refresh the view immediately
        emit('updated', updatedTask);
      } catch (error: any) {
        // AC4: Permission check error handling
        if (error.response?.status === 403) {
          ElMessage.error('You do not have permission to edit this task');
        } else if (error.response?.status === 404) {
          ElMessage.error('Task not found');
        } else {
          ElMessage.error('Failed to update task: ' + (error.message || 'Unknown error'));
        }
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleCancel = () => {
  emit('cancel');
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString();
};
</script>

<style scoped>
.task-edit-form {
  padding: 20px;
}

.el-form {
  max-width: 600px;
}
</style>
