# TeamTodo Frontend

TeamTodo前端应用 - 基于Vue 3 + TypeScript + Vite构建

## 技术栈

- Vue 3
- TypeScript
- Vite
- Element Plus (UI组件库)
- Pinia (状态管理)
- Vue Router 4 (路由管理)

## 环境要求

- Node.js 16+
- npm 7+

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

应用将在 http://localhost:5173 启动

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产构建

```bash
npm run preview
```

## 项目结构

```
frontend/
├── src/
│   ├── router/          # Vue Router配置
│   ├── stores/          # Pinia状态管理
│   ├── views/           # 页面组件
│   ├── components/      # 可复用组件
│   ├── assets/          # 静态资源
│   ├── App.vue          # 根组件
│   └── main.ts          # 应用入口
├── public/              # 公共静态资源
├── index.html           # HTML模板
├── vite.config.ts       # Vite配置
└── package.json         # 依赖配置
```

## 功能验证

- ✅ Vite开发服务器正常启动
- ✅ Element Plus组件正常使用
- ✅ 路由跳转正常工作
- ✅ Pinia状态管理正常工作

## IDE推荐

推荐使用 [VS Code](https://code.visualstudio.com/) + [Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar) 扩展。
