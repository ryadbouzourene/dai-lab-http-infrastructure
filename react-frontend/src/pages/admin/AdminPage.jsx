import React, { useContext } from 'react';
import { Box, Typography, Button, Stack } from '@mui/material';
import Layout from '../../components/layouts/Layout';
import { AuthContext } from '../../contexts/AuthContext';
import { useParams } from "react-router-dom";
import { Navigate } from 'react-router-dom';

function AdminPage() {
  const { user } = useContext(AuthContext);
  const { noss } = useParams();

  if (noss != user.noss) {
    return <Navigate to="/unauthorized" />;
  }

  return (
    <Layout>
      <Box>
        <Typography variant="h5">Informations générales</Typography>
        {/* Infos générales et bouton pour modifier */}
        <Box>
          <Typography><strong>Numéro de sécurité sociale :</strong> {user.noss}</Typography>
          <Typography><strong>E-mail :</strong> {user.username}</Typography>
          <Typography><strong>Role :</strong> {user.role}</Typography>
        </Box>
      </Box>
    </Layout>
  );
}

export default AdminPage;
