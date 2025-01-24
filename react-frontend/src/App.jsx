import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import DieteticiensPage from './pages/dieteticien/DieteticiensPage';
import InfirmiersPage from './pages/infirmier/InfirmiersPage';
import PatientsPage from './pages/patient/PatientsPage';
import RepasPage from './pages/RepasPage';
import ConsommablesPage from './pages/ConsommablesPage';
import AdminPage from './pages/admin/AdminPage';
import DieteticienPage from './pages/dieteticien/DieteticienPage';
import InfirmierPage from './pages/infirmier/InfirmierPage';
import PatientPage from './pages/patient/PatientPage';
import NotFoundPage from './pages/NotFoundPage';
import UnauthorizedPage from './pages/UnauthorizedPage';

import PrivateRoute from './components/routes/PrivateRoute';
import { Roles } from './utils/utils'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Routes publiques */}
          <Route path="/" element={<HomePage />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />
          <Route path="/login" element={<LoginPage />} />

          {/* Routes privées */}
          <Route path="/admin/:noss" element={<PrivateRoute allowedRoles={[Roles.ADMIN]}><AdminPage /></PrivateRoute>} />
          <Route path="/dieteticiens" element={<PrivateRoute allowedRoles={[Roles.ADMIN, Roles.DIETETICIEN]}><DieteticiensPage /></PrivateRoute>} />
          <Route path="/dieteticiens/:noss" element={<PrivateRoute allowedRoles={[Roles.ADMIN, Roles.DIETETICIEN]}><DieteticienPage /></PrivateRoute>} />
          <Route path="/infirmiers" element={<PrivateRoute allowedRoles={[Roles.ADMIN, Roles.DIETETICIEN, Roles.INFIRMIER]}><InfirmiersPage /></PrivateRoute>} />
          <Route path="/infirmiers/:noss" element={<PrivateRoute allowedRoles={[Roles.ADMIN, Roles.DIETETICIEN, Roles.INFIRMIER]}><InfirmierPage /></PrivateRoute>} />
          <Route path="/patients" element={<PrivateRoute allowedRoles={[Roles.ADMIN, Roles.DIETETICIEN, Roles.INFIRMIER]}><PatientsPage /></PrivateRoute> } />
          <Route path="/patients/:noss" element={<PrivateRoute allowedRoles={[Roles.ADMIN, Roles.DIETETICIEN, Roles.INFIRMIER, Roles.PATIENT]}><PatientPage /></PrivateRoute>} />
          <Route path="/patients/:noss/repas/:date" element={<PrivateRoute allowedRoles={[Roles.ADMIN, Roles.DIETETICIEN, Roles.INFIRMIER, Roles.PATIENT]}><RepasPage /></PrivateRoute>} />
          <Route path="/consommables" element={<PrivateRoute allowedRoles={[Roles.ADMIN, Roles.DIETETICIEN, Roles.INFIRMIER, Roles.PATIENT]}><ConsommablesPage /></PrivateRoute>} />
        
          {/* Erreur 404 */}
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
