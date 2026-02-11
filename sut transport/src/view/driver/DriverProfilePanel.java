package view.driver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import javax.imageio.ImageIO;

import model.Driver;
import controller.SystemManager;
import view.user.FontManager;
import view.user.RoundedPanel;

public class DriverProfilePanel extends JPanel {

    private Driver currentDriver;
    private SystemManager systemManager;

    private JTextField txtName;
    private JTextField txtBusId;
    private JPasswordField txtPassword;
    private JTextField txtEmail;
    
    // 🎨 ธีมสี
    private final Color THEME_BG_CREAM = new Color(255, 244, 225);
    private final Color THEME_ORANGE = new Color(255, 160, 80);
    private final Color THEME_ORANGE_DARK = new Color(230, 120, 50);
    private final Color THEME_TEXT_BROWN = new Color(90, 50, 20);

    public DriverProfilePanel(Driver driver, SystemManager manager) {
        this.currentDriver = driver;
        this.systemManager = manager;

        setLayout(null);
        setBackground(THEME_BG_CREAM);

        initUI();
    }

    private void initUI() {
        // --- หัวข้อ ---
        JLabel lblTitle = new JLabel("โปรไฟล์");
        lblTitle.setFont(getFont(true, 36));
        lblTitle.setForeground(THEME_TEXT_BROWN);
        lblTitle.setBounds(50, 20, 300, 50);
        add(lblTitle);

        // --- การ์ดหลัก ---
        RoundedPanel cardPanel = new RoundedPanel(30) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        cardPanel.setLayout(null);
        cardPanel.setBounds(50, 90, 950, 500); 
        add(cardPanel);

        int startY = 40;
        int gapY = 85; 
        int labelX = 50;
        int fieldX = 50;
        int fieldW = 400;
        
        // 1. ชื่อผู้ใช้
        createLabel(cardPanel, "ชื่อผู้ใช้ :", labelX, startY);
        txtName = createRoundedTextField(currentDriver != null ? currentDriver.getName() : "");
        txtName.setBounds(fieldX, startY + 30, fieldW, 45);
        cardPanel.add(txtName);

        // 2. รหัสรถบัส (แก้ไขให้ดึงค่าจริง)
        createLabel(cardPanel, "รหัสรถบัส :", labelX, startY + gapY);
        // ✅ [แก้] ดึงจาก getBusId()
        String busIdVal = (currentDriver != null) ? currentDriver.getBusId() : "-";
        txtBusId = createRoundedTextField(busIdVal); 
        txtBusId.setBounds(fieldX, startY + gapY + 30, fieldW, 45);
        cardPanel.add(txtBusId);

        // 3. รหัสผ่าน
        createLabel(cardPanel, "รหัสผ่าน :", labelX, startY + gapY * 2);
        String passVal = (currentDriver != null) ? currentDriver.getPassword() : "";
        txtPassword = createRoundedPasswordField(passVal);
        txtPassword.setBounds(fieldX, startY + gapY * 2 + 30, fieldW, 45);
        cardPanel.add(txtPassword);

        // 4. Email (แก้ไขให้ดึงค่าจริง)
        createLabel(cardPanel, "Email :", labelX, startY + gapY * 3);
        // ✅ [แก้] ดึงจาก getEmail()
        String emailVal = (currentDriver != null) ? currentDriver.getEmail() : "";
        txtEmail = createRoundedTextField(emailVal); 
        txtEmail.setBounds(fieldX, startY + gapY * 3 + 30, fieldW, 45);
        cardPanel.add(txtEmail);

        // --- โลโก้ SUT ---
        JLabel lblLogo = new JLabel();
        ImageIcon icon = loadIcon("/img/logo_sut.png", 200, 280); 
        if (icon == null || icon.getIconWidth() == -1) icon = loadIcon("/img/logo_bus.png", 200, 200);
        if (icon != null) {
            lblLogo.setIcon(icon);
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        }
        lblLogo.setBounds(600, 100, 250, 300);
        cardPanel.add(lblLogo);

        // --- ปุ่มบันทึก ---
        JButton btnSave = new JButton("บันทึก");
        styleRoundedButton(btnSave);
        btnSave.setBounds(300, 420, 150, 50);
        btnSave.addActionListener(e -> saveProfile());
        cardPanel.add(btnSave);
    }

