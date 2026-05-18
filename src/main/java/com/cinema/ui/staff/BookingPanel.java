package com.cinema.ui.staff;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.cinema.dao.MovieDAO;
import com.cinema.dao.ShowtimeDAO;
import com.cinema.entity.Account;
import com.cinema.entity.Movie;
import com.cinema.entity.Showtime;

public class BookingPanel extends JPanel {
    private MovieDAO movieDAO = new MovieDAO();
    private ShowtimeDAO showtimeDAO = new ShowtimeDAO(); 
    
    private JPanel pnlMovieGrid;
    private Movie selectedMovie;
    private JComboBox<Showtime> cbShowtimes;
    private JButton btnGoToSeats; 
    private Account currentStaff;
    
    private JLabel lblMovieDetailInfo;
    private final Color LIGHT_BLUE_BG = new Color(235, 246, 255); 
    private final Color TEXT_DARK = new Color(33, 37, 41);

    public BookingPanel(Account staff) {
        this.currentStaff = staff;
        setLayout(new BorderLayout(15, 15));
        setBackground(LIGHT_BLUE_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initComponents();
        loadMoviesToGrid(); 
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("HỆ THỐNG ĐẶT VÉ TẠI QUẦY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // --- 1. KHU VỰC TRÁI (DANH SÁCH PHIM XẾP DỌC) ---
        JPanel pnlLeftContainer = new JPanel(new BorderLayout(0, 15));
        pnlLeftContainer.setBackground(LIGHT_BLUE_BG);
        pnlLeftContainer.setPreferredSize(new Dimension(330, 0)); 

        pnlMovieGrid = new JPanel(); 
        pnlMovieGrid.setBackground(Color.WHITE);
        
        JScrollPane scrollMovies = new JScrollPane(pnlMovieGrid);
        scrollMovies.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 240)), "1. Danh sách phim đang có lịch chiếu:", 
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13)
        ));
        
        // CẤU HÌNH THANH TRƯỢT: Ép hiển thị dọc bên phải, Tắt hoàn toàn chiều ngang
        scrollMovies.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
        scrollMovies.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMovies.getVerticalScrollBar().setUnitIncrement(20); 
        
        pnlLeftContainer.add(scrollMovies, BorderLayout.CENTER);
        add(pnlLeftContainer, BorderLayout.WEST);

        // --- 2. KHU VỰC TRUNG TÂM ---
        JPanel pnlCenterContainer = new JPanel(new BorderLayout(0, 20));
        pnlCenterContainer.setBackground(Color.WHITE);
        pnlCenterContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 240), 1),
            new EmptyBorder(30, 35, 30, 35)
        ));

        lblMovieDetailInfo = new JLabel("<html><center><b style='font-size:16px; color:#555;'>VUI LÒNG CHỌN BỘ PHIM BÊN TRÁI</b></center></html>", SwingConstants.CENTER);
        pnlCenterContainer.add(lblMovieDetailInfo, BorderLayout.NORTH);

        JPanel pnlShowtimeSelect = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlShowtimeSelect.setOpaque(false);
        pnlShowtimeSelect.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 240)), "2. Lựa chọn khung giờ chiếu:", 
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13)
        ));

        cbShowtimes = new JComboBox<>();
        cbShowtimes.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cbShowtimes.setPreferredSize(new Dimension(0, 45));
        
        cbShowtimes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Showtime) {
                    Showtime st = (Showtime) value;
                    java.text.SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm");
                    java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    
                    String start = st.getStartTime() != null ? sdfTime.format(st.getStartTime()) : "00:00";
                    String end = st.getEndTime() != null ? sdfTime.format(st.getEndTime()) : "00:00";
                    String dateStr = st.getStartTime() != null ? sdfDate.format(st.getStartTime()) : "--/--/----"; 
                    
                    String priceStr = String.format("%,.0f", st.getTicketPrice());
                    
                    setText("Ngày: " + dateStr + "  |  " + start + " - " + end + "  |  Giá vé: " + priceStr + " đ");
                }
                return this;
            }
        });
        pnlShowtimeSelect.add(cbShowtimes);
        pnlCenterContainer.add(pnlShowtimeSelect, BorderLayout.CENTER);

        btnGoToSeats = new JButton("TIẾP TỤC CHỌN GHẾ NGỒI");
        btnGoToSeats.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGoToSeats.setBackground(new Color(0, 123, 255));
        btnGoToSeats.setForeground(Color.WHITE);
        btnGoToSeats.setPreferredSize(new Dimension(0, 55));
        btnGoToSeats.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoToSeats.setFocusPainted(false);
        
        btnGoToSeats.addActionListener(e -> {
            Showtime selectedST = (Showtime) cbShowtimes.getSelectedItem();
            if (selectedST == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bộ phim và khung giờ chiếu trước!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            java.awt.Window win = SwingUtilities.getWindowAncestor(this);
            JFrame mainFrame = (win instanceof JFrame) ? (JFrame) win : null;
            
            SeatSelectionDialog seatDialog = new SeatSelectionDialog(mainFrame, currentStaff, selectedST);
            seatDialog.setVisible(true);
        });
        
        pnlCenterContainer.add(btnGoToSeats, BorderLayout.SOUTH);
        
        // --- THANH TRƯỢT KHU VỰC TRUNG TÂM ---
        JScrollPane scrollCenter = new JScrollPane(pnlCenterContainer);
        scrollCenter.setBorder(null); 
        scrollCenter.getViewport().setBackground(Color.WHITE); 
        scrollCenter.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCenter.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollCenter.getVerticalScrollBar().setUnitIncrement(20); 

        add(scrollCenter, BorderLayout.CENTER);
    }
    
    private void showMovieDetail(Movie movie) {
        // 1. Tạo Panel chính chứa toàn bộ nội dung (Sử dụng BorderLayout để chia Trái - Phải)
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0)); // Khoảng cách giữa ảnh và text là 20px
        mainPanel.setPreferredSize(new Dimension(550, 350));

     // --- 2. BÊN TRÁI: ẢNH POSTER ---
        JLabel lblPoster = new JLabel("", SwingConstants.CENTER);
        lblPoster.setPreferredSize(new Dimension(200, 300));
        lblPoster.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Khung viền mỏng cho ảnh

        // Lấy đường dẫn dựa theo tên phim (vd: "/images/Mai.jpg")
        String path = "/images/" + movie.getTitle() + ".jpg";
        
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                // Đưa ảnh trực tiếp vào lblPoster và scale cho vừa với khung 200x300
                Image img = new ImageIcon(url).getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                lblPoster.setIcon(new ImageIcon(img));
            } else {
                // Báo lỗi trên giao diện nếu sai tên file hoặc sai đường dẫn
                lblPoster.setText("Không tìm thấy ảnh");
                System.out.println("DEBUG - Không tìm thấy file: " + path);
            }
        } catch (Exception ex) {
            lblPoster.setText("Lỗi load ảnh");
        }
        
        // Thêm duy nhất lblPoster vào mainPanel
        mainPanel.add(lblPoster, BorderLayout.WEST);

        // --- 3. BÊN PHẢI: THÔNG TIN CHI TIẾT ---
        JPanel pnlDetails = new JPanel();
        pnlDetails.setLayout(new BoxLayout(pnlDetails, BoxLayout.Y_AXIS));

        // Các thông tin cơ bản
        JLabel lblTitle = new JLabel("Tên phim: " + movie.getTitle());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 123, 255)); // Chữ màu xanh cho nổi bật
        
        JLabel lblGenre = new JLabel("Thể loại: " + movie.getGenre());
        lblGenre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel lblDuration = new JLabel("Thời lượng: " + movie.getDurationMinutes() + " phút");
        lblDuration.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblDescTitle = new JLabel("Mô tả:");
        lblDescTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Xử lý phần Mô tả với thanh trượt dọc
        JTextArea txtDesc = new JTextArea(movie.getDescription());
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);       // Tự động xuống dòng
        txtDesc.setWrapStyleWord(true);  // Xuống dòng theo từ, không cắt ngang chữ
        txtDesc.setEditable(false);      // Không cho phép chỉnh sửa
        txtDesc.setBackground(new Color(245, 247, 250)); // Màu nền nhạt để phân biệt

        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Chỉ hiện thanh trượt khi văn bản quá dài
        scrollDesc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollDesc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        // Đưa các thành phần vào pnlDetails (sử dụng Box.createVerticalStrut để tạo khoảng cách)
        pnlDetails.add(lblTitle);
        pnlDetails.add(Box.createVerticalStrut(10));
        pnlDetails.add(lblGenre);
        pnlDetails.add(Box.createVerticalStrut(10));
        pnlDetails.add(lblDuration);
        pnlDetails.add(Box.createVerticalStrut(20));
        pnlDetails.add(lblDescTitle);
        pnlDetails.add(Box.createVerticalStrut(5));
        pnlDetails.add(scrollDesc); // TextArea sẽ tự động chiếm không gian còn lại

        mainPanel.add(pnlDetails, BorderLayout.CENTER);

        // --- 4. HIỂN THỊ HỘP THOẠI ---
        // Sử dụng PLAIN_MESSAGE để ẩn icon chữ 'i' mặc định của JOptionPane
        JOptionPane.showMessageDialog(this, mainPanel, "Chi tiết phim: " + movie.getTitle(), JOptionPane.PLAIN_MESSAGE);
    }

    private void loadMoviesToGrid() {
        pnlMovieGrid.removeAll();
        boolean hasMovie = false;
        
        System.out.println("========== DEBUG BẮT ĐẦU TẢI PHIM ==========");
        List<Movie> allMovies = movieDAO.getAllMovies(); 
        
        if (allMovies != null) {
            System.out.println("Tổng số phim lấy được từ MovieDAO: " + allMovies.size());
            
            // Ép xếp dọc 1 cột và ngăn kéo dãn thẻ phim
            JPanel gridLayoutWrapper = new JPanel(new GridLayout(0, 1, 10, 15));
            gridLayoutWrapper.setBackground(Color.WHITE);
            
            for (Movie movie : allMovies) {
                List<Showtime> sts = showtimeDAO.getShowtimesByMovie(movie.getId());
                int countShowtimes = (sts != null) ? sts.size() : 0;
                System.out.println("Phim: [" + movie.getTitle() + "] - Trạng thái DB: [" + movie.getStatus() + "] - Số lịch chiếu: " + countShowtimes);
                
                if (countShowtimes > 0) {
                    gridLayoutWrapper.add(createMovieCard(movie));
                    hasMovie = true;
                    System.out.println("-> Đã hiển thị phim: " + movie.getTitle() + " lên giao diện.");
                }
            }
            
            pnlMovieGrid.setLayout(new BorderLayout());
            pnlMovieGrid.add(gridLayoutWrapper, BorderLayout.NORTH);
        } else {
            System.out.println("LỖI: movieDAO.getAllMovies() trả về null.");
        }
        System.out.println("===========================================");
        
        if (!hasMovie) {
            pnlMovieGrid.setLayout(new BorderLayout());
            pnlMovieGrid.add(new JLabel("<html><center><i style='color:gray;'>Hiện tại không có bộ phim nào<br>được xếp suất chiếu!</i></center></html>", SwingConstants.CENTER));
        }
        
        pnlMovieGrid.revalidate();
        pnlMovieGrid.repaint();
    }

    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        card.setPreferredSize(new Dimension(260, 270));

        JLabel lblPoster = new JLabel("", SwingConstants.CENTER);
        lblPoster.setOpaque(true);
        lblPoster.setBackground(new Color(240, 245, 250)); 
        lblPoster.setPreferredSize(new Dimension(150, 190)); 

        String imagePath = "/images/" + movie.getTitle() + ".jpg";
        java.net.URL imgURL = getClass().getResource(imagePath);
        
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(150, 190, Image.SCALE_SMOOTH);
            lblPoster.setIcon(new ImageIcon(img));
        } else {
            lblPoster.setText("<html><center><span style='color:#a0aab5; font-size:18px;'></span><br><b style='color:#a0aab5; font-size:11px;'>NO POSTER</b></center></html>");
        }
        card.add(lblPoster, BorderLayout.CENTER);

        JLabel lblName = new JLabel("<html><center><b>" + movie.getTitle().toUpperCase() + "</b></center></html>", SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblName.setForeground(TEXT_DARK);
        lblName.setBorder(new EmptyBorder(8, 0, 0, 0)); 
        card.add(lblName, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedMovie = movie;
                
                Container parent = card.getParent(); 
                for (Component comp : parent.getComponents()) {
                    if (comp instanceof JPanel) {
                        ((JPanel) comp).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
                            new EmptyBorder(10, 10, 10, 10)
                        ));
                    }
                }
                
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 123, 255), 2),
                    new EmptyBorder(9, 9, 9, 9) 
                ));
                
                lblMovieDetailInfo.setText("<html><center><b style='font-size:18px; color:#0066cc;'>" + movie.getTitle().toUpperCase() + "</b><br><span style='color:gray; font-size:12px;'>Thể loại: " + movie.getGenre() + "  |  Thời lượng: " + movie.getDurationMinutes() + " phút</span></center></html>");
                loadShowtimesForMovie(movie.getId());
                if (e.getClickCount() == 2) {
                    showMovieDetail(movie);
                }
            
            
            }
        });
        

        return card;
    }

    private void loadShowtimesForMovie(int movieId) {
        cbShowtimes.removeAllItems();
        List<Showtime> list = showtimeDAO.getShowtimesByMovie(movieId);
        if (list != null && !list.isEmpty()) {
            for (Showtime st : list) {
                cbShowtimes.addItem(st);
            }
        }
    }
}
