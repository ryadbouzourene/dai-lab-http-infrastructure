import React, { useState, useEffect } from 'react';
import { Box, Typography } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import patientService from '../../services/patientService';

function PatientAllergies({ patient }) {
  const [allergies, setAllergies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchAllergies = async () => {
    setLoading(true);
    setError('');
    try {
      const allergiesData = await patientService.getAllergies(patient.noSS);
      setAllergies(allergiesData);
    } catch (err) {
      console.error('Erreur lors de la récupération des allergies :', err);
      setError('Impossible de récupérer les allergies.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllergies();
  }, [patient.noSS]);

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        Allergies du patient
      </Typography>

      {/* Gestion des erreurs et du chargement */}
      {loading ? (
        <Typography>Chargement des allergies...</Typography>
      ) : error ? (
        <Typography color="error">{error}</Typography>
      ) : (
        <Box sx={{ bgcolor: 'white' }}>
          <DataGrid
            rows={allergies}
            columns={[{ field: 'nomAllergene', headerName: 'Allergies', flex: 1 }]}
            pageSize={5}
            rowsPerPageOptions={[5]}
            disableSelectionOnClick
            getRowId={(row) => row.nomAllergene} 
          />
        </Box>
      )}
    </Box>
  );
}

export default PatientAllergies;