    private void saveProfile() {
        if (currentDriver == null) return;

        String newName = txtName.getText().trim();
        String newBusId = txtBusId.getText().trim();
        String newPass = new String(txtPassword.getPassword()).trim();
        String newEmail = txtEmail.getText().trim();

        if (newName.isEmpty() || newPass.isEmpty()) {
            showCustomDialog("แจ้งเตือน", "กรุณากรอกข้อมูลให้ครบถ้วน", "warning");
            return;
        }

        // ✅ อัปเดตข้อมูลครบทุกช่อง
        currentDriver.setName(newName);
        currentDriver.setPassword(newPass);
        currentDriver.setBusId(newBusId); 
        currentDriver.setEmail(newEmail); 
        
        try {
            systemManager.saveData(); 
            showCustomDialog("สำเร็จ", "บันทึกข้อมูลเรียบร้อยแล้ว!", "success");
        } catch (Exception ex) {
            ex.printStackTrace();
            showCustomDialog("เกิดข้อผิดพลาด", "บันทึกข้อมูลล้มเหลว", "error");
        }
    }

    // --- Helper Methods (เหมือนเดิม) ---
    private void createLabel(JPanel p, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(getFont(true, 18));
        lbl.setForeground(THEME_TEXT_BROWN);
        lbl.setBounds(x, y, 200, 30);
        p.add(lbl);
    }

    private JTextField createRoundedTextField(String text) {
        JTextField field = new JTextField(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g); g2.dispose();
            }
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200)); 
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(5, 15, 5, 15)); 
        field.setFont(getFont(false, 16));
        return field;
    }

    private JPasswordField createRoundedPasswordField(String text) {
        JPasswordField field = new JPasswordField(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g); g2.dispose();
            }
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(5, 15, 5, 15));
        field.setFont(getFont(false, 16));
        return field;
    }

    private void styleRoundedButton(JButton btn) {
        btn.setFont(getFont(true, 18));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (btn.getModel().isPressed()) g2.setColor(THEME_ORANGE_DARK);
                else g2.setColor(THEME_ORANGE);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 45, 45); 
                super.paint(g, c); g2.dispose();
            }
        });
    }
    
    private void styleDialogButton(JButton btn, Color bgColor) {
        btn.setFont(getFont(true, 16));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (btn.getModel().isPressed()) g2.setColor(bgColor.darker());
                else g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
                super.paint(g, c); g2.dispose();
            }
        });
    }

    private void showCustomDialog(String title, String message, String type) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));
        dialog.setSize(420, 230);
        dialog.setLocationRelativeTo(this);
        Color themeColor = "success".equals(type) ? new Color(46, 204, 113) : ("warning".equals(type) ? new Color(243, 156, 18) : new Color(231, 76, 60));
        String iconText = "success".equals(type) ? "✓" : ("warning".equals(type) ? "!" : "✕");
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
        lblMsg.setFont(getFont(false, 16));
        lblMsg.setForeground(new Color(80, 80, 80));
        lblMsg.setBounds(30, 105, 360, 50);
        mainPanel.add(lblMsg);
        JButton btnOK = new JButton("ตกลง");
        styleDialogButton(btnOK, themeColor);
        btnOK.setBounds(135, 170, 150, 40);
        btnOK.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnOK);
        dialog.setVisible(true);
    }

    private Font getFont(boolean bold, float size) {
        try { return bold ? FontManager.bold(size) : FontManager.regular(size); } 
        catch (Exception e) { return new Font("Tahoma", bold ? Font.BOLD : Font.PLAIN, (int)size); }
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            URL url = getClass().getResource(path);
            if (url == null && path.startsWith("/")) url = getClass().getResource(path.substring(1));
            if (url == null) return new ImageIcon();
            Image img = ImageIO.read(url);
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return null; }
    }
}