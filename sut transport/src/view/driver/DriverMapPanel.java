package view.driver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import model.Driver;
import view.user.FontManager;  
import view.user.RoundedPanel; 

public class DriverMapPanel extends JPanel {

    private Driver currentDriver;

    // --- 1. รายชื่อสถานี ---
    private final String[] STATION_NAMES = {
        "เทคโนธานี", "The Mall", "Terminal 21", "บขส. ใหม่", "Central Plaza", "อนุสาวรีย์ย่าโม"
    };
    
    // --- 2. รูปหมุดเล็ก (บนแผนที่) ---
    private final String[] STATION_ICONS = {
        "/img/pin_techno1.png", "/img/pin_mall1.png", "/img/pin_terminal1.png", 
        "/img/pin_bks1.png", "/img/pin_central1.png", "/img/pin_yamo1.png"
    };
    
    // --- 3. พิกัดหมุด ---
    private final Point[] LOCATIONS = {
        new Point(240, 360), // 1. เทคโนธานี
        new Point(365, 110), // 2. The Mall
        new Point(430, 95), // 3. Terminal 21
        new Point(480,  50), // 4. บขส. ใหม่
        new Point(570, 60),  // 5. Central Plaza
        new Point(530, 155), // 6. อนุสาวรีย์ย่าโม
    };

    private Image bgMapImage = loadIconImage("/img/bg_map.jpg"); 
    
    private Map<String, String[]> stationGallery = new HashMap<>();

    // 🎨 ชุดสีธีมน่ารัก (Cute Theme Colors)
    private final Color COLOR_CREAM_BG = new Color(255, 248, 225); // สีครีมเหลืองนวล
    private final Color COLOR_ORANGE_BTN = new Color(255, 160, 100); // สีส้มพาสเทล
    private final Color COLOR_GRAY_BTN = new Color(180, 180, 180);   // สีเทา
    private final Color COLOR_RED_CLOSE = new Color(255, 100, 100);  // สีแดงพาสเทล

