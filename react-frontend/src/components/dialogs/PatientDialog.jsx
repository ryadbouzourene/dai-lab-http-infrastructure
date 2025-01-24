import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Stack,
  MenuItem,
  FormControl,
  Select,
  InputLabel,
  Alert,
  Checkbox,
  ListItemText,
  OutlinedInput
} from '@mui/material';
import patientService from '../../services/patientService';
import dieteticienService from '../../services/dieteticienService';
import consommableService from '../../services/consommableService';

function PatientDialog({ open, handleClose, patientData, onSave }) {
  const [nom, setNom] = useState('');
  const [prenom, setPrenom] = useState('');
  const [dateNaissance, setDateNaissance] = useState('');
  const [sexe, setSexe] = useState('');
  const [email, setEmail] = useState('');
  const [dieteticien, setDieteticien] = useState('');
  const [allergies, setAllergies] = useState([]);
  const [dieteticiens, setDieteticiens] = useState([]);
  const [allergiesOptions, setAllergiesOptions] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const dieteticiensData = await dieteticienService.getDieteticiens();
        setDieteticiens(dieteticiensData);
        const allergiesData = await consommableService.getAllergenes();
        setAllergiesOptions(allergiesData);

        if (patientData) {
          const patientAllergies = await patientService.getAllergies(patientData.noSS);
          setAllergies(patientAllergies.map((allergy) => allergy.nomAllergene));
        }
      } catch (err) {
        console.error('Erreur lors de la récupération des données :', err);
        setError('Impossible de charger les données nécessaires.');
      }
    };
    if (open) {
      fetchData();
    }
  }, [open, patientData]);

  useEffect(() => {
    if (open && patientData) {
      setNom(patientData.nom || '');
      setPrenom(patientData.prenom || '');
      setDateNaissance(patientData.dateNaissance || '');
      setSexe(patientData.sexe || '');
      setEmail(patientData.email || '');
      setDieteticien(patientData.nossDieteticien || '');
    } else if (!open) {
      // Réinitialiser le formulaire à la fermeture
      setNom('');
      setPrenom('');
      setDateNaissance('');
      setSexe('');
      setEmail('');
      setDieteticien('');
      setAllergies([]);
      setError('');
    }
  }, [open, patientData]);

  const handleSubmit = async () => {
    setError('');
    if (!nom || !prenom || !dateNaissance || !sexe || !email) {
      setError('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    const patientPayload = {
      noSS: patientData?.noSS || null,
      nom,
      prenom,
      dateNaissance,
      sexe,
      email,
      dieteticien,
      allergies
    };

    try {
      if (patientData) {
        await patientService.updatePatient(patientPayload);
      } else {
        await patientService.createPatient(patientPayload);
      }
      onSave(); // Rafraîchir les données après création/mise à jour
      handleClose();
    } catch (err) {
      console.error("Erreur lors de l'enregistrement du patient :", err);
      setError("Une erreur est survenue lors de l'enregistrement.");
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>{patientData ? 'Mettre à jour le patient' : 'Créer un nouveau patient'}</DialogTitle>
      <DialogContent>
        <Stack spacing={2}>
          {error && <Alert severity="error">{error}</Alert>}
          {patientData && (
            <TextField
              label="Numéro de sécurité sociale"
              value={patientData.noSS}
              fullWidth
              disabled
            />
          )}
          <TextField
            label="Nom"
            value={nom}
            onChange={(e) => setNom(e.target.value)}
            fullWidth
            required
          />
          <TextField
            label="Prénom"
            value={prenom}
            onChange={(e) => setPrenom(e.target.value)}
            fullWidth
            required
          />
          <TextField
            label="Date de naissance"
            type="date"
            value={dateNaissance}
            onChange={(e) => setDateNaissance(e.target.value)}
            fullWidth
            InputLabelProps={{ shrink: true }}
            required
          />
          <FormControl fullWidth required>
            <InputLabel>Sexe</InputLabel>
            <Select value={sexe} onChange={(e) => setSexe(e.target.value)}>
              <MenuItem value="Homme">Homme</MenuItem>
              <MenuItem value="Femme">Femme</MenuItem>
            </Select>
          </FormControl>
          <TextField
            label="E-mail"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            fullWidth
            required
          />
          <FormControl fullWidth>
            <InputLabel>Diététicien</InputLabel>
            <Select
              value={dieteticien}
              onChange={(e) => setDieteticien(e.target.value)}
            >
              {dieteticiens.map((diet) => (
                <MenuItem key={diet.noSS} value={diet.noSS}>
                  {diet.nom} {diet.prenom}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <FormControl fullWidth>
            <InputLabel>Allergies</InputLabel>
            <Select
              multiple
              value={allergies}
              onChange={(e) => setAllergies(e.target.value)}
              input={<OutlinedInput label="Allergies" />}
              renderValue={(selected) => selected.join(', ')}
            >
              {allergiesOptions.map((allergy) => (
                <MenuItem key={allergy.nomAllergene} value={allergy.nomAllergene}>
                  <Checkbox checked={allergies.indexOf(allergy.nomAllergene) > -1} />
                  <ListItemText primary={allergy.nomAllergene} />
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} variant="outlined">
          Annuler
        </Button>
        <Button onClick={handleSubmit} variant="contained" color="primary">
          {patientData ? 'Mettre à jour' : 'Créer'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default PatientDialog;
