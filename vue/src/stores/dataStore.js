import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/api'
import { useAuthStore } from '@/stores/authStore'

function normalizeOrder(o) {
  return { ...o, payTime: o.payTime || '' }
}

export const useDataStore = defineStore('data', () => {
  const residents = ref([])
  const utilities = ref([])
  const meterReadings = ref([])
  const usageFlows = ref([])
  const orders = ref([])
  const currentResidentId = ref('R001')
  const loading = ref(false)
  const inited = ref(false)

  const currentResident = computed(() =>
    residents.value.find((r) => r.id === currentResidentId.value),
  )

  async function fetchResidents(keyword) {
    residents.value = await api.getResidents(keyword)
    if (
      residents.value.length &&
      !residents.value.some((r) => r.id === currentResidentId.value)
    ) {
      currentResidentId.value = residents.value[0].id
    }
  }

  async function fetchUtilities(residentId) {
    utilities.value = await api.getUtilities(residentId)
  }

  async function fetchMeterReadings(params = {}) {
    meterReadings.value = await api.getMeterReadings(params)
  }

  async function fetchUsageFlows(params = {}) {
    usageFlows.value = await api.getUsageFlows(params)
  }

  async function fetchOrders(params = {}) {
    const list = await api.getOrders(params)
    orders.value = list.map(normalizeOrder)
  }

  async function calculateBill(period) {
    return api.calculateBill(currentResidentId.value, period)
  }

  async function generateBill(period) {
    const item = await api.generateBill(currentResidentId.value, period)
    await fetchOrders()
    return normalizeOrder(item)
  }

  async function payOrder(id, payData) {
    const result = await api.payOrder(id, payData)
    await fetchOrders()
    return result
  }

  async function simulateFullPayment(period, payData) {
    const result = await api.simulateFullPayment(currentResidentId.value, period, payData)
    await fetchOrders()
    return result
  }

  async function init() {
    if (loading.value) return
    loading.value = true
    try {
      const authStore = useAuthStore()
      if (authStore.isResident && authStore.residentId) {
        currentResidentId.value = authStore.residentId
      }
      await Promise.all([
        fetchResidents(),
        fetchUtilities(),
        fetchMeterReadings(),
        fetchUsageFlows(),
        fetchOrders(),
      ])
      inited.value = true
    } finally {
      loading.value = false
    }
  }

  async function ensureInit() {
    if (!inited.value) await init()
  }

  async function addResident(data) {
    const item = await api.createResident(data)
    await fetchResidents()
    return item
  }

  async function updateResident(id, data) {
    await api.updateResident(id, data)
    await fetchResidents()
  }

  async function deleteResident(id) {
    await api.deleteResident(id)
    await Promise.all([
      fetchResidents(),
      fetchUtilities(),
      fetchMeterReadings(),
      fetchUsageFlows(),
      fetchOrders(),
    ])
  }

  async function addUtility(data) {
    const item = await api.createUtility(data)
    await fetchUtilities()
    return item
  }

  async function updateUtility(id, data) {
    await api.updateUtility(id, data)
    await fetchUtilities()
  }

  async function deleteUtility(id) {
    await api.deleteUtility(id)
    await fetchUtilities()
  }

  async function addMeterReading(data) {
    const result = await api.createMeterReading(data)
    await Promise.all([fetchMeterReadings(), fetchUsageFlows()])
    return result?.meterReading ?? result
  }

  async function addOrder(data) {
    const item = await api.createOrder(data)
    await fetchOrders()
    return normalizeOrder(item)
  }

  async function updateOrder(id, data) {
    await api.updateOrder(id, data)
    await fetchOrders()
  }

  async function deleteOrder(id) {
    await api.deleteOrder(id)
    await fetchOrders()
  }

  async function fetchReportByTime(start, end) {
    return api.getReportByTime({ start, end })
  }

  async function fetchReportByBuilding() {
    return api.getReportByBuilding()
  }

  async function fetchReportByType() {
    return api.getReportByType()
  }

  async function fetchReportPayment(period) {
    return api.getReportPayment(period)
  }

  async function fetchAdminDashboard() {
    return api.getAdminDashboard()
  }

  return {
    residents,
    utilities,
    meterReadings,
    usageFlows,
    orders,
    currentResidentId,
    currentResident,
    loading,
    inited,
    init,
    ensureInit,
    fetchResidents,
    fetchUtilities,
    fetchMeterReadings,
    fetchUsageFlows,
    fetchOrders,
    calculateBill,
    generateBill,
    payOrder,
    simulateFullPayment,
    addResident,
    updateResident,
    deleteResident,
    addUtility,
    updateUtility,
    deleteUtility,
    addMeterReading,
    addOrder,
    updateOrder,
    deleteOrder,
    fetchReportByTime,
    fetchReportByBuilding,
    fetchReportByType,
    fetchReportPayment,
    fetchAdminDashboard,
  }
})
