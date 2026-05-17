package com.cinema.ui.staff;

import com.cinema.dao.InvoiceDAO;
import com.cinema.entity.Invoice;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class InvoiceHistoryPanel extends JPanel {
    private JTable tblInvoices;
    private DefaultTableModel model;
    private JLabel lblTotalCount, lblTotalRevenue;
    private InvoiceDAO invoiceDAO = new InvoiceDAO();

    private final Color CONTENT_BG = new Color(245, 247, 250);
    private final Color BORDER_COLOR = new Color(230, 233, 237);
    private final Color DANGER_RED = new Color(220, 53, 69);
    private final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private final Color TEXT_DARK = new Color(33, 37, 41);

    public InvoiceHistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);
        initComponents();
        loadInvoiceData();
    }

    private void initComponents() {
        // --- 1. Header ---
        JLabel lblTitle = new JLabel("LỊCH SỬ GIAO DỊCH", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(30, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"MÃ HD", "NV (ID)", "HỌ TÊN KH", "SĐT", "TỔNG TIỀN", "THỜI GIAN", "TRẠNG THÁI"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblInvoices = new JTable(model);
        tblInvoices.setRowHeight(35);
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        javax.swing.table.DefaultTableCellRenderer rightRenderer = new javax.swing.table.DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        // Áp dụng căn lề cho từng cột cụ thể
        tblInvoices.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // MÃ HD (Giữa)
        tblInvoices.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // NV (ID) (Giữa)
        tblInvoices.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);   // HỌ TÊN KH (Trái)
        tblInvoices.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // SĐT (Giữa)
        tblInvoices.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);  // TỔNG TIỀN (Phải)
        tblInvoices.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // THỜI GIAN (Giữa)
        tblInvoices.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // TRẠNG THÁI (Giữa)
        add(new JScrollPane(tblInvoices), BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        pnlFooter.setPreferredSize(new Dimension(0, 80));
        pnlFooter.setBorder(new EmptyBorder(0, 20, 0, 20)); // Tạo khoảng cách với 2 mép

        JButton btnRefresh = createStyledButton("Làm mới", SECONDARY_COLOR);
        btnRefresh.addActionListener(e -> loadInvoiceData());
        
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        pnlLeft.setOpaque(false);
        pnlLeft.add(btnRefresh);

        JPanel pnlRightStats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 25));
        pnlRightStats.setOpaque(false);

        lblTotalCount = new JLabel("Tổng số lượng: 0 hóa đơn");
        lblTotalCount.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTotalCount.setForeground(TEXT_DARK);

        lblTotalRevenue = new JLabel("TỔNG DOANH THU: 0 VNĐ");
        lblTotalRevenue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalRevenue.setForeground(DANGER_RED);

        pnlRightStats.add(lblTotalCount);
        pnlRightStats.add(lblTotalRevenue);

        // Ráp vào Footer
        pnlFooter.add(pnlLeft, BorderLayout.WEST);
        pnlFooter.add(pnlRightStats, BorderLayout.EAST);

        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadInvoiceData() {
        model.setRowCount(0);
        double totalRevenue = 0;
        List<Invoice> list = invoiceDAO.getAllInvoices(); 
        
        if (list != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (Invoice inv : list) {
                String status = "PAID".equalsIgnoreCase(inv.getPaymentStatus()) ? "Đã thanh toán" : "Chưa thanh toán";
                
                Object[] row = {
                    "#" + inv.getId(),
                    "NV-" + inv.getAccountId(), // Hiện mã nhân viên
                    inv.getFullName(),
                    inv.getSdt(),
                    String.format("%,.0f VNĐ", inv.getTotalAmount()),
                    sdf.format(inv.getBookingTime()),
                    status
                };
                model.addRow(row);
                totalRevenue += inv.getTotalAmount();
            }
        }
        lblTotalCount.setText("Tổng số lượng: " + model.getRowCount() + " hóa đơn");
        lblTotalRevenue.setText(String.format("TỔNG DOANH THU: %,.0f VNĐ", totalRevenue));
    }
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
