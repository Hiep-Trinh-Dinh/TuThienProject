import { Button } from "../ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"
import { Progress } from "../ui/progress"
import { MapPin, Calendar, Users } from "lucide-react"
import { Link } from "react-router-dom"

const featuredProjects = [
  {
    id: 1,
    title: "Clean Water for Rural Communities",
    description: "Providing access to clean, safe drinking water for 500 families in remote villages.",
    location: "Kenya, East Africa",
    raised: 75000,
    goal: 100000,
    donors: 234,
    daysLeft: 15,
    image: "/clean-water-well-in-african-village.jpg",
  },
  {
    id: 2,
    title: "Education for Underprivileged Children",
    description: "Building schools and providing educational resources for children in underserved areas.",
    location: "Guatemala, Central America",
    raised: 45000,
    goal: 80000,
    donors: 156,
    daysLeft: 28,
    image: "/classroom-learning.png",
  },
  {
    id: 3,
    title: "Emergency Food Relief",
    description: "Providing nutritious meals and food packages to families affected by natural disasters.",
    location: "Philippines, Southeast Asia",
    raised: 32000,
    goal: 50000,
    donors: 189,
    daysLeft: 7,
    image: "/food-relief-distribution-center.jpg",
  },
]

export function FeaturedProjects() {
  return (
    <section className="py-16 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">Featured Projects</h2>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            Discover urgent causes that need your support right now
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
          {featuredProjects.map((project) => {
            const progressPercentage = (project.raised / project.goal) * 100

            return (
              <Card key={project.id} className="overflow-hidden hover:shadow-lg transition-shadow">
                <div className="aspect-video relative overflow-hidden">
                  <img
                    src={project.image || "/placeholder.svg"}
                    alt={project.title}
                    className="w-full h-full object-cover"
                  />
                </div>

                <CardHeader>
                  <CardTitle className="text-xl text-balance">{project.title}</CardTitle>
                  <CardDescription className="text-pretty">{project.description}</CardDescription>
                </CardHeader>

                <CardContent className="space-y-4">
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <MapPin className="h-4 w-4" />
                    {project.location}
                  </div>

                  <div className="space-y-2">
                    <div className="flex justify-between text-sm">
                      <span className="font-medium">${project.raised.toLocaleString()} raised</span>
                      <span className="text-muted-foreground">${project.goal.toLocaleString()} goal</span>
                    </div>
                    <Progress value={progressPercentage} className="h-2" />
                  </div>

                  <div className="flex justify-between items-center text-sm text-muted-foreground">
                    <div className="flex items-center gap-1">
                      <Users className="h-4 w-4" />
                      {project.donors} donors
                    </div>
                    <div className="flex items-center gap-1">
                      <Calendar className="h-4 w-4" />
                      {project.daysLeft} days left
                    </div>
                  </div>

                  <Button asChild className="w-full">
                    <Link to={`/projects/${project.id}`}>Support This Cause</Link>
                  </Button>
                </CardContent>
              </Card>
            )
          })}
        </div>

        <div className="text-center">
          <Button variant="outline" size="lg" asChild>
            <Link to="/projects">View All Projects</Link>
          </Button>
        </div>
      </div>
    </section>
  )
}
