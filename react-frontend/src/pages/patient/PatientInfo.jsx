import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import {calculateAge} from '../../utils/utils'

function PatientInfo({patient, dieteticien}) {
  return (
    <Box sx={{ marginBottom: 4 }}>
      <Typography variant="h4" gutterBottom>
        Patient
      </Typography>

      <Typography variant="h5" gutterBottom>
        Informations générales
      </Typography>
      <Box>
        <Typography><strong>Numéro de sécurité sociale :</strong> {patient.noSS}</Typography>
        <Typography><strong>Nom :</strong> {patient.nom}</Typography>
        <Typography><strong>Prénom :</strong> {patient.prenom}</Typography>
        <Typography><strong>Date de naissance :</strong> {patient.dateNaissance} ({calculateAge(patient.dateNaissance)} ans)</Typography>
        <Typography><strong>Sexe :</strong> {patient.sexe}</Typography>
        <Typography><strong>E-mail :</strong> {patient.email}</Typography>
        <Typography>
          <strong>Diététicien :</strong> {(dieteticien.nom + ' ' + dieteticien.prenom) || 'Aucun'}
        </Typography>
        <Typography><strong>Date d'admission :</strong> {patient.dateAdmission}</Typography>
      </Box>
    </Box>
  );
}

export default PatientInfo;
