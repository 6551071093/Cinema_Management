package com.cinema.ui.admin;

import com.cinema.dao.RoomDAO;
import com.cinema.dao.ShowtimeDAO;
import com.cinema.dao.DatabaseConnection; 
import com.cinema.entity.Movie;
import com.cinema.entity.Room;
import com.cinema.entity.Showtime;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class AddShowtimeDialog extends JDialog {
    private Movie movie;
    private JTextField txtMovieName, txtStart, txtEnd;
    private JComboBox<Room> cbRoom; 
    private ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private RoomDAO roomDAO = new RoomDAO(); 

    public AddShowtimeDialog(JFrame parent, Movie movie) {
        super(parent, "Thiết lập lịch chiếu", true);
        this.movie = movie;
        setSize(450, 520);
        setLocationRelativeTo(parent);
        initComponents();
        loadActiveRooms(); 
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel pnlForm = new JPanel(new GridLayout(8, 1, 10, 5));
        pnlForm.setBorder(new EmptyBorder(30, 40, 30, 40));
        pnlForm.setBackground(Color.WHITE);

        txtMovieName = new JTextField(movie.getTitle());
        txtMovieName.setEditable(false);
        styleInput(txtMovieName);

        cbRoom = new JComboBox<>();
        cbRoom.setPreferredSize(new Dimension(0, 40));
        
        cbRoom.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room) {
                    setText(((Room) value).getName());
                }
                return this;
            }
        });

        txtStart = new JTextField("2024-05-16 19:00");
        styleInput(txtStart);
        
        txtEnd = new JTextField("2024-05-16 21:00");
        styleInput(txtEnd);

        pnlForm.add(new JLabel("Phim:"));
        pnlForm.add(txtMovieName);
        pnlForm.add(new JLabel("Chọn phòng chiếu (Chỉ hiện phòng sẵn sàng):"));
        pnlForm.add(cbRoom); 
        pnlForm.add(new JLabel("Bắt đầu (yyyy-MM-dd HH:mm):"));
        pnlForm.add(txtStart);
        pnlForm.add(new JLabel("Kết thúc (yyyy-MM-dd HH:mm):"));
        pnlForm.add(txtEnd);

        add(pnlForm, BorderLayout.CENTER);

        JButton btnSave = new JButton("XÁC NHẬN LƯU");
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setPreferredSize(new Dimension(0, 60));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> handleSave());
        add(btnSave, BorderLayout.SOUTH);
    }

    private void loadActiveRooms() {
        cbRoom.removeAllItems();
        List<Room> allRooms = roomDAO.getAllRooms();
        boolean hasActiveRoom = false;

        if (allRooms != null) {
            for (Room r : allRooms) {
                if ("ACTIVE".equalsIgnoreCase(r.getStatus())) {
                    cbRoom.addItem(r);
                    hasActiveRoom = true;
                }
            }
        }

        if (!hasActiveRoom) {
            JOptionPane.showMessageDialog(this, "Hiện không có phòng nào sẵn sàng! (Tất cả đang bảo trì)");
        }
    }

    private void handleSave() {
        Room selectedRoom = (Room) cbRoom.getSelectedItem();
        
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng chiếu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setLenient(false);

            Timestamp start = new Timestamp(sdf.parse(txtStart.getText().trim()).getTime());
            Timestamp end = new Timestamp(sdf.parse(txtEnd.getText().trim()).getTime());

            if (end.before(start)) {
                JOptionPane.showMessageDialog(this, "Thời gian kết thúc phải sau thời gian bắt đầu!");
                return;
            }

            Showtime st = new Showtime(0, movie.getId(), selectedRoom.getId(), start, end, 80000);

            if (showtimeDAO.addShowtime(st)) {
                JOptionPane.showMessageDialog(this, "Lưu lịch chiếu thành công!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể lưu vào CSDL.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Định dạng thời gian sai! (yyyy-MM-dd HH:mm)");
        }
    }

    private void styleInput(JTextField txt) {
        txt.setPreferredSize(new Dimension(0, 40));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
}