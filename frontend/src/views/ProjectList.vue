<template>
  <div class="project-list-view">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>项目列表</span>
          <el-button type="primary" @click="showCreateDialog = true">+ 新建项目</el-button>
        </div>
      </template>

      <el-table :data="projects" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="项目名称" min-width="160">
          <template #default="{ row }">
            <el-link type="primary" @click="goToProject(row.id)">{{ row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="goToProject(row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && projects.length === 0" description="暂无项目，点击「新建项目」创建" />
    </el-card>

    <!-- Create Project Dialog (AC1) -->
    <el-dialog v-model="showCreateDialog" title="新建项目" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <!-- AC4: name is required -->
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入项目名称" maxlength="100" show-word-limit />
        </el-form-item>
        <!-- AC4: description is optional -->
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入项目描述（选填）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage } from 'element-plus';
import { projectApi, type ProjectResponse } from '../api/project';

const router = useRouter();

// Mock current user ID - in a real app this comes from auth store
const currentUserId = ref(1);

const projects = ref<ProjectResponse[]>([]);
const loading = ref(false);
const showCreateDialog = ref(false);
const creating = ref(false);
const formRef = ref<FormInstance>();

const form = reactive({
  name: '',
  description: '',
});

// AC4: project name is required
const rules: FormRules = {
  name: [
    { required: true, message: '项目名称不能为空', trigger: 'blur' },
    { min: 1, max: 100, message: '项目名称长度在 1 到 100 个字符', trigger: 'blur' },
  ],
};

const loadProjects = async () => {
  loading.value = true;
  try {
    projects.value = await projectApi.listProjects();
  } catch (error) {
    ElMessage.error('加载项目列表失败');
  } finally {
    loading.value = false;
  }
};

// AC1: Admin fills in name and description, then creates
const handleCreate = async () => {
  if (!formRef.value) return;
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  creating.value = true;
  try {
    const created = await projectApi.createProject({
      name: form.name,
      description: form.description || undefined,
      ownerId: currentUserId.value,
    });
    ElMessage.success('项目创建成功');
    closeDialog();
    // AC2: Redirect to project detail page after creation
    router.push(`/project/${created.id}`);
  } catch (error: any) {
    const msg = error?.response?.data?.error || '创建项目失败';
    ElMessage.error(msg);
  } finally {
    creating.value = false;
  }
};

const closeDialog = () => {
  showCreateDialog.value = false;
  formRef.value?.resetFields();
};

const goToProject = (id: number) => {
  router.push(`/project/${id}`);
};

onMounted(() => {
  loadProjects();
});
</script>

<style scoped>
.project-list-view {
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
  font-size: 18px;
  font-weight: bold;
}
</style>
