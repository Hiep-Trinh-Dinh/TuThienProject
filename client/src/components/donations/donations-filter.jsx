"use client"

import { Input } from "../ui/input"
import { Label } from "../ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../ui/select"

const paymentMethods = [
  "All Payment Methods",
  "vnpay",
  "viettel_money", 
  "momo",
  "credit_card",
  "bank_transfer"
]

const paymentMethodsMap = {
  'vnpay': 'VNPay',
  'viettel_money': 'Viettel Money', 
  'momo': 'Momo',
  'credit_card': 'Credit Card',
  'bank_transfer': 'Bank Transfer'
}

export function DonationsFilters({
  selectedPaymentMethod,
  setSelectedPaymentMethod,
  selectedPaymentStatus,
  setSelectedPaymentStatus,
  selectedFrom,
  setSelectedFrom,
  selectedTo,
  setSelectedTo,
  selectedAmountFrom,
  setSelectedAmountFrom,
  selectedAmountTo,
  setSelectedAmountTo,
  sortBy,
  setSortBy,
  totalResults,
}) {
  return (
    <div className="bg-white rounded-lg shadow-sm border p-6 mb-8">
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4 mb-4">

        {/* Payment Method Filter */}
        <div className="space-y-2">
          <Label>Phương Thức Thanh Toán</Label>
          <Select value={selectedPaymentMethod} onValueChange={setSelectedPaymentMethod}>
            <SelectTrigger>
              <SelectValue placeholder="Chọn phương thức" />
            </SelectTrigger>
            <SelectContent>
              {paymentMethods.map((paymentMethod) => (
                <SelectItem key={paymentMethod} value={paymentMethod}>
                  {paymentMethod === "All Payment Methods" ? 
                    "Tất cả phương thức thanh toán" 
                    : paymentMethodsMap[paymentMethod] || paymentMethod}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Payment Status Filter */}
        <div className="space-y-2">
          <Label>Trạng Thái Thanh Toán</Label>
          <Select value={selectedPaymentStatus} onValueChange={setSelectedPaymentStatus}>
            <SelectTrigger>
              <SelectValue placeholder="Chọn trạng thái" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Tất cả</SelectItem>
              <SelectItem value="success">Thành công</SelectItem>
              <SelectItem value="pending">Đang chờ</SelectItem>
              <SelectItem value="failed">Thất bại</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Sort By */}
        <div className="space-y-2">
          <Label>Sắp xếp theo</Label>
          <Select value={sortBy} onValueChange={setSortBy}>
            <SelectTrigger>
              <SelectValue placeholder="Sắp xếp" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="newest">Mới nhất</SelectItem>
              <SelectItem value="highest">Số tiền quyên góp cao nhất</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* FROM DATE */}
        <div className="space-y-2">
          <Label>Từ ngày</Label>
          <Input
            type="date"
            value={selectedFrom || ""}
            onChange={(e) => setSelectedFrom(e.target.value)}
          />
        </div>

        {/* TO DATE */}
        <div className="space-y-2">
          <Label>Đến ngày</Label>
          <Input
            type="date"
            value={selectedTo || ""}
            onChange={(e) => setSelectedTo(e.target.value)}
          />
        </div>

        {/* AMOUNT FROM */}
        <div className="space-y-2">
          <Label>Số tiền từ</Label>
          <Input
            type="number"
            placeholder="0"
            min="0"
            value={selectedAmountFrom || ""}
            onChange={(e) => setSelectedAmountFrom(e.target.value)}
          />
        </div>

        {/* AMOUNT TO */}
        <div className="space-y-2">
          <Label>Số tiền đến</Label>
          <Input
            type="number"
            placeholder="0"
            min="0"
            value={selectedAmountTo || ""}
            onChange={(e) => setSelectedAmountTo(e.target.value)}
          />
        </div>

      </div>

      <div className="text-sm text-muted-foreground">
        Hiển thị {totalResults} quyên góp
      </div>
    </div>
  )
}
