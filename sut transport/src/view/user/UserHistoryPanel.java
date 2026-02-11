package view.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.DefaultCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// Import จำเป็น
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import model.Passenger;
import controller.SystemManager;
import model.Booking;

public class UserHistoryPanel extends JPanel {

    private Passenger currentUser;
    private SystemManager systemManager;
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JTextField txtSearch;
    private JComboBox<String> cbSort;

    private final Color THEME_BG = new Color(255, 244, 225);
    private final Color THEME_ORANGE = new Color(255, 167, 73);

    private static final Map<String, Integer> THAI_MONTH_MAP = new HashMap<>();
    static {
        THAI_MONTH_MAP.put("มกราคม", 1); THAI_MONTH_MAP.put("กุมภาพันธ์", 2); THAI_MONTH_MAP.put("มีนาคม", 3);
        THAI_MONTH_MAP.put("เมษายน", 4); THAI_MONTH_MAP.put("พฤษภาคม", 5); THAI_MONTH_MAP.put("มิถุนายน", 6);
        THAI_MONTH_MAP.put("กรกฎาคม", 7); THAI_MONTH_MAP.put("สิงหาคม", 8); THAI_MONTH_MAP.put("กันยายน", 9);
        THAI_MONTH_MAP.put("ตุลาคม", 10); THAI_MONTH_MAP.put("พฤศจิกายน", 11); THAI_MONTH_MAP.put("ธันวาคม", 12);
    }

    public UserHistoryPanel(Passenger user, SystemManager manager) {
        this.currentUser = user;
        this.systemManager = manager;

        setLayout(null);
        setBackground(THEME_BG);

        createHeader();
        createControlBar();
        createTable();
        
        loadBookingHistory(); 
    }

    private void createHeader() {
        JLabel lblTitle = new JLabel("ประวัติการจอง");
        try { lblTitle.setFont(FontManager.bold(32f)); } catch (Exception e) { lblTitle.setFont(new Font("Tahoma", Font.BOLD, 32)); }
        lblTitle.setForeground(new Color(120, 70, 30));
        lblTitle.setBounds(50, 30, 300, 45);
        add(lblTitle);
    }

