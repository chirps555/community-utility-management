# 社区居民水电费管理系统

基于 Spring Boot + Vue 3 的社区居民水电费管理系统，实现居民信息管理、抄表录入、阶梯计价、在线缴费、报表统计等功能。

## 技术栈

### 后端
- **框架**: Spring Boot 3.x
- **数据库**: H2 / MySQL
- **认证**: JWT
- **构建工具**: Maven

### 前端
- **框架**: Vue 3 + Vite
- **UI 组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router

## 项目结构

```
社区居民水电费管理/
├── sdfguanlixt/          # Spring Boot 后端
│   ├── src/main/java/com/example/sdfguanlixt/
│   │   ├── controller/   # REST API 控制器
│   │   ├── service/      # 业务逻辑层
│   │   ├── repository/   # 数据访问层
│   │   ├── entity/       # JPA 实体
│   │   ├── dto/          # 数据传输对象
│   │   ├── config/       # 配置类
│   │   └── common/       # 公共工具类
│   └── src/main/resources/
│       ├── application.yml
│       └── data.sql
├── vue/                  # Vue 3 前端
│   ├── src/
│   │   ├── views/        # 页面组件
│   │   ├── components/   # 公共组件
│   │   ├── api/          # API 接口
│   │   ├── stores/       # Pinia 状态管理
│   │   └── router/       # 路由配置
│   └── package.json
└── community_utility.sql # MySQL 初始化脚本
```

## 功能模块

### 管理员端
- 居民管理（新增、编辑、删除）
- 水电单价配置
- 抄表录入
- 报表统计（按时间、楼栋、类型）
- 系统管理

### 居民端
- 个人信息查询
- 账单计算与查询
- 在线缴费
- 用水用电记录查询
- 费用明细查看

## 阶梯计价算法

系统采用阶梯计价算法，用量越多，单价越高：

### 水费阶梯
| 档位 | 用量范围 | 倍率 |
|------|---------|------|
| 第1档 | 0-15吨 | 1.0倍 |
| 第2档 | 15-25吨 | 1.2倍 |
| 第3档 | >25吨 | 1.5倍 |

### 电费阶梯
| 档位 | 用量范围 | 倍率 |
|------|---------|------|
| 第1档 | 0-200度 | 1.0倍 |
| 第2档 | 200-400度 | 1.1倍 |
| 第3档 | >400度 | 1.3倍 |

## 快速开始

### 环境要求
- Java 17+
- Node.js 18+
- Maven / npm

### 启动后端

```bash
cd sdfguanlixt
mvnw.cmd spring-boot:run
```

后端服务启动在 `http://localhost:8080`

### 启动前端

```bash
cd vue
npm install
npm run dev
```

前端服务启动在 `http://localhost:5173`

### 访问系统

打开浏览器访问 `http://localhost:5173`

## 登录账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 居民张三 | 13800138001 | 123456 |
| 居民李四 | 13800138002 | 123456 |

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 居民管理
- `GET /api/residents` - 获取居民列表
- `POST /api/residents` - 新增居民
- `PUT /api/residents/{id}` - 更新居民信息
- `DELETE /api/residents/{id}` - 删除居民

### 抄表管理
- `GET /api/meter-readings` - 获取抄表记录
- `POST /api/meter-readings` - 录入抄表数据

### 订单管理
- `GET /api/orders` - 获取订单列表
- `POST /api/orders` - 生成订单
- `POST /api/orders/{id}/pay` - 缴费

### 报表统计
- `GET /api/reports/by-time` - 按时间统计
- `GET /api/reports/by-building` - 按楼栋统计
- `GET /api/reports/by-type` - 按类型统计

## 数据库配置

### H2 数据库（默认）
启动后自动初始化数据，无需额外配置。

H2 控制台：`http://localhost:8080/h2-console`

### MySQL 数据库
1. 创建数据库 `community_utility`
2. 修改 `application.yml` 中的数据库配置
3. 使用 `mysql` 配置启动：

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

