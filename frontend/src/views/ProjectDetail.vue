<template>
  <div class="project-detail-view">
    <el-page-header @back="goBack" :content="project?.name || '项目详情'" />

    <div v-if="loading" style="margin-top: 40px; text-align: center;">
      <el-skeleton :rows="5" animated />
    </div>

    <el-tabs v-else v-model="activeTab" class="demo-tabs" style="margin-top: 20px;">
      <el-tab-pane label="概览" name="overview">
        <el-card>
          <h2>{{ project?.name }}</h2>
          <p>{{ project?.description || '（无描述）' }}</p>
          <p><strong>Owner ID:</strong> {{ project?.ownerId }}</p>
          <p><strong>创建时间:</strong> {{ project?.createdAt }}</p>
        </el-card>
      </el-tab-pane>

      <!-- Member Management Tab -->
      <el-tab-pane label="成员管理" name="members">
        <MemberManage
          :project-id="projectId"
          :current-user-id="currentUserId"
          :is-owner="isOwner"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import MemberManage from '../components/MemberManage.vue';
import { projectApi, type ProjectResponse } from '../api/project';

const router = useRouter();
const route = useRoute();

const projectId = computed(() => Number(route.params.id) || 1);
const currentUserId = ref(1); // Mock current logged-in user
const project = ref<ProjectResponse | null>(null);
const loading = ref(false);

const activeTab = ref('overview');

const isOwner = computed(() => {
  return project.value?.ownerId === currentUserId.value;
});

const goBack = () => {
  router.push('/projects');
};

const loadProject = async () => {
  loading.value = true;
  try {
    project.value = await projectApi.getProject(projectId.value);
  } catch (error) {
    ElMessage.error('加载项目详情失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadProject();
});
</script>

<style scoped>
.project-detail-view {
  padding: 20px;
}

.demo-tabs {
  margin-top: 20px;
}
</style>
