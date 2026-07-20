# 社区居民水电费管理系统 — 后端对接规范

> 本文档供后端项目实现与前端联调使用。前端当前为 Pinia 内存数据，联调时需将 `VITE_API_BASE_URL` 指向后端。

---

## 一、端口与环境

| 服务 | 端口 | 说明 |
|------|------|------|
| **前端（Vite 开发）** | `5173` | `npm run dev` 默认地址 `http://localhost:5173` |
| **后端 API** | `8080` | 建议统一使用，基础路径见下 |
| **MySQL（若使用）** | `3306` | 数据库名建议：`community_utility` |
| **Redis（可选）** | `6379` | 会话/缓存，非必须 |

### 后端基础 URL

```
http://localhost:8080/api
```

### 前端环境变量（联调时在项目根目录建 `.env.development`）

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

### Vite 开发代理（可选，避免 CORS）

```js
// vite.config.js → server.proxy
'/api': { target: 'http://localhost:8080', changeOrigin: true }
```

### CORS（后端必须配置）

- 允许来源：`http://localhost:5173`（生产环境改为实际前端域名）
- 允许方法：`GET, POST, PUT, DELETE, OPTIONS`
- 允许头：`Content-Type, Authorization`
- 允许携带凭证：若使用 Cookie 会话则 `credentials: true`

---

## 二、统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

列表接口 `data` 建议：

```json
{
  "list": [],
  "total": 0
}
```

---

## 三、认证（建议实现，前端暂未接）

| 角色 | role | 说明 |
|------|------|------|
| 管理员 | `ADMIN` | 全部管理端接口 |
| 居民 | `RESIDENT` | 仅本人数据（`residentId` 从 token 取） |

```
POST /api/auth/login
Body: { "username": "admin", "password": "xxx", "role": "ADMIN" }
      或居民用手机号+密码，role: "RESIDENT"
Response data: { "token": "jwt...", "residentId": "R001", "name": "张三" }
```

请求头：`Authorization: Bearer <token>`

---

## 四、数据实体字段（与前端一致）

### 1. 住户 resident

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 如 R001 |
| name | string | 姓名 |
| phone | string | 电话 |
| building | string | 楼栋，如 1栋 |
| unit | string | 单元，如 101 |
| type | string | 业主 / 租户 |
| moveInDate | string | yyyy-MM-dd |

### 2. 水电费配置 utility

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | |
| residentId | string | |
| residentName | string | 冗余，便于列表展示 |
| waterPrice | number | 元/吨 |
| electricPrice | number | 元/度 |
| waterBase | number | 水表底数 |
| electricBase | number | 电表底数 |
| remark | string | |

### 3. 抄表记录 meterReading

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | |
| residentId | string | |
| residentName | string | |
| period | string | 账期 yyyy-MM |
| waterReading | number | 本期水表读数 |
| electricReading | number | 本期电表读数 |
| readDate | string | yyyy-MM-dd |
| operator | string | 抄表员 |

录入时可传（后端计算用量）：`prevWater`, `prevElectric`

### 4. 用量流水 usageFlow（抄表录入后自动生成）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | |
| residentId | string | |
| residentName | string | |
| type | string | 水 / 电 |
| usage | number | 用量 |
| period | string | |
| amount | number | 金额 |
| createTime | string | |

**业务规则**：`usage = max(0, 本期读数 - 上期读数)`；`amount = usage × 对应单价`（从 utility 表取）

### 5. 缴费订单 order

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | |
| residentId | string | |
| residentName | string | |
| period | string | |
| waterFee | number | |
| electricFee | number | |
| total | number | waterFee + electricFee |
| status | string | 待缴费 / 已缴费 |
| payTime | string | 可空 |
| createTime | string | |

---

## 五、REST API 清单

基础路径：`http://localhost:8080/api`

### 住户管理 `/residents`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/residents?keyword=` | 查询（姓名/电话/楼栋） |
| GET | `/residents/{id}` | 详情 |
| POST | `/residents` | 添加 |
| PUT | `/residents/{id}` | 修改 |
| DELETE | `/residents/{id}` | 删除（级联删 utility 等） |

### 水电费配置 `/utilities`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/utilities?residentId=` | 查询 |
| POST | `/utilities` | 添加 |
| PUT | `/utilities/{id}` | 修改 |
| DELETE | `/utilities/{id}` | 删除 |

### 抄表 `/meter-readings`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/meter-readings?residentId=&period=` | 抄表记录查询 |
| POST | `/meter-readings` | 抄表录入（**同时生成 usageFlow**） |

Body 示例：

```json
{
  "residentId": "R001",
  "period": "2026-05",
  "prevWater": 120.3,
  "waterReading": 125.5,
  "prevElectric": 800,
  "electricReading": 856.2,
  "readDate": "2026-05-22",
  "operator": "管理员"
}
```

### 用量流水 `/usage-flows`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/usage-flows?residentId=&type=&period=` | 查询 |

居民端：传 token 或 `residentId`（仅本人）

### 订单 `/orders`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/orders?residentId=&period=&status=` | 查询 |
| POST | `/orders` | 生成订单 |
| PUT | `/orders/{id}` | 修改 |
| DELETE | `/orders/{id}` | 删除 |

### 统计报表 `/reports`（管理员）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/reports/by-time?start=2026-01&end=2026-12` | 按时间段 |
| GET | `/reports/by-building` | 按楼栋单元 |
| GET | `/reports/by-type` | 按住户类型 |
| GET | `/reports/payment?period=2026-04` | 缴费报表（可导出 CSV，或前端自行导出） |

### 仪表盘（可选）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/dashboard/admin` | 住户数、待缴订单、抄表数、累计实收 |
| GET | `/dashboard/resident` | 当前居民待缴/已缴（需 residentId） |

---

## 六、数据库建议（MySQL）

```sql
-- 字符集 utf8mb4
CREATE DATABASE community_utility DEFAULT CHARSET utf8mb4;
```

表：`resident`, `utility`, `meter_reading`, `usage_flow`, `payment_order`  
（可选）`sys_user`：管理员账号、居民账号与 `resident_id` 关联

连接示例（Spring Boot `application.yml`）：

```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/community_utility?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

---

## 七、技术栈建议（任选其一）

| 方案 | 端口 | 说明 |
|------|------|------|
| **Spring Boot 3** + MyBatis-Plus / JPA | 8080 | 课程设计常用 |
| **Node.js** + Express/Koa + Sequelize | 8080 或 3000 | 若用 3000，请同步改本文档与前端 `.env` |

---

## 八、联调检查清单

- [ ] 后端 `8080` 启动，`GET http://localhost:8080/api/residents` 有数据
- [ ] CORS 允许 `5173`
- [ ] 抄表 POST 后自动生成 1～2 条 usageFlow
- [ ] 删除住户时级联处理关联数据
- [ ] 前端配置 `VITE_API_BASE_URL` 并改造 `dataStore.js` 为 API 调用

---

## 九、给下一轮「写后端」的提示语（可直接复制）

```
请根据 d:\desktop\新建文件夹\docs\BACKEND_SPEC.md 实现后端：
- 端口 8080，API 前缀 /api
- MySQL 数据库 community_utility
- 实现全部 REST 接口与抄表自动生成用量流水逻辑
- 配置 CORS 允许 http://localhost:5173
技术栈：[Spring Boot / Node.js]（任选你填写的）
```
