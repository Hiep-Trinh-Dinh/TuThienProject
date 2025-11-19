import {
  Snackbar,
  Alert
} from "@mui/material";
import { useState, useEffect, useRef } from "react";
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { useParams } from "react-router-dom"
import api from "../../axiosConfig";

function EditProfileSection(){
    // change info form
    const nameRegex = /^[A-ZÀ-Ỹ][a-zà-ỹ]+(?: [A-ZÀ-Ỹ][a-zà-ỹ]+)+$/;
    const phoneRegex = /^(?:\+?\d{1,3})?[ -]?(?:\d{9,10})$/;
    const userData = JSON.parse(localStorage.getItem("charity-user"));
    const {id} = useParams();
    
    const [form, setForm] = useState({
        fullName: "",
        email: "",
        phone: "",
        status:"",
        createdAt:"",
        avatarUrl: "",
        coverPhotoUrl: ""
    });

    // Refs for file inputs
    const avatarInputRef = useRef(null);
    const coverInputRef = useRef(null);

    // States for image previews
    const [avatarPreview, setAvatarPreview] = useState("");
    const [coverPreview, setCoverPreview] = useState("");

    // Loading states
    const [avatarUploading, setAvatarUploading] = useState(false);
    const [coverUploading, setCoverUploading] = useState(false);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name] : e.target.value});
    }

    useEffect(()=>{
        window.scrollTo(0, 0);
        const fetchUpdateUser = async ()=>{
            try {
                setForm(userData);
                // Set preview images from existing data
                if (userData.avatar) {
                    setAvatarPreview(userData.avatar);
                }
                if (userData.coverPhoto) {
                    setCoverPreview(userData.coverPhoto);
                }
            } catch (error) {
                console.error("Error Fetching user",error.message);
            }
        }
        fetchUpdateUser();
    },[id]);

    // Snackbar states
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

    // Avatar upload handlers
    const handleAvatarClick = () => {
        avatarInputRef.current?.click();
    };

    const handleCoverClick = () => {
        coverInputRef.current?.click();
    };

    const handleAvatarChange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Validate file type
        if (!file.type.startsWith('image/')) {
            setSnackBarMessage("Please select a valid image file");
            setSnackBarErrorOpen(true);
            return;
        }

        // Validate file size (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            setSnackBarMessage("Image size should be less than 5MB");
            setSnackBarErrorOpen(true);
            return;
        }

        setAvatarUploading(true);

        try {
            // Create preview
            const previewUrl = URL.createObjectURL(file);
            setAvatarPreview(previewUrl);

            // Upload to server
            const formData = new FormData();
            formData.append('image', file);
            formData.append('type', 'avatar');

            const response = await api.post('/accounts/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            // Update form with new avatar URL
            const avatarUrl = response.data.url;
            setForm((prev) => ({ ...prev, avatarUrl: avatarUrl }));
            setSnackBarMessage("Avatar updated successfully!");
            setSnackSuccessBarOpen(true);

        } catch (error) {
            console.error("Error uploading avatar:", error);
            setSnackBarMessage("Failed to upload avatar. Please try again.");
            setSnackBarErrorOpen(true);
            // Revert preview on error
            setAvatarPreview(form.avatarUrl);
        } finally {
            setAvatarUploading(false);
        }
    };

    const handleCoverChange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Validate file type
        if (!file.type.startsWith('image/')) {
            setSnackBarMessage("Please select a valid image file");
            setSnackBarErrorOpen(true);
            return;
        }

        // Validate file size (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            setSnackBarMessage("Image size should be less than 5MB");
            setSnackBarErrorOpen(true);
            return;
        }

        setCoverUploading(true);

        try {
            // Create preview
            const previewUrl = URL.createObjectURL(file);
            setCoverPreview(previewUrl);

            // Upload to server
            const formData = new FormData();
            formData.append('image', file);
            formData.append('type', 'cover');

            const response = await api.post('/accounts/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            
            // Update form with new cover URL
            const coverUrl = response.data.url;
            setForm((prev) => ({ ...prev, coverPhotoUrl: coverUrl }));
            setSnackBarMessage("Cover photo updated successfully!");
            setSnackSuccessBarOpen(true);

        } catch (error) {
            console.error("Error uploading cover:", error);
            setSnackBarMessage("Failed to upload cover photo. Please try again.");
            setSnackBarErrorOpen(true);
            // Revert preview on error
            setCoverPreview(form.coverPhotoUrl);
        } finally {
            setCoverUploading(false);
        }
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
                console.log(form);
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

    // Default images
    const defaultAvatar = "https://givenow.vn/wp-content/themes/funlin-progression-child/images/placeholder/avatar.png";
    const defaultCover = "/api/placeholder/1200/400";

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
        
        {/* Hidden file inputs */}
        <input
            type="file"
            ref={avatarInputRef}
            onChange={handleAvatarChange}
            accept="image/*"
            style={{ display: 'none' }}
        />
        <input
            type="file"
            ref={coverInputRef}
            onChange={handleCoverChange}
            accept="image/*"
            style={{ display: 'none' }}
        />
        
        <div className="w-full">
            {/* Profile Header */}
            <div className="w-full">
                {/* Cover Photo */}
                <div 
                    className="relative h-48 md:h-60 bg-gray-200 cursor-pointer group"
                    onClick={handleCoverClick}
                >
                    {coverPreview || form.coverPhotoUrl ? (
                        <img
                            src={coverPreview || form.coverPhotoUrl}
                            alt="Cover"
                            className="w-full h-full object-cover"
                        />
                    ) : (
                        <div className="w-full h-full bg-gradient-to-r from-blue-400 to-purple-500 flex items-center justify-center">
                            <span className="text-white text-lg font-medium">Click to upload cover photo</span>
                        </div>
                    )}
                    
                    {/* Upload overlay */}
                    <div className="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition-all duration-300 flex items-center justify-center opacity-0 group-hover:opacity-100">
                        <div className="text-white text-center">
                            {coverUploading ? (
                                <div className="flex items-center gap-2">
                                    <div className="w-6 h-6 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                    <span>Uploading...</span>
                                </div>
                            ) : (
                                <>
                                    <div className="text-2xl mb-1">📷</div>
                                    <p className="font-medium">Change Cover Photo</p>
                                </>
                            )}
                        </div>
                    </div>
                </div>

                {/* Avatar + Info */}
                <div className="relative flex flex-col items-center -mt-16 md:-mt-20">
                    <div className="relative">
                        <div 
                            className="relative cursor-pointer group"
                            onClick={handleAvatarClick}
                        >
                            <img
                                src={avatarPreview || form.avatarUrl || defaultAvatar}
                                className="w-32 h-32 rounded-full border-4 border-white shadow-md object-cover"
                                alt="Avatar"
                            />
                            
                            {/* Upload overlay */}
                            <div className="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-40 rounded-full transition-all duration-300 flex items-center justify-center opacity-0 group-hover:opacity-100">
                                {avatarUploading ? (
                                    <div className="w-8 h-8 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                ) : (
                                    <div className="text-white text-center">
                                        <div className="text-xl">📷</div>
                                        <p className="text-xs font-medium mt-1">Change</p>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    <h2 className="mt-3 text-xl font-bold text-gray-800">{userData.fullName}</h2>

                    <div className="mt-4 flex space-x-6 border-b w-full justify-center">
                        <p className="pb-2 border-b-2 border-indigo-600 font-medium text-indigo-600">
                        👤 Personal Information
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
                    className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200"
                    >
                    Save Changes
                    </Button>
                </div>
                </form>
            </div>
        </div>
    </>
    )
}

export default EditProfileSection;