import React, { useEffect, useState } from "react";
import userService from "../../services/userService";
import UserRolesManagement from "./user-roles";
import { UsersFilters } from "../../components/user/user-filter"
import { SearchInfo } from "../../components/user/search-info"
import { Pagination } from "../../components/ui/pagination"

export default function AdminUserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [viewUser, setViewUser] = useState(null);

  // users filter
  const [selectedAuthProvider, setSelectedAuthProvider] = useState("All Authentication Providers")
  const [selectedStatus, setSelectedStatus] = useState("all")
  const [searchQuery, setSearchQuery] = useState("")
  const [sortBy, setSortBy] = useState("newest")

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [size] = useState(6)
  

useEffect(() => {
    const loadUsers = async () => {
      try {
        setLoading(true)
        setError(null)
        const response = await userService.searchUsers(
          searchQuery,
          selectedAuthProvider,
          selectedStatus,
          sortBy,
          currentPage,
          size
        )
        if (Array.isArray(response)) {
          const total = response.length
          const pages = Math.ceil(total / size)
          const sliceStart = currentPage * size
          const sliceEnd = sliceStart + size
          setUsers(response.slice(sliceStart, sliceEnd))
          setTotalPages(pages)
          setTotalElements(total)
        } else {
          setUsers(response.content || [])
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
    const timeoutId = setTimeout(loadUsers, 300)
    return () => clearTimeout(timeoutId)
  }, [searchQuery, selectedAuthProvider, selectedStatus, sortBy, currentPage]);

  // Reset to first page when filters change
    useEffect(() => {
      setCurrentPage(0)
    }, [searchQuery, selectedAuthProvider, selectedStatus, sortBy,size])

  // useEffect(() => {
  //   loadUsers();
  // }, []);

  // const loadUsers = () => {
  //   setLoading(true);
  //   setError("");
  //   userService.getAllUsers()
  //     .then((data) => {
  //       // Nếu backend trả về { result: [...] } thì lấy data.result, nếu trả về list thì lấy trực tiếp.
  //       if (Array.isArray(data)) setUsers(data);
  //       else if (data.result && Array.isArray(data.result)) setUsers(data.result);
  //       else setUsers([]);
  //     })
  //     .catch(() => setError("Lỗi tải dữ liệu người dùng"))
  //     .finally(() => setLoading(false));
  // };

  const toggleLock = async (user) => {
    const userId = user.userId || user.id || user.user_id;
    const isLocked = String(user.status).toUpperCase() === 'INACTIVE' || String(user.status).toUpperCase() === 'BANNED';
    const nextStatus = isLocked ? 'ACTIVE' : 'INACTIVE';
    const confirmMsg = isLocked ? 'Mở khóa người dùng này?' : 'Khóa người dùng này? Người dùng sẽ không thể đăng nhập.';
    if (!window.confirm(confirmMsg)) return;
    try {
      await userService.updateUserStatus(userId, nextStatus);
      loadUsers();
    } catch (e) {
      alert('Cập nhật trạng thái thất bại');
    }
  };

  const handlePageChange = (page) => {
    if (page < 0 || page >= totalPages) return
    setCurrentPage(page)
    try { window.scrollTo({ top: 0, behavior: 'smooth' }) } catch {}
  }

  return (
    <div>
      <h2 className="text-2xl font-semibold mb-4">Quản lý Người dùng</h2>
      <div className="mb-3">
        <UsersFilters
          selectedAuthProvider={selectedAuthProvider}
          setSelectedAuthProvider={setSelectedAuthProvider}
          selectedStatus={selectedStatus}
          setSelectedStatus={setSelectedStatus}
          searchQuery={searchQuery}
          setSearchQuery={setSearchQuery}
          sortBy={sortBy}
          setSortBy={setSortBy}
          totalResults={totalElements}
        />
        <SearchInfo
          searchQuery={searchQuery}
          selectedAuthProvider={selectedAuthProvider}
          selectedStatus={selectedStatus}
          sortBy={sortBy}
          onClearFilter={handleClearFilters}
        />
      </div>
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
            <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[180px]">Tên</th>
            <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[220px]">Email</th>
            <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[150px]">Quyền</th>
            <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[92px]">Trạng thái</th>
            <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[120px]">Ngày tạo</th>
            <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[240px]">Hành động</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => {
            const userId = u.userId || u.id || u.user_id;
            const userRoles = u.roles || [];
            const roleNames = userRoles.map(r => r.name || r).join(', ') || 'Chưa có quyền';
            return (
            <tr key={userId}>
              <td className="border border-gray-300 px-3 py-2 align-middle">{userId}</td>
              <td className="border border-gray-300 px-3 py-2 align-middle">{u.fullName || u.full_name || u.name}</td>
              <td className="border border-gray-300 px-3 py-2 align-middle">{u.email}</td>
              <td className="border border-gray-300 px-3 py-2 align-middle">
                <span className="text-xs text-gray-600">{roleNames}</span>
              </td>
              <td className="border border-gray-300 px-3 py-2 align-middle">{u.status}</td>
              <td className="border border-gray-300 px-3 py-2 align-middle">{u.createdAt || u.created_at}</td>
              <td className="border border-gray-300 px-3 py-2 align-middle">
                <button 
                  onClick={() => setSelectedUserId(userId)}
                  className="mr-2 min-w-[72px] bg-blue-600 hover:bg-blue-700 text-white px-3 py-1.5 rounded-md transition"
                >
                  Quyền
                </button>
                <button 
                  onClick={() => setViewUser(u)}
                  className="mr-2 min-w-[72px] bg-gray-600 hover:bg-gray-700 text-white px-3 py-1.5 rounded-md transition"
                >
                  Xem
                </button>
                <button 
                  onClick={() => toggleLock(u)}
                  className={`${(String(u.status).toUpperCase() === 'INACTIVE' || String(u.status).toUpperCase() === 'BANNED') ? 'bg-emerald-600 hover:bg-emerald-700' : 'bg-red-600 hover:bg-red-700'} min-w-[96px] text-white px-3 py-1.5 rounded-md transition`}
                >
                  {(String(u.status).toUpperCase() === 'INACTIVE' || String(u.status).toUpperCase() === 'BANNED') ? 'Mở khóa' : 'Khóa'}
                </button>
              </td>
            </tr>
          )})}
        </tbody>
      </table>
      </div>
      )}

      {totalPages > 1 && (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
          totalElements={totalElements}
          size={size}
        />
      )}

      {/* Modal quản lý quyền */}
      {selectedUserId && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000]">
          <UserRolesManagement
            userId={selectedUserId}
            onClose={() => setSelectedUserId(null)}
            onSuccess={() => {
              setSelectedUserId(null);
              loadUsers();
            }}
          />
        </div>
      )}

      {/* Modal xem thông tin user */}
      {viewUser && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000]">
          <div className="bg-white p-5 rounded-lg w-[min(640px,92vw)]">
            <h3 className="mt-0 text-lg font-semibold">Thông tin người dùng</h3>
            <div className="leading-7">
              <div><b>ID:</b> {viewUser.userId || viewUser.id || viewUser.user_id}</div>
              <div><b>Họ tên:</b> {viewUser.fullName || viewUser.full_name || viewUser.name || '—'}</div>
              <div><b>Email:</b> {viewUser.email || '—'}</div>
              <div><b>Số điện thoại:</b> {viewUser.phone || '—'}</div>
              <div><b>Trạng thái:</b> {viewUser.status || '—'}</div>
              <div><b>Provider:</b> {viewUser.authProvider || '—'}</div>
              <div><b>Ngày tạo:</b> {viewUser.createdAt || viewUser.created_at || '—'}</div>
              <div><b>Quyền:</b> {(viewUser.roles || []).map(r => r.name || r).join(', ') || 'Chưa có quyền'}</div>
            </div>
            <div className="mt-4 flex gap-2 justify-end">
              <button onClick={() => setViewUser(null)} className="px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-md transition">Đóng</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
  function handleClearFilters() {
    setSearchQuery("")
    setSelectedAuthProvider("All Authentication Providers")
    setSelectedStatus("all")
    setSortBy("newest")
    setCurrentPage(0)
  }
}
