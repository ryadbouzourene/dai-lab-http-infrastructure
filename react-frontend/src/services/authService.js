import axios from 'axios';

const API_BASE_URL = 'https://localhost/api';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // Inclure les cookies de session
});

const login = async (username, password) => {
  try {
    const response = await axiosInstance.post('/login', { username, password });
    return response.data;
  } catch (error) {
    throw error;
  }
};

const logout = async () => {
  try {
    const response = await axiosInstance.post('/logout');
    return response.data;
  } catch (error) {
    throw error;
  }
};

const checkSession = async () => {
  try {
    const response = await axiosInstance.get('/session');
    return response.data;
  } catch (error) {
    if (error.response && error.response.status === 401) {
      return null; // Non authentifié
    }
    throw error;
  }
};

export default {
  login,
  logout,
  checkSession,
};
