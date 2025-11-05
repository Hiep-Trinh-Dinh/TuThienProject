import React, { useEffect, useState } from "react";
import userService from "../../services/userService";
import roleService from "../../services/roleService";

export default function UserRolesManagement({ userId, onClose, onSuccess }) {
  const [user, setUser] = useState(null);
  const [roles, setRoles] = useState([]);
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    loadData();
  }, [userId]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [usersData, rolesData] = await Promise.all([
        userService.getAllUsers(),
        roleService.getAllRoles()
      ]);

      const userList = Array.isArray(usersData) ? usersData : (usersData.result || []);
      const foundUser = userList.find(u => (u.userId || u.id) === userId);
      const rolesList = Array.isArray(rolesData) ? rolesData : (rolesData.result || []);

      setUser(foundUser);
      setRoles(rolesList);
      setSelectedRoles(foundUser?.roles?.map(r => r.name || r) || []);
    } catch (err) {
      setError("Lỗi tải dữ liệu");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRoleToggle = (roleName) => {
    setSelectedRoles(prev => 
      prev.includes(roleName)
        ? prev.filter(r => r !== roleName)
        : [...prev, roleName]
    );
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      setError("");
      await userService.updateUserRoles(userId, {
        roles: selectedRoles
      });
      if (onSuccess) onSuccess();
      if (onClose) onClose();
    } catch (err) {
      setError("Lỗi cập nhật quyền");
      console.error(err);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div>Đang tải...</div>;
  }

  if (!user) {
    return <div>Không tìm thấy user</div>;
  }

  return (
    <div style={{ padding: 20, background: '#fff', borderRadius: 8, maxWidth: 600 }}>
      <h3>Quản lý quyền cho: {user.fullName || user.email}</h3>
      
      {error && (
        <div style={{ color: 'red', marginBottom: 10 }}>{error}</div>
      )}

      <div style={{ marginBottom: 20 }}>
        <h4>Chọn quyền:</h4>
        {roles.map(role => (
          <label key={role.name} style={{ display: 'block', marginBottom: 10 }}>
            <input
              type="checkbox"
              checked={selectedRoles.includes(role.name)}
              onChange={() => handleRoleToggle(role.name)}
              style={{ marginRight: 8 }}
            />
            <strong>{role.name}</strong>
            {role.description && (
              <span style={{ color: '#666', marginLeft: 8 }}> - {role.description}</span>
            )}
          </label>
        ))}
      </div>

      <div style={{ display: 'flex', gap: 10 }}>
        <button
          onClick={handleSave}
          disabled={saving}
          style={{
            padding: '10px 20px',
            background: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: 4,
            cursor: saving ? 'not-allowed' : 'pointer'
          }}
        >
          {saving ? 'Đang lưu...' : 'Lưu'}
        </button>
        <button
          onClick={onClose}
          style={{
            padding: '10px 20px',
            background: '#6c757d',
            color: 'white',
            border: 'none',
            borderRadius: 4,
            cursor: 'pointer'
          }}
        >
          Hủy
        </button>
      </div>
    </div>
  );
}

