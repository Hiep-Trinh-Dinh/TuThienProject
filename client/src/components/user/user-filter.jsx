"use client"

import { Input } from "../ui/input"
import { Label } from "../ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../ui/select"
import { Search } from "lucide-react"

const authProviders = [
  "All Authentication Providers",
  "Facebook",
  "Google", 
  "Local",
  "Github"
]

const authProviderMap = {
  'FACEBOOK': 'Facebook',
  'GOOGLE': 'Google', 
  'LOCAL': 'Local',
  'GITHUB': 'Github'
}

export function UsersFilters({
  selectedAuthProvider,
  setSelectedAuthProvider,
  selectedStatus,
  setSelectedStatus,
  searchQuery,
  setSearchQuery,
  sortBy,
  setSortBy,
  totalResults,
}) {
  return (
    <div className="bg-white rounded-lg shadow-sm border p-6 mb-8">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
        {/* Search */}
        <div className="space-y-2">
          <Label htmlFor="search">Tìm kiếm người dùng</Label>
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              id="search"
              placeholder="Tìm kiếm theo Họ tên, Số điện  thoại, Địa chỉ Email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>

        {/* authProvider Filter */}
        <div className="space-y-2">
          <Label>Tài khoản đăng nhập bằng</Label>
          <Select value={selectedAuthProvider} onValueChange={setSelectedAuthProvider}>
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {authProviders.map((authProvider) => (
                <SelectItem key={authProvider} value={authProvider}>
                  {authProvider === "All Authentication Providers" ? "Tất cả" : authProviderMap[authProvider] || authProvider}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Status Filter */}
        <div className="space-y-2">
          <Label>Trạng thái</Label>
          <Select value={selectedStatus} onValueChange={setSelectedStatus}>
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Tất cả</SelectItem>
              <SelectItem value="ACTIVE">Đang hoạt động</SelectItem>
              <SelectItem value="INACTIVE">Không hoạt động</SelectItem>
              <SelectItem value="BANNED">Đã bị cấm</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Sort By */}
        <div className="space-y-2">
          <Label>Sắp xếp theo</Label>
          <Select value={sortBy} onValueChange={setSortBy}>
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="newest">Mới nhất</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="text-sm text-muted-foreground">
        Hiển thị {totalResults} người dùng
      </div>
    </div>
  )
}
