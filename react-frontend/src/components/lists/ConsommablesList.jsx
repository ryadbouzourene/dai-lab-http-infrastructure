import React from 'react';
import { Box } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';

function ConsommablesList({ consommablesData, displayQty = false }) {
  // Définition des colonnes
  const columns = [
    { field: 'type_consommable', headerName: 'Type', flex: 1 },
    { field: 'nom', headerName: 'Nom', flex: 1 },
    { field: 'quantite', headerName: 'Quantité (g)', flex: 1 },
    { field: 'calories', headerName: 'Calories (kcal)', flex: 1 },
    { field: 'proteines', headerName: 'Protéines (g)', flex: 1 },
    { field: 'glucides', headerName: 'Glucides (g)', flex: 1 },
    { field: 'lipides', headerName: 'Lipides (g)', flex: 1 },
    { field: 'potassium', headerName: 'Potassium (mg)', flex: 1 },
    { field: 'cholesterol', headerName: 'Cholestérol (mg)', flex: 1 },
    { field: 'sodium', headerName: 'Sodium (mg)', flex: 1 },
    { field: 'vit_A', headerName: 'Vitamine A (mg)', flex: 1 },
    { field: 'vit_C', headerName: 'Vitamine C (mg)', flex: 1 },
    { field: 'vit_D', headerName: 'Vitamine D (mg)', flex: 1 },
    { field: 'calcium', headerName: 'Calcium (mg)', flex: 1 },
    { field: 'fer', headerName: 'Fer (mg)', flex: 1 },
  ];

  return (
    <Box sx={{ bgcolor: 'white' }}>
      <DataGrid
        rows={consommablesData}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5, 10, 20]}
        disableSelectionOnClick
        getRowId={(row) => row.nom}
        columnVisibilityModel={{ quantite: displayQty }}
      />
    </Box>
  );
}

export default ConsommablesList;