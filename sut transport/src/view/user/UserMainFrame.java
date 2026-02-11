package view.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import javax.imageio.ImageIO;
import controller.SystemManager;
import model.Passenger;

public class UserMainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    private Passenger currentUser;
    private SystemManager systemManager;

    private JButton btnHome;
    private JButton btnBook;
    private JButton btnHistory;
    private JButton btnMap;
    private JButton btnProfile;

    // 🎨 ธีมสี (ใช้ร่วมกันเพื่อให้สวยงาม)
    private final Color COLOR_CREAM_BG = new Color(255, 248, 225);
    private final Color COLOR_ORANGE_SIDEBAR = new Color(255, 167, 73);
    private final Color COLOR_RED_BTN = new Color(255, 100, 100);
    private final Color COLOR_GRAY_BTN = new Color(200, 200, 200);

    public UserMainFrame(Passenger user, SystemManager manager) {
        this.currentUser = user;
        this.systemManager = manager;

        setTitle("SUT-TRANSPORT - ผู้ใช้: " + (user != null ? user.getName() : "Guest"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        
        getContentPane().setBackground(COLOR_CREAM_BG);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        getContentPane().add(sidebar, BorderLayout.WEST);

        // Content
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_CREAM_BG);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // เริ่มต้น
        changePanel("home");
    }
    
    public void changePanel(String cardName) {
        contentPanel.removeAll(); 
        JPanel newPanel = null;

        switch (cardName) {
            case "home":
                newPanel = new UserHomePanel(this);
                updateSidebarActiveState(btnHome);
                break;
            case "book":
                newPanel = new UserBookingPanel(currentUser, systemManager, this);
                updateSidebarActiveState(btnBook);
                break;
            case "history":
                newPanel = new UserHistoryPanel(currentUser, systemManager);
                updateSidebarActiveState(btnHistory);
                break;
            case "map":
                newPanel = new UserMapPanel(this);
                updateSidebarActiveState(btnMap);
                break;
            case "profile":
                try {
                    newPanel = new UserProfilePanel(currentUser, systemManager, this);
                } catch (Exception e) {
                    newPanel = new JPanel(); 
                }
                updateSidebarActiveState(btnProfile);
                break;
        }

        if (newPanel != null) {
            contentPanel.add(newPanel, cardName);
            cardLayout.show(contentPanel, cardName);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showProfile() {
        changePanel("profile");
    }

    // =========================================================================
    // ✨ Custom Logout Dialog (หน้าต่างแจ้งเตือนทำเอง สวยๆ)
    // =========================================================================
    private void showLogoutDialog() {
        JDialog dialog = new JDialog(this, "แจ้งเตือน", true);
        dialog.setUndecorated(true); // ไม่มีขอบ Windows เดิม
        dialog.setBackground(new Color(0,0,0,0)); // พื้นหลังโปร่งใส
        dialog.setSize(400, 240);
        dialog.setLocationRelativeTo(this);

        // Panel หลัก (วาดขอบมน + เงา)
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // วาดเงา
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 30, 30);
                
                // วาดพื้นหลัง
                g2.setColor(COLOR_CREAM_BG);
                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 30, 30);
                
                // วาดเส้นขอบสีส้ม
                g2.setColor(COLOR_ORANGE_SIDEBAR);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth()-10, getHeight()-10, 30, 30);
                
                g2.dispose();
            }
        };
        mainPanel.setLayout(null);
        dialog.add(mainPanel);

        // ไอคอนตกแต่ง (?)
        JLabel lblIcon = new JLabel("?");
        lblIcon.setFont(new Font("Tahoma", Font.BOLD, 60));
        lblIcon.setForeground(COLOR_ORANGE_SIDEBAR);
        lblIcon.setBounds(175, 20, 50, 70);
        mainPanel.add(lblIcon);

        // ข้อความ
        JLabel lblText = new JLabel("ยืนยันที่จะออกจากระบบหรือไม่?", SwingConstants.CENTER);
        lblText.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblText.setForeground(new Color(90, 50, 20)); // สีน้ำตาลเข้ม
        lblText.setBounds(20, 100, 350, 30);
        mainPanel.add(lblText);

        // ปุ่ม "กลับ" (สีเทา)
        JButton btnCancel = createCustomButton("กลับ", COLOR_GRAY_BTN, Color.DARK_GRAY);
        btnCancel.setBounds(60, 160, 120, 45);
        btnCancel.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnCancel);

        // ปุ่ม "ยืนยัน" (สีแดง)
        JButton btnConfirm = createCustomButton("ยืนยัน", COLOR_RED_BTN, Color.WHITE);
        btnConfirm.setBounds(210, 160, 120, 45);
        btnConfirm.addActionListener(e -> {
            dialog.dispose();
            if (currentUser != null) currentUser.logout();
            dispose(); // ปิดหน้าหลัก
            SwingUtilities.invokeLater(() -> new LoginSelectFrame(this.systemManager).setVisible(true));
        });
        mainPanel.add(btnConfirm);

        dialog.setVisible(true);
    }
    
    // Helper สร้างปุ่มสวยๆ
    private JButton createCustomButton(String text, Color bgColor, Color textColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40); // ทรงแคปซูล
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn.setForeground(textColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_ORANGE_SIDEBAR); 
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(110, getHeight()));
        sidebar.setLayout(null);

        // Logo
        JLabel lblLogoImg = new JLabel(loadIconOriginal("/img/logo_bus.png", 60, 60));
        lblLogoImg.setBounds(25, 10, 60, 60);
        sidebar.add(lblLogoImg);
        
        JLabel lblLogoText = new JLabel("SUTRANS", SwingConstants.CENTER);
        try {
             lblLogoText.setFont(FontManager.bold(11f));
        } catch (Exception e) {
             lblLogoText.setFont(new Font("Tahoma", Font.BOLD, 11));
        }
        lblLogoText.setForeground(Color.WHITE);
        lblLogoText.setBounds(0, 70, 110, 20);
        sidebar.add(lblLogoText);
        
        sidebar.add(createSeparator(20, 100));

        int startY = 120;
        int gap = 70;
        
        btnHome = createSidebarButton("/img/ic_home.png", "home", startY);
        sidebar.add(btnHome);
        sidebar.add(createSeparator(20, startY + 55));

        btnBook = createSidebarButton("/img/ic_bus.png", "book", startY + gap);
        sidebar.add(btnBook);
        sidebar.add(createSeparator(20, startY + gap + 55));

        btnHistory = createSidebarButton("/img/ic_ticket.png", "history", startY + gap * 2);
        sidebar.add(btnHistory);
        sidebar.add(createSeparator(20, startY + gap * 2 + 55));

        btnMap = createSidebarButton("/img/ic_map.png", "map", startY + gap * 3);
        sidebar.add(btnMap);
        sidebar.add(createSeparator(20, startY + gap * 3 + 55));

        btnProfile = createSidebarButton("/img/ic_profileuser.png", "profile", startY + gap * 4);
        sidebar.add(btnProfile);
        sidebar.add(createSeparator(20, startY + gap * 4 + 55));

        // Logout Button
        JButton btnLogout = new JButton(loadIconOriginal("/img/ic_logout.png", 32, 32));
        btnLogout.setBounds(30, startY + gap * 5, 50, 50);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(false);
        
        // ✅ เรียกใช้ Custom Dialog ตรงนี้
        btnLogout.addActionListener(e -> showLogoutDialog());
        
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton createSidebarButton(String iconPath, String cardName, int y) {
        JButton btn = new JButton();
        btn.setBounds(30, y, 50, 50);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.putClientProperty("iconPath", iconPath);
        btn.setIcon(loadColoredIcon(iconPath, 32, 32, Color.WHITE));
        btn.addActionListener(e -> {
            changePanel(cardName); 
        });
        return btn;
    }

    private void updateSidebarActiveState(JButton activeBtn) {
        JButton[] allBtns = {btnHome, btnBook, btnHistory, btnMap, btnProfile};
        for (JButton btn : allBtns) {
            String path = (String) btn.getClientProperty("iconPath");
            if (btn == activeBtn) {
                btn.setIcon(loadColoredIcon(path, 32, 32, Color.BLACK));
            } else {
                btn.setIcon(loadColoredIcon(path, 32, 32, Color.WHITE));
            }
        }
    }
    
    private Icon loadColoredIcon(String resourcePath, int w, int h, Color color) {
        try {
            java.net.URL url = getClass().getResource(resourcePath);
            if (url == null) return null;
            
            Image originalImg = ImageIO.read(url);
            Image scaledImg = originalImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            ImageFilter filter = new RGBImageFilter() {
                public int markerRGB = color.getRGB() | 0xFF000000; 
                public final int filterRGB(int x, int y, int rgb) {
                    if ((rgb & 0xFF000000) != 0) { 
                        return markerRGB;
                    }
                    return rgb;
                }
            };
            
            ImageProducer ip = new FilteredImageSource(scaledImg.getSource(), filter);
            Image coloredImg = Toolkit.getDefaultToolkit().createImage(ip);
            
            return new ImageIcon(coloredImg);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JComponent createSeparator(int x, int y) {
        JPanel line = new JPanel();
        line.setBackground(new Color(255, 255, 255, 150));
        line.setBounds(x, y, 70, 2);
        return line;
    }
    
    private Icon loadIconOriginal(String resourcePath, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(resourcePath);
            if (url == null) return new ImageIcon();
            ImageIcon icon = new ImageIcon(url);
            return new ImageIcon(icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return new ImageIcon(); }
    }
}