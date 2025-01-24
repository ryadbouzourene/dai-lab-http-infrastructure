import React, { useState, useEffect, useContext } from "react";
import { Box, Typography, Divider, CircularProgress, Stack } from "@mui/material";
import { PieChart } from "@mui/x-charts";
import Layout from "../components/layouts/Layout";
import patientService from "../services/patientService";
import infirmierService from "../services/infirmierService";
import repasService from "../services/repasService";
import { useParams } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import { Navigate } from 'react-router-dom';
import { calculateAge, Roles } from '../utils/utils';
import ConsommablesList from "../components/lists/ConsommablesList";

function RepasPage() {
  const { user } = useContext(AuthContext);
  const { noss, date } = useParams();

  const [patient, setPatient] = useState(null);
  const [nurse, setNurse] = useState(null);
  const [meal, setMeal] = useState(null);
  const [consumables, setConsumables] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError("");

        // Récupérer les informations du patient
        const patientData = await patientService.getPatientById(noss);
        setPatient(patientData);

        // Récupérer les informations du repas
        const mealData = await repasService.getRepas(noss, date);
        setMeal(mealData);

        // Récupérer les informations de l'infirmier
        if (mealData.nossInfirmier) {
          const nurseData = await infirmierService.getInfirmierById(mealData.nossInfirmier);
          setNurse(nurseData);
        }

        // Récupérer les consommables associés au repas
        const consumablesData = await repasService.getRepasConsommables(noss, date);
        setConsumables(consumablesData);
      } catch (err) {
        console.error("Erreur lors de la récupération des données :", err);
        setError("Impossible de charger les données.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [noss, date]);

  if (user.role === Roles.PATIENT && noss != user.noss) {
    return <Navigate to="/unauthorized" />;
  }

  if (loading) {
    return (
      <Layout>
        <Box sx={{ padding: 4, textAlign: "center" }}>
          <CircularProgress />
          <Typography>Chargement des données...</Typography>
        </Box>
      </Layout>
    );
  }

  if (error) {
    return (
      <Layout>
        <Box sx={{ padding: 4, textAlign: "center" }}>
          <Typography color="error">{error}</Typography>
        </Box>
      </Layout>
    );
  }

  // Calculer les données nutritionnelles et vitaminiques pour les graphiques
  const nutritionData = [
    { id: "Glucides", value: consumables.reduce((sum, c) => sum + c.glucides, 0), label: "Glucides" },
    { id: "Protéines", value: consumables.reduce((sum, c) => sum + c.proteines, 0), label: "Protéines" },
    { id: "Lipides", value: consumables.reduce((sum, c) => sum + c.lipides, 0), label: "Lipides" },
  ];

  const vitaminData = [
    { id: "Vitamine A", value: consumables.reduce((sum, c) => sum + c.vit_A, 0), label: "Vitamine A" },
    { id: "Vitamine C", value: consumables.reduce((sum, c) => sum + c.vit_C, 0), label: "Vitamine C" },
    { id: "Vitamine D", value: consumables.reduce((sum, c) => sum + c.vit_D, 0), label: "Vitamine D" },
  ];

  return (
    <Layout>
      <Box sx={{ padding: 4 }}>
        {/* Section 1: Informations générales */}
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: 4,
          }}
        >
          {/* Patient Info */}
          <Box sx={{ flex: 1, marginRight: 2 }}>
            <Typography variant="h6">Informations du Patient</Typography>
            <Divider sx={{ marginBottom: 2 }} />
            <Typography>Nom : {patient.nom} {patient.prenom}</Typography>
            <Typography>NoSS : {patient.noSS}</Typography>
            <Typography>Âge : {calculateAge(patient.dateNaissance)} ans</Typography>
            <Typography>Sexe : {patient.sexe}</Typography>
          </Box>

          {/* Nurse Info */}
          <Box sx={{ flex: 1, marginLeft: 2 }}>
            <Typography variant="h6">Informations de l'Infirmier</Typography>
            <Divider sx={{ marginBottom: 2 }} />
            {nurse ? (
              <>
                <Typography>Nom : {nurse.nom} {nurse.prenom}</Typography>
                <Typography>NoSS : {nurse.noSS}</Typography>
              </>
            ) : (
              <Typography>Aucun infirmier enregistré.</Typography>
            )}
          </Box>
        </Box>

        {/* Section 2: Informations du repas */}
        <Box sx={{ marginBottom: 4 }}>
          <Typography variant="h6">Informations du Repas</Typography>
          <Divider sx={{ marginBottom: 2 }} />
          <Typography>Date : {new Date(meal.dateConsommation).toLocaleDateString()}</Typography>
          <Typography>Heure : {new Date(meal.dateConsommation).toLocaleTimeString()}</Typography>
          <Typography>Type : {meal.type}</Typography>
          <Typography>Commentaire : {meal.comment}</Typography>
        </Box>

        {/* Section 3: Liste des consommables */}
        <Box sx={{ marginBottom: 4 }}>
          <Typography variant="h6">Liste des Consommables</Typography>
          <Divider sx={{ marginBottom: 2 }} />
          <ConsommablesList consommablesData={consumables} displayQty={true}/>
        </Box>

        {/* Section 4: Graphiques */}
        <Typography variant="h6">Récapitulatif nutritionnel</Typography>
        <Divider sx={{ marginBottom: 2 }} />
        <Stack direction="row" justifyContent="space-evenly" alignItems="center">
          {/* Graphique des macronutriments */}
          <Stack alignItems="center" spacing={2}>
            <PieChart
              series={[
                {
                  data: nutritionData,
                  innerLabel: ({ id, value }) => `${id}: ${value.toFixed(2)} g`,
                },
              ]}
              width={400}
              height={200}
            />
          </Stack>

          {/* Graphique des vitamines */}
          <Stack alignItems="center" spacing={2}>
            <PieChart
              series={[
                {
                  data: vitaminData,
                  innerLabel: ({ id, value }) => `${id}: ${value.toFixed(2)} mg`,
                },
              ]}
              width={400}
              height={200}
            />
          </Stack>
        </Stack>
      </Box>
    </Layout>
  );
}

export default RepasPage;
