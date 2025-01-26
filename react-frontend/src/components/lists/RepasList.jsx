import React from 'react';
import { Box, IconButton } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import VisibilityIcon from '@mui/icons-material/Visibility';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import repasService from '../../services/repasService'

function RepasList({ repas, reload, displayNossPatient = false, allowDelete = false }) {
  const navigate = useNavigate();

  const onView = (noSS, date) => {
    navigate(`/patients/${noSS}/repas/${date}`);
  };

  const onDelete = async (noss, date) => {
    try {
      await repasService.deleteRepas(noss, date);
      await reload();
    } catch (error) {
      console.error('Erreur lors de la suppression du repas :', error);
    }
  };

  // Définition des colonnes  
  const columns = [
    { field: 'nossPatient', headerName: 'NoSS patient', flex: 1 },
    { field: 'dateConsommation', headerName: 'Date', flex: 1 },
    { field: 'type', headerName: 'Type', flex: 1 },
    { field: 'comment', headerName: 'Commentaire', flex: 1 },
    { field: 'nossInfirmier', headerName: 'NoSS infirmier', flex: 1 },
    {field: 'totalCalories', headerName: 'Total calories', flex: 1},
    {field: 'totalProteines', headerName: 'Total protéines (g)', flex: 1},
    {field: 'totalGlucides', headerName: 'Total glucides (g)', flex: 1},
    {field: 'totalLipides', headerName: 'Total lipides (g)', flex: 1},
    {field: 'totalHydratation', headerName: 'Total hydratation (ml)', flex: 1},
    {
      field: 'actions',
      headerName: 'Actions',
      flex: 1,
      sortable: false,
      renderCell: (params) => (
        <Box>
          {/* Bouton Voir */}
          <IconButton
            color="primary"
            onClick={() => onView(params.row.nossPatient, params.row.dateConsommation)}
            title="Voir le repas"
          >
            <VisibilityIcon />
          </IconButton>
          {/* Bouton Supprimer */}
          {allowDelete ? 
          <IconButton
            color="error"
            onClick={() => onDelete(params.row.nossPatient, params.row.dateConsommation)}
            title="Supprimer le repas"
          >
            <DeleteIcon />
          </IconButton> : null}
        </Box>
      ),
    },
  ];

  return (
    <Box sx={{ bgcolor: 'white' }}>
      <DataGrid
        rows={repas}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5, 10, 20]}
        disableSelectionOnClick
        getRowId={(row) => `${row.nossPatient}-${row.dateConsommation}`}
        columnVisibilityModel={{ nossPatient: displayNossPatient }}
      />
    </Box>
  );
}

export default RepasList;
