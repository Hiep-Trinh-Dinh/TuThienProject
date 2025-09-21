"use client"

import { createContext, useContext, useState, useEffect } from "react"

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
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
  }, [])

  const login = (userData) => {
    setUser(userData)
    localStorage.setItem("charity-user", JSON.stringify(userData))
  }

  const register = (userData) => {
    setUser(userData)
    localStorage.setItem("charity-user", JSON.stringify(userData))
  }

  const logout = () => {
    setUser(null)
    localStorage.removeItem("charity-user")
    localStorage.removeItem("token")
  }

  const value = {
    user,
    login,
    register,
    logout,
    isAuthenticated: !!user,
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
