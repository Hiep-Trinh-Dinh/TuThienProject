-- Dữ liệu mẫu cho admin dashboard (tương thích với data.sql mới - BIGINT)
-- Lưu ý: Xóa dữ liệu cũ trước khi insert để tránh lỗi duplicate

-- Xóa dữ liệu cũ (theo thứ tự ngược với foreign key dependencies)
DELETE FROM transactions_log;
DELETE FROM system_logs;
DELETE FROM news;
DELETE FROM reports;
DELETE FROM ambassadors;
DELETE FROM donations;
DELETE FROM projects;
DELETE FROM role_permissions;
DELETE FROM users_roles;
DELETE FROM confirmation_token;
DELETE FROM forgot_password;
DELETE FROM invalidated_token;
DELETE FROM permission;
DELETE FROM role;
DELETE FROM users;

-- ============================================
-- 1. USERS (Bảng người dùng)
-- ============================================
INSERT INTO users (full_name, email, password_hash, phone, status, auth_provider, created_at)
VALUES
('Admin User', 'admin@gmail.com', '$2a$10$hashedAdminPwd', '0912345678', 'ACTIVE', NULL, '2023-01-01'),
('Nguyễn Văn A', 'user1@email.com', '$2a$10$hashedUser1', '0909999999', 'ACTIVE', NULL, '2023-02-15'),
('Trần Thị B', 'user2@email.com', '$2a$10$hashedUser2', '0911888888', 'BANNED', 'google', '2023-03-05'),
('Lê Văn C', 'user3@email.com', '$2a$10$hashedUser3', NULL, 'INACTIVE', NULL, '2023-04-10'),
('Phạm Thị D', 'user4@email.com', '$2a$10$hashedUser4', '0912777777', 'ACTIVE', NULL, '2023-05-20'),
('Hoàng Văn E', 'user5@email.com', '$2a$10$hashedUser5', '0913666666', 'ACTIVE', 'facebook', '2023-06-15');

-- ============================================
-- 2. PROJECTS (Bảng dự án từ thiện)
-- ============================================
INSERT INTO projects (org_id, title, description, category, goal_amount, raised_amount, start_date, end_date, status, created_at)
VALUES
(1, 'Cứu trợ lũ lụt miền Trung', 'Hỗ trợ người dân vùng lũ lụt miền Trung với quỹ cứu trợ khẩn cấp', 'thien_tai', 20000000, 8000000, '2023-10-01', '2023-12-15', 'active', '2023-09-01'),
(1, 'Chương trình Mùa đông ấm', 'Tặng áo ấm cho trẻ em miền núi phía Bắc', 'tre_em', 15000000, 4000000, '2023-11-01', '2023-12-20', 'pending', '2023-10-10'),
(2, 'Hỗ trợ bệnh nhi ung thư', 'Gây quỹ cho các cháu điều trị ung thư tại bệnh viện Nhi Trung ương', 'y_te', 30000000, 20000000, '2023-05-15', '2023-09-01', 'closed', '2023-03-01'),
(2, 'Xây dựng trường học vùng cao', 'Xây dựng trường học cho trẻ em vùng cao Tây Bắc', 'tre_em', 50000000, 15000000, '2023-08-01', '2024-02-28', 'active', '2023-07-15'),
(3, 'Bảo vệ môi trường biển', 'Chiến dịch làm sạch bãi biển và bảo vệ sinh vật biển', 'moi_truong', 10000000, 3000000, '2023-09-01', '2023-12-31', 'active', '2023-08-20');

-- ============================================
-- 3. DONATIONS (Bảng quyên góp)
-- ============================================
INSERT INTO donations (project_id, donor_id, amount, payment_method, payment_status, donated_at)
VALUES
(1, 2, 1000000, 'vnpay', 'success', '2023-10-20 10:30:00'),
(1, 3, 500000, 'momo', 'success', '2023-10-25 14:20:00'),
(1, 4, 2000000, 'bank_transfer', 'success', '2023-11-01 09:15:00'),
(2, 2, 200000, 'viettel_money', 'pending', '2023-11-10 16:45:00'),
(2, 5, 500000, 'vnpay', 'success', '2023-11-15 11:30:00'),
(3, 4, 700000, 'bank_transfer', 'success', '2023-05-20 13:20:00'),
(3, 6, 1500000, 'momo', 'success', '2023-06-10 10:00:00'),
(4, 2, 1000000, 'vnpay', 'success', '2023-08-15 15:30:00'),
(4, 5, 2000000, 'bank_transfer', 'success', '2023-09-01 09:00:00'),
(5, 3, 500000, 'momo', 'success', '2023-09-10 14:15:00');

