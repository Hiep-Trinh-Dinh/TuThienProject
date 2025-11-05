import React from "react";
import Sidebar from "../../components/admin/sidebar";

export default function AdminLayout({ children }) {
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
