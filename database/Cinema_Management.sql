-- Khởi tạo Database
CREATE DATABASE IF NOT EXISTS CinemaManagement
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE CinemaManagement;
-- 1. Bảng ACCOUNT (Hỗ trợ Auth, JavaMail và Soft Delete)
CREATE TABLE ACCOUNT (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE DEFAULT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Sẽ lưu chuỗi băm của BCrypt
    role VARCHAR(20) NOT NULL, -- 'ADMIN' hoặc 'STAFF'
    status VARCHAR(20) DEFAULT 'ACTIVE' -- 'ACTIVE' hoặc 'INACTIVE'
);
-- 2. Bảng MOVIE (Đã thêm poster_url, description, release_date cho UI)
CREATE TABLE MOVIE (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration_minutes INT NOT NULL,
    genre VARCHAR(100),
    poster_url VARCHAR(255) DEFAULT '/images/default_poster.png',
    description TEXT,
    release_date DATE,
    status VARCHAR(50) NOT NULL 
);
-- 3. Bảng ROOM (Đã thêm trạng thái bảo trì)
CREATE TABLE ROOM (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL, 
    num_rows INT NOT NULL,
    num_cols INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' -- 'ACTIVE' hoặc 'MAINTENANCE'
);
-- 4. Bảng SHOWTIME
CREATE TABLE SHOWTIME (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    room_id INT NOT NULL,
    start_time DATETIME NOT NULL, 
    end_time DATETIME NOT NULL,
    ticket_price DOUBLE NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES MOVIE(id),
    FOREIGN KEY (room_id) REFERENCES ROOM(id)
);
-- 5. Bảng INVOICE (Hóa đơn - MỚI THEO LỰA CHỌN 2)
CREATE TABLE INVOICE (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL, -- Nhân viên lập hóa đơn
    customer_email VARCHAR(150) DEFAULT NULL,
    total_amount DOUBLE NOT NULL, -- Tổng tiền của tất cả các vé
    payment_status VARCHAR(20) DEFAULT 'PAID',
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES ACCOUNT(id)
);
-- 6. Bảng TICKET (Vé - GỌN NHẸ HƠN, TRỎ VỀ INVOICE)
CREATE TABLE TICKET (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT NOT NULL, -- Liên kết với hóa đơn tổng
    showtime_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL, 
    FOREIGN KEY (invoice_id) REFERENCES INVOICE(id),
    FOREIGN KEY (showtime_id) REFERENCES SHOWTIME(id),
    CONSTRAINT unique_seat_per_showtime UNIQUE (showtime_id, seat_number) 
);
