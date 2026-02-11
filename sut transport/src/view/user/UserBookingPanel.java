package view.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.net.URL;

import model.Passenger;
import controller.SystemManager;
import model.Booking;

public class UserBookingPanel extends JPanel {

    private Passenger currentUser;
    private SystemManager systemManager;
    private final UserMainFrame parentFrame;
    private final List<Integer> selectedSeatIndexes = new ArrayList<>();
    private final List<String> occupiedSeats = new ArrayList<>(); 
    private JLabel[] seatLabels;
    
    private final String[] SEAT_NAMES = {
        "A1", "A2", "A3", 
        "B1", "B2", "B3",  
        "C1", "C2", "C3", 
        "D1", "D2", "D3"   
    };

    private final int SEAT_SIZE = 42; 
    
    private JComboBox<String> cbFrom, cbTo, cbTime;
    private JComboBox<String> cbDay, cbMonth, cbYear; 
    private JLabel lblCount;
    private JButton btnConfirm;

    private Icon iconSeatEmpty, iconSeatSelected, iconSeatOccupied;
    private Icon iconDriver, iconDoor, iconSteering;
    private Icon iconLoc, iconCal, iconClock, iconSwap, iconDownArrow;
    
    // 🎨 ธีมสี
    private final Color COLOR_ORANGE = new Color(255, 167, 73);
    private final Color COLOR_ORANGE_DARK = new Color(230, 140, 50);
    private final Color COLOR_BG_CREAM = new Color(255, 244, 225);
    private final Color COLOR_TEXT_BROWN = new Color(90, 50, 20);

    private final String[] STATION_LIST = {
        "เลือกจุดออกเดินทาง", "เทคโนธานี", "The Mall", "Terminal 21", 
        "บขส. ใหม่", "Central", "ลานท้าวสุรนารี", "กลับถึงเทคโนธานี"
    };
    private final String[] DESTINATION_LIST = {
        "เลือกปลายทาง", "เทคโนธานี", "The Mall", "Terminal 21", 
        "บขส. ใหม่", "Central", "ลานท้าวสุรนารี", "กลับถึงเทคโนธานี"
    };
    
    // 🚩 [แก้ไข] เพิ่ม " น." ต่อท้ายเวลา
    private final String[] TIME_LIST = {
        "เลือกรอบเวลา", "07 : 30 น.", "09 : 00 น.", "10 : 30 น.",
        "12 : 00 น.", "13 : 30 น.", "15 : 00 น.",
        "16 : 30 น.", "18 : 00 น."
    };
    
    private final String[] THAI_MONTHS = {
        "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
        "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
    };

    public UserBookingPanel(Passenger user, SystemManager manager, UserMainFrame parentFrame) {
        this.currentUser = user;
        this.systemManager = manager;
        this.parentFrame = parentFrame;

        setLayout(null);
        setBackground(COLOR_BG_CREAM); 

        loadIcons();
        createHeader();
        createMainLayout();
        
        checkSeatAvailability();
    }

    private void loadIcons() {
        iconSeatEmpty    = loadIcon("/img/ic_seat.png", SEAT_SIZE, SEAT_SIZE);
        iconSeatSelected = loadIcon("/img/ic_seat_selected.png", SEAT_SIZE, SEAT_SIZE);
        iconSeatOccupied = loadIcon("/img/ic_seat_user.png", SEAT_SIZE, SEAT_SIZE); 
        
        iconDriver   = loadIcon("/img/ic_driver1.png", 32, 32);   
        iconSteering = loadIcon("/img/ic_steering.png", 35, 35); 
        iconDoor     = loadIcon("/img/ic_exit.png", 28, 28);      
        
        iconLoc       = loadIcon("/img/ic_location.png", 20, 20);
        iconCal       = loadIcon("/img/ic_calendar.png", 20, 20);
        iconClock     = loadIcon("/img/ic_clock.png", 20, 20);
        iconSwap      = loadIcon("/img/ic_swap.png", 28, 28);
        iconDownArrow = loadIcon("/img/ic_down_arrow.png", 16, 16);
    }

    private void createHeader() {
        JLabel lblTitle = new JLabel("จองตั๋ว");
        try { lblTitle.setFont(FontManager.bold(32f)); } catch(Exception e) { lblTitle.setFont(new Font("Tahoma", Font.BOLD, 32)); }
        lblTitle.setForeground(new Color(120, 70, 30));
        lblTitle.setBounds(50, 20, 200, 45);
        add(lblTitle);
        
        JLabel lblSub = new JLabel("กรุณาเลือกที่ต้องการไป");
        try { lblSub.setFont(FontManager.bold(16f)); } catch(Exception e) {}
        lblSub.setForeground(new Color(255, 140, 50));
        lblSub.setBounds(50, 65, 300, 25);
        add(lblSub);
    }

