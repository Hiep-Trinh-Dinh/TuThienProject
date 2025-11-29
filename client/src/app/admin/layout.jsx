import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../contexts/auth-context";
import Sidebar from "../../components/admin/sidebar";

export default function AdminLayout({ children }) {

  const { user, loading } = useAuth(); // lấy thông tin user từ context
  if (loading) {
    return (<div className="spinner-container">
            <div className="spinner"></div>
            </div>
      );
  }
  // Nếu chưa login then chuyển hướng sang trang /login
  if (!user) {
    return <Navigate to="/login" replace />;
  } else if (user.roles[0].name !== "admin") {
    return <Navigate to="/" replace />;
  }


  return (
    <div style={{ display: "flex", height: "100vh" }}>
      <Sidebar />
      <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
        <header style={{ padding: 16, background: '#fafafa', borderBottom: '1px solid #eee' }}>
          <h2 style={{ margin: 0 }}>Admin Dashboard</h2>
        </header>
        <main style={{ flex: 1, overflow: 'auto', padding: 24 }}>{children}</main>
      </div>
    </div>
  );
}
