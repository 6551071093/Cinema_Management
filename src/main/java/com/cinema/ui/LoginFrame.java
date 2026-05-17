package com.cinema.ui;

import com.cinema.entity.Account;
import com.cinema.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblForgotPass;
    private AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("HỆ THỐNG QUẢN LÝ RẠP CHIẾU PHIM IT CINEMA");
        setSize(750, 450); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(1, 2));

        JPanel pnlLeft = new JPanel(new GridBagLayout()) {
            private Image bgImage;
            {
                try {
                    java.net.URL bgURL = getClass().getResource("/images/cinema_bg.jpg");
                    if (bgURL != null) {
                        bgImage = new ImageIcon(bgURL).getImage();
                    }
                } catch (Exception e) {}
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 0, 0, 130)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 48), 0, getHeight(), new Color(36, 59, 85));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        JLabel lblLoginTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblLoginTitle.setForeground(Color.WHITE); 
        pnlLeft.add(lblLoginTitle);
        add(pnlLeft); 

        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBackground(Color.WHITE); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblWelcome = new JLabel("Chào mừng trở lại!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWelcome.setForeground(new Color(100, 100, 100));
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 40, 25, 40);
        pnlRight.add(lblWelcome, gbc);

        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 40, 5, 40);
        pnlRight.add(lblUser, gbc);

        txtUsername = new JTextField();
        styleTextField(txtUsername);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 20, 40);
        pnlRight.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 40, 5, 40);
        pnlRight.add(lblPass, gbc);

        JPanel pnlPasswordRow = new JPanel(new BorderLayout(10, 0)); 
        pnlPasswordRow.setOpaque(false); 

        txtPassword = new JPasswordField();
        styleTextField(txtPassword); 
        pnlPasswordRow.add(txtPassword, BorderLayout.CENTER);

        JToggleButton btnTogglePass = new JToggleButton("Hiện"); 
        styleButton(btnTogglePass, new Color(40, 167, 69));
        btnTogglePass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTogglePass.setPreferredSize(new Dimension(85, 50)); 
        
        btnTogglePass.addActionListener(e -> {
            if (btnTogglePass.isSelected()) {
                txtPassword.setEchoChar((char) 0);
                btnTogglePass.setText("Ẩn"); 
            } else {
                txtPassword.setEchoChar('•');
                btnTogglePass.setText("Hiện");
            }
        });
        pnlPasswordRow.add(btnTogglePass, BorderLayout.EAST);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 40, 5, 40);
        pnlRight.add(pnlPasswordRow, gbc);

        lblForgotPass = new JLabel("<html><u><i>Quên mật khẩu?</i></u></html>", SwingConstants.RIGHT);
        lblForgotPass.setForeground(new Color(0, 123, 255)); 
        lblForgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        lblForgotPass.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ForgotPasswordDialog(LoginFrame.this).setVisible(true);
            }
        });
        
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 40, 10, 40); 
        pnlRight.add(lblForgotPass, gbc);

        btnLogin = new JButton("Đăng nhập");
        styleButton(btnLogin, new Color(50, 167, 69)); 
        btnLogin.addActionListener(e -> handleLogin());
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 40, 20, 40);
        pnlRight.add(btnLogin, gbc);

        add(pnlRight); 
    }

    private void styleTextField(JTextField txt) {
        txt.setPreferredSize(new Dimension(250, 50)); 
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12) 
        ));
    }

    private void styleButton(AbstractButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false); 
        btn.setPreferredSize(new Dimension(250, 50)); 
        btn.setBorder(BorderFactory.createEmptyBorder()); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        Account account = authService.login(username, password);
        if (account != null) {
            new MainFrame(account).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Tên đăng nhập hoặc mật khẩu không chính xác!", 
                "Lỗi đăng nhập", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

