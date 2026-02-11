package view.user;

import model.Passenger;
import controller.SystemManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.EmptyBorder; 
import java.net.URL; 
import javax.imageio.ImageIO; 

public class UserProfilePanel extends JPanel {

    private Passenger currentUser;
    private SystemManager systemManager;
    private final UserMainFrame parentFrame; 

    private JTextField txtName, txtStudentId, txtEmail;
    private JPasswordField txtPassword;

    public UserProfilePanel(Passenger user, SystemManager manager, UserMainFrame parentFrame) {
        this.currentUser = user; 
        this.systemManager = manager; 
        this.parentFrame = parentFrame; 

        setLayout(null);
        setBackground(new Color(255, 244, 225)); 

        initUI();
    }

    private void initUI() {
        // 1. หัวข้อ
        JLabel lblTitle = new JLabel("โปรไฟล์");
        Font customFont = new Font("Tahoma", Font.BOLD, 36);
        try { customFont = FontManager.bold(36f); } catch (Exception e) {}
        lblTitle.setFont(customFont);
        lblTitle.setForeground(new Color(90, 50, 20)); 
        lblTitle.setBounds(80, 20, 200, 60); 
        add(lblTitle);

        // 2. การ์ดสีขาว
        JPanel cardPanel = new RoundedPanel(30) { 
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        cardPanel.setLayout(null);
        cardPanel.setOpaque(false);
        cardPanel.setBounds(80, 90, 900, 500); 
        add(cardPanel);

        // 3. โลโก้
        JLabel lblSutLogo = new JLabel(loadIcon("/img/logo_sut.png", 200, 250)); 
        lblSutLogo.setBounds(600, 100, 200, 250); 
        cardPanel.add(lblSutLogo);

        // 4. ฟอร์มข้อมูล
        int startX = 60;
        int startY = 40;
        int gap = 85; 

        // --- ชื่อผู้ใช้ ---
        addFormLabel(cardPanel, "ชื่อผู้ใช้ :", startX, startY);
        String userName = (currentUser != null) ? currentUser.getName() : "";
        txtName = addFormTextField(cardPanel, userName, startX, startY + 35);

        // --- รหัสนักศึกษา ---
        addFormLabel(cardPanel, "รหัสนักศึกษา :", startX, startY + gap);
        String studentId = (currentUser != null) ? currentUser.getId() : "";
        txtStudentId = addFormTextField(cardPanel, studentId, startX, startY + gap + 35);

        // --- รหัสผ่าน ---
        addFormLabel(cardPanel, "รหัสผ่าน :", startX, startY + gap * 2);
        String password = (currentUser != null) ? currentUser.getPassword() : "";
        txtPassword = new JPasswordField(password);
        styleTextField(txtPassword);
        txtPassword.setBounds(startX, startY + gap * 2 + 35, 350, 50); 
        cardPanel.add(txtPassword);

        // --- Email (แก้ไขส่วนนี้) ---
        addFormLabel(cardPanel, "Email :", startX, startY + gap * 3);
        // ✅ [แก้] ดึงข้อมูลจาก getEmail() จริงๆ
        String email = (currentUser != null && currentUser.getEmail() != null) ? currentUser.getEmail() : "";
        txtEmail = addFormTextField(cardPanel, email, startX, startY + gap * 3 + 35);

        // 5. ปุ่มบันทึก
        JButton btnSave = new JButton("บันทึก");
        setupSaveButton(btnSave);
        btnSave.setBounds(320, 420, 160, 50);
        cardPanel.add(btnSave);

        btnSave.addActionListener(e -> saveProfile());
    }

    private void saveProfile() {
        if (currentUser == null) return;

        String newName = txtName.getText().trim();
        String newPass = new String(txtPassword.getPassword()).trim();
        String newId = txtStudentId.getText().trim();
        String newEmail = txtEmail.getText().trim(); // รับค่า Email

        if (newName.isEmpty() || newPass.isEmpty() || newId.isEmpty()) {
            showCustomDialog("แจ้งเตือน", "กรุณากรอกข้อมูลให้ครบถ้วน", "warning");
            return;
        }

        // ✅ อัปเดตข้อมูลเข้า Object
        currentUser.setName(newName);
        currentUser.setPassword(newPass);
        currentUser.setId(newId);
        currentUser.setEmail(newEmail); // บันทึก Email

        try {
            systemManager.saveData();
            showCustomDialog("สำเร็จ", "บันทึกข้อมูลเรียบร้อยแล้ว!", "success");
        } catch (Exception e) {
            e.printStackTrace();
            showCustomDialog("ข้อผิดพลาด", "บันทึกข้อมูลล้มเหลว", "error");
        }
    }

    // --- Helper UI Methods ---
    private void setupSaveButton(JButton btn) {
        Font f = new Font("Tahoma", Font.BOLD, 18);
        try { f = FontManager.bold(18f); } catch (Exception e) {}
        btn.setFont(f);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (btn.getModel().isPressed()) g2.setColor(new Color(230, 140, 60));
                else g2.setColor(new Color(255, 167, 73));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 45, 45);
                g2.dispose(); super.paint(g, c);
            }
        });
    }

    private void addFormLabel(JPanel p, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        Font f = new Font("Tahoma", Font.BOLD, 16);
        try { f = FontManager.bold(16f); } catch (Exception e) {}
        lbl.setFont(f); 
        lbl.setForeground(new Color(60, 30, 10)); 
        lbl.setBounds(x, y, 200, 30);
        p.add(lbl);
    }

    private JTextField addFormTextField(JPanel p, String text, int x, int y) {
        JTextField txt = new JTextField(text);
        styleTextField(txt);
        txt.setBounds(x, y, 350, 50); 
        p.add(txt);
        return txt;
    }

    private void styleTextField(JTextField txt) {
        Font f = new Font("Tahoma", Font.PLAIN, 16);
        try { f = FontManager.regular(16f); } catch (Exception e) {}
        txt.setFont(f); 
        txt.setForeground(Color.BLACK);
        txt.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10), BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        txt.setBackground(Color.WHITE);
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
             java.net.URL url = getClass().getResource(path);
             if (url == null) return new ImageIcon();
             Image img = javax.imageio.ImageIO.read(url);
             return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return new ImageIcon(); }
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        RoundedBorder(int radius) { this.radius = radius; }
        public Insets getBorderInsets(Component c) { return new Insets(radius+1, radius+1, radius+2, radius); }
        public boolean isBorderOpaque() { return true; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200)); 
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
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
        lblMsg.setFont(new Font("Tahoma", Font.PLAIN, 16));
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