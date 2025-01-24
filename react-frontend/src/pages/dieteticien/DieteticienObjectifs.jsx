import React from 'react';
import { Box, Stack, Typography } from '@mui/material';
import { PieChart } from '@mui/x-charts';
import ObjectifsList from '../../components/lists/ObjectifsList';

function DieteticienObjectifs({ objectifs }) {
  // Calculer les proportions des statuts
  const objectifsStatus = {
    terminé: objectifs.filter((obj) => new Date(obj.date_fin) < new Date()).length,
    encours: objectifs.filter(
      (obj) => new Date(obj.date_debut) <= new Date() && new Date(obj.date_fin) >= new Date()
    ).length,
    avenir: objectifs.filter((obj) => new Date(obj.date_debut) > new Date()).length,
  };

  // Calculer les proportions des résultats pour les objectifs terminés
  const objectifsTermines = objectifs.filter((obj) => new Date(obj.date_fin) < new Date());
  const objectifsResultats = {
    réussi: objectifsTermines.filter((obj) => obj.reussi === true).length,
    échoué: objectifsTermines.filter((obj) => obj.reussi === false).length,
  };

  return (
    <>
    <Box sx={{ bgcolor: 'white'}}>
      {/* Tableau des objectifs */}
      <ObjectifsList objectifs={objectifs}/>
    </Box>

      {/* Graphiques */}
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
  );
}

export default DieteticienObjectifs;
