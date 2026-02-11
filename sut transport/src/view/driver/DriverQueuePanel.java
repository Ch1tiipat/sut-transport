package view.driver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import javax.imageio.ImageIO;

import model.Driver;
import model.Booking; 
import controller.SystemManager;
import view.user.FontManager;
import view.user.RoundedPanel;

public class DriverQueuePanel extends JPanel {

    private Driver currentDriver;
    private SystemManager systemManager; 
    private JPanel seatContainer; 
    private JLabel lblTotalPassenger; 
    
    private JComboBox<String> cbDateSelector;
    private JComboBox<String> cbTimeSelector; 
    private JComboBox<String> cbSeatSelector;
    private JButton btnConfirm;
    
    private Timer refreshTimer;

    private ImageIcon iconSeatEmpty;
    private ImageIcon iconSeatUser;     
    private ImageIcon iconSeatCheckIn;  
    private ImageIcon iconDriver, iconSteering, iconDoor;

    // 🎨 ธีมสี
    private final Color THEME_ORANGE = new Color(255, 167, 73); 
    private final Color THEME_YELLOW_ACTION = new Color(255, 230, 80); // สีเหลืองตอนกด
    private final Color THEME_BG_CREAM = new Color(255, 248, 240);
    private final Color SEAT_BG_COLOR = new Color(255, 235, 215);
    private final Color COLOR_TEXT_BROWN = new Color(90, 50, 20);

    private final int SEAT_SIZE = 50; 

    private final String[] SEAT_NAMES = {
        "A1", "A2", "A3", 
        "B1", "B2", "B3",  
        "C1", "C2", "C3", 
        "D1", "D2", "D3"   
    };

    private final Point[] SEAT_POSITIONS = {
        new Point(130, 130), new Point(330, 130), new Point(400, 130), 
        new Point(130, 210), new Point(330, 210), new Point(400, 210), 
        new Point(130, 290), new Point(330, 290), new Point(400, 290), 
        new Point(130, 370), new Point(330, 370), new Point(400, 370)  
    };

    private final String DEFAULT_SELECTION_TEXT = "เลือกที่นั่ง";
    private final String DEFAULT_DATE_TEXT = "เลือกวันที่";
    
    private final String[] TIME_LIST = {
        "เลือกรอบเวลา", "07 : 30 น.", "09 : 00 น.", "10 : 30 น.",
        "12 : 00 น.", "13 : 30 น.", "15 : 00 น.",
        "16 : 30 น.", "18 : 00 น."
    };

    public DriverQueuePanel(Driver driver, SystemManager sysManager) {
        this.currentDriver = driver;
        this.systemManager = sysManager;
        
        setLayout(null);
        setBackground(THEME_BG_CREAM); 

        loadIcons();
        initUI();
        loadDateData();

        refreshData();
        
        refreshTimer = new Timer(1000, e -> refreshData());
        refreshTimer.start();
    }

    private void loadIcons() {
        iconSeatEmpty    = loadIcon("/img/ic_seat.png", SEAT_SIZE, SEAT_SIZE);
        iconSeatUser     = loadIcon("/img/ic_seat_user.png", SEAT_SIZE, SEAT_SIZE);     
        iconSeatCheckIn  = loadIcon("/img/ic_seat_selected.png", SEAT_SIZE, SEAT_SIZE); 
        iconDriver   = loadIcon("/img/ic_driver1.png", 35, 35); 
        iconSteering = loadIcon("/img/ic_steering.png", 38, 38); 
        iconDoor     = loadIcon("/img/ic_exit.png", 30, 30); 
    }

