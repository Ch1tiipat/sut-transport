package controller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class SystemManager {

    private static final String DB_FILE = "sut_transport_db_final_v10.dat";
    
    private List<Passenger> allPassengers;
    private List<Driver> allDrivers;
    private List<Schedule> allSchedules;
    private List<Booking> allBookings; 

    public SystemManager() {
        File f = new File(DB_FILE);
        System.out.println(">>> SystemManager Started");
        System.out.println(">>> Database File: " + f.getAbsolutePath());
        
        if (!loadData()) {
            initDefaultData();
        }
        
        if (allBookings == null) allBookings = new ArrayList<>();
    }

    // --- Passenger ---
    public Passenger loginAsPassenger(String id, String password) {
        for (Passenger p : allPassengers) {
            if (p.login(id, password)) return p;
        }
        return null;
    }

    public boolean registerPassenger(String name, String email, String password, String studentID) {
        for (Passenger p : allPassengers) {
            if (p.getStudentID().equals(studentID)) return false;
        }
        allPassengers.add(new Passenger(name, email, password, studentID));
        saveData();
        return true;
    }

    // --- Driver ---
    public Driver loginAsDriver(String inputId, String password) {
        for (Driver d : allDrivers) {
            if (d.login(inputId, password)) return d;
        }
        return null;
    }

    public boolean registerDriver(String name, String email, String password, String busId) {
        for (Driver d : allDrivers) {
            if (d.getEmail().equalsIgnoreCase(email)) return false; 
        }
        allDrivers.add(new Driver(name, email, password, busId));
        saveData();
        return true;
    }

    // --- Other Methods ---
    public List<Schedule> getAllSchedules() { return allSchedules; }
    public void notifyBookingUpdated() { saveData(); }
    public void addBooking(Booking booking) {
        if (allBookings == null) allBookings = new ArrayList<>();
        allBookings.add(booking);
        saveData(); 
    }
    public List<Booking> getAllBookings() { return allBookings; }

    // --- Save/Load & Export Excel ---
    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
            out.writeObject(allPassengers);
            out.writeObject(allDrivers);
            out.writeObject(allSchedules);
            out.writeObject(allBookings); 
            System.out.println(">>> Data Saved.");
        } catch (IOException e) { e.printStackTrace(); }
        
        // Export Excel (CSV)
        exportToExcel();
    }

    private void exportToExcel() {
        try {
            PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("Passenger_Data.csv"), StandardCharsets.UTF_8));
            pw1.write('\ufeff'); 
            pw1.println("ลำดับ,ชื่อผู้ใช้,อีเมล,รหัสผ่าน,รหัสนักศึกษา"); 
            for (int i = 0; i < allPassengers.size(); i++) {
                Passenger p = allPassengers.get(i);
                pw1.println((i+1) + "," + cleanText(p.getName()) + "," + cleanText(p.getEmail()) + "," + cleanText(p.getPassword()) + "," + cleanText(p.getStudentID()));
            }
            pw1.close();

            PrintWriter pw2 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("Driver_Data.csv"), StandardCharsets.UTF_8));
            pw2.write('\ufeff');
            pw2.println("ลำดับ,ชื่อผู้ใช้,อีเมล,รหัสผ่าน,ทะเบียนรถ");
            for (int i = 0; i < allDrivers.size(); i++) {
                Driver d = allDrivers.get(i);
                pw2.println((i+1) + "," + cleanText(d.getName()) + "," + cleanText(d.getEmail()) + "," + cleanText(d.getPassword()) + "," + cleanText(d.getBusId()));
            }
            pw2.close();
        } catch (Exception e) {}
    }
    
    private String cleanText(String text) {
        if (text == null) return "-";
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    @SuppressWarnings("unchecked")
    private boolean loadData() {
        File f = new File(DB_FILE);
        if (!f.exists()) return false;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            allPassengers = (List<Passenger>) in.readObject();
            allDrivers = (List<Driver>) in.readObject();
            allSchedules = (List<Schedule>) in.readObject();
            try { allBookings = (List<Booking>) in.readObject(); } catch (Exception e) { allBookings = new ArrayList<>(); }
            return true;
        } catch (Exception e) { return false; }
    }

    private void initDefaultData() {
        System.out.println(">>> Initializing Default Data...");
        this.allPassengers = new ArrayList<>();
        this.allDrivers = new ArrayList<>();
        this.allSchedules = new ArrayList<>();
        this.allBookings = new ArrayList<>();
        
        Driver d1 = new Driver("ลุงสมหมาย", "driver@sut.ac.th", "1234", "BUS-01");
        allDrivers.add(d1);
        
        Passenger p1 = new Passenger("นักศึกษาตัวอย่าง", "student@sut.ac.th", "1234", "B6300001");
        allPassengers.add(p1);

        Route r1 = new Route("R1", "Technopolis -> The Mall");
        r1.addStop(new Stop("S1", "Technopolis"));
        r1.addStop(new Stop("S2", "The Mall"));
        
        Vehicle v1 = new Vehicle("BUS-01", 12); 
        Schedule s1 = new Schedule("SCH-01", r1, v1, LocalDateTime.now().plusHours(1));
        s1.assignDriver(d1);
        s1.updateStatus("รอออกเดินทาง");
        allSchedules.add(s1);
        
        saveData();
    }

    // =========================================================
    // 🔐 ส่วนจัดการลืมรหัสผ่าน (แก้ไขให้รองรับ Email แล้ว)
    // =========================================================

    /**
     * ค้นหา User จาก ID หรือ Email หรือ Username
     */
    public User findUserByInput(String input) {
        // 1. หาในคนขับ (เช็ค email หรือ ชื่อ)
        for (Driver d : allDrivers) {
            if (d.getName().equalsIgnoreCase(input) || d.getEmail().equalsIgnoreCase(input)) {
                return d;
            }
        }
        // 2. หาในผู้โดยสาร (เช็ค รหัสนักศึกษา หรือ ชื่อ หรือ **Email**)
        for (Passenger p : allPassengers) {
            // ✅ เพิ่มการเช็ค Email ตรงนี้
            if (p.getStudentID().equals(input) || 
                p.getName().equalsIgnoreCase(input) || 
                (p.getEmail() != null && p.getEmail().equalsIgnoreCase(input))) {
                return p;
            }
        }
        return null;
    }

    /**
     * เปลี่ยนรหัสผ่านใหม่และบันทึก
     */
    public void resetUserPassword(User user, String newPass) {
        if (user != null) {
            user.setPassword(newPass);
            saveData(); // บันทึกลง .dat และ csv ทันที
            System.out.println(">>> Password reset for: " + user.getName());
        }
    }
}