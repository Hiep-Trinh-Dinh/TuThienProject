import { useEffect, useState } from "react"
import { Users, DollarSign, Globe, Heart } from "lucide-react"

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(value || 0)

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

export function ImpactStats() {
  const [globalStats, setGlobalStats] = useState({
    totalDonors: 0,
    totalRaisedAmount: 0,
  })

  useEffect(() => {
    let isMounted = true
    const fetchStats = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/api/donations/stats/global`)
        if (!response.ok) {
          throw new Error("Failed to fetch global stats")
        }
        const data = await response.json()
        if (isMounted) {
          setGlobalStats({
            totalDonors: Number(data.totalDonors) || 0,
            totalRaisedAmount: Number(data.totalRaisedAmount) || 0,
          })
        }
      } catch (error) {
        console.error("Failed to load global stats:", error)
      }
    }

    fetchStats()
    return () => {
      isMounted = false
    }
  }, [])

  const stats = [
    {
      icon: Users,
      value: `${globalStats.totalDonors.toLocaleString("vi-VN")}`,
      label: "Người ủng hộ",
      description: "Các mạnh thường quân đã đồng hành",
    },
    {
      icon: DollarSign,
      value: formatCurrency(globalStats.totalRaisedAmount),
      label: "Tổng số tiền quyên góp",
      description: "Tổng quyên góp cho toàn hệ thống",
    },
    {
      icon: Globe,
      value: "25+",
      label: "Quốc gia",
      description: "Phạm vi ảnh hưởng của các chương trình",
    },
    {
      icon: Heart,
      value: "1,200+",
      label: "Dự án đang hoạt động",
      description: "Các dự án đang được cộng đồng hỗ trợ",
    },
  ]

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
