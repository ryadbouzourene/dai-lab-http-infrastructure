import React, { useState, useEffect, useContext } from 'react';
import { Box, Typography, Tabs, Tab, Stack, Button } from '@mui/material';
import Layout from '../../components/layouts/Layout';
import { useParams, Navigate } from 'react-router-dom';
import dieteticienService from '../../services/dieteticienService';
import DieteticienPatients from './DieteticienPatients';
import DieteticienObjectifs from './DieteticienObjectifs';
import { AuthContext } from '../../contexts/AuthContext';
import { Roles, calculateAge } from '../../utils/utils';

function DieteticienPage() {
  const { user } = useContext(AuthContext);
  const { noss } = useParams();
  const [tabValue, setTabValue] = useState(0);
  const [dieteticien, setDieteticien] = useState(null);
  const [patients, setPatients] = useState([]);
  const [objectifs, setObjectifs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDieteticienData = async () => {
    try {
      const dieteticienData = await dieteticienService.getDieteticienById(noss);
      setDieteticien(dieteticienData);

      const patientsData = await dieteticienService.getPatients(noss);
      setPatients(patientsData);

      const objectifsData = await dieteticienService.getObjectifs(noss);
      setObjectifs(objectifsData);
    } catch (err) {
      console.error('Erreur lors de la récupération des données :', err);
      setError('Impossible de charger les informations.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDieteticienData();
  }, [noss]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  if (user.role === Roles.DIETETICIEN && noss != user.noss) {
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
      {/* Section Infos générales */}
      <Stack direction="row" justifyContent="space-between" alignItems="flex-start">
        <Box>
          <Typography variant="h4" gutterBottom>
            Diététicien
          </Typography>
          <Typography variant="h5" gutterBottom>
            Informations générales
          </Typography>
          <Typography><strong>Statut :</strong> {dieteticien.statut}</Typography>
          <Typography><strong>Numéro de sécurité sociale :</strong> {dieteticien.noSS}</Typography>
          <Typography><strong>Nom :</strong> {dieteticien.nom}</Typography>
          <Typography><strong>Prénom :</strong> {dieteticien.prenom}</Typography>
          <Typography><strong>Date de naissance :</strong> {dieteticien.dateNaissance} ({calculateAge(dieteticien.dateNaissance)} ans)</Typography>
          <Typography><strong>Sexe :</strong> {dieteticien.sexe}</Typography>
          <Typography><strong>E-mail :</strong> {dieteticien.email}</Typography>
          <Typography><strong>Service :</strong> {dieteticien.idService}</Typography>
          <Typography><strong>Date d'embauche :</strong> {dieteticien.dateEmbauche}</Typography>
        </Box>
      </Stack>

      {/* Tabs pour les sections */}
      <Box>
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
            mb: 3,
            mt: 3,
          }}
        >
          <Tab label="Patients" />
          <Tab label="Objectifs" />
        </Tabs>
      </Box>

      {/* Contenu des Tabs */}
      {tabValue === 0 && <DieteticienPatients patients={patients} reload={fetchDieteticienData} />}
      {tabValue === 1 && <DieteticienObjectifs objectifs={objectifs} reload={fetchDieteticienData} />}
    </Layout>
  );
}

export default DieteticienPage;
