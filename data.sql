-- Create Database
CREATE DATABASE IF NOT EXISTS tuthiendb;
USE tuthiendb;

-- 1. Bảng users
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'INACTIVE',
    auth_provider VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    avatar_url VARCHAR(500),
    cover_photo_url VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Bảng projects
CREATE TABLE projects (
    project_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category ENUM('tre_em','y_te','moi_truong','thien_tai','khac') NOT NULL,
    goal_amount DECIMAL(15,2) NOT NULL,
    raised_amount DECIMAL(15,2) DEFAULT 0,
    start_date DATE,
    end_date DATE,
    status ENUM('pending','active','closed','rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (org_id) REFERENCES users(user_id),
    image_url VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Bảng donations
CREATE TABLE donations (
    donation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    donor_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    payment_method ENUM('vnpay','viettel_money','momo','credit_card','bank_transfer') NOT NULL,
    payment_status ENUM('success','pending','failed') DEFAULT 'failed',
    donated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_id VARCHAR(100),
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    FOREIGN KEY (donor_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Bảng ambassadors (chỉnh về BIGINT)
CREATE TABLE ambassadors (
    ambassador_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    share_link VARCHAR(255),
    raised_amount DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Bảng reports (BIGINT cho FK)
CREATE TABLE reports (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    file_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Bảng news
CREATE TABLE news (
    news_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    image_url VARCHAR(255),
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Bảng transactions_log
CREATE TABLE transactions_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donation_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donation_id) REFERENCES donations(donation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Bảng system_logs
CREATE TABLE system_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(200) NOT NULL,
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Bảng role (quyền)
CREATE TABLE role (
    name VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Bảng permission (phân quyền)
CREATE TABLE permission (
    name VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. Bảng users_roles (junction table: User <-> Role)
CREATE TABLE users_roles (
    user_user_id BIGINT NOT NULL,
    roles_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_user_id, roles_name),
    FOREIGN KEY (user_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (roles_name) REFERENCES role(name) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. Bảng role_permissions (junction table: Role <-> Permission)
CREATE TABLE role_permissions (
    role_name VARCHAR(50) NOT NULL,
    permissions_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (role_name, permissions_name),
    FOREIGN KEY (role_name) REFERENCES role(name) ON DELETE CASCADE,
    FOREIGN KEY (permissions_name) REFERENCES permission(name) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. Bảng confirmation_token
CREATE TABLE confirmation_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    create_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. Bảng forgot_password
CREATE TABLE forgot_password (
    forgot_password_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    otp INT NOT NULL,
    expiration_time DATETIME NOT NULL,
    user_user_id BIGINT,
    FOREIGN KEY (user_user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. Bảng invalidated_token
CREATE TABLE invalidated_token (
    id VARCHAR(255) PRIMARY KEY,
    expired_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- USERS
INSERT INTO users (full_name, email, password_hash, phone, status, auth_provider, created_at) VALUES
('Admin User', 'adminfake@gmail.com', '$2a$10$hashedAdminPwd', '0912345678', 'ACTIVE', NULL, '2023-01-01'),
('Nguyễn Văn A', 'user1@email.com', '$2a$10$hashedUser1', '0909999999', 'ACTIVE', NULL, '2023-02-15'),
('Trần Thị B', 'user2@email.com', '$2a$10$hashedUser2', '0911888888', 'BANNED', 'google', '2023-03-05'),
('Lê Văn C', 'user3@email.com', '$2a$10$hashedUser3', '0911555555', 'INACTIVE', NULL, '2023-04-10'),
('Phạm Thị D', 'user4@email.com', '$2a$10$hashedUser4', '0912777777', 'ACTIVE', NULL, '2023-05-20'),
('Hoàng Văn E', 'user5@email.com', '$2a$10$hashedUser5', '0913666666', 'ACTIVE', 'facebook', '2023-06-15');

-- ROLE
INSERT INTO role (name, description) VALUES
('admin', 'Quản trị viên - có toàn quyền quản lý hệ thống'),
('user', 'Người dùng thường - có quyền xem và quyên góp'),
('vip_user', 'Người dùng VIP - có quyền tạo dự án cá nhân'),
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
('user', 'DONATION_VIEW'),
('vip_user', 'PROJECT_CREATE');

-- USERS_ROLES
INSERT INTO users_roles (user_user_id, roles_name) VALUES
(1, 'admin'),
(2, 'user'),
(3, 'user'),
(4, 'user'),
(5, 'moderator'),
(6, 'user');

-- PROJECTS
INSERT INTO projects (org_id, title, description, category, goal_amount, raised_amount, start_date, end_date, status, created_at, image_url) VALUES
(1, 'Cứu trợ lũ lụt miền Trung', 'Hỗ trợ người dân vùng lũ lụt miền Trung với quỹ cứu trợ khẩn cấp', 'thien_tai', 20000000, 8000000, '2023-10-01', '2023-12-15', 'active', '2023-09-01', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764756189/projects/projects/project_20251203170305394.jpg.jpg'),
(1, 'Chương trình Mùa đông ấm', 'Tặng áo ấm cho trẻ em miền núi phía Bắc', 'tre_em', 15000000, 4000000, '2023-11-01', '2023-12-20', 'pending', '2023-10-10', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764756220/projects/projects/project_20251203170334078.jpg.jpg'),
(2, 'Hỗ trợ bệnh nhi ung thư', 'Gây quỹ cho các cháu điều trị ung thư tại bệnh viện Nhi Trung ương', 'y_te', 30000000, 20000000, '2023-05-15', '2023-09-01', 'closed', '2023-03-01', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764756239/projects/projects/project_20251203170355394.jpg.jpg'),
(2, 'Xây dựng trường học vùng cao', 'Xây dựng trường học cho trẻ em vùng cao Tây Bắc', 'tre_em', 50000000, 15000000, '2023-08-01', '2024-02-28', 'active', '2023-07-15', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764756251/projects/projects/project_20251203170406512.jpg.jpg'),
(3, 'Bảo vệ môi trường biển', 'Chiến dịch làm sạch bãi biển và bảo vệ sinh vật biển', 'moi_truong', 10000000, 3000000, '2023-09-01', '2023-12-31', 'active', '2023-08-20', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764756269/projects/projects/project_20251203170426088.jpg.jpg'),
(1, 'Bữa Ăn Ấm Áp', 'Dự án cung cấp các suất ăn dinh dưỡng hằng tuần cho người vô gia cư, người lao động nghèo và các cụ già neo đơn tại khu vực TP.HCM. Tình nguyện viên sẽ trực tiếp chuẩn bị, đóng hộp và phân phát các phần ăn trong buổi tối. Mỗi suất ăn được tính toán để đảm bảo đủ dinh dưỡng và an toàn thực phẩm.', 'khac', 5000000000, 80000000, '2025-12-03', '2025-12-31', 'active', '2025-12-03', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764756362/projects/projects/project_20251203170557402.jpg.jpg'),
(1, 'Tiếp Sức Đến Trường', 'Mục tiêu của dự án là hỗ trợ học sinh nghèo tại các vùng sâu vùng xa có hoàn cảnh khó khăn. Dự án trao tặng tập vở, sách giáo khoa, balo, áo ấm và học bổng. Ngoài ra còn tổ chức các lớp học kỹ năng sống và hướng nghiệp giúp các em phát triển toàn diện hơn.', 'tre_em', 10000000000, 40000000, '2025-12-03', '2025-12-31', 'active', '2025-12-31', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764756521/projects/projects/project_20251203170836319.webp.webp'),
(2, 'Ngôi Nhà Yêu Thương', 'Dự án xây mới hoặc sửa chữa nhà ở xuống cấp cho các hộ gia đình nghèo, người già neo đơn hoặc các gia đình chịu thiên tai. Tình nguyện viên và đội thợ địa phương sẽ phối hợp để hoàn thành công trình an toàn, vững chắc, giúp họ có mái ấm ổn định.', 'khac', 1000000000000, 200000000, '2025-12-03', '2025-12-31', 'active', '2025-12-31', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764756724/projects/projects/project_20251203171159676.jpg.jpg'),
(2, 'Xanh Hóa Cộng Đồng', 'Dự án hướng đến trồng cây xanh tại trường học, công viên, khu dân cư nhằm cải thiện chất lượng không khí và nâng cao ý thức bảo vệ môi trường. Ngoài việc trồng cây, dự án còn tổ chức ngày “Green Day” để dọn rác, phân loại rác và tập huấn tái chế cho cư dân.', 'moi_truong', 5000000000, 150000000, '2025-12-03', '2025-12-31', 'active', '2025-12-31', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764756789/projects/projects/project_20251203171304972.webp.webp'),
(3, 'Kết Nối Yêu Thương', 'Chương trình đến thăm và hỗ trợ tinh thần cho người già sống tại các viện dưỡng lão hoặc sống một mình. Dự án bao gồm tặng quà, trò chuyện, tổ chức các hoạt động văn nghệ, khám sức khỏe miễn phí và hỗ trợ thuốc men cơ bản cho các cụ.', 'khac', 1000000000, 300000000, '2025-12-03', '2025-12-31', 'active', '2025-12-31', 'https://res.cloudinary.com/digj9t8om/image/upload/v1764757209/projects/projects/project_20251203172004719.jpg.jpg');
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
