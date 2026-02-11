package view.driver;

import controller.SystemManager;
import model.Booking;
import view.user.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class DriverStatsPanel extends JPanel {

    private SystemManager systemManager;
    
    // Dropdowns
    private JComboBox<String> cbDateFilter; 
    private JComboBox<String> cbStatType;   
    
    private JLabel lblChartImage; 
    private JPanel legendPanel;

    private Map<String, Integer> currentStats = new HashMap<>();
    private int totalPassengers = 0;
    
    private final String DEFAULT_DATE_OPTION = "แสดงทุกวัน";

    // ชุดสี Modern Pastel
    private final Color[] CHART_COLORS = {
        new Color(255, 99, 132),   // ชมพูเข้ม
        new Color(54, 162, 235),   // ฟ้าสดใส
        new Color(255, 206, 86),   // เหลือง
        new Color(75, 192, 192),   // เขียวมินต์
        new Color(153, 102, 255),  // ม่วง
        new Color(255, 159, 64),   // ส้ม
        new Color(231, 233, 237)   // เทา
    };
    
    private final Color THEME_ORANGE = new Color(255, 167, 73);

    public DriverStatsPanel(SystemManager manager) {
        this.systemManager = manager;

        setLayout(null);
        setBackground(new Color(255, 244, 225)); 

        // 1. หัวข้อ
        JLabel lblTitle = new JLabel("สรุปยอดและสถิติ");
        try { lblTitle.setFont(FontManager.bold(32f)); } catch(Exception e) { lblTitle.setFont(new Font("Tahoma", Font.BOLD, 32)); }
        lblTitle.setForeground(new Color(90, 50, 20)); 
        lblTitle.setBounds(50, 30, 300, 45);
        add(lblTitle);

        // 2. การ์ดพื้นหลัง
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(null);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBounds(50, 100, 890, 480);
        cardPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 3, 1, new Color(220, 220, 220)));
        add(cardPanel);

        JLabel lblSubTitle = new JLabel("สถิติผู้โดยสาร");
        lblSubTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblSubTitle.setBounds(40, 30, 200, 30);
        cardPanel.add(lblSubTitle);

        // =================================================================
        // ส่วนควบคุม : วันที่ (ซ้าย) + ประเภท (ขวา)
        // =================================================================
        
        // 1. Dropdown เลือกวันที่
        cbDateFilter = new JComboBox<>();
        cbDateFilter.setBounds(390, 25, 200, 45); 
        cbDateFilter.setFont(new Font("Tahoma", Font.BOLD, 14));
        cbDateFilter.setForeground(new Color(80, 80, 80));
        cbDateFilter.setFocusable(false);
        cbDateFilter.setOpaque(false);
        cbDateFilter.setUI(new ModernComboBoxUI());
        cbDateFilter.setRenderer(new ModernListRenderer());
        
        loadDateData();
        
        cbDateFilter.addActionListener(e -> updateDisplay());
        cardPanel.add(cbDateFilter);

        // 2. Dropdown เลือกประเภท (จำแนกตาม...)
        String[] options = {"จำแนกตามปลายทาง", "จำแนกตามรอบเวลา"};
        cbStatType = new JComboBox<>(options);
        cbStatType.setBounds(600, 25, 260, 45); 
        cbStatType.setFont(new Font("Tahoma", Font.BOLD, 14));
        cbStatType.setForeground(new Color(80, 80, 80));
        cbStatType.setFocusable(false);
        cbStatType.setOpaque(false); 
        cbStatType.setUI(new ModernComboBoxUI());
        cbStatType.setRenderer(new ModernListRenderer());
        cbStatType.addActionListener(e -> updateDisplay()); 
        cardPanel.add(cbStatType);

        // =================================================================

        // พื้นที่แสดงกราฟ
        lblChartImage = new JLabel();
        lblChartImage.setBounds(60, 80, 380, 380); 
        cardPanel.add(lblChartImage);

        // พื้นที่ Legend
        legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS)); 
        legendPanel.setOpaque(false);
        legendPanel.setBounds(460, 120, 400, 300);
        cardPanel.add(legendPanel);

        updateDisplay();
    }

    private void loadDateData() {
        cbDateFilter.removeAllItems();
        cbDateFilter.addItem(DEFAULT_DATE_OPTION); 
        
        if (systemManager != null) {
            List<Booking> bookings = systemManager.getAllBookings();
            Set<String> uniqueDates = new HashSet<>();
            if (bookings != null) {
                for (Booking b : bookings) {
                    if (!"ยกเลิก".equals(b.getStatus())) {
                        uniqueDates.add(b.getDate());
                    }
                }
            }
            List<String> sortedDates = new ArrayList<>(uniqueDates);
            Collections.sort(sortedDates);
            
            for (String d : sortedDates) {
                cbDateFilter.addItem(d);
            }
        }
    }

    public void updateDisplay() {
        calculateStats();
        BufferedImage chartImg = createChartImage();
        lblChartImage.setIcon(new ImageIcon(chartImg));
        updateLegend();
    }

    private void calculateStats() {
        currentStats.clear();
        totalPassengers = 0;
        
        String selectedDate = (String) cbDateFilter.getSelectedItem();
        String selectedType = (String) cbStatType.getSelectedItem();
        
        List<Booking> bookings = (systemManager != null) ? systemManager.getAllBookings() : null;

        if (bookings != null && !bookings.isEmpty()) {
            for (Booking b : bookings) {
                if (!"ยกเลิก".equals(b.getStatus())) {
                    
                    boolean isDateMatch = false;
                    if (selectedDate == null || DEFAULT_DATE_OPTION.equals(selectedDate)) {
                        isDateMatch = true; 
                    } else {
                        if (b.getDate().equals(selectedDate)) {
                            isDateMatch = true;
                        }
                    }

                    if (isDateMatch) {
                        String key;
                        if ("จำแนกตามปลายทาง".equals(selectedType)) {
                            key = b.getDestination();
                        } else {
                            // 🚩 [แก้ไข] จัดการเรื่องเวลาให้มี " น." เสมอ (รวมกลุ่มข้อมูลเก่า/ใหม่)
                            String rawTime = b.getTime().trim();
                            if (!rawTime.endsWith("น.")) {
                                key = rawTime + " น.";
                            } else {
                                key = rawTime;
                            }
                        }
                        
                        int seats = b.getSeatCount();
                        currentStats.put(key, currentStats.getOrDefault(key, 0) + seats);
                        totalPassengers += seats;
                    }
                }
            }
        }
        
        if ("จำแนกตามรอบเวลา".equals(selectedType)) {
            Map<String, Integer> sortedMap = new TreeMap<>(currentStats);
            currentStats = sortedMap;
        }
    }

    private BufferedImage createChartImage() {
        int width = 380;
        int height = 380;
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffer.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int pieSize = 300;
        int x = (width - pieSize) / 2;
        int y = (height - pieSize) / 2;
        
        if (totalPassengers == 0) {
            g2.setColor(new Color(245, 245, 245));
            g2.fillOval(x, y, pieSize, pieSize); 
            
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Tahoma", Font.BOLD, 18));
            String msg = "ไม่มีข้อมูลการจอง";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (width - fm.stringWidth(msg)) / 2, height / 2 + 5);
            
            g2.dispose();
            return buffer;
        }

        double startAngle = 90;
        int colorIndex = 0;
        double holeRatio = 0.65; 

        for (Map.Entry<String, Integer> entry : currentStats.entrySet()) {
            int value = entry.getValue();
            double arcAngle = ((double) value / totalPassengers) * 360;

            Arc2D.Double arc = new Arc2D.Double(x, y, pieSize, pieSize, startAngle, arcAngle, Arc2D.PIE);
            
            g2.setColor(CHART_COLORS[colorIndex % CHART_COLORS.length]);
            g2.fill(arc);
            
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(4f));
            g2.draw(arc);

            if (arcAngle > 10) { 
                double midAngle = Math.toRadians(startAngle + arcAngle / 2);
                double radius = (pieSize / 2.0) * 0.82; 
                int labelX = x + pieSize/2 + (int)(Math.cos(midAngle) * radius);
                int labelY = y + pieSize/2 - (int)(Math.sin(midAngle) * radius);
                
                String pText = Math.round(((double)value/totalPassengers)*100) + "%";
                g2.setFont(new Font("Tahoma", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                
                g2.setColor(new Color(0,0,0,50));
                g2.drawString(pText, labelX - fm.stringWidth(pText)/2 + 1, labelY + fm.getAscent()/2 + 1);
                
                g2.setColor(Color.WHITE);
                g2.drawString(pText, labelX - fm.stringWidth(pText)/2, labelY + fm.getAscent()/2);
            }
            startAngle += arcAngle;
            colorIndex++;
        }

        int holeSize = (int)(pieSize * holeRatio);
        int holeX = x + (pieSize - holeSize) / 2;
        int holeY = y + (pieSize - holeSize) / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(holeX, holeY, holeSize, holeSize);

        g2.setColor(new Color(80, 80, 80));
        g2.setFont(new Font("Tahoma", Font.PLAIN, 16));
        String totalLabel = "ยอดรวม";
        FontMetrics fmLabel = g2.getFontMetrics();
        g2.drawString(totalLabel, (width - fmLabel.stringWidth(totalLabel)) / 2, (height / 2) - 10);
        
        g2.setColor(THEME_ORANGE);
        g2.setFont(new Font("Tahoma", Font.BOLD, 36));
        String totalVal = String.valueOf(totalPassengers);
        FontMetrics fmVal = g2.getFontMetrics();
        g2.drawString(totalVal, (width - fmVal.stringWidth(totalVal)) / 2, (height / 2) + 25);
        
        g2.setFont(new Font("Tahoma", Font.PLAIN, 14));
        g2.setColor(Color.GRAY);
        String unit = "คน";
        g2.drawString(unit, (width - g2.getFontMetrics().stringWidth(unit)) / 2, (height / 2) + 50);

        g2.dispose();
        return buffer;
    }

    private void updateLegend() {
        legendPanel.removeAll();
        if (totalPassengers == 0) {
            legendPanel.revalidate();
            legendPanel.repaint();
            return;
        }
        
        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : currentStats.entrySet()) {
            String labelName = entry.getKey();
            int count = entry.getValue();
            double percent = ((double) count / totalPassengers) * 100;
            
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
            row.setOpaque(false);
            
            JPanel colorBox = new RoundedPanel(5) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                }
            };
            colorBox.setPreferredSize(new Dimension(18, 18));
            colorBox.setBackground(CHART_COLORS[colorIndex % CHART_COLORS.length]);
            colorBox.setOpaque(false);
            
            String htmlText = String.format("<html><span style='font-size:14px; color:#555;'><b>%s</b></span> <span style='color:#888; margin-left:10px;'>%d คน (%.1f%%)</span></html>", labelName, count, percent);
            JLabel lblText = new JLabel(htmlText);
            
            row.add(colorBox);
            row.add(lblText);
            legendPanel.add(row);
            colorIndex++;
        }
        legendPanel.revalidate();
        legendPanel.repaint();
    }

    private class ModernComboBoxUI extends BasicComboBoxUI {
        @Override protected JButton createArrowButton() {
            JButton btn = new JButton();
            btn.setContentAreaFilled(false);
            btn.setBorder(new EmptyBorder(0,0,0,0));
            btn.setFocusPainted(false);
            btn.setIcon(new Icon() { 
                @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(THEME_ORANGE);
                    int size = 8; int shiftX = 5; int shiftY = 8;
                    g2.fillPolygon(new int[]{x+shiftX, x+shiftX+size, x+shiftX+size/2}, 
                                   new int[]{y+shiftY, y+shiftY, y+shiftY+size}, 3);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 20; }
                @Override public int getIconHeight() { return 20; }
            });
            return btn;
        }
        @Override public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
            g2.setColor(THEME_ORANGE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, c.getWidth()-1, c.getHeight()-1, 20, 20);
            g2.dispose();
            super.paint(g, c);
        }
        @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {}
        @Override protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                @Override protected JScrollPane createScroller() {
                    JScrollPane scroller = super.createScroller();
                    scroller.setVerticalScrollBar(new JScrollBar(JScrollBar.VERTICAL) {
                        @Override public Dimension getPreferredSize() { return new Dimension(5, super.getPreferredSize().height); }
                    }); return scroller;
                }
            }; popup.setBorder(BorderFactory.createLineBorder(THEME_ORANGE, 1)); return popup;
        }
    }

    private class ModernListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setBorder(new EmptyBorder(8, 15, 8, 15)); 
            if (isSelected && index != -1) {
                c.setBackground(new Color(255, 235, 215)); 
                c.setForeground(new Color(120, 70, 0));   
                c.setOpaque(true);
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.DARK_GRAY);
                c.setOpaque(index != -1); 
            }
            if (index == -1) {
                c.setOpaque(false);
                c.setForeground(Color.DARK_GRAY);
            }
            return c;
        }
    }
    
    private class RoundedPanel extends JPanel {
        private int radius;
        public RoundedPanel(int radius) { this.radius = radius; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}