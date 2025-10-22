"use client"

import {
  Snackbar,
  Alert
} from "@mui/material";

import api from "../../axiosConfig";
import { useState, useRef, useEffect  } from "react"
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from "../../components/ui/card";
import { Input } from "../../components/ui/input";
import { Button } from "../../components/ui/button";
import { Label } from "../../components/ui/label";
import { useNavigate } from "react-router-dom"
import { useParams } from "react-router-dom"


export function VerifyOtpForm() {
    const [otp, setOtp] = useState(["", "", "", "", "", ""]);
    const [timer, setTimer] = useState(60);
    const inputRefs = useRef([]);
    const navigate = useNavigate();
    const {email} = useParams();

    // Đếm ngược 60s
    useEffect(() => {
    if (timer > 0) {
        const countdown = setInterval(() => setTimer((t) => t - 1), 1000);
        return () => clearInterval(countdown);
    }
    }, [timer]);


    const handleChange = (e, index) => {
    const value = e.target.value.replace(/\D/, ""); // chỉ nhận số
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    // tự động nhảy sang ô tiếp theo
    if (value && index < otp.length - 1) {
        inputRefs.current[index + 1].focus();
    }
    };

    const handleKeyDown = (e, index) => {
    if (e.key === "Backspace" && !otp[index] && index > 0) {
        inputRefs.current[index - 1].focus();
    }
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

    const handleSubmit = async (e) => {
        e.preventDefault();
        const code = otp.join("");
        if (code.length === 6) {
            try {
                const res = await api.post(`/forgotPassword/verifyOtp/${code}/${email}`);
                navigate(`/changepassword/${email}`);
            } catch (error) {
                if(error.response && error.response.status == 400){
                    setSnackBarMessage("Verify failed. Please try again.");
                    setSnackBarErrorOpen(true);
                }
            } 
        }else{
            setSnackBarMessage("Vui lòng nhập đủ 6 số OTP!");
            setSnackBarErrorOpen(true);
        }
    }

    const handleResend = async (e) => {
        e.preventDefault();
        setTimer(60);
        setOtp(["", "", "", "", "", ""]);
        const res = await api.post(`/forgotPassword/verifyMail/${email}`);
    };

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
  
    <Card className="w-full max-w-md mx-auto shadow-lg border rounded-2xl">
      <CardHeader className="text-center">
        <CardTitle className="text-2xl font-bold text-primary">OTP Verification</CardTitle>
        <CardDescription>
            OTP has been sent to your email. Please enter it soon before it expires in {timer}s.
        </CardDescription>
      </CardHeader>

      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="flex justify-center gap-3">
            {otp.map((value, index) => (
              <Input
                key={index}
                ref={(el) => (inputRefs.current[index] = el)}
                type="text"
                maxLength="1"
                value={value}
                onChange={(e) => handleChange(e, index)}
                onKeyDown={(e) => handleKeyDown(e, index)}
                className="w-12 h-12 text-center text-lg font-semibold border-2 border-gray-300 rounded-lg focus:border-primary focus:ring-primary transition-all"
              />
            ))}
          </div>

          <Button type="submit" className="w-full">
            Verify OTP
          </Button>

          <div className="text-center text-sm text-muted-foreground">
            {timer > 0 ? (
              <p>OTP expires in <span className="font-medium">{timer}s</span></p>
            ) : (
              <button
                type="button"
                onClick={handleResend}
                className="text-primary hover:underline"
              >
                Resend OTP
              </button>
            )}
          </div>
        </form>
      </CardContent>

      <CardFooter className="text-center text-xs text-gray-500">
        Please ensure Checking in Spame if not received OTP.
      </CardFooter>
    </Card>
</>
  )
}

export default VerifyOtpForm