-- ============================================
-- 4. AMBASSADORS (Bảng sứ giả gây quỹ)
-- ============================================
INSERT INTO ambassadors (user_id, project_id, share_link, raised_amount, created_at)
VALUES
(2, 1, 'https://mycharity.com/share/abc123', 600000, '2023-10-10 08:00:00'),
(3, 2, 'https://mycharity.com/share/xyz456', 200000, '2023-11-03 10:30:00'),
(4, 1, 'https://mycharity.com/share/def789', 2000000, '2023-10-28 12:00:00'),
(5, 4, 'https://mycharity.com/share/ghi012', 3000000, '2023-08-20 09:15:00'),
(6, 3, 'https://mycharity.com/share/jkl345', 1500000, '2023-06-05 14:20:00');

-- ============================================
-- 5. REPORTS (Bảng báo cáo dự án)
-- ============================================
INSERT INTO reports (project_id, content, file_url, created_at)
VALUES
(1, 'Báo cáo tiến độ cứu trợ miền Trung đợt 1 - Đã phân phát 800 suất quà cho các hộ gia đình', NULL, '2023-11-01 10:00:00'),
(1, 'Báo cáo tiến độ cứu trợ miền Trung đợt 2 - Đã xây dựng 5 căn nhà tạm', 'uploads/report-cuutro-dot2.pdf', '2023-11-15 14:30:00'),
(2, 'Tổng hợp quỹ áo ấm đã trao - Đã phân phát 500 bộ áo ấm cho trẻ em', 'uploads/report-muadongam.pdf', '2023-12-18 09:00:00'),
(3, 'Báo cáo cuối kỳ - Đã hỗ trợ 20 bệnh nhi điều trị ung thư', 'uploads/report-benh-nhi.pdf', '2023-08-30 16:00:00'),
(4, 'Báo cáo quý 1 - Đã hoàn thành 30% công trình xây dựng', 'uploads/report-truong-hoc-q1.pdf', '2023-10-15 11:00:00');

-- ============================================
-- 6. NEWS (Bảng tin tức)
-- ============================================
INSERT INTO news (title, content, image_url, author_id, created_at)
VALUES
('Lễ phát động quyên góp cứu trợ miền Trung', 'Chiều ngày 15/9, tại Hà Nội đã diễn ra lễ phát động quyên góp cứu trợ miền Trung...', NULL, 1, '2023-11-02 10:00:00'),
('Tổng kết chương trình Mùa đông ấm 2023', 'Chương trình Mùa đông ấm đã hoàn thành với hơn 500 bộ áo ấm được trao tặng...', 'uploads/img-mda.jpg', 1, '2023-12-25 14:00:00'),
('Khởi công xây dựng trường học vùng cao', 'Dự án xây dựng trường học cho trẻ em vùng cao đã chính thức khởi công...', 'uploads/img-truong-hoc.jpg', 1, '2023-08-01 09:00:00'),
('Hỗ trợ thành công 20 bệnh nhi ung thư', 'Chương trình hỗ trợ bệnh nhi ung thư đã hoàn thành với tổng số tiền 20 triệu đồng...', NULL, 1, '2023-09-01 11:30:00'),
('Chiến dịch làm sạch bãi biển thành công', 'Hơn 200 tình nguyện viên đã tham gia chiến dịch làm sạch bãi biển...', 'uploads/img-bien.jpg', 1, '2023-10-15 15:00:00');

