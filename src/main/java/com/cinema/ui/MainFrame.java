package com.cinema.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.cinema.entity.Account;
import com.cinema.ui.admin.AccountPanel;
import com.cinema.ui.admin.MoviePanel;
import com.cinema.ui.admin.RoomPanel;
import com.cinema.ui.admin.ShowtimePanel;
import com.cinema.ui.staff.BookingPanel;
import com.cinema.ui.staff.ChangePasswordDialog;
import com.cinema.ui.staff.InvoiceHistoryPanel;

public class MainFrame extends JFrame {
    private Account currentAccount;
    private JPanel pnlSidebar, pnlContent;
    private CardLayout cardLayout;
    private List<JButton> menuButtons = new ArrayList<>();
    private InvoiceHistoryPanel historyPanel;
    
    private final Color SIDEBAR_COLOR = new Color(33, 37, 41); 
    private final Color HOVER_COLOR = new Color(70, 75, 80);    
    private final Color ACTIVE_COLOR = new Color(0, 123, 255);  
    private final Color LOGO_GOLD = new Color(255, 215, 0); 
    private final Color MENU_WHITE = Color.WHITE;
    private final Color BUTTON_RED = new Color(220, 53, 69);
    private final Color CONTENT_BG = new Color(235, 246, 255);

    public MainFrame(Account account) {
        this.currentAccount = account;
        setTitle("IT CINEMA MANAGEMENT SYSTEM");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        pnlSidebar = new JPanel();
        pnlSidebar.setBackground(SIDEBAR_COLOR);
        pnlSidebar.setPreferredSize(new Dimension(260, 0));
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBorder(new EmptyBorder(30, 0, 20, 0));

        JLabel lblLogo = new JLabel("IT CINEMA");
        lblLogo.setForeground(LOGO_GOLD);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(new EmptyBorder(0, 0, 20, 0)); 
        pnlSidebar.add(lblLogo);

        JPanel separator = new JPanel();
        separator.setMaximumSize(new Dimension(200, 1));
        separator.setBackground(new Color(100, 100, 100));
        pnlSidebar.add(separator);
        pnlSidebar.add(Box.createVerticalStrut(40)); 

        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setBackground(CONTENT_BG);

        if ("ADMIN".equalsIgnoreCase(currentAccount.getRole())) {
            setupAdminFeatures();
} else {
            setupStaffFeatures();
        }

        pnlSidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setPreferredSize(new Dimension(200, 45));
        btnLogout.setMaximumSize(new Dimension(200, 45));
        btnLogout.setBackground(BUTTON_RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có muốn đăng xuất không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });

        pnlSidebar.add(btnLogout);
        pnlSidebar.add(Box.createVerticalStrut(20));

        add(pnlSidebar, BorderLayout.WEST);
        add(pnlContent, BorderLayout.CENTER);
    }

    private void setupAdminFeatures() {
        JButton btnMovies = createMenuButton("Quản lý phim");
        JButton btnShowtimes = createMenuButton("Quản lý lịch chiếu");
        JButton btnRooms = createMenuButton("Quản lý phòng chiếu");
        JButton btnAccounts = createMenuButton("Quản lý tài khoản");

        pnlContent.add(new MoviePanel(), "MOVIES");
        pnlContent.add(new ShowtimePanel(), "SHOWTIMES");
        pnlContent.add(new RoomPanel(), "ROOMS");
        pnlContent.add(new AccountPanel(), "ACCOUNTS");

        btnMovies.addActionListener(e -> { cardLayout.show(pnlContent, "MOVIES"); setActive(btnMovies); });
        btnShowtimes.addActionListener(e -> { cardLayout.show(pnlContent, "SHOWTIMES"); setActive(btnShowtimes); });
        btnRooms.addActionListener(e -> { cardLayout.show(pnlContent, "ROOMS"); setActive(btnRooms); });
        btnAccounts.addActionListener(e -> { cardLayout.show(pnlContent, "ACCOUNTS"); setActive(btnAccounts); });

        pnlSidebar.add(btnMovies);
        pnlSidebar.add(Box.createVerticalStrut(20)); 
        pnlSidebar.add(btnShowtimes);
        pnlSidebar.add(Box.createVerticalStrut(20));
        pnlSidebar.add(btnRooms);
        pnlSidebar.add(Box.createVerticalStrut(20));
        pnlSidebar.add(btnAccounts);
        
        setActive(btnMovies);
        cardLayout.show(pnlContent, "MOVIES");
    }

    private void setupStaffFeatures() {
        JButton btnBooking = createMenuButton("Bán vé tại quầy");
        JButton btnHistory = createMenuButton("Lịch sử hóa đơn");
        JButton btnChangePass = createMenuButton("Đổi mật khẩu");

        historyPanel = new InvoiceHistoryPanel();
        
        pnlContent.add(new BookingPanel(currentAccount), "BOOKING");
pnlContent.add(historyPanel, "HISTORY"); 

        btnBooking.addActionListener(e -> { 
            cardLayout.show(pnlContent, "BOOKING"); 
            setActive(btnBooking); 
        });

        btnHistory.addActionListener(e -> { 
            cardLayout.show(pnlContent, "HISTORY"); 
            setActive(btnHistory); 
        });

        btnChangePass.addActionListener(e -> { 
            setActive(btnChangePass);
            new ChangePasswordDialog(this).setVisible(true); 
        });

        pnlSidebar.add(btnBooking);
        pnlSidebar.add(Box.createVerticalStrut(20));
        pnlSidebar.add(btnHistory);
        pnlSidebar.add(Box.createVerticalStrut(20));
        pnlSidebar.add(btnChangePass);
        
        setActive(btnBooking);
        cardLayout.show(pnlContent, "BOOKING");
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(230, 50));
        btn.setMaximumSize(new Dimension(230, 50)); 
        
        btn.setBackground(SIDEBAR_COLOR);
        btn.setForeground(MENU_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); 
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        menuButtons.add(btn);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.getBackground() != ACTIVE_COLOR) {
                    btn.setBackground(HOVER_COLOR);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.getBackground() != ACTIVE_COLOR) {
                    btn.setBackground(SIDEBAR_COLOR);
                }
            }
        });
        
        return btn;
    }

    private void setActive(JButton activeBtn) {
        for (JButton btn : menuButtons) {
            btn.setBackground(SIDEBAR_COLOR); 
        }
        activeBtn.setBackground(ACTIVE_COLOR); 
    }
}
