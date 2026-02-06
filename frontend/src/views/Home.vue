<template>
  <div class="home">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>欢迎使用 TeamTodo</span>
        </div>
      </template>
      <div class="demo-section">
        <h2>Element Plus 组件测试</h2>
        <el-button type="primary" @click="handleClick">主要按钮</el-button>
        <el-button type="success">成功按钮</el-button>
        <el-button type="info">信息按钮</el-button>
        <el-button type="warning">警告按钮</el-button>
        <el-button type="danger">危险按钮</el-button>
      </div>
      
      <el-divider />
      
      <div class="demo-section">
        <h2>Pinia 状态管理测试</h2>
        <p>计数器值: {{ count }}</p>
        <el-button-group>
          <el-button @click="decrement">-</el-button>
          <el-button @click="increment">+</el-button>
        </el-button-group>
      </div>
      
      <el-divider />
      
      <div class="demo-section">
        <h2>Vue Router 路由测试</h2>
        <el-button type="primary" @click="goToAbout">前往关于页面</el-button>
      </div>

      <el-divider />
      
      <div class="demo-section">
        <h2>项目成员管理功能 (US005)</h2>
        <p>测试项目成员的添加、查看、移除功能</p>
        <el-button type="primary" @click="goToProject">查看项目详情与成员管理</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useCounterStore } from '../stores/counter'
import { storeToRefs } from 'pinia'

const router = useRouter()
const counterStore = useCounterStore()
const { count } = storeToRefs(counterStore)
const { increment, decrement } = counterStore

const handleClick = () => {
  ElMessage.success('Element Plus 组件工作正常！')
}

const goToAbout = () => {
  router.push('/about')
}

const goToProject = () => {
  router.push('/project/1')
}
</script>

<style scoped>
.home {
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

.demo-section {
  margin: 20px 0;
}

.demo-section h2 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #409eff;
}

.demo-section p {
  margin: 10px 0;
  font-size: 16px;
}
</style>
