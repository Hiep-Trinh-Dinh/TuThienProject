"use client"

import { Badge } from "../ui/badge"
import { Progress } from "../ui/progress"
import { Calendar, Share2 } from "lucide-react"
import { Button } from "../ui/button"
import { resolveProjectImage } from "../../lib/utils"

export function ProjectHero({ project }) {
  const raised = Number(project.raisedAmount || 0)
  const goal = Number(project.goalAmount || 0)
  const progressPercentage = project.progressPercentage || 0
  const daysLeft = project.daysLeft
  const donors = project.donorCount || 0
  const heroImage = resolveProjectImage(project.imageUrl || project.image)
  const hasImage = heroImage && heroImage !== "/placeholder.svg"

  // Map category to Vietnamese
  const categoryMap = {
    'y_te': 'Y tế',
    'tre_em': 'Trẻ em', 
    'thien_tai': 'Thiên tai',
    'moi_truong': 'Môi trường',
    'khac': 'Khác'
  }

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: project.title,
        text: project.description,
        url: window.location.href,
      })
    } else {
      navigator.clipboard.writeText(window.location.href)
      // You could add a toast notification here
    }
  }

  return (
    <section className="relative bg-gradient-to-br from-primary/10 via-white to-primary/5 py-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div className="space-y-6">
            <div className="flex flex-wrap gap-2">
              {project.category && (
                <Badge variant="secondary">
                  {categoryMap[project.category] || project.category}
                </Badge>
              )}
              <Badge variant="outline">
                {project.status}
              </Badge>
            </div>

            <h1 className="text-4xl md:text-5xl font-bold text-foreground text-balance">{project.title}</h1>

            {project.description && (
              <p className="text-xl text-muted-foreground text-pretty">{project.description}</p>
            )}

            <div className="flex items-center gap-4 text-muted-foreground">
              {daysLeft !== undefined && daysLeft > 0 && (
                <div className="flex items-center gap-2">
                  <Calendar className="h-5 w-5" />
                  <span>{daysLeft} ngày còn lại</span>
                </div>
              )}
            </div>

            <div className="text-sm text-muted-foreground">
              Tổ chức ID: <span className="font-medium text-foreground">{project.orgId}</span>
            </div>

            <Button onClick={handleShare} variant="outline" className="w-fit bg-transparent">
              <Share2 className="h-4 w-4 mr-2" />
              Chia sẻ dự án
            </Button>
          </div>

          <div className="space-y-6">
            <div className="aspect-video rounded-lg overflow-hidden shadow-lg bg-gradient-to-br from-blue-50 to-indigo-100">
              {hasImage ? (
                <img
                  src={heroImage}
                  alt={project.title}
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center">
                  <div className="text-8xl opacity-20">🤝</div>
                </div>
              )}
            </div>

            <div className="bg-white rounded-lg p-6 shadow-sm border">
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-2xl font-bold text-primary">{raised.toLocaleString()} VNĐ</span>
                  <span className="text-muted-foreground">mục tiêu {goal.toLocaleString()} VNĐ</span>
                </div>

                <Progress value={progressPercentage} className="h-3" />

                <div className="grid grid-cols-2 gap-4 text-center">
                  <div>
                    <div className="text-2xl font-bold text-foreground">{donors}</div>
                    <div className="text-sm text-muted-foreground">Người quyên góp</div>
                  </div>
                  <div>
                    <div className="text-2xl font-bold text-foreground">{Math.round(progressPercentage)}%</div>
                    <div className="text-sm text-muted-foreground">Đã quyên góp</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
