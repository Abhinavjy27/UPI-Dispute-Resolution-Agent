import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000';

export const api = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      console.error('API Error:', error.response.data);
    } else if (error.request) {
      console.error('Network Error:', error.message);
    } else {
      console.error('Error:', error.message);
    }
    return Promise.reject(error);
  }
);

// API endpoints
export const disputeApi = {
  createDispute: (data: {
    transactionId: string;
    merchantUpi: string;
    amount: number;
    customerPhone: string;
  }) => api.post('/disputes', data),

  getDisputeById: (id: string) => api.get(`/disputes/${id}`),

  getUserDisputes: (phone: string) => api.get(`/disputes/user/${encodeURIComponent(phone)}`),

  healthCheck: () => api.get('/health'),
};

export default api;
