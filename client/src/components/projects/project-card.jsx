import { Button } from "../ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"
import { Progress } from "../ui/progress"
import { Badge } from "../ui/badge"
import { Calendar, Users } from "lucide-react"
import { Link } from "react-router-dom"

export function ProjectCard({ project }) {
  const id = project.projectId
  const raised = Number(project.raisedAmount || 0)
  const goal = Number(project.goalAmount || 0)
  const donors = Number(project.donorCount || 0)
  const category = project.category
  const daysLeft = project.daysLeft
  const progressPercentage = project.progressPercentage || 0

  // Map category to Vietnamese
  const categoryMap = {
    'y_te': 'Y tế',
    'tre_em': 'Trẻ em', 
    'thien_tai': 'Thiên tai',
    'moi_truong': 'Môi trường',
    'khac': 'Khác'
  }

  return (
    <Card className="overflow-hidden hover:shadow-lg transition-all duration-300 group">
      <div className="aspect-video relative overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-100">
        <div className="w-full h-full flex items-center justify-center">
          <div className="text-6xl opacity-20">🤝</div>
        </div>
        <div className="absolute top-4 left-4 flex gap-2">
          {category && (
            <Badge className="border" variant="outline">
              {categoryMap[category] || category}
            </Badge>
          )}
        </div>
      </div>

      <CardHeader>
        <div className="flex justify-between items-start gap-2">
          <CardTitle className="text-xl text-balance line-clamp-2 group-hover:text-primary transition-colors">
            {project.title}
          </CardTitle>
        </div>
        <CardDescription className="text-pretty line-clamp-3">{project.description}</CardDescription>
      </CardHeader>

      <CardContent className="space-y-4">
        <div className="space-y-2">
          <div className="text-xs text-muted-foreground">
            Tổ chức ID: {project.orgId}
          </div>
        </div>

        <div className="space-y-2">
          <div className="flex justify-between text-sm">
            <span className="font-medium">{raised.toLocaleString()} VNĐ</span>
            <span className="text-muted-foreground">{goal.toLocaleString()} VNĐ</span>
          </div>
          <Progress value={progressPercentage} className="h-2" />
          <div className="text-xs text-muted-foreground">{Math.round(progressPercentage)}% đã quyên góp</div>
        </div>

        <div className="flex justify-between items-center text-sm text-muted-foreground">
          <div className="flex items-center gap-1">
            <Users className="h-4 w-4" />
            {donors} người quyên góp
          </div>
          {daysLeft !== undefined && daysLeft > 0 && (
            <div className="flex items-center gap-1">
              <Calendar className="h-4 w-4" />
              {daysLeft} ngày còn lại
            </div>
          )}
        </div>

        <Button asChild className="w-full">
          <Link to={`/projects/${id}`}>Quyên Góp</Link>
        </Button>
      </CardContent>
    </Card>
  )
}
