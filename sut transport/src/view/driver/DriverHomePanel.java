package view.driver;

import view.user.FontManager;
import view.user.RoundedPanel;
import controller.SystemManager;
import model.Booking;
import model.BusStatus; 

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.net.URL;

public class DriverHomePanel extends JPanel {

    private DriverMainFrame parentFrame;
    private SystemManager systemManager;
    
    // 🎨 ธีมสี
    private final Color THEME_BG_CREAM = new Color(255, 244, 225);
    private final Color THEME_TEXT_BROWN = new Color(90, 50, 20);
    private final Color COLOR_BTN_ORANGE = new Color(255, 167, 73);
    private final Color COLOR_BTN_GREEN = new Color(46, 204, 113);
    
    private final String[] STATION_NAMES = {
        "เทคโนธานี", "The Mall", "Terminal 21", 
        "บขส. ใหม่", "Central", "ลานท้าวสุรนารี", "กลับถึงเทคโนธานี"
    };

    private final String[] SCHEDULES = {
        "เลือกรอบเวลา...", "07 : 30 น.", "09 : 00 น.", "10 : 30 น.", "12 : 00 น.",
        "13 : 30 น.", "15 : 00 น.", "16 : 30 น.", "18 : 00 น."
    };

    private JPanel listPanel;
    private JComboBox<String> cbRounds;
    private RoundedButton btnAction; // ปุ่มควบคุมหลัก (ประกาศเป็น Global เพื่อแก้ข้อความ)

    public DriverHomePanel(DriverMainFrame parent, SystemManager manager) {
        this.parentFrame = parent;
        this.systemManager = manager;

        setBackground(THEME_BG_CREAM);
        setLayout(null);
        
        BusStatus.addListener(() -> {
            renderStationList();
            updateButtonState(); // อัปเดตปุ่มเมื่อสถานะเปลี่ยน
            if (listPanel != null) {
                listPanel.revalidate();
                listPanel.repaint();
            }
        });

        JLabel lblTitle = new JLabel("หน้าหลัก");
        try { lblTitle.setFont(FontManager.bold(32f)); } catch(Exception e) { lblTitle.setFont(new Font("Tahoma", Font.BOLD, 32)); }
        lblTitle.setForeground(THEME_TEXT_BROWN);
        lblTitle.setBounds(50, 20, 200, 45);
        add(lblTitle);

        addRoundSelector();
        createMainCard();
        updateButtonState(); // เรียกครั้งแรก
    }

    private void addRoundSelector() {
        JLabel lblRound = new JLabel("รอบเวลาวิ่ง :");
        lblRound.setFont(getFont(true, 18f));
        lblRound.setForeground(THEME_TEXT_BROWN);
        lblRound.setBounds(500, 25, 120, 35);
        add(lblRound);

        cbRounds = new JComboBox<>(SCHEDULES);
        cbRounds.setFont(new Font("Tahoma", Font.PLAIN, 16));
        cbRounds.setBounds(620, 25, 180, 35);
        cbRounds.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (!BusStatus.currentRoundTime.equals("-")) {
            cbRounds.setSelectedItem(BusStatus.currentRoundTime);
        }

        cbRounds.addActionListener(e -> {
            String selected = (String) cbRounds.getSelectedItem();
            if (selected != null && !selected.equals("เลือกรอบเวลา...")) {
                BusStatus.currentRoundTime = selected; 
            } else {
                BusStatus.currentRoundTime = "-";
            }
            BusStatus.notifyAllScreens(); 
        });

        add(cbRounds);
    }

