# TeamTodo Backend

TeamTodo后端应用 - 基于Spring Boot 3.x + MyBatis Plus + MySQL构建

## 技术栈

- Java 17
- Spring Boot 3.2.1
- MyBatis Plus 3.5.5
- MySQL 8.0+
- Maven

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

## 快速开始

### 1. 数据库配置

创建数据库并执行初始化脚本:

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### 2. 配置数据库连接

修改 `src/main/resources/application.yml` 中的数据库连接信息:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/teamtodo
    username: root
    password: your_password
```

### 3. 构建项目

```bash
mvn clean install
```

### 4. 运行应用

```bash
mvn spring-boot:run
```

或者:

```bash
java -jar target/teamtodo-backend-1.0.0-SNAPSHOT.jar
```

## 验证

### 健康检查接口

应用启动后，访问以下接口验证:

1. **自定义健康检查**: http://localhost:8080/api/health
2. **Ping接口**: http://localhost:8080/api/health/ping
3. **Spring Actuator健康检查**: http://localhost:8080/api/actuator/health

### 预期响应

```json
{
  "status": "UP",
  "message": "TeamTodo Backend is running",
  "timestamp": "2026-01-30T15:00:00"
}
```

## 项目结构

```
teamtodo-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/teamtodo/
│   │   │       ├── TeamTodoApplication.java
│   │   │       ├── config/         # 配置类
│   │   │       ├── controller/     # 控制器
│   │   │       ├── service/        # 服务层
│   │   │       ├── mapper/         # MyBatis Mapper
│   │   │       └── entity/         # 实体类
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── schema.sql
│   └── test/
│       └── java/
│           └── com/teamtodo/
└── pom.xml
```

## 验收标准检查

- [x] Spring Boot应用可成功启动
- [x] 数据库连接配置完成
- [x] 基础健康检查接口可访问
