import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../../components/ui/card"
import { Alert, AlertDescription } from "../../components/ui/alert"
import { useState } from "react";
import { useParams } from "react-router-dom";
import api from "../../axiosConfig";

function ChangePassword(){
    const [error, setError] = useState("")
    const {id} = useParams();
    const [confirmPassword , setConfirmPassword] = useState("")

    // change password form
    const [formPwd, setFormPwd] = useState({
        currentPassword: "",
        newPassword: ""});
    const handleChangePwd = (e) => {
        setFormPwd({ ...formPwd, [e.target.name] : e.target.value});
    }
    const handleChangePwdSubmit = async (e) => {
        e.preventDefault()
        setError("")
        if (formPwd.newPassword !== confirmPassword) {
            setError("Passwords do not match")
            return
        }
        try {
            const res = await api.patch(`/accounts/password/${id}`, formPwd);
            localStorage.removeItem("charity-user");
            localStorage.setItem("charity-user",JSON.stringify(res.data));
            alert("Successfully");
            console.log(formPwd.newPassword)
            } catch (error) {
            if(error.response && error.response.status === 403){
                alert("Current password is not correct");
            }else{
                alert("Changing failed. Please try again");
            }
        }
    }

    return(
        <>
    <Card className="w-full max-w-md mx-auto">
                <CardHeader className="text-center">
                    <CardTitle className="text-2xl font-bold text-primary">Change Password</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleChangePwdSubmit} className="space-y-4">
                    {error && (
                        <Alert variant="destructive">
                        <AlertDescription>{error}</AlertDescription>
                        </Alert>
                    )}
                    <div className="space-y-2">
                        <Label htmlFor="currentPassword">Current Password</Label>
                        <Input
                        id="currentPassword"
                        name="currentPassword"
                        type="password"
                        value={formPwd.currentPassword}
                        onChange={handleChangePwd}
                        placeholder="Type in current password"
                        required
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
                        required
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="confirmPassword">Confirm New Password</Label>
                        <Input
                        id="confirmPassword"
                        type="password"
                        name="confirmPassword"
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        placeholder="Confirm your password"
                        required
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