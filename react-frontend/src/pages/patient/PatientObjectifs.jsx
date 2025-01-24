import React, { useState, useEffect } from 'react';
import { Box, Typography, Stack } from '@mui/material';
import { PieChart } from '@mui/x-charts';
import ObjectifsList from '../../components/lists/ObjectifsList';
import patientService from '../../services/patientService';

function PatientObjectifs({ patient }) {
  const [objectifs, setObjectifs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchObjectifs = async () => {
    setLoading(true);
    setError('');
    try {
      const objectifsData = await patientService.getObjectifs(patient.noSS);
      setObjectifs(objectifsData);
    } catch (err) {
      console.error('Erreur lors de la récupération des objectifs :', err);
      setError('Impossible de récupérer les objectifs.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchObjectifs();
  }, [patient.noSS]);

  // Calcul des statistiques
  const objectifsStatus = {
    terminé: objectifs.filter((obj) => new Date(obj.date_fin) < new Date()).length,
    encours: objectifs.filter(
      (obj) => new Date(obj.date_debut) <= new Date() && new Date(obj.date_fin) >= new Date()
    ).length,
    avenir: objectifs.filter((obj) => new Date(obj.date_debut) > new Date()).length,
  };

  const objectifsTermines = objectifs.filter((obj) => new Date(obj.date_fin) < new Date());
  const objectifsResultats = {
    réussi: objectifsTermines.filter((obj) => obj.reussi === true).length,
    échoué: objectifsTermines.filter((obj) => obj.reussi === false).length,
  };

  return (
    <Box>
      {/* Gestion des erreurs et du chargement */}
      {loading ? (
        <Typography>Chargement des objectifs...</Typography>
      ) : error ? (
        <Typography color="error">{error}</Typography>
      ) : (
        <>
          {/* Liste des objectifs */}
          <Typography variant="h6" gutterBottom>
            Liste des objectifs
          </Typography>
          <ObjectifsList
            objectifs={objectifs}
          />

          {/* Statistiques des objectifs */}
          <Typography variant="h6" mt={5} gutterBottom>
            Statistiques des objectifs
          </Typography>
          <Stack direction="row" justifyContent="space-evenly" alignItems="center" mt={3}>
            {/* Piechart des statuts */}
            <Stack alignItems="center" spacing={3}>
              <Typography variant="subtitle1" gutterBottom>
                Proportion des statuts
              </Typography>
              <PieChart
                series={[
                  {
                    data: [
                      { id: 'Terminé', value: objectifsStatus.terminé, label: 'Terminé' },
                      { id: 'En cours', value: objectifsStatus.encours, label: 'En cours' },
                      { id: 'À venir', value: objectifsStatus.avenir, label: 'À venir' },
                    ],
                    innerLabel: ({ id, value }) => `${id} (${value})`,
                  },
                ]}
                width={400}
                height={200}
              />
            </Stack>

            {/* Piechart des résultats */}
            <Stack alignItems="center" spacing={3}>
              <Typography variant="subtitle1" gutterBottom>
                Proportion des résultats des objectifs terminés
              </Typography>
              <PieChart
                series={[
                  {
                    data: [
                      { id: 'Réussi', value: objectifsResultats.réussi, label: 'Réussi' },
                      { id: 'Échoué', value: objectifsResultats.échoué, label: 'Échoué' },
                    ],
                    innerLabel: ({ id, value }) => `${id} (${value})`,
                  },
                ]}
                width={400}
                height={200}
              />
            </Stack>
          </Stack>
        </>
      )}
    </Box>
  );
}

export default PatientObjectifs;