    private void loadDateData() {
        cbDateSelector.removeAllItems();
        cbDateSelector.addItem(DEFAULT_DATE_TEXT);
        
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
                cbDateSelector.addItem(d);
            }
        }
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("รายการคิว ผู้โดยสาร");
        try { lblTitle.setFont(FontManager.bold(36f)); } catch(Exception e) { lblTitle.setFont(new Font("Tahoma", Font.BOLD, 36)); }
        lblTitle.setForeground(COLOR_TEXT_BROWN);
        lblTitle.setBounds(50, 20, 500, 50);
        add(lblTitle);

        seatContainer = new RoundedPanel(35) { 
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SEAT_BG_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
            }
        };
        seatContainer.setLayout(null);
        seatContainer.setBounds(50, 90, 580, 480); 
        add(seatContainer);

        createRightControlPanel();
    }

    private void createRightControlPanel() {
        JPanel controlPanel = new JPanel(); 
        controlPanel.setLayout(null);
        controlPanel.setOpaque(false); 
        controlPanel.setBounds(660, 90, 300, 520); 

        int yOffset = 0;

        // 1. ป้ายจำนวนคน
        JPanel pnlCount = new RoundedPanel(25);
        pnlCount.setBackground(THEME_ORANGE);
        pnlCount.setBounds(0, yOffset, 280, 55); 
        pnlCount.setLayout(new GridBagLayout()); 
        
        lblTotalPassenger = new JLabel("จำนวนผู้โดยสาร: 0 คน");
        lblTotalPassenger.setFont(new Font("Tahoma", Font.BOLD, 20)); 
        lblTotalPassenger.setForeground(Color.WHITE);
        pnlCount.add(lblTotalPassenger);
        
        controlPanel.add(pnlCount);
        yOffset += 80; 

        // 2. ส่วนเลือกวันที่
        JLabel lblDateTitle = new JLabel("วันที่เดินทาง");
        lblDateTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblDateTitle.setForeground(COLOR_TEXT_BROWN);
        lblDateTitle.setBounds(10, yOffset, 200, 25);
        controlPanel.add(lblDateTitle);
        yOffset += 30;

        cbDateSelector = new JComboBox<>();
        cbDateSelector.setFont(new Font("Tahoma", Font.BOLD, 15));
        cbDateSelector.setBounds(10, yOffset, 260, 50);
        cbDateSelector.setOpaque(false);
        cbDateSelector.setBackground(new Color(0,0,0,0));
        cbDateSelector.setForeground(new Color(80, 80, 80));
        cbDateSelector.setFocusable(false);
        cbDateSelector.setUI(new CleanComboBoxUI());
        cbDateSelector.setRenderer(new StyledListRenderer());
        cbDateSelector.addActionListener(e -> refreshData());
        controlPanel.add(cbDateSelector);
        yOffset += 70;

        // 3. ส่วนเลือกรอบเวลา
        JLabel lblTimeTitle = new JLabel("รอบเวลา");
        lblTimeTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTimeTitle.setForeground(COLOR_TEXT_BROWN);
        lblTimeTitle.setBounds(10, yOffset, 200, 25);
        controlPanel.add(lblTimeTitle);
        yOffset += 30;

        cbTimeSelector = new JComboBox<>(TIME_LIST);
        cbTimeSelector.setFont(new Font("Tahoma", Font.BOLD, 15));
        cbTimeSelector.setBounds(10, yOffset, 260, 50); 
        cbTimeSelector.setOpaque(false);
        cbTimeSelector.setBackground(new Color(0,0,0,0));
        cbTimeSelector.setForeground(new Color(80, 80, 80));
        cbTimeSelector.setFocusable(false);
        cbTimeSelector.setUI(new CleanComboBoxUI());
        cbTimeSelector.addActionListener(e -> refreshData());
        cbTimeSelector.setRenderer(new StyledListRenderer());
        controlPanel.add(cbTimeSelector);
        yOffset += 70;

        // 4. ส่วนยืนยันที่นั่ง
        JLabel lblSeatTitle = new JLabel("ยืนยันที่นั่ง");
        lblSeatTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblSeatTitle.setForeground(COLOR_TEXT_BROWN);
        lblSeatTitle.setBounds(10, yOffset, 200, 25);
        controlPanel.add(lblSeatTitle);
        yOffset += 30;

        cbSeatSelector = new JComboBox<>();
        cbSeatSelector.setFont(new Font("Tahoma", Font.BOLD, 15));
        cbSeatSelector.setBounds(10, yOffset, 260, 50); 
        cbSeatSelector.setOpaque(false);
        cbSeatSelector.setBackground(new Color(0,0,0,0)); 
        cbSeatSelector.setForeground(new Color(80, 80, 80));
        cbSeatSelector.setFocusable(false); 
        cbSeatSelector.setUI(new CleanComboBoxUI());
        cbSeatSelector.setRenderer(new StyledListRenderer());
        controlPanel.add(cbSeatSelector);
        yOffset += 70;

        // 5. ปุ่มตกลง
        btnConfirm = new JButton("ตกลง");
        styleButtonOrangeYellow(btnConfirm); // ใช้ฟังก์ชันแต่งปุ่ม
        btnConfirm.setBounds(40, yOffset, 200, 55); 
        
        btnConfirm.addActionListener(e -> {
            String selectedSeat = (String) cbSeatSelector.getSelectedItem();
            if (selectedSeat != null && !selectedSeat.equals(DEFAULT_SELECTION_TEXT)) {
                confirmPassengerBoarding(selectedSeat);
            } else {
                showCustomDialog("แจ้งเตือน", "กรุณาเลือกที่นั่งผู้โดยสารก่อนครับ", "warning");
            }
        });
        
        controlPanel.add(btnConfirm);
        add(controlPanel);
    }

    // =========================================================================
    // ✨ ฟังก์ชันช่วยแต่งปุ่ม (สีส้ม -> เหลือง)
    // =========================================================================
    private void styleButtonOrangeYellow(JButton btn) {
        btn.setFont(new Font("Tahoma", Font.BOLD, 20));
        btn.setForeground(Color.WHITE); // เริ่มต้นสีขาว
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // ตรวจสอบสถานะการกด
                if (btn.getModel().isPressed() || btn.getModel().isRollover()) {
                    g2.setColor(THEME_YELLOW_ACTION); // สีเหลือง
                    btn.setForeground(COLOR_TEXT_BROWN); // เปลี่ยนตัวหนังสือเป็นสีน้ำตาล
                } else {
                    g2.setColor(THEME_ORANGE); // สีส้ม
                    btn.setForeground(Color.WHITE); // ตัวหนังสือสีขาว
                }
                
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 45, 45); 
                super.paint(g, c);
                g2.dispose();
            }
        });
    }

    public void refreshData() {
        Object selectedSeatItem = cbSeatSelector.getSelectedItem();
        String selectedDate = (String) cbDateSelector.getSelectedItem();
        String selectedTime = (String) cbTimeSelector.getSelectedItem();
        
        boolean isDateSelected = selectedDate != null && !selectedDate.equals(DEFAULT_DATE_TEXT);
        boolean isTimeSelected = selectedTime != null && !selectedTime.equals(TIME_LIST[0]);

        seatContainer.removeAll();
        cbSeatSelector.removeAllItems();
        cbSeatSelector.addItem(DEFAULT_SELECTION_TEXT);

        JLabel lDoor = new JLabel(iconDoor); lDoor.setBounds(40, 20, 30, 30); seatContainer.add(lDoor);
        JLabel lWheel = new JLabel(iconSteering); lWheel.setBounds(240, 30, 38, 38); seatContainer.add(lWheel);
        JLabel lDriver = new JLabel(iconDriver); lDriver.setBounds(242, 90, 35, 35); seatContainer.add(lDriver);

        List<Booking> allBookings = (systemManager != null) ? systemManager.getAllBookings() : new ArrayList<>();
        int passengerCount = 0;

        for (Booking b : allBookings) {
            if (isDateSelected && !b.getDate().equals(selectedDate)) continue;
            if (isTimeSelected && !b.getTime().trim().equals(selectedTime.trim())) continue; 

            if ("รอดำเนินการ".equals(b.getStatus())) {
                String seats = b.getSeatNumbers(); 
                if (seats != null) {
                    String[] seatArray = seats.split(",");
                    for (String s : seatArray) {
                        cbSeatSelector.addItem(s.trim());
                    }
                }
            }
            
            if (!"ยกเลิก".equals(b.getStatus())) {
                passengerCount += b.getSeatCount();
            }
        }
        
        if (selectedSeatItem != null && !DEFAULT_SELECTION_TEXT.equals(selectedSeatItem)) {
             for (int i = 0; i < cbSeatSelector.getItemCount(); i++) {
                 if (cbSeatSelector.getItemAt(i).equals(selectedSeatItem)) {
                     cbSeatSelector.setSelectedItem(selectedSeatItem);
                     break;
                 }
             }
        }
        
        lblTotalPassenger.setText("จำนวนผู้โดยสาร: " + passengerCount + " คน");

        for (int i = 0; i < SEAT_NAMES.length; i++) {
            String seatId = SEAT_NAMES[i];
            Point p = SEAT_POSITIONS[i];
            String seatStatus = "ว่าง"; 
            
            for (Booking b : allBookings) {
                if (isDateSelected && !b.getDate().equals(selectedDate)) continue;
                if (isTimeSelected && !b.getTime().trim().equals(selectedTime.trim())) continue;
                
                if (b.getSeatNumbers() != null && b.getSeatNumbers().contains(seatId)) {
                    if ("สำเร็จ".equals(b.getStatus())) seatStatus = "สำเร็จ";
                    else if ("รอดำเนินการ".equals(b.getStatus())) seatStatus = "รอดำเนินการ";
                    else if ("ยกเลิก".equals(b.getStatus())) continue; 
                    break; 
                }
            }
            createSeatLabel(p.x, p.y, seatId, seatStatus);
        }

        seatContainer.repaint();
        seatContainer.revalidate();
    }

    private void createSeatLabel(int x, int y, String seatId, String status) {
        JLabel lblSeat = new JLabel();
        lblSeat.setBounds(x, y, SEAT_SIZE, SEAT_SIZE); 
        lblSeat.setHorizontalAlignment(SwingConstants.CENTER);
        
        ImageIcon iconToShow = iconSeatEmpty; 
        
        if ("สำเร็จ".equals(status)) {
            iconToShow = iconSeatCheckIn; 
        } else if ("รอดำเนินการ".equals(status)) {
            iconToShow = iconSeatUser;    
        }
        
        if (iconToShow != null) {
            lblSeat.setIcon(iconToShow);
        } else {
            lblSeat.setOpaque(true);
            lblSeat.setBackground(Color.LIGHT_GRAY);
            lblSeat.setText(seatId);
        }

        if ("สำเร็จ".equals(status)) lblSeat.setToolTipText(seatId + " : ขึ้นรถแล้ว");
        else if ("รอดำเนินการ".equals(status)) lblSeat.setToolTipText(seatId + " : รอคนขับยืนยัน");
        else lblSeat.setToolTipText(seatId + " : ว่าง");

        seatContainer.add(lblSeat);
        
        JLabel lblId = new JLabel(seatId, SwingConstants.CENTER);
        lblId.setBounds(x, y + SEAT_SIZE + 2, SEAT_SIZE, 20); 
        lblId.setForeground(new Color(255, 140, 50));
        lblId.setFont(new Font("Tahoma", Font.BOLD, 15)); 
        seatContainer.add(lblId);
    }

    private void confirmPassengerBoarding(String seatId) {
        String selectedDate = (String) cbDateSelector.getSelectedItem();
        String selectedTime = (String) cbTimeSelector.getSelectedItem();
        
        boolean isDateSelected = selectedDate != null && !selectedDate.equals(DEFAULT_DATE_TEXT);
        boolean isTimeSelected = selectedTime != null && !selectedTime.equals(TIME_LIST[0]);

        if (!isDateSelected || !isTimeSelected) {
            showCustomDialog("แจ้งเตือน", "กรุณาเลือกวันที่และรอบเวลา<br>ที่ต้องการยืนยันก่อนครับ", "warning");
            return;
        }

        List<Booking> allBookings = systemManager.getAllBookings();
        boolean found = false;
        
        for (Booking b : allBookings) {
            if ("รอดำเนินการ".equals(b.getStatus()) && 
                b.getSeatNumbers().contains(seatId) &&
                b.getDate().equals(selectedDate) &&
                b.getTime().trim().equals(selectedTime.trim())) {
                
                b.setStatus("สำเร็จ");
                systemManager.saveData(); 
                
                found = true;
                showCustomDialog("สำเร็จ", "ยืนยันผู้โดยสารที่นั่ง " + seatId + " เรียบร้อยแล้ว", "success");
                
                refreshData(); 
                break;
            }
        }
        
        if (!found) {
            showCustomDialog("ข้อผิดพลาด", "ไม่พบข้อมูลการจองในรอบเวลานี้", "error");
        }
    }

    // =========================================================================
    // ✨ Custom Dialog: หน้าต่างแจ้งเตือนสวยๆ
    // =========================================================================
    private void showCustomDialog(String title, String message, String type) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));
        dialog.setSize(400, 220);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 30, 30);
                g2.setColor(THEME_BG_CREAM);
                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 30, 30);
                g2.setColor(THEME_ORANGE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth()-10, getHeight()-10, 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setLayout(null);
        dialog.add(mainPanel);

        // ไอคอน
        JLabel lblIcon = new JLabel();
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        
        if ("warning".equals(type)) {
            lblIcon.setText("⚠️"); 
        } else if ("success".equals(type)) {
            lblIcon.setText("✅");
        } else {
            lblIcon.setText("❌");
        }
        lblIcon.setBounds(160, 20, 80, 60);
        mainPanel.add(lblIcon);

        // ข้อความ
        JLabel lblMsg = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblMsg.setForeground(COLOR_TEXT_BROWN);
        lblMsg.setBounds(20, 80, 360, 60);
        mainPanel.add(lblMsg);

        // ปุ่ม OK (ใช้สไตล์เดียวกับปุ่มหลัก)
        JButton btnOK = new JButton("ตกลง");
        styleButtonOrangeYellow(btnOK); 
        btnOK.setBounds(130, 150, 140, 45);
        btnOK.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnOK);

        dialog.setVisible(true);
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            URL url = getClass().getResource(path);
            if (url == null && path.startsWith("/")) url = getClass().getResource(path.substring(1));
            if (url == null) return null;
            Image img = ImageIO.read(url);
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return null; }
    }

    private class CleanComboBoxUI extends BasicComboBoxUI {
        @Override public void installUI(JComponent c) { super.installUI(c); comboBox.setBorder(new EmptyBorder(0,0,0,0)); }
        @Override public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 25, 25); 
            g2.setColor(THEME_ORANGE); g2.setStroke(new BasicStroke(2f)); g2.drawRoundRect(0, 0, c.getWidth()-1, c.getHeight()-1, 25, 25);
            g2.dispose(); super.paint(g, c); 
        }
        @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {}
        @Override protected JButton createArrowButton() {
            JButton btn = new JButton(); btn.setContentAreaFilled(false); btn.setBorder(new EmptyBorder(0,0,0,0)); btn.setFocusPainted(false);
            btn.setIcon(new Icon() {
                @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(THEME_ORANGE); int size = 8; int shiftX = 5; int shiftY = 8;
                    g2.fillPolygon(new int[]{x+shiftX, x+shiftX+size, x+shiftX+size/2}, new int[]{y+shiftY, y+shiftY, y+shiftY+size}, 3); g2.dispose();
                }
                @Override public int getIconWidth() { return 20; } @Override public int getIconHeight() { return 20; }
            }); return btn;
        }
        @Override protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                @Override protected JScrollPane createScroller() {
                    JScrollPane scroller = super.createScroller(); scroller.setVerticalScrollBar(new JScrollBar(JScrollBar.VERTICAL) {
                        @Override public Dimension getPreferredSize() { return new Dimension(5, super.getPreferredSize().height); }
                    }); return scroller;
                }
            }; popup.setBorder(BorderFactory.createLineBorder(THEME_ORANGE, 1)); popup.getList().setBackground(Color.WHITE); return popup;
        }
    }
    
    private class StyledListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setHorizontalAlignment(SwingConstants.CENTER);
            c.setBorder(new EmptyBorder(5, 0, 5, 0));
            
            if (isSelected && index != -1) {
                c.setOpaque(true);
                c.setBackground(THEME_ORANGE);
                c.setForeground(Color.WHITE);
            } else {
                c.setOpaque(false); 
                c.setForeground(Color.DARK_GRAY);
            }

            if (DEFAULT_SELECTION_TEXT.equals(value) || TIME_LIST[0].equals(value) || DEFAULT_DATE_TEXT.equals(value)) {
                c.setForeground(Color.GRAY);
                c.setFont(new Font("Tahoma", Font.PLAIN, 14));
            } else {
                c.setFont(new Font("Tahoma", Font.BOLD, 14));
                if (isSelected && index != -1) c.setForeground(Color.WHITE);
            }
            return c;
        }
    }
}