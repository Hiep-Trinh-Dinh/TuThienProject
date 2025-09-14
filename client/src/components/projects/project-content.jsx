import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"

export function ProjectContent({ project }) {
  const description = project.description
  const startDate = project.startDate
  const endDate = project.endDate

  return (
    <div className="space-y-8">
      <Card>
        <CardHeader>
          <CardTitle>Mô tả dự án</CardTitle>
          {(startDate || endDate) && (
            <CardDescription>
              {startDate && <>Bắt đầu: {new Date(startDate).toLocaleDateString('vi-VN')} </>}
              {endDate && <>• Kết thúc: {new Date(endDate).toLocaleDateString('vi-VN')}</>}
            </CardDescription>
          )}
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground leading-relaxed">{description}</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Thông tin dự án</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <h4 className="font-medium text-foreground mb-2">ID Dự án</h4>
              <p className="text-muted-foreground">{project.projectId}</p>
            </div>
            <div>
              <h4 className="font-medium text-foreground mb-2">Tổ chức</h4>
              <p className="text-muted-foreground">ID: {project.orgId}</p>
            </div>
            <div>
              <h4 className="font-medium text-foreground mb-2">Trạng thái</h4>
              <p className="text-muted-foreground">{project.status}</p>
            </div>
            <div>
              <h4 className="font-medium text-foreground mb-2">Ngày tạo</h4>
              <p className="text-muted-foreground">
                {new Date(project.createdAt).toLocaleDateString('vi-VN')}
              </p>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
