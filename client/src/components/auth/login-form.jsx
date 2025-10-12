"use client"

import {
  Snackbar,
  Alert
} from "@mui/material";

import api from "../../axiosConfig"
import { useState } from "react"
import { API_BASE_URL } from "../../../eslint.config"
import { Button } from "../ui/button"
import { Input } from "../ui/input"
import { Label } from "../ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"
import { Link,useNavigate } from "react-router-dom"
import { useAuth } from "../../contexts/auth-context"

export function LoginForm() {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  const { login } = useAuth()
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate();
  const [form, setForm] = useState({
    email: localStorage.getItem("userEmail") || "",
    password: localStorage.getItem("userPwd") || ""
  });
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name] : e.target.value});
  }
  // cài đặt thêm thông báo error
  const [snackBarErrorOpen, setSnackBarErrorOpen] = useState(false);
  const [snackBarSuccessOpen, setSnackSuccessBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");

  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setSnackBarErrorOpen(false);
    setSnackSuccessBarOpen(false);
  };

  if(localStorage.getItem("userEmail") 
    && localStorage.getItem("userPwd")){
      setSnackBarMessage("Signing up successfully!")
      setSnackSuccessBarOpen(true)
      localStorage.removeItem("userEmail")
      localStorage.removeItem("userPwd")
  }
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    let accept = true
    setLoading(true)

    if(form.email.trim() === ""){
      setSnackBarMessage("Email address field cannot be empty.");
      setSnackBarErrorOpen(true);
      accept = false
    }else if(!emailRegex.test(form.email)){
      setSnackBarMessage("Email address is invalid format.\nExample: user@example.com");
      setSnackBarErrorOpen(true);
      accept = false
    }else if(form.password.trim() === ""){
      setSnackBarMessage("Password field cannot be empty.");
      setSnackBarErrorOpen(true);
      accept = false
    }

    if(accept === true){
      try {
        const res = await api.post("/auth/login", form);
        login(res.data.token);
        navigate("/");
      } catch (error) {
        if(error.response && error.response.status == 400){
          setSnackBarMessage("Email address or password cannot be empty.");
          setSnackBarErrorOpen(true);
        }else{
          setSnackBarMessage("Login failed. Please try again.");
          setSnackBarErrorOpen(true);
        }
      }
    }
  
    setLoading(false)
  }

  return (<>
    <Snackbar
      open={snackBarSuccessOpen}
      onClose={handleCloseSnackBar}
      autoHideDuration={6000}
      anchorOrigin={{ vertical: "top", horizontal: "right" }}
    >
      <Alert
        onClose={handleCloseSnackBar}
        severity="success"
        variant="filled"
        sx={{ width: "100%" }}
      >
        {snackBarMessage}
      </Alert>
    </Snackbar>

    <Snackbar
      open={snackBarErrorOpen}
      onClose={handleCloseSnackBar}
      autoHideDuration={6000}
      anchorOrigin={{ vertical: "top", horizontal: "right" }}
    >
      <Alert
        onClose={handleCloseSnackBar}
        severity="error"
        variant="filled"
        sx={{ width: "100%" }}
      >
        {snackBarMessage}
      </Alert>
    </Snackbar>
  
    <Card className="w-full max-w-md mx-auto">
      <CardHeader className="text-center">
        <CardTitle className="text-2xl font-bold text-primary">Welcome Back</CardTitle>
        <CardDescription>Sign in to your account to continue supporting causes</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="text"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="Enter your email"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="Enter your password"
              
            />
          </div>

          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? "Signing in..." : "Sign In"}
          </Button>

          
          <div className="text-center text-sm text-muted-foreground">
            <a href = {`${API_BASE_URL}/oauth2/authorization/google`} className="text-primary hover:underline">
              Login with Google
            </a>
          </div>
          {/* <div className="text-center text-sm text-muted-foreground">
            <a href = {`${API_BASE_URL}/oauth2/authorization/facebook`} className="text-primary hover:underline">
              Login with Facebook
            </a>
          </div> */}

          <div className="text-center text-sm text-muted-foreground">
            Don't have an account?{" "}
            <Link to="/register" className="text-primary hover:underline">
              Sign up here
            </Link>
          </div>

          <div className="text-xs text-muted-foreground bg-muted p-3 rounded">
            <strong>Demo credentials:</strong>
            <br />
            Email: john@example.com
            <br />
            Password: password123
          </div>
        </form>
      </CardContent>
    </Card>
</>
  )
}

export default LoginForm
