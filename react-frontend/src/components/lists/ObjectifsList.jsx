import React from 'react';
import { Box } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';

function ObjectifsList({ objectifs }) {
  // Définition des colonnes
  const columns = [
    { field: 'numero', headerName: 'Numéro', flex: 1 },
    { field: 'titre', headerName: 'Titre', flex: 2 },
    { field: 'date_debut', headerName: 'Date Début', flex: 1 },
    { field: 'date_fin', headerName: 'Date Fin', flex: 1 },
    { field: 'reussi', headerName: 'Réussi', flex: 1, renderCell: (params) => (params.row.reussi ? 'Oui' : 'Non') },
    { field: 'commentaire', headerName: 'Commentaire', flex: 2 },
    { field: 'noss_patient', headerName: 'NoSS Patient', flex: 1 },
    { field: 'noss_dieteticien', headerName: 'NoSS Diététicien', flex: 1 },
  ];

  return (
    <Box sx={{ bgcolor: 'white' }}>
      <DataGrid
        rows={objectifs}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5, 10, 20]}
        disableSelectionOnClick
        getRowId={(row) => row.numero}
      />
    </Box>
  );
}

export default ObjectifsList;
