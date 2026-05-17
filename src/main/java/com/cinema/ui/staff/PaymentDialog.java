package com.cinema.ui.staff;

import com.cinema.dao.DatabaseConnection;
import com.cinema.entity.Account;
import com.cinema.entity.Showtime;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PaymentDialog extends JDialog {
    private Account currentStaff;
    private Showtime showtime;
    private List<String> seats;
    private double totalAmount;

    // Giao diện
    private JTextField txtCustName, txtCustPhone;
    private JButton btnPayAndPrint;

    public PaymentDialog(JFrame parent, Account staff, Showtime showtime, List<String> selectedSeats) {
        super(parent, "TIẾN HÀNH THANH TOÁN & XUẤT HÓA ĐƠN", true);
        this.currentStaff = staff;
        this.showtime = showtime;
        this.seats = selectedSeats;
        this.totalAmount = selectedSeats.size() * showtime.getTicketPrice();

        setSize(450, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        // --- 1. THÔNG TIN ĐƠN HÀNG ---
        JPanel pnlSummary = new JPanel(new GridLayout(3, 1, 5, 5));
        pnlSummary.setBackground(new Color(240, 245, 250));
        pnlSummary.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 240)), 
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        pnlSummary.add(new JLabel("Suất chiếu ID: " + showtime.getId() + " | Phòng: " + showtime.getRoomId()));
        pnlSummary.add(new JLabel("Danh sách ghế chọn: " + seats.toString()));
        
        JLabel lblTotal = new JLabel("TỔNG TIỀN: " + String.format("%,.0f", totalAmount) + " VNĐ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(220, 53, 69));
        pnlSummary.add(lblTotal);
        
        add(pnlSummary, BorderLayout.NORTH);

        // --- 2. FORM NHẬP THÔNG TIN ---
        JPanel pnlCustomer = new JPanel(new GridLayout(4, 1, 5, 5));
        pnlCustomer.setBackground(Color.WHITE);
        pnlCustomer.setBorder(new EmptyBorder(10, 30, 10, 30));
        
        pnlCustomer.add(new JLabel("Tên khách hàng (In lên vé):"));
        txtCustName = new JTextField();
        txtCustName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlCustomer.add(txtCustName);
        
        pnlCustomer.add(new JLabel("Số điện thoại:"));
        txtCustPhone = new JTextField();
        txtCustPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlCustomer.add(txtCustPhone);
        
        add(pnlCustomer, BorderLayout.CENTER);

        // --- 3. NÚT XÁC NHẬN ---
        btnPayAndPrint = new JButton("XÁC NHẬN THANH TOÁN");
        btnPayAndPrint.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnPayAndPrint.setBackground(new Color(40, 167, 69));
        btnPayAndPrint.setForeground(Color.WHITE);
        btnPayAndPrint.setPreferredSize(new Dimension(0, 55));
        btnPayAndPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPayAndPrint.setFocusPainted(false);
        
        btnPayAndPrint.addActionListener(e -> processPaymentAndPrint());
        add(btnPayAndPrint, BorderLayout.SOUTH);
    }

    private void processPaymentAndPrint() {
        String custName = txtCustName.getText().trim();
        String custPhone = txtCustPhone.getText().trim();
        if (custName.isEmpty()) custName = "Khách vãng lai"; 

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bật Transaction

            // 1. THỰC HIỆN INSERT VÀO BẢNG INVOICE THEO CẤU TRÚC MỚI
            int realInvoiceId = -1;
            String insertInvoiceSql = "INSERT INTO INVOICE (account_id, full_name, sdt, total_amount, payment_status, booking_time) VALUES (?, ?, ?, ?, ?, NOW())";
            
            try (PreparedStatement psInvoice = conn.prepareStatement(insertInvoiceSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psInvoice.setInt(1, currentStaff.getId()); // ID nhân viên
                psInvoice.setString(2, custName);          // Tên khách hàng tách riêng
                psInvoice.setString(3, custPhone);         // SĐT tách riêng
                psInvoice.setDouble(4, totalAmount);       // Tổng tiền
                psInvoice.setString(5, "PAID");            // Trạng thái đã thanh toán
                psInvoice.executeUpdate();
                
                try (java.sql.ResultSet rs = psInvoice.getGeneratedKeys()) {
                    if (rs.next()) {
                        realInvoiceId = rs.getInt(1); 
                    }
                }
            }

            if (realInvoiceId == -1) {
                throw new java.sql.SQLException("Lỗi hệ thống: Không thể tạo Hóa đơn!");
            }

            // 2. LƯU VÀO BẢNG TICKET (Khóa ghế)
            String insertTicketSql = "INSERT INTO TICKET (showtime_id, seat_number, invoice_id) VALUES (?, ?, ?)";
            try (PreparedStatement psTicket = conn.prepareStatement(insertTicketSql)) {
                for (String seatCode : seats) {
                    psTicket.setInt(1, showtime.getId());
                    psTicket.setString(2, seatCode);
                    psTicket.setInt(3, realInvoiceId);
                    psTicket.addBatch(); 
                }
                psTicket.executeBatch(); 
            }

            // Hoàn tất Transaction
            conn.commit(); 
            
            // 3. ĐÓNG CỬA SỔ & HIỂN THỊ HÓA ĐƠN TRÊN MÀN HÌNH
            this.dispose();
            showInvoiceReceipt(realInvoiceId, custName, custPhone);

        } catch (Exception ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception e) { e.printStackTrace(); }
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi xử lý thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hàm hiển thị giao diện Hóa Đơn (Mô phỏng máy in bill)
     */
    private void showInvoiceReceipt(int invoiceId, String customerName, String customerPhone) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentTime = sdf.format(new Date());
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("======================================\n");
        receipt.append("          CỤM RẠP IT CINEMA           \n");
        receipt.append("         HÓA ĐƠN THANH TOÁN           \n");
        receipt.append("======================================\n");
        receipt.append("Mã hóa đơn: HD").append(String.format("%05d", invoiceId)).append("\n");
        receipt.append("Ngày in   : ").append(currentTime).append("\n");
        receipt.append("Thu ngân  : Nhân viên ID ").append(currentStaff.getId()).append("\n");
        receipt.append("Khách hàng: ").append(customerName).append("\n");
        if (!customerPhone.isEmpty()) {
            receipt.append("SĐT       : ").append(customerPhone).append("\n");
        }
        receipt.append("--------------------------------------\n");
        receipt.append("THÔNG TIN VÉ:\n");
        receipt.append("- Suất chiếu ID: ").append(showtime.getId()).append("\n");
        receipt.append("- Phòng chiếu  : ").append(showtime.getRoomId()).append("\n");
        receipt.append("- Vị trí ghế   : ").append(seats.toString()).append("\n");
        receipt.append("- Đơn giá      : ").append(String.format("%,.0f", showtime.getTicketPrice())).append(" VNĐ\n");
        receipt.append("--------------------------------------\n");
        receipt.append("TỔNG CỘNG: ").append(String.format("%,.0f", totalAmount)).append(" VNĐ\n");
        receipt.append("======================================\n");
        receipt.append("      Xin cảm ơn và hẹn gặp lại!      \n");
        
        saveInvoiceToFile(invoiceId, receipt.toString());

        JTextArea txtReceipt = new JTextArea(receipt.toString());
        txtReceipt.setFont(new Font("Monospaced", Font.BOLD, 14)); 
        txtReceipt.setEditable(false);
        txtReceipt.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtReceipt.setBackground(new Color(253, 253, 253));

        JOptionPane.showMessageDialog(null, new JScrollPane(txtReceipt), "Thanh toán thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void saveInvoiceToFile(int invoiceId, String receiptContent) {
        try {
            // 1. Tạo đối tượng thư mục tên là "invoices" ở thư mục gốc của dự án
            java.io.File folder = new java.io.File("invoices");
            if (!folder.exists()) {
                folder.mkdirs(); // Tự động tạo thư mục nếu chưa có sẵn
            }
            
            // 2. Đặt tên file theo định dạng HD00001.txt, HD00002.txt...
            String fileName = String.format("invoices/HD%05d.txt", invoiceId);
            java.io.File file = new java.io.File(fileName);
            
            // 3. Tiến hành ghi file với bảng mã UTF-8 để chốt cứng tiếng Việt không bị lỗi font
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                    new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), "UTF-8"))) {
                writer.write(receiptContent);
            }
            
            // In ra Console đường dẫn tuyệt đối để bạn dễ tìm bấm vào xem thử
            System.out.println("ĐÃ LƯU TRỮ HÓA ĐƠN TRA SOÁT TẠI: " + file.getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tự động lưu trữ hóa đơn văn bản: " + e.getMessage());
            e.printStackTrace();
        }
    }
}