package com.cinema.ui.admin;

import com.cinema.dao.AccountDAO;
import com.cinema.entity.Account;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class AccountPanel extends JPanel {
    private JPanel pnlGrid;
    private AccountDAO accountDAO = new AccountDAO();
    
    private Account selectedAccount = null;
    private JPanel selectedOutline = null;
    
    private final Color CONTENT_BG = new Color(245, 247, 250);
    private final Color ACCENT_BLUE = new Color(0, 123, 255);
    private final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private final Color DANGER_RED = new Color(220, 53, 69);
    private final Color WARNING_ORANGE = new Color(255, 193, 7);
    private final Color INFO_CYAN = new Color(23, 162, 184);
    private final Color BORDER_COLOR = new Color(225, 228, 232);
    private final Color TEXT_DARK = new Color(33, 37, 41);

    public AccountPanel() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);
        initComponents();
        loadAccountData();
    }

    private void initComponents() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(CONTENT_BG);
        pnlHeader.setBorder(new EmptyBorder(30, 45, 15, 45));

        JLabel lblTitle = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 37, 41));
        lblTitle.setBorder(new EmptyBorder(30, 45, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        pnlGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 35, 35));
        pnlGrid.setBackground(CONTENT_BG);
        
        JScrollPane scrollPane = new JScrollPane(pnlGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CONTENT_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JButton btnAdd = createStyledButton("Thêm tài khoản", SUCCESS_GREEN);
        JButton btnUpdate = createStyledButton("Cập nhật", WARNING_ORANGE);
        btnUpdate.setForeground(TEXT_DARK);
        JButton btnDelete = createStyledButton("Xóa tài khoản", DANGER_RED);
        JButton btnRefresh = createStyledButton("Làm mới", new Color(108, 117, 125));
        
        btnRefresh.addActionListener(e -> loadAccountData());

        btnAdd.addActionListener(e -> {
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AccountDialog dialog = new AccountDialog(mainFrame, null);
            dialog.setVisible(true);
            if (dialog.isUpdated()) loadAccountData();
        });

        btnUpdate.addActionListener(e -> {
            if (selectedAccount == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn tài khoản muốn cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AccountDialog dialog = new AccountDialog(mainFrame, selectedAccount);
            dialog.setVisible(true);
            if (dialog.isUpdated()) loadAccountData();
        });

        btnDelete.addActionListener(e -> {
            if (selectedAccount == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn tài khoản muốn xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int choice = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa tài khoản @" + selectedAccount.getUsername() + "?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                if (accountDAO.deleteAccount(selectedAccount.getId())) {
                    JOptionPane.showMessageDialog(this, "Đã xóa tài khoản thành công!");
                    loadAccountData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản do ràng buộc dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlFooter.add(btnAdd);
        pnlFooter.add(btnUpdate);
        pnlFooter.add(btnDelete);
        pnlFooter.add(btnRefresh);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadAccountData() {
        pnlGrid.removeAll();
        selectedAccount = null;
        selectedOutline = null;
        List<Account> list = accountDAO.getAllAccounts(); 
        if (list != null) {
            for (Account acc : list) pnlGrid.add(createAccountCard(acc));
        }
        pnlGrid.revalidate();
        pnlGrid.repaint();
    }

    private JPanel createAccountCard(Account acc) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(230, 290));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(15, 15, 15, 15)));

        JLabel lblAvatar = new JLabel("", SwingConstants.CENTER);
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        int avatarSize = 100;
        java.net.URL avatarUrl = getClass().getResource("/images/avatar.jpg");
        if (avatarUrl != null) {
            Image img = new ImageIcon(avatarUrl).getImage().getScaledInstance(avatarSize, avatarSize, Image.SCALE_SMOOTH);
            lblAvatar.setIcon(new ImageIcon(img));
            lblAvatar.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 2, true));
        } else {
            lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 65));
            lblAvatar.setText("👤");
            lblAvatar.setForeground(new Color(180, 180, 180));
        }

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setOpaque(false);
        pnlInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(acc.getFullName() != null ? acc.getFullName() : "Ẩn danh", SwingConstants.CENTER);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(TEXT_DARK);

        JLabel lblUsername = new JLabel("Username: " + acc.getUsername(), SwingConstants.CENTER);
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(new Color(90, 95, 105));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(170, 1));
        separator.setForeground(new Color(235, 235, 235));

        JPanel pnlBadges = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        pnlBadges.setOpaque(false);

        String roleStr = acc.getRole() != null ? acc.getRole().toUpperCase() : "STAFF";
        JLabel lblRole = createBadge(roleStr, roleStr.equals("ADMIN") ? DANGER_RED : INFO_CYAN);
        
        String statusStr = acc.getStatus() != null ? acc.getStatus().toUpperCase() : "ACTIVE";
        boolean isActive = statusStr.equals("ACTIVE") || statusStr.equals("HOẠT ĐỘNG");
        JLabel lblStatus = createBadge(statusStr, isActive ? SUCCESS_GREEN : new Color(140, 145, 155));

        pnlBadges.add(lblRole);
        pnlBadges.add(lblStatus);

        pnlInfo.add(lblName);
        pnlInfo.add(Box.createVerticalStrut(5));
        pnlInfo.add(lblUsername); 
        pnlInfo.add(Box.createVerticalStrut(12));
        pnlInfo.add(separator);
        pnlInfo.add(Box.createVerticalStrut(12));
        pnlInfo.add(pnlBadges);

        card.add(Box.createVerticalGlue());
        card.add(lblAvatar);
        card.add(Box.createVerticalStrut(12));
        card.add(pnlInfo);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (selectedAccount != acc) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT_BLUE, 3, true), new EmptyBorder(13, 13, 13, 13)));
                }
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedAccount != acc) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(15, 15, 15, 15)));
                }
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selectedOutline != null) {
                    selectedOutline.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(15, 15, 15, 15)));
                }
                selectedAccount = acc;
                selectedOutline = card;
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ACCENT_BLUE, 3, true), new EmptyBorder(13, 13, 13, 13)));
            }
        });

        return card;
    }

    private JLabel createBadge(String text, Color bgColor) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(Color.WHITE);
        badge.setBackground(bgColor);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        return badge;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(175, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}


