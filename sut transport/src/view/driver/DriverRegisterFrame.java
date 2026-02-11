package view.driver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import controller.SystemManager;
import view.user.FontManager;
import view.user.RoundedPanel;

public class DriverRegisterFrame extends JFrame {

    private JTextField txtUsername, txtEmail, txtBusID;
    private JPasswordField txtPassword;
    private SystemManager systemManager;

    public DriverRegisterFrame(SystemManager manager) {
        this.systemManager = manager;
        
        setTitle("SUT-TRANSPORT - สมัครสมาชิกคนขับ");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(255, 244, 225));
        getContentPane().setLayout(null);

        JLabel lblLogo = new JLabel("SUT-TRANSPORT", SwingConstants.CENTER);
        try { lblLogo.setFont(FontManager.bold(28f)); } catch(Exception e) {}
        lblLogo.setForeground(new Color(90, 50, 20));
        lblLogo.setBounds(0, 20, 900, 40);
        getContentPane().add(lblLogo);

        // Panel
        RoundedPanel panel = new RoundedPanel(20);
        panel.setBounds(220, 80, 460, 470);
        panel.setLayout(null);
        panel.setBackground(new Color(255, 175, 80)); 
        getContentPane().add(panel);

        JLabel lblTitle = new JLabel("สมัครสมาชิก (คนขับ)", SwingConstants.CENTER);
        try { lblTitle.setFont(FontManager.bold(22f)); } catch(Exception e) {}
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 20, 460, 30);
        panel.add(lblTitle);

        // --- Inputs ---
        int startY = 70;
        int gap = 80;

        addLabelAndField(panel, "ชื่อผู้ใช้ :", startY);
        txtUsername = createTextField(panel, startY + 30);

        addLabelAndField(panel, "Email :", startY + gap);
        txtEmail = createTextField(panel, startY + gap + 30);

        addLabelAndField(panel, "รหัสผ่าน :", startY + (gap * 2));
        RoundedPanel passBox = createWhiteInputBox();
        passBox.setBounds(70, startY + (gap * 2) + 30, 320, 35);
        passBox.setLayout(null);
        panel.add(passBox);
        txtPassword = new JPasswordField();
        txtPassword.setBorder(null);
        try { txtPassword.setFont(FontManager.regular(14f)); } catch(Exception e) {}
        txtPassword.setBounds(10, 5, 300, 25);
        passBox.add(txtPassword);

        addLabelAndField(panel, "หมายเลขรถ (Bus ID) :", startY + (gap * 3));
        txtBusID = createTextField(panel, startY + (gap * 3) + 30);

        // Button
        JButton btnRegister = new JButton("ลงทะเบียน");
        styleInteractiveButton(btnRegister);
        btnRegister.setBounds(60, 395, 340, 45); 
        panel.add(btnRegister);

        JButton btnBack = createCustomBackButton();
        getContentPane().add(btnBack);

        // Events
        btnRegister.addActionListener(e -> {
            String name = txtUsername.getText().trim();
            String email = txtEmail.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();
            String busId = txtBusID.getText().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || busId.isEmpty()) {
                showCustomDialog("แจ้งเตือน", "กรุณากรอกข้อมูลให้ครบถ้วน", "warning");
                return;
            }

            boolean success = systemManager.registerDriver(name, email, pass, busId);
            if (success) {
                showCustomDialog("สำเร็จ", "สมัครสมาชิกคนขับสำเร็จ!", "success");
                dispose();
                SwingUtilities.invokeLater(() -> new DriverLoginFrame(this.systemManager).setVisible(true));
            } else {
                showCustomDialog("ผิดพลาด", "สมัครไม่สำเร็จ (Email ซ้ำ)", "error");
            }
        });
    }

    // ✨ [แก้ไข] เพิ่ม (AbstractButton) Cast
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
                
                // 🚩 [แก้ตรงนี้]
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

    private void addLabelAndField(JPanel p, String text, int y) {
        JLabel lbl = new JLabel(text);
        try { lbl.setFont(FontManager.bold(14f)); } catch(Exception e) {}
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(70, y, 200, 25);
        p.add(lbl);
    }
    
    private JTextField createTextField(JPanel p, int y) {
        RoundedPanel box = createWhiteInputBox();
        box.setBounds(70, y, 320, 35);
        box.setLayout(null);
        p.add(box);
        JTextField txt = new JTextField();
        txt.setBorder(null);
        try { txt.setFont(FontManager.regular(14f)); } catch(Exception e) {}
        txt.setBounds(10, 5, 300, 25);
        box.add(txt);
        return txt;
    }

    private RoundedPanel createWhiteInputBox() {
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
        btnBack.setBounds(750, 520, 110, 45); 
        btnBack.addActionListener(e -> {
            dispose();
            new DriverLoginFrame(this.systemManager).setVisible(true);
        });
        return btnBack;
    }

    private void showCustomDialog(String title, String message, String type) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));
        dialog.setSize(420, 230);
        dialog.setLocationRelativeTo(this);
        Color themeColor = "success".equals(type) ? new Color(46, 204, 113) : new Color(231, 76, 60);
        String iconText = "success".equals(type) ? "✓" : "✕";
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