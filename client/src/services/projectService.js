const API_BASE_URL = 'http://localhost:8080/api/projects';

class ProjectService {
  // Lấy tất cả dự án
  async getAllProjects() {
    try {
      const response = await fetch(`${API_BASE_URL}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching projects:', error);
      throw error;
    }
  }

  // Lấy dự án theo ID
  async getProjectById(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/${id}`);
      if (!response.ok) {
        if (response.status === 404) {
          return null;
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Error fetching project ${id}:`, error);
      throw error;
    }
  }

  // Lấy dự án theo danh mục
  async getProjectsByCategory(category) {
    try {
      const response = await fetch(`${API_BASE_URL}/category/${category}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Error fetching projects by category ${category}:`, error);
      throw error;
    }
  }

  // Tìm kiếm dự án nâng cao với phân trang
  async searchProjects(query, category, status, sortBy, page = 0, size = 6) {
    try {
      const params = new URLSearchParams();
      if (query) params.append('q', query);
      if (category && category !== 'All Categories') params.append('category', category);
      if (status && status !== 'all') params.append('status', status);
      if (sortBy) params.append('sortBy', sortBy);
      params.append('page', page);
      params.append('size', size);

      const response = await fetch(`${API_BASE_URL}/search?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Error searching projects:`, error);
      throw error;
    }
  }

  // Sắp xếp dự án
  async getProjectsSortedBy(sortBy) {
    try {
      const response = await fetch(`${API_BASE_URL}/sort/${sortBy}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Error fetching sorted projects by ${sortBy}:`, error);
      throw error;
    }
  }

  // Tạo dự án mới
  async createProject(projectData) {
    try {
      const response = await fetch(`${API_BASE_URL}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(projectData),
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error creating project:', error);
      throw error;
    }
  }

  // Cập nhật dự án
  async updateProject(id, projectData) {
    try {
      const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(projectData),
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Error updating project ${id}:`, error);
      throw error;
    }
  }

  // Xóa dự án
  async deleteProject(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: 'DELETE',
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return true;
    } catch (error) {
      console.error(`Error deleting project ${id}:`, error);
      throw error;
    }
  }
}

export default new ProjectService();
