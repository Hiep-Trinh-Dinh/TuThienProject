import { useState, useEffect } from "react"
import { ProjectHero } from "./project-hero"
import { ProjectContent } from "./project-content"
import { DonationSection } from "./donation-section"
import { RelatedProjects } from "./related-projects"
import projectService from "../../services/projectService"

export function ProjectDetails({ project }) {
  const [relatedProjects, setRelatedProjects] = useState([])

  useEffect(() => {
    const loadRelatedProjects = async () => {
      try {
        const projects = await projectService.getProjectsByCategory(project.category.toLowerCase())
        const filtered = projects.filter((p) => p.projectId !== project.projectId).slice(0, 3)
        setRelatedProjects(filtered)
      } catch (error) {
        console.error('Error loading related projects:', error)
        setRelatedProjects([])
      }
    }

    if (project?.category) {
      loadRelatedProjects()
    }
  }, [project?.category, project?.projectId])

  return (
    <div className="min-h-screen bg-gray-50">
      <ProjectHero project={project} />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <ProjectContent project={project} />
          </div>
          <div className="lg:col-span-1">
            <DonationSection project={project} />
          </div>
        </div>
      </div>
      {relatedProjects.length > 0 && <RelatedProjects projects={relatedProjects} />}
    </div>
  )
}
