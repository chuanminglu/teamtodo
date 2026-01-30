# 运行说明

## 开发环境运行 (无需MySQL)

使用test profile运行，可以在没有MySQL的情况下测试应用启动和健康检查接口:

```bash
# 使用Maven运行
mvn spring-boot:run -Dspring-boot.run.profiles=test

# 或使用jar包运行
java -jar target/teamtodo-backend-1.0.0-SNAPSHOT.jar --spring.profiles.active=test
```

## 生产环境运行 (需要MySQL)

1. 确保MySQL已安装并运行
2. 执行数据库初始化脚本:
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

3. 修改`src/main/resources/application.yml`中的数据库连接配置:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/teamtodo
    username: your_username
    password: your_password
```

4. 运行应用:
```bash
# 使用Maven
mvn spring-boot:run

# 或使用jar包
java -jar target/teamtodo-backend-1.0.0-SNAPSHOT.jar
```

## 健康检查接口

应用启动后，可以访问以下接口验证:

1. **自定义健康检查**: 
   ```
   GET http://localhost:8080/api/health
   ```
   响应:
   ```json
   {
     "status": "UP",
     "message": "TeamTodo Backend is running",
     "timestamp": "2026-01-30T15:00:00"
   }
   ```

2. **Ping接口**:
   ```
   GET http://localhost:8080/api/health/ping
   ```
   响应:
   ```json
   {
     "message": "pong"
   }
   ```

3. **Spring Actuator健康检查**:
   ```
   GET http://localhost:8080/api/actuator/health
   ```
   响应:
   ```json
   {
     "status": "UP",
     "components": {
       "diskSpace": {...},
       "ping": {"status": "UP"}
     }
   }
   ```
