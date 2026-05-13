# DeepSeek Doctor

基于 Spring Boot + Ollama 的 AI 智能医疗问诊系统。用户通过网页聊天界面提问，后端调用本地部署的大语言模型实时流式返回诊断建议，对话记录持久化至 MySQL。

---

## 功能特性

- **实时流式问答**：基于 Server-Sent Events（SSE）将 AI 回复逐字推送至前端，响应延迟低
- **医疗专属模型**：对接本地 Ollama 自定义模型 `my-doctor`，专注医疗健康领域问答
- **对话历史记录**：每次问答自动存入 MySQL，支持按用户查询历史记录
- **Markdown 渲染**：前端使用 Marked.js 渲染 AI 回复中的格式化内容
- **多用户并发**：使用 `ConcurrentHashMap` 管理多路 SSE 长连接，支持多用户同时在线
- **AOP 性能监控**：切面自动记录 Service 层方法执行耗时

---

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.3.8 / Java 21 |
| AI 推理 | Spring AI Ollama 1.0.0-M4 |
| 实时通信 | Server-Sent Events (SSE) |
| 数据库 | MySQL 8.0 + MyBatis-Plus 3.5.10 |
| 连接池 | HikariCP |
| 构建工具 | Maven |
| 前端 | HTML5 + JavaScript + Marked.js |

---

## 系统架构

```
前端 (chat-doctor.html)
    │
    ├── GET  /sse/connect                    建立 SSE 长连接
    ├── POST /ollama/ai/doctor/stream3       发送问题（流式）
    └── GET  /ollama/ai/doctor/records       查询历史记录
         │
         ▼
Controller 层 (OllamaController / SSEController)
         │
         ▼
Service 层 (OllamaService / ChatRecordService)
    │              │
    ▼              ▼
Ollama 本地模型   MySQL (chat_record 表)
(my-doctor:1.0.1.Release)
```

**核心流程：**
1. 前端通过 `/sse/connect` 建立 SSE 长连接
2. 用户提问 → 后端保存问题到数据库
3. 后端调用 Ollama 获取 `Flux<ChatResponse>` 流式响应
4. 每个响应片段通过 SSE `add` 事件推送前端
5. 推送完成后发送 `finish` 事件，并将完整回复存入数据库

---

## 快速开始

### 前置依赖

- Java 21+
- Maven 3.8+
- MySQL 8.0（数据库名：`deepseek-doctor`）
- [Ollama](https://ollama.ai) 已在本地运行，并加载 `my-doctor:1.0.1.Release` 模型

### 数据库初始化

项目启动时会自动执行 `schema.sql` 建表，无需手动初始化。

确保 MySQL 中已创建数据库：

```sql
CREATE DATABASE `deepseek-doctor` DEFAULT CHARACTER SET utf8mb4;
```

### 配置

编辑 `src/main/resources/application-dev.yml`，按需修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/deepseek-doctor
    username: root
    password: 123456
```

Ollama 地址和模型名称在 `src/main/resources/application.yml` 中配置：

```yaml
spring:
  ai:
    ollama:
      base-url: http://127.0.0.1:11434
      chat:
        model: my-doctor:1.0.1.Release
```

### 构建与运行

```bash
# 构建
mvn clean package -DskipTests

# 运行
java -jar target/deepsuck-doctor-1.0-SNAPSHOT.jar

# 或直接开发模式运行
mvn spring-boot:run
```

服务启动后访问：`http://localhost:8080/chat-doctor.html`

---

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/sse/connect?userId={id}` | 建立 SSE 连接 |
| POST | `/ollama/ai/doctor/stream3` | 发送问诊请求（流式） |
| GET | `/ollama/ai/doctor/records?userId={id}` | 查询用户历史记录 |
| GET | `/sse/online` | 获取当前在线用户数 |

---

## 项目结构

```
src/main/
├── java/com/toxy/deepsuckdoctor/
│   ├── controller/        # OllamaController、SSEController
│   ├── service/           # OllamaService、ChatRecordService
│   ├── mapper/            # MyBatis-Plus Mapper
│   ├── entity/            # ChatRecord 实体
│   ├── config/            # CorsConfig（跨域）
│   ├── aspect/            # ServiceLogAspect（AOP 日志）
│   └── sse/               # SSEServer（连接管理）
└── resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-prod.yml
    ├── schema.sql
    └── static/
        └── chat-doctor.html
```
