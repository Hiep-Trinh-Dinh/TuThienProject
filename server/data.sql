-- ================================
-- Database schema for Charity Web
-- ================================

-- 1. Bảng users (người dùng)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,   -- Mã định danh duy nhất cho người dùng
    full_name VARCHAR(100) NOT NULL,          -- Họ và tên đầy đủ
    email VARCHAR(100) UNIQUE NOT NULL,       -- Địa chỉ email (duy nhất, dùng để đăng nhập)
    password_hash VARCHAR(255) NOT NULL,      -- Mật khẩu đã mã hóa
    role ENUM('admin', 'user') DEFAULT 'user', -- Vai trò của người dùng
    phone VARCHAR(20),                        -- Số điện thoại
    status ENUM('active', 'inactive', 'banned') DEFAULT 'active', -- Trạng thái tài khoản
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Ngày tạo tài khoản
);

-- 2. Bảng projects (dự án từ thiện)
CREATE TABLE projects (
    project_id INT AUTO_INCREMENT PRIMARY KEY, -- Mã dự án
    org_id INT NOT NULL,                       -- FK: Người/tổ chức tạo dự án
    title VARCHAR(200) NOT NULL,               -- Tiêu đề dự án
    description TEXT,                          -- Nội dung mô tả chi tiết
    category ENUM('tre_em','y_te','moi_truong','thien_tai','khac') NOT NULL, -- Lĩnh vực
    goal_amount DECIMAL(15,2) NOT NULL,        -- Số tiền mục tiêu
    raised_amount DECIMAL(15,2) DEFAULT 0,     -- Số tiền đã gây quỹ được
    start_date DATE,                           -- Ngày bắt đầu
    end_date DATE,                             -- Ngày kết thúc
    status ENUM('pending','active','closed','rejected') DEFAULT 'pending', -- Trạng thái dự án
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Ngày tạo dự án
    FOREIGN KEY (org_id) REFERENCES users(user_id) -- Quan hệ: 1 tổ chức (user) có nhiều dự án
);

-- 3. Bảng donations (quyên góp)
CREATE TABLE donations (
    donation_id INT AUTO_INCREMENT PRIMARY KEY, -- Mã quyên góp
    project_id INT NOT NULL,                    -- FK: Dự án nhận quyên góp
    donor_id INT NOT NULL,                      -- FK: Người quyên góp
    amount DECIMAL(15,2) NOT NULL,              -- Số tiền quyên góp
    payment_method ENUM('vnpay','viettel_money','momo','bank_transfer') NOT NULL, -- Phương thức thanh toán
    payment_status ENUM('success','pending','failed') DEFAULT 'pending', -- Trạng thái thanh toán
    donated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời điểm quyên góp
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    FOREIGN KEY (donor_id) REFERENCES users(user_id)
);

-- 4. Bảng ambassadors (sứ giả gây quỹ)
CREATE TABLE ambassadors (
    ambassador_id INT AUTO_INCREMENT PRIMARY KEY, -- Mã bản ghi sứ giả
    user_id INT NOT NULL,                         -- FK: Người làm sứ giả
    project_id INT NOT NULL,                      -- FK: Dự án mà sứ giả hỗ trợ
    share_link VARCHAR(255),                      -- Link cá nhân kêu gọi quyên góp
    raised_amount DECIMAL(15,2) DEFAULT 0,        -- Số tiền đã kêu gọi được qua sứ giả này
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Ngày tạo bản ghi
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- 5. Bảng reports (báo cáo dự án)
CREATE TABLE reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY, -- Mã báo cáo
    project_id INT NOT NULL,                  -- FK: Dự án liên quan
    content TEXT NOT NULL,                    -- Nội dung báo cáo
    file_url VARCHAR(255),                    -- Link file đính kèm (PDF, ảnh…)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Ngày tạo báo cáo
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- 6. Bảng news (tin tức / CMS)
CREATE TABLE news (
    news_id INT AUTO_INCREMENT PRIMARY KEY, -- Mã tin tức
    title VARCHAR(200) NOT NULL,            -- Tiêu đề tin tức
    content TEXT NOT NULL,                  -- Nội dung chi tiết
    image_url VARCHAR(255),                 -- Link ảnh minh họa
    author_id INT NOT NULL,                 -- FK: Người viết tin tức
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Ngày đăng
    FOREIGN KEY (author_id) REFERENCES users(user_id)
);

-- 7. Bảng transactions_log (nhật ký giao dịch)
CREATE TABLE transactions_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY, -- Mã log
    donation_id INT NOT NULL,              -- FK: Quyên góp liên quan
    action VARCHAR(100) NOT NULL,          -- Hành động (created, success, failed…)
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời điểm log
    FOREIGN KEY (donation_id) REFERENCES donations(donation_id)
);

-- 8. Bảng system_logs (nhật ký hệ thống)
CREATE TABLE system_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY, -- Mã log
    user_id INT,                           -- FK: Người thực hiện (có thể NULL nếu hệ thống tự động)
    action VARCHAR(200) NOT NULL,          -- Hành động (login, create_project…)
    ip_address VARCHAR(50),                -- Địa chỉ IP
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời điểm
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
