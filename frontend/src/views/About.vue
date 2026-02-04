<template>
  <div class="about">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>关于 TeamTodo</span>
        </div>
      </template>
      <div class="content">
        <el-descriptions title="项目信息" :column="1" border>
          <el-descriptions-item label="项目名称">TeamTodo</el-descriptions-item>
          <el-descriptions-item label="前端技术栈">Vue 3 + TypeScript + Vite</el-descriptions-item>
          <el-descriptions-item label="UI 组件库">Element Plus</el-descriptions-item>
          <el-descriptions-item label="状态管理">Pinia</el-descriptions-item>
          <el-descriptions-item label="路由">Vue Router 4</el-descriptions-item>
        </el-descriptions>
        
        <div class="back-button">
          <el-button type="primary" @click="goBack">返回首页</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.about {
  padding: 20px;
}

.box-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.content {
  padding: 20px;
}

.back-button {
  margin-top: 30px;
  text-align: center;
}
</style>
