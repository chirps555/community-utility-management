# 社区居民水电费记录管理系统 — 后端

Spring Boot 3 + JPA，对接前端 `d:\desktop\新建文件夹`。

## 关键配置

| 项目 | 值 |
|------|-----|
| 后端端口 | **8080** |
| API 基础路径 | **http://localhost:8080/api** |
| 前端开发端口 | **5173** |
| CORS 允许来源 | http://localhost:5173 |

## 运行

### 方式一：内置 H2（无需 MySQL，默认）

```bash
cd "d:\实验\jsp实验\sdfguanlixt"
mvnw.cmd spring-boot:run
```

启动后访问：`http://localhost:8080/api/residents`

H2 控制台：`http://localhost:8080/h2-console`（JDBC URL 见控制台提示）

### 方式二：MySQL

1. 创建数据库 `community_utility`
2. 修改 `application.yml` 中 `mysql` 配置的账号密码
3. 启动：

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

## 测试账号（登录接口）

| 角色 | username | password | role |
|------|----------|----------|------|
| 管理员 | admin | admin123 | ADMIN |
| 居民张三 | 13800138001 | 123456 | RESIDENT |
| 居民李四 | 13800138002 | 123456 | RESIDENT |

```
POST http://localhost:8080/api/auth/login
{"username":"admin","password":"admin123","role":"ADMIN"}
```

## 前端联调

在 Vue 项目根目录创建 `.env.development`：

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## 主要接口

- `GET/POST /api/residents`，`PUT/DELETE /api/residents/{id}`
- `GET/POST /api/utilities`，`PUT/DELETE /api/utilities/{id}`
- `GET/POST /api/meter-readings`（POST 自动生成用量流水）
- `GET /api/usage-flows`
- `GET/POST /api/orders`，`PUT/DELETE /api/orders/{id}`
- `GET /api/reports/by-time|by-building|by-type|payment`
- `GET /api/dashboard/admin|resident`

统一响应：`{ "code": 200, "message": "success", "data": {} }`
