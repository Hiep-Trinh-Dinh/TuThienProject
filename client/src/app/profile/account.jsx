import {
  Snackbar,
  Alert
} from "@mui/material";
import { useState, useEffect } from "react";
import "./account.css"

function AccountSection(){
    
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

                    <div className="mt-3 relative flex flex-row text-center gap-5">
                            <div className="w-45 relative flex flex-col items-center text-foreground">
                                <h2 className="">Projects</h2>
                                <p className="text-red-500 text-xl font-bold">0</p>
                            </div>

                            <div className="w-45 relative flex flex-col items-center text-foreground border-r-1 border-l-1 border-l-neutral-400 border-r-neutral-400">
                                <h2 className="pl-15 pr-15">Organizations</h2>
                                <p className="text-red-500 text-xl font-bold">0</p>
                            </div>

                            <div className="w-45 relative flex flex-col items-center text-foreground">
                                <h2 className="">Amount Donated</h2>
                                <p className="text-red-500 text-xl font-bold">0đ</p>
                            </div>
                    </div>

                    <div className="w-130 mt-10 relative flex flex-col items-center text-center">
                        <h1 className="text-4xl font-bold">Supported Projects</h1>
                        <p className="mt-5 text-gray-500">
                            You haven't supported any projects yet. 
                            Explore our projects and find one that matches your interests to make a difference!</p>
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
export default AccountSection;