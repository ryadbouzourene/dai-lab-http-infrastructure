import axios from 'axios';

const API_BASE_URL = '/api';

const getRepasConsommables = async (noss, date)  => {
  try {
    const response = await axios.get(`${API_BASE_URL}/repas/consommables?noss=${noss}&date=${date}`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des consommables :', error);
    throw error;
  }
}

const getRepas = async (noss, date)  => {
  try {
    const response = await axios.get(`${API_BASE_URL}/repas?noss=${noss}&date=${date}`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération du repas  : ${noss} ${date}`, error);
    throw error;
  }
}

const createRepas = async (repasData) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/repas/`, repasData, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la création du repas :', error);
    throw error;
  }
};

const deleteRepas = async (noss, date) => {
  try {
    const response = await axios.delete(`${API_BASE_URL}/repas?noss=${noss}&date=${date}`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la suppression du repas ${noss} ${date} :`, error);
    throw error;
  }
};

export default {
  getRepasConsommables,
  getRepas,
  createRepas,
  deleteRepas
};
