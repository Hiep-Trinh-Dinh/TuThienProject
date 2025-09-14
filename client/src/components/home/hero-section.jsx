import { useState, useEffect } from "react"
import { Button } from "../ui/button"
import { ArrowRight, Heart } from "lucide-react"
import { Link } from "react-router-dom"
import projectService from "../../services/projectService"

export function HeroSection() {
  const [stats, setStats] = useState({
    totalProjects: 0,
    totalRaised: 0,
    totalDonors: 0
  })

  useEffect(() => {
    const loadStats = async () => {
      try {
        const projects = await projectService.getAllProjects()
        const totalRaised = projects.reduce((sum, project) => sum + (project.raisedAmount || 0), 0)
        const totalDonors = projects.reduce((sum, project) => sum + (project.donorCount || 0), 0)
        
        setStats({
          totalProjects: projects.length,
          totalRaised,
          totalDonors
        })
      } catch (error) {
        console.error('Error loading stats:', error)
      }
    }

    loadStats()
  }, [])

  return (
    <section className="relative bg-gradient-to-br from-green-50 via-white to-green-50 py-20 lg:py-32">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center">
          <h1 className="text-4xl md:text-6xl font-bold text-foreground mb-6 text-balance">
            Quyên Góp Từ Thiện
          </h1>
          
          <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
            Cùng nhau tạo ra sự khác biệt tích cực cho cộng đồng. Hỗ trợ các dự án từ thiện ý nghĩa và giúp đỡ những người cần thiết.
          </p>

          {/* Stats */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-12">
            <div className="text-center">
              <div className="text-3xl font-bold text-primary mb-2">{stats.totalProjects}</div>
              <div className="text-muted-foreground">Dự án đang hoạt động</div>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold text-primary mb-2">{stats.totalRaised.toLocaleString()} VNĐ</div>
              <div className="text-muted-foreground">Đã quyên góp</div>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold text-primary mb-2">{stats.totalDonors}</div>
              <div className="text-muted-foreground">Người quyên góp</div>
            </div>
          </div>

          <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
            <Button size="lg" asChild className="text-lg px-8 py-6">
              <Link to="/projects">
                <Heart className="mr-2 h-5 w-5" />
                Quyên Góp Ngay
                <ArrowRight className="ml-2 h-5 w-5" />
              </Link>
            </Button>
            <Button size="lg" variant="outline" asChild className="text-lg px-8 py-6">
              <Link to="/about">
                Tìm Hiểu Thêm
              </Link>
            </Button>
          </div>
        </div>
      </div>

      {/* Background decoration */}
      <div className="absolute inset-0 -z-10 overflow-hidden">
        <div className="absolute top-1/4 left-1/4 w-72 h-72 bg-primary/5 rounded-full blur-3xl"></div>
        <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-green-200/20 rounded-full blur-3xl"></div>
      </div>
    </section>
  )
}

export default HeroSection
