import {
  Snackbar,
  Alert
} from "@mui/material";
import { useState, useEffect } from "react";
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { useParams } from "react-router-dom"
import api from "../../axiosConfig";


function ProfileSection(){
    // change info form
    const nameRegex = /^[A-ZÀ-Ỹ][a-zà-ỹ]+(?: [A-ZÀ-Ỹ][a-zà-ỹ]+)+$/;
    const phoneRegex = /^(?:\+?\d{1,3})?[ -]?(?:\d{9,10})$/;
    const {id} = useParams();
    const [form, setForm] = useState({
        fullName: "",
        email: "",
        phone: "",
        status:"",
        createdAt:""});
    const handleChange = (e) => {
        setForm({ ...form, [e.target.name] : e.target.value});
    }
    const userData = JSON.parse(localStorage.getItem("charity-user"));
    useEffect(()=>{
        window.scrollTo(0, 0);
        const fetchUpdateUser = async ()=>{
            try {
                setForm(userData);
            } catch (error) {
                console.error("Error Fetching user",error.message);
            }
        }
        fetchUpdateUser();
    },[id]);
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

    const handleInfoSubmit = async (e) => {
        e.preventDefault();
        let accept = true;

        if(form.fullName.trim() === ""){
            setSnackBarMessage("Full name cannot be empty.")
            setSnackBarErrorOpen(true)
            accept = false
        }else if(!nameRegex.test(form.fullName)){
            setSnackBarMessage("Your full name is not true.")
            setSnackBarErrorOpen(true)
            accept = false
        }
        
        if(form.phone && !phoneRegex.test(form.phone)){
            setSnackBarMessage("Your phone number is not valid.")
            setSnackBarErrorOpen(true)
            accept = false
        }

        if(accept){
            try {
                const res = await api.put(`/accounts/${id}`, form);
                localStorage.removeItem("charity-user");
                localStorage.setItem("charity-user",JSON.stringify(res.data.result));
                setSnackBarMessage("Updated Successfully!")
                setSnackSuccessBarOpen(true)
            } catch (error) {
                if (error.response && error.response.status === 403) {
                    setSnackBarMessage("User already exists")
                    setSnackBarErrorOpen(true)
                } else {
                    setSnackBarMessage("Update failed. Please try again")
                    setSnackBarErrorOpen(true)
                }
            }
        }
        
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
        
        <div className="w-full">
            {/* Profile Header */}
            <div className="w-full">
                {/* Cover Photo */}
                <div className="relative h-48 md:h-60 bg-gray-200">
                </div>
                {/* Avatar + Info */}
                <div className="relative flex flex-col items-center -mt-16 md:-mt-20">
                <div className="relative">
                    <img
                    src="https://givenow.vn/wp-content/themes/funlin-progression-child/images/placeholder/avatar.png"
                    className="w-32 h-32 rounded-full border-4 border-white shadow-md object-cover"
                    /> 
                </div>

                <h2 className="mt-3 text-xl font-bold text-gray-800">{userData.fullName}</h2>

                <div className="mt-4 flex space-x-6 border-b w-full justify-center">
                    <p className="pb-2 border-b-2 border-indigo-600 font-medium text-indigo-600">
                    👤 Thông tin cá nhân
                    </p>
                </div>
                </div>
            </div>

            {/* Profile Form */}
            <div className="container mx-auto px-4 py-8">
                <form
                onSubmit={handleInfoSubmit}
                className="bg-white shadow rounded-lg p-6 md:p-8 max-w-4xl mx-auto grid grid-cols-1 md:grid-cols-2 gap-6"
                >

                {/* Full Name */}
                <div className="space-y-2">
                    <Label htmlFor="fullName" className="font-medium text-gray-700">
                    Full Name
                    </Label>
                    <Input
                    id="fullName"
                    type="text"
                    name="fullName"
                    value={form.fullName}
                    onChange={handleChange}
                    placeholder="Enter your full name"
                    className="rounded-lg"
                    />
                </div>

                {/* Email */}
                <div className="space-y-2">
                    <Label htmlFor="email" className="font-medium text-gray-700">
                    Email
                    </Label>
                    <Input
                    id="email"
                    type="email"
                    name="email"
                    value={form.email}
                    disabled
                    className="rounded-lg bg-gray-100 text-gray-600"
                    />
                </div>

                {/* Phone */}
                <div className="space-y-2">
                    <Label htmlFor="phone" className="font-medium text-gray-700">
                    Phone
                    </Label>
                    <Input
                    id="phone"
                    type="tel"
                    name="phone"
                    value={form.phone ?? ""}
                    onChange={(e) => {
                        const value = e.target.value.replace(/[^0-9]/g, "");
                        setForm({ ...form, phone: value });
                    }}
                    placeholder="Enter your phone number"
                    required
                    pattern="^0[0-9]{9,10}$"
                    title="Phone number must start with 0 and be 10–11 digits"
                    className="rounded-lg"
                    />
                </div>

                {/* Status */}
                <div className="space-y-2">
                    <Label htmlFor="status" className="font-medium text-gray-700">
                    Status
                    </Label>
                    <Input
                    id="status"
                    type="text"
                    name="status"
                    value={form.status}
                    disabled
                    className="rounded-lg bg-gray-100 text-gray-600"
                    />
                </div>

                {/* Created Date */}
                <div className="space-y-2">
                    <Label htmlFor="createdAt" className="font-medium text-gray-700">
                    Created Date
                    </Label>
                    <Input
                    id="createdAt"
                    type="date"
                    name="createdAt"
                    value={form.createdAt ? form.createdAt.split("T")[0] : ""}
                    disabled
                    className="rounded-lg bg-gray-100 text-gray-600"
                    />
                </div>

                {/* Save button full width */}
                <div className="md:col-span-2">
                    <Button
                    type="submit"
                    className="w-full text-white font-semibold py-2 px-4 rounded-lg"
                    >
                    Save
                    </Button>
                </div>
                </form>
            </div>
        </div>
    </>
    )
}
export default ProfileSection;