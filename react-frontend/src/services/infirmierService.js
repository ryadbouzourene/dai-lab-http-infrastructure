import axios from 'axios';

const API_BASE_URL = 'https://localhost/api';

const getInfirmiers = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/infirmiers`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des infirmiers :', error);
    throw error;
  }
};

const getInfirmierById = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/infirmiers/${id}`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération de l'infirmier avec l'id ${id} :`, error);
    throw error;
  }
};

const getRepas = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/infirmiers/${id}/repas`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des repas de l'infirmier avec l'id ${id} :`, error);
    throw error;
  }
};

export default {
  getInfirmiers,
  getInfirmierById,
  getRepas
};
