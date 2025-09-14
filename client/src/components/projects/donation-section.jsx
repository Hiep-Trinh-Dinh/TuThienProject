"use client"

import { useState } from "react"
import { useAuth } from "../../contexts/auth-context"
import { Button } from "../ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"
import { Input } from "../ui/input"
import { Label } from "../ui/label"
import { RadioGroup, RadioGroupItem } from "../ui/radio-group"
import { Checkbox } from "../ui/checkbox"
import { Alert, AlertDescription } from "../ui/alert"
import { Heart, CreditCard, Shield, Users } from "lucide-react"
import { Link } from "react-router-dom"

const donationAmounts = [50000, 100000, 200000, 500000, 1000000, 2000000]

export function DonationSection({ project }) {
  const { user } = useAuth()
  const [selectedAmount, setSelectedAmount] = useState(100000)
  const [customAmount, setCustomAmount] = useState("")
  const [isCustom, setIsCustom] = useState(false)
  const [isMonthly, setIsMonthly] = useState(false)
  const [isAnonymous, setIsAnonymous] = useState(false)
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(false)

  const finalAmount = isCustom ? Number.parseFloat(customAmount) || 0 : selectedAmount

  const handleDonate = async (e) => {
    e.preventDefault()

    if (!user) {
      // Redirect to login with return URL
      const pid = project.projectId
      window.location.href = `/login?redirect=/projects/${pid}`
      return
    }

    if (finalAmount < 50000) {
      alert("Số tiền quyên góp tối thiểu là 50,000 VNĐ")
      return
    }

    setLoading(true)

    // Simulate donation processing
    setTimeout(() => {
      setSuccess(true)
      setLoading(false)

      // Reset form after success
      setTimeout(() => {
        setSuccess(false)
        setSelectedAmount(50)
        setCustomAmount("")
        setIsCustom(false)
      }, 3000)
    }, 2000)
  }

  if (success) {
    return (
      <Card className="sticky top-4">
        <CardContent className="p-6 text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Heart className="h-8 w-8 text-green-600" />
          </div>
          <h3 className="text-xl font-bold text-foreground mb-2">Thank You!</h3>
          <p className="text-muted-foreground mb-4">
            Quyên góp {finalAmount.toLocaleString()} VNĐ của bạn đã được xử lý thành công. Bạn đang tạo ra sự khác biệt thực sự!
          </p>
          <Button variant="outline" onClick={() => setSuccess(false)} className="w-full">
            Quyên góp lại
          </Button>
        </CardContent>
      </Card>
    )
  }

  return (
    <div className="space-y-6">
      <Card className="sticky top-4">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Heart className="h-5 w-5 text-primary" />
            Hỗ trợ dự án này
          </CardTitle>
          <CardDescription>Mỗi quyên góp đều giúp chúng ta tiến gần hơn đến mục tiêu</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleDonate} className="space-y-6">
            {/* Donation Amount Selection */}
            <div className="space-y-4">
              <Label>Chọn số tiền</Label>
              <RadioGroup
                value={isCustom ? "custom" : selectedAmount.toString()}
                onValueChange={(value) => {
                  if (value === "custom") {
                    setIsCustom(true)
                  } else {
                    setIsCustom(false)
                    setSelectedAmount(Number.parseInt(value))
                  }
                }}
              >
                <div className="grid grid-cols-2 gap-2">
                  {donationAmounts.map((amount) => (
                    <div key={amount} className="flex items-center space-x-2">
                      <RadioGroupItem value={amount.toString()} id={`amount-${amount}`} />
                      <Label htmlFor={`amount-${amount}`} className="cursor-pointer">
                        {amount.toLocaleString()} VNĐ
                      </Label>
                    </div>
                  ))}
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="custom" id="custom-amount" />
                  <Label htmlFor="custom-amount" className="cursor-pointer">
                    Số tiền tùy chỉnh
                  </Label>
                </div>
              </RadioGroup>

              {isCustom && (
                <div className="space-y-2">
                  <Label htmlFor="custom-input">Nhập số tiền (VNĐ)</Label>
                  <Input
                    id="custom-input"
                    type="number"
                    min="50000"
                    step="1000"
                    placeholder="Nhập số tiền"
                    value={customAmount}
                    onChange={(e) => setCustomAmount(e.target.value)}
                  />
                </div>
              )}
            </div>

            {/* Donation Options */}
            <div className="space-y-4">
              <div className="flex items-center space-x-2">
                <Checkbox id="monthly" checked={isMonthly} onCheckedChange={setIsMonthly} />
                <Label htmlFor="monthly" className="text-sm">
                  Quyên góp hàng tháng
                </Label>
              </div>

              <div className="flex items-center space-x-2">
                <Checkbox id="anonymous" checked={isAnonymous} onCheckedChange={setIsAnonymous} />
                <Label htmlFor="anonymous" className="text-sm">
                  Quyên góp ẩn danh
                </Label>
              </div>
            </div>

            {/* Donation Summary */}
            {finalAmount > 0 && (
              <div className="bg-primary/5 p-4 rounded-lg space-y-2">
                <div className="flex justify-between">
                  <span>Số tiền quyên góp:</span>
                  <span className="font-medium">{finalAmount.toLocaleString()} VNĐ</span>
                </div>
                {isMonthly && (
                  <div className="flex justify-between text-sm text-muted-foreground">
                    <span>Tần suất:</span>
                    <span>Hàng tháng</span>
                  </div>
                )}
                <div className="flex justify-between text-sm text-muted-foreground">
                  <span>Phí xử lý:</span>
                  <span>0 VNĐ (chúng tôi chi trả)</span>
                </div>
                <div className="border-t pt-2 flex justify-between font-medium">
                  <span>Tổng cộng:</span>
                  <span>{finalAmount.toLocaleString()} VNĐ</span>
                </div>
              </div>
            )}

            {/* Login Required Alert */}
            {!user && (
              <Alert>
                <Shield className="h-4 w-4" />
                <AlertDescription>
                  Vui lòng{" "}
                  <Link to={`/login?redirect=/projects/${project.projectId}`} className="text-primary hover:underline">
                    đăng nhập
                  </Link>{" "}
                  hoặc{" "}
                  <Link to={`/register?redirect=/projects/${project.projectId}`} className="text-primary hover:underline">
                    tạo tài khoản
                  </Link>{" "}
                  để quyên góp.
                </AlertDescription>
              </Alert>
            )}

            {/* Donate Button */}
            <Button type="submit" className="w-full" size="lg" disabled={loading || !user || finalAmount < 50000}>
              {loading ? (
                "Đang xử lý..."
              ) : (
                <>
                  <CreditCard className="h-4 w-4 mr-2" />
                  Quyên góp {finalAmount > 0 ? finalAmount.toLocaleString() : "0"} VNĐ
                  {isMonthly && "/tháng"}
                </>
              )}
            </Button>

            {/* Security Notice */}
            <div className="text-xs text-muted-foreground text-center space-y-1">
              <div className="flex items-center justify-center gap-1">
                <Shield className="h-3 w-3" />
                <span>Quyên góp an toàn với mã hóa tiêu chuẩn ngành</span>
              </div>
              <div>Quyên góp của bạn có thể được khấu trừ thuế. Biên lai sẽ được gửi qua email.</div>
            </div>
          </form>
        </CardContent>
      </Card>

      {/* Recent Donors */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Users className="h-5 w-5" />
            Người ủng hộ gần đây
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[
              { name: "Nguyễn Văn A", amount: 200000, time: "2 giờ trước" },
              { name: "Ẩn danh", amount: 100000, time: "5 giờ trước" },
              { name: "Trần Thị B", amount: 500000, time: "1 ngày trước" },
              { name: "Lê Văn C", amount: 150000, time: "2 ngày trước" },
            ].map((donor, index) => (
              <div key={index} className="flex justify-between items-center text-sm">
                <div>
                  <div className="font-medium">{donor.name}</div>
                  <div className="text-muted-foreground">{donor.time}</div>
                </div>
                <div className="font-medium text-primary">{donor.amount.toLocaleString()} VNĐ</div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
