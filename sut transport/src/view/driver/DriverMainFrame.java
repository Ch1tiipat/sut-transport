package view.driver;

import view.user.FontManager;
import view.user.RoundedPanel;
import view.user.LoginSelectFrame;
import model.Driver; 
import controller.SystemManager; 

import javax.swing.*;
import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import javax.imageio.ImageIO;

public class DriverMainFrame extends JFrame {

    CardLayout cardLayout;
    JPanel contentPanel;
    
    private Driver currentDriver;
    private SystemManager systemManager;

    private JButton btnHome;
    private JButton btnQueue;
    private JButton btnStation;
    private JButton btnStats;
    private JButton btnProfile;
    
    // 🎨 ธีมสี
    private final Color COLOR_CREAM_BG = new Color(255, 244, 225);
    private final Color COLOR_ORANGE_SIDEBAR = new Color(255, 167, 73);
    private final Color COLOR_RED_BTN = new Color(255, 100, 100);
    private final Color COLOR_GRAY_BTN = new Color(200, 200, 200);

    public DriverMainFrame(Driver driver, SystemManager manager) {
        this.currentDriver = driver;
        this.systemManager = manager;

        setTitle("SUT-TRANSPORT - คนขับ: " + driver.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(COLOR_CREAM_BG);
        getContentPane().setLayout(new BorderLayout());
        
        JPanel sidebar = createSidebar();
        getContentPane().add(sidebar, BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_CREAM_BG);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        changePanel("home");
    }

    public void changePanel(String cardName) {
        contentPanel.removeAll(); 
        JPanel newPanel = null;
        
        switch (cardName) {
            case "home":
                newPanel = new DriverHomePanel(this, systemManager);
                updateSidebarActiveState(btnHome);
                break;
            case "queue":
                newPanel = new DriverQueuePanel(currentDriver, systemManager);
                updateSidebarActiveState(btnQueue);
                break;
            case "station":
                newPanel = new DriverMapPanel(currentDriver);
                updateSidebarActiveState(btnStation);
                break;
            case "stats":
                newPanel = new DriverStatsPanel(systemManager);
                updateSidebarActiveState(btnStats);
                break;
            case "profile":
                newPanel = new DriverProfilePanel(currentDriver, this.systemManager);
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
    // ✨ Custom Logout Dialog (คนขับ)
    // =========================================================================
    private void showLogoutDialog() {
        JDialog dialog = new JDialog(this, "แจ้งเตือน", true);
        dialog.setUndecorated(true); 
        dialog.setBackground(new Color(0,0,0,0)); 
        dialog.setSize(400, 240);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 30, 30);
                g2.setColor(COLOR_CREAM_BG);
                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 30, 30);
                g2.setColor(COLOR_ORANGE_SIDEBAR);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth()-10, getHeight()-10, 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setLayout(null);
        dialog.add(mainPanel);

        JLabel lblIcon = new JLabel("?");
        lblIcon.setFont(new Font("Tahoma", Font.BOLD, 60));
        lblIcon.setForeground(COLOR_ORANGE_SIDEBAR);
        lblIcon.setBounds(175, 20, 50, 70);
        mainPanel.add(lblIcon);

        JLabel lblText = new JLabel("ยืนยันที่จะออกจากระบบหรือไม่?", SwingConstants.CENTER);
        lblText.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblText.setForeground(new Color(90, 50, 20)); 
        lblText.setBounds(20, 100, 350, 30);
        mainPanel.add(lblText);

        JButton btnCancel = createCustomButton("กลับ", COLOR_GRAY_BTN, Color.DARK_GRAY);
        btnCancel.setBounds(60, 160, 120, 45);
        btnCancel.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnCancel);

        JButton btnConfirm = createCustomButton("ยืนยัน", COLOR_RED_BTN, Color.WHITE);
        btnConfirm.setBounds(210, 160, 120, 45);
        btnConfirm.addActionListener(e -> {
            dialog.dispose();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginSelectFrame(this.systemManager).setVisible(true));
        });
        mainPanel.add(btnConfirm);

        dialog.setVisible(true);
    }
    
    private JButton createCustomButton(String text, Color bgColor, Color textColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(bgColor.darker());
                else g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
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
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient สวยๆ
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 190, 120), 0, getHeight(), COLOR_ORANGE_SIDEBAR);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(110, getHeight()));
        sidebar.setLayout(null);

        JLabel lblLogoImg = new JLabel(loadIconOriginal("/img/logo_bus.png", 60, 60));
        lblLogoImg.setBounds(25, 10, 60, 60);
        sidebar.add(lblLogoImg);
        
        JLabel lblLogoText = new JLabel("SUTRANS", SwingConstants.CENTER);
        try { lblLogoText.setFont(FontManager.bold(11f)); } catch(Exception e) {}
        lblLogoText.setForeground(Color.WHITE);
        lblLogoText.setBounds(0, 70, 110, 20);
        sidebar.add(lblLogoText);
        
        sidebar.add(createSeparator(20, 100));

        int startY = 120;
        int gap = 70;

        btnHome = createSidebarButton("/img/ic_home.png", "home", startY);
        sidebar.add(btnHome);
        sidebar.add(createSeparator(20, startY + 55));

        btnQueue = createSidebarButton("/img/ic_bus.png", "queue", startY + gap);
        sidebar.add(btnQueue);
        sidebar.add(createSeparator(20, startY + gap + 55));

        btnStation = createSidebarButton("/img/ic_map.png", "station", startY + gap * 2);
        sidebar.add(btnStation);
        sidebar.add(createSeparator(20, startY + gap * 2 + 55));

        btnStats = createSidebarButton("/img/ic_circledriver.png", "stats", startY + gap * 3);
        sidebar.add(btnStats);
        sidebar.add(createSeparator(20, startY + gap * 3 + 55));

        btnProfile = createSidebarButton("/img/ic_profileuser.png", "profile", startY + gap * 4);
        sidebar.add(btnProfile);
        sidebar.add(createSeparator(20, startY + gap * 4 + 55));

        // Logout
        JButton btnLogout = new JButton(loadIconOriginal("/img/ic_logout.png", 32, 32));
        btnLogout.setBounds(30, startY + gap * 5, 50, 50);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // ✅ ใช้ Custom Dialog
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
        
        btn.addActionListener(e -> changePanel(cardName));
        return btn;
    }

    private void updateSidebarActiveState(JButton activeBtn) {
        JButton[] allBtns = {btnHome, btnQueue, btnStation, btnStats, btnProfile};
        for (JButton btn : allBtns) {
            if (btn == null) continue;
            
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
    
    private Icon loadIconOriginal(String path, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) return new ImageIcon();
            ImageIcon icon = new ImageIcon(url);
            return new ImageIcon(icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return new ImageIcon(); }
    }
    
    private JComponent createSeparator(int x, int y) {
        JPanel line = new JPanel();
        line.setBackground(new Color(255, 255, 255, 150));
        line.setBounds(x, y, 70, 2);
        return line;
    }
}