    private void createMainCard() {
        RoundedPanel card = new RoundedPanel(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setBounds(50, 80, 860, 500); 
        card.setLayout(null);
        add(card);

        // Header
        RoundedPanel header = new RoundedPanel(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 175, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        header.setBounds(0, 0, 860, 50);
        header.setLayout(null);
        card.add(header);

        JLabel hStation = new JLabel("สถานี", SwingConstants.CENTER);
        hStation.setFont(getFont(true, 18f));
        hStation.setForeground(Color.WHITE);
        hStation.setBounds(30, 10, 300, 30);
        header.add(hStation);

        JLabel hCount = new JLabel("จำนวนผู้โดยสาร ขึ้น-ลง", SwingConstants.CENTER);
        hCount.setFont(getFont(true, 18f));
        hCount.setForeground(Color.WHITE);
        hCount.setBounds(400, 10, 350, 30);
        header.add(hCount);

        // List Panel
        listPanel = new JPanel();
        listPanel.setLayout(null);
        listPanel.setOpaque(false);
        listPanel.setBounds(0, 60, 860, 440); 
        card.add(listPanel);

        // ✨ ปุ่มควบคุมการเดินรถ (สร้างครั้งเดียวแล้วเก็บใส่ตัวแปร)
        btnAction = new RoundedButton("สถานีถัดไป", 20);
        btnAction.setFont(getFont(true, 18f));
        btnAction.setForeground(Color.WHITE);
        btnAction.setBounds(620, 385, 200, 50); // ขยายปุ่มให้ใหญ่ขึ้น
        
        btnAction.addActionListener(e -> handleBusAction());
        
        listPanel.add(btnAction);

        renderStationList();
    }

    // ✨ ฟังก์ชันจัดการปุ่มกด (Logic 2 จังหวะ)
    private void handleBusAction() {
        if (BusStatus.currentRoundTime.equals("-") || cbRounds.getSelectedIndex() == 0) {
            showCustomDialog("แจ้งเตือน", "กรุณาเลือกรอบเวลาก่อน!", "warning");
            return;
        }

        if (BusStatus.isEnRoute) {
            // จังหวะ 2: กำลังวิ่งอยู่ -> กดเพื่อ "ถึงสถานี"
            BusStatus.arriveAtNextStation();
            
            if (BusStatus.currentStationIndex == 0) {
                showCustomDialog("แจ้งเตือน", "จบการเดินรถรอบนี้ เริ่มต้นใหม่", "info");
            }
        } else {
            // จังหวะ 1: จอดอยู่ -> กดเพื่อ "ออกเดินทาง"
            if (BusStatus.currentStationIndex < STATION_NAMES.length - 1) {
                BusStatus.departFromStation();
            } else {
                // กรณีสุดสายแล้ว (Index 6)
                showCustomDialog("แจ้งเตือน", "สิ้นสุดระยะทางแล้ว กรุณารีเซ็ตหรือเริ่มรอบใหม่", "info");
                // อาจจะ Reset กลับไป 0 อัตโนมัติก็ได้ถ้าต้องการ
                BusStatus.currentStationIndex = 0; 
                BusStatus.notifyAllScreens();
            }
        }
    }

    // ✨ อัปเดตหน้าตาปุ่มตามสถานะ
    private void updateButtonState() {
        if (btnAction == null) return;

        if (BusStatus.isEnRoute) {
            // รถกำลังวิ่ง -> ปุ่มต้องบอกว่า "กดเมื่อถึงสถานี..."
            String nextStationName = "สถานีถัดไป";
            int nextIndex = BusStatus.currentStationIndex + 1;
            if (nextIndex < STATION_NAMES.length) {
                nextStationName = STATION_NAMES[nextIndex];
            }
            btnAction.setText("ถึง: " + nextStationName);
            btnAction.setBackground(COLOR_BTN_GREEN); // สีเขียว = กดเมื่อถึง
        } else {
            // รถจอดอยู่ -> ปุ่มต้องบอกว่า "ออกเดินทาง"
            btnAction.setText("ออกเดินทาง >");
            btnAction.setBackground(COLOR_BTN_ORANGE); // สีส้ม = กดเพื่อไปต่อ
        }
    }

    private void renderStationList() {
        
        Component[] comps = listPanel.getComponents();
        for (Component c : comps) {
            if (c != btnAction) {
                listPanel.remove(c);
            }
        }

        int startY = 10;
        int gap = 53; 

        for (int i = 0; i < STATION_NAMES.length; i++) {
            String stationName = STATION_NAMES[i];
            boolean isCurrent = (i == BusStatus.currentStationIndex);

            // ป้ายชื่อสถานี
            JPanel nameTag = createStationNameTag(stationName, isCurrent);
            nameTag.setBounds(80, startY, 200, 45);
            listPanel.add(nameTag);

            int upCount = countPassengers(stationName, true);
            int downCount = countPassengers(stationName, false);

            JPanel btnDown = createCountBadge(downCount, false); 
            btnDown.setBounds(460, startY, 130, 45);
            listPanel.add(btnDown);

            JPanel btnUp = createCountBadge(upCount, true); 
            btnUp.setBounds(610, startY, 130, 45);
            listPanel.add(btnUp);

            startY += gap;
        }
    }

    
    private void showCustomDialog(String title, String message, String type) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 25, 25);
                g2.setColor(THEME_BG_CREAM);
                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 25, 25);
                g2.setColor(THEME_BG_CREAM); 
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth()-10, getHeight()-10, 25, 25);
                g2.dispose();
            }
        };
        mainPanel.setLayout(null);
        dialog.add(mainPanel);

        JLabel lblMsg = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        lblMsg.setFont(getFont(true, 16));
        lblMsg.setForeground(THEME_TEXT_BROWN);
        lblMsg.setBounds(50, 40, 300, 60);
        mainPanel.add(lblMsg);

        JButton btnOK = new JButton("ตกลง");
        btnOK.setBounds(140, 130, 120, 40);
        btnOK.setFocusPainted(false);
        btnOK.setBorderPainted(false);
        btnOK.setBackground(COLOR_BTN_ORANGE);
        btnOK.setForeground(Color.WHITE);
        btnOK.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnOK);

        dialog.setVisible(true);
    }

    private int countPassengers(String stationName, boolean checkSource) {
        if (systemManager == null) return 0;
        List<Booking> bookings = systemManager.getAllBookings();
        int total = 0;
        for (Booking b : bookings) {
            if (!"ยกเลิก".equals(b.getStatus())) {
                if (checkSource) {
                    if (stationName.equals(b.getSource())) total += b.getSeatCount();
                } else {
                    if (stationName.equals(b.getDestination())) total += b.getSeatCount();
                }
            }
        }
        return total;
    }

    private JPanel createStationNameTag(String text, boolean isHighlight) {
        RoundedPanel p = new RoundedPanel(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isHighlight) {
                    g2.setColor(new Color(255, 175, 80)); 
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.setColor(new Color(220, 220, 220)); 
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                }
            }
        };
        p.setLayout(new BorderLayout());
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(getFont(true, 15f));
        lbl.setForeground(isHighlight ? Color.WHITE : Color.BLACK);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private JPanel createCountBadge(int count, boolean isUp) {
        Color bgColor = isUp ? new Color(144, 238, 144) : new Color(255, 92, 92);
        String iconPath = isUp ? "/img/ic_Up.png" : "/img/ic_Down.png";
        
        RoundedPanel p = new RoundedPanel(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        p.setLayout(null);

        JLabel icon = new JLabel(loadIcon(iconPath, 20, 20));
        icon.setBounds(15, 12, 20, 20);
        p.add(icon);
        
        JLabel lblCount = new JLabel(count + " คน");
        lblCount.setFont(getFont(true, 15f));
        lblCount.setForeground(isUp ? new Color(30, 100, 30) : Color.WHITE);
        lblCount.setBounds(50, 10, 70, 25);
        p.add(lblCount);

        return p;
    }

    private Font getFont(boolean bold, float size) {
        try { return bold ? FontManager.bold(size) : FontManager.regular(size); } 
        catch (Exception e) { return new Font("Tahoma", bold ? Font.BOLD : Font.PLAIN, (int)size); }
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return new ImageIcon();
            Image img = ImageIO.read(url);
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return new ImageIcon(); }
    }

    class RoundedButton extends JButton {
        private int radius;
        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else {
                g2.setColor(getBackground());
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }
}