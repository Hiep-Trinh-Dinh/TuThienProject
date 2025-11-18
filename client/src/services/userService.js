const API_BASE_URL = 'http://localhost:8080/api/accounts';
const ADMIN_API_BASE_URL = 'http://localhost:8080/api/admin';

class UserService {
  // Lấy danh sách người dùng - dành cho admin (sử dụng endpoint riêng)
  async getAllUsers() {
    try {
      const token = localStorage.getItem('token');
      console.log('Fetching users from:', `${ADMIN_API_BASE_URL}/users`);
      const response = await fetch(`${ADMIN_API_BASE_URL}/users`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      console.log('Response status:', response.status);
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Error response:', errorText);
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      console.log('Raw response data:', data);
      // Backend trả về ApiResponse với format: { code, message, result }
      const users = data.result || (Array.isArray(data) ? data : []);
      console.log('Parsed users:', users);
      return users;
    } catch (error) {
      console.error('Error fetching users:', error);
      throw error;
    }
  }

  // Cập nhật thông tin user (bao gồm roles) - dành cho admin
  async updateUserRoles(userId, userData) {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API_BASE_URL}/user/${userId}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      return data.result || data;
    } catch (error) {
      console.error('Error updating user roles:', error);
      throw error;
    }
  }

  // Cập nhật trạng thái user (lock/unlock)
  async updateUserStatus(userId, status) {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API_BASE_URL}/user/${userId}/status?status=${encodeURIComponent(status)}`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`,
        }
      });
      if (!response.ok) {
        const text = await response.text();
        throw new Error(`HTTP error! status: ${response.status} - ${text}`);
      }
      const data = await response.json();
      return data.result || data;
    } catch (error) {
      console.error('Error updating user status:', error);
      throw error;
    }
  }
}

export default new UserService();