    private void createMainLayout() {
        RoundedPanel mainCard = new RoundedPanel(30) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        mainCard.setLayout(null);
        mainCard.setBounds(50, 100, 950, 500); 
        add(mainCard);

        int startX = 60;
        int fieldW = 320; 
        int fieldH = 45;
        
        int startInputY = 25;  
        int verticalGap = 60;  

        // --- 1. ส่วนเลือกเส้นทาง (แถวบน) ---
        JPanel pnlFrom = createInputBox(iconLoc);
        pnlFrom.setBounds(startX, startInputY, fieldW, fieldH);
        mainCard.add(pnlFrom);
        cbFrom = createStyledComboBox(STATION_LIST, true); 
        cbFrom.addActionListener(e -> checkSeatAvailability()); 
        pnlFrom.add(cbFrom);

        JLabel lblSwap = new JLabel(iconSwap);
        lblSwap.setBounds(startX + fieldW + 20, startInputY + 8, 30, 30); 
        mainCard.add(lblSwap);
        
        JPanel pnlTo = createInputBox(iconLoc);
        pnlTo.setBounds(startX + fieldW + 70, startInputY, fieldW, fieldH); 
        mainCard.add(pnlTo);
        cbTo = createStyledComboBox(DESTINATION_LIST, true);
        cbTo.addActionListener(e -> checkSeatAvailability()); 
        pnlTo.add(cbTo);
        
        // --- 2. ส่วนเลือกวันเวลา (แถวสอง) ---
        JPanel pnlDate = createDateInputBox(iconCal, fieldW, fieldH);
        pnlDate.setBounds(startX, startInputY + verticalGap, fieldW, fieldH);
        mainCard.add(pnlDate);

        JPanel pnlTime = createInputBox(iconClock);
        pnlTime.setBounds(startX + fieldW + 70, startInputY + verticalGap, fieldW, fieldH);
        mainCard.add(pnlTime);
        cbTime = createStyledComboBox(TIME_LIST, true);
        cbTime.addActionListener(e -> checkSeatAvailability()); 
        pnlTime.add(cbTime);
        
        // --- 3. ส่วนผังที่นั่ง และ ปุ่มกด ---
        int seatStartY = startInputY + verticalGap + fieldH + 15; 
        createSeatLayout(mainCard, seatStartY);
        createActionButtons(mainCard, seatStartY);
    }

