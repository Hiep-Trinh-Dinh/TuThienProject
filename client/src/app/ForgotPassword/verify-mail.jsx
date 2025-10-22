"use client"

import {
  Snackbar,
  Alert
} from "@mui/material";

import api from "../../axiosConfig";
import { useState } from "react"
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../../components/ui/card"
import { useNavigate } from "react-router-dom"
import "../../components/auth/spinner.css"

export function VerifyMailForm() {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate();
  const [form, setForm] = useState({
    email:"",
  });
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name] : e.target.value});
  }
  // cài đặt thêm thông báo error
  const [snackBarErrorOpen, setSnackBarErrorOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");

  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setSnackBarErrorOpen(false);
  };
  
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
    }

    if(accept === true){
      try {
        const res = await api.post(`/forgotPassword/verifyMail/${form.email}`);
        navigate(`/verifyotp/${form.email}`);
      } catch (error) {
        if(error.response && error.response.status == 400){
          setSnackBarMessage("Email address cannot be empty.");
          setSnackBarErrorOpen(true);
        }else{
          setSnackBarMessage("Verify failed. Please try again.");
          setSnackBarErrorOpen(true);
        }
      }
    }
  
    setLoading(false)
  }
    if (loading) {
        return (<div className="spinner-container">
                <div className="spinner"></div>
                </div>
        );
    }
  return (<>

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
        <CardTitle className="text-2xl font-bold text-primary">FORGOT PASSWORD</CardTitle>
        <CardDescription>
            Please fill in your email address. You will recieve a mail with password reset instruction.
            </CardDescription>
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

          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? "Verifying..." : "Get New Password"}
          </Button>

        </form>
      </CardContent>
    </Card>
</>
  )
}

export default VerifyMailForm
