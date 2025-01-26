import React, {useContext} from 'react';
import { Typography, Box, Button, Stack } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import Layout from '../components/layouts/Layout';
import { AuthContext } from '../contexts/AuthContext';
import { Roles } from '../utils/utils';

function UnauthorizedPage() {
  const navigate = useNavigate();
  const { user } = useContext(AuthContext); 

  const handleNavigation = (path) => {
    navigate(path);
    handleMenuClose();
  };
  
  return (
    <Layout>
      <Box textAlign="center" mt={5}>
        <Typography variant="h4" color="error" gutterBottom>
          Accès Refusé
        </Typography>
        <Typography variant="body1">
          Vous n'avez pas les permissions nécessaires pour accéder à cette page.
        </Typography>
        <Stack direction="row" spacing={2} justifyContent="center" mt={3}>
          <Button variant="contained" color="primary" 
            onClick={() => {
              switch (user.role) {
                case Roles.ADMIN:
                  handleNavigation(`/admin/${user.noss}`);
                  break;
                case Roles.DIETETICIEN:
                  handleNavigation(`/dieteticiens/${user.noss}`);
                  break;
                case Roles.INFIRMIER:
                  handleNavigation(`/infirmiers/${user.noss}`);
                  break;
                case Roles.PATIENT:
                  handleNavigation(`/patients/${user.noss}`);
                  break;
              }
            }}>
            Retour au dashboard
          </Button>

          <Button variant="outlined" color="primary" onClick={() => navigate('/')}>
            Retour à l'accueil
          </Button>
        </Stack>
        
      </Box>
    </Layout>
  );
}

export default UnauthorizedPage;
