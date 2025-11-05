import React from 'react';
import { Link } from 'react-router-dom';

const menu = [
  { label: 'Quản lý Dự án', path: '/admin/projects' },
  { label: 'Quản lý Người dùng', path: '/admin/users' },
  { label: 'Sao kê quyên góp', path: '/admin/donations' },
  // Có thể mở rộng thêm các mục khác
];

export default function Sidebar() {
  return (
    <aside style={{ width: 220, background: '#222', color: '#fff', display: 'flex', flexDirection: 'column', height: '100vh' }}>
      <div style={{ padding: 24, fontWeight: 700, fontSize: 20, borderBottom: '1px solid #333' }}>
        Admin Panel
      </div>
      <nav style={{ flex: 1 }}>
        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {menu.map(item => (
            <li key={item.path}>
              <Link to={item.path} style={{ display: 'block', padding: '14px 24px', color: '#fff', textDecoration: 'none' }}>
                {item.label}
              </Link>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
}
