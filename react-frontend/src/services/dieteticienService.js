import axios from 'axios';

const API_BASE_URL = '/api';

const getDieteticiens = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/dieteticiens`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des diététiciens :', error);
    throw error;
  }
};

const getDieteticienById = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/dieteticiens/${id}`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération du diététicien avec l'id ${id} :`, error);
    throw error;
  }
};

const getPatients = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/dieteticiens/${id}/patients`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des patients du diététicien avec l'id ${id} :`, error);
    throw error;
  }
};

const getObjectifs = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/dieteticiens/${id}/objectifs`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des objectifs du diététicien avec l'id ${id} :`, error);
    throw error;
  }
};

export default {
  getDieteticiens,
  getDieteticienById,
  getPatients,
  getObjectifs,
};
