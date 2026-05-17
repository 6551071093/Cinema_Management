package com.cinema.ui.staff;

import com.cinema.dao.RoomDAO;
import com.cinema.dao.ShowtimeDAO;
import com.cinema.dao.TicketDAO; // ĐÃ SỬA: Thêm import TicketDAO
import com.cinema.dao.DatabaseConnection;
import com.cinema.entity.Showtime;
import com.cinema.entity.Room;
import com.cinema.entity.Account;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionDialog extends JDialog {
    private Showtime showtime;
    private List<String> selectedSeats = new ArrayList<>(); // Danh sách ghế nhân viên đang chọn (Màu xanh)
    private RoomDAO roomDAO = new RoomDAO();
    private ShowtimeDAO showtimeDAO = new ShowtimeDAO(); 
    private TicketDAO ticketDAO = new TicketDAO(); // ĐÃ SỬA: Khai báo đối tượng ticketDAO
    private Account currentStaff;
    
    private JPanel pnlSeatGrid;
    private JButton btnConfirm;

    public SeatSelectionDialog(JFrame parent, Account staff, Showtime showtime) {
        super(parent, "SƠ ĐỒ CHỌN GHẾ NGỒI XẾP VÉ", true);
        this.showtime = showtime;
        this.currentStaff = staff;
        
        setSize(950, 720);
        setLocationRelativeTo(parent);
        getContentPane().setLayout(new BorderLayout(15, 15));
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(20, 25, 20, 25));
        getContentPane().setBackground(new Color(245, 247, 250)); 
        
        initDialogComponents();
        renderSeatGrid();
    }

    private void initDialogComponents() {
        // 1. Mô phỏng Màn hình chiếu phim ở đỉnh JDialog
        JLabel lblScreen = new JLabel("MÀN HÌNH CHIẾU PHIM (SCREEN)", SwingConstants.CENTER);
        lblScreen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblScreen.setOpaque(true);
        lblScreen.setBackground(new Color(215, 225, 235));
        lblScreen.setForeground(new Color(70, 80, 95));
        lblScreen.setPreferredSize(new Dimension(0, 35));
        add(lblScreen, BorderLayout.NORTH);

        // 2. Lưới chứa ma trận nút bấm ghế
        pnlSeatGrid = new JPanel();
        pnlSeatGrid.setBackground(Color.WHITE);
        pnlSeatGrid.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        add(pnlSeatGrid, BorderLayout.CENTER);

        // 3. Nút xác nhận thanh toán dưới đáy
        btnConfirm = new JButton("TIẾP TỤC THANH TOÁN (Đã chọn 0 ghế)");
        btnConfirm.setPreferredSize(new Dimension(0, 55));
        btnConfirm.setBackground(new Color(40, 167, 69)); // Màu xanh lá chủ đạo hành động
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // SỰ KIỆN CLICK MỞ PAYMENT DIALOG ĐƯỢC ĐẶT CỐ ĐỊNH Ở ĐÂY
        btnConfirm.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một chỗ ngồi trống trên sơ đồ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            } else {
                this.dispose();
                new PaymentDialog((JFrame)this.getParent(), currentStaff, showtime, selectedSeats).setVisible(true);
            }
        });
        add(btnConfirm, BorderLayout.SOUTH);
    }

    private void renderSeatGrid() {
        pnlSeatGrid.removeAll();
        selectedSeats.clear();

        // 1. Lấy cấu trúc phòng chiếu từ DB
        Room room = roomDAO.getRoomById(showtime.getRoomId());
        if (room == null) return;

        int numRows = room.getNumRows();
        int numCols = room.getNumCols();
        pnlSeatGrid.setLayout(new GridLayout(numRows, numCols, 6, 6));

        // 2. Lấy danh sách các ghế màu Đỏ (Đã mua thành công) từ Database
        List<String> occupiedSeats = ticketDAO.getOccupiedSeats(showtime.getId());

        // 3. Quy chuẩn kích thước icon
        int sSize = 35;
        ImageIcon imgGray = loadScaledIcon("/images/seat_empty.png", sSize, sSize);   
        ImageIcon imgRed = loadScaledIcon("/images/seat_booked.png", sSize, sSize);     
        ImageIcon imgGreen = loadScaledIcon("/images/seat_selected.png", sSize, sSize); 

        // 4. Lập vòng lặp dựng ma trận
        for (int r = 0; r < numRows; r++) {
            char rowLetter = (char) ('A' + r); 
            for (int c = 1; c <= numCols; c++) {
                String seatCode = rowLetter + "-" + c; 

                JButton btnSeat = new JButton(seatCode);
                btnSeat.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btnSeat.setHorizontalTextPosition(SwingConstants.CENTER);
                btnSeat.setVerticalTextPosition(SwingConstants.BOTTOM);
                btnSeat.setFocusPainted(false);

                // ĐÃ SỬA: Thay thế listBookedSeats thành occupiedSeats
                if (occupiedSeats.contains(seatCode)) {
                    if (imgRed != null) {
                        btnSeat.setIcon(imgRed);
                        // 1. ÉP SWING KHÔNG LÀM MỜ ẢNH KHI KHÓA NÚT
                        btnSeat.setDisabledIcon(imgRed); 
                    } else {
                        btnSeat.setBackground(new Color(220, 53, 69));
                    }
                    
                    btnSeat.setText("<html><font color='white'>" + seatCode + "</font></html>");
                    btnSeat.setEnabled(false); // Khóa ghế không cho phép click
                } else {
                    // Đảm bảo ghế trống hiển thị text bình thường (không dùng HTML)
                    btnSeat.setText(seatCode); 
                    
                    if (imgGray != null) btnSeat.setIcon(imgGray);
                    else btnSeat.setBackground(new Color(225, 230, 235));
                    
                    btnSeat.setForeground(new Color(33, 37, 41)); // Chữ màu đen/xám cho ghế trống
                    btnSeat.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    // Sự kiện click chọn ghế
                    btnSeat.addActionListener(evt -> {
                        if (selectedSeats.contains(seatCode)) {
                            selectedSeats.remove(seatCode);
                            if (imgGray != null) btnSeat.setIcon(imgGray);
                            else btnSeat.setBackground(new Color(225, 230, 235));
                        } else {
                            selectedSeats.add(seatCode);
                            if (imgGreen != null) btnSeat.setIcon(imgGreen);
                            else btnSeat.setBackground(new Color(40, 167, 69));
                        }
                        
                        // Cập nhật số lượng ghế và tiền động trên nút Confirm
                        double totalAmt = selectedSeats.size() * showtime.getTicketPrice();
                        btnConfirm.setText(String.format("TIẾP TỤC THANH TOÁN (Đã chọn %d ghế | Tổng: %,.0f VNĐ)", selectedSeats.size(), totalAmt));
                    });
                }
                pnlSeatGrid.add(btnSeat);
            }
        }
        pnlSeatGrid.revalidate();
        pnlSeatGrid.repaint();
    }

    private ImageIcon loadScaledIcon(String path, int w, int h) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
}