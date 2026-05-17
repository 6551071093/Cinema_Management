package com.cinema.ui.staff;

import com.cinema.dao.AccountDAO;
import com.cinema.entity.Account;
import com.cinema.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField txtOldPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnSave;
    private JButton btnCancel;
    private AccountDAO accountDAO;
    private Account loggedInAccount;

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Đổi mật khẩu", true); 
        this.accountDAO = new AccountDAO();
        this.loggedInAccount = AuthService.getLoggedInAccount();

        setSize(380, 250);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Mật khẩu cũ:"));
        txtOldPassword = new JPasswordField();
        panel.add(txtOldPassword);

        panel.add(new JLabel("Mật khẩu mới:"));
        txtNewPassword = new JPasswordField();
        panel.add(txtNewPassword);

        panel.add(new JLabel("Xác nhận mật khẩu mới:"));
        txtConfirmPassword = new JPasswordField();
        panel.add(txtConfirmPassword);

        btnSave = new JButton("Lưu thay đổi");
        btnSave.addActionListener(this::handleChangePassword);
        panel.add(btnSave);

        btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> this.dispose());
        panel.add(btnCancel);

        add(panel);
    }

    private void handleChangePassword(ActionEvent e) {
        String oldPass = new String(txtOldPassword.getPassword());
        String newPass = new String(txtNewPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra mật khẩu cũ có khớp với mật khẩu đang lưu trong hệ thống không
        if (!BCrypt.checkpw(oldPass, loggedInAccount.getPassword())) {
            JOptionPane.showMessageDialog(this, "Mật khẩu cũ không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Băm mật khẩu mới bằng BCrypt trước khi cập nhật vào DB
        String hashedNewPassword = BCrypt.hashpw(newPass, BCrypt.gensalt());
        boolean isUpdated = accountDAO.updatePassword(loggedInAccount.getId(), hashedNewPassword);

        if (isUpdated) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            // Cập nhật lại mật khẩu mới cho session hiện tại
            loggedInAccount.setPassword(hashedNewPassword);
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật mật khẩu trên hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

