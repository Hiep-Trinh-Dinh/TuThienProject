"use client"

import { useState, useEffect } from "react"
import { ProjectCard } from "./project-card"
import { ProjectFilters } from "./project-filters"
import { SearchInfo } from "./search-info"
import { Pagination } from "../ui/pagination"
import projectService from "../../services/projectService"

export default ProjectsListing

export function ProjectsListing() {
  const [projects, setProjects] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedCategory, setSelectedCategory] = useState("All Categories")
  const [selectedStatus, setSelectedStatus] = useState("all")
  const [searchQuery, setSearchQuery] = useState("")
  const [sortBy, setSortBy] = useState("newest")
  
  // Pagination state
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [size] = useState(6)

  // Load projects with advanced search and pagination
  useEffect(() => {
    const loadProjects = async () => {
      try {
        setLoading(true)
        setError(null)
        const response = await projectService.searchProjects(
          searchQuery,
          selectedCategory,
          selectedStatus,
          sortBy,
          currentPage,
          size
        )
        
        setProjects(response.content || [])
        setTotalPages(response.totalPages || 0)
        setTotalElements(response.totalElements || 0)
      } catch (err) {
        setError(err.message)
        console.error('Error loading projects:', err)
      } finally {
        setLoading(false)
      }
    }

    const timeoutId = setTimeout(loadProjects, 500) // Debounce search
    return () => clearTimeout(timeoutId)
  }, [searchQuery, selectedCategory, selectedStatus, sortBy, currentPage, size])

  // Reset to first page when filters change
  useEffect(() => {
    setCurrentPage(0)
  }, [searchQuery, selectedCategory, selectedStatus, sortBy])

  const handlePageChange = (page) => {
    setCurrentPage(page)
  }

  const handleClearFilters = () => {
    setSearchQuery("")
    setSelectedCategory("All Categories")
    setSelectedStatus("all")
    setSortBy("newest")
    setCurrentPage(0)
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <ProjectFilters
        selectedCategory={selectedCategory}
        setSelectedCategory={setSelectedCategory}
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
        selectedCategory={selectedCategory}
        selectedStatus={selectedStatus}
        sortBy={sortBy}
        onClearFilter={handleClearFilters}
      />

      {loading ? (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">⏳</div>
          <h3 className="text-xl font-semibold text-foreground mb-2">Đang tải dự án...</h3>
          <p className="text-muted-foreground">Vui lòng chờ trong khi chúng tôi tải dữ liệu dự án.</p>
        </div>
      ) : error ? (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">❌</div>
          <h3 className="text-xl font-semibold text-foreground mb-2">Lỗi tải dự án</h3>
          <p className="text-muted-foreground mb-4">{error}</p>
          <button 
            onClick={() => window.location.reload()} 
            className="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90"
          >
            Thử lại
          </button>
        </div>
      ) : projects.length === 0 ? (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">🔍</div>
          <h3 className="text-xl font-semibold text-foreground mb-2">Không tìm thấy dự án</h3>
          <p className="text-muted-foreground">Hãy thử điều chỉnh bộ lọc hoặc từ khóa tìm kiếm để tìm thêm dự án.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {projects.map((project) => (
            <ProjectCard key={project.projectId} project={project} />
          ))}
        </div>
      )}

      {!loading && !error && projects.length > 0 && (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
          totalElements={totalElements}
          size={size}
        />
      )}
    </div>
  )
}
