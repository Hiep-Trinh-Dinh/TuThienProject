-- Sample data for Charity Web Application - Projects Only
-- Insert sample users first (required for foreign key constraint)
-- Password for all users: "password123"
INSERT INTO users (user_id, full_name, email, password_hash, role, phone, status, created_at) VALUES
(1, 'Tổ chức từ thiện A', 'org1@charity.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'user', '0123456789', 'active', NOW()),
(2, 'Tổ chức từ thiện B', 'org2@charity.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'user', '0987654321', 'active', NOW()),
(3, 'Tổ chức từ thiện C', 'org3@charity.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'user', '0912345678', 'active', NOW()),
(4, 'Tổ chức từ thiện D', 'org4@charity.org', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'user', '0923456789', 'active', NOW());

-- Insert sample projects (matching the original schema)
INSERT INTO projects (org_id, title, description, category, goal_amount, raised_amount, start_date, end_date, status, created_at) VALUES
(1, 'Nước sạch cho cộng đồng nông thôn', 'Cung cấp nước sạch và an toàn cho 500 gia đình ở các làng quê xa xôi thông qua việc xây dựng giếng nước và hệ thống lọc nước.', 'y_te', 100000.00, 75000.00, '2024-01-15', '2024-01-30', 'active', NOW()),
(2, 'Giáo dục cho trẻ em có hoàn cảnh khó khăn', 'Xây dựng trường học và cung cấp tài liệu giáo dục cho trẻ em ở các vùng khó khăn của Guatemala.', 'tre_em', 80000.00, 45000.00, '2024-02-01', '2024-03-01', 'active', NOW()),
(3, 'Cứu trợ lương thực khẩn cấp', 'Cung cấp bữa ăn dinh dưỡng và gói lương thực cho các gia đình bị ảnh hưởng bởi thiên tai ở Philippines.', 'thien_tai', 50000.00, 32000.00, '2024-03-10', '2024-03-20', 'active', NOW()),
(4, 'Chăm sóc sức khỏe ở vùng sâu vùng xa', 'Thành lập các phòng khám di động để phục vụ cộng đồng biệt lập ở nông thôn Ấn Độ.', 'y_te', 65000.00, 28000.00, '2024-01-20', '2024-03-05', 'active', NOW()),
(1, 'Dự án trồng rừng', 'Trồng 10.000 cây xanh để chống lại nạn phá rừng và biến đổi khí hậu ở rừng Amazon.', 'moi_truong', 40000.00, 18000.00, '2024-02-15', '2024-04-15', 'active', NOW()),
(2, 'Hỗ trợ chăm sóc người cao tuổi', 'Cung cấp dịch vụ chăm sóc và đồng hành cho người cao tuổi trong cộng đồng đô thị.', 'khac', 35000.00, 22000.00, '2024-03-01', '2024-04-10', 'active', NOW()),
(3, 'Tài chính vi mô cho nữ doanh nhân', 'Cung cấp khoản vay nhỏ và đào tạo kinh doanh cho nữ doanh nhân ở nông thôn Bangladesh.', 'khac', 30000.00, 15000.00, '2024-01-30', '2024-03-10', 'active', NOW()),
(4, 'Chương trình hỗ trợ người tị nạn', 'Cung cấp nơi ở, thức ăn và hỗ trợ hòa nhập cho người tị nạn chạy trốn khỏi các vùng xung đột.', 'khac', 75000.00, 42000.00, '2024-02-20', '2024-03-25', 'active', NOW());
