import axios from 'axios';
import { API_BASE_URL } from '../config/endpointMappings';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  register: (data) => api.post('/api/auth/register', data),
  login: (data) => api.post('/api/auth/login', data)
};

// User API
export const userAPI = {
  getProfile: () => api.get('/api/users/me'),
  updateProfile: (data) => api.put('/api/users/me', data)
};

// Product API
export const productAPI = {
  getAll: () => api.get('/api/products'),
  getById: (id) => api.get(`/api/products/${id}`),
  search: (keyword) => api.get('/api/products/search', { params: { keyword } }),
  getByCategory: (category) => api.get(`/api/products/category/${category}`)
};

// Cart API
export const cartAPI = {
  get: () => api.get('/api/cart'),
  addItem: (data) => api.post('/api/cart/items', data),
  removeItem: (productId) => api.delete(`/api/cart/items/${productId}`),
  clear: () => api.delete('/api/cart')
};

// Order API
export const orderAPI = {
  create: () => api.post('/api/orders'),
  getAll: () => api.get('/api/orders'),
  getById: (id) => api.get(`/api/orders/${id}`),
  cancel: (id) => api.post(`/api/orders/${id}/cancel`)
};

// Inventory API
export const inventoryAPI = {
  checkStock: (productId) => api.get(`/api/inventory/${productId}`)
};

export default api;
