"use client"

import { useState } from "react"
import { Button } from "../ui/button"
import { Input } from "../ui/input"
import { Label } from "../ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"
import { Alert, AlertDescription } from "../ui/alert"
import { Link, useNavigate } from "react-router-dom"
import api from "../../axiosConfig"

export function RegisterForm() {

  const [form, setForm] = useState({email:"",
                                    passwordHash:"",
                                    fullName:""});
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name] : e.target.value});
  }
  const [confirmPassword , setConfirmPassword] = useState("")
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(error)
    let min = 6;

    if (form.passwordHash !== confirmPassword) {
      setError("Passwords do not match")
      return
    }
    
    if (form.passwordHash.length < min) {
      setError(`Password must be at least ${min} characters`)
      return
    }

    setLoading(true)
    try {
      const res = await api.post("/accounts/register", form);
      navigate("/login");
    } catch (error) {
      if(error.response && error.response.status === 403){
        setError("User already exists");
      }else{
        setError("Signup failed. Please try again");
      }
    }
    
  }

  return (
    <Card className="w-full max-w-md mx-auto">
      <CardHeader className="text-center">
        <CardTitle className="text-2xl font-bold text-primary">Join Our Mission</CardTitle>
        <CardDescription>Create an account to start making a difference</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <Alert variant="destructive">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          <div className="space-y-2">
            <Label htmlFor="fullName">Full Name</Label>
            <Input
              id="fullName"
              type="text"
              name="fullName"
              value={form.fullName}
              onChange={handleChange}
              placeholder="Enter your full name"
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="Enter your email"
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="passwordHash"
              name="passwordHash"
              type="password"
              value={form.passwordHash}
              onChange={handleChange}
              placeholder="Create a password"
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="confirmPassword">Confirm Password</Label>
            <Input
              id="confirmPassword"
              type="password"
              name="confirmPassword"
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm your password"
              required
            />
          </div>

          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? "Creating account..." : "Create Account"}
          </Button>

          <div className="text-center text-sm text-muted-foreground">
            Already have an account?{" "}
            <Link to="/login" className="text-primary hover:underline">
              Sign in here
            </Link>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}

export default RegisterForm
