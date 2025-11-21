import {
  Snackbar,
  Alert
} from "@mui/material";
import { useState, useEffect } from "react";

import "./account.css"
import projectService from "../../services/projectService"

function PendingCampSection(){
    
    const userData = JSON.parse(localStorage.getItem("charity-user"));
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

    const [pendingProjectsTotal, setPendingProjectsTotal] = useState(0);
    const [activeProjectsTotal, setActiveProjectsTotal] = useState(0);
    const [organizaionsTotal, setOrganizaionsTotal] = useState(0);
    const [amountTotal, setAmountTotal] = useState(0);

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

            const projectStats = await projectService.getProjectStats(userId);
            
            if (projectStats) {
            setPendingProjectsTotal(projectStats.totalPending)
            setActiveProjectsTotal(projectStats.totalActive)
            setOrganizaionsTotal(projectStats.totalProjects);
            setAmountTotal(projectStats.totalRaisedAmount);
            } else {
                setPendingProjectsTotal(0)
                setActiveProjectsTotal(0)
                setOrganizaionsTotal(0);
                setAmountTotal(0);
            }
        }
        loadDataSta();
    },[])

    const [projects, setProjects] = useState([]);
      const [loading, setLoading] = useState(true);
      const [error, setError] = useState("");
    
      // Form state for creating a new project
      const [form, setForm] = useState({
        title: "",
        description: "",
        imageUrl: "",
        category: "tre_em",
        goalAmount: "",
        startDate: "",
        endDate: "",
      });
      const [creating, setCreating] = useState(false);
      const [createError, setCreateError] = useState("");
      const [showCreateForm, setShowCreateForm] = useState(false);
      const [imageUploading, setImageUploading] = useState(false);
    
      // View/Edit modal states
      const [viewProject, setViewProject] = useState(null);
      const [editProject, setEditProject] = useState(null);
      const [editSaving, setEditSaving] = useState(false);
      const [editError, setEditError] = useState("");
      const [editImageUploading, setEditImageUploading] = useState(false);
    
      // Thêm state preview image local
      const [localImagePreview, setLocalImagePreview] = useState("");
    
      // Thêm state preview ảnh và file ảnh local
      const [selectedFile, setSelectedFile] = useState(null);
      const [previewUrl, setPreviewUrl] = useState("");
    
      const [currentPage, setCurrentPage] = useState(1);
      const itemsPerPage = 6;
      useEffect(() => { setCurrentPage(1); }, [projects.length]);
      const totalPages = Math.ceil(projects.length / itemsPerPage) || 1;
      const currentData = projects.slice((currentPage-1)*itemsPerPage, currentPage*itemsPerPage);
    
      useEffect(() => {
        projectService.getProjectsByUserId(userId)
          .then((data) => setProjects(data))
          .catch((err) => {setError("System data loading error"); setSnackBarMessage("System data loading error")})
          .finally(() => setLoading(false));
      }, []);
    
      // Xử lý effect khi đổi selectedFile (và dọn dẹp Object URL)
      useEffect(() => {
        if (selectedFile) {
          const url = URL.createObjectURL(selectedFile);
          setPreviewUrl(url);
          return () => URL.revokeObjectURL(url);
        } else if (form.imageUrl) {
          setPreviewUrl(form.imageUrl);
        } else {
          setPreviewUrl("");
        }
      }, [selectedFile, form.imageUrl]);
    
      const reloadProjects = async () => {
        setLoading(true);
        try {
          const data = await projectService.getProjectsByUserId(userId);
          setProjects(data);
          console.log(data);
        } catch (e) {
          setError("Project data loading error");
          setSnackBarMessage("Project data loading error");
        } finally {
          setLoading(false);
        }
      };
    
      const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
      };
    
      // Sửa hàm handleImageFileChange để validate url BE trả về và tránh preview lỗi
      const handleImageFileChange = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setSelectedFile(file);
        setImageUploading(true);
        setCreateError("");
        try {
          const result = await projectService.uploadProjectImage(file);
          // Validate: chỉ set preview nếu là url đúng định dạng
          if (
            result.imageUrl &&
            (result.imageUrl.startsWith("http://") || result.imageUrl.startsWith("https://") || result.imageUrl.startsWith("/uploads/"))
          ) {
            setForm((prev) => ({ ...prev, imageUrl: result.imageUrl }));
            setPreviewUrl(result.imageUrl);
            setSelectedFile(null);
          } else {
            setCreateError("Server returns invalid URL: " + (result.imageUrl || "(empty)"));
            // Vẫn giữ preview local tạm thời, KHÔNG set previewUrl bằng giá trị invalid
          }
        } catch (err) {
          setCreateError("Image uploading failed. Please try again.");
          setSnackBarMessage("Image uploading failed. Please try again.");
        } finally {
          setImageUploading(false);
        }
      };
    
      const handleCreate = async (e) => {
        e.preventDefault();
        setCreateError("");
        setCreating(true);
        try {
          // Lấy orgId từ user đang đăng nhập nếu có
          const storedUser = localStorage.getItem("charity-user");
          let orgId = 1;
          if (storedUser) {
            try {
              const parsed = JSON.parse(storedUser);
              orgId = parsed.userId || parsed.id || 1;
            } catch {}
          }
    
          const normalizedImageUrl = (form.imageUrl || "").trim();
          const payload = {
            orgId,
            title: form.title.trim(),
            description: form.description.trim(),
            imageUrl: normalizedImageUrl || null,
            category: form.category, // phải khớp enum: tre_em, y_te, moi_truong, thien_tai, khac
            goalAmount: Number(form.goalAmount || 0),
            startDate: form.startDate || null,
            endDate: form.endDate || null,
            status: "pending",
          };
    
          if (!payload.title) {
            setCreateError("Please fill in the project title.");
            setSnackBarMessage("Please fill in the project title.");
            setCreating(false);
            return;
          }
          if (!payload.goalAmount || payload.goalAmount <= 0) {
            setCreateError("The goal amount must be greater than 0.");
            setSnackBarMessage("The goal amount must be greater than 0.");
            setCreating(false);
            return;
          }
    
          await projectService.createProject(payload);
          // Refresh list
          setLoading(true);
          const data = await projectService.getProjectsByUserId(userId);
          setProjects(data);
          setLoading(false);
          // Reset form
          setForm({ title: "", description: "", imageUrl: "", category: "tre_em", goalAmount: "", startDate: "", endDate: "" });
          setShowCreateForm(false);
        } catch (err) {
          setCreateError("Creating project failed. Please try again.");
          setSnackBarMessage("Creating project failed. Please try again.");
        } finally {
          setCreating(false);
        }
      };
    
      // View details
      const handleView = async (projectId) => {
        try {
          const data = await projectService.getProjectById(projectId);
          setViewProject(data);
        } catch (e) {
          setSnackBarMessage("Cannot load project detail.");
        }
      };
    
      // Delete project
      const handleDelete = async (projectId) => {
        if (!window.confirm("Please confirm to deleting this project.")) return;
        try {
          // Soft delete: load current project, then update status to pending
          const current = await projectService.getProjectById(projectId);
          if (!current) throw new Error('Project not found');
          const payload = {
            orgId: current.orgId,
            title: current.title,
            description: current.description,
            imageUrl: current.imageUrl,
            category: current.category,
            goalAmount: current.goalAmount,
            raisedAmount: current.raisedAmount,
            startDate: current.startDate,
            endDate: current.endDate,
            status: 'pending',
          };
          await projectService.updateProject(projectId, payload);
          await reloadProjects();
        } catch (e) {
          setSnackBarMessage("Updating project status failed.");
        }
      };
    
      // Open Edit
      const openEdit = async (projectId) => {
        setEditError("");
        try {
          const data = await projectService.getProjectById(projectId);
          setEditProject({
            projectId: data.projectId,
            title: data.title || "",
            description: data.description || "",
            imageUrl: data.imageUrl || "",
            category: data.category || "tre_em",
            goalAmount: data.goalAmount || 0,
            startDate: data.startDate || "",
            endDate: data.endDate || "",
            status: data.status || "active",
            orgId: data.orgId, // giữ lại orgId đúng
          });
        } catch (e) {
          setSnackBarMessage("Cannot load data to edit.");
        }
      };
    
      const handleEditImageUpload = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;
        if (!editProject) return;
        setEditImageUploading(true);
        setEditError("");
        try {
          const result = await projectService.uploadProjectImage(file);
          setEditProject((prev) => prev ? ({ ...prev, imageUrl: result.imageUrl }) : prev);
        } catch (err) {
          console.error(err);
          setEditError("Loading image failed. Please try again.");
          setSnackBarMessage("Loading image failed. Please try again.");
        } finally {
          setEditImageUploading(false);
        }
      };
    
      // Save Edit
      const saveEdit = async (e) => {
        e.preventDefault();
        if (!editProject) return;
        setEditSaving(true);
        setEditError("");
        try {
          const normalizedImageUrl = (editProject.imageUrl || "").trim();
          const payload = {
            orgId: editProject.orgId, // giữ nguyên orgId cũ
            title: (editProject.title || "").trim(),
            description: (editProject.description || "").trim(),
            imageUrl: normalizedImageUrl || null,
            category: editProject.category,
            goalAmount: Number(editProject.goalAmount || 0),
            startDate: editProject.startDate || null,
            endDate: editProject.endDate || null,
            status: editProject.status,
          };
          if (!payload.title) {

            setEditError("Title cannot be empty.");
            setSnackBarMessage("Title cannot be empty.");
            setEditSaving(false);
            return;
          }
          await projectService.updateProject(editProject.projectId, payload);
          setEditProject(null);
          await reloadProjects();
        } catch (e) {
          setEditError("Cập nhật thất bại. Vui lòng thử lại");
          setSnackBarMessage("Updating failed. Please try again.");
        } finally {
          setEditSaving(false);
        }
      };

    // Helper component for detail items
    const DetailItem = ({ label, value, badge = false, fullWidth = false }) => (
    <div className={fullWidth ? 'md:col-span-2' : ''}>
        <label className="block font-semibold mb-1 text-gray-700">{label}</label>
        {badge ? (
        <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800 capitalize">
            {value}
        </span>
        ) : (
        <p className="text-gray-900 bg-gray-50 px-3 py-2 rounded-lg border border-gray-200">{value}</p>
        )}
    </div>
    );
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
                                <h2 className="">Pending Projects</h2>
                                <p className="text-red-500 text-xl font-bold">{pendingProjectsTotal}</p>
                            </div>

                            <div className="w-45 relative flex flex-col items-center text-foreground border-l-1 border-l-neutral-400 border-r-neutral-400">
                                <h2 className="">Active Projects</h2>
                                <p className="text-red-500 text-xl font-bold">{activeProjectsTotal}</p>
                            </div>

                            <div className="w-45 relative flex flex-col items-center text-foreground border-r-1 border-l-1 border-l-neutral-400 border-r-neutral-400">
                                <h2 className="pl-15 pr-15">Organizations</h2>
                                <p className="text-red-500 text-xl font-bold">{organizaionsTotal}</p>
                            </div>

                            <div className="w-45 relative flex flex-col items-center text-foreground">
                                <h2 className="">Donated Project Amount</h2>
                                <p className="text-red-500 text-xl font-bold">{formatCurrency(amountTotal)}</p>
                            </div>
                    </div>

                    <div>

                    {projects.length > 0 ? (
                         <>
                        <h1 className="text-2xl font-bold mb-4 mt-5 text-center">Project Management</h1>
                        {/* Toggle create project form */}
                        <div className="mb-6">
                        <button 
                            onClick={() => setShowCreateForm((v) => !v)} 
                            className={`${
                            showCreateForm 
                                ? 'bg-gray-500 hover:bg-gray-600' 
                                : 'bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700'
                            } text-white rounded-lg px-6 py-3 transition-all duration-300 shadow-md hover:shadow-lg flex items-center gap-2 font-medium`}
                        >
                            <span className="text-lg">+</span>
                            {showCreateForm ? "Hide Form" : "Create New Project"}
                        </button>
                        </div>

                        {/* Create project form */}
                        {showCreateForm && (
                        <div className="border border-gray-200 p-6 rounded-xl mb-6 bg-white shadow-lg">
                            <h3 className="m-0 mb-4 text-xl font-bold text-gray-800 flex items-center gap-2">
                            <span className="text-emerald-600">+</span>
                            Add New Project
                            </h3>
                            {createError && (
                            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 flex items-center gap-2">
                                <span className="text-lg">⚠️</span>
                                {createError}
                            </div>
                            )}
                            <form onSubmit={handleCreate} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div className="md:col-span-2">
                                <label className="block font-semibold mb-2 text-gray-700">Title *</label>
                                <input 
                                name="title" 
                                value={form.title} 
                                onChange={handleChange} 
                                placeholder="Enter project title" 
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                                required
                                />
                            </div>
                            
                            <div className="md:col-span-2">
                                <label className="block font-semibold mb-2 text-gray-700">Description</label>
                                <textarea 
                                name="description" 
                                value={form.description} 
                                onChange={handleChange} 
                                placeholder="Brief description about the project" 
                                rows={3} 
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200 resize-vertical"
                                />
                            </div>
                            
                            <div className="md:col-span-2">
                                <label className="block font-semibold mb-2 text-gray-700">Project Image *</label>
                                <div className="space-y-3">
                                <div className="flex items-center gap-3">
                                    <input 
                                    type="file" 
                                    accept="image/*" 
                                    onChange={handleImageFileChange} 
                                    disabled={imageUploading}
                                    className="file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-emerald-50 file:text-emerald-700 hover:file:bg-emerald-100 transition-colors"
                                    />
                                    {imageUploading && (
                                    <div className="flex items-center gap-2 text-sm text-gray-500">
                                        <div className="w-4 h-4 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin"></div>
                                        Uploading...
                                    </div>
                                    )}
                                </div>
                                {previewUrl && (
                                    <div className="relative">
                                    <img 
                                        src={previewUrl} 
                                        alt="Project preview" 
                                        className="w-full max-h-80 object-cover rounded-lg border-2 border-gray-200 shadow-sm"
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setPreviewUrl(null)}
                                        className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white rounded-full w-8 h-8 flex items-center justify-center transition-colors"
                                    >
                                        ×
                                    </button>
                                    </div>
                                )}
                                </div>
                            </div>
                            
                            <div>
                                <label className="block font-semibold mb-2 text-gray-700">Category</label>
                                <select 
                                name="category" 
                                value={form.category} 
                                onChange={handleChange} 
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200 bg-white"
                                >
                                <option value="children">Children</option>
                                <option value="healthcare">Healthcare</option>
                                <option value="environment">Environment</option>
                                <option value="disaster">Disaster Relief</option>
                                <option value="other">Other</option>
                                </select>
                            </div>
                            
                            <div>
                                <label className="block font-semibold mb-2 text-gray-700">Goal Amount (VND) *</label>
                                <input 
                                type="number" 
                                min="0" 
                                step="1000" 
                                name="goalAmount" 
                                value={form.goalAmount} 
                                onChange={handleChange} 
                                placeholder="e.g., 10,000,000" 
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                                required
                                />
                            </div>
                            
                            <div>
                                <label className="block font-semibold mb-2 text-gray-700">Start Date</label>
                                <input 
                                type="date" 
                                name="startDate" 
                                value={form.startDate} 
                                onChange={handleChange} 
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                                />
                            </div>
                            
                            <div>
                                <label className="block font-semibold mb-2 text-gray-700">End Date</label>
                                <input 
                                type="date" 
                                name="endDate" 
                                value={form.endDate} 
                                onChange={handleChange} 
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                                />
                            </div>
                            
                            <div className="md:col-span-2 flex gap-3 pt-2">
                                <button 
                                type="submit" 
                                disabled={creating}
                                className="px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white rounded-lg transition-all duration-300 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2 font-medium"
                                >
                                {creating ? (
                                    <>
                                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                    Creating...
                                    </>
                                ) : (
                                    <>
                                    <span>✓</span>
                                    Create Project
                                    </>
                                )}
                                </button>
                                <button 
                                type="button" 
                                onClick={() => setShowCreateForm(false)}
                                className="px-6 py-3 bg-gray-500 hover:bg-gray-600 text-white rounded-lg transition-all duration-300 font-medium"
                                >
                                Cancel
                                </button>
                            </div>
                            </form>
                        </div>
                        )}

                        {/* Loading and Error States */}
                        {loading ? (
                        <div className="flex justify-center items-center py-12">
                            <div className="flex flex-col items-center gap-3">
                            <div className="w-12 h-12 border-4 border-emerald-600 border-t-transparent rounded-full animate-spin"></div>
                            <p className="text-gray-600 font-medium">Loading data...</p>
                            </div>
                        </div>
                        ) : error ? (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-6 py-4 rounded-xl text-center">
                            <div className="flex items-center justify-center gap-2">
                            <span className="text-xl">⚠️</span>
                            <span className="font-medium">{error}</span>
                            </div>
                        </div>
                        ) : (
                        <div className="bg-white rounded-xl shadow-lg overflow-hidden">
                            <div className="overflow-x-auto">
                            <table className="w-full min-w-[820px] border-collapse font-sans">
                                <thead>
                                <tr className="bg-gradient-to-r from-gray-50 to-gray-100">
                                    <th className="border-b border-gray-200 px-4 py-4 font-bold text-gray-700 text-left min-w-[80px]">ID</th>
                                    <th className="border-b border-gray-200 px-4 py-4 font-bold text-gray-700 text-left">Title</th>
                                    <th className="border-b border-gray-200 px-4 py-4 font-bold text-gray-700 text-left min-w-[120px]">Status</th>
                                    <th className="border-b border-gray-200 px-4 py-4 font-bold text-gray-700 text-left min-w-[140px]">Created Date</th>
                                    <th className="border-b border-gray-200 px-4 py-4 font-bold text-gray-700 text-left min-w-[220px]">Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                {currentData.length === 0 ? (
                                    <tr>
                                    <td colSpan="5" className="border-b border-gray-200 p-8 text-center text-gray-500">
                                        <div className="flex flex-col items-center gap-3">
                                        <span className="text-4xl">📁</span>
                                        <div>
                                            <p className="font-medium text-gray-700 mb-1">No projects found</p>
                                            <p className="text-sm text-gray-500">Get started by creating your first project</p>
                                        </div>
                                        </div>
                                    </td>
                                    </tr>
                                ) : (
                                    currentData.map((project, idx) => {
                                    const projectId = project.projectId || project.id || project.project_id || idx;
                                    const status = project.status || 'N/A';
                                    const createdAt = project.createdAt || project.created_at;
                                    const formattedDate = createdAt 
                                        ? new Date(createdAt).toLocaleDateString('en-US', { 
                                            year: 'numeric', 
                                            month: 'short', 
                                            day: '2-digit',
                                            hour: '2-digit',
                                            minute: '2-digit'
                                        })
                                        : 'N/A';
                                    
                                    const statusColors = {
                                        active: 'bg-green-100 text-green-800',
                                        pending: 'bg-yellow-100 text-yellow-800',
                                        closed: 'bg-gray-100 text-gray-800',
                                        rejected: 'bg-red-100 text-red-800'
                                    };
                                    
                                    return (
                                        <tr key={projectId} className="hover:bg-gray-50 transition-colors duration-200">
                                        <td className="border-b border-gray-200 px-4 py-4 align-middle font-medium text-gray-900">
                                            #{projectId}
                                        </td>
                                        <td className="border-b border-gray-200 px-4 py-4 align-middle">
                                            <div className="font-medium text-gray-900">{project.title || 'N/A'}</div>
                                            {project.category && (
                                            <div className="text-sm text-gray-500 capitalize">{project.category}</div>
                                            )}
                                        </td>
                                        <td className="border-b border-gray-200 px-4 py-4 align-middle">
                                            <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium capitalize ${statusColors[status] || 'bg-gray-100 text-gray-800'}`}>
                                            {status}
                                            </span>
                                        </td>
                                        <td className="border-b border-gray-200 px-4 py-4 align-middle text-gray-600 text-sm">
                                            {formattedDate}
                                        </td>
                                        <td className="border-b border-gray-200 px-4 py-4 align-middle">
                                            <div className="flex gap-2">
                                            <button 
                                                onClick={() => handleView(projectId)} 
                                                className="flex items-center gap-1 bg-blue-600 hover:bg-blue-700 text-white px-3 py-2 rounded-lg transition-all duration-200 shadow-sm hover:shadow-md text-sm font-medium"
                                            >
                                                <span></span>
                                                View
                                            </button>
                                            <button 
                                                onClick={() => openEdit(projectId)} 
                                                className="flex items-center gap-1 bg-gray-600 hover:bg-gray-700 text-white px-3 py-2 rounded-lg transition-all duration-200 shadow-sm hover:shadow-md text-sm font-medium"
                                            >
                                                <span></span>
                                                Edit
                                            </button>
                                            <button 
                                                onClick={() => handleDelete(projectId)} 
                                                className="flex items-center gap-1 bg-red-600 hover:bg-red-700 text-white px-3 py-2 rounded-lg transition-all duration-200 shadow-sm hover:shadow-md text-sm font-medium"
                                            >
                                                <span></span>
                                                Delete
                                            </button>
                                            </div>
                                        </td>
                                        </tr>
                                    );
                                    })
                                )}
                                </tbody>
                            </table>
                            </div>
                        </div>
                        )}

                        {/* Pagination */}
                        {totalPages > 1 && (
                        <div className="flex gap-2 mt-6 items-center justify-center mb-6">
                            <button
                            className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200 shadow-sm"
                            onClick={() => setCurrentPage(p => p-1)}
                            disabled={currentPage === 1}
                            >
                            {"‹"}
                            </button>
                            
                            {Array.from({length: totalPages}).map((_, idx) => (
                            <button
                                key={idx}
                                className={`px-4 py-2 rounded-lg transition-all duration-200 ${
                                currentPage === idx+1 
                                    ? 'bg-gradient-to-r from-emerald-600 to-teal-600 text-white font-semibold shadow-md' 
                                    : 'bg-white border border-gray-300 text-gray-700 hover:bg-gray-50 shadow-sm'
                                }`}
                                onClick={() => setCurrentPage(idx+1)}
                            >
                                {idx+1}
                            </button>
                            ))}
                            
                            <button
                            className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200 shadow-sm"
                            onClick={() => setCurrentPage(p => p+1)}
                            disabled={currentPage === totalPages}
                            >
                            {"›"}
                            </button>
                        </div>
                        )}

                        {/* View Modal */}
                        {viewProject && (
                        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000] p-4">
                            <div className="bg-white p-6 rounded-2xl w-[min(640px,92vw)] max-h-[90vh] overflow-y-auto shadow-2xl">
                            <div className="flex items-center justify-between mb-4">
                                <h3 className="text-xl font-bold text-gray-800 flex items-center gap-2">
                                Project Details
                                </h3>
                                <button 
                                onClick={() => setViewProject(null)}
                                className="text-gray-400 hover:text-gray-600 text-2xl transition-colors"
                                >
                                ×
                                </button>
                            </div>
                            
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                                <DetailItem label="ID" value={viewProject.projectId} />
                                <DetailItem label="Status" value={viewProject.status} badge />
                                <DetailItem label="Title" value={viewProject.title} fullWidth />
                                <DetailItem label="Description" value={viewProject.description || '—'} fullWidth />
                                <DetailItem label="Category" value={viewProject.category} />
                                <DetailItem label="Goal Amount" value={formatCurrency(viewProject.goalAmount)} />
                                <DetailItem label="Raised Amount" value={formatCurrency(viewProject.raisedAmount)} />
                                <DetailItem label="Start Date" value={viewProject.startDate || '—'} />
                                <DetailItem label="End Date" value={viewProject.endDate || '—'} />
                                <DetailItem label="Created At" value={viewProject.createdAt || '—'} />
                                
                                {viewProject.imageUrl && (
                                <div className="md:col-span-2">
                                    <label className="block font-semibold mb-2 text-gray-700">Project Image</label>
                                    <a href={viewProject.imageUrl} target="_blank" rel="noreferrer" className="block">
                                    <img 
                                        src={viewProject.imageUrl} 
                                        alt="Project" 
                                        className="w-full h-48 object-cover rounded-lg border-2 border-gray-200 hover:border-emerald-500 transition-colors duration-200"
                                    />
                                    </a>
                                </div>
                                )}
                            </div>
                            
                            <div className="mt-6 flex gap-3 justify-end border-t border-gray-200 pt-4">
                                <button 
                                onClick={() => setViewProject(null)} 
                                className="px-6 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-lg transition-colors duration-200 font-medium"
                                >
                                Close
                                </button>
                            </div>
                            </div>
                        </div>
                        )}

                        {/* Edit Modal */}
                            {editProject && (
                            <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000] p-4">
                                <div className="bg-white p-6 rounded-2xl w-[min(720px,94vw)] max-h-[90vh] overflow-y-auto shadow-2xl">
                                {/* Header */}
                                <div className="flex items-center justify-between mb-6 pb-4 border-b border-gray-200">
                                    <h3 className="text-xl font-bold text-gray-800 flex items-center gap-2">
                                    Edit Project
                                    </h3>
                                    <button 
                                    onClick={() => setEditProject(null)}
                                    className="text-gray-400 hover:text-gray-600 text-2xl transition-colors duration-200 hover:bg-gray-100 w-8 h-8 rounded-full flex items-center justify-center"
                                    >
                                    ×
                                    </button>
                                </div>

                                {/* Error Message */}
                                {editError && (
                                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 flex items-center gap-2">
                                    <span className="text-lg">⚠️</span>
                                    {editError}
                                    </div>
                                )}

                                {/* Edit Form */}
                                <form onSubmit={saveEdit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    {/* Title */}
                                    <div className="md:col-span-2">
                                    <label className="block font-semibold mb-2 text-gray-700">Title *</label>
                                    <input 
                                        value={editProject.title} 
                                        onChange={(e) => setEditProject(p => ({...p, title: e.target.value}))} 
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                                        required
                                    />
                                    </div>

                                    {/* Description */}
                                    <div className="md:col-span-2">
                                    <label className="block font-semibold mb-2 text-gray-700">Description</label>
                                    <textarea 
                                        rows={3} 
                                        value={editProject.description} 
                                        onChange={(e) => setEditProject(p => ({...p, description: e.target.value}))} 
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200 resize-vertical"
                                        placeholder="Enter project description..."
                                    />
                                    </div>

                                    {/* Project Image */}
                                    <div className="md:col-span-2">
                                    <label className="block font-semibold mb-2 text-gray-700">Project Image</label>
                                    <div className="space-y-3">
                                        {/* URL Input */}
                                        <input 
                                        value={editProject.imageUrl} 
                                        onChange={(e) => setEditProject(p => ({...p, imageUrl: e.target.value}))} 
                                        placeholder="Enter image URL or upload file below"
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                                        />
                                        
                                        {/* File Upload */}
                                        <div className="flex items-center gap-3">
                                        <input 
                                            type="file" 
                                            accept="image/*" 
                                            onChange={handleEditImageUpload} 
                                            disabled={editImageUploading}
                                            className="file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100 transition-colors"
                                        />
                                        {editImageUploading && (
                                            <div className="flex items-center gap-2 text-sm text-gray-500">
                                            <div className="w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                                            Uploading...
                                            </div>
                                        )}
                                        </div>

                                        {/* Image Preview */}
                                        {editProject.imageUrl && (
                                        <div className="relative">
                                            <img 
                                            src={editProject.imageUrl} 
                                            alt="Project preview" 
                                            className="w-full max-h-80 object-cover rounded-lg border-2 border-gray-200 shadow-sm"
                                            />
                                            <button
                                            type="button"
                                            onClick={() => setEditProject(p => ({...p, imageUrl: ''}))}
                                            className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white rounded-full w-8 h-8 flex items-center justify-center transition-colors shadow-md"
                                            >
                                            ×
                                            </button>
                                        </div>
                                        )}
                                    </div>
                                    </div>

                                    {/* Category */}
                                    <div>
                                    <label className="block font-semibold mb-2 text-gray-700">Category</label>
                                    <select 
                                        value={editProject.category} 
                                        onChange={(e) => setEditProject(p => ({...p, category: e.target.value}))} 
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200 bg-white"
                                    >
                                        <option value="tre_em">Children</option>
                                        <option value="y_te">Healthcare</option>
                                        <option value="moi_truong">Environment</option>
                                        <option value="thien_tai">Disaster Relief</option>
                                        <option value="khac">Other</option>
                                    </select>
                                    </div>

                                    {/* Goal Amount */}
                                    <div>
                                    <label className="block font-semibold mb-2 text-gray-700">Goal Amount (VND) *</label>
                                    <input 
                                        type="number" 
                                        min="0" 
                                        step="1000" 
                                        value={editProject.goalAmount} 
                                        onChange={(e) => setEditProject(p => ({...p, goalAmount: e.target.value}))} 
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                                        required
                                    />
                                    </div>

                                    {/* Start Date */}
                                    <div>
                                    <label className="block font-semibold mb-2 text-gray-700">Start Date</label>
                                    <input 
                                        type="date" 
                                        value={editProject.startDate || ''} 
                                        onChange={(e) => setEditProject(p => ({...p, startDate: e.target.value}))} 
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                                    />
                                    </div>

                                    {/* End Date */}
                                    <div>
                                    <label className="block font-semibold mb-2 text-gray-700">End Date</label>
                                    <input 
                                        type="date" 
                                        value={editProject.endDate || ''} 
                                        onChange={(e) => setEditProject(p => ({...p, endDate: e.target.value}))} 
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                                    />
                                    </div>

                                    {/* Status */}
                                    <div className="md:col-span-2">
                                    <label className="block font-semibold mb-2 text-gray-700">Status</label>
                                    <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
                                        {[
                                        { value: 'active', label: 'Active', color: 'bg-green-500' },
                                        { value: 'pending', label: 'Pending', color: 'bg-yellow-500' },
                                        { value: 'closed', label: 'Closed', color: 'bg-gray-500' },
                                        { value: 'rejected', label: 'Rejected', color: 'bg-red-500' }
                                        ].map((statusOption) => (
                                        <button
                                            key={statusOption.value}
                                            type="button"
                                            onClick={() => setEditProject(p => ({...p, status: statusOption.value}))}
                                            className={`flex items-center gap-2 px-4 py-3 rounded-lg border-2 transition-all duration-200 ${
                                            editProject.status === statusOption.value
                                                ? `border-${statusOption.color.split('-')[1]}-500 bg-${statusOption.color.split('-')[1]}-50 text-${statusOption.color.split('-')[1]}-700 font-semibold`
                                                : 'border-gray-300 bg-white text-gray-700 hover:bg-gray-50'
                                            }`}
                                        >
                                            <div className={`w-3 h-3 rounded-full ${statusOption.color}`}></div>
                                            {statusOption.label}
                                        </button>
                                        ))}
                                    </div>
                                    </div>

                                    {/* Action Buttons */}
                                    <div className="md:col-span-2 flex gap-3 justify-end pt-4 border-t border-gray-200">
                                    <button 
                                        type="button" 
                                        onClick={() => setEditProject(null)} 
                                        className="px-6 py-3 bg-gray-500 hover:bg-gray-600 text-white rounded-lg transition-all duration-200 font-medium shadow-sm hover:shadow-md"
                                    >
                                        Cancel
                                    </button>
                                    <button 
                                        type="submit" 
                                        disabled={editSaving}
                                        className="px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white rounded-lg transition-all duration-200 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed font-medium flex items-center gap-2"
                                    >
                                        {editSaving ? (
                                        <>
                                            <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                            Saving...
                                        </>
                                        ) : (
                                        <>
                                            Save Changes
                                        </>
                                        )}
                                    </button>
                                    </div>
                                </form>
                                </div>
                            </div>
                            )}
                    </>
                    ) 
                    :(<>
                        <div className="w-180 mt-10 relative flex flex-col items-center text-center">
                        <h1 className="text-3xl font-bold">The ambassador project is pending approval</h1>
                        <p className="mt-5 text-gray-500">
                            Discover projects tailored for you and recommendations that match your interests.</p>
                    </div>
                    
                    <div className="mt-8 mb-5 relative flex flex-col items-center text-center">
                        <div className="mb-5">
                            <img src="https://givenow.vn/wp-content/themes/funlin-progression-child/images/no-records.png" alt="..."></img>
                        </div>

                        <div className="mb-6">
                            <button 
                                onClick={() => setShowCreateForm((v) => !v)} 
                                className={`${
                                showCreateForm 
                                    ? 'bg-gray-500 hover:bg-gray-600' 
                                    : 'bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700'
                                } text-white rounded-lg px-6 py-3 transition-all duration-300 shadow-md hover:shadow-lg flex items-center gap-2 font-medium`}
                            >
                                <span className="text-lg">+</span>
                                {showCreateForm ? "Hide Form" : "Create New Project"}
                            </button>
                        </div>
                    {/* Create project form */}
                    {showCreateForm && (
                    <div className="border border-gray-200 p-6 rounded-xl mb-6 bg-white shadow-lg">
                        <h3 className="m-0 mb-4 text-xl font-bold text-gray-800 flex items-center gap-2">
                        <span className="text-emerald-600">+</span>
                        Add New Project
                        </h3>
                        {createError && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 flex items-center gap-2">
                            <span className="text-lg">⚠️</span>
                            {createError}
                        </div>
                        )}
                        <form onSubmit={handleCreate} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="md:col-span-2">
                            <label className="block font-semibold mb-2 text-gray-700">Title *</label>
                            <input 
                            name="title" 
                            value={form.title} 
                            onChange={handleChange} 
                            placeholder="Enter project title" 
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                            required
                            />
                        </div>
                        
                        <div className="md:col-span-2">
                            <label className="block font-semibold mb-2 text-gray-700">Description</label>
                            <textarea 
                            name="description" 
                            value={form.description} 
                            onChange={handleChange} 
                            placeholder="Brief description about the project" 
                            rows={3} 
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200 resize-vertical"
                            />
                        </div>
                        
                        <div className="md:col-span-2">
                            <label className="block font-semibold mb-2 text-gray-700">Project Image *</label>
                            <div className="space-y-3">
                            <div className="flex items-center gap-3">
                                <input 
                                type="file" 
                                accept="image/*" 
                                onChange={handleImageFileChange} 
                                disabled={imageUploading}
                                className="file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-emerald-50 file:text-emerald-700 hover:file:bg-emerald-100 transition-colors"
                                />
                                {imageUploading && (
                                <div className="flex items-center gap-2 text-sm text-gray-500">
                                    <div className="w-4 h-4 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin"></div>
                                    Uploading...
                                </div>
                                )}
                            </div>
                            {previewUrl && (
                                <div className="relative">
                                <img 
                                    src={previewUrl} 
                                    alt="Project preview" 
                                    className="w-full max-h-80 object-cover rounded-lg border-2 border-gray-200 shadow-sm"
                                />
                                <button
                                    type="button"
                                    onClick={() => setPreviewUrl(null)}
                                    className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white rounded-full w-8 h-8 flex items-center justify-center transition-colors"
                                >
                                    ×
                                </button>
                                </div>
                            )}
                            </div>
                        </div>
                        
                        <div>
                            <label className="block font-semibold mb-2 text-gray-700">Category</label>
                            <select 
                            name="category" 
                            value={form.category} 
                            onChange={handleChange} 
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200 bg-white"
                            >
                            <option value="children">Children</option>
                            <option value="healthcare">Healthcare</option>
                            <option value="environment">Environment</option>
                            <option value="disaster">Disaster Relief</option>
                            <option value="other">Other</option>
                            </select>
                        </div>
                        
                        <div>
                            <label className="block font-semibold mb-2 text-gray-700">Goal Amount (VND) *</label>
                            <input 
                            type="number" 
                            min="0" 
                            step="1000" 
                            name="goalAmount" 
                            value={form.goalAmount} 
                            onChange={handleChange} 
                            placeholder="e.g., 10,000,000" 
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                            required
                            />
                        </div>
                        
                        <div>
                            <label className="block font-semibold mb-2 text-gray-700">Start Date</label>
                            <input 
                            type="date" 
                            name="startDate" 
                            value={form.startDate} 
                            onChange={handleChange} 
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                            />
                        </div>
                        
                        <div>
                            <label className="block font-semibold mb-2 text-gray-700">End Date</label>
                            <input 
                            type="date" 
                            name="endDate" 
                            value={form.endDate} 
                            onChange={handleChange} 
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                            />
                        </div>
                        
                        <div className="md:col-span-2 flex gap-3 pt-2">
                            <button 
                            type="submit" 
                            disabled={creating}
                            className="px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white rounded-lg transition-all duration-300 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2 font-medium"
                            >
                            {creating ? (
                                <>
                                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                Creating...
                                </>
                            ) : (
                                <>
                                <span>✓</span>
                                Create Project
                                </>
                            )}
                            </button>
                            <button 
                            type="button" 
                            onClick={() => setShowCreateForm(false)}
                            className="px-6 py-3 bg-gray-500 hover:bg-gray-600 text-white rounded-lg transition-all duration-300 font-medium"
                            >
                            Cancel
                            </button>
                        </div>
                        </form>
                    </div>
                    )}
                    </div></>
                     )}
                   
                    </div>          
                </div>
            </div>
            <br />
            <br />
        </div>
    </>
    )
}
export default PendingCampSection;