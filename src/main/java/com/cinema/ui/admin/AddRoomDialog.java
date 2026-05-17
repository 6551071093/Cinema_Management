package com.cinema.ui.admin;

import com.cinema.dao.RoomDAO;
import com.cinema.entity.Room;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddRoomDialog extends JDialog {
    private JTextField txtName, txtRows, txtCols;
    private JComboBox<String> cbStatus;
    private RoomDAO roomDAO = new RoomDAO();
    
    private Room selectedRoom;
    private boolean isUpdated = false;

    public AddRoomDialog(JFrame parent) {
        this(parent, null);
    }

    public AddRoomDialog(JFrame parent, Room room) {
        super(parent, (room == null ? "Thêm Phòng Chiếu Mới" : "Cập Nhật Phòng Chiếu"), true);
        this.selectedRoom = room;
        setSize(450, 500);
        setLocationRelativeTo(parent);
        initComponents();
        
        if (selectedRoom != null) {
            fillData();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridLayout(8, 1, 10, 5));
        pnlForm.setBorder(new EmptyBorder(30, 40, 30, 40));
        pnlForm.setBackground(Color.WHITE);

        txtName = new JTextField();
        styleInput(txtName);

        txtRows = new JTextField();
        styleInput(txtRows);

        txtCols = new JTextField();
        styleInput(txtCols);

        cbStatus = new JComboBox<>(new String[]{"ACTIVE", "MAINTENANCE"});
        cbStatus.setPreferredSize(new Dimension(0, 40));

        pnlForm.add(new JLabel("Tên phòng chiếu (Ví dụ: CINEMA 05):"));
        pnlForm.add(txtName);
        pnlForm.add(new JLabel("Số lượng hàng ghế (Rows):"));
        pnlForm.add(txtRows);
        pnlForm.add(new JLabel("Số lượng cột ghế (Columns):"));
        pnlForm.add(txtCols);
        pnlForm.add(new JLabel("Trạng thái phòng:"));
        pnlForm.add(cbStatus);

        add(pnlForm, BorderLayout.CENTER);

        String btnText = (selectedRoom == null) ? "XÁC NHẬN LƯU PHÒNG" : "LƯU THAY ĐỔI";
        JButton btnSave = new JButton(btnText);
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setPreferredSize(new Dimension(0, 60));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);

        btnSave.addActionListener(e -> handleSave());
        add(btnSave, BorderLayout.SOUTH);
    }

    private void fillData() {
        txtName.setText(selectedRoom.getName());
        txtRows.setText(String.valueOf(selectedRoom.getNumRows()));
        txtCols.setText(String.valueOf(selectedRoom.getNumCols()));
        cbStatus.setSelectedItem(selectedRoom.getStatus());
    }

    private void handleSave() {
        String name = txtName.getText().trim();
        String rowsStr = txtRows.getText().trim();
        String colsStr = txtCols.getText().trim();
        String status = (String) cbStatus.getSelectedItem();

        if (name.isEmpty() || rowsStr.isEmpty() || colsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đủ thông số phòng chiếu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int rows = Integer.parseInt(rowsStr);
            int cols = Integer.parseInt(colsStr);

            boolean success;
            if (selectedRoom == null) {
                // CHẾ ĐỘ THÊM MỚI
                Room newRoom = new Room(0, name, rows, cols, status);
                success = roomDAO.addRoom(newRoom);
            } else {
                // CHẾ ĐỘ CẬP NHẬT: Lấy lại ID cũ để update
                Room updatedRoom = new Room(selectedRoom.getId(), name, rows, cols, status);
                success = roomDAO.updateRoom(updatedRoom); 
            }

            if (success) {
                String msg = (selectedRoom == null) ? "Thêm mới thành công!" : "Cập nhật thành công!";
                JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                isUpdated = true; 
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Hàng/Cột phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    private void styleInput(JTextField txt) {
        txt.setPreferredSize(new Dimension(0, 40));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
}