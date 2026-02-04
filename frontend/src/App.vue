<template>
  <div id="app">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>TeamTodo</h1>
          <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            class="el-menu-demo"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/about">关于</el-menu-item>
          </el-menu>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeMenu = ref(route.path)

watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})

const handleMenuSelect = (index: string) => {
  router.push(index)
}
</script>

<style scoped>
#app {
  min-height: 100vh;
}

.el-header {
  background-color: #409eff;
  color: #fff;
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  margin-right: 50px;
  font-size: 24px;
}

.el-menu-demo {
  background-color: transparent;
  border-bottom: none;
}

.el-menu--horizontal .el-menu-item {
  color: #fff;
  border-bottom: 2px solid transparent;
}

.el-menu--horizontal .el-menu-item:hover,
.el-menu--horizontal .el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.1);
  border-bottom-color: #fff;
  color: #fff;
}

.el-main {
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
}
</style>
