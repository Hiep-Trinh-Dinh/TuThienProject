# Docker Quick Start Guide - TuThienProject

## 🐳 Cách chạy project với Docker

### 1. Yêu cầu
- Docker Desktop đã cài đặt
- Docker Compose đã cài đặt (thường đi kèm Docker Desktop)

### 2. Chạy toàn bộ ứng dụng (1 lệnh)

```bash
# Build và chạy tất cả services (MySQL, Backend, Frontend)
docker-compose up --build

# Hoặc chạy ở chế độ background
docker-compose up -d --build
```

**Ứng dụng sẽ chạy tại:**
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- MySQL: localhost:3307

### 3. Dừng ứng dụng

```bash
# Dừng containers
docker-compose down

# Dừng và xóa volumes (xóa data database)
docker-compose down -v
```

### 4. Xem logs

```bash
# Xem tất cả logs
docker-compose logs -f

# Xem log của service cụ thể
docker-compose logs -f server
docker-compose logs -f client
docker-compose logs -f mysql
```

### 5. Rebuild lại sau khi sửa code

```bash
# Rebuild service cụ thể
docker-compose up -d --build server
docker-compose up -d --build client

# Rebuild tất cả
docker-compose up -d --build
```

### 7. Kiểm tra trạng thái

```bash
# Xem containers đang chạy
docker-compose ps

# Xem resource usage
docker stats
```

## 📋 Thông tin đăng nhập

Sau khi containers chạy, database sẽ tự động import data từ `sample_admin_data.sql`.

## 🐛 Troubleshooting

### Backend không connect được MySQL
```bash
# Chờ MySQL khởi động hoàn toàn (30-60 giây)
# Hoặc restart backend container
docker-compose restart backend
```

### Xóa tất cả và start lại từ đầu
```bash
docker-compose down -v
docker-compose up --build
```

### Xem chi tiết lỗi
```bash
docker-compose logs server
docker-compose logs mysql
```

