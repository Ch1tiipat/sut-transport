package view.user;

import javax.swing.*;
import java.awt.*;

public class SideMenuPanel extends JPanel {

    public SideMenuPanel(JFrame parent) {

        setLayout(null);
        setBackground(new Color(255, 167, 73)); // สีส้มตาม Figma
        setBounds(0, 0, 120, parent.getHeight()); // ความกว้าง 120px

        // ===============================
        // โลโก้
        // ===============================
        ImageIcon logo = new ImageIcon("img/logo.png"); // ใส่รูปที่คุณให้มา
        JLabel lblLogo = new JLabel(logo);
        lblLogo.setBounds(10, 10, 100, 100);
        add(lblLogo);

        JLabel lblText = new JLabel("SU TRANS");
        lblText.setFont(FontManager.bold(12f));
        lblText.setForeground(Color.WHITE);
        lblText.setBounds(15, 90, 100, 30);
        add(lblText);

        // ===============================
        // ปุ่มเมนู
        // ===============================

        addMenuButton("img/ic_home.png", 150, e -> showPanel(parent, "home"));
        addLine(200);

        addMenuButton("img/ic_bus.png", 230, e -> showPanel(parent, "booking"));
        addLine(280);

        addMenuButton("img/ic_ticket.png", 310, e -> showPanel(parent, "history"));
        addLine(360);

        addMenuButton("img/ic_map.png", 390, e -> showPanel(parent, "map"));
        addLine(440);

        addMenuButton("img/ic_logout.png", parent.getHeight() - 120, e -> System.exit(0));
    }

    // ===============================
    // ปุ่มไอคอนแบบ Figma
    // ===============================
    private void addMenuButton(String iconPath, int y, java.awt.event.ActionListener action) {

        JButton btn = new JButton(new ImageIcon(iconPath));
        btn.setBounds(35, y, 50, 50);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.addActionListener(action);
        add(btn);
    }

    // ===============================
    // เส้นคั่นสีขาวบาง ๆ
    // ===============================
    private void addLine(int y) {
        JPanel line = new JPanel();
        line.setBackground(new Color(255, 255, 255, 120)); // ขาวโปร่งบาง
        line.setBounds(20, y, 80, 2);
        add(line);
    }

    // ===============================
    // สลับ panel ตามเมนู
    // ===============================
    private void showPanel(JFrame parent, String name) {
        CardLayout cl = (CardLayout) parent.getContentPane().getLayout();
        cl.show(parent.getContentPane(), name);
    }
}
