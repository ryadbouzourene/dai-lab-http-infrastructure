import axios from 'axios';

const API_BASE_URL = 'https://localhost/api';

const getPatients = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/patients`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des patients :', error);
    throw error;
  }
};

const getPatientById = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/patients/${id}`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération du patient avec l'id ${id} :`, error);
    throw error;
  }
};

const getDieteticien = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/patients/${id}/dieteticien`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération du diététicien du patient avec l'id ${id} :`, error);
    throw error;
  }
};

const getRepas = async (id, interval) => {
  try {
    let response;

    if (!interval) {
      response = await axios.get(`${API_BASE_URL}/patients/${id}/repas`, { withCredentials: true });
    } else {
      response = await axios.get(`${API_BASE_URL}/patients/${id}/repas?interval=${interval}`, { withCredentials: true });
    }

    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des repas du patient avec l'id ${id} :`, error);
    throw error;
  }
};

const getObjectifs = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/patients/${id}/objectifs`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des objectifs du patient avec l'id ${id} :`, error);
    throw error;
  }
};

const getAllergies = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/patients/${id}/allergies`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des allergies du patient avec l'id ${id} :`, error);
    throw error;
  }
};

const getSante = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/patients/${id}/sante`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des données de santé du patient avec l'id ${id} :`, error);
    throw error;
  }
};

const createPatient = async (patientData) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/patients`, patientData, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la création du patient :', error);
    throw error;
  }
};

const updatePatient = async (id, patientData) => {
  try {
    const response = await axios.put(`${API_BASE_URL}/patients/${id}`, patientData, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la mise à jour du patient avec l'id ${id} :`, error);
    throw error;
  }
};

const deletePatient = async (id) => {
  try {
    const response = await axios.delete(`${API_BASE_URL}/patients/${id}`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la suppression du patient avec l'id ${id} :`, error);
    throw error;
  }
};

const createSante = async (santeData) => {
  try {
    console.log(santeData)
    const response = await axios.post(`${API_BASE_URL}/sante`, santeData, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la création des données de santé :', error);
    throw error;
  }
}

const deleteSante = async (noss, date) => {
  try {
    const response = await axios.delete(`${API_BASE_URL}/sante?noss=${noss}&date=${date}`, { withCredentials: true });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la suppression des données de sante ${noss} ${date} :`, error);
    throw error;
  }
};

export default {
  getPatients,
  getPatientById,
  getDieteticien,
  getRepas,
  getObjectifs,
  getAllergies,
  getSante,
  createPatient,
  updatePatient,
  deletePatient,
  createSante,
  deleteSante
};
