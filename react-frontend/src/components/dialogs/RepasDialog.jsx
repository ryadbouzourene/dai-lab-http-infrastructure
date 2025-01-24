import React, { useState, useEffect, useContext } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Stack,
  MenuItem,
  Select,
  FormControl,
  Alert,
  Typography,
  IconButton,
  Divider,
  InputLabel
} from '@mui/material';
import { AuthContext } from '../../contexts/AuthContext';
import { Roles } from '../../utils/utils';
import patientService from '../../services/patientService';
import repasService from '../../services/repasService';
import DeleteIcon from '@mui/icons-material/Delete';
import AddBox from '@mui/icons-material/AddBox';
import consommableService from '../../services/consommableService';

function RepasDialog({ open, handleClose, patientNoSS, onSave }) {
  const { user } = useContext(AuthContext);
  const [type, setType] = useState('');
  const [remarque, setRemarque] = useState('');
  const [selectedPatientNoss, setSelectedPatientNoss] = useState(patientNoSS);
  const [patients, setPatients] = useState([]);
  const [consommables, setConsommables] = useState([]);
  const [selectedConsommables, setSelectedConsommables] = useState([]);
  const [error, setError] = useState('');

  const isInfirmier = user.role === Roles.INFIRMIER;

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (!patientNoSS) {
          const patientsData = await patientService.getPatients();
          setPatients(patientsData);
        }
        const consommablesData = await consommableService.getConsommables();
        setConsommables(consommablesData);
      } catch (err) {
        console.error('Erreur lors de la récupération des données :', err);
        setError('Impossible de charger les données nécessaires.');
      }
    };

    fetchData();
  }, [patientNoSS]);

  // Réinitialisation des champs lorsque la modale s'ouvre
  useEffect(() => {
    if (open) {
      setType('');
      setRemarque('');
      setSelectedConsommables([]);
      setError('');
    }
  }, [open]);

  const handleAddConsommable = () => {
    setSelectedConsommables([...selectedConsommables, { id: '', quantite: '' }]);
  };

  const handleConsommableChange = (index, field, value) => {
    const updatedConsommables = [...selectedConsommables];
    updatedConsommables[index][field] = value;
    setSelectedConsommables(updatedConsommables);
  };

  const handleRemoveConsommable = (index) => {
    const updatedConsommables = [...selectedConsommables];
    updatedConsommables.splice(index, 1);
    setSelectedConsommables(updatedConsommables);
  };

  const handleSubmit = async () => {
    setError('');
    if (!type || !patientNoSS) {
      setError('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    const repasData = {
      nossPatient: selectedPatientNoss,
      nossInfirmier: isInfirmier ? user.noss : null,
      type : type,
      comment: remarque,
      consommables: selectedConsommables.map((cons) => ({
        id: parseInt(cons.id, 10),
        quantite: parseInt(cons.quantite, 10),
      })),
    };

    try {
      await repasService.createRepas(repasData);
      onSave(); // Callback pour rafraîchir la liste après création
      handleClose();
    } catch (err) {
      console.error('Erreur lors de la création du repas :', err);
      setError(err.response.data.error || 'Impossible de créer le repas. Vérifiez les informations saisies.');
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>Ajouter un repas</DialogTitle>
      <DialogContent>
        <Stack spacing={2}>
          {error && <Alert severity="error">{error}</Alert>}
          {!patientNoSS && (
            <FormControl fullWidth>
              <InputLabel>Patient</InputLabel>
              <Select
                value={patients.length ? patients[0].noSS : ''}
                onChange={(e) => setSelectedPatientNoss(e.target.value)}
              >
                {patients.map((patient) => (
                  <MenuItem key={patient.noSS} value={patient.noSS}>
                    {patient.nom} {patient.prenom} - n°{patient.noSS}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}
          <FormControl fullWidth>
            <InputLabel>Type de repas</InputLabel>
            <Select
              value={type}
              onChange={(e) => setType(e.target.value)}
            >
              {['PETITDEJEUNER', 'DEJEUNER', 'DINER', 'COLLATION'].map((option) => (
                <MenuItem key={option} value={option}>
                  {option}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Commentaire"
            value={remarque}
            onChange={(e) => setRemarque(e.target.value)}
            multiline
            rows={3}
            fullWidth
          />
          <Stack direction="row" alignItems="center">
            <Typography variant="h6">Consommables</Typography>
            <IconButton onClick={handleAddConsommable} color="primary">
              <AddBox />
            </IconButton>
          </Stack>
          <Divider />
          {selectedConsommables.map((cons, index) => (
            <Stack key={index} direction="row" spacing={2} alignItems="center">
              <FormControl fullWidth>
                <InputLabel>Consommable</InputLabel>
                <Select
                  value={cons.id}
                  onChange={(e) => handleConsommableChange(index, 'id', e.target.value)}
                >
                  {consommables.map((item) => (
                    <MenuItem key={item.id} value={item.id}>
                      {item.nom}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
              <TextField
                label="Quantité (g)"
                type="number"
                value={cons.quantite}
                onChange={(e) => handleConsommableChange(index, 'quantite', e.target.value)}
                fullWidth
              />
              <IconButton
                color="error"
                onClick={() => handleRemoveConsommable(index)}
                title="Supprimer le consommable"
              >
                <DeleteIcon />
              </IconButton>
            </Stack>
          ))}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} variant='outlined'>
          Annuler
        </Button>
        <Button onClick={handleSubmit} variant="contained" color="primary">
          Valider
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default RepasDialog;
