import request from './request'

function listData(data) {
  if (Array.isArray(data)) return data
  return data?.list ?? []
}

export const api = {
  getResidents: (keyword) =>
    request.get('/residents', { params: { keyword } }).then(listData),
  getResident: (id) => request.get(`/residents/${id}`),
  createResident: (data) => request.post('/residents', data),
  updateResident: (id, data) => request.put(`/residents/${id}`, data),
  deleteResident: (id) => request.delete(`/residents/${id}`),

  getUtilities: (residentId) =>
    request.get('/utilities', { params: { residentId } }).then(listData),
  createUtility: (data) => request.post('/utilities', data),
  updateUtility: (id, data) => request.put(`/utilities/${id}`, data),
  deleteUtility: (id) => request.delete(`/utilities/${id}`),

  getMeterReadings: (params) =>
    request.get('/meter-readings', { params }).then(listData),
  createMeterReading: (data) => request.post('/meter-readings', data),

  getUsageFlows: (params) =>
    request.get('/usage-flows', { params }).then(listData),

  getOrders: (params) =>
    request.get('/orders', { params }).then(listData),
  calculateBill: (residentId, period) =>
    request.get('/orders/calculate', { params: { residentId, period } }),
  generateBill: (residentId, period) =>
    request.post('/orders/generate-bill', null, { params: { residentId, period } }),
  payOrder: (id, data) => request.post(`/orders/${id}/pay`, data),
  simulateFullPayment: (residentId, period, data) =>
    request.post('/orders/simulate-full-payment', data, { params: { residentId, period } }),
  createOrder: (data) => request.post('/orders', data),
  updateOrder: (id, data) => request.put(`/orders/${id}`, data),
  deleteOrder: (id) => request.delete(`/orders/${id}`),

  getReportByTime: (params) =>
    request.get('/reports/by-time', { params }).then(listData),
  getReportByBuilding: () =>
    request.get('/reports/by-building').then(listData),
  getReportByType: () => request.get('/reports/by-type').then(listData),
  getReportPayment: (period) =>
    request.get('/reports/payment', { params: { period } }).then(listData),

  getAdminDashboard: () => request.get('/dashboard/admin'),
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
}

export default api
