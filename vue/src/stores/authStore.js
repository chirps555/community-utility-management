import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/api'

const STORAGE_KEY = 'auth'

function loadAuth() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

function saveAuth(data) {
  if (data) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
    localStorage.setItem('token', data.token)
  } else {
    localStorage.removeItem(STORAGE_KEY)
    localStorage.removeItem('token')
  }
}

export const useAuthStore = defineStore('auth', () => {
  const saved = loadAuth()
  const token = ref(saved?.token || '')
  const role = ref(saved?.role || '')
  const name = ref(saved?.name || '')
  const residentId = ref(saved?.residentId || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ADMIN')
  const isResident = computed(() => role.value === 'RESIDENT')

  function persist() {
    saveAuth({
      token: token.value,
      role: role.value,
      name: name.value,
      residentId: residentId.value,
    })
  }

  async function login(username, password, loginRole) {
    const data = await api.login({
      username,
      password,
      role: loginRole,
    })
    applyAuth(data)
    return data
  }

  async function register(payload) {
    const data = await api.register(payload)
    applyAuth(data)
    return data
  }

  function applyAuth(data) {
    token.value = data.token
    role.value = data.role
    name.value = data.name
    residentId.value = data.residentId || ''
    persist()
  }

  function logout() {
    token.value = ''
    role.value = ''
    name.value = ''
    residentId.value = ''
    saveAuth(null)
  }

  return {
    token,
    role,
    name,
    residentId,
    isLoggedIn,
    isAdmin,
    isResident,
    login,
    register,
    logout,
  }
})
