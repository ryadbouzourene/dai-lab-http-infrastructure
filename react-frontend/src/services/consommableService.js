import axios from 'axios';

const API_BASE_URL = '/api';

const getConsommables = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/consommables`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des consommables :', error);
    throw error;
  }
};

const getAllergenes = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/allergenes`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des allergènes :', error);
    throw error;
  }
};

export default {
  getConsommables,
  getAllergenes,
};
