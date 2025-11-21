import {
  Snackbar,
  Alert
} from "@mui/material";
import { useState, useEffect } from "react";
import "./account.css"
import { ProjectCard } from "../../components/projects/project-card"
import { Pagination } from "../../components/ui/pagination"
import projectService from "../../services/projectService"
import {getTotalDonationsByDonor, getTotalAmountDonationsByDonor} from "../../services/donationService"

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

    ////////////////////////////////

    const [projects, setProjects] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    const [projectsTotal, setProjectsTotal] = useState(0);
    const [organizaionsTotal, setOrganizaionsTotal] = useState(0);
    const [amountTotal, setAmountTotal] = useState(0);
    
    // Pagination state (0-based page index)
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [totalElements, setTotalElements] = useState(0)
    const [size] = useState(6)
    const userId = userData.userId;

    function formatCurrency(amount, currency = "VND", locale = "vi-VN") {
        return new Intl.NumberFormat(locale, {
            style: "currency",
            currency: currency,
        }).format(amount);
    }

    // Load thong ke.
    useEffect(() => {
        const loadDataSta = async () => {
            const totalAmount = await getTotalAmountDonationsByDonor(userId);

            const totalDonations = await getTotalDonationsByDonor(userId);

            const totalOrganizations = await projectService.getTotalProjectsByUserId(userId);
            
            totalAmount ? setAmountTotal(totalAmount) : setAmountTotal(0);

            totalDonations  ? setProjectsTotal(totalDonations) : setProjectsTotal(0);
            
            totalOrganizations ? setOrganizaionsTotal(totalOrganizations) : setOrganizaionsTotal(0);
        }
        loadDataSta();
    },[])

    // Load projects with advanced search and pagination
    useEffect(() => {
    const loadProjects = async () => {
        try {
        setLoading(true)
        setError(null)
        const response = await projectService.getDonatedProjects(
            currentPage,
            size,
            userId
        )     

        // Fallback: nếu API không trả về kiểu phân trang (content/totalPages/totalElements)
        if (Array.isArray(response)) {
            const total = response.length
            const pages = Math.ceil(total / size)
            const sliceStart = currentPage * size
            const sliceEnd = sliceStart + size
            setProjects(response.slice(sliceStart, sliceEnd))
            setTotalPages(pages)
            setTotalElements(total)
        } else {
            setProjects(response.content || [])
            setTotalPages(response.totalPages ?? 0)
            setTotalElements(response.totalElements ?? (response.content ? response.content.length : 0))
        }
        } catch (err) {
        setError(err.message)
        console.error('Error loading projects:', err)
        } finally {
        setLoading(false)
        }
    }

    const timeoutId = setTimeout(loadProjects, 300)
    return () => clearTimeout(timeoutId)
    }, [currentPage, size])

    // Reset to first page when filters change
    useEffect(() => {
    setCurrentPage(0)
    }, [])

    const handlePageChange = (page) => {
    if (page < 0 || page >= totalPages) return
    setCurrentPage(page)
    try { window.scrollTo({ top: 0, behavior: 'smooth' }) } catch {}
    }

    // Default images
    const avatarPreview = userData.avatarUrl;
    const coverPreview = userData.coverPhotoUrl;
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
        
        <div className="w-full">
            {/* Profile Header */}
            <div className="w-full">
                {/* Cover Photo */}
                <div className="relative h-48 md:h-60 bg-gray-200">
                    <img
                        src={coverPreview || defaultCover}
                        alt="Cover"
                        className="w-full h-full object-cover"
                    />
                </div>
                {/* Avatar + Info */}
                <div className="relative flex flex-col items-center -mt-16 md:-mt-20">
                    <div className="relative">
                        <img
                        src={avatarPreview || defaultAvatar}
                        className="w-32 h-32 rounded-full border-4 border-white shadow-md object-cover"
                        alt="Avatar"
                        /> 
                    </div>

                    <h2 className="mt-3 text-xl font-bold text-gray-800">{userData.fullName}</h2>

                    <div className="mt-3 relative flex flex-row text-center gap-5">
                            <div className="w-45 relative flex flex-col items-center text-foreground">
                                <h2 className="">Supported Projects</h2>
                                <p className="text-red-500 text-xl font-bold">{projectsTotal}</p>
                            </div>

                            <div className="w-45 relative flex flex-col items-center text-foreground border-r-1 border-l-1 border-l-neutral-400 border-r-neutral-400">
                                <h2 className="pl-15 pr-15">Organizations</h2>
                                <p className="text-red-500 text-xl font-bold">{organizaionsTotal}</p>
                            </div>

                            <div className="w-45 relative flex flex-col items-center text-foreground">
                                <h2 className="">Amount Donated</h2>
                                <p className="text-red-500 text-xl font-bold">{formatCurrency(amountTotal)}</p>
                            </div>
                    </div>
                        <h1 className="text-4xl font-bold mt-5">Supported Projects</h1>
                    
                        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

                        {loading ? (
                            <div className="text-center py-12">
                            <div className="text-6xl mb-4">⏳</div>
                            <h3 className="text-xl font-semibold text-foreground mb-2">Đang tải dự án...</h3>
                            <p className="text-muted-foreground">Vui lòng chờ trong khi chúng tôi tải dữ liệu dự án.</p>
                            </div>
                        ) : error ? (
                            <div className="text-center py-12">
                            <div className="text-6xl mb-4">❌</div>
                            <h3 className="text-xl font-semibold text-foreground mb-2">Lỗi tải dự án</h3>
                            <p className="text-muted-foreground mb-4">{error}</p>
                            <button 
                                onClick={() => window.location.reload()} 
                                className="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90"
                            >
                                Thử lại
                            </button>
                            </div>
                        ) : projects.length === 0 ? (
                            <>
                                <div className="relative flex flex-col items-center text-center justify-content-center">
                                <p className="mt-5 text-gray-500">
                                    You haven't supported any projects yet. 
                                    Explore our projects and find one that matches your interests to make a difference!</p>
                                </div>
                                
                                <div className="mt-8 mb-5 relative flex flex-col items-center text-center">
                                    <div className="mb-5">
                                        <img src="https://givenow.vn/wp-content/themes/funlin-progression-child/images/no-records.png" alt="..."></img>
                                    </div>

                                    <div>
                                        <a href="/projects" className="button">DISCOVER OUR PROJECTS</a>
                                    </div>
                                </div>
                            </>
                        ) : (
                            <>
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                                {projects.map((project) => (
                                <ProjectCard key={project.projectId} project={project} />
                                ))}
                            </div>

                            {totalPages > 1 && (
                                <Pagination
                                currentPage={currentPage}
                                totalPages={totalPages}
                                onPageChange={handlePageChange}
                                totalElements={totalElements}
                                size={size}
                                />
                            )}
                            </>
                        )}
                    </div>                  
                </div>
            </div>

        </div>
    </>
    )
}
export default AccountSection;