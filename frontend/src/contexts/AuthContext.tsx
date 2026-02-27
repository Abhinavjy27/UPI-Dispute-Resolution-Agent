import React, { createContext, useContext, useState, useEffect } from 'react';

interface AuthContextType {
  phone: string | null;
  login: (phoneNumber: string) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [phone, setPhone] = useState<string | null>(null);

  // Load phone from localStorage on mount
  useEffect(() => {
    const savedPhone = localStorage.getItem('userPhone');
    if (savedPhone) {
      setPhone(savedPhone);
    }
  }, []);

  const login = (phoneNumber: string) => {
    setPhone(phoneNumber);
    localStorage.setItem('userPhone', phoneNumber);
  };

  const logout = () => {
    setPhone(null);
    localStorage.removeItem('userPhone');
  };

  return (
    <AuthContext.Provider value={{ 
      phone, 
      login, 
      logout, 
      isAuthenticated: !!phone 
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
