package app;

import javax.swing.*;
import java.awt.*;
import controller.SystemManager;
import view.user.LoginSelectFrame;

public class MainSimulation {

    // ฟังก์ชันตั้งค่าฟอนต์ (แก้สระลอย/ภาษาไทย)
    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public static void main(String[] args) {
        // ตั้งค่า Look and Feel
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) {}

        // ตั้งค่าฟอนต์ไทย
        setUIFont(new javax.swing.plaf.FontUIResource("Tahoma", Font.PLAIN, 14));

        SwingUtilities.invokeLater(() -> {
            // 1. สร้างระบบจัดการข้อมูลกลาง (Database ในหน่วยความจำ)
            SystemManager sharedSystemManager = new SystemManager();

            // 🚩 เปิดหน้า Login หน้าที่ 1 (สำหรับคนขับ - ไว้ซ้ายจอ)
            
            LoginSelectFrame loginWindow1 = new LoginSelectFrame(sharedSystemManager);
            loginWindow1.setTitle("SUT Transport - หน้าต่างที่ 1 (Login คนขับ)");
            loginWindow1.setLocation(100, 100); // ตำแหน่งซ้าย
            loginWindow1.setVisible(true);

            // 🚩 เปิดหน้า Login หน้าที่ 2 (สำหรับผู้ใช้ - ไว้ขวาจอ)
           
            LoginSelectFrame loginWindow2 = new LoginSelectFrame(sharedSystemManager);
            loginWindow2.setTitle("SUT Transport - หน้าต่างที่ 2 (Login ผู้ใช้)");
            loginWindow2.setLocation(900, 100); // ตำแหน่งขวา
            loginWindow2.setVisible(true);
            
           
        });
    }
}