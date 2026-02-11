package view.user;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {

    private int cornerRadius;

    public RoundedPanel(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setOpaque(false); // เพื่อให้วาดเอง ไม่ใช้พื้นหลัง JPanel เดิม
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // ไล่สีส้มแบบ Figma
        Color top = new Color(255, 190, 120);     // ส้มอ่อนด้านบน
        Color bottom = new Color(255, 167, 73);   // ส้มเข้มด้านล่าง
        GradientPaint gp = new GradientPaint(
                0, 0, top,
                0, getHeight(), bottom
        );
        g2.setPaint(gp);

        // วาด panel แบบมุมโค้ง radius 20 px
        Shape round = new RoundRectangle2D.Float(
                0, 0, getWidth(), getHeight(),
                cornerRadius, cornerRadius
        );
        g2.fill(round);

        g2.dispose();
    }
}
