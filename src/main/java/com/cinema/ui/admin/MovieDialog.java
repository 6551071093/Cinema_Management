package com.cinema.ui.admin;

import com.cinema.dao.MovieDAO;
import com.cinema.entity.Movie;
import com.cinema.utils.DateFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MovieDialog extends JDialog {
    private JTextField txtTitle, txtDuration, txtGenre, txtReleaseDate;
    private JComboBox<String> cbStatus;
    private JTextPane txtDescription; 
    private JButton btnSave, btnCancel;
    
    private MovieDAO movieDAO = new MovieDAO();
    private Movie currentMovie;
    private boolean isUpdated = false;
    private boolean isViewOnly; 
    
    private final Color SOFT_BG = new Color(244, 247, 252); 

    public MovieDialog(JFrame parent) {
        this(parent, null, false); 
    }

    public MovieDialog(JFrame parent, Movie movie, boolean isViewOnly) {
        super(parent, movie == null ? "Thêm phim mới" : (isViewOnly ? "Chi tiết phim" : "Cập nhật phim"), true);
        this.currentMovie = movie;
        this.isViewOnly = isViewOnly; 
        
        setSize(550, 650); 
        setLocationRelativeTo(parent);
        
        getContentPane().setBackground(SOFT_BG);
        
        initComponents();
        
        if (movie != null) {
            fillData(movie);
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBackground(SOFT_BG); // Áp dụng màu nền dịu
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel pnlTop = new JPanel(new GridLayout(5, 2, 15, 15)); 
        pnlTop.setBackground(SOFT_BG); 
        
        pnlTop.add(createStyledLabel("Tên phim:"));
        txtTitle = new JTextField();
        styleTextField(txtTitle);
        addContextMenu(txtTitle);
        pnlTop.add(txtTitle);

        pnlTop.add(createStyledLabel("Thời lượng (phút):"));
        txtDuration = new JTextField();
        styleTextField(txtDuration);
        addContextMenu(txtDuration);
        pnlTop.add(txtDuration);

        pnlTop.add(createStyledLabel("Thể loại:"));
        txtGenre = new JTextField();
        styleTextField(txtGenre);
        addContextMenu(txtGenre);
        pnlTop.add(txtGenre);

        pnlTop.add(createStyledLabel("Ngày chiếu (dd/MM/yyyy):"));
        txtReleaseDate = new JTextField();
        styleTextField(txtReleaseDate);
        addContextMenu(txtReleaseDate);
        pnlTop.add(txtReleaseDate);

        pnlTop.add(createStyledLabel("Trạng thái:"));
        cbStatus = new JComboBox<>(new String[]{"Showing", "Upcoming"});
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbStatus.setBackground(Color.WHITE); 
        pnlTop.add(cbStatus);

        mainPanel.add(pnlTop, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout(5, 10));
        pnlCenter.setBackground(SOFT_BG); 
        pnlCenter.add(createStyledLabel("Mô tả phim:"), BorderLayout.NORTH);
        
        txtDescription = new JTextPane();
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescription.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        addContextMenu(txtDescription); 
        
        JScrollPane scrollDesc = new JScrollPane(txtDescription); 
        scrollDesc.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        pnlCenter.add(scrollDesc, BorderLayout.CENTER);

        mainPanel.add(pnlCenter, BorderLayout.CENTER);

        if (isViewOnly) {
            txtTitle.setEditable(false);
            txtDuration.setEditable(false);
            txtGenre.setEditable(false);
            txtReleaseDate.setEditable(false);
            txtDescription.setEditable(false);
            cbStatus.setEnabled(false);
            
            txtTitle.getCaret().setBlinkRate(0);
            txtTitle.getCaret().setVisible(false);
            txtDuration.getCaret().setBlinkRate(0);
            txtDuration.getCaret().setVisible(false);
            txtGenre.getCaret().setBlinkRate(0);
            txtGenre.getCaret().setVisible(false);
            txtReleaseDate.getCaret().setBlinkRate(0);
            txtReleaseDate.getCaret().setVisible(false);
            txtDescription.getCaret().setBlinkRate(0);
            txtDescription.getCaret().setVisible(false);
            
            txtTitle.setBackground(SOFT_BG);
            txtDuration.setBackground(SOFT_BG);
            txtGenre.setBackground(SOFT_BG);
            txtReleaseDate.setBackground(SOFT_BG);
            txtDescription.setBackground(SOFT_BG);
        }

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlBottom.setBackground(SOFT_BG);
        pnlBottom.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        if (!isViewOnly) {
            btnSave = new JButton(currentMovie == null ? "Lưu mới" : "Cập nhật");
            styleButton(btnSave, new Color(40, 167, 69)); 
            btnSave.addActionListener(e -> handleSave());
            pnlBottom.add(btnSave);
        }

        btnCancel = new JButton("Đóng");
        styleButton(btnCancel, new Color(108, 117, 125)); 
        btnCancel.addActionListener(e -> dispose());
        pnlBottom.add(btnCancel);

        mainPanel.add(pnlBottom, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(60, 65, 75)); // Chữ xám đậm, rất hợp với nền xanh nhạt
        return lbl;
    }

    private void styleTextField(JTextField txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(0, 35)); 
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40)); 
        btn.setBorder(BorderFactory.createEmptyBorder()); 
    }

    private void addContextMenu(JComponent component) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem copyItem = new JMenuItem("Copy (Sao chép)");
        copyItem.addActionListener(e -> {
            if (component instanceof javax.swing.text.JTextComponent) {
                ((javax.swing.text.JTextComponent) component).copy();
            }
        });
        popup.add(copyItem);
        component.setComponentPopupMenu(popup);
    }

    private void fillData(Movie m) {
        txtTitle.setText(m.getTitle());
        txtDuration.setText(String.valueOf(m.getDurationMinutes()));
        txtGenre.setText(m.getGenre());
        try {
            txtReleaseDate.setText(DateFormatter.formatDate(m.getReleaseDate()));
        } catch (Exception e) {
            txtReleaseDate.setText("");
        }
        cbStatus.setSelectedItem(m.getStatus());
        
        txtDescription.setText(m.getDescription()); 

        javax.swing.text.StyledDocument doc = txtDescription.getStyledDocument();
        javax.swing.text.SimpleAttributeSet justify = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setAlignment(justify, javax.swing.text.StyleConstants.ALIGN_JUSTIFIED);
        doc.setParagraphAttributes(0, doc.getLength(), justify, false);
    }

    private void handleSave() {
        try {
            String title = txtTitle.getText();
            int duration = Integer.parseInt(txtDuration.getText());
            String genre = txtGenre.getText();
            java.util.Date parsedDate = DateFormatter.parseDate(txtReleaseDate.getText());
            java.sql.Date releaseDate = new java.sql.Date(parsedDate.getTime());
            String status = cbStatus.getSelectedItem().toString();
            String description = txtDescription.getText(); 
            String poster = title + ".jpg";

            if (currentMovie == null) {
                Movie newMovie = new Movie(0, title, duration, genre, poster, description, releaseDate, status);
                if (movieDAO.addMovie(newMovie)) {
                    JOptionPane.showMessageDialog(this, "Thêm phim thành công!");
                    isUpdated = true;
                    dispose();
                }
            } else {
                currentMovie.setTitle(title);
                currentMovie.setDurationMinutes(duration);
                currentMovie.setGenre(genre);
                currentMovie.setReleaseDate(releaseDate);
                currentMovie.setStatus(status);
                currentMovie.setDescription(description); 
                currentMovie.setPosterUrl(poster);

                if (movieDAO.updateMovie(currentMovie)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật phim thành công!");
                    isUpdated = true;
                    dispose();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: Vui lòng kiểm tra định dạng ngày và số.");
        }
    }

    public boolean isUpdated() {
        return isUpdated;
    }
}