    private void createSeatLayout(JPanel parent, int startY) {
        RoundedPanel seatBg = new RoundedPanel(35) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 235, 215)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                g2.dispose();
            }
        };
        seatBg.setLayout(null);
        seatBg.setBounds(60, startY, 580, 330); 
        parent.add(seatBg);
        
        JLabel lDoor = new JLabel(iconDoor); 
        lDoor.setBounds(40, 25, 30, 30); 
        seatBg.add(lDoor);
        
        JLabel lWheel = new JLabel(iconSteering); 
        lWheel.setBounds(245, 25, 35, 35); 
        seatBg.add(lWheel);
        
        JLabel lDriver = new JLabel(iconDriver); 
        lDriver.setBounds(247, 70, 32, 32); 
        seatBg.add(lDriver);

        seatLabels = new JLabel[12];
        
        int startSeatY = 100;
        int gapY = 55; 
        
        for (int i = 0; i < SEAT_NAMES.length; i++) {
            int row = i / 3;
            int col = i % 3;
            
            int xPos = 0;
            if (col == 0) xPos = 135;      
            else if (col == 1) xPos = 335; 
            else xPos = 405;                
            
            int yPos = startSeatY + (row * gapY);

            JLabel btn = new JLabel();
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            btn.setIcon(iconSeatEmpty);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBounds(xPos, yPos, SEAT_SIZE, SEAT_SIZE);
            btn.setToolTipText("ที่นั่ง " + SEAT_NAMES[i]);
            
            int currentRealIndex = i;
            btn.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    toggleSeatSelection(currentRealIndex, btn);
                }
            });
            seatLabels[i] = btn;
            seatBg.add(btn);

            JLabel lblId = new JLabel(SEAT_NAMES[i], SwingConstants.CENTER);
            lblId.setBounds(xPos, yPos + 35, SEAT_SIZE, 20); 
            lblId.setForeground(new Color(255, 140, 50)); 
            lblId.setFont(new Font("Tahoma", Font.BOLD, 13)); 
            seatBg.add(lblId);
        }
    }

    private void createActionButtons(JPanel parent, int startY) {
        int startX = 680;
        int btnStartY = startY + 80; 
        
        RoundedPanel pnlCount = new RoundedPanel(20) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 175, 80)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        pnlCount.setBounds(startX, btnStartY, 220, 55); 
        pnlCount.setLayout(new BorderLayout());
        parent.add(pnlCount);
        
        lblCount = new JLabel("จำนวนที่นั่ง 0", SwingConstants.CENTER);
        try { lblCount.setFont(FontManager.bold(18f)); } catch(Exception e) {}
        lblCount.setForeground(Color.WHITE);
        pnlCount.add(lblCount);

        RoundedPanel pnlSave = new RoundedPanel(20) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 150, 60)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        pnlSave.setBounds(startX, btnStartY + 70, 220, 55); 
        pnlSave.setLayout(new BorderLayout());
        parent.add(pnlSave);
        
        btnConfirm = new JButton("บันทึกการจอง");
        try { btnConfirm.setFont(FontManager.bold(18f)); } catch(Exception e) {}
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setContentAreaFilled(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlSave.add(btnConfirm);

        btnConfirm.addActionListener(e -> onConfirmBooking());
    }
    
    private void checkSeatAvailability() {
        if (seatLabels == null || cbFrom == null || cbTo == null || cbTime == null) return;

        selectedSeatIndexes.clear();
        occupiedSeats.clear();
        lblCount.setText("จำนวนที่นั่ง 0");

        String currentDate = cbDay.getSelectedItem() + " " + cbMonth.getSelectedItem() + " " + cbYear.getSelectedItem();
        String currentTime = (String) cbTime.getSelectedItem();
        String currentFrom = (String) cbFrom.getSelectedItem();
        String currentTo = (String) cbTo.getSelectedItem();

        if (cbFrom.getSelectedIndex() == 0 || cbTo.getSelectedIndex() == 0 || cbTime.getSelectedIndex() == 0) {
            for (JLabel lbl : seatLabels) {
                lbl.setIcon(iconSeatEmpty);
                lbl.setEnabled(true);
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            return;
        }

        List<Booking> allBookings = systemManager.getAllBookings();
        if (allBookings != null) {
            for (Booking b : allBookings) {
                if (b.getDate().equals(currentDate) && 
                    b.getTime().trim().equals(currentTime.trim()) &&
                    b.getSource().equals(currentFrom) &&
                    b.getDestination().equals(currentTo) &&
                    !b.getStatus().equals("ยกเลิก")) {
                    
                    String[] seats = b.getSeatNumbers().split(",");
                    for (String s : seats) {
                        occupiedSeats.add(s.trim());
                    }
                }
            }
        }

        for (int i = 0; i < SEAT_NAMES.length; i++) {
            String seatName = SEAT_NAMES[i];
            JLabel lbl = seatLabels[i];

            if (occupiedSeats.contains(seatName)) {
                lbl.setIcon(iconSeatOccupied); 
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                lbl.setToolTipText("ที่นั่ง " + seatName + " (ไม่ว่าง)");
            } else {
                lbl.setIcon(iconSeatEmpty);
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                lbl.setToolTipText("ที่นั่ง " + seatName + " (ว่าง)");
            }
        }
        repaint();
    }

    private void toggleSeatSelection(int seatIndex, JLabel lbl) {
        if (occupiedSeats.contains(SEAT_NAMES[seatIndex])) return; 

        if (selectedSeatIndexes.contains(seatIndex)) {
            selectedSeatIndexes.remove(Integer.valueOf(seatIndex));
            lbl.setIcon(iconSeatEmpty);
        } else {
            selectedSeatIndexes.add(seatIndex);
            lbl.setIcon(iconSeatSelected);
        }
        lblCount.setText("จำนวนที่นั่ง " + selectedSeatIndexes.size());
    }

    private void onConfirmBooking() {
        if (cbFrom.getSelectedIndex() == 0 || cbTo.getSelectedIndex() == 0 || cbTime.getSelectedIndex() == 0) {
            // ⚠️ Use Custom Dialog
            showCustomDialog("แจ้งเตือน", "กรุณาเลือกข้อมูลการเดินทางให้ครบถ้วน", "warning");
            return;
        }
        if (selectedSeatIndexes.isEmpty()) {
            // ⚠️ Use Custom Dialog
            showCustomDialog("แจ้งเตือน", "กรุณาเลือกที่นั่งอย่างน้อย 1 ที่", "warning");
            return;
        }
        
        String selectedDate = cbDay.getSelectedItem() + " " + cbMonth.getSelectedItem() + " " + cbYear.getSelectedItem();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedSeatIndexes.size(); i++) {
            int seatIdx = selectedSeatIndexes.get(i);
            if (seatIdx >= 0 && seatIdx < SEAT_NAMES.length) {
                sb.append(SEAT_NAMES[seatIdx]);
                if (i < selectedSeatIndexes.size() - 1) sb.append(", ");
            }
        }
        String seatStr = sb.toString();
        
        try {
            Booking newBooking = new Booking(
                selectedDate, (String) cbTime.getSelectedItem(), (String) cbFrom.getSelectedItem(),
                (String) cbTo.getSelectedItem(), selectedSeatIndexes.size(), seatStr
            );
            newBooking.setStatus("รอดำเนินการ"); 
            systemManager.addBooking(newBooking);
            
            // สร้างข้อความ HTML สำหรับ Dialog
            String successMsg = "จองสำเร็จ!<br>" +
                                "วันที่: " + selectedDate + "<br>" +
                                "เวลา: " + ((String)cbTime.getSelectedItem()).trim() + "<br>" +
                                "เส้นทาง: " + cbFrom.getSelectedItem() + " -> " + cbTo.getSelectedItem() + "<br>" +
                                "ที่นั่ง: " + selectedSeatIndexes.size() + " ที่นั่ง (" + seatStr + ")";
                                
            // ⚠️ Use Custom Dialog
            showCustomDialog("สำเร็จ", successMsg, "success");
            
            if (parentFrame != null) parentFrame.changePanel("history");
            
            selectedSeatIndexes.clear();
            lblCount.setText("จำนวนที่นั่ง 0");
            checkSeatAvailability();
            cbFrom.setSelectedIndex(0); cbTo.setSelectedIndex(0); cbTime.setSelectedIndex(0);
        } catch (Exception e) {
             e.printStackTrace();
             showCustomDialog("ข้อผิดพลาด", "Error: " + e.getMessage(), "error");
        }
    }

    // =========================================================================
    // ✨ Custom Dialog: หน้าต่างแจ้งเตือนสวยๆ (เหมือน DriverQueuePanel)
    // =========================================================================
    private void showCustomDialog(String title, String message, String type) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));
        dialog.setSize(420, 260); // ขยายความสูงนิดหน่อยเผื่อข้อความยาว
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // เงา
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 25, 25);
                
                // พื้นหลัง
                g2.setColor(new Color(255, 250, 240)); 
                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 25, 25);
                
                // ขอบ
                g2.setColor(COLOR_ORANGE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth()-10, getHeight()-10, 25, 25);
                g2.dispose();
            }
        };
        mainPanel.setLayout(null);
        dialog.add(mainPanel);

        // ไอคอน
        JLabel lblIcon = new JLabel();
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35)); // ปรับขนาดไอคอนเป็น 35 ตามที่ขอ
        
        if ("warning".equals(type)) {
            lblIcon.setText("⚠️"); 
        } else if ("success".equals(type)) {
            lblIcon.setText("✅");
        } else {
            lblIcon.setText("❌");
        }
        lblIcon.setBounds(170, 20, 80, 60); // จัดกึ่งกลาง
        mainPanel.add(lblIcon);

        // ข้อความ (รองรับ HTML เพื่อขึ้นบรรทัดใหม่)
        JLabel lblMsg = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblMsg.setForeground(COLOR_TEXT_BROWN);
        lblMsg.setBounds(30, 80, 360, 100); // ขยายพื้นที่ข้อความ
        mainPanel.add(lblMsg);

        // ปุ่ม OK
        JButton btnOK = new JButton("ตกลง");
        styleRoundedButton(btnOK);
        btnOK.setBounds(140, 190, 140, 45);
        btnOK.addActionListener(e -> dialog.dispose());
        mainPanel.add(btnOK);

        dialog.setVisible(true);
    }

    private void styleRoundedButton(JButton btn) {
        btn.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (btn.getModel().isPressed()) {
                    g2.setColor(COLOR_ORANGE_DARK);
                } else if (btn.getModel().isRollover()) {
                    g2.setColor(COLOR_ORANGE.brighter());
                } else {
                    g2.setColor(COLOR_ORANGE);
                }
                
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 45, 45); 
                super.paint(g, c);
                g2.dispose();
            }
        });
    }

    private JPanel createInputBox(Icon icon) {
        JPanel p = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20); g2.dispose();
            }
            @Override
            public void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 167, 73)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        p.setOpaque(false);
        JLabel lblIcon = new JLabel(icon); lblIcon.setBorder(new EmptyBorder(0, 12, 0, 0)); p.add(lblIcon, BorderLayout.WEST);
        return p;
    }
    
    private JPanel createDateInputBox(Icon icon, int width, int height) {
        JPanel p = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20); g2.dispose();
            }
            @Override
            public void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 167, 73)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        p.setOpaque(false); 
        p.setPreferredSize(new Dimension(width, height));
        
        JLabel lblIcon = new JLabel(icon); 
        lblIcon.setBorder(new EmptyBorder(0, 12, 0, 0)); 
        p.add(lblIcon, BorderLayout.WEST);

        JPanel dateGroup = new JPanel(new GridBagLayout()); 
        dateGroup.setOpaque(false); 
        dateGroup.setBorder(new EmptyBorder(5, 0, 5, 5)); 
        p.add(dateGroup, BorderLayout.CENTER); 
        
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.fill = GridBagConstraints.BOTH; 

        // 1. วันที่ (Day)
        gbc.gridx = 0; 
        gbc.weightx = 0.6; 
        gbc.insets = new Insets(0, 0, 0, 2); 
        String[] days = new String[31]; for (int i = 0; i < 31; i++) days[i] = String.format("%02d", i + 1);
        cbDay = createStyledComboBox(days, true); 
        cbDay.addActionListener(e -> checkSeatAvailability()); 
        dateGroup.add(cbDay, gbc);

        // 2. เดือน (Month)
        gbc.gridx = 1; 
        gbc.weightx = 1.2; 
        gbc.insets = new Insets(0, 0, 0, 2); 
        cbMonth = createStyledComboBox(THAI_MONTHS, true); 
        cbMonth.addActionListener(e -> checkSeatAvailability()); 
        dateGroup.add(cbMonth, gbc);

        // 3. ปี (Year)
        gbc.gridx = 2; 
        gbc.weightx = 0.8; 
        gbc.insets = new Insets(0, 0, 0, 0); 
        int currentY = LocalDate.now().getYear(); String[] years = { String.valueOf(currentY), String.valueOf(currentY + 1) };
        cbYear = createStyledComboBox(years, true); 
        cbYear.addActionListener(e -> checkSeatAvailability()); 
        dateGroup.add(cbYear, gbc);

        LocalDate today = LocalDate.now(); 
        cbDay.setSelectedIndex(today.getDayOfMonth() - 1); 
        cbMonth.setSelectedIndex(today.getMonthValue() - 1); 
        cbYear.setSelectedItem(String.valueOf(today.getYear()));
        
        return p;
    }

    private JComboBox<String> createStyledComboBox(String[] items, boolean showArrow) {
        JComboBox<String> box = new JComboBox<>(items);
        try { box.setFont(FontManager.regular(15f)); } catch(Exception e) {}
        box.setForeground(new Color(100, 100, 100)); box.setOpaque(false); box.setBackground(new Color(0,0,0,0)); box.setFocusable(false); box.setBorder(null);
        
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(5, 5, 5, 5)); 
                lbl.setFocusable(false); 
                if (isSelected) { lbl.setBackground(new Color(255, 220, 180)); lbl.setForeground(new Color(100, 50, 0)); } 
                else { lbl.setBackground(Color.WHITE); lbl.setForeground(new Color(100, 100, 100)); }
                lbl.setOpaque(true); return lbl;
            }
        });
        
        box.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                if (showArrow) { JButton b = new JButton(); if (iconDownArrow != null) b.setIcon(iconDownArrow); else b.setText("v"); b.setFocusPainted(false); b.setContentAreaFilled(false); b.setBorderPainted(false); return b; } else { return null; }
            }
            @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {}
            @Override protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override protected JScrollPane createScroller() {
                        JScrollPane scroller = super.createScroller(); scroller.setVerticalScrollBar(new JScrollBar(JScrollBar.VERTICAL)); return scroller;
                    }
                }; popup.setBorder(BorderFactory.createLineBorder(new Color(255, 167, 73), 1)); JList list = popup.getList(); list.setOpaque(true); list.setBackground(Color.WHITE); return popup;
            }
        });
        return box;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) return new ImageIcon();
            Image img = javax.imageio.ImageIO.read(url);
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return new ImageIcon(); }
    }
}