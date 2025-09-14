import { useState, useEffect } from "react"
import { useParams } from "react-router-dom"
import { ProjectDetails } from "../../../components/projects/project-details"
import projectService from "../../../services/projectService"

export default function ProjectPage() {
  const { id } = useParams()
  const [project, setProject] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const loadProject = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await projectService.getProjectById(id)
        if (data) {
          setProject(data)
        } else {
          setError('Project not found')
        }
      } catch (err) {
        setError(err.message)
        console.error('Error loading project:', err)
      } finally {
        setLoading(false)
      }
    }

    if (id) {
      loadProject()
    }
  }, [id])

  if (loading) {
    return (
      <div className="max-w-3xl mx-auto py-16 px-4 text-center">
        <div className="text-6xl mb-4">⏳</div>
        <h3 className="text-xl font-semibold text-foreground mb-2">Loading project...</h3>
        <p className="text-muted-foreground">Please wait while we fetch the project details.</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="max-w-3xl mx-auto py-16 px-4 text-center">
        <div className="text-6xl mb-4">❌</div>
        <h3 className="text-xl font-semibold text-foreground mb-2">Error loading project</h3>
        <p className="text-muted-foreground mb-4">{error}</p>
        <button 
          onClick={() => window.location.reload()} 
          className="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90"
        >
          Try Again
        </button>
      </div>
    )
  }

  if (!project) {
    return (
      <div className="max-w-3xl mx-auto py-16 px-4 text-center">
        <div className="text-6xl mb-4">🔍</div>
        <h3 className="text-xl font-semibold text-foreground mb-2">Project not found</h3>
        <p className="text-muted-foreground">The project you're looking for doesn't exist or has been removed.</p>
      </div>
    )
  }

  return (
    <div>
      <ProjectDetails project={project} />
    </div>
  )
}
