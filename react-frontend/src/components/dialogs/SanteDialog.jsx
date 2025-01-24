import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  MenuItem,
  Stack,
} from '@mui/material';
import patientService from '../../services/patientService';

const niveauxActivite = [
  'SEDENTAIRE',
  'LEGEREMENT_ACTIF',
  'MODEREMENT_ACTIF',
  'TRES_ACTIF',
  'EXTREMEMENT_ACTIF',
];

function SanteDialog({ open, handleClose, onSave, patientNoSS }) {
  const [formData, setFormData] = useState({
    taille: '',
    poids: '',
    tour_de_taille: '',
    niveau_activite_physique: '',
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const newSanteData = {
        noss_patient: patientNoSS,
        taille: parseFloat(formData.taille),
        poids: parseFloat(formData.poids),
        tour_de_taille: parseFloat(formData.tour_de_taille),
        niveau_activite_physique: formData.niveau_activite_physique,
      };
      await patientService.createSante(newSanteData);
      onSave(); // Appeler la fonction de mise à jour des données parent
      handleClose(); // Fermer la modale
    } catch (error) {
      console.error('Erreur lors de la création des données de santé :', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>Saisir des données de santé</DialogTitle>
      <DialogContent>
        <Stack spacing={2} mt={2}>
          <TextField
            name="taille"
            label="Taille (cm)"
            type="number"
            value={formData.taille}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            name="poids"
            label="Poids (kg)"
            type="number"
            value={formData.poids}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            name="tour_de_taille"
            label="Tour de Taille (cm)"
            type="number"
            value={formData.tour_de_taille}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            name="niveau_activite_physique"
            label="Niveau d'Activité Physique"
            select
            value={formData.niveau_activite_physique}
            onChange={handleChange}
            fullWidth
          >
            {niveauxActivite.map((niveau) => (
              <MenuItem key={niveau} value={niveau}>
                {niveau}
              </MenuItem>
            ))}
          </TextField>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} variant='outlined' disabled={loading}>
          Annuler
        </Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          color="primary"
          disabled={loading || !formData.taille || !formData.poids || !formData.niveau_activite_physique}
        >
          {loading ? 'En cours...' : 'Ajouter'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default SanteDialog;
