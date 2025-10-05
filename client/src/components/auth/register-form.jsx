"use client"

import { useState } from "react"
// import { useAuth } from "../../contexts/auth-context"
import { Button } from "../ui/button"
import { Input } from "../ui/input"
import { Label } from "../ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"
import { Alert, AlertDescription } from "../ui/alert"
import { Link, useNavigate } from "react-router-dom"
import api from "../../axiosConfig"
import { useAuth } from "../../contexts/auth-context"

export function RegisterForm() {

  const [form, setForm] = useState({email:"",
                                    passwordHash:"",
                                    fullName:""});
  const { register } = useAuth();
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name] : e.target.value});
  }
  // const [email, setEmail] = useState("")
  // const [password, setPassword] = useState("")
  // const [name, setName] = useState("")
  const [confirmPassword , setConfirmPassword] = useState("")
  const [error, setError] = useState("")
  // const [loading, setLoading] = useState(false)
  // const { register } = useAuth()

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError("")

    if (form.passwordHash !== confirmPassword) {
      setError("Passwords do not match")
      return
    }

    // if (password.length < 6) {
    //   setError("Password must be at least 6 characters")
    //   return
    // }

    // setLoading(true)
    // const result = await register(email, password, name)
    try {
      const res = await api.post("/accounts/register", form);
      navigate("/login");
    } catch (error) {
      if(error.response && error.response.status === 403){
        alert("User already exists");
      }else{
        alert("Signup failed. Please try again");
      }
    }
    // if (!result.success) {
    //   setError(result.error)
    // }

    
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

          {/* <Button type="submit" className="w-full" disabled={loading}>
            {loading ? "Creating account..." : "Create Account"}
          </Button> */}
          <Button type="submit" className="w-full" >
            Create Account
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
