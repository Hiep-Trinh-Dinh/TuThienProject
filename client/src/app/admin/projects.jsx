import React, { useEffect, useState } from "react";
import projectService from "../../services/projectService";

export default function AdminProjectList() {
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
    projectService.getAllTypeProjects()
      .then((data) => setProjects(data))
      .catch((err) => setError("Lỗi tải dữ liệu dự án"))
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
      const data = await projectService.getAllTypeProjects();
      setProjects(data);
    } catch (e) {
      setError("Lỗi tải dữ liệu dự án");
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
        setCreateError("Server trả về đường dẫn ảnh không hợp lệ: " + (result.imageUrl || "(empty)"));
        // Vẫn giữ preview local tạm thời, KHÔNG set previewUrl bằng giá trị invalid
      }
    } catch (err) {
      setCreateError("Tải ảnh thất bại. Vui lòng thử lại.");
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
        status: "active",
      };

      if (!payload.title) {
        setCreateError("Vui lòng nhập tiêu đề dự án");
        setCreating(false);
        return;
      }
      if (!payload.goalAmount || payload.goalAmount <= 0) {
        setCreateError("Mục tiêu gây quỹ phải lớn hơn 0");
        setCreating(false);
        return;
      }

      await projectService.createProject(payload);
      // Refresh list
      setLoading(true);
      const data = await projectService.getAllTypeProjects();
      setProjects(data);
      setLoading(false);
      // Reset form
      setForm({ title: "", description: "", imageUrl: "", category: "tre_em", goalAmount: "", startDate: "", endDate: "" });
      setShowCreateForm(false);
    } catch (err) {
      setCreateError("Tạo dự án thất bại. Vui lòng thử lại.");
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
      alert("Không thể tải chi tiết dự án");
    }
  };

  // Delete project
  const handleDelete = async (projectId) => {
    if (!window.confirm("Bạn có chắc muốn xóa dự án này")) return;
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
      alert("Cập nhật trạng thái dự án thất bại");
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
      alert("Không thể tải dữ liệu để sửa");
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
      setEditError("Tải ảnh thất bại. Vui lòng thử lại");
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
        setEditError("Tiêu đề không được để trống");
        setEditSaving(false);
        return;
      }
      await projectService.updateProject(editProject.projectId, payload);
      setEditProject(null);
      await reloadProjects();
    } catch (e) {
      setEditError("Cập nhật thất bại. Vui lòng thử lại");
    } finally {
      setEditSaving(false);
    }
  };

  function formatCurrency(amount, currency = "VND", locale = "vi-VN") {
      return new Intl.NumberFormat(locale, {
          style: "currency",
          currency: currency,
      }).format(amount);
  }
  
  return (
    <div>
      <h2 className="text-2xl font-semibold mb-4">Quản lý Dự án</h2>

      {/* Toggle create project form */}
      <div className="mb-3">
        <button onClick={() => setShowCreateForm((v) => !v)} className={`${showCreateForm ? 'bg-gray-500 hover:bg-gray-600' : 'bg-emerald-600 hover:bg-emerald-700'} text-white rounded-md px-4 py-2 transition`}>
          {showCreateForm ? "Ẩn form" : "Tạo dự án mới"}
        </button>
      </div>

      {/* Create project form */}
      {showCreateForm && (
      <div className="border border-gray-200 p-4 rounded-lg mb-4">
        <h3 className="m-0 mb-3 text-lg font-semibold">Thêm dự án mới</h3>
        {createError && <div className="text-red-600 mb-2">{createError}</div>}
        <form onSubmit={handleCreate} className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div className="md:col-span-2">
            <label className="block font-semibold mb-1">Tiêu đề</label>
            <input name="title" value={form.title} onChange={handleChange} placeholder="Nhập tiêu đề dự án" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
          </div>
          <div className="md:col-span-2">
            <label className="block font-semibold mb-1">Mô tả</label>
            <textarea name="description" value={form.description} onChange={handleChange} placeholder="Mô tả ngắn về dự án" rows={3} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
          </div>
          <div className="md:col-span-2">
            <label className="block font-semibold mb-1">Ảnh dự án (bắt buộc dùng chức năng upload)</label>
            <div className="flex flex-col gap-2">
              <div className="flex items-center gap-3">
                <input type="file" accept="image/*" onChange={handleImageFileChange} disabled={imageUploading} />
                {imageUploading && <span className="text-sm text-muted-foreground">Đang tải ảnh...</span>}
              </div>
              {previewUrl && (
                <img src={previewUrl} alt="Xem trước ảnh dự án" className="w-full max-h-60 object-cover rounded border" />
              )}
            </div>
          </div>
          <div>
            <label className="block font-semibold mb-1">Danh mục</label>
            <select name="category" value={form.category} onChange={handleChange} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500">
              <option value="tre_em">Trẻ em</option>
              <option value="y_te">Y tế</option>
              <option value="moi_truong">Môi trường</option>
              <option value="thien_tai">Thiên tai</option>
              <option value="khac">Khác</option>
            </select>
          </div>
          <div>
            <label className="block font-semibold mb-1">Mục tiêu (VNĐ)</label>
            <input type="number" min="0" step="1000" name="goalAmount" value={form.goalAmount} onChange={handleChange} placeholder="Ví dụ: 10000000" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
          </div>
          <div>
            <label className="block font-semibold mb-1">Ngày bắt đầu</label>
            <input type="date" name="startDate" value={form.startDate} onChange={handleChange} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
          </div>
          <div>
            <label className="block font-semibold mb-1">Ngày kết thúc</label>
            <input type="date" name="endDate" value={form.endDate} onChange={handleChange} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
          </div>
          <div className="md:col-span-2 flex gap-2">
            <button type="submit" disabled={creating} className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-md transition">
              {creating ? "Đang tạo..." : "Tạo dự án"}
            </button>
            <button type="button" onClick={() => setShowCreateForm(false)} className="px-4 py-2 bg-gray-500 hover:bg-gray-600 text-white rounded-md transition">
              Hủy
            </button>
          </div>
        </form>
      </div>
      )}

      {loading ? (
        <div>Đang tải dữ liệu...</div>
      ) : error ? (
        <div className="text-red-600">{error}</div>
      ) : (
      <div className="overflow-x-auto">
        <table className="w-full min-w-[820px] border-collapse font-sans">
          <thead>
            <tr>
              <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[60px]">ID</th>
              <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left">Tiêu đề</th>
              <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left">Trạng thái</th>
              <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[120px]">Ngày tạo</th>
              <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[200px]">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {currentData.length === 0 ? (
              <tr>
                <td colSpan="5" className="border border-gray-300 p-5 text-center text-gray-600">
                  Không có dữ liệu dự án
                </td>
              </tr>
            ) : (
              currentData.map((project, idx) => {
                const projectId = project.projectId || project.id || project.project_id || idx;
                const status = project.status || 'N/A';
                const createdAt = project.createdAt || project.created_at;
                const formattedDate = createdAt 
                  ? new Date(createdAt).toLocaleDateString('vi-VN', { 
                      year: 'numeric', 
                      month: '2-digit', 
                      day: '2-digit',
                      hour: '2-digit',
                      minute: '2-digit'
                    })
                  : 'N/A';
                
                return (
                  <tr key={projectId}>
                    <td className="border border-gray-300 px-3 py-2 align-middle">{projectId}</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle">{project.title || 'N/A'}</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle">{status}</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle">{formattedDate}</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle">
                      <button 
                        onClick={() => handleView(projectId)} 
                        className="mr-2 min-w-[72px] bg-blue-600 hover:bg-blue-700 text-white px-3 py-1.5 rounded-md transition"
                      >
                        Xem
                      </button>
                      <button 
                        onClick={() => openEdit(projectId)} 
                        className="mr-2 min-w-[72px] bg-gray-600 hover:bg-gray-700 text-white px-3 py-1.5 rounded-md transition"
                      >
                        Sửa
                      </button>
                      <button 
                        onClick={() => handleDelete(projectId)} 
                        className="min-w-[72px] bg-red-600 hover:bg-red-700 text-white px-3 py-1.5 rounded-md transition"
                      >
                        Xóa
                      </button>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
      )}

      {totalPages > 1 && (
        <div className="flex gap-2 mt-4 items-center justify-center">
          <button
            className="px-3 py-1 bg-gray-100 rounded disabled:opacity-50"
            onClick={() => setCurrentPage(p => p-1)}
            disabled={currentPage === 1}>{"<"}</button>
          {Array.from({length: totalPages}).map((_, idx) => (
            <button
              key={idx}
              className={`px-3 py-1 rounded ${currentPage === idx+1 ? 'bg-blue-600 text-white font-semibold' : 'bg-gray-100'}`}
              onClick={() => setCurrentPage(idx+1)}>{idx+1}</button>
          ))}
          <button
            className="px-3 py-1 bg-gray-100 rounded disabled:opacity-50"
            onClick={() => setCurrentPage(p => p+1)}
            disabled={currentPage === totalPages}>{">"}</button>
        </div>
      )}

      {/* View Modal */}
      {viewProject && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000]">
          <div className="bg-white p-5 rounded-lg w-[min(640px,92vw)]">
            <h3 className="mt-0 text-lg font-semibold">Chi tiết dự án</h3>
            <div className="leading-7">
              <div><b>ID:</b> {viewProject.projectId}</div>
              <div><b>Tiêu đề:</b> {viewProject.title}</div>
              <div><b>Mô tả:</b> {viewProject.description || '—'}</div>
              <div><b>Ảnh:</b> {viewProject.imageUrl ? <a href={viewProject.imageUrl} target="_blank" rel="noreferrer" className="text-blue-600 underline">Xem ảnh</a> : '—'}</div>
              <div><b>Danh mục:</b> {viewProject.category}</div>
              <div><b>Trạng thái:</b> {viewProject.status}</div>
              <div><b>Mục tiêu:</b> {formatCurrency(viewProject.goalAmount)}</div>
              <div><b>Đã gây quỹ:</b> {formatCurrency(viewProject.raisedAmount)}</div>
              <div><b>Bắt đầu:</b> {viewProject.startDate || '—'}</div>
              <div><b>Kết thúc:</b> {viewProject.endDate || '—'}</div>
              <div><b>Tạo lúc:</b> {viewProject.createdAt || '—'}</div>
            </div>
            <div className="mt-4 flex gap-2 justify-end">
              <button onClick={() => setViewProject(null)} className="px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-md transition">Đóng</button>
            </div>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {editProject && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000]">
          <div className="bg-white p-5 rounded-lg w-[min(720px,94vw)]">
            <h3 className="mt-0 text-lg font-semibold">Sửa dự án</h3>
            {editError && <div className="text-red-600 mb-2">{editError}</div>}
            <form onSubmit={saveEdit} className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div className="md:col-span-2">
                <label className="block font-semibold mb-1">Tiêu đề</label>
                <input value={editProject.title} onChange={(e)=>setEditProject(p=>({...p, title:e.target.value}))} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div className="md:col-span-2">
                <label className="block font-semibold mb-1">Mô tả</label>
                <textarea rows={3} value={editProject.description} onChange={(e)=>setEditProject(p=>({...p, description:e.target.value}))} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div className="md:col-span-2">
                <label className="block font-semibold mb-1">Ảnh dự án (URL)</label>
                <div className="flex flex-col gap-2">
                  <input value={editProject.imageUrl} onChange={(e)=>setEditProject(p=>({...p, imageUrl:e.target.value}))} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
                  <div className="flex items-center gap-3">
                    <input type="file" accept="image/*" onChange={handleEditImageUpload} disabled={editImageUploading} />
                    {editImageUploading && <span className="text-sm text-muted-foreground">Đang tải ảnh...</span>}
                  </div>
                  {editProject.imageUrl && (
                    <img src={editProject.imageUrl} alt="Xem trước ảnh dự án" className="w-full max-h-60 object-cover rounded border" />
                  )}
                </div>
              </div>
              <div>
                <label className="block font-semibold mb-1">Danh mục</label>
                <select value={editProject.category} onChange={(e)=>setEditProject(p=>({...p, category:e.target.value}))} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500">
                  <option value="tre_em">Trẻ em</option>
                  <option value="y_te">Y tế</option>
                  <option value="moi_truong">Môi trường</option>
                  <option value="thien_tai">Thiên tai</option>
                  <option value="khac">Khác</option>
                </select>
              </div>
              <div>
                <label className="block font-semibold mb-1">Mục tiêu (VNĐ)</label>
                <input type="number" min="0" step="1000" value={editProject.goalAmount} onChange={(e)=>setEditProject(p=>({...p, goalAmount:e.target.value}))} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <label className="block font-semibold mb-1">Ngày bắt đầu</label>
                <input type="date" value={editProject.startDate || ''} onChange={(e)=>setEditProject(p=>({...p, startDate:e.target.value}))} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <label className="block font-semibold mb-1">Ngày kết thúc</label>
                <input type="date" value={editProject.endDate || ''} onChange={(e)=>setEditProject(p=>({...p, endDate:e.target.value}))} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <label className="block font-semibold mb-1">Trạng thái</label>
                <select value={editProject.status} onChange={(e)=>setEditProject(p=>({...p, status:e.target.value}))} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500">
                  <option value="active">active</option>
                  <option value="pending">pending</option>
                  <option value="closed">closed</option>
                  <option value="rejected">rejected</option>
                </select>
              </div>
              <div className="md:col-span-2 flex gap-2 justify-end">
                <button type="button" onClick={()=>setEditProject(null)} className="px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-md transition">Hủy</button>
                <button type="submit" disabled={editSaving} className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-md transition">
                  {editSaving ? 'Đang lưu...' : 'Lưu'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
