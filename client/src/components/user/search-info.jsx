import { Badge } from "../ui/badge"
import { X } from "lucide-react"

const authProviderMap = {
  'FACEBOOK': 'Facebook',
  'GOOGLE': 'Google', 
  'LOCAL': 'Local',
  'GITHUB': 'Github',
}

const statusMap = {
  'ACTIVE': 'Đang hoạt động',
  'INACTIVE': 'Chờ duyệt',
  'BANNED': 'Đã đóng'
}

const sortMap = {
  'newest': 'Mới nhất',
}

export function SearchInfo({ 
  searchQuery, 
  selectedAuthProvider, 
  selectedStatus, 
  sortBy, 
  onClearFilter 
}) {
  const hasActiveFilters = searchQuery || 
    (selectedAuthProvider && selectedAuthProvider !== 'All Authentication Providers') || 
    (selectedStatus && selectedStatus !== 'all') || 
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
        {searchQuery && (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            Tìm kiếm: "{searchQuery}"
          </Badge>
        )}
        
        {selectedAuthProvider && selectedAuthProvider !== 'All Authentication Providers' && (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            Tài khoản: {authProviderMap[selectedAuthProvider] || selectedAuthProvider}
          </Badge>
        )}
        
        {selectedStatus && selectedStatus !== 'all' && (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            Trạng thái: {statusMap[selectedStatus] || selectedStatus}
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
