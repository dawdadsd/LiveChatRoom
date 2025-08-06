# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个前后端分离的社交网络演示系统，采用Spring Boot后端和Vue.js前端：

### 技术栈
**后端 (Java 21 + Spring Boot 3.5.4)**:
- Spring Security 6.x (JWT认证)
- MyBatis-Plus 3.5.12 (ORM框架)
- MySQL 8.0.33 (数据库)
- Spring WebSocket (实时通信)

**前端 (Vue.js 3.5.13 + TypeScript)**:
- Vite 6.2.0 (构建工具)
- Tailwind CSS 4.1.10 (样式框架)
- ESLint + Prettier (代码规范)

## 常用命令

### 后端开发命令
```bash
# 启动后端服务 (在项目根目录)
mvn spring-boot:run

# 运行测试
mvn test

# 编译项目
mvn compile

# 打包项目
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests
```

### 前端开发命令
```bash
# 进入前端目录
cd fronted

# 安装依赖
npm install

# 启动开发服务器 (http://localhost:5173)
npm run dev

# 构建生产版本
npm run build

# 代码检查
npm run lint

# 代码格式化
npm run format

# 预览构建结果
npm run preview
```

## 核心架构

### 数据库设计
- 使用JPA继承策略 `InheritanceType.SINGLE_TABLE`
- User基类，StudentUser和TeacherUser子类
- 通过`user_type`字段区分用户类型（STUDENT/TEACHER）

### 认证系统
- JWT双令牌机制：Access Token(1小时) + Refresh Token(7天)
- Spring Security过滤器链处理认证
- 自动令牌刷新机制

### 实时通信
- WebSocket支持实时聊天
- 连接管理和消息路由
- 支持多用户并发连接

## 重要文件说明

### 后端关键文件
- `src/main/java/xiaowu/social_network_demo/SocialNetworkDemoApplication.java` - 应用入口
- `src/main/java/xiaowu/social_network_demo/config/SecurityConfig.java` - 安全配置
- `src/main/java/xiaowu/social_network_demo/utils/JwtUtil.java` - JWT工具类
- `src/main/java/xiaowu/social_network_demo/mapper/UserMapper.java` - 数据访问层
- `src/main/resources/application.yml` - 应用配置

### 前端关键文件
- `fronted/src/main.ts` - 前端入口文件
- `fronted/src/services/authService.ts` - 认证服务
- `fronted/src/services/httpClient.ts` - HTTP客户端
- `fronted/src/config/api.ts` - API配置

### 配置文件
- `pom.xml` - Maven依赖配置
- `fronted/package.json` - NPM依赖配置
- `schema.sql` - 数据库表结构

## 开发注意事项

### User实体类特殊处理
- User类已从抽象类改为普通类以支持MyBatis实例化
- 不要使用MyBatis-Plus的`insert()`方法，使用自定义的`insertUser()`方法
- 查询时根据`user_type`使用对应的子类查询方法

### JWT认证流程
- 所有API请求需要在Header中包含：`Authorization: Bearer {accessToken}`
- 公开接口路径：`/api/auth/**`, `/api/public/**`, `/chat/**`
- 权限控制：教师访问`/api/teacher/**`，学生访问`/api/student/**`

### WebSocket连接
- 连接端点：`ws://localhost:8080/chat`
- 支持跨域连接
- 自动会话管理和消息路由

### 数据库连接
- 默认连接：`jdbc:mysql://localhost:3306/social_network`
- 用户名/密码：`root/root`
- 确保MySQL服务运行并创建了对应数据库

## 故障排除

### 常见问题
1. **User实例化错误**: 确保使用`UserMapper.insertUser()`而不是`insert()`
2. **JWT认证失败**: 检查application.yml中的JWT密钥配置
3. **跨域问题**: 已配置CORS，检查CorsConfig.java
4. **WebSocket连接失败**: 确保防火墙允许端口8080访问

### 调试技巧
- 后端日志级别在application.yml中配置
- 前端开发模式下会自动测试API连接
- 使用浏览器开发者工具查看网络请求和WebSocket连接

## 项目特色功能

### 双令牌认证机制
实现了安全的JWT双令牌系统，支持自动刷新和失效处理。

### 角色分离设计
教师和学生有不同的数据模型和API权限，支持扩展其他用户类型。

### 实时通信系统
基于WebSocket的聊天功能，支持多用户实时消息传递。

### 前后端完全分离
清晰的API接口设计，支持独立开发和部署。