-- ============================================
-- TẠO DATABASE UTF8MB4
-- ============================================
CREATE DATABASE IF NOT EXISTS tuthiendb 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE tuthiendb;

-- ============================================
-- TẠO CÁC BẢNG
-- ============================================

-- Bảng forgot_password
CREATE TABLE IF NOT EXISTS forgot_password (
    forgot_password_id INT AUTO_INCREMENT PRIMARY KEY,
    otp INT,
    expiration_time DATETIME,
    user_user_id BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng invalidated_token
CREATE TABLE IF NOT EXISTS invalidated_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500),
    expiry_time TIMESTAMP,
    type VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng users
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    phone VARCHAR(20),
    status VARCHAR(50),
    auth_provider VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    avatar_url VARCHAR(500),
    cover_photo_url VARCHAR(500),
    forgot_password INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng confirmation_token
CREATE TABLE IF NOT EXISTS confirmation_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    confirmed_at TIMESTAMP NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng role
CREATE TABLE IF NOT EXISTS role (
    name VARCHAR(255) PRIMARY KEY,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng permission
CREATE TABLE IF NOT EXISTS permission (
    name VARCHAR(255) PRIMARY KEY,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng role_permissions
CREATE TABLE IF NOT EXISTS role_permissions (
    role_name VARCHAR(255),
    permissions_name VARCHAR(255),
    PRIMARY KEY (role_name, permissions_name),
    FOREIGN KEY (role_name) REFERENCES role(name) ON DELETE CASCADE,
    FOREIGN KEY (permissions_name) REFERENCES permission(name) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng users_roles
CREATE TABLE IF NOT EXISTS users_roles (
    user_user_id BIGINT,
    roles_name VARCHAR(255),
    PRIMARY KEY (user_user_id, roles_name),
    FOREIGN KEY (user_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (roles_name) REFERENCES role(name) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng projects
CREATE TABLE IF NOT EXISTS projects (
    project_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id BIGINT,
    title VARCHAR(255),
    description TEXT,
    image_url VARCHAR(500),
    category VARCHAR(100),
    goal_amount DECIMAL(15,2),
    raised_amount DECIMAL(15,2) DEFAULT 0,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng donations
CREATE TABLE IF NOT EXISTS donations (
    donation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT,
    donor_id BIGINT,
    amount DECIMAL(15,2),
    payment_method VARCHAR(100),
    payment_status VARCHAR(50),
    donated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
    FOREIGN KEY (donor_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng ambassadors
CREATE TABLE IF NOT EXISTS ambassadors (
    ambassador_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    project_id BIGINT,
    share_link VARCHAR(500),
    raised_amount DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng reports
CREATE TABLE IF NOT EXISTS reports (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT,
    content TEXT,
    file_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng news
CREATE TABLE IF NOT EXISTS news (
    news_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500),
    content TEXT,
    image_url VARCHAR(500),
    author_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng system_logs
CREATE TABLE IF NOT EXISTS system_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(255),
    ip_address VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng transactions_log
CREATE TABLE IF NOT EXISTS transactions_log (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donation_id BIGINT,
    action VARCHAR(255),
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donation_id) REFERENCES donations(donation_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- THÊM KHÓA NGOẠI SAU KHI TẠO BẢNG
-- ============================================
ALTER TABLE users ADD CONSTRAINT fk_users_forgot_password 
FOREIGN KEY (forgot_password) REFERENCES forgot_password(forgot_password_id) ON DELETE SET NULL;

-- ============================================
-- TẠO INDEXES CHO HIỆU NĂNG
-- ============================================

-- Indexes cho bảng users
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Indexes cho bảng projects
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_category ON projects(category);
CREATE INDEX idx_projects_created_at ON projects(created_at);
CREATE INDEX idx_projects_dates ON projects(start_date, end_date);

-- Indexes cho bảng donations
CREATE INDEX idx_donations_project_id ON donations(project_id);
CREATE INDEX idx_donations_donor_id ON donations(donor_id);
CREATE INDEX idx_donations_donated_at ON donations(donated_at);
CREATE INDEX idx_donations_status ON donations(payment_status);

-- Indexes cho bảng ambassadors
CREATE INDEX idx_ambassadors_user_id ON ambassadors(user_id);
CREATE INDEX idx_ambassadors_project_id ON ambassadors(project_id);

-- Indexes cho bảng news
CREATE INDEX idx_news_author_id ON news(author_id);
CREATE INDEX idx_news_created_at ON news(created_at);

-- Indexes cho bảng system_logs
CREATE INDEX idx_system_logs_user_id ON system_logs(user_id);
CREATE INDEX idx_system_logs_created_at ON system_logs(created_at);

-- ============================================
-- XÓA DỮ LIỆU CŨ (NẾU CÓ)
-- ============================================
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
DELETE FROM invalidated_token;
DELETE FROM permission;
DELETE FROM role;
DELETE FROM forgot_password;
DELETE FROM users;

-- ============================================
-- CHÈN DỮ LIỆU MẪU
-- ============================================

-- USERS
INSERT INTO users (full_name, email, password_hash, phone, status, auth_provider, created_at) VALUES
('Admin User', 'admin@gmail.com', '$2a$10$hashedAdminPwd', '0912345678', 'ACTIVE', NULL, '2023-01-01'),
('Nguyễn Văn A', 'user1@email.com', '$2a$10$hashedUser1', '0909999999', 'ACTIVE', NULL, '2023-02-15'),
('Trần Thị B', 'user2@email.com', '$2a$10$hashedUser2', '0911888888', 'BANNED', 'google', '2023-03-05'),
('Lê Văn C', 'user3@email.com', '$2a$10$hashedUser3', '0911555555', 'INACTIVE', NULL, '2023-04-10'),
('Phạm Thị D', 'user4@email.com', '$2a$10$hashedUser4', '0912777777', 'ACTIVE', NULL, '2023-05-20'),
('Hoàng Văn E', 'user5@email.com', '$2a$10$hashedUser5', '0913666666', 'ACTIVE', 'facebook', '2023-06-15');

-- ROLE
INSERT INTO role (name, description) VALUES
('admin', 'Quản trị viên - có toàn quyền quản lý hệ thống'),
('user', 'Người dùng thường - có quyền xem và quyên góp'),
('moderator', 'Người kiểm duyệt - có quyền duyệt dự án');

-- PERMISSION
INSERT INTO permission (name, description) VALUES
('PROJECT_CREATE', 'Quyền tạo dự án'),
('PROJECT_APPROVE', 'Quyền duyệt dự án'),
('PROJECT_DELETE', 'Quyền xóa dự án'),
('USER_MANAGE', 'Quyền quản lý người dùng'),
('ROLE_MANAGE', 'Quyền quản lý quyền'),
('DONATION_VIEW', 'Quyền xem quyên góp');

-- ROLE_PERMISSIONS
INSERT INTO role_permissions (role_name, permissions_name) VALUES
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

-- USERS_ROLES
INSERT INTO users_roles (user_user_id, roles_name) VALUES
(1, 'admin'),
(2, 'user'),
(3, 'user'),
(4, 'user'),
(5, 'moderator'),
(6, 'user');

-- PROJECTS
INSERT INTO projects (org_id, title, description, category, goal_amount, raised_amount, start_date, end_date, status, created_at) VALUES
(1, 'Cứu trợ lũ lụt miền Trung', 'Hỗ trợ người dân vùng lũ lụt miền Trung với quỹ cứu trợ khẩn cấp', 'thien_tai', 20000000, 8000000, '2023-10-01', '2023-12-15', 'active', '2023-09-01'),
(1, 'Chương trình Mùa đông ấm', 'Tặng áo ấm cho trẻ em miền núi phía Bắc', 'tre_em', 15000000, 4000000, '2023-11-01', '2023-12-20', 'pending', '2023-10-10'),
(2, 'Hỗ trợ bệnh nhi ung thư', 'Gây quỹ cho các cháu điều trị ung thư tại bệnh viện Nhi Trung ương', 'y_te', 30000000, 20000000, '2023-05-15', '2023-09-01', 'closed', '2023-03-01'),
(2, 'Xây dựng trường học vùng cao', 'Xây dựng trường học cho trẻ em vùng cao Tây Bắc', 'tre_em', 50000000, 15000000, '2023-08-01', '2024-02-28', 'active', '2023-07-15'),
(3, 'Bảo vệ môi trường biển', 'Chiến dịch làm sạch bãi biển và bảo vệ sinh vật biển', 'moi_truong', 10000000, 3000000, '2023-09-01', '2023-12-31', 'active', '2023-08-20');

-- DONATIONS
INSERT INTO donations (project_id, donor_id, amount, payment_method, payment_status, donated_at) VALUES
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

-- AMBASSADORS
INSERT INTO ambassadors (user_id, project_id, share_link, raised_amount, created_at) VALUES
(2, 1, 'https://mycharity.com/share/abc123', 600000, '2023-10-10 08:00:00'),
(3, 2, 'https://mycharity.com/share/xyz456', 200000, '2023-11-03 10:30:00'),
(4, 1, 'https://mycharity.com/share/def789', 2000000, '2023-10-28 12:00:00'),
(5, 4, 'https://mycharity.com/share/ghi012', 3000000, '2023-08-20 09:15:00'),
(6, 3, 'https://mycharity.com/share/jkl345', 1500000, '2023-06-05 14:20:00');

-- REPORTS
INSERT INTO reports (project_id, content, file_url, created_at) VALUES
(1, 'Báo cáo tiến độ cứu trợ miền Trung đợt 1 - Đã phân phát 800 suất quà cho các hộ gia đình', NULL, '2023-11-01 10:00:00'),
(1, 'Báo cáo tiến độ cứu trợ miền Trung đợt 2 - Đã xây dựng 5 căn nhà tạm', 'uploads/report-cuutro-dot2.pdf', '2023-11-15 14:30:00'),
(2, 'Tổng hợp quỹ áo ấm đã trao - Đã phân phát 500 bộ áo ấm cho trẻ em', 'uploads/report-muadongam.pdf', '2023-12-18 09:00:00'),
(3, 'Báo cáo cuối kỳ - Đã hỗ trợ 20 bệnh nhi điều trị ung thư', 'uploads/report-benh-nhi.pdf', '2023-08-30 16:00:00'),
(4, 'Báo cáo quý 1 - Đã hoàn thành 30% công trình xây dựng', 'uploads/report-truong-hoc-q1.pdf', '2023-10-15 11:00:00');

-- NEWS
INSERT INTO news (title, content, image_url, author_id, created_at) VALUES
('Lễ phát động quyên góp cứu trợ miền Trung', 'Chiều ngày 15/9, tại Hà Nội đã diễn ra lễ phát động quyên góp cứu trợ miền Trung...', NULL, 1, '2023-11-02 10:00:00'),
('Tổng kết chương trình Mùa đông ấm 2023', 'Chương trình Mùa đông ấm đã hoàn thành với hơn 500 bộ áo ấm được trao tặng...', 'uploads/img-mda.jpg', 1, '2023-12-25 14:00:00'),
('Khởi công xây dựng trường học vùng cao', 'Dự án xây dựng trường học cho trẻ em vùng cao đã chính thức khởi công...', 'uploads/img-truong-hoc.jpg', 1, '2023-08-01 09:00:00'),
('Hỗ trợ thành công 20 bệnh nhi ung thư', 'Chương trình hỗ trợ bệnh nhi ung thư đã hoàn thành với tổng số tiền 20 triệu đồng...', NULL, 1, '2023-09-01 11:30:00'),
('Chiến dịch làm sạch bãi biển thành công', 'Hơn 200 tình nguyện viên đã tham gia chiến dịch làm sạch bãi biển...', 'uploads/img-bien.jpg', 1, '2023-10-15 15:00:00');

-- SYSTEM LOGS
INSERT INTO system_logs (user_id, action, ip_address, created_at) VALUES
(1, 'login', '127.0.0.1', NOW()),
(2, 'create_project', '192.168.1.100', '2023-09-01 08:00:00'),
(2, 'login', '192.168.1.100', '2023-10-20 10:00:00'),
(3, 'login', '192.168.1.101', '2023-11-03 10:30:00'),
(4, 'donate', '192.168.1.102', '2023-11-01 09:15:00'),
(5, 'create_project', '192.168.1.103', '2023-07-15 14:00:00'),
(6, 'donate', '192.168.1.104', '2023-06-10 10:00:00'),
(1, 'update_project_status', '127.0.0.1', '2023-11-01 10:30:00');

-- TRANSACTIONS LOG
INSERT INTO transactions_log (donation_id, action, log_time) VALUES
(1, 'donated', '2023-10-20 10:30:00'),
(2, 'donated', '2023-10-25 14:20:00'),
(3, 'donated', '2023-11-01 09:15:00'),
(4, 'donated', '2023-11-10 16:45:00'),
(5, 'donated', '2023-11-15 11:30:00'),
(6, 'donated', '2023-05-20 13:20:00'),
(7, 'donated', '2023-06-10 10:00:00'),
(8, 'donated', '2023-08-15 15:30:00'),
(9, 'donated', '2023-09-01 09:00:00'),
(10, 'donated', '2023-09-10 14:15:00');

-- ============================================
-- KIỂM TRA DỮ LIỆU
-- ============================================
SELECT 'Database created successfully!' as status;

SELECT 
    (SELECT COUNT(*) FROM users) as user_count,
    (SELECT COUNT(*) FROM projects) as project_count,
    (SELECT COUNT(*) FROM donations) as donation_count,
    (SELECT SUM(amount) FROM donations WHERE payment_status = 'success') as total_donations;