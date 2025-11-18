"use client"

import { createContext, useContext, useState, useEffect } from "react"
import api from "../axiosConfig";

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    const storedUser = localStorage.getItem("charity-user")
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser))
      } catch (e) {
        console.error("Invalid stored user:", e)
        localStorage.removeItem("charity-user")
      }
    }
    setLoading(false);
  }, [])

  const login = async (token) => {
    localStorage.setItem("token", token)
    const res = await api.get("/accounts/info");
    const userData = res.data.result;
    setUser(userData)
    // Lưu vào localStorage để dùng khi reload trang
    localStorage.setItem("charity-user", JSON.stringify(userData));
    return userData;
  }


  const register = (userData) => {
    setUser(userData)
    localStorage.setItem("charity-user", JSON.stringify(userData))
  }

  const logout = async (token) => {
    setUser(null)
    const res = await api.post("/auth/logout",{token});
    localStorage.removeItem("charity-user")
    localStorage.removeItem("token")
  }

  const value = {
    user,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    loading,
  }
  
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
