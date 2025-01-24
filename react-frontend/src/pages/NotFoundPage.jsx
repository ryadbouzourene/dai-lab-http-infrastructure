import React from 'react';
import { Typography, Box, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import Layout from '../components/layouts/Layout'

function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <Layout>
      <Box textAlign="center" mt={5}>
        <Typography variant="h4" color="error" gutterBottom>
          Page introuvable (404)
        </Typography>
        <Typography variant="body1" gutterBottom>
          La page que vous recherchez n'existe pas ou a été déplacée.
        </Typography>
        <Button variant="contained" color="primary" onClick={() => navigate('/')}>
          Retour à l'accueil
        </Button>
      </Box>
    </Layout>
  );
}

export default NotFoundPage;