-- ============================================
-- 7. SYSTEM LOGS (Bảng nhật ký hệ thống)
-- ============================================
INSERT INTO system_logs (user_id, action, ip_address, created_at)
VALUES
(1, 'login', '127.0.0.1', NOW()),
(2, 'create_project', '192.168.1.100', '2023-09-01 08:00:00'),
(2, 'login', '192.168.1.100', '2023-10-20 10:00:00'),
(3, 'login', '192.168.1.101', '2023-11-03 10:30:00'),
(4, 'donate', '192.168.1.102', '2023-11-01 09:15:00'),
(5, 'create_project', '192.168.1.103', '2023-07-15 14:00:00'),
(6, 'donate', '192.168.1.104', '2023-06-10 10:00:00'),
(1, 'update_project_status', '127.0.0.1', '2023-11-01 10:00:00'),
(2, 'update_profile', '192.168.1.100', '2023-10-15 16:00:00');

-- ============================================
-- 8. TRANSACTIONS LOG (Bảng nhật ký giao dịch)
-- ============================================
INSERT INTO transactions_log (donation_id, action, log_time)
VALUES
(1, 'created', '2023-10-20 10:30:00'),
(1, 'success', '2023-10-20 10:30:15'),
(2, 'created', '2023-10-25 14:20:00'),
(2, 'success', '2023-10-25 14:20:10'),
(3, 'created', '2023-11-01 09:15:00'),
(3, 'success', '2023-11-01 09:15:20'),
(4, 'created', '2023-11-10 16:45:00'),
(4, 'pending', '2023-11-10 16:45:00'),
(5, 'created', '2023-11-15 11:30:00'),
(5, 'success', '2023-11-15 11:30:12'),
(6, 'created', '2023-05-20 13:20:00'),
(6, 'success', '2023-05-20 13:20:18'),
(7, 'created', '2023-06-10 10:00:00'),
(7, 'success', '2023-06-10 10:00:25'),
(8, 'created', '2023-08-15 15:30:00'),
(8, 'success', '2023-08-15 15:30:10'),
(9, 'created', '2023-09-01 09:00:00'),
(9, 'success', '2023-09-01 09:00:15'),
(10, 'created', '2023-09-10 14:15:00'),
(10, 'success', '2023-09-10 14:15:20');

-- ============================================
-- 9. ROLES (Bảng quyền)
-- ============================================
INSERT INTO role (name, description)
VALUES
('admin', 'Quản trị viên - có toàn quyền quản lý hệ thống'),
('user', 'Người dùng thường - có quyền xem và quyên góp'),
('moderator', 'Người kiểm duyệt - có quyền duyệt dự án');

-- ============================================
-- 10. PERMISSIONS (Bảng phân quyền)
-- ============================================
INSERT INTO permission (name, description)
VALUES
('PROJECT_CREATE', 'Quyền tạo dự án'),
('PROJECT_APPROVE', 'Quyền duyệt dự án'),
('PROJECT_DELETE', 'Quyền xóa dự án'),
('USER_MANAGE', 'Quyền quản lý người dùng'),
('ROLE_MANAGE', 'Quyền quản lý quyền'),
('DONATION_VIEW', 'Quyền xem quyên góp');

-- ============================================
-- 11. ROLE_PERMISSIONS (Gán permission cho role)
-- ============================================
INSERT INTO role_permissions (role_name, permissions_name)
VALUES
('admin', 'PROJECT_CREATE'),
('admin', 'PROJECT_APPROVE'),
('admin', 'PROJECT_DELETE'),
('admin', 'USER_MANAGE'),
('admin', 'ROLE_MANAGE'),
('admin', 'DONATION_VIEW'),
('moderator', 'PROJECT_APPROVE'),
('moderator', 'DONATION_VIEW'),
('user', 'PROJECT_CREATE'),
('user', 'DONATION_VIEW');

-- ============================================
-- 12. USERS_ROLES (Gán role cho user)
-- ============================================
INSERT INTO users_roles (user_user_id, roles_name)
VALUES
(1, 'admin'),  -- Admin User có quyền admin
(2, 'user'),   -- Nguyễn Văn A có quyền user
(3, 'user'),   -- Trần Thị B có quyền user
(4, 'user'),   -- Lê Văn C có quyền user
(5, 'moderator'), -- Phạm Thị D có quyền moderator
(6, 'user');   -- Hoàng Văn E có quyền user

-- Kết thúc dữ liệu mẫu
