import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"
import { Button } from "../ui/button"
import { Progress } from "../ui/progress"
import { Link } from "react-router-dom"
import { resolveProjectImage } from "../../lib/utils"

export function RelatedProjects({ projects }) {
  if (!projects || projects.length === 0) {
    return null
  }

  return (
    <section className="py-16 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-bold text-foreground mb-4">Dự án liên quan</h2>
          <p className="text-lg text-muted-foreground">Khám phá các dự án khác trong cùng danh mục</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {projects.map((project) => {
            const progressPercentage = project.progressPercentage || 0

            const imageSrc = resolveProjectImage(project.imageUrl || project.image)
            const hasImage = imageSrc && imageSrc !== "/placeholder.svg"

            return (
              <Card key={project.projectId} className="overflow-hidden hover:shadow-lg transition-shadow">
                <div className="aspect-video relative overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-100">
                  {hasImage ? (
                    <img
                      src={imageSrc}
                      alt={project.title}
                      className="w-full h-full object-cover"
                      loading="lazy"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center">
                      <div className="text-6xl opacity-20">🤝</div>
                    </div>
                  )}
                </div>

                <CardHeader>
                  <CardTitle className="text-lg text-balance line-clamp-2">{project.title}</CardTitle>
                  <CardDescription className="line-clamp-2">{project.description}</CardDescription>
                </CardHeader>

                <CardContent className="space-y-4">
                  <div className="text-sm text-muted-foreground">
                    Tổ chức ID: {project.orgId}
                  </div>

                  <div className="space-y-2">
                    <div className="flex justify-between text-sm">
                      <span className="font-medium">{project.raisedAmount?.toLocaleString()} VNĐ</span>
                      <span className="text-muted-foreground">{project.goalAmount?.toLocaleString()} VNĐ</span>
                    </div>
                    <Progress value={progressPercentage} className="h-2" />
                    <div className="text-xs text-muted-foreground">{Math.round(progressPercentage)}% đã quyên góp</div>
                  </div>

                  <Button asChild className="w-full bg-transparent" variant="outline">
                    <Link to={`/projects/${project.projectId}`}>Xem dự án</Link>
                  </Button>
                </CardContent>
              </Card>
            )
          })}
        </div>
      </div>
    </section>
  )
}
