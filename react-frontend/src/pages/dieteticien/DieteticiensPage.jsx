import React, { useState, useEffect } from 'react';
import { Typography } from '@mui/material';
import Layout from '../../components/layouts/Layout';
import DieteticiensList from '../../components/lists/DieteticiensList';
import dieteticienService from '../../services/dieteticienService';

function DieteticiensPage() {
  const [dieteticiens, setDieteticiens] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDieteticiens = async () => {
      try {
        const data = await dieteticienService.getDieteticiens();
        setDieteticiens(data);
      } catch (error) {
        console.error('Erreur lors de la récupération des diététiciens :', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDieteticiens();
  }, []);

  return (
    <Layout>
      <Typography variant="h4" gutterBottom>
        Liste des diététiciens
      </Typography>
      {loading ? (
        <Typography>Chargement...</Typography>
      ) : (
        <DieteticiensList dieteticiens={dieteticiens} />
      )}
    </Layout>
  );
}

export default DieteticiensPage;
