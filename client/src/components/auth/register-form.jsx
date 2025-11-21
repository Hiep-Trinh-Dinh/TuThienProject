"use client"
import {
  Snackbar,
  Alert
} from "@mui/material";
import { useState } from "react"
import { Button } from "../ui/button"
import { Input } from "../ui/input"
import { Label } from "../ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"
import { Link, useNavigate } from "react-router-dom"
import api from "../../axiosConfig"

export function RegisterForm() {
  // Validating Regex
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  const nameRegex = /^[A-ZÀ-Ỹ][a-zà-ỹ]+(?: [A-ZÀ-Ỹ][a-zà-ỹ]+)+$/;
  // Password min length = 6
  const pwdRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,}$/;
  const [strength, setStrength] = useState(0);
  const [strengthColor, setStrengthColor] = useState("transparent");
  const [form, setForm] = useState({email:"",
                                    passwordHash:"",
                                    fullName:""});
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [e.target.name] : e.target.value});
    if (name === "passwordHash") evaluateStrength(value);
  }
  const [confirmPassword , setConfirmPassword] = useState("")
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate();
  // password strength bar
  const evaluateStrength = (pwd) => {
    let score = 0;
    if (pwd.length >= 6) score++;
    if (/[A-Z]/.test(pwd)) score++;
    if (/[!@#$%^&*]/.test(pwd)) score++;

    setStrength(score);

    if (score === 0) setStrengthColor("transparent");
    else if (score === 1) setStrengthColor("#ef4444"); // red
    else if (score === 2) setStrengthColor("#facc15"); // yellow
    else if (score === 3) setStrengthColor("#22c55e"); // green
  };

  const handleSubmit = async (e) => {
    e.preventDefault()
    let min = 6;
    let accept = true
    setLoading(true)

    if(form.fullName.trim() === ""){
      setSnackBarMessage("Full name field cannot be empty.");
      setSnackErrorBarOpen(true);
      accept = false
    }else if(!nameRegex.test(form.fullName)){
      setSnackBarMessage("Your full name is not real.");
      setSnackErrorBarOpen(true);
      accept = false
    }else if(form.email.trim() === ""){
      setSnackBarMessage("Email address cannot be empty.");
      setSnackErrorBarOpen(true);
      accept = false
    }else if(!emailRegex.test(form.email)){
      setSnackBarMessage("Email address is invalid format.\n Example: user@example.com");
      setSnackErrorBarOpen(true);
      accept = false
    }else if(form.passwordHash.trim() === ""){
      setSnackBarMessage("Password field cannot be empty.");
      setSnackErrorBarOpen(true);
      accept = false
    }else if(!pwdRegex.test(form.passwordHash)){
      setSnackBarMessage(`Password must be at least ${min} characters long.\n` +
                          "Contains at least 1 uppercase letter.\n" +
                          "Contains at least 1 symbol (e.g., !@#$%^&*).");
      setSnackErrorBarOpen(true);
      accept =  false
    }else if (form.passwordHash !== confirmPassword) {
      setSnackBarMessage(`Confirm password does not match`);
      setSnackErrorBarOpen(true);
      accept = false
    }
    
    if(accept){
      try {
        const res = await api.post("/accounts/register", form);
        localStorage.setItem("userEmail",form.email)
        localStorage.setItem("userPwd",form.passwordHash)
        navigate("/login");
      } catch (error) {
        if(error.response && error.response.status === 403){
          setSnackBarMessage(`User already exists`);
          setSnackErrorBarOpen(true);
        }else{
          setSnackBarMessage(`Signup failed. Please try again`);
          setSnackErrorBarOpen(true);
        }
      }
    }
    
    setLoading(false)
  }

  const [snackBarSuccessOpen, setSnackSuccessBarOpen] = useState(false);
  const [snackBarErrorOpen, setSnackErrorBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  // cài đặt thêm thông báo error
  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setSnackErrorBarOpen(false)
    setSnackSuccessBarOpen(false);
  };

  return (
  <>
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
        <CardTitle className="text-2xl font-bold text-primary">Join Our Mission</CardTitle>
        <CardDescription>Create an account to start making a difference</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="fullName">Full Name</Label>
            <Input
              id="fullName"
              type="text"
              name="fullName"
              value={form.fullName}
              onChange={handleChange}
              placeholder="Enter your full name"
            />
          </div>

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
              id="passwordHash"
              name="passwordHash"
              type="password"
              value={form.passwordHash}
              onChange={handleChange}
              placeholder="Create a password"
            />
            {/* Password Strength Bar */}
            <div className="w-full h-2 bg-gray-200 rounded mt-1 overflow-hidden">
              <div
                className="h-full transition-all duration-300"
                style={{
                  width: `${(strength / 3) * 100}%`,
                  backgroundColor: strengthColor,
                }}
              ></div>
            </div>
            
            {/* Optional text feedback */}
            {form.passwordHash && (
              <p
                className="text-sm mt-1 font-medium"
                style={{ color: strengthColor }}
              >
                {strength === 1
                  ? "Weak"
                  : strength === 2
                  ? "Medium"
                  : strength === 3
                  ? "Strong"
                  : ""}
              </p>
            )}
            <div className="text-left text-sm text-muted-foreground">
              Password must be at least 6 characters long. <br />
              Contains at least 1 uppercase letter. <br />
              Contains at least 1 symbol (e.g., !@#$%^&*).
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="confirmPassword">Confirm Password</Label>
            <Input
              id="confirmPassword"
              type="password"
              name="confirmPassword"
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm your password"
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
  </>
  )
}

export default RegisterForm
