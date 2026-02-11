package view.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import controller.SystemManager;
import model.Passenger;

public class UserLoginFrame extends JFrame {

    private JTextField txtStudentId;
    private JPasswordField txtPassword;
    private SystemManager systemManager;

    // 🎨 ธีมสี
    private final Color THEME_TEXT = new Color(90, 50, 20); // สีน้ำตาลเข้ม

    public UserLoginFrame(SystemManager manager) {
        this.systemManager = manager;

        setTitle("SUT-TRANSPORT - เข้าสู่ระบบผู้ใช้");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(255, 244, 225));
        getContentPane().setLayout(null);

        // Logo
        JLabel lblLogo = new JLabel("SUT-TRANSPORT", SwingConstants.CENTER);
        try { lblLogo.setFont(FontManager.bold(28f)); } catch(Exception e) {}
        lblLogo.setForeground(THEME_TEXT);
        lblLogo.setBounds(0, 30, 900, 40);
        getContentPane().add(lblLogo);

        // Main Panel
        RoundedPanel panel = new RoundedPanel(20);
        panel.setBounds(210, 100, 480, 380);
        panel.setLayout(null);
        panel.setBackground(new Color(255, 175, 80)); 
        getContentPane().add(panel);

        JLabel lblTitle = new JLabel("เข้าสู่ระบบ", SwingConstants.CENTER);
        try { lblTitle.setFont(FontManager.bold(22f)); } catch(Exception e) {}
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 25, 480, 30);
        panel.add(lblTitle);

        // --- Inputs ---
        int startY = 85;
        int gap = 85;

        // 1. รหัสนักศึกษา
        JLabel lblStudentId = new JLabel("รหัสนักศึกษา :");
        try { lblStudentId.setFont(FontManager.bold(16f)); } catch(Exception e) {}
        lblStudentId.setForeground(Color.WHITE);
        lblStudentId.setBounds(70, startY, 150, 25);
        panel.add(lblStudentId);

        RoundedPanel idBox = createInputBox();
        idBox.setBounds(70, startY + 30, 340, 40);
        idBox.setLayout(null);
        panel.add(idBox);
        txtStudentId = new JTextField();
        txtStudentId.setBorder(null);
        try { txtStudentId.setFont(FontManager.regular(16f)); } catch(Exception e) {}
        txtStudentId.setBounds(15, 8, 310, 24);
        idBox.add(txtStudentId);

        // 2. รหัสผ่าน
        JLabel lblPass = new JLabel("รหัสผ่าน :");
        try { lblPass.setFont(FontManager.bold(16f)); } catch(Exception e) {}
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(70, startY + gap, 150, 25);
        panel.add(lblPass);

        RoundedPanel passBox = createInputBox();
        passBox.setBounds(70, startY + gap + 30, 340, 40);
        passBox.setLayout(null);
        panel.add(passBox);
        txtPassword = new JPasswordField();
        txtPassword.setBorder(null);
        try { txtPassword.setFont(FontManager.regular(16f)); } catch(Exception e) {}
        txtPassword.setBounds(15, 8, 310, 24);
        passBox.add(txtPassword);

        // Button Login
        JButton btnLogin = new JButton("เข้าสู่ระบบ");
        styleInteractiveButton(btnLogin); 
        btnLogin.setBounds(70, 280, 340, 45);
        panel.add(btnLogin);

        // --- ปุ่มสมัครสมาชิก & ลืมรหัสผ่าน (จัดระเบียบใหม่) ---
        
        // ปุ่มสมัครสมาชิก (ซ้าย)
        JButton btnGoRegister = new JButton("สมัครบัญชีผู้ใช้");
        btnGoRegister.setFocusPainted(false);
        btnGoRegister.setContentAreaFilled(false);
        btnGoRegister.setBorderPainted(false);
        btnGoRegister.setHorizontalAlignment(SwingConstants.LEFT);
        try { btnGoRegister.setFont(FontManager.bold(13f)); } catch(Exception e) {}
        btnGoRegister.setForeground(Color.WHITE);
        btnGoRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoRegister.setBounds(70, 335, 150, 25);
        
        btnGoRegister.addActionListener(e -> {
            dispose();
            new UserRegisterFrame(this.systemManager).setVisible(true);
        });
        panel.add(btnGoRegister);

        // ปุ่มลืมรหัสผ่าน (ขวา) - สีน้ำตาลเข้ม
        JButton btnForgot = new JButton("ลืมรหัสผ่าน?");
        btnForgot.setFocusPainted(false);
        btnForgot.setContentAreaFilled(false);
        btnForgot.setBorderPainted(false);
        btnForgot.setHorizontalAlignment(SwingConstants.RIGHT);
        try { btnForgot.setFont(FontManager.bold(13f)); } catch(Exception e) {}
        
        btnForgot.setForeground(new Color(90, 50, 20)); // สีเข้มตามที่ขอ
        
        btnForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnForgot.setBounds(260, 335, 150, 25);
        
        btnForgot.addActionListener(e -> {
            // เปิดหน้าลืมรหัสผ่าน
            new ForgotPasswordFrame(this.systemManager).setVisible(true);
        });
        panel.add(btnForgot);

        // Back Button
        JButton btnBack = createCustomBackButton();
        getContentPane().add(btnBack);

        // --- Event Login ---
        btnLogin.addActionListener(e -> {
            String id = txtStudentId.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();
            
            Passenger user = systemManager.loginAsPassenger(id, pass);
            
            if (user != null) {
                dispose();
                new UserMainFrame(user, systemManager).setVisible(true);
            } else {
                // ✅ ใช้ Custom Dialog แจ้งเตือนแน่นอน
                showCustomDialog("แจ้งเตือน", "รหัสนักศึกษาหรือรหัสผ่านไม่ถูกต้อง", "error");
            }
        });
    }

    // --- Helper Methods ---

    private void styleInteractiveButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        try { btn.setFont(FontManager.bold(18f)); } catch(Exception e) {}
        btn.setForeground(new Color(110, 60, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { btn.setForeground(new Color(110, 60, 20)); }
        });

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;
                if (b.getModel().isRollover() || b.getModel().isPressed()) {
                    g2.setColor(Color.GRAY);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
                super.paint(g, c);
                g2.dispose();
            }
        });
    }

    private RoundedPanel createInputBox() {
        return new RoundedPanel(10) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
    }
    
    private JButton createCustomBackButton() {
        JButton btnBack = new JButton("ย้อนกลับ") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) { g2.setColor(new Color(180, 50, 50)); } else { g2.setColor(new Color(220, 70, 70)); }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);
                try { g2.setFont(FontManager.bold(16f)); } catch(Exception e) {}
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y - 2);
                g2.dispose();
            }
        };
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setBounds(750, 480, 110, 45); 
        btnBack.addActionListener(e -> {
            dispose();
            new LoginSelectFrame(this.systemManager).setVisible(true);
        });
        return btnBack;
    }

    // ✨ Custom Dialog (ใช้แทน JOptionPane)
    private void showCustomDialog(String title, String message, String type) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));
        dialog.setSize(420, 230);
        dialog.setLocationRelativeTo(this);

        Color themeColor;
        String iconText;
        if ("success".equals(type)) {
            themeColor = new Color(46, 204, 113); iconText = "✓";
        } else if ("warning".equals(type)) {
            themeColor = new Color(243, 156, 18); iconText = "!";
        } else {
            themeColor = new Color(231, 76, 60); iconText = "✕";
        }

        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 40)); g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 25, 25);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 25, 25);
                g2.setColor(themeColor);
                Area header = new Area(new RoundRectangle2D.Double(0, 0, getWidth()-10, 70, 25, 25));
                header.add(new Area(new Rectangle2D.Double(0, 25, getWidth()-10, 45)));
                g2.fill(header); g2.dispose();
            }
        };
        mainPanel.setLayout(null);
        dialog.add(mainPanel);

        JLabel lblIcon = new JLabel(iconText, SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); g2.fillOval(0,0,getWidth(),getHeight());
                g2.setColor(themeColor); g2.setStroke(new BasicStroke(3f)); g2.drawOval(2,2,getWidth()-4,getHeight()-4);
                super.paintComponent(g); g2.dispose();
            }
        };
        lblIcon.setFont(new Font("Tahoma", Font.BOLD, 40));
        lblIcon.setForeground(themeColor);
        lblIcon.setBounds(175, 25, 70, 70);
        mainPanel.add(lblIcon);

        JLabel lblMsg = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        try { lblMsg.setFont(FontManager.regular(16f)); } catch(Exception e) {}
        lblMsg.setBounds(30, 105, 360, 50);
        mainPanel.add(lblMsg);

        JButton btnOK = new JButton("ตกลง");
        btnOK.setBounds(135, 170, 150, 40);
        btnOK.setFocusPainted(false);
        btnOK.setBorderPainted(false);
        btnOK.setContentAreaFilled(false);
        btnOK.setForeground(Color.WHITE);
        btnOK.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(themeColor); g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
                g2.dispose(); super.paint(g, c);
            }
        });
        btnOK.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnOK);
        
        dialog.setVisible(true);
    }
}