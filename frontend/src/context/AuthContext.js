import React, { createContext, useContext, useState } from 'react';
import api from '../api/axios';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('resqnet_user');
    return stored ? JSON.parse(stored) : null;
  });

  const login = async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const { token, name, email: userEmail, role } = res.data;
    localStorage.setItem('resqnet_token', token);
    const userObj = { name, email: userEmail, role };
    localStorage.setItem('resqnet_user', JSON.stringify(userObj));
    setUser(userObj);
    return userObj;
  };

  const register = async (payload) => {
    const res = await api.post('/auth/register', payload);
    const { token, name, email, role } = res.data;
    localStorage.setItem('resqnet_token', token);
    const userObj = { name, email, role };
    localStorage.setItem('resqnet_user', JSON.stringify(userObj));
    setUser(userObj);
    return userObj;
  };

  const logout = () => {
    localStorage.removeItem('resqnet_token');
    localStorage.removeItem('resqnet_user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
