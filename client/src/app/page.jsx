import { HeroSection } from "../components/home/hero-section"
import { FeaturedProjects } from "../components/home/featured-projects"
import { ImpactStats } from "../components/home/impact-stats"
import { CallToAction } from "../components/home/call-to-action"
import { Footer } from "../components/layout/footer"

const HomePage = () => {
  return (
    <div>
      <HeroSection />
      <FeaturedProjects />
      <ImpactStats />
      <CallToAction />
      <Footer />
    </div>
  )
}

export default HomePage
