import React, { useState, useEffect } from 'react';
import { Box, Typography, Button, Stack } from '@mui/material';
import { LineChart, PieChart } from '@mui/x-charts';
import RepasList from '../../components/lists/RepasList';
import patientService from '../../services/patientService';

const intervals = [
  { label: "Aujourd'hui", value: "0 day" },
  { label: "Une semaine", value: "1 week" },
  { label: "Un mois", value: "1 month" },
  { label: "Une année", value: "1 year" },
  { label: "Tout", value: null },
];

function PatientRepas({ patient }) {
  const [repas, setRepas] = useState([]);
  const [interval, setInterval] = useState(intervals[0]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [nutritionData, setNutritionData] = useState([]);
  const [hydrationData, setHydrationData] = useState([]);
  const [caloriesData, setCaloriesData] = useState([]);

  const fetchRepas = async (selectedInterval) => {
    setLoading(true);
    setError('');
    try {
      const repasData = await patientService.getRepas(patient.noSS, selectedInterval.value);
      setRepas(repasData);

      // Traitement des données pour les graphiques
      const nutritionStats = repasData.reduce(
        (acc, repas) => {
          acc.glucides += repas.totalGlucides || 0;
          acc.lipides += repas.totalLipides || 0;
          acc.proteines += repas.totalProteines || 0;
          return acc;
        },
        { glucides: 0, lipides: 0, proteines: 0 }
      );

      setNutritionData([
        { id: 0, value: nutritionStats.glucides, label: 'Glucides' },
        { id: 1, value: nutritionStats.lipides, label: 'Lipides' },
        { id: 2, value: nutritionStats.proteines, label: 'Protéines' },
      ]);

      // Agréger les données par jour
      const groupedByDate = repasData.reduce((acc, repas) => {
        const date = repas.dateConsommation.split('T')[0]; // Garder uniquement la date (yyyy-MM-dd)
        if (!acc[date]) {
          acc[date] = { date, calories: 0, hydratation: 0 };
        }
        acc[date].calories += repas.totalCalories || 0;
        acc[date].hydratation += repas.totalHydratation || 0;
        return acc;
      }, {});

      const aggregatedData = Object.values(groupedByDate);

      setCaloriesData(aggregatedData.map(({ date, calories }) => ({ date: new Date(date), calories })));
      setHydrationData(aggregatedData.map(({ date, hydratation }) => ({ date: new Date(date), hydratation })));
    } catch (err) {
      console.error('Erreur lors de la récupération des repas :', err);
      setError('Impossible de récupérer les repas.');
    } finally {
      setLoading(false);
    }
  };

  // Charger les repas au changement d'intervalle
  useEffect(() => {
    fetchRepas(interval);
  }, [interval, patient.noSS]);

  return (
    <Box>
      {/* Boutons pour les intervalles */}
      <Stack direction="row" justifyContent="center" spacing={10} sx={{ marginBottom: 2 }}>
        {intervals.map((i) => (
          <Button
            key={i.label}
            variant={interval === i ? 'contained' : 'outlined'}
            onClick={() => setInterval(i)}
          >
            {i.label}
          </Button>
        ))}
      </Stack>

      {error && <Typography color="error">{error}</Typography>}

      {loading ? (
        <Typography>Chargement des repas...</Typography>
      ) : (
        <>
          {/* Liste des repas */}
          <Typography variant="h6" mt={5} mb={2} gutterBottom>
            Repas consommés ({interval.label})
          </Typography>
          <RepasList repas={repas} reload={() => fetchRepas(interval)} allowDelete={true}/>

          {/* Graphiques */}
          <Typography variant="h6" mt={5} mb={2} gutterBottom>
            Statistiques nutritionnelles ({interval.label})
          </Typography>
          <Stack direction="row" spacing={4} alignItems="center">
            <PieChart
              series={[{ data: nutritionData }]}
              width={400}
              height={200}
            />

            <LineChart
              xAxis={[{ dataKey: 'date', label: 'Date', scaleType: 'time' }]} // Axe X en échelle temporelle
              series={[{ dataKey: 'calories', label: 'Calories' }]}
              dataset={caloriesData}
              width={500}
              height={300}
            />
            <LineChart
              xAxis={[{ dataKey: 'date', label: 'Date', scaleType: 'time' }]} // Axe X en échelle temporelle
              series={[{ dataKey: 'hydratation', label: 'Hydratation (ml)' }]}
              dataset={hydrationData}
              width={500}
              height={300}
            />
          </Stack>
        </>
      )}
    </Box>
  );
}

export default PatientRepas;
