package com.cinema.ui;

import com.cinema.dao.AccountDAO;
import com.cinema.service.AuthService;
import com.cinema.service.EmailService;
import com.cinema.utils.ValidationUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ForgotPasswordDialog extends JDialog {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private JTextField txtEmail;
    private JLabel lblEmailError;
    
    private JTextField txtOTP;
    private JLabel lblOTPError;
    private JLabel lblTimer;
    private int secondsRemaining = 180; // 3 phút
    private Timer countdownTimer;
    
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JLabel lblPassError;

    private String generatedOTP;
    private String targetEmail;
    private AccountDAO accountDAO = new AccountDAO();
    private EmailService emailService = new EmailService();

    public ForgotPasswordDialog(JFrame parent) {
        super(parent, "Quên mật khẩu", true);
        setSize(450, 350);
        setLocationRelativeTo(parent);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        initStep1(); // Card 1: Nhập Email
        initStep2(); // Card 2: Xác thực OTP
        initStep3(); // Card 3: Đặt lại mật khẩu
        
        add(mainPanel);
    }

    // --- BƯỚC 1: XÁC THỰC EMAIL ---
    private void initStep1() {
        JPanel pnl = new JPanel(null);
        JLabel lbl = new JLabel("Nhập Email đã đăng ký:");
        lbl.setBounds(50, 50, 300, 30);
        
        txtEmail = new JTextField();
        txtEmail.setBounds(50, 80, 330, 35);
        
        lblEmailError = createErrorLabel();
        lblEmailError.setBounds(50, 115, 330, 20);
        
        JButton btnSend = new JButton("Nhận mã OTP");
        btnSend.setBounds(50, 150, 150, 40);
        btnSend.addActionListener(e -> handleSendOTP());
        
        pnl.add(lbl); pnl.add(txtEmail); pnl.add(lblEmailError); pnl.add(btnSend);
        mainPanel.add(pnl, "STEP1");
    }

    private void handleSendOTP() {
        String email = txtEmail.getText().trim();
        lblEmailError.setVisible(false);
        
        // 1. Validate định dạng Email
        String error = ValidationUtils.validateEmail(email);
        if (error != null) {
            showError(lblEmailError, error);
            return;
        }
        
        // 2. Kiểm tra Email có trong DB không
        if (accountDAO.findByEmail(email) == null) {
            showError(lblEmailError, "Đây không phải email đã đăng ký!");
            return;
        }

        // 3. Sinh OTP & Gửi Email (Dùng SwingWorker để tránh treo UI)
        targetEmail = email;
        generatedOTP = String.format("%06d", new Random().nextInt(999999));
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return emailService.sendEmail(targetEmail, "Mã OTP đặt lại mật khẩu", "Mã của bạn là: " + generatedOTP);
            }
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                
                JOptionPane.showMessageDialog(ForgotPasswordDialog.this, 
                    "Hệ thống đã gửi mã OTP đến email của bạn!\n\n(Giả lập tin nhắn) Mã OTP của bạn là:  " + generatedOTP, 
                    "Thông báo OTP", 
                    JOptionPane.INFORMATION_MESSAGE);

                startTimer();
                cardLayout.show(mainPanel, "STEP2"); 
            }
        }.execute();
    }

    // --- BƯỚC 2: XÁC MINH OTP ---
    private void initStep2() {
        JPanel pnl = new JPanel(null);
        JLabel lbl = new JLabel("Nhập mã OTP (6 chữ số):");
        lbl.setBounds(50, 50, 300, 30);
        
        txtOTP = new JTextField();
        txtOTP.setBounds(50, 80, 330, 35);
        
        lblOTPError = createErrorLabel();
        lblOTPError.setBounds(50, 115, 330, 20);
        
        lblTimer = new JLabel("Thời gian còn lại: 03:00");
        lblTimer.setBounds(50, 140, 200, 20);
        
        JButton btnVerify = new JButton("Xác nhận OTP");
        btnVerify.setBounds(50, 170, 150, 40);
        btnVerify.addActionListener(e -> {
            if (txtOTP.getText().equals(generatedOTP)) {
                countdownTimer.stop();
                cardLayout.show(mainPanel, "STEP3"); 
            } else {
                showError(lblOTPError, "Mã OTP không chính xác!");
            }
        });
        
        pnl.add(lbl); pnl.add(txtOTP); pnl.add(lblOTPError); pnl.add(lblTimer); pnl.add(btnVerify);
        mainPanel.add(pnl, "STEP2");
    }

    private void startTimer() {
        countdownTimer = new Timer(1000, e -> {
            secondsRemaining--;
            int min = secondsRemaining / 60;
            int sec = secondsRemaining % 60;
            lblTimer.setText(String.format("Thời gian còn lại: %02d:%02d", min, sec));
            if (secondsRemaining <= 0) {
                countdownTimer.stop();
                showError(lblOTPError, "Mã OTP đã hết hạn!");
            }
        });
        countdownTimer.start();
    }

    // --- BƯỚC 3: ĐẶT LẠI MẬT KHẨU ---
    private void initStep3() {
        JPanel pnl = new JPanel(null);
        JLabel lbl1 = new JLabel("Mật khẩu mới:");
        lbl1.setBounds(50, 30, 200, 25);
        txtNewPassword = new JPasswordField();
        txtNewPassword.setBounds(50, 55, 330, 35);
        
        JLabel lbl2 = new JLabel("Xác nhận mật khẩu:");
        lbl2.setBounds(50, 100, 200, 25);
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setBounds(50, 125, 330, 35);
        
        lblPassError = createErrorLabel();
        lblPassError.setBounds(50, 165, 330, 40); 
        
        JButton btnSave = new JButton("Đổi mật khẩu");
        btnSave.setBounds(50, 210, 150, 40);
        btnSave.addActionListener(e -> handleResetPassword());
        
        pnl.add(lbl1); pnl.add(txtNewPassword); pnl.add(lbl2); 
        pnl.add(txtConfirmPassword); pnl.add(lblPassError); pnl.add(btnSave);
        mainPanel.add(pnl, "STEP3");
    }

    private void handleResetPassword() {
        String pass = new String(txtNewPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());
        
        // Validate nghiêm ngặt (8-20 ký tự, hoa, thường, số, đặc biệt...)
        String error = ValidationUtils.validateStrictPassword(pass, confirm);
        if (error != null) {
            showError(lblPassError, "<html>" + error + "</html>");
            return;
        }

        String hashed = BCrypt.hashpw(pass, BCrypt.gensalt());
        if (accountDAO.updatePasswordByEmail(targetEmail, hashed)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            this.dispose();
        }
    }
    private JLabel createErrorLabel() {
        JLabel label = new JLabel();
        label.setForeground(Color.RED);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        label.setVisible(false);
        return label;
    }
    private void showError(JLabel label, String msg) {
        label.setText(msg);
        label.setVisible(true);
    }
}

