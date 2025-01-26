import React, { useEffect, useState } from 'react';
import { Box, Typography, Tab, Tabs } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import Layout from '../components/layouts/Layout';
import consommableService from '../services/consommableService';
import ConsommablesList from '../components/lists/ConsommablesList';

function ConsommablesPage() {
  const [consommables, setConsommables] = useState([]);
  const [allergenes, setAllergenes] = useState([]);
  const [tab, setTab] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError('');
      try {
        const consommablesData = await consommableService.getConsommables();
        setConsommables(consommablesData);

        const allergenesData = await consommableService.getAllergenes();
        setAllergenes(allergenesData);
      } catch (err) {
        console.error('Erreur lors de la récupération des données :', err);
        setError('Impossible de récupérer les données.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return (
    <Layout>
      <Typography variant="h4" gutterBottom>
        Consommables et allergènes
      </Typography>

      <Tabs
        value={tab}
        onChange={(e, newValue) => setTab(newValue)}
        sx={{ marginBottom: 2 }}
      >
        <Tab label="Consommables" />
        <Tab label="Allergènes" />
      </Tabs>

      {loading ? (
        <Typography>Chargement des données...</Typography>
      ) : error ? (
        <Typography color="error">{error}</Typography>
      ) : (
        <>
          {tab === 0 && (
            <Box>
              <ConsommablesList consommablesData={consommables}/>
            </Box>
          )}

          {tab === 1 && (
            <Box>
              <Box sx={{ bgcolor: 'white' }}>
                <DataGrid
                  rows={allergenes}
                  columns={[
                    { field: 'nomAllergene', headerName: 'Nom', flex: 1 },
                  ]}
                  pageSize={5}
                  rowsPerPageOptions={[5, 10, 20]}
                  disableSelectionOnClick
                  getRowId={(row) => row.nomAllergene}
                />
              </Box>
            </Box>
          )}
        </>
      )}
    </Layout>
  );
}

export default ConsommablesPage;
