import React, { useState, useContext } from 'react';
import { AppBar, Toolbar, Typography, IconButton, Button, Menu, MenuItem } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import { Roles } from '../utils/utils';

function TopBar() {
  const [anchorEl, setAnchorEl] = useState(null);
  const { user, logout } = useContext(AuthContext); 
  const navigate = useNavigate();

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleNavigation = (path) => {
    navigate(path);
    handleMenuClose();
  };

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <IconButton
          edge="start"
          color="inherit"
          aria-label="menu"
          sx={{ mr: 2 }}
          onClick={handleMenuOpen}
        >
          <MenuIcon />
        </IconButton>
        <Typography variant="h6" sx={{ flexGrow: 1 }}>
          Suivi Diététique
        </Typography>
        <Button color="inherit" onClick={handleLogout}>
          Déconnexion
        </Button>

        {/* Menu déroulant */}
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleMenuClose}
        >
          <MenuItem onClick={() => {
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
            Dashboard
          </MenuItem>
          <MenuItem onClick={() => handleNavigation('/dieteticiens')}>
            Diététiciens
          </MenuItem>
          <MenuItem onClick={() => handleNavigation('/infirmiers')}>
            Infirmiers
          </MenuItem>
          <MenuItem onClick={() => handleNavigation('/patients')}>
            Patients
          </MenuItem>
          <MenuItem onClick={() => handleNavigation('/consommables')}>
            Consommables et allergènes
          </MenuItem>
        </Menu>
      </Toolbar>
    </AppBar>
  );
}

export default TopBar;
