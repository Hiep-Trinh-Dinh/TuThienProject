import { Button } from "../ui/button"
import { Heart, ArrowRight } from "lucide-react"
import { Link } from "react-router-dom"

export function CallToAction() {
  return (
    <section className="py-20 bg-primary text-primary-foreground">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div className="flex justify-center mb-6">
          <Heart className="h-12 w-12" />
        </div>

        <h2 className="text-3xl md:text-4xl font-bold mb-6 text-balance">Ready to Make a Difference?</h2>

        <p className="text-xl mb-8 opacity-90 text-pretty">
          Join our community of changemakers and help us create a better world for everyone. Your contribution, no
          matter the size, can transform lives and build hope.
        </p>

        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <Button size="lg" variant="secondary" asChild className="text-lg px-8 py-6">
            <Link to="/register">
              Start Your Journey
              <ArrowRight className="ml-2 h-5 w-5" />
            </Link>
          </Button>
          <Button
            size="lg"
            variant="outline"
            asChild
            className="text-lg px-8 py-6 border-primary-foreground text-primary-foreground hover:bg-primary-foreground hover:text-primary bg-transparent"
          >
            <Link to="/projects">Browse Projects</Link>
          </Button>
        </div>
      </div>
    </section>
  )
}
