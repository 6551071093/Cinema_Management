package com.cinema.ui.admin;

import com.cinema.dao.RoomDAO;
import com.cinema.entity.Room;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class RoomPanel extends JPanel {
    private JPanel pnlGrid;
    private RoomDAO roomDAO = new RoomDAO();
    
    private Room selectedRoom = null;
    private JPanel selectedOutline = null;
    
    private final Color CONTENT_BG = new Color(245, 247, 250);
    private final Color ACCENT_BLUE = new Color(0, 123, 255);
    private final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private final Color DANGER_RED = new Color(220, 53, 69);
    private final Color WARNING_ORANGE = new Color(255, 193, 7);
    private final Color BORDER_COLOR = new Color(230, 233, 237);
    private final Color TEXT_DARK = new Color(33, 37, 41);

    public RoomPanel() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);
        initComponents();
        loadRoomData();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("QUẢN LÝ PHÒNG CHIẾU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(30, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        pnlGrid = new JPanel(new GridLayout(0, 4, 20, 20));
        pnlGrid.setBackground(CONTENT_BG);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CONTENT_BG);
        wrapper.add(pnlGrid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        add(scrollPane, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JButton btnAdd = createStyledButton("Thêm phòng mới", SUCCESS_GREEN);
        JButton btnUpdate = createStyledButton("Cập nhật phòng", WARNING_ORANGE);
        JButton btnDelete = createStyledButton("Xóa phòng", DANGER_RED);
        JButton btnRefresh = createStyledButton("Làm mới", new Color(108, 117, 125));
        
        btnUpdate.setForeground(TEXT_DARK); 



     // 1. Sự kiện Thêm phòng mới
     btnAdd.addActionListener(e -> {
         JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
         
         AddRoomDialog dialog = new AddRoomDialog(mainFrame); 
         dialog.setVisible(true);
         
         loadRoomData(); 
     });

     // 2. Sự kiện Cập nhật phòng
     btnUpdate.addActionListener(e -> {
         if (selectedRoom == null) {
             JOptionPane.showMessageDialog(this, 
                 "Vui lòng click chọn phòng chiếu muốn cập nhật!", 
                 "Thông báo", JOptionPane.WARNING_MESSAGE);
             return;
         }
         
         JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
         
         AddRoomDialog dialog = new AddRoomDialog(mainFrame, selectedRoom); 
         dialog.setVisible(true);
         
         if (dialog.isUpdated()) {
             loadRoomData();
         }
     });

        // Xóa phòng
        btnDelete.addActionListener(e -> {
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng chiếu muốn xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int choice = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa '" + selectedRoom.getName() + "'?\nHành động này không thể hoàn tác!", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
            if (choice == JOptionPane.YES_OPTION) {
                if (roomDAO.deleteRoom(selectedRoom.getId())) {
                    JOptionPane.showMessageDialog(this, "Xóa phòng thành công!");
                    loadRoomData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa phòng đang có lịch chiếu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRefresh.addActionListener(e -> loadRoomData());

        pnlFooter.add(btnAdd);
        pnlFooter.add(btnUpdate);
        pnlFooter.add(btnDelete);
        pnlFooter.add(btnRefresh);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadRoomData() {
        pnlGrid.removeAll();
        selectedRoom = null;
        selectedOutline = null;
        
        List<Room> list = roomDAO.getAllRooms();
        if (list != null) {
            for (Room r : list) pnlGrid.add(createRoomCard(r));
        }
        
        pnlGrid.revalidate();
        pnlGrid.repaint();
    }

    private JPanel createRoomCard(Room room) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(220, 280));
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout(0, 10));
        
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), 
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblImg = new JLabel("", SwingConstants.CENTER);
        java.net.URL url = getClass().getResource("/images/cinema_bg.jpg");

        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(180, 130, Image.SCALE_SMOOTH);
            lblImg.setIcon(new ImageIcon(img));
        } else {
            lblImg.setOpaque(true);
            lblImg.setBackground(new Color(240, 240, 240));
            lblImg.setText("Room Photo");
        }

        if ("MAINTENANCE".equalsIgnoreCase(room.getStatus())) {
            lblImg.setLayout(new BorderLayout());
            
            JLabel lblOverlay = new JLabel("ĐANG BẢO TRÌ", SwingConstants.CENTER);
            lblOverlay.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblOverlay.setForeground(Color.WHITE);
            
            lblOverlay.setBackground(new Color(220, 53, 69, 150)); 
            lblOverlay.setOpaque(true);
            
            lblImg.add(lblOverlay, BorderLayout.CENTER);
        }

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlInfo.setOpaque(false);
        
        JLabel lblName = new JLabel("<html><center>" + room.getName().toUpperCase() + "</center></html>", SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(TEXT_DARK);
        
        JButton btnDetail = new JButton("XEM CHI TIẾT");
        btnDetail.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDetail.setBackground(ACCENT_BLUE);
        btnDetail.setForeground(Color.WHITE);
        btnDetail.setFocusPainted(false);
        btnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetail.addActionListener(e -> {
        	showRoomDetail(room);
        });

        pnlInfo.add(lblName);
        pnlInfo.add(btnDetail);

        card.add(lblImg, BorderLayout.CENTER);
        card.add(pnlInfo, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (selectedRoom != room) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT_BLUE, 3, true), new EmptyBorder(13, 13, 13, 13)));
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedRoom != room) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(15, 15, 15, 15)));
                }
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selectedOutline != null) {
                    selectedOutline.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(15, 15, 15, 15)));
                }
                selectedRoom = room;
                selectedOutline = card;
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ACCENT_BLUE, 3, true), new EmptyBorder(13, 13, 13, 13)));
            }
        });

        return card;
    }

    private void showRoomDetail(Room room) {
        int totalSeats = room.getNumRows() * room.getNumCols();
        
        StringBuilder sb = new StringBuilder();
        sb.append("THÔNG TIN CHI TIẾT PHÒNG CHIẾU\n");
        sb.append("------------------------------------------\n");
        sb.append("Tên phòng: \t").append(room.getName()).append("\n");
        sb.append("Số hàng ghế: \t").append(room.getNumRows()).append("\n");
        sb.append("Số cột ghế: \t").append(room.getNumCols()).append("\n");
        sb.append("Sức chứa: \t").append(totalSeats).append(" ghế\n");
        sb.append("Trạng thái: \t").append(room.getStatus().toUpperCase()).append("\n");
        sb.append("------------------------------------------");

        JOptionPane.showMessageDialog(this, sb.toString(), "Chi tiết " + room.getName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(185, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
