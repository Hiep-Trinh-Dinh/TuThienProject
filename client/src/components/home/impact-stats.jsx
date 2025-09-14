import { Users, DollarSign, Globe, Heart } from "lucide-react"

const stats = [
  {
    icon: Users,
    value: "50,000+",
    label: "Lives Impacted",
    description: "People helped through our programs",
  },
  {
    icon: DollarSign,
    value: "$2.5M+",
    label: "Funds Raised",
    description: "Total donations received this year",
  },
  {
    icon: Globe,
    value: "25+",
    label: "Countries",
    description: "Global reach of our initiatives",
  },
  {
    icon: Heart,
    value: "1,200+",
    label: "Active Donors",
    description: "Generous supporters making change happen",
  },
]

export function ImpactStats() {
  return (
    <section className="py-16 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">Our Impact in Numbers</h2>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            See the tangible difference we're making together in communities worldwide
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {stats.map((stat, index) => {
            const Icon = stat.icon
            return (
              <div key={index} className="text-center group">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-primary/10 rounded-full mb-4 group-hover:bg-primary/20 transition-colors">
                  <Icon className="h-8 w-8 text-primary" />
                </div>
                <div className="text-3xl font-bold text-foreground mb-2">{stat.value}</div>
                <div className="text-lg font-semibold text-primary mb-1">{stat.label}</div>
                <div className="text-sm text-muted-foreground">{stat.description}</div>
              </div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
