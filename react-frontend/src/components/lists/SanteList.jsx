import React, {useContext} from 'react';
import { Box, IconButton } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import DeleteIcon from '@mui/icons-material/Delete';
import patientService from '../../services/patientService';
import { AuthContext } from '../../contexts/AuthContext';
import { Roles } from '../../utils/utils';

function SanteList({ santeData, reload, allowDelete = false }) {
  const { user } = useContext(AuthContext);

  // Fonction appelée lors du clic sur "Supprimer"
  const onDelete = async (noss, date) => {
    try {
      await patientService.deleteSante(noss, date);
      await reload(); // Appeler le callback pour rafraîchir les données
    } catch (error) {
      console.error('Erreur lors de la suppression des données de santé :', error);
    }
  };

  // Définition des colonnes
  const columns = [
    { field: 'noss_patient', headerName: 'NoSS Patient', flex: 1 },
    { field: 'date', headerName: 'Date', flex: 1 },
    { field: 'taille', headerName: 'Taille (cm)', flex: 1 },
    { field: 'poids', headerName: 'Poids (kg)', flex: 1 },
    { field: 'imc', headerName: 'IMC', flex: 1 },
    { field: 'tour_de_taille', headerName: 'Tour de Taille (cm)', flex: 1 },
    { field: 'niveau_activite_physique', headerName: "Niveau d'activité physique", flex: 1 },
    {
      field: 'actions',
      headerName: 'Actions',
      flex: 1,
      sortable: false,
      renderCell: (params) => (
        <Box>
          {/* Bouton Supprimer */}
          {allowDelete && user.role != Roles.PATIENT ? (
            <IconButton
              color="error"
              onClick={() => onDelete(params.row.noss_patient, params.row.date)}
              title="Supprimer les données de santé"
            >
              <DeleteIcon />
            </IconButton>
          ) : null}
        </Box>
      ),
    },
  ];

  return (
    <Box sx={{ bgcolor: 'white' }}>
      <DataGrid
        rows={santeData}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5, 10, 20]}
        disableSelectionOnClick
        getRowId={(row) => `${row.noss_patient}-${row.date}`} // Identifiant unique basé sur NoSS et date
      />
    </Box>
  );
}

export default SanteList;
