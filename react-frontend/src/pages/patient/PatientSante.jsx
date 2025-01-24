import React, { useState, useEffect } from 'react';
import { Box, Button, Typography, Stack } from '@mui/material';
import { LineChart, PieChart } from '@mui/x-charts';
import SanteList from '../../components/lists/SanteList';
import SanteDialog from '../../components/dialogs/SanteDialog'; // Importer la modale pour saisir les données de santé
import patientService from '../../services/patientService';
import { Roles } from '../../utils/utils';

function PatientSante({ patient, user }) {
  const [donneesSante, setDonneesSante] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isDialogOpen, setIsDialogOpen] = useState(false); // État pour ouvrir/fermer la modale
  const [stats, setStats] = useState({
    poidsMoyen: 0,
    imcMoyen: 0,
    tailleMoyenne: 0,
    tourDeTailleMoyen: 0,
    activiteRepartition: [],
  });

  const niveauxActivite = [
    'Sédentaire',
    'Légèrement actif',
    'Modérément actif',
    'Très actif',
    'Extrêmement actif',
  ];

  const fetchSanteData = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await patientService.getSante(patient.noSS);

      const donneesTransformees = data.map((entry, index) => ({
        id: index + 1,
        noss_patient: entry.noss_patient,
        dateGraph: new Date(entry.date),
        date: entry.date,
        taille: entry.taille,
        poids: entry.poids,
        imc: entry.IMC,
        tour_de_taille: entry.tour_de_taille,
        niveau_activite_physique: entry.niveau_activite_physique,
      }));

      setDonneesSante(donneesTransformees);

      // Calcul des statistiques
      const poidsTotal = donneesTransformees.reduce((sum, entry) => sum + entry.poids, 0);
      const imcTotal = donneesTransformees.reduce((sum, entry) => sum + entry.imc, 0);
      const tailleTotal = donneesTransformees.reduce((sum, entry) => sum + entry.taille, 0);
      const tourDeTailleTotal = donneesTransformees.reduce((sum, entry) => sum + entry.tour_de_taille, 0);

      const activiteCounts = donneesTransformees.reduce((acc, entry) => {
        acc[entry.niveau_activite_physique] = (acc[entry.niveau_activite_physique] || 0) + 1;
        return acc;
      }, {});

      const activiteRepartition = niveauxActivite.map((niveau) => ({
        id: niveau,
        label: niveau,
        value: activiteCounts[niveau] || 0,
      }));

      setStats({
        poidsMoyen: parseFloat((poidsTotal / donneesTransformees.length).toFixed(2)),
        imcMoyen: parseFloat((imcTotal / donneesTransformees.length).toFixed(2)),
        tailleMoyenne: parseFloat((tailleTotal / donneesTransformees.length).toFixed(2)),
        tourDeTailleMoyen: parseFloat((tourDeTailleTotal / donneesTransformees.length).toFixed(2)),
        activiteRepartition,
      });
    } catch (err) {
      console.error('Erreur lors de la récupération des données de santé :', err);
      setError('Impossible de récupérer les données de santé.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSanteData();
  }, [patient.noSS]);

  const handleOpenDialog = () => setIsDialogOpen(true);
  const handleCloseDialog = () => setIsDialogOpen(false);

  if (loading) {
    return <Typography>Chargement des données de santé...</Typography>;
  }

  if (error) {
    return <Typography color="error">{error}</Typography>;
  }

  return (
    <Box>
      {/* Bouton pour saisir une nouvelle donnée */}
      {user.role != Roles.PATIENT ? (<Button
        variant="contained"
        color="primary"
        sx={{ marginBottom: 2 }}
        onClick={handleOpenDialog} // Ouvrir la modale
      >
        Saisir
      </Button>) : null}
    

      {/* Tableau des données de santé */}
      <Typography variant="h6" gutterBottom>
        Liste des données de santé
      </Typography>
      <SanteList
        santeData={donneesSante}
        reload={fetchSanteData}
        allowDelete={true}
      />

      {/* Graphiques */}
      <Typography variant="h6" mt={5} gutterBottom>
        Statistiques des données de santé
      </Typography>
      <Stack direction="row" justifyContent="space-evenly" alignItems="center" mt={3}>
        {/* Graphique linéaire pour le poids et l'IMC */}
        <Stack alignItems="center" spacing={3}>
          <Typography variant="subtitle1" gutterBottom>
            Évolution du poids et de l'IMC
          </Typography>
          <LineChart
            xAxis={[{ dataKey: 'dateGraph', label: 'Date', scaleType: 'time' }]}
            series={[
              { dataKey: 'poids', label: 'Poids (kg)' },
              { dataKey: 'imc', label: 'IMC' },
            ]}
            dataset={donneesSante}
            width={500}
            height={300}
          />
        </Stack>

        {/* Piechart pour la répartition des niveaux d'activité physique */}
        <Stack alignItems="center" spacing={3}>
          <Typography variant="subtitle1" gutterBottom>
            Répartition des niveaux d'activité physique
          </Typography>
          <PieChart
            series={[
              {
                data: stats.activiteRepartition,
                innerLabel: ({ id, value }) => `${id} (${value})`, // Affichage des labels
              },
            ]}
            width={400}
            height={200}
          />
        </Stack>
      </Stack>

      {/* Statistiques moyennes */}
      <Typography variant="h6" mt={5}>
        Statistiques moyennes
      </Typography>
      <Box mt={2}>
        <Typography><strong>Poids moyen :</strong> {stats.poidsMoyen} kg</Typography>
        <Typography><strong>IMC moyen :</strong> {stats.imcMoyen}</Typography>
        <Typography><strong>Taille moyenne :</strong> {stats.tailleMoyenne} cm</Typography>
        <Typography><strong>Tour de taille moyen :</strong> {stats.tourDeTailleMoyen} cm</Typography>
      </Box>

      {/* Modale pour saisir une nouvelle donnée de santé */}
      <SanteDialog
        open={isDialogOpen}
        handleClose={handleCloseDialog}
        onSave={fetchSanteData} // Recharger les données après la saisie
        patientNoSS={patient.noSS}
      />
    </Box>
  );
}

export default PatientSante;
