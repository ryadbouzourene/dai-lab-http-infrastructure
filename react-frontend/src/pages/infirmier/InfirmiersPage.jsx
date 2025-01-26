import React, { useState, useEffect } from 'react';
import { Button, Typography } from '@mui/material';
import Layout from '../../components/layouts/Layout';
import InfirmiersList from '../../components/lists/InfirmiersList';
import infirmierService from '../../services/infirmierService';

function InfirmiersPage() {
  const [infirmiers, setInfirmiers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchInfirmiers = async () => {
      try {
        const data = await infirmierService.getInfirmiers();
        setInfirmiers(data);
      } catch (error) {
        console.error('Erreur lors de la récupération des infirmiers :', error);
      } finally {
        setLoading(false);
      }
    };

    fetchInfirmiers();
  }, []);

  return (
    <Layout>
      <Typography variant="h4" gutterBottom>
        Liste des infirmiers
      </Typography>
      {loading ? (
        <Typography>Chargement...</Typography>
      ) : (
        <InfirmiersList infirmiers={infirmiers} />
      )}
    </Layout>
  );
}

export default InfirmiersPage;
