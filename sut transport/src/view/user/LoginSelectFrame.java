package view.user;

import view.driver.DriverLoginFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controller.SystemManager;

public class LoginSelectFrame extends JFrame {

    private SystemManager systemManager;

    public LoginSelectFrame(SystemManager manager) {
        this.systemManager = manager;

        setTitle("SUT-TRANSPORT - เลือกประเภทการเข้าสู่ระบบ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(255, 244, 225));
        getContentPane().setLayout(null);

        // ---------- โลโก้ด้านบน ----------
        JLabel lblLogo = new JLabel("SUT-TRANSPORT", SwingConstants.CENTER);
        try { lblLogo.setFont(FontManager.bold(24f)); } catch(Exception e) {}
        lblLogo.setBounds(0, 20, 900, 40);
        getContentPane().add(lblLogo);

        // ---------- กล่องส้มมุมโค้งกลางจอ ----------
        RoundedPanel panel = new RoundedPanel(20);
        panel.setBounds(220, 80, 460, 330);
        panel.setLayout(null);
        panel.setBackground(new Color(255, 175, 80)); // สีส้มพื้นหลัง
        getContentPane().add(panel);

        // =====================================================
        // ปุ่ม 1 : เข้าสู่ระบบนักศึกษา
        // =====================================================
        JButton btnStudentLogin = new JButton("เข้าสู่ระบบนักศึกษา");
        btnStudentLogin.setBounds(70, 80, 320, 70);
        
        // ใส่ไอคอน
        Icon studentIcon = loadIcon("/img/ic_driver.png", 40, 40); 
        if(studentIcon != null) btnStudentLogin.setIcon(studentIcon);
        
        styleInteractiveButton(btnStudentLogin); 
        panel.add(btnStudentLogin);

        // =====================================================
        // ปุ่ม 2 : เข้าสู่ระบบคนขับ
        // =====================================================
        JButton btnDriverLogin = new JButton("เข้าสู่ระบบคนขับ");
        btnDriverLogin.setBounds(70, 180, 320, 70);
        
        // ใส่ไอคอน
        Icon driverIcon = loadIcon("/img/ic_student.png", 50, 50); 
        if(driverIcon != null) btnDriverLogin.setIcon(driverIcon);
        
        styleInteractiveButton(btnDriverLogin);
        panel.add(btnDriverLogin);

        // ======================= EVENTS =======================
        btnStudentLogin.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new UserLoginFrame(this.systemManager).setVisible(true));
        });

        btnDriverLogin.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new DriverLoginFrame(this.systemManager).setVisible(true)); 
        });
    }

    private Icon loadIcon(String path, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch(Exception e) {
            return null;
        }
    }
    
    // ✨ ฟังก์ชันแต่งปุ่ม (แก้ไขสีตามที่ขอ)
    private void styleInteractiveButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        
        try { btn.setFont(FontManager.bold(18f)); } catch(Exception e) {
            btn.setFont(new Font("Tahoma", Font.BOLD, 18));
        }
        
        // 🚩 กำหนดสีตัวอักษรเป็นสีน้ำตาล (และจะไม่เปลี่ยนเป็นสีขาวแล้ว)
        btn.setForeground(new Color(110, 60, 20)); 
        
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIconTextGap(15);

        // UI วาดพื้นหลัง
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                
                // 🚩 เช็คสถานะ Hover/Pressed
                if (b.getModel().isRollover() || b.getModel().isPressed()) {
                    // เปลี่ยนพื้นหลังเป็นสีเทาอ่อนๆ (Light Gray)
                    g2.setColor(new Color(235, 235, 235)); 
                } else {
                    // พื้นหลังปกติสีขาว
                    g2.setColor(Color.WHITE); 
                }
                
                // วาดสี่เหลี่ยมมุมมน
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g, c);
                g2.dispose();
            }
        });
    }
}