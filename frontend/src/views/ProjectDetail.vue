<template>
  <div class="project-detail-view">
    <el-page-header @back="goBack" content="Project Detail" />
    
    <el-tabs v-model="activeTab" class="demo-tabs" style="margin-top: 20px;">
      <el-tab-pane label="Overview" name="overview">
        <el-card>
          <h2>{{ project.name }}</h2>
          <p>{{ project.description }}</p>
          <p><strong>Owner ID:</strong> {{ project.ownerId }}</p>
        </el-card>
      </el-tab-pane>
      
      <!-- Member Management Tab -->
      <el-tab-pane label="Members" name="members">
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
import { useRouter } from 'vue-router';
import MemberManage from '../components/MemberManage.vue';

const router = useRouter();

// Mock project data - in real app, this would come from API
const projectId = ref(1);
const currentUserId = ref(1); // Mock current logged-in user
const project = ref({
  id: 1,
  name: 'TeamTodo Project',
  description: 'A collaborative todo management system',
  ownerId: 1,
});

const activeTab = ref('overview');

const isOwner = computed(() => {
  return project.value.ownerId === currentUserId.value;
});

const goBack = () => {
  router.push('/');
};

onMounted(() => {
  // In a real app, load project data from API
  // const id = route.params.id;
  // loadProject(id);
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
