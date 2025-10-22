import {
  Snackbar,
  Alert
} from "@mui/material";
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card"
import { useState } from "react";
import { useParams } from "react-router-dom";
import api from "../../axiosConfig";

function ChangePassword(){
    // Regex
    const pwdRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,}$/;
    const [strength, setStrength] = useState(0);
    const [strengthColor, setStrengthColor] = useState("transparent");
    const {id} = useParams();
    const [confirmPassword , setConfirmPassword] = useState("")

    // change password form
    const [formPwd, setFormPwd] = useState({
        currentPassword: "",
        newPassword: ""});
    const handleChangePwd = (e) => {
        const { name, value } = e.target;
        setFormPwd({ ...formPwd, [e.target.name] : e.target.value});
        if (name === "newPassword") evaluateStrength(value);
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
    const [snackBarSuccessOpen, setSnackSuccessBarOpen] = useState(false);
    const [snackBarMessage, setSnackBarMessage] = useState("");

    const handleCloseSnackBar = (event, reason) => {
        if (reason === "clickaway") {
        return;
        }

        setSnackBarErrorOpen(false);
        setSnackSuccessBarOpen(false);
    };

    const handleChangePwdSubmit = async (e) => {
        e.preventDefault()
        let accept = true
        let min = 6
        
        if(formPwd.currentPassword.trim() === ""){
            setSnackBarMessage("Current password cannot be empty.");
            setSnackBarErrorOpen(true);
            accept = false
        }else if(formPwd.newPassword.trim() === ""){
            setSnackBarMessage("New password cannot be empty.");
            setSnackBarErrorOpen(true);
            accept = false
        }else if(!pwdRegex.test(formPwd.newPassword)){
            setSnackBarMessage(`Password must be at least ${min} characters long.\n` +
            "Contains at least 1 uppercase letter.\n" +
            "Contains at least 1 symbol (e.g., !@#$%^&*).");
            setSnackBarErrorOpen(true);
            accept =  false
        }else if (formPwd.newPassword.trim() === formPwd.currentPassword.trim()){
            setSnackBarMessage("Your new password cannot be as same as your former password!");
            setSnackBarErrorOpen(true);
            accept =  false
        }else if(confirmPassword.trim() === ""){
            setSnackBarMessage("Confirm password cannot be empty.");
            setSnackBarErrorOpen(true);
            accept = false  
        }else if (formPwd.newPassword !== confirmPassword) {
            setSnackBarMessage("Passwords do not match");
            setSnackBarErrorOpen(true);
            accept = false
        }

        if(accept){
            try {
                const res = await api.patch(`/accounts/password/${id}`, formPwd);
                setSnackBarMessage("Successfully");
                setSnackSuccessBarOpen(true);
                } catch (error) {
                if(error.response && error.response.status === 403){
                    setSnackBarMessage("Current password is not correct");
                    setSnackBarErrorOpen(true);
                }else{
                    setSnackBarMessage("Changing failed. Please check your current password again. It might be not correct");
                    setSnackBarErrorOpen(true);
                }
            }
        }
    }

    return(
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
                    <CardTitle className="text-2xl font-bold text-primary">Change Password</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleChangePwdSubmit} className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="currentPassword">Current Password</Label>
                        <Input
                        id="currentPassword"
                        name="currentPassword"
                        type="password"
                        value={formPwd.currentPassword}
                        onChange={handleChangePwd}
                        placeholder="Type in current password"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="newPassword">New Password</Label>
                        <Input
                        id="newPassword"
                        name="newPassword"
                        type="password"
                        value={formPwd.newPassword}
                        onChange={handleChangePwd}
                        placeholder="Create a new password"
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
                        {formPwd.newPassword && (
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
                        <Label htmlFor="confirmPassword">Confirm New Password</Label>
                        <Input
                        id="confirmPassword"
                        type="password"
                        name="confirmPassword"
                        onChange={(e) => setConfirmPassword(e.target.value)}
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
export default ChangePassword