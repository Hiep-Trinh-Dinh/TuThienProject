"use client"

import { createContext, useContext, useState, useEffect } from "react"

const AuthContext = createContext({})

// Mock user data for demonstration
const mockUsers = [
  {
    id: 1,
    email: "john@example.com",
    password: "password123",
    name: "John Doe",
    joinDate: "2024-01-15",
  },
  {
    id: 2,
    email: "jane@example.com",
    password: "password123",
    name: "Jane Smith",
    joinDate: "2024-02-20",
  },
]

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // Check for stored user session
    const storedUser = localStorage.getItem("charity-user")
    if (storedUser) {
      setUser(JSON.parse(storedUser))
    }
    setLoading(false)
  }, [])

  const login = async (email, password) => {
    // Mock authentication
    const foundUser = mockUsers.find((u) => u.email === email && u.password === password)

    if (foundUser) {
      const userSession = { ...foundUser }
      delete userSession.password // Remove password from session
      setUser(userSession)
      localStorage.setItem("charity-user", JSON.stringify(userSession))
      return { success: true }
    }

    return { success: false, error: "Invalid email or password" }
  }

  const register = async (email, password, name) => {
    // Check if user already exists
    const existingUser = mockUsers.find((u) => u.email === email)
    if (existingUser) {
      return { success: false, error: "User already exists" }
    }

    // Create new user
    const newUser = {
      id: mockUsers.length + 1,
      email,
      name,
      joinDate: new Date().toISOString().split("T")[0],
    }

    mockUsers.push({ ...newUser, password })

    const userSession = { ...newUser }
    setUser(userSession)
    localStorage.setItem("charity-user", JSON.stringify(userSession))

    return { success: true }
  }

  const logout = () => {
    setUser(null)
    localStorage.removeItem("charity-user")
  }

  const value = {
    user,
    login,
    register,
    logout,
    loading,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
