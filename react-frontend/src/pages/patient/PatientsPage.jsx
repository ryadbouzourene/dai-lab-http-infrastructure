import React, { useState, useEffect, useContext } from 'react';
import { Button, Typography, Stack } from '@mui/material';
import Layout from '../../components/layouts/Layout';
import PatientsList from '../../components/lists/PatientsList';
import patientService from '../../services/patientService';
import PatientDialog from '../../components/dialogs/PatientDialog';
import { AuthContext } from '../../contexts/AuthContext';
import { Roles } from '../../utils/utils';

function PatientsPage() {
  const { user } = useContext(AuthContext);
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);

  const fetchPatients = async () => {
    try {
      const data = await patientService.getPatients();
      setPatients(data);
    } catch (error) {
      console.error('Erreur lors de la récupération des patients :', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPatients();
  }, []);

  const handleOpenDialog = () => {
    setDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setDialogOpen(false);
  };

  const handleSave = () => {
    fetchPatients(); // Rafraîchir la liste après création d'un patient
    handleCloseDialog();
  };

  return (
    <Layout>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h4">Liste des patients</Typography>

        {user.role === Roles.ADMIN || user.role === Roles.DIETETICIEN ?
          <Button variant="contained" onClick={handleOpenDialog}>
            Nouveau patient
          </Button> 
          : null}
      </Stack>
      {loading ? (
        <Typography>Chargement...</Typography>
      ) : (
        <PatientsList patients={patients} reload={fetchPatients} />
      )}
      <PatientDialog
        open={dialogOpen}
        handleClose={handleCloseDialog}
        onSave={handleSave}
        patientData={null} // Null pour indiquer qu'il s'agit d'une création
      />
    </Layout>
  );
}

export default PatientsPage;
