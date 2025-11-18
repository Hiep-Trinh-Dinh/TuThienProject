-- 1. Bảng users
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- đổi từ INT sang BIGINT
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'INACTIVE',
    auth_provider VARCHAR(15) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng projects
CREATE TABLE projects (
    project_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- đổi từ INT sang BIGINT
    org_id BIGINT NOT NULL,                       -- FK: Người/tổ chức tạo dự án, đổi thành BIGINT
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category ENUM('tre_em','y_te','moi_truong','thien_tai','khac') NOT NULL,
    goal_amount DECIMAL(15,2) NOT NULL,
    raised_amount DECIMAL(15,2) DEFAULT 0,
    start_date DATE,
    end_date DATE,
    status ENUM('pending','active','closed','rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (org_id) REFERENCES users(user_id)
);

-- 3. Bảng donations
CREATE TABLE donations (
    donation_id INT AUTO_INCREMENT PRIMARY KEY, -- Mã quyên góp
    project_id INT NOT NULL,                    -- FK: Dự án nhận quyên góp
    donor_id INT NOT NULL,                      -- FK: Người quyên góp
    amount DECIMAL(15,2) NOT NULL,              -- Số tiền quyên góp
    payment_method ENUM('vnpay','viettel_money','momo','bank_transfer') NOT NULL, -- Phương thức thanh toán
    payment_status ENUM('success','pending','failed') DEFAULT 'success', -- Trạng thái thanh toán
    donated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời điểm quyên góp
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    FOREIGN KEY (donor_id) REFERENCES users(user_id)
);

-- 4. Bảng ambassadors
CREATE TABLE ambassadors (
    ambassador_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,                   -- BIGINT
    project_id BIGINT NOT NULL,                -- BIGINT
    share_link VARCHAR(255),
    raised_amount DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- 5. Bảng reports
CREATE TABLE reports (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    file_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- 6. Bảng news
CREATE TABLE news (
    news_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    image_url VARCHAR(255),
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(user_id)
);

-- 7. Bảng transactions_log
CREATE TABLE transactions_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donation_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donation_id) REFERENCES donations(donation_id)
);

-- 8. Bảng system_logs
CREATE TABLE system_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,   -- BIGINT cho FK
    action VARCHAR(200) NOT NULL,
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 9. Bảng role (quyền)
CREATE TABLE role (
    name VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255)
);

-- 10. Bảng permission (phân quyền)
CREATE TABLE permission (
    name VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255)
);

-- 11. Bảng users_roles (junction table: User <-> Role)
CREATE TABLE users_roles (
    user_user_id BIGINT NOT NULL,
    roles_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_user_id, roles_name),
    FOREIGN KEY (user_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (roles_name) REFERENCES role(name) ON DELETE CASCADE
);

-- 12. Bảng role_permissions (junction table: Role <-> Permission)
CREATE TABLE role_permissions (
    role_name VARCHAR(50) NOT NULL,
    permissions_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (role_name, permissions_name),
    FOREIGN KEY (role_name) REFERENCES role(name) ON DELETE CASCADE,
    FOREIGN KEY (permissions_name) REFERENCES permission(name) ON DELETE CASCADE
);

-- 13. Bảng confirmation_token (xác thực email đăng ký)
CREATE TABLE confirmation_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    create_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 14. Bảng forgot_password (quên mật khẩu)
CREATE TABLE forgot_password (
    forgot_password_id INT AUTO_INCREMENT PRIMARY KEY,
    otp INT NOT NULL,
    expiration_time DATETIME NOT NULL,
    user_user_id BIGINT,
    FOREIGN KEY (user_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 15. Bảng invalidated_token (token đã bị vô hiệu hóa)
CREATE TABLE invalidated_token (
    id VARCHAR(255) PRIMARY KEY,
    expired_time DATETIME
);