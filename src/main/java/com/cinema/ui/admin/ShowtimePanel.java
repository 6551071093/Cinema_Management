package com.cinema.ui.admin;

import com.cinema.dao.MovieDAO;
import com.cinema.dao.ShowtimeDAO;
import com.cinema.entity.Movie;
import com.cinema.entity.Showtime;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class ShowtimePanel extends JPanel {
    private JPanel pnlGrid;
    private MovieDAO movieDAO = new MovieDAO();
    private ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    
    private Movie selectedMovie = null;
    private JPanel selectedOutline = null; 
    
    private final Color CONTENT_BG = new Color(245, 247, 250);
    private final Color CARD_BORDER = new Color(230, 233, 237);
    private final Color HOVER_BLUE = new Color(0, 123, 255);

    public ShowtimePanel() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);
        initComponents();
        loadMovieData();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("QUẢN LÝ LỊCH CHIẾU THEO PHIM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 37, 41));
        lblTitle.setBorder(new EmptyBorder(30, 45, 10, 0));
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
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        
        JButton btnAddShowtime = new JButton("Thêm suất chiếu mới");
        JButton btnRefresh = new JButton("Làm mới");
        
        styleFooterButton(btnAddShowtime, new Color(40, 167, 69));
        styleFooterButton(btnRefresh, new Color(108, 117, 125));
        
        btnRefresh.addActionListener(e -> loadMovieData());
        
        btnAddShowtime.addActionListener(e -> {
            if (selectedMovie == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn một bộ phim ở trên trước khi thêm lịch chiếu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            AddShowtimeDialog dialog = new AddShowtimeDialog((JFrame) SwingUtilities.getWindowAncestor(this), selectedMovie);
            dialog.setVisible(true);
        });
        
        pnlFooter.add(btnAddShowtime);
        pnlFooter.add(btnRefresh);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadMovieData() {
        pnlGrid.removeAll();
        selectedMovie = null; 
        selectedOutline = null;
        
        List<Movie> movies = movieDAO.getAllMovies();
        if (movies != null) {
            for (Movie m : movies) {
                pnlGrid.add(createMovieShowtimeCard(m));
            }
        }
        pnlGrid.revalidate();
        pnlGrid.repaint();
    }

    private JPanel createMovieShowtimeCard(Movie movie) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(240, 320));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblPoster = new JLabel("", SwingConstants.CENTER);
        lblPoster.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            String imagePath = "/images/" + movie.getTitle() + ".jpg";
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(140, 180, Image.SCALE_SMOOTH);
                lblPoster.setIcon(new ImageIcon(img));
            } else {
                lblPoster.setText("No Image");
                lblPoster.setPreferredSize(new Dimension(140, 180));
            }
        } catch (Exception e) {}

        JLabel lblName = new JLabel("<html><center>" + movie.getTitle() + "</center></html>", SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setPreferredSize(new Dimension(200, 40));

        JButton btnView = new JButton("Xem lịch chiếu");
        btnView.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnView.setBackground(new Color(0, 123, 255));
        btnView.setForeground(Color.WHITE);
        btnView.setFocusPainted(false);
        btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnView.addActionListener(e -> showShowtimesList(movie));

        card.add(lblPoster);
        card.add(Box.createVerticalStrut(10));
        card.add(lblName);
        card.add(Box.createVerticalGlue());
        card.add(btnView);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selectedOutline != null && selectedOutline != card) {
                    selectedOutline.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(CARD_BORDER, 1), new EmptyBorder(15, 15, 15, 15)));
                }
                selectedMovie = movie;
                selectedOutline = card;
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(HOVER_BLUE, 3), new EmptyBorder(13, 13, 13, 13)));
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (selectedOutline != card) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(HOVER_BLUE, 2), new EmptyBorder(14, 14, 14, 14)));
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedOutline != card) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(CARD_BORDER, 1), new EmptyBorder(15, 15, 15, 15)));
                }
            }
        });

        return card;
    }

    private void showShowtimesList(Movie movie) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Lịch chiếu: " + movie.getTitle(), true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel pnlList = new JPanel();
        pnlList.setLayout(new BoxLayout(pnlList, BoxLayout.Y_AXIS));
        pnlList.setBackground(Color.WHITE);
        
        List<Showtime> list = showtimeDAO.getShowtimesByMovie(movie.getId());
        
        if (list == null || list.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có suất chiếu nào cho phim này.");
            lblEmpty.setBorder(new EmptyBorder(20, 20, 20, 20));
            pnlList.add(lblEmpty);
        } else {
            for (Showtime st : list) {
                JPanel row = new JPanel(new BorderLayout(15, 0));
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    new EmptyBorder(15, 20, 15, 20)
                ));
                
                String timeInfo = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(st.getStartTime());
                JLabel lblInfo = new JLabel("<html><b>Phòng: " + st.getRoomId() + "</b> <br>Bắt đầu: " + timeInfo + "</html>");
                lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                JButton btnDelete = new JButton("Xóa");
                btnDelete.setBackground(new Color(220, 53, 69));
                btnDelete.setForeground(Color.WHITE);
                btnDelete.setFocusPainted(false);
                btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                btnDelete.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(dialog, "Bạn có chắc chắn muốn xóa suất chiếu này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean isDeleted = showtimeDAO.deleteShowtime(st.getId());
                        if (isDeleted) {
                            JOptionPane.showMessageDialog(dialog, "Đã xóa thành công!");
                            dialog.dispose();
                            showShowtimesList(movie); // Tải lại danh sách
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Xóa thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                
                row.add(lblInfo, BorderLayout.CENTER);
                row.add(btnDelete, BorderLayout.EAST);
                pnlList.add(row);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(pnlList);
        // Ép cuộn dọc cho hộp thoại
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void styleFooterButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setFocusPainted(false);
        btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
