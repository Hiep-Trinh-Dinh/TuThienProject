import {
  Snackbar,
  Alert
} from "@mui/material";
import { useState } from "react";
import "./account.css"

function PendingCampSection(){
    
    const userData = JSON.parse(localStorage.getItem("charity-user"));
    window.scrollTo(0, 0);
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

                    <div className="w-180 mt-10 relative flex flex-col items-center text-center">
                        <h1 className="text-3xl font-bold">The ambassador project is pending approval</h1>
                        <p className="mt-5 text-gray-500">
                            Discover projects tailored for you and recommendations that match your interests.</p>
                    </div>
                    
                    <div class="mt-8 mb-5 relative flex flex-col items-center text-center">
                        <div class="mb-5">
                            <img src="https://givenow.vn/wp-content/themes/funlin-progression-child/images/no-records.png" alt="..."></img>
                        </div>

                        <div>
                            <a href="/projects" class="button">DISCOVER OUR PROJECTS</a>
                        </div>
                    </div>          
                </div>
            </div>
        </div>
    </>
    )
}
export default PendingCampSection;