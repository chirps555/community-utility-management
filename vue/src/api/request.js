import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 20000,
  headers: { 'Content-Type': 'application/json' },
})

let onUnauthorized = null

export function setUnauthorizedHandler(handler) {
  onUnauthorized = handler
}

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body.code === 'number' && body.code !== 200) {
      if (body.code === 401 && onUnauthorized) {
        onUnauthorized()
      }
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body?.data !== undefined ? body.data : body
  },
  (error) => {
    const code = error.response?.data?.code
    if (code === 401 && onUnauthorized) {
      onUnauthorized()
    }
    const msg =
      error.response?.data?.message || error.message || '网络错误，请确认后端已启动'
    ElMessage.error(msg)
    return Promise.reject(error)
  },
)

export default request
