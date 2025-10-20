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
import { useParams } from "react-router-dom"


export function ChangePasswordForm() {
  // Regex
    const pwdRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,}$/;
    const [strength, setStrength] = useState(0);
    const [strengthColor, setStrengthColor] = useState("transparent");
    const {email} = useParams();
    const navigator = useNavigate();
    // change password form
    const [formPwd, setFormPwd] = useState({
        password: "",
        repeatedPassword: ""});
    const handleChangePwd = (e) => {
        const { name, value } = e.target;
        setFormPwd({ ...formPwd, [e.target.name] : e.target.value});
        if (name === "password") evaluateStrength(value);
    }

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

    // cài đặt thêm thông báo error
    const [snackBarErrorOpen, setSnackBarErrorOpen] = useState(false);
    const [snackBarMessage, setSnackBarMessage] = useState("");

    const handleCloseSnackBar = (event, reason) => {
        if (reason === "clickaway") {
        return;
        }

        setSnackBarErrorOpen(false);
    };

    const handleChangePwdSubmit = async (e) => {
        e.preventDefault()
        let accept = true
        let min = 6
        
        if(formPwd.password.trim() === ""){
            setSnackBarMessage("The password field cannot be empty.");
            setSnackBarErrorOpen(true);
            accept = false
        }else if(formPwd.repeatedPassword.trim() === ""){
            setSnackBarMessage("The repeated password field cannot be empty.");
            setSnackBarErrorOpen(true);
            accept = false
        }else if(!pwdRegex.test(formPwd.password)){
            setSnackBarMessage(`Password must be at least ${min} characters long.\n` +
            "Contains at least 1 uppercase letter.\n" +
            "Contains at least 1 symbol (e.g., !@#$%^&*).");
            setSnackBarErrorOpen(true);
            accept =  false
        }else if (formPwd.password.trim() !== formPwd.repeatedPassword.trim()){
            setSnackBarMessage("Your repeated password does not match your password!");
            setSnackBarErrorOpen(true);
            accept =  false
        }

        if(accept){
            try {
                const res = await api.post(`/forgotPassword/changePassword/${email}`, formPwd);
                navigator("/login");
                } catch (error) {
                if(error.response && error.response.status === 403){
                    setSnackBarMessage("Invalid Forgot Password Request!");
                    setSnackBarErrorOpen(true);
                }else{
                    setSnackBarMessage("Changing failed. Please try again.");
                    setSnackBarErrorOpen(true);
                }
            }
        }
    }

    return(
        <>
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
                    <CardTitle className="text-2xl font-bold text-primary">Change Password</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleChangePwdSubmit} className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="password">New Password</Label>
                        <Input
                        id="password"
                        name="password"
                        type="password"
                        value={formPwd.password}
                        onChange={handleChangePwd}
                        placeholder="Type in new password"
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
                        {formPwd.password && (
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
                        <Label htmlFor="repeatedPassword">Confirm New Password</Label>
                        <Input
                        id="repeatedPassword"
                        name="repeatedPassword"
                        type="password"
                        value={formPwd.repeatedPassword}
                        onChange={handleChangePwd}
                        placeholder="Confirm your password"
                        />
                    </div>

                    <Button type="submit" className="w-full" >
                       Change Password
                    </Button>           
                    </form>
                </CardContent>
            </Card>
            </>
    )
}
export default ChangePasswordForm
