import React from 'react';
import { Box } from '@mui/material';
import TopBar from '../..//components/TopBar';

function Layout({ children }) {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      {/* Topbar */}
      <TopBar />

      {/* Page Content */}
      <Box sx={{ flex: 1, padding: 2, backgroundColor: '#f5f5f5' }}>
        {children}
      </Box>
    </Box>
  );
}

export default Layout;
