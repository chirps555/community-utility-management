<template>
  <div class="home">
    <div class="hero">
      <h1>社区居民水电费记录管理系统</h1>
      <p>请选择登录端，使用账号密码登录后进入系统</p>
    </div>

    <div class="portals">
      <el-card class="portal-card admin" shadow="hover" @click="$router.push({ path: '/login', query: { role: 'ADMIN' } })">
        <el-icon :size="48" color="#409eff"><Setting /></el-icon>
        <h2>管理员端</h2>
        <ul>
          <li>住户信息管理</li>
          <li>水电费信息管理</li>
          <li>抄表与用量管理</li>
          <li>缴费统计与报表</li>
        </ul>
        <el-button type="primary" size="large">管理员登录</el-button>
      </el-card>

      <el-card class="portal-card resident" shadow="hover" @click="$router.push({ path: '/login', query: { role: 'RESIDENT' } })">
        <el-icon :size="48" color="#67c23a"><User /></el-icon>
        <h2>居民端</h2>
        <ul>
          <li>缴费订单管理</li>
          <li>住户 / 水电费信息查询</li>
          <li>用量流水与抄表记录查询</li>
          <li>订单查询</li>
        </ul>
        <el-button type="success" size="large">居民登录</el-button>
        <p class="register-link" @click.stop="$router.push('/register')">
          新用户？<span>立即注册</span>
        </p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(160deg, #e8f4ff 0%, #f5f7fa 50%, #fff 100%);
}

.hero {
  text-align: center;
  margin-bottom: 48px;
}

.hero h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 12px;
}

.hero p {
  color: #909399;
  font-size: 14px;
}

.portals {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  justify-content: center;
}

.portal-card {
  width: 320px;
  cursor: pointer;
  text-align: center;
  transition: transform 0.2s;
}

.portal-card:hover {
  transform: translateY(-4px);
}

.portal-card h2 {
  margin: 16px 0 12px;
  color: #303133;
}

.portal-card ul {
  list-style: none;
  text-align: left;
  margin: 0 0 24px;
  padding: 0 24px;
  color: #606266;
  font-size: 14px;
  line-height: 2;
}

.portal-card ul li::before {
  content: '• ';
  color: #409eff;
}

.register-link {
  margin-top: 12px;
  font-size: 13px;
  color: #909399;
}

.register-link span {
  color: #67c23a;
  cursor: pointer;
}

.register-link span:hover {
  text-decoration: underline;
}
</style>
