import React, { useState, useEffect, useContext } from 'react';
import { Box, Typography, Tabs, Tab } from '@mui/material';
import Layout from '../../components/layouts/Layout';
import { useParams, Navigate } from 'react-router-dom';
import infirmierService from '../../services/infirmierService';
import patientService from '../../services/patientService';
import { AuthContext } from '../../contexts/AuthContext';
import { Roles, calculateAge } from '../../utils/utils';
import PatientsList from '../../components/lists/PatientsList';
import RepasList from '../../components/lists/RepasList';

function InfirmierPage() {
  const { user } = useContext(AuthContext);
  const { noss } = useParams();
  const [tabValue, setTabValue] = useState(0);
  const [infirmier, setInfirmier] = useState(null);
  const [patients, setPatients] = useState([]);
  const [repas, setRepas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const fetchInfirmierData = async () => {
    setLoading(true);
    setError(null);
    try {
      // Récupérer les informations de l'infirmier
      const infirmierData = await infirmierService.getInfirmierById(noss);
      setInfirmier(infirmierData);

      // Récupérer les patients associés
      const patientsData = await patientService.getPatients();
      setPatients(patientsData);

      // Récupérer les repas associés
      const repasData = await infirmierService.getRepas(noss);
      setRepas(repasData);
    } catch (err) {
      console.error("Erreur lors de la récupération des données :", err);
      setError("Impossible de charger les informations.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInfirmierData();
  }, [noss]);

  if (user.role === Roles.INFIRMIER && user.noss != noss) {
    return <Navigate to="/unauthorized" />;
  }

  if (loading) {
    return (
      <Layout>
        <Typography>Chargement...</Typography>
      </Layout>
    );
  }

  if (error) {
    return (
      <Layout>
        <Typography color="error">{error}</Typography>
      </Layout>
    );
  }

  return (
    <Layout>
      <Typography variant="h4" gutterBottom>
        Infirmier
      </Typography>

      {/* Section Informations générales */}
      <Box>
        <Typography variant="h5" gutterBottom>
          Informations générales
        </Typography>
        {infirmier && (
          <Box>
            <Typography><strong>Statut :</strong> {infirmier.statut}</Typography>
            <Typography><strong>Numéro de sécurité sociale :</strong> {infirmier.noSS}</Typography>
            <Typography><strong>Nom :</strong> {infirmier.nom}</Typography>
            <Typography><strong>Prénom :</strong> {infirmier.prenom}</Typography>
            <Typography>
              <strong>Date de naissance :</strong> {infirmier.dateNaissance} ({calculateAge(infirmier.dateNaissance)} ans)
            </Typography>
            <Typography><strong>Sexe :</strong> {infirmier.sexe}</Typography>
            <Typography><strong>E-mail :</strong> {infirmier.email}</Typography>
            <Typography><strong>Service :</strong> {infirmier.idService}</Typography>
            <Typography><strong>Date d'embauche :</strong> {infirmier.dateEmbauche}</Typography>
            <Typography>
              <strong>Certificats :</strong> {infirmier.certificats.join(', ')}
            </Typography>
          </Box>
        )}
      </Box>

      {/* Tabs Section */}
      <Box sx={{ marginTop: 4 }}>
        <Tabs
          value={tabValue}
          onChange={handleTabChange}
          centered
          variant="fullWidth"
          sx={{
            '& .MuiTabs-indicator': { backgroundColor: 'primary.main' },
            '& .MuiTab-root': {
              flex: 1,
              textTransform: 'none',
              fontWeight: 'bold',
            },
            marginBottom: 3,
          }}
        >
          <Tab label="Tous les patients" />
          <Tab label="Repas saisis" />
        </Tabs>
      </Box>

      {/* Contenu des Tabs */}
      {tabValue === 0 && (
        <PatientsList patients={patients} reload={fetchInfirmierData} />
      )}
      {tabValue === 1 && (
        <Box>
          <RepasList repas={repas} displayNossPatient={true} />
        </Box>
      )}
    </Layout>
  );
}

export default InfirmierPage;
