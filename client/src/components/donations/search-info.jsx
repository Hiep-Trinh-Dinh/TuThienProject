import { Badge } from "../ui/badge"
import { X } from "lucide-react"

const paymentMethodsMap = {
  'vnpay': 'VNPay',
  'viettel_money': 'Viettel Money', 
  'momo': 'Momo',
  'credit_card': 'Credit Card',
  'bank_transfer': 'Bank Transfer'
}

const paymentStatusMap = {
  'success': 'Thành công',
  'pending': 'Đang chờ',
  'failed': 'Thất bại'
}

const sortMap = {
  'newest': 'Mới nhất',
  'highest': 'Số tiền quyên góp cao nhất',
}

export function SearchInfo({ 
  selectedPaymentMethod,
  selectedPaymentStatus,
  selectedFrom,
  selectedTo,
  selectedAmountFrom,
  selectedAmountTo, 
  sortBy, 
  onClearFilter 
}) {
  const hasActiveFilters =
    (selectedPaymentMethod && selectedPaymentMethod !== 'All Payment Methods') ||
    (selectedPaymentStatus && selectedPaymentStatus !== 'all') ||
    (selectedFrom && selectedFrom !== '') ||
    (selectedTo && selectedTo !== '') ||
    (selectedAmountFrom && selectedAmountFrom !== '') ||
    (selectedAmountTo && selectedAmountTo !== '') ||
    (sortBy && sortBy !== 'newest')

  if (!hasActiveFilters) {
    return null
  }

  return (
    <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
      <div className="flex items-center justify-between mb-2">
        <h3 className="text-sm font-medium text-blue-900">Bộ lọc hiện tại:</h3>
        <button
          onClick={onClearFilter}
          className="text-blue-600 hover:text-blue-800 text-sm flex items-center gap-1"
        >
          <X className="h-3 w-3" />
          Xóa tất cả
        </button>
      </div>
      
      <div className="flex flex-wrap gap-2">
        
        {selectedPaymentMethod && selectedPaymentMethod !== 'All Payment Methods' && (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            Phương Thức Thanh Toán: {paymentMethodsMap[selectedPaymentMethod] || selectedPaymentMethod}
          </Badge>
        )}
        
        {selectedPaymentStatus && selectedPaymentStatus !== 'all' && (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            Trạng Thái: {paymentStatusMap[selectedPaymentStatus] || selectedPaymentStatus}
          </Badge>
        )}

        {((selectedFrom && selectedFrom !== '') && (selectedTo && selectedTo !== '') ) && (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            Bắt Đầu: {selectedFrom} - Kết Thúc: {selectedTo}
          </Badge>
        )}

        {((selectedAmountFrom && selectedAmountFrom !== '') && (selectedAmountTo && selectedAmountTo !== '') ) && (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            Từ: {Number(selectedAmountFrom || 0).toLocaleString()}đ - Đến: {Number(selectedAmountTo || 0).toLocaleString()}đ
          </Badge>
        )}
        
        {sortBy && sortBy !== 'newest' && (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            Sắp xếp: {sortMap[sortBy] || sortBy}
          </Badge>
        )}
      </div>
    </div>
  )
}
