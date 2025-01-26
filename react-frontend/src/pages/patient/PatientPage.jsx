import React, { useState, useEffect, useContext } from 'react';
import { Box, Typography, Tabs, Tab, Stack, Button } from '@mui/material';
import Layout from '../../components/layouts/Layout';
import { useParams } from 'react-router-dom';
import patientService from '../../services/patientService';
import PatientInfo from './PatientInfo';
import PatientRepas from './PatientRepas';
import PatientSante from './PatientSante';
import PatientObjectifs from './PatientObjectifs';
import PatientAllergies from './PatientAllergies';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import { Roles } from '../../utils/utils';
import RepasDialog from '../../components/dialogs/RepasDialog';
import PatientDialog from '../../components/dialogs/PatientDialog';

function PatientPage() {
  const { user } = useContext(AuthContext);
  const { noss } = useParams();
  const [tabValue, setTabValue] = useState(0);
  const [patient, setPatient] = useState(null);
  const [dieteticien, setDieteticien] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isRepasDialogOpen, setIsRepasDialogOpen] = useState(false);
  const [isPatientDialogOpen, setIsPatientDialogOpen] = useState(false);

  const [reloadTrigger, setReloadTrigger] = useState(0);

  const fetchPatient = async () => {
    try {
      setLoading(true);
      const patientData = await patientService.getPatientById(noss);
      setPatient(patientData);

      const dieteticienData = await patientService.getDieteticien(noss);
      setDieteticien(dieteticienData);
    } catch (err) {
      console.error('Erreur lors de la récupération du patient :', err);
      setError('Impossible de charger les informations du patient.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPatient();
  }, [noss, reloadTrigger]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleOpenRepasDialog = () => {
    setIsRepasDialogOpen(true);
  };

  const handleCloseRepasDialog = () => {
    setIsRepasDialogOpen(false);
  };

  const handleSaveRepasDialog = () => {
    setReloadTrigger((prev) => prev + 1);
    handleCloseRepasDialog();
  };

  const handleOpenPatientDialog = () => {
    setIsPatientDialogOpen(true);
  };

  const handleClosePatientDialog = () => {
    setIsPatientDialogOpen(false);
  };

  const handleSavePatientDialog = () => {
    setReloadTrigger((prev) => prev + 1);
    handleClosePatientDialog();
  };

  if (user.role === Roles.PATIENT && noss != user.noss) {
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
      <Stack direction="column" spacing={2}>
        <Stack direction="row" justifyContent="space-between" alignItems="flex-start">
          <Stack>
            <PatientInfo patient={patient} dieteticien={dieteticien} />

            {user.role === Roles.ADMIN || user.role === Roles.DIETETICIEN ?
              <Button variant="outlined" onClick={handleOpenPatientDialog}>
                Modifier les données
              </Button>
              : null
            }

          </Stack>
          <Button variant="contained" onClick={handleOpenRepasDialog}>
            Ajouter un repas
          </Button>
        </Stack>
      </Stack>

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
          }}
        >
          <Tab label="Repas" />
          <Tab label="Données de santé" />
          <Tab label="Objectifs" />
          <Tab label="Allergies" />
        </Tabs>
      </Box>

      {tabValue === 0 && <PatientRepas patient={patient} reloadTrigger={reloadTrigger} />}
      {tabValue === 1 && <PatientSante patient={patient} user={user} reloadTrigger={reloadTrigger} />}
      {tabValue === 2 && <PatientObjectifs patient={patient} reloadTrigger={reloadTrigger} />}
      {tabValue === 3 && <PatientAllergies patient={patient} reloadTrigger={reloadTrigger} />}

      <RepasDialog
        open={isRepasDialogOpen}
        handleClose={handleCloseRepasDialog}
        onSave={handleSaveRepasDialog}
        patientNoSS={noss}
      />

      <PatientDialog
        open={isPatientDialogOpen}
        handleClose={handleClosePatientDialog}
        onSave={handleSavePatientDialog}
        patientData={patient}
      />
    </Layout>
  );
}

export default PatientPage;
