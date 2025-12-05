import React, { useEffect, useState } from "react";
import { getDonations, updateDonationStatus, searchDonations } from "../../services/donationService";
import axios from "axios";
import { DonationsFilters } from "../../components/donations/donations-filter"
import { SearchInfo } from "../../components/donations/search-info"
import { Pagination } from "../../components/ui/pagination"

function statusColor(status) {
  switch (status) {
    case "success": return "text-green-700 bg-green-100";
    case "pending": return "text-yellow-800 bg-yellow-100";
    case "failed": return "text-red-700 bg-red-100";
    default: return "text-gray-600 bg-gray-100";
  }
}

export default function AdminDonationsList() {
  const [donations, setDonations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedDonation, setSelectedDonation] = useState(null);
  const [editDonation, setEditDonation] = useState(null);
  const [editStatus, setEditStatus] = useState("pending");
  const [saving, setSaving] = useState(false);

  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState("All Payment Methods")
  const [selectedPaymentStatus, setSelectedPaymentStatus] = useState("all")
  const [selectedFrom, setSelectedFrom] = useState("")
  const [selectedTo, setSelectedTo] = useState("")
  const [selectedAmountFrom, setSelectedAmountFrom] = useState("")
  const [selectedAmountTo, setSelectedAmountTo] = useState("")
  const [sortBy, setSortBy] = useState("newest")

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [size] = useState(6)

  useEffect(() => {
    const loadDonations = async () => {
      try {
        setLoading(true)
        setError(null)
        const response = await searchDonations(
          selectedPaymentMethod,
          selectedPaymentStatus,
          selectedFrom,
          selectedTo,
          selectedAmountFrom,
          selectedAmountTo,
          sortBy,
          currentPage,
          size
        )
        if (Array.isArray(response)) {
          const total = response.length
          const pages = Math.ceil(total / size)
          const sliceStart = currentPage * size
          const sliceEnd = sliceStart + size
          setDonations(response.slice(sliceStart, sliceEnd))
          setTotalPages(pages)
          setTotalElements(total)
        } else {
          setDonations(response.content || [])
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
    const timeoutId = setTimeout(loadDonations, 300)
    return () => clearTimeout(timeoutId)
  }, [selectedPaymentMethod, selectedPaymentStatus, selectedFrom, selectedTo, selectedAmountFrom, selectedAmountTo, sortBy, currentPage]);
  // Reset to first page when filters change
  useEffect(() => {
    setCurrentPage(0)
  }, [selectedPaymentMethod, selectedPaymentStatus, selectedFrom, selectedTo, selectedAmountFrom, selectedAmountTo, sortBy,size])
  
  
  const openEdit = (d) => {
    setEditDonation(d);
    setEditStatus(d.paymentStatus || "pending");
  };

  const submitEdit = async (e) => {
    e.preventDefault();
    if (!editDonation) return;
    try {
      setSaving(true);
      await updateDonationStatus(editDonation.donationId, editStatus);
      setEditDonation(null);
      setCurrentPage();
    } catch (err) {
      alert("Cập nhật trạng thái thất bại");
    } finally {
      setSaving(false);
    }
  };

  // Thêm hàm xử lý xuất Excel
  const handleExportExcel = async () => {
    try {
      const token = localStorage.getItem("token"); // Token lưu sau đăng nhập
      const response = await axios.get("/api/donations/report/excel", {
        responseType: "blob",
        headers: token
          ? { Authorization: `Bearer ${token}` }
          : {},
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "donations_report.xlsx");
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert("Xuất file Excel thất bại!");
    }
  };

  const handlePageChange = (page) => {
    if (page < 0 || page >= totalPages) return
    setCurrentPage(page)
    try { window.scrollTo({ top: 0, behavior: 'smooth' }) } catch {}
  }
  return (
    <div>
      <h2 className="text-2xl font-semibold mb-4">Quản lý Quyên góp</h2>
      <DonationsFilters
        selectedPaymentMethod={selectedPaymentMethod}
        setSelectedPaymentMethod={setSelectedPaymentMethod}
        selectedPaymentStatus={selectedPaymentStatus}
        setSelectedPaymentStatus={setSelectedPaymentStatus}
        selectedFrom={selectedFrom}
        setSelectedFrom={setSelectedFrom}
        selectedTo={selectedTo}
        setSelectedTo={setSelectedTo}
        selectedAmountFrom={selectedAmountFrom}
        setSelectedAmountFrom={setSelectedAmountFrom}
        selectedAmountTo={selectedAmountTo}
        setSelectedAmountTo={setSelectedAmountTo}
        sortBy={sortBy}
        setSortBy={setSortBy}
        totalResults={totalElements}
      />
      <SearchInfo
        selectedPaymentMethod={selectedPaymentMethod}
        selectedPaymentStatus={selectedPaymentStatus}
        selectedFrom={selectedFrom}
        selectedTo={selectedTo}
        selectedAmountFrom={selectedAmountFrom}
        selectedAmountTo={selectedAmountTo}
        sortBy={sortBy}
        onClearFilter={handleClearFilters}
      />
      <div className="mb-3 flex items-center justify-between">
        <button
          onClick={handleExportExcel}
          className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md font-medium"
        >
          Xuất Excel sao kê
        </button>
      </div>
      {loading ? (
        <div>Đang tải dữ liệu...</div>
      ) : error ? (
        <div className="text-red-600">{error}</div>
      ) : (
        <>
        <div className="overflow-x-auto">
          <table className="w-full min-w-[900px] border-collapse font-sans">
            <thead>
              <tr>
                <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[60px]">ID</th>
                <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[132px]">Người quyên góp</th>
                <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[180px]">Dự án</th>
                <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left">Số tiền</th>
                <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left">Phương thức</th>
                <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[88px]">Trạng thái</th>
                <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[148px]">Thời gian</th>
                <th className="border border-gray-300 px-3 py-2 font-semibold bg-gray-50 text-left min-w-[180px]">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {donations.length === 0 ? (
                <tr>
                  <td colSpan="8" className="border border-gray-300 p-5 text-center text-gray-600">Không có dữ liệu quyên góp</td>
                </tr>
              ) : (
                donations.map((d) => (
                  <tr key={d.donationId}>
                    <td className="border border-gray-300 px-3 py-2 align-middle">{d.donationId}</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle">{d.donorName || d.donorId}</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle">{d.projectName || d.projectId}</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle font-semibold text-blue-700">{Number(d.amount || 0).toLocaleString()} đ</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle capitalize">{d.paymentMethod}</td>
                    <td className={`border border-gray-300 px-3 py-2 align-middle font-semibold rounded ${statusColor(d.paymentStatus)}`}>{d.paymentStatus}</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle">{d.donatedAt ? new Date(d.donatedAt).toLocaleString() : ""}</td>
                    <td className="border border-gray-300 px-3 py-2 align-middle">
                      <button onClick={() => setSelectedDonation(d)} className="mr-2 min-w-[72px] bg-blue-600 hover:bg-blue-700 text-white px-3 py-1.5 rounded-md transition">Xem</button>
                      <button onClick={() => openEdit(d)} className="min-w-[72px] bg-gray-600 hover:bg-gray-700 text-white px-3 py-1.5 rounded-md transition">Sửa</button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {/* Pagination */}
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

      {/* Modal xem thông tin quyên góp */}
      {selectedDonation && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000]">
          <div className="bg-white p-5 rounded-lg w-[min(680px,94vw)]">
            <h3 className="mt-0 text-lg font-semibold mb-2">Chi tiết quyên góp</h3>
            <div className="leading-7">
              <div><b>ID Giao dịch:</b> {selectedDonation.donationId}</div>
              <div><b>Người quyên góp:</b> {selectedDonation.donorName || selectedDonation.donorId}</div>
              <div><b>Dự án:</b> {selectedDonation.projectName || selectedDonation.projectId}</div>
              <div><b>Số tiền:</b> {Number(selectedDonation.amount || 0).toLocaleString()} đ</div>
              <div><b>Phương thức:</b> {selectedDonation.paymentMethod}</div>
              <div><b>Trạng thái:</b> {selectedDonation.paymentStatus}</div>
              <div><b>Thời gian:</b> {selectedDonation.donatedAt ? new Date(selectedDonation.donatedAt).toLocaleString() : ""}</div>
            </div>
            <div className="mt-4 flex gap-2 justify-end">
              <button onClick={() => setSelectedDonation(null)} className="px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-md transition">Đóng</button>
            </div>
          </div>
        </div>
      )}

      {/* Modal sửa trạng thái quyên góp */}
      {editDonation && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000]">
          <div className="bg-white p-5 rounded-lg w-[min(520px,92vw)]">
            <h3 className="mt-0 text-lg font-semibold mb-3">Cập nhật trạng thái quyên góp</h3>
            <form onSubmit={submitEdit} className="space-y-3">
              <div>
                <label className="block font-semibold mb-1">Trạng thái</label>
                <select value={editStatus} onChange={(e)=>setEditStatus(e.target.value)} className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500">
                  <option value="success">success</option>
                  <option value="pending">pending</option>
                  <option value="failed">failed</option>
                </select>
              </div>
              <div className="flex gap-2 justify-end">
                <button type="button" onClick={()=>setEditDonation(null)} className="px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-md transition">Hủy</button>
                <button type="submit" disabled={saving} className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-md transition">
                  {saving ? 'Đang lưu...' : 'Lưu'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
  function handleClearFilters() {
    setSelectedPaymentMethod("All Payment Methods")
    setSelectedPaymentStatus("all")
    setSelectedFrom("")
    setSelectedTo("")
    setSelectedAmountFrom("")
    setSelectedAmountTo("")
    setSortBy("newest")
    setCurrentPage(0)
  }
}
