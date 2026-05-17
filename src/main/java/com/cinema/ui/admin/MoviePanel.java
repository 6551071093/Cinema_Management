package com.cinema.ui.admin;

import com.cinema.dao.MovieDAO;
import com.cinema.entity.Movie;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class MoviePanel extends JPanel {
    private JPanel pnlGrid;
    private MovieDAO movieDAO = new MovieDAO();
    
    private Movie selectedMovie = null;
    private JPanel selectedOutline = null;
    
    private final Color CONTENT_BG = new Color(245, 247, 250);
    private final Color ACCENT_BLUE = new Color(0, 123, 255);
    private final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private final Color DANGER_RED = new Color(220, 53, 69);
    private final Color WARNING_ORANGE = new Color(255, 193, 7);
    private final Color BORDER_COLOR = new Color(230, 233, 237);
    private final Color TEXT_DARK = new Color(33, 37, 41);

    public MoviePanel() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);
        initComponents();
        loadMovieData();
    }

    private void initComponents() {
        // --- 1. Header ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(CONTENT_BG);
        pnlHeader.setBorder(new EmptyBorder(30, 45, 15, 45));

        JLabel lblTitle = new JLabel("QUẢN LÝ DANH SÁCH PHIM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 37, 41));
        lblTitle.setBorder(new EmptyBorder(30, 45, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- 2. Grid hiển thị phim ---
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

        // --- 3. Footer Toolbar chứa 4 nút sát cạnh nhau ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JButton btnAdd = createStyledButton("Thêm phim mới", SUCCESS_GREEN);
        JButton btnUpdate = createStyledButton("Cập nhật phim", WARNING_ORANGE);
        JButton btnDelete = createStyledButton("Xóa phim", DANGER_RED);
        JButton btnRefresh = createStyledButton("Làm mới", new Color(108, 117, 125));
        
        btnUpdate.setForeground(TEXT_DARK);

        // --- Cài đặt sự kiện cho các nút bấm ---
        
        // Sự kiện Thêm phim mới
        btnAdd.addActionListener(e -> {
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            new MovieDialog(mainFrame).setVisible(true);
            loadMovieData();
        });

        // Sự kiện Cập nhật phim
        btnUpdate.addActionListener(e -> {
            // 1. Kiểm tra xem người dùng đã chọn phim chưa (Giống logic nút Xóa)
            if (selectedMovie == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn bộ phim muốn cập nhật trên danh sách!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 2. Nếu đã chọn, lấy cửa sổ chính để gắn Dialog
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            
            // 3. Mở MovieDialog với tham số isViewOnly = false (Mở sẵn ở chế độ Sửa)
            MovieDialog dialog = new MovieDialog(mainFrame, selectedMovie, false);
            dialog.setVisible(true);
            
            // 4. Sau khi Dialog đóng lại, kiểm tra xem người dùng có thực sự bấm "Cập nhật" không
            if (dialog.isUpdated()) {
                loadMovieData(); 
            }
        });

        // Sự kiện Xóa phim
        btnDelete.addActionListener(e -> {
            if (selectedMovie == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn bộ phim muốn xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int choice = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa phim '" + selectedMovie.getTitle() + "' không?\nHành động này không thể hoàn tác!", 
                "Xác nhận xóa phim", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
            if (choice == JOptionPane.YES_OPTION) {
                boolean deleted = movieDAO.deleteMovie(selectedMovie.getId());
                
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Đã xóa phim thành công!");
                    loadMovieData(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa phim!\nLý do: Phim này đang có suất chiếu trong hệ thống.", "Lỗi xóa dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Sự kiện Làm mới dữ liệu
        btnRefresh.addActionListener(e -> loadMovieData());
        
        pnlFooter.add(btnAdd);
        pnlFooter.add(btnUpdate);
        pnlFooter.add(btnDelete);
        pnlFooter.add(btnRefresh);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadMovieData() {
        pnlGrid.removeAll();
        selectedMovie = null;
        selectedOutline = null;
        List<Movie> list = movieDAO.getAllMovies();
        if (list != null) {
            for (Movie m : list) pnlGrid.add(createMovieCard(m));
        }
        pnlGrid.revalidate();
        pnlGrid.repaint();
    }

    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(220, 340));
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout(0, 10));
        
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), 
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblImg = new JLabel("", SwingConstants.CENTER);
        String path = "/images/" + movie.getTitle() + ".jpg";
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(140, 200, Image.SCALE_SMOOTH);
            lblImg.setIcon(new ImageIcon(img));
        }

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlInfo.setOpaque(false);
        
        JLabel lblTitle = new JLabel("<html><center>" + movie.getTitle().toUpperCase() + "</center></html>", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(TEXT_DARK);
        
        JButton btnDetail = new JButton("XEM CHI TIẾT");
        btnDetail.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDetail.setBackground(ACCENT_BLUE);
        btnDetail.setForeground(Color.WHITE);
        btnDetail.setFocusPainted(false);
        btnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetail.addActionListener(e -> showMovieDetailDialog(movie));

        pnlInfo.add(lblTitle);
        pnlInfo.add(btnDetail);

        card.add(lblImg, BorderLayout.CENTER);
        card.add(pnlInfo, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (selectedMovie != movie) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT_BLUE, 3, true), 
                        new EmptyBorder(13, 13, 13, 13)
                    ));
                }
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedMovie != movie) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, true), 
                        new EmptyBorder(15, 15, 15, 15)
                    ));
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selectedOutline != null) {
                    selectedOutline.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, true), 
                        new EmptyBorder(15, 15, 15, 15)
                    ));
                }
                selectedMovie = movie;
                selectedOutline = card;
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ACCENT_BLUE, 3, true), 
                    new EmptyBorder(13, 13, 13, 13)
                ));
            }
        });
        return card;
    }

    private void showMovieDetailDialog(Movie movie) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chi tiết phim: " + movie.getTitle(), true);
        dialog.setSize(650, 450); 
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.getContentPane().setBackground(Color.WHITE);

        JLabel lblPoster = new JLabel("", SwingConstants.CENTER);
        lblPoster.setBorder(new EmptyBorder(25, 20, 0, 10));
        String path = "/images/" + movie.getTitle() + ".jpg";
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(180, 260, Image.SCALE_SMOOTH);
            lblPoster.setIcon(new ImageIcon(img));
        } else {
            lblPoster.setPreferredSize(new Dimension(180, 260));
            lblPoster.setText("No Poster");
            lblPoster.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(25, 20, 0, 10),
                BorderFactory.createLineBorder(new Color(200, 200, 200))
            ));
        }
        dialog.add(lblPoster, BorderLayout.WEST);

        JPanel pnlRight = new JPanel(new BorderLayout(0, 15));
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(new EmptyBorder(25, 10, 10, 25));

        JPanel pnlInfo = new JPanel(new GridLayout(5, 1, 0, 5));
        pnlInfo.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("<html><b style='font-size:18px; color:#212529;'>" + movie.getTitle().toUpperCase() + "</b></html>");
        JLabel lblGenre = new JLabel("<html><b>Thể loại:</b> " + movie.getGenre() + "</html>");
        
        JLabel lblDuration = new JLabel("<html><b>Thời lượng:</b> " + movie.getDurationMinutes() + " phút</html>"); 
        
        String releaseDateStr = "Đang cập nhật";
        if (movie.getReleaseDate() != null) {
            releaseDateStr = new java.text.SimpleDateFormat("dd/MM/yyyy").format(movie.getReleaseDate());
        }
        JLabel lblRelease = new JLabel("<html><b>Ngày chiếu:</b> " + releaseDateStr + "</html>");
        
        String statusColor = movie.getStatus().equalsIgnoreCase("Showing") ? "#28a745" : "#007bff";
        JLabel lblStatus = new JLabel("<html><b>Trạng thái:</b> <span style='color:" + statusColor + ";'>" + movie.getStatus() + "</span></html>");

        lblGenre.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDuration.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblRelease.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        pnlInfo.add(lblTitle);
        pnlInfo.add(lblGenre);
        pnlInfo.add(lblDuration);
        pnlInfo.add(lblRelease);
        pnlInfo.add(lblStatus);

        pnlRight.add(pnlInfo, BorderLayout.NORTH);

        JPanel pnlDesc = new JPanel(new BorderLayout(0, 5));
        pnlDesc.setBackground(Color.WHITE);
        
        JLabel lblDescTitle = new JLabel("<html><b>Mô tả:</b></html>");
        lblDescTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        pnlDesc.add(lblDescTitle, BorderLayout.NORTH);

        JTextArea txtDesc = new JTextArea(movie.getDescription() != null ? movie.getDescription() : "Chưa có mô tả phim.");
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        pnlDesc.add(scrollDesc, BorderLayout.CENTER);

        pnlRight.add(pnlDesc, BorderLayout.CENTER);
        dialog.add(pnlRight, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        JButton btnClose = new JButton("ĐÓNG LẠI");
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setPreferredSize(new Dimension(150, 40));
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dialog.dispose());
        
        pnlBottom.add(btnClose);
        dialog.add(pnlBottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
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
