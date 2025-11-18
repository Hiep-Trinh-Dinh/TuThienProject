const API_BASE_URL = 'http://localhost:8080/api/roles';

class RoleService {
  // Lấy danh sách tất cả roles
  async getAllRoles() {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(API_BASE_URL, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      return data.result || data;
    } catch (error) {
      console.error('Error fetching roles:', error);
      throw error;
    }
  }
}

export default new RoleService();

