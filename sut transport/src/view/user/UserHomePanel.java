package view.user;

import model.BusStatus; 
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class UserHomePanel extends JPanel {

    private UserMainFrame parentFrame;

    private final String[] STATION_NAMES = {
        "เทคโนธานี", "The Mall", "Terminal 21", 
        "บขส. ใหม่", "Central", "ลานท้าวสุรนารี", "กลับถึงเทคโนธานี"
    };

    private final int[] TIME_OFFSETS = {0, 15, 18, 21, 31, 41, 61};

    private JPanel panelStatusList;
    private JPanel panelScheduleList;

    public UserHomePanel(UserMainFrame parentFrame) {
        this.parentFrame = parentFrame;
        initPanel();
    }

    public UserHomePanel() {
        initPanel();
    }

    private void initPanel() {
        setLayout(null);
        setBackground(new Color(255, 244, 225)); 

        BusStatus.addListener(() -> {
            updateStatusDisplay();
        });

        JLabel lblTitle = new JLabel("หน้าหลัก");
        try { lblTitle.setFont(FontManager.bold(32f)); } catch(Exception e) { lblTitle.setFont(new Font("Tahoma", Font.BOLD, 32)); }
        lblTitle.setForeground(new Color(90, 50, 20)); 
        lblTitle.setBounds(50, 30, 400, 50); 
        add(lblTitle);

        createSplitTables();
        updateStatusDisplay();
    }

    private void createSplitTables() {
        // --- ตารางซ้าย ---
        RoundedPanel leftCard = new RoundedPanel(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 175, 80));
                g2.fillRoundRect(0, 0, getWidth(), 80, 20, 20); 
                g2.fillRect(0, 40, getWidth(), 20); 
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 60, getWidth(), getHeight() - 60, 20, 20);
                g2.fillRect(0, 60, getWidth(), 20);
                g2.dispose();
            }
        };
        leftCard.setLayout(null);
        leftCard.setOpaque(false);
        leftCard.setBounds(50, 100, 600, 440); 
        add(leftCard);

        addHeaderLabel(leftCard, "สถานี", 30, 15, 150);
        addHeaderLabel(leftCard, "เวลาถึง (โดยประมาณ)", 200, 15, 200); 
        addHeaderLabel(leftCard, "สถานะ", 420, 15, 150);

        panelStatusList = new JPanel();
        panelStatusList.setLayout(null);
        panelStatusList.setOpaque(false);
        panelStatusList.setBounds(0, 60, 600, 380);
        leftCard.add(panelStatusList);

        // --- ตารางขวา ---
        RoundedPanel rightCard = new RoundedPanel(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 175, 80));
                g2.fillRoundRect(0, 0, getWidth(), 80, 20, 20);
                g2.fillRect(0, 40, getWidth(), 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 60, getWidth(), getHeight() - 60, 20, 20);
                g2.fillRect(0, 60, getWidth(), 20);
                g2.dispose();
            }
        };
        rightCard.setLayout(null);
        rightCard.setOpaque(false);
        rightCard.setBounds(680, 100, 280, 440); 
        add(rightCard);

        addHeaderLabel(rightCard, "ตารางออกรถ", 0, 15, 280);

        panelScheduleList = new JPanel();
        panelScheduleList.setLayout(null);
        panelScheduleList.setOpaque(false);
        panelScheduleList.setBounds(0, 65, 280, 375);
        rightCard.add(panelScheduleList);
    }

    private void addHeaderLabel(JPanel p, String text, int x, int y, int w) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        try { lbl.setFont(FontManager.bold(18f)); } catch (Exception e) { lbl.setFont(new Font("Tahoma", Font.BOLD, 18)); }
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(x, y, w, 30);
        p.add(lbl);
    }

    private void updateStatusDisplay() {
        if (panelStatusList == null || panelScheduleList == null) return;
        
        panelStatusList.removeAll();
        panelScheduleList.removeAll();
        
        int currentBusIndex = BusStatus.currentStationIndex;
        boolean isEnRoute = BusStatus.isEnRoute; // ✨ รับค่าสถานะการวิ่ง
        String activeRound = BusStatus.currentRoundTime; 
        
        Font fontName = new Font("Tahoma", Font.BOLD, 16);
        Font fontStatus = new Font("Tahoma", Font.BOLD, 14);
        Font fontTime = new Font("Tahoma", Font.PLAIN, 16);
        try { 
            fontName = FontManager.bold(16f); 
            fontStatus = FontManager.bold(14f);
            fontTime = FontManager.regular(16f);
        } catch (Exception e) {}

        // --- 1. อัปเดตตารางซ้าย ---
        int rowHeightStatus = 48;
        int startY_Status = 20;

        for (int i = 0; i < STATION_NAMES.length; i++) {
            JLabel lblName = new JLabel(STATION_NAMES[i]);
            lblName.setFont(fontName);
            lblName.setForeground(Color.BLACK);
            lblName.setBounds(30, startY_Status, 180, 30);
            panelStatusList.add(lblName);

            String estimatedTime = calculateETA(activeRound, TIME_OFFSETS[i]);
            JLabel lblETA = new JLabel(estimatedTime, SwingConstants.CENTER);
            lblETA.setFont(fontTime);
            lblETA.setForeground(new Color(100, 100, 100)); 
            lblETA.setBounds(200, startY_Status, 200, 30);
            panelStatusList.add(lblETA);

            // ✨ Logic การเปลี่ยนสีและข้อความ
            String statusText;
            Color statusColor;

            if (i < currentBusIndex) {
                // สถานีที่ผ่านมาแล้ว
                statusText = "ผ่านแล้ว";
                statusColor = new Color(220, 50, 50); // แดง
            } else if (i == currentBusIndex) {
                // สถานีปัจจุบัน
                if (isEnRoute) {
                    // ถ้ากำลังวิ่งออกจากสถานีนี้ -> ถือว่าผ่านไปแล้ว/กำลังออก
                    statusText = "ออกมาเเล้ว";
                    statusColor = new Color(220, 50, 50); // แดง (หรือส้มเข้มก็ได้)
                } else {
                    // จอดอยู่ -> ถึงแล้ว
                    statusText = "ถึงแล้ว";
                    statusColor = new Color(40, 160, 60); // เขียว
                }
            } else if (i == currentBusIndex + 1) {
                // สถานีถัดไป
                if (isEnRoute) {
                    // กำลังวิ่งมาหา -> สีส้ม
                    statusText = "กำลังมา";
                    statusColor = new Color(255, 140, 0); // ส้ม
                } else {
                    // ยังไม่วิ่งมา -> สีน้ำเงิน
                    statusText = "กำลังรอ";
                    statusColor = new Color(100, 100, 255); // น้ำเงิน
                }
            } else {
                // สถานีไกลๆ
                statusText = "กำลังรอ";
                statusColor = new Color(100, 100, 255); // น้ำเงิน
            }
            
            // กรณีพิเศษ: ถ้ายังไม่เลือกรอบ
            if (activeRound.equals("-")) {
                statusText = "-";
                statusColor = Color.GRAY;
            }

            RoundedLabel lblStatus = new RoundedLabel(statusText, statusColor, fontStatus);
            lblStatus.setBounds(420, startY_Status, 120, 30);
            panelStatusList.add(lblStatus);

            startY_Status += rowHeightStatus;
        }

        // --- 2. อัปเดตตารางขวา (Highlight) ---
        String[] schedules = {
            "07 : 30 น.", "09 : 00 น.", "10 : 30 น.", "12 : 00 น.",
            "13 : 30 น.", "15 : 00 น.", "16 : 30 น.", "18 : 00 น."
        };
        
        int rowHeightSchedule = 44; 
        int startY_Schedule = 10; 

        for (String time : schedules) {
            boolean isActive = time.equals(activeRound);
            
            if (isActive) {
                RoundedLabel lblActive = new RoundedLabel(time, new Color(46, 204, 113), fontName);
                lblActive.setBounds(40, startY_Schedule, 200, 36);
                panelScheduleList.add(lblActive);
            } else {
                JLabel lblTime = new JLabel(time, SwingConstants.CENTER);
                lblTime.setFont(fontName);
                lblTime.setForeground(new Color(60, 60, 60)); 
                lblTime.setBounds(0, startY_Schedule, 280, 30);
                panelScheduleList.add(lblTime);
            }
            startY_Schedule += rowHeightSchedule; 
        }

        panelStatusList.repaint();
        panelStatusList.revalidate();
        panelScheduleList.repaint();
        panelScheduleList.revalidate();
    }
    
    private String calculateETA(String baseTime, int addMinutes) {
        if (baseTime.equals("-") || baseTime.equals("เลือกรอบเวลา...")) return "-";
        try {
            String clean = baseTime.replace(" น.", "").trim(); 
            String[] parts = clean.split(" : ");
            int h = Integer.parseInt(parts[0].trim());
            int m = Integer.parseInt(parts[1].trim());
            m += addMinutes;
            while (m >= 60) { m -= 60; h = (h + 1) % 24; }
            return String.format("%02d : %02d น.", h, m);
        } catch (Exception e) { return "-"; }
    }
    
    class RoundedLabel extends JLabel {
        private Color bgColor;
        public RoundedLabel(String text, Color color, Font font) {
            super(text, SwingConstants.CENTER);
            this.bgColor = color;
            setFont(font);
            setForeground(Color.WHITE);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}