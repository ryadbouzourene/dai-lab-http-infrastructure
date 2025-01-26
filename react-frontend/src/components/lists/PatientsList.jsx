import React from 'react';
import { Box, IconButton } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import VisibilityIcon from '@mui/icons-material/Visibility';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import patientService from '../../services/patientService';

function PatientsList({ patients, reload, allowDelete = false }) {
  const navigate = useNavigate();

  const onView = (noSS) => {
    navigate(`/patients/${noSS}`);
  };
  
  const onDelete = async (id) => {
    try {
      await patientService.deletePatient(id);
      await reload()
    } catch (error) {
      console.error('Erreur lors de la suppression du patient :', error);
    }
  };

  // Définition des colonnes
  const columns = [
    { field: 'noSS', headerName: 'NoSS', flex: 1 },
    { field: 'nom', headerName: 'Nom', flex: 1 },
    { field: 'prenom', headerName: 'Prénom', flex: 1 },
    { field: 'dateNaissance', headerName: 'Date de naissance', flex: 1 },
    { field: 'sexe', headerName: 'Sexe', flex: 1 },
    { field: 'email', headerName: 'E-mail', flex: 1 },
    { field: 'dateAdmission', headerName: "Date d'admission", flex: 1 },

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
            title="Voir le patient"
          >
            <VisibilityIcon />
          </IconButton>

          {/* Bouton Supprimer */}

          {allowDelete ?  
            <IconButton
              color="error"
              onClick={() => onDelete(params.row.noSS)}
              title="Supprimer le patient"
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
        rows={patients}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5, 10, 20]}
        disableSelectionOnClick
        getRowId={(row) => row.noSS}
      />
    </Box>
  );
}

export default PatientsList;
