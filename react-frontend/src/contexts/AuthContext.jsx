import React, { createContext, useState, useEffect } from 'react';
import authService from '../services/authService';

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const verifySession = async () => {
      try {
        const sessionData = await authService.checkSession();
        if (sessionData) {
          setUser(sessionData);
        } else {
          setUser(null); // Si non authentifié, on réinitialise
        }
      } catch (error) {
        console.error('Erreur lors de la vérification de session :', error);
        setUser(null);
      } finally {
        setLoading(false); // La vérification est terminée
      }
    };

    verifySession();
  }, []);

  const updateUser = (data) => {
    setUser(data);
  };

  const logout = async () => {
    try {
      await authService.logout();
      setUser(null);
    } catch (error) {
      console.error('Erreur lors de la déconnexion :', error);
    }
  };

  return (
    <AuthContext.Provider value={{ user, updateUser, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}
