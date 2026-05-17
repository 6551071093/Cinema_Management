package com.cinema.ui.admin;

import com.cinema.dao.AccountDAO;
import com.cinema.entity.Account;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountDialog extends JDialog {
    private JTextField txtFullName, txtEmail, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole, cbStatus;
    
    private AccountDAO accountDAO = new AccountDAO();
    private Account currentAccount;
    private boolean isUpdated = false;
    
    private final Color SOFT_BG = new Color(244, 247, 252); 

    public AccountDialog(JFrame parent, Account account) {
        super(parent, account == null ? "Thêm tài khoản mới" : "Cập nhật tài khoản", true);
        this.currentAccount = account;
        
        setSize(450, 640); 
        setLocationRelativeTo(parent);
        getContentPane().setBackground(SOFT_BG);
        
        initComponents();
        if (account != null) {
            fillData(account);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel pnlMain = new JPanel(new GridLayout(6, 1, 10, 12));
        pnlMain.setBackground(SOFT_BG);
        pnlMain.setBorder(new EmptyBorder(25, 35, 10, 35));

        txtFullName = createTextField();
        txtEmail = createTextField(); 
        txtUsername = createTextField();
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(0, 38));
        
        if (currentAccount == null) {
            txtPassword.setText("123456"); 
        }
        
        cbRole = new JComboBox<>(new String[]{"STAFF", "ADMIN"});
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbRole.setBackground(Color.WHITE);
        
        cbStatus = new JComboBox<>(new String[]{"ACTIVE", "LOCKED"});
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbStatus.setBackground(Color.WHITE);

        // Lắp ráp các trường vào Form (Đã có thêm Email)
        pnlMain.add(createInputGroup("Họ và Tên:", txtFullName));
        pnlMain.add(createInputGroup("Địa chỉ Email:", txtEmail)); 
        pnlMain.add(createInputGroup("Tên đăng nhập (Username):", txtUsername));
        pnlMain.add(createInputGroup("Mật khẩu tài khoản (Mã hóa):", txtPassword));
        pnlMain.add(createInputGroup("Quyền hạn (Role):", cbRole));
        pnlMain.add(createInputGroup("Trạng thái hoạt động:", cbStatus));

        add(pnlMain, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlBottom.setBackground(SOFT_BG);
        pnlBottom.setBorder(new EmptyBorder(15, 35, 25, 35));
        
        JButton btnSave = new JButton(currentAccount == null ? "LƯU MỚI" : "CẬP NHẬT");
        btnSave.setBackground(new Color(40, 167, 69)); 
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(130, 42));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> handleSave());
        
        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setPreferredSize(new Dimension(95, 42));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        pnlBottom.add(btnSave);
        pnlBottom.add(btnCancel);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private JPanel createInputGroup(String label, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setBackground(SOFT_BG);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(60, 65, 75));
        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(0, 38));
        return txt;
    }

    private void fillData(Account acc) {
        txtFullName.setText(acc.getFullName());
        txtEmail.setText(acc.getEmail()); 
        txtUsername.setText(acc.getUsername());
        
        txtUsername.setEditable(false); 
        txtUsername.setBackground(new Color(230, 233, 237));
        
        txtPassword.setText(acc.getPassword());
        cbRole.setSelectedItem(acc.getRole());
        cbStatus.setSelectedItem(acc.getStatus());
    }

    private void handleSave() {
        String fname = txtFullName.getText().trim();
        String email = txtEmail.getText().trim(); 
        String uname = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String role = cbRole.getSelectedItem().toString();
        String status = cbStatus.getSelectedItem().toString();

        // Kiểm tra validation không bỏ trống (Bao gồm cả email)
        if (fname.isEmpty() || email.isEmpty() || uname.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường thông tin tài khoản!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentAccount == null) { 
            Account acc = new Account();
            acc.setFullName(fname);
            acc.setEmail(email);
            acc.setUsername(uname);
            acc.setPassword(pass);
            acc.setRole(role);
            acc.setStatus(status);
            
            if (accountDAO.addAccount(acc)) {
                JOptionPane.showMessageDialog(this, "Thêm tài khoản mới thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                isUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể tạo tài khoản!\nTên đăng nhập '" + uname + "' có thể đã tồn tại.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        } else { 
            // CẬP NHẬT DỮ LIỆU
            currentAccount.setFullName(fname);
            currentAccount.setEmail(email);
            currentAccount.setPassword(pass);
            currentAccount.setRole(role);
            currentAccount.setStatus(status);
            
            if (accountDAO.updateAccount(currentAccount)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                isUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Kết nối CSDL thất bại, không thể cập nhật!", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public boolean isUpdated() {
        return isUpdated;
    }
}

