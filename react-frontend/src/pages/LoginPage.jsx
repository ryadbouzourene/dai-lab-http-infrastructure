import React, { useState, useContext } from 'react';
import { TextField, Button, Stack, Typography, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import authService from '../services/authService';
import { AuthContext } from '../contexts/AuthContext';
import { Roles } from '../utils/utils';

function LoginPage() {
  const navigate = useNavigate();
  const { updateUser } = useContext(AuthContext);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [touched, setTouched] = useState({ email: false, password: false });
  const [errorMessage, setErrorMessage] = useState('');

  const handleLogin = async () => {
    try {
      const data = await authService.login(email, password);
      
      updateUser(data); 
    
      if (data.role === Roles.PATIENT) {
        navigate(`/patients/${data.noss}`);
      } else if (data.role === Roles.INFIRMIER) {
        navigate(`/infirmiers/${data.noss}`);
      } else if (data.role === Roles.DIETETICIEN) {
        navigate(`/dieteticiens/${data.noss}`);
      } else if (data.role === Roles.ADMIN) {
        navigate(`/admin/${data.noss}`);
      } else {
        navigate('/unauthorized');
      }
    } catch (error) {
      setErrorMessage("Identifiants incorrects ou problème réseau");
    }
  };

  return (
    <Stack
      display="flex"
      justifyContent="center"
      alignItems="center"
      minHeight="100vh"
      bgcolor="#f5f5f5"
    >
      <Paper
        elevation={3}
        sx={{
          padding: '2rem',
          maxWidth: '400px',
          width: '100%',
          textAlign: 'center',
        }}
      >
        <Typography variant="h5" gutterBottom>
          Connexion
        </Typography>

        {errorMessage && (
          <Typography color="error" variant="body2" mb={2}>
            {errorMessage}
          </Typography>
        )}

        <Stack
          component="form"
          display="flex"
          gap={2}
          alignItems="center"
        >
          <TextField
            label="Email"
            type="email"
            variant="outlined"
            size="small"
            fullWidth
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onBlur={() => setTouched({ ...touched, email: true })}
            error={touched.email && !email}
            helperText={touched.email && !email ? "L'email est requis" : ""}
          />

          <TextField
            label="Mot de passe"
            type="password"
            variant="outlined"
            size="small"
            fullWidth
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onBlur={() => setTouched({ ...touched, password: true })}
            error={touched.password && !password}
            helperText={touched.password && !password ? "Le mot de passe est requis" : ""}
          />

          <Button
            variant="contained"
            color="primary"
            onClick={handleLogin}
            fullWidth
            disabled={!email || !password}
          >
            Se connecter
          </Button>
        </Stack>
      </Paper>
    </Stack>
  );
}

export default LoginPage;