    public DriverMapPanel(Driver driver) {
        this.currentDriver = driver;

        setLayout(null);
        setBackground(new Color(255, 244, 225)); // พื้นหลังหน้าหลัก
        
        initGalleryData();

        // --- ส่วนหัวข้อ ---
        JLabel lblTitle = new JLabel("เส้นทางการเดินรถ");
        Font customFont = new Font("Tahoma", Font.BOLD, 32);
        try { customFont = FontManager.bold(32f); } catch (Exception e) {}
        
        lblTitle.setFont(customFont);
        lblTitle.setForeground(new Color(90, 50, 20));
        lblTitle.setBounds(50, 15, 400, 50); 
        add(lblTitle);

        // --- Map Panel ---
        JPanel mapPanel = new RoundedPanel(10) { 
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                
                if (bgMapImage != null) {
                    g2.drawImage(bgMapImage, 0, 0, w, h, null);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, w, h);
                }
                
                g2.setColor(new Color(200, 150, 100));
                g2.setStroke(new BasicStroke(4f));
                g2.drawRect(0, 0, w, h);
            }
        };
        
        mapPanel.setLayout(null);
        mapPanel.setOpaque(false);
        mapPanel.setBounds(40, 80, 830, 440); 
        add(mapPanel);

        Font customBold12 = new Font("Tahoma", Font.BOLD, 12);
        try { customBold12 = FontManager.bold(12f); } catch (Exception e) {}

        for (int i = 0; i < STATION_NAMES.length; i++) {
            createStationPin(mapPanel, i, customBold12);
        }
    }
    
    // =========================================================================
    // 🚩 ข้อมูลรูปภาพ Gallery
    // =========================================================================
    private void initGalleryData() {
        // 1. เทคโนธานี
        stationGallery.put("เทคโนธานี", new String[]{
            "/img/pin_techno1.png",  
            "/img/pin_techno2.png",
            "/img/pin_techno3.png"
        });
        
        // 2. The Mall
        stationGallery.put("The Mall", new String[]{
            "/img/pin_mall1.png",
            "/img/pin_mall2.png",
            "/img/pin_mall3.png"
        });

        // 3. Terminal 21
        stationGallery.put("Terminal 21", new String[]{
            "/img/pin_terminal1.png",
            "/img/pin_terminal2.png",
            "/img/pin_terminal3.png"
        });

        // 4. บขส. ใหม่
        stationGallery.put("บขส. ใหม่", new String[]{
            "/img/pin_bks1.png",
            "/img/pin_bks2.png",
            "/img/pin_bks3.png"
        });

        // 5. Central Plaza
        stationGallery.put("Central Plaza", new String[]{
            "/img/pin_central1.png",
            "/img/pin_central2.png",
            "/img/pin_central3.png"
        });

        // 6. อนุสาวรีย์ย่าโม
        stationGallery.put("อนุสาวรีย์ย่าโม", new String[]{
            "/img/pin_yamo1.png",
            "/img/pin_yamo2.png",
            "/img/pin_yamo3.png"
        });
    }

    // =========================================================================

    private void createStationPin(JPanel panel, int index, Font font) {
        int x = LOCATIONS[index].x;
        int y = LOCATIONS[index].y;

        String name = STATION_NAMES[index];
        String iconPath = STATION_ICONS[index];
        Image iconImg = loadIconImage(iconPath);

        JButton btnPin = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 140, 50));
                } else {
                    g2.setColor(new Color(230, 90, 40));
                }
                
                g2.fillOval(10, 5, 40, 40);
                int[] xPoints = {20, 40, 30};
                int[] yPoints = {35, 35, 60}; 
                g2.fillPolygon(xPoints, yPoints, 3);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(13, 8, 34, 34);

                if (iconImg != null) {
                    Shape originalClip = g2.getClip();
                    java.awt.geom.Ellipse2D.Float circleClip = new java.awt.geom.Ellipse2D.Float(15, 10, 30, 30);
                    g2.setClip(circleClip);
                    g2.drawImage(iconImg, 15, 10, 30, 30, null);
                    g2.setClip(originalClip);
                }
                g2.dispose();
            }
        };

        btnPin.setContentAreaFilled(false);
        btnPin.setBorderPainted(false);
        btnPin.setFocusPainted(false);
        btnPin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPin.setBounds(x - 30, y - 60, 60, 70); 
        
        btnPin.addActionListener(e -> showGalleryPopup(name));

        panel.add(btnPin);

        JLabel lblName = new JLabel(name, SwingConstants.CENTER) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textX = (getWidth() - textWidth) / 2;
                int textY = 15; 

                g2.setColor(new Color(255, 255, 255, 240));
                g2.drawString(getText(), textX - 1, textY); 
                g2.drawString(getText(), textX + 1, textY);
                g2.drawString(getText(), textX, textY - 1);
                g2.drawString(getText(), textX, textY + 1);
                
                g2.setColor(Color.BLACK);
                g2.drawString(getText(), textX, textY); 
                g2.dispose();
            }
        };
        lblName.setFont(font);
        lblName.setBounds(x - 70, y + 10, 140, 25); 
        panel.add(lblName);
    }

    // =========================================================================
    // ✨ [UI ใหม่] Popup น่ารัก + ปุ่มวงรี (Capsule Shape)
    // =========================================================================
    private void showGalleryPopup(String stationName) {
        String[] images = stationGallery.get(stationName);
        
        // Fallback
        if (images == null || images.length == 0) {
             for(int i=0; i<STATION_NAMES.length; i++) {
                 if(STATION_NAMES[i].equals(stationName)) {
                     images = new String[]{ STATION_ICONS[i] };
                     break;
                 }
             }
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "ภาพสถานที่: " + stationName, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 580); 
        dialog.setLocationRelativeTo(this);
        
        // 1. พื้นหลัง Dialog เป็นสีครีมเหลือง
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(COLOR_CREAM_BG);
        dialog.setContentPane(contentPanel);

        final String[] galleryImages = images;
        final int[] currentIndex = {0}; 

        // 2. ส่วนหัว (Header)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_CREAM_BG);
        headerPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        JLabel lblHeader = new JLabel(stationName);
        lblHeader.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblHeader.setForeground(new Color(120, 70, 0)); // น้ำตาลเข้ม
        headerPanel.add(lblHeader);
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // 3. พื้นที่แสดงรูป
        JPanel imageContainer = new JPanel(new GridBagLayout()); 
        imageContainer.setBackground(COLOR_CREAM_BG);
        
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        
        // กรอบรูปสีขาว ขอบมน
        lblImage.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 5),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        updateGalleryImage(lblImage, galleryImages[0], stationName); 
        imageContainer.add(lblImage);
        contentPanel.add(imageContainer, BorderLayout.CENTER);

        // 4. แผงควบคุม (Control Panel)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(COLOR_CREAM_BG);
        controlPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // ปุ่ม < ก่อนหน้า และ ถัดไป >
        JButton btnPrev = new JButton(" < ก่อนหน้า ");
        JButton btnNext = new JButton(" ถัดไป > ");
        
        JLabel lblCounter = new JLabel("  1 / " + galleryImages.length + "  ");
        lblCounter.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblCounter.setForeground(new Color(100, 60, 20));

        // ใช้ฟังก์ชันแต่งปุ่มวงรี
        styleRoundedButton(btnNext, COLOR_ORANGE_BTN);
        styleRoundedButton(btnPrev, COLOR_ORANGE_BTN);
        
        JButton btnClose = new JButton("ปิด");
        styleRoundedButton(btnClose, COLOR_RED_CLOSE);
        
        btnClose.addActionListener(e -> dialog.dispose());

        // Logic ปุ่มถัดไป
        btnNext.addActionListener(e -> {
            if (currentIndex[0] < galleryImages.length - 1) {
                currentIndex[0]++;
                updateGalleryImage(lblImage, galleryImages[currentIndex[0]], stationName);
                lblCounter.setText("  " + (currentIndex[0] + 1) + " / " + galleryImages.length + "  ");
                
                btnPrev.setEnabled(true);
                btnNext.setEnabled(currentIndex[0] < galleryImages.length - 1);
            }
        });

        // Logic ปุ่มย้อนกลับ
        btnPrev.addActionListener(e -> {
            if (currentIndex[0] > 0) {
                currentIndex[0]--;
                updateGalleryImage(lblImage, galleryImages[currentIndex[0]], stationName);
                lblCounter.setText("  " + (currentIndex[0] + 1) + " / " + galleryImages.length + "  ");
                
                btnNext.setEnabled(true);
                btnPrev.setEnabled(currentIndex[0] > 0);
            }
        });
        
        // สถานะเริ่มต้น
        btnPrev.setEnabled(false);
        btnNext.setEnabled(galleryImages.length > 1);

        controlPanel.add(btnPrev);
        controlPanel.add(lblCounter);
        controlPanel.add(btnNext);
        controlPanel.add(Box.createHorizontalStrut(30)); 
        controlPanel.add(btnClose);

        contentPanel.add(controlPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void updateGalleryImage(JLabel label, String path, String stationName) {
        int targetWidth = 600;
        int targetHeight = 350; 
        
        Image img = loadIconImage(path);
        
        // Fallback
        if (img == null) {
            String defaultIconPath = null;
            for(int i=0; i<STATION_NAMES.length; i++) {
                if(STATION_NAMES[i].equals(stationName)) {
                    defaultIconPath = STATION_ICONS[i];
                    break;
                }
            }
            if (defaultIconPath != null) img = loadIconImage(defaultIconPath); 
        }

        if (img != null) {
            Image scaledImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImg));
            label.setText("");
        } else {
            label.setIcon(null);
            label.setText("ไม่พบภาพ");
        }
    }
    
   
    private void styleRoundedButton(JButton btn, Color bgColor) {
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 40));
        
        // Custom Painter
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                Color color = b.isEnabled() ? bgColor : Color.LIGHT_GRAY;
                
                if (b.getModel().isPressed() && b.isEnabled()) {
                    color = color.darker();
                }
              
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 2, c.getWidth(), c.getHeight(), 40, 40);

                g2.setColor(color);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
                
                super.paint(g, c);
                g2.dispose();
            }
        });
    }

    private Image loadIconImage(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null; 
            return ImageIO.read(url);
        } catch (Exception e) {
            return null;
        }
    }
}