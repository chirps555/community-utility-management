# 社区居民水电费记录管理系统（前端）

基于 Vue 3 + Vite + Vue Router + Pinia + Element Plus 的单页应用，按功能框架图实现管理员端与居民端全部模块。

## 功能结构

### 管理员端
- 住户信息管理：添加、查询、修改、删除
- 水电费信息管理：添加、查询、修改、删除
- 抄表与用量管理：抄表录入、用量流水、抄表记录
- 缴费统计与报表：按时间 / 楼栋 / 住户类型统计、报表生成与 CSV 导出

### 居民端
- 缴费订单管理：生成、查询、修改、删除
- 信息查询：住户、水电费、用量、抄表、订单

## 运行

```bash
npm install
npm run dev
```

浏览器访问控制台显示的本地地址（一般为 `http://localhost:5173`）。

## 构建

```bash
npm run build
```

## 说明

- 所有页面均为标准 `.vue` 单文件组件（`<template>` + `<script setup>` + `<style scoped>`）
- 当前使用 Pinia 内存模拟数据，便于演示；对接后端时可将 `stores/dataStore.js` 替换为 API 调用
