import React from 'react';
import { Box, IconButton } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { useNavigate } from 'react-router-dom';

function DieteticiensList({ dieteticiens }) {
  const navigate = useNavigate();

  const onView = (id) => {
    navigate(`/dieteticiens/${id}`);
  };

  // Définition des colonnes
  const columns = [
    { field: 'noSS', headerName: 'NoSS', flex: 1 },
    { field: 'nom', headerName: 'Nom', flex: 1 },
    { field: 'prenom', headerName: 'Prénom', flex: 1 },
    { field: 'email', headerName: 'E-mail', flex: 1 },
    { field: 'dateEmbauche', headerName: "Date d'embauche", flex: 1 },

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
            onClick={() => onView(params.row.noSS)}
            title="Voir le diététicien"
          >
            <VisibilityIcon />
          </IconButton>
        </Box>
      ),
    },
  ];

  return (
    <Box sx={{ bgcolor: 'white' }}>
      <DataGrid
        rows={dieteticiens}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5, 10, 20]}
        disableSelectionOnClick
        getRowId={(row) => row.noSS}
      />
    </Box>
  );
}

export default DieteticiensList;
