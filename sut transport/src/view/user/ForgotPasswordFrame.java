package view.user;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;
import controller.SystemManager;
import model.User;

public class ForgotPasswordFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtInput;
    private SystemManager systemManager;
    private int generatedOTP; 
    private User foundUser;

    // 🎨 ธีมสี
    private final Color THEME_PRIMARY = new Color(255, 167, 73); // ส้มหลัก
    private final Color THEME_BG = new Color(255, 244, 225);     // พื้นหลังครีม
    private final Color THEME_TEXT = new Color(90, 50, 20);      // น้ำตาลเข้ม

    public ForgotPasswordFrame(SystemManager manager) {
        this.systemManager = manager;

        setTitle("SUT-TRANSPORT - ลืมรหัสผ่าน");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 380);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(THEME_BG);
        getContentPane().setLayout(null);

        // Header
        JLabel lblTitle = new JLabel("ลืมรหัสผ่าน?", SwingConstants.CENTER);
        try { lblTitle.setFont(FontManager.bold(26f)); } catch(Exception e) { lblTitle.setFont(new Font("Tahoma", Font.BOLD, 26)); }
        lblTitle.setForeground(THEME_TEXT);
        lblTitle.setBounds(0, 25, 500, 40);
        getContentPane().add(lblTitle);

        JLabel lblDesc = new JLabel("กรอก Email หรือ รหัสนักศึกษา เพื่อรับรหัส OTP", SwingConstants.CENTER);
        try { lblDesc.setFont(FontManager.regular(14f)); } catch(Exception e) { lblDesc.setFont(new Font("Tahoma", Font.PLAIN, 14)); }
        lblDesc.setForeground(Color.GRAY);
        lblDesc.setBounds(0, 65, 500, 30);
        getContentPane().add(lblDesc);

        // Input Box
        RoundedPanel box = new RoundedPanel(10) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        box.setBounds(75, 120, 350, 45);
        box.setLayout(null);
        getContentPane().add(box);

        txtInput = new JTextField();
        txtInput.setBorder(null);
        try { txtInput.setFont(FontManager.regular(16f)); } catch(Exception e) { txtInput.setFont(new Font("Tahoma", Font.PLAIN, 16)); }
        txtInput.setBounds(10, 8, 330, 30);
        box.add(txtInput);

        // Button OTP
        JButton btnSendOTP = new JButton("รับรหัส OTP");
        styleButton(btnSendOTP, THEME_PRIMARY);
        btnSendOTP.setBounds(75, 190, 350, 50);
        getContentPane().add(btnSendOTP);

        // Button Cancel
        JButton btnClose = new JButton("ยกเลิก");
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        
        // 🚩 แก้ไขตรงนี้: เปลี่ยนจาก Color.GRAY เป็น Color.DARK_GRAY ให้เข้มขึ้น
        btnClose.setForeground(Color.DARK_GRAY); 
        
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setBounds(200, 260, 100, 30);
        btnClose.addActionListener(e -> dispose());
        getContentPane().add(btnClose);

        // --- Event ---
        btnSendOTP.addActionListener(e -> {
            String input = txtInput.getText().trim();
            if (input.isEmpty()) {
                showCustomDialog("แจ้งเตือน", "กรุณากรอกข้อมูล", "warning");
                return;
            }

            foundUser = systemManager.findUserByInput(input);

            if (foundUser != null) {
                Random rand = new Random();
                generatedOTP = 1000 + rand.nextInt(9000); 

                // 1. แจ้งรหัส OTP (Simulation)
                showCustomDialog("SMS Simulation", "รหัส OTP ของคุณคือ: " + generatedOTP, "success");

                // 2. ถาม OTP
                verifyOTPStep();

            } else {
                showCustomDialog("ไม่พบข้อมูล", "ไม่พบผู้ใช้ในระบบ", "error");
            }
        });
    }

    // ขั้นตอนตรวจสอบ OTP
    private void verifyOTPStep() {
        String userOTP = showCustomInputDialog("ยืนยันตัวตน", "กรุณากรอกรหัส OTP 4 หลัก:", false);

        if (userOTP != null) {
            if (userOTP.equals(String.valueOf(generatedOTP))) {
                resetPasswordStep();
            } else {
                showCustomDialog("ผิดพลาด", "รหัส OTP ไม่ถูกต้อง!", "error");
            }
        }
    }

    // ขั้นตอนเปลี่ยนรหัสผ่าน
    private void resetPasswordStep() {
        String newPass = showCustomInputDialog("เปลี่ยนรหัสผ่าน", "กรุณาตั้งรหัสผ่านใหม่:", true);

        if (newPass != null && !newPass.trim().isEmpty()) {
            systemManager.resetUserPassword(foundUser, newPass.trim());
            showCustomDialog("สำเร็จ", "เปลี่ยนรหัสผ่านสำเร็จ!", "success");
            dispose(); 
        } else if (newPass != null) { 
             showCustomDialog("แจ้งเตือน", "รหัสผ่านต้องไม่ว่างเปล่า", "warning");
        }
    }

    // ✨ Custom Input Dialog
    private String showCustomInputDialog(String title, String message, boolean isPassword) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        final String[] result = {null};

        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 40)); g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 25, 25);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 25, 25);
                
                g2.setColor(THEME_PRIMARY);
                Area header = new Area(new RoundRectangle2D.Double(0, 0, getWidth()-10, 60, 25, 25));
                header.add(new Area(new Rectangle2D.Double(0, 25, getWidth()-10, 35)));
                g2.fill(header);
                g2.dispose();
            }
        };
        mainPanel.setLayout(null);
        dialog.add(mainPanel);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 15, 390, 30);
        mainPanel.add(lblTitle);

        JLabel lblMsg = new JLabel(message, SwingConstants.CENTER);
        lblMsg.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblMsg.setBounds(20, 80, 350, 25);
        mainPanel.add(lblMsg);

        JTextField field;
        if (isPassword) {
            field = new JPasswordField();
        } else {
            field = new JTextField();
        }
        field.setFont(new Font("Tahoma", Font.PLAIN, 16));
        field.setBounds(50, 115, 290, 35);
        field.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        mainPanel.add(field);

        JButton btnOK = new JButton("ตกลง");
        styleButton(btnOK, new Color(46, 204, 113));
        btnOK.setBounds(60, 175, 120, 40);
        btnOK.addActionListener(e -> {
            result[0] = field.getText();
            dialog.dispose();
        });
        mainPanel.add(btnOK);

        JButton btnCancel = new JButton("ยกเลิก");
        styleButton(btnCancel, new Color(231, 76, 60));
        btnCancel.setBounds(210, 175, 120, 40);
        btnCancel.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnCancel);

        dialog.setVisible(true);
        return result[0];
    }

    // ✨ Custom Message Dialog
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
        lblMsg.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblMsg.setBounds(30, 105, 360, 50);
        mainPanel.add(lblMsg);

        JButton btnOK = new JButton("ตกลง");
        styleButton(btnOK, themeColor);
        btnOK.setBounds(135, 170, 150, 40);
        btnOK.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnOK);

        dialog.setVisible(true);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        try { btn.setFont(FontManager.bold(16f)); } catch(Exception e) { btn.setFont(new Font("Tahoma", Font.BOLD, 16)); }
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;
                if (b.getModel().isPressed()) g2.setColor(color.darker());
                else g2.setColor(color);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
                super.paint(g, c); g2.dispose();
            }
        });
    }
}