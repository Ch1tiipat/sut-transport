package model;

import java.io.Serializable;

public class Driver extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String busId = "-";
    private Schedule currentSchedule;

    // Constructor: (ชื่อ, อีเมล, รหัส, ทะเบียนรถ)
    public Driver(String name, String email, String password, String busId) {
        // 🚩 ส่งตาม User: (Name, Email, Password)
        super(name, email, password); 
        
        this.busId = busId;
        this.setType("Driver");
    }

    public Driver(String name, String email, String password) {
        super(name, email, password);
        this.setType("Driver");
    }

    @Override
    public boolean login(String inputId, String inputPassword) {
        boolean isEmailMatch = getEmail().equalsIgnoreCase(inputId);
        boolean isNameMatch = getName().equalsIgnoreCase(inputId);
        boolean isPassMatch = getPassword().equals(inputPassword);
        return (isEmailMatch || isNameMatch) && isPassMatch;
    }

    // Getters / Setters
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    public void setBusID(String busId) { this.busId = busId; } 
    public String getBusID() { return busId; }
    public Schedule getCurrentSchedule() { return currentSchedule; }
    public void setCurrentSchedule(Schedule currentSchedule) { this.currentSchedule = currentSchedule; }
}