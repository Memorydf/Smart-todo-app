# 智能待办小程序 (Smart Todo App)

基于 UniApp + Spring Boot + DeepSeek AI 构建的智能待办事项管理应用。

## 功能特性

- 📋 **待办管理**：添加、编辑、删除待办事项，支持分类和优先级
- 🤖 **AI 助手**：基于 DeepSeek-V4-Flash 的智能待办生成
- 📊 **统计分析**：待办完成情况统计与可视化
- ⚙️ **设置中心**：个性化配置与数据管理

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | UniApp、Vue 3、Pinia、SCSS |
| 后端 | Spring Boot 3、MyBatis-Plus、SQLite |
| AI | DeepSeek-V4-Flash API / Ollama (可选) |
| 构建 | Vite、Maven |

## 快速开始

### 环境要求

- Node.js >= 18.x
- Java >= 17
- Maven >= 3.8

### 1. 配置后端

```bash
cd backend
# 复制配置文件并填入你的 DeepSeek API Key
cp src/main/resources/application.yml.example src/main/resources/application.yml
# 编辑 application.yml，设置 deepseek.api-key
```

### 2. 配置前端

```bash
# 复制配置文件
cp src/utils/config.js.example src/utils/config.js
# 根据需要修改 API 地址
```

### 3. 安装依赖

```bash
# 前端依赖
npm install

# 后端依赖（Maven 自动下载）
cd backend
mvn clean compile
```

### 4. 启动服务

```bash
# 启动后端（端口 8083）
cd backend
mvn spring-boot:run

# 启动前端 H5（端口 5173）
npm run dev:h5

# 或构建微信小程序
npm run build:mp-weixin
```

### 5. 访问应用

- H5 地址：http://localhost:5173
- 后端 API：http://localhost:8083

## 项目结构

```
├── src/                    # 前端源码
│   ├── api/               # API 请求封装
│   ├── components/        # 通用组件
│   ├── pages/             # 页面组件
│   ├── store/             # Pinia 状态管理
│   ├── utils/             # 工具函数
│   └── static/            # 静态资源
├── backend/               # 后端服务
│   ├── src/main/java/com/todo/
│   │   ├── controller/    # REST API 控制层
│   │   ├── service/       # 业务逻辑层
│   │   ├── mapper/        # 数据访问层
│   │   ├── entity/        # 数据库实体
│   │   └── config/        # 配置类
│   └── src/main/resources/
│       └── db/            # 数据库初始化脚本
├── dist/                  # 构建产物
├── package.json           # 前端依赖配置
└── pom.xml               # 后端依赖配置
```

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/todo/list` | GET | 获取待办列表 |
| `/todo/add` | POST | 添加待办 |
| `/todo/update` | PUT | 更新待办 |
| `/todo/delete/{id}` | DELETE | 删除待办 |
| `/ai/chat` | POST | AI 对话 |
| `/ai/generateTodosAndSave` | POST | AI 生成待办并保存 |
| `/statistics/overview` | GET | 统计概览 |

## 配置说明

### DeepSeek API Key

在 [DeepSeek 平台](https://platform.deepseek.com/) 获取 API Key，然后配置到 `backend/src/main/resources/application.yml`：

```yaml
deepseek:
  api-key: YOUR_DEEPSEEK_API_KEY_HERE
  model: deepseek-v4-flash
```

### 使用 Ollama（可选）

如果不想使用 DeepSeek API，可以配置本地 Ollama：

```yaml
deepseek:
  api-key: ""  # 留空则使用 Ollama
ollama:
  base-url: http://localhost:11434
  model: deepseek-r1:7b
```

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！