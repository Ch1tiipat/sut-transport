package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Passenger extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentID;
    private List<Booking> myBookings;

    // Constructor: (ชื่อ, อีเมล, รหัส, รหัสนักศึกษา)
    public Passenger(String name, String email, String password, String studentID) {
        // 🚩 ส่งตาม User: (Name, Email, Password)
        super(name, email, password);
        
        this.studentID = studentID;
        this.myBookings = new ArrayList<>();
    }

    @Override
    public boolean login(String inputID, String password) {
        return this.studentID.equals(inputID) && getPassword().equals(password);
    }
    
    // --- Booking Logic ---
    public Booking bookSeat(Schedule schedule, String seatID) {
        if (!schedule.getVehicle().isSeatAvailable(seatID)) return null;
        schedule.getVehicle().reserveSeat(seatID);
        String bookingID = "BK-" + System.currentTimeMillis();
        Stop start = (!schedule.getRoute().getStops().isEmpty()) ? schedule.getRoute().getStops().get(0) : null;
        Stop end = (!schedule.getRoute().getStops().isEmpty()) ? schedule.getRoute().getStops().get(schedule.getRoute().getStops().size() - 1) : null;
        Booking newBooking = new Booking(bookingID, this, schedule, seatID, start, end);
        this.myBookings.add(newBooking);
        return newBooking;
    }
    public List<Booking> viewHistory() { return myBookings; }
    public void cancelMyBooking(Booking booking) {
        if(booking != null) {
            booking.getSchedule().getVehicle().freeSeat(booking.getSeatNumber());
            booking.cancelTicket();
        }
    }
    
    // Getters / Setters
    public String getStudentID() { return studentID; }
    public void setId(String newId) { this.studentID = newId; }
    public String getId() { return this.studentID; }
    public void logout() {}
}