    private void createControlBar() {
        int startX = 530; 

        // 🚩 [1. Sort Panel]
        // ไม่ต้องใช้ RoundedPanel รองหลังแล้ว เพราะเราจะวาด ComboBox ให้สวยเอง
        String[] sortOptions = {"วันที่ (ใหม่ -> เก่า)", "วันที่ (เก่า -> ใหม่)", "สถานะ"};
        cbSort = new JComboBox<>(sortOptions);
        cbSort.setBounds(startX, 35, 180, 40);
        cbSort.setFont(new Font("Tahoma", Font.BOLD, 14));
        cbSort.setForeground(new Color(80, 80, 80));
        cbSort.setFocusable(false);
        
        // 🚩 Apply Custom UI ให้ ComboBox สวยขึ้น
        cbSort.setUI(new ModernComboBoxUI());
        
        // Renderer สำหรับรายการใน Dropdown
        cbSort.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBorder(new EmptyBorder(8, 10, 8, 10)); // เพิ่มระยะห่างให้อ่านง่าย
                if (isSelected) {
                    c.setBackground(new Color(255, 235, 215)); // สีส้มอ่อนตอนเลือก
                    c.setForeground(new Color(200, 100, 0));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.DARK_GRAY);
                }
                return c;
            }
        });

        cbSort.addActionListener(e -> loadBookingHistory());
        add(cbSort);

        // [2. Search Panel]
        JPanel searchPanel = new RoundedPanel(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        searchPanel.setBounds(startX + 190, 35, 230, 40);
        searchPanel.setLayout(null);
        add(searchPanel);

        txtSearch = new JTextField();
        txtSearch.setBounds(15, 5, 170, 30);
        txtSearch.setBorder(null);
        txtSearch.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtSearch.setOpaque(false);
        
        TextPrompt placeholder = new TextPrompt("ค้นหา...", txtSearch);
        placeholder.changeAlpha(0.5f);
        
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadBookingHistory();
            }
        });
        searchPanel.add(txtSearch);

        JLabel lblIcon = new JLabel("🔍");
        lblIcon.setBounds(195, 5, 30, 30);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        searchPanel.add(lblIcon);
    }

    // 🚩 Custom UI Class สำหรับ ComboBox สวยๆ
    private class ModernComboBoxUI extends BasicComboBoxUI {
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            comboBox.setBorder(new EmptyBorder(0, 0, 0, 0)); // ลบขอบเดิม
            comboBox.setOpaque(false);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // วาดพื้นหลังโค้งมน
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
            
            // วาดเส้นขอบบางๆ (ถ้าต้องการ)
            // g2.setColor(new Color(220, 220, 220));
            // g2.drawRoundRect(0, 0, c.getWidth()-1, c.getHeight()-1, 20, 20);
            
            g2.dispose();
            
            // ให้วาดตัวหนังสือทับลงไป
            super.paint(g, c);
        }

        @Override 
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // ไม่ต้องวาด Background ซ้ำ (เพราะเราวาดเองใน paint แล้ว)
        }

        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton();
            btn.setContentAreaFilled(false);
            btn.setBorder(new EmptyBorder(0, 0, 0, 0));
            btn.setFocusPainted(false);
            btn.setIcon(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(THEME_ORANGE);
                    // วาดสามเหลี่ยมเล็กๆ
                    int size = 8;
                    int shiftX = 5;
                    int shiftY = 8;
                    g2.fillPolygon(new int[]{x+shiftX, x+shiftX+size, x+shiftX+size/2}, new int[]{y+shiftY, y+shiftY, y+shiftY+size}, 3);
                    g2.dispose();
                }
                @Override
                public int getIconWidth() { return 20; }
                @Override
                public int getIconHeight() { return 20; }
            });
            return btn;
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    JScrollPane scroller = super.createScroller();
                    scroller.setVerticalScrollBar(new JScrollBar(JScrollBar.VERTICAL) {
                        @Override
                        public Dimension getPreferredSize() {
                            return new Dimension(8, super.getPreferredSize().height); // Scrollbar บางลง
                        }
                    });
                    return scroller;
                }
            };
            popup.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
            return popup;
        }
    }

    private void createTable() {
        // ... (โค้ดตารางคงเดิม ไม่เปลี่ยนแปลง) ...
        RoundedPanel tableCard = new RoundedPanel(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        tableCard.setLayout(new BorderLayout());
        tableCard.setBounds(50, 100, 890, 480);
        add(tableCard);

        String[] columnNames = {"วันที่จอง", "เวลาที่จอง", "จุดเริ่ม", "ปลายทาง", "ที่นั่ง", "ลำดับ", "สถานะ", "จัดการ"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 7; }
            @Override
            public Class<?> getColumnClass(int columnIndex) { return columnIndex == 7 ? Object.class : String.class; }
        };

        table = new JTable(tableModel);
        table.setSelectionBackground(Color.WHITE); 
        table.setSelectionForeground(Color.BLACK);
        table.setFocusable(false);
        table.setRowHeight(50);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        try { table.setFont(FontManager.regular(14f)); } catch (Exception e) {}

        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(255, 167, 73));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        try { headerRenderer.setFont(FontManager.bold(16f)); } catch (Exception e) {}
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, false, false, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(null);
                c.setForeground(Color.BLACK);
                c.setBackground(Color.WHITE);
                return c;
            }
        };
        for (int i = 0; i < 7; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);
    }

    public void loadBookingHistory() {
        String keyword = txtSearch.getText().trim();
        int sortIndex = cbSort.getSelectedIndex(); 

        tableModel.setRowCount(0);
        List<Booking> originalList = systemManager.getAllBookings();
        
        if (originalList != null) {
            List<Booking> displayList = new ArrayList<>();

            for (Booking b : originalList) {
                if (keyword != null && !keyword.isEmpty()) {
                    String k = keyword.toLowerCase();
                    boolean match = b.getSource().toLowerCase().contains(k) ||
                                    b.getDestination().toLowerCase().contains(k) ||
                                    b.getDate().toLowerCase().contains(k) ||
                                    b.getTime().contains(k) ||
                                    b.getStatus().toLowerCase().contains(k);
                    if (!match) continue; 
                }
                displayList.add(b);
            }

            Collections.sort(displayList, new Comparator<Booking>() {
                @Override
                public int compare(Booking b1, Booking b2) {
                    long dateVal1 = parseDateValue(b1.getDate());
                    long dateVal2 = parseDateValue(b2.getDate());
                    int dateCompare = Long.compare(dateVal1, dateVal2);
                    
                    if (dateCompare == 0) {
                        dateCompare = b1.getTime().compareTo(b2.getTime());
                    }

                    if (sortIndex == 0) return -1 * dateCompare; 
                    else if (sortIndex == 1) return dateCompare; 
                    else return b1.getStatus().compareTo(b2.getStatus());
                }
            });

            for (Booking b : displayList) {
                String btnText;
                if ("ยกเลิก".equals(b.getStatus())) btnText = "ยกเลิกแล้ว";
                else if ("สำเร็จ".equals(b.getStatus())) btnText = "ยืนยันแล้ว"; 
                else btnText = "ยกเลิก"; 
                
                tableModel.addRow(new Object[]{
                    b.getDate(), b.getTime(), b.getSource(), b.getDestination(),
                    b.getSeatCount(), b.getSeatNumbers(), b.getStatus(), btnText
                });
            }
        }
    }

    private long parseDateValue(String dateStr) {
        try {
            String[] parts = dateStr.trim().split("\\s+");
            if (parts.length >= 3) {
                int day = Integer.parseInt(parts[0]);
                String monthStr = parts[1];
                int year = Integer.parseInt(parts[2]);
                int month = THAI_MONTH_MAP.getOrDefault(monthStr, 0);
                return (year * 10000L) + (month * 100L) + day;
            }
        } catch (Exception e) {}
        return 0;
    }
    
    // ... (Helper Classes คงเดิม) ...
    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, false, false, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(null); label.setBackground(Color.WHITE);
            String status = (String) value;
            try { label.setFont(FontManager.bold(14f)); } catch (Exception e) {}
            if ("ยกเลิก".equals(status)) label.setForeground(new Color(220, 50, 50));
            else if ("สำเร็จ".equals(status)) label.setForeground(new Color(40, 160, 60));
            else if ("รอดำเนินการ".equals(status)) label.setForeground(new Color(255, 140, 0)); 
            else label.setForeground(Color.GRAY);
            return label;
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true); setBorderPainted(false); setFocusPainted(false); setFont(new Font("Tahoma", Font.BOLD, 12));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String text = (value == null) ? "" : value.toString();
            setText(text);
            if ("ยกเลิก".equals(text)) { 
                setBackground(new Color(220, 50, 50)); setForeground(Color.WHITE); setEnabled(true); 
            } else if ("ยืนยันแล้ว".equals(text) || "สำเร็จ".equals(text)) {
                setBackground(new Color(200, 230, 200)); setForeground(new Color(40, 100, 40)); setEnabled(false); 
            } else if ("ยกเลิกแล้ว".equals(text)) {
                setBackground(new Color(255, 200, 200)); setForeground(new Color(200, 50, 50)); setEnabled(false); 
            } else { 
                setBackground(Color.WHITE); setForeground(Color.GRAY); setEnabled(false); 
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true); button.setBorderPainted(false); button.setFocusPainted(false);
            button.addActionListener(e -> fireEditingStopped());
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label); currentRow = row; isPushed = true;
            if ("ยกเลิก".equals(label)) { 
                button.setBackground(new Color(220, 50, 50)); button.setForeground(Color.WHITE); 
            } else if ("ยกเลิกแล้ว".equals(label)) {
                button.setBackground(new Color(255, 200, 200)); button.setForeground(new Color(200, 50, 50));   
            } else { 
                button.setBackground(Color.WHITE); button.setForeground(Color.GRAY); 
            }
            return button;
        }
        @Override
        public Object getCellEditorValue() {
            if (isPushed && "ยกเลิก".equals(label)) {
                int confirm = JOptionPane.showConfirmDialog(button, "ยืนยันการยกเลิกการจอง?", "ยืนยัน", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    List<Booking> allBookings = systemManager.getAllBookings();
                    String date = (String) table.getValueAt(currentRow, 0);
                    String time = (String) table.getValueAt(currentRow, 1);
                    String seats = (String) table.getValueAt(currentRow, 5);
                    for (Booking b : allBookings) {
                        if (b.getDate().equals(date) && b.getTime().equals(time) && 
                            b.getSeatNumbers().equals(seats) && "รอดำเนินการ".equals(b.getStatus())) {
                            b.setStatus("ยกเลิก");
                            systemManager.saveData();
                            break;
                        }
                    }
                    loadBookingHistory(); 
                    JOptionPane.showMessageDialog(button, "ยกเลิกเรียบร้อย");
                    return "ยกเลิกแล้ว";
                }
            }
            isPushed = false;
            return label;
        }
        @Override
        public boolean stopCellEditing() { isPushed = false; return super.stopCellEditing(); }
    }

    @SuppressWarnings("serial")
    class TextPrompt extends JLabel implements FocusListener, DocumentListener {
        JTextComponent component;
        Document document;
        public TextPrompt(String text, JTextComponent component) {
            this.component = component;
            document = component.getDocument();
            setText(text); setFont(component.getFont()); setForeground(Color.GRAY);
            setBorder(new EmptyBorder(component.getInsets()));
            component.addFocusListener(this); document.addDocumentListener(this);
            component.setLayout(new BorderLayout()); component.add(this);
            checkForPrompt();
        }
        public void changeAlpha(float alpha) { changeAlpha((int)(alpha * 255)); }
        public void changeAlpha(int alpha) {
            alpha = alpha > 255 ? 255 : alpha < 0 ? 0 : alpha;
            setForeground(new Color(getForeground().getRed(), getForeground().getGreen(), getForeground().getBlue(), alpha));
        }
        public void changeStyle(int style) { setFont(getFont().deriveFont(style)); }
        public void checkForPrompt() { if (document.getLength() > 0) setVisible(false); else setVisible(true); }
        public void focusGained(FocusEvent e) { checkForPrompt(); }
        public void focusLost(FocusEvent e) { checkForPrompt(); }
        public void insertUpdate(DocumentEvent e) { checkForPrompt(); }
        public void removeUpdate(DocumentEvent e) { checkForPrompt(); }
        public void changedUpdate(DocumentEvent e) { checkForPrompt(); }
    }
}