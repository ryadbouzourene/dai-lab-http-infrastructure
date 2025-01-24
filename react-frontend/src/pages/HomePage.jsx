import React from 'react';
import { Typography, Button, Stack } from '@mui/material';
import { useNavigate } from 'react-router-dom';

function HomePage() {
  const navigate = useNavigate();

  return (
    <Stack
      spacing={2}
      justifyContent="center"
      alignItems="center"
      sx={{ minHeight: '100vh' }}
    >
      <Typography variant="h4">Bienvenue !</Typography>
      <Button
        variant="contained"
        color="primary"
        onClick={() => navigate("/login")}
      >
        Se connecter
      </Button>
    </Stack>
  );
}

export default HomePage;
