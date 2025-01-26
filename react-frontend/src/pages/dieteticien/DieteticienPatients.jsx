import React from 'react';
import { Box } from '@mui/material';
import PatientsList from '../../components/lists/PatientsList';

function DieteticienPatients({ patients, reload }) {
  return (
    <Box>
      <PatientsList patients={patients} reload={reload} allowDelete={true}/>
    </Box>
  );
}

export default DieteticienPatients;
