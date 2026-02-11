package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private String scheduleID;
    private LocalDateTime departureTime; 
    private String status; 

    private Route route;
    private Vehicle vehicle;
    private Driver driver;
    private List<Booking> bookingsOnThisSchedule;

    public Schedule(String scheduleID, Route route, Vehicle vehicle, LocalDateTime departureTime) {
        this.scheduleID = scheduleID;
        this.route = route;
        this.vehicle = vehicle;
        this.departureTime = departureTime;
        this.status = "ยังไม่เริ่ม";
        this.bookingsOnThisSchedule = new ArrayList<>();
    }

    public int getAvailableSeatCount() {
        if (vehicle == null) return 0;
        return vehicle.getCapacity() - bookingsOnThisSchedule.size();
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }
    
    public void addBooking(Booking booking) {
        this.bookingsOnThisSchedule.add(booking);
    }
    
    // Getters
    public LocalDateTime getDepartureTime() { return departureTime; }
    public String getStatus() { return status; }
    public String getScheduleID() { return scheduleID; }
    public Route getRoute() { return route; }
    public Vehicle getVehicle() { return vehicle; }
    
    public void assignDriver(Driver driver) {
        this.driver = driver;
    }

    // *** เพิ่มเมธอดนี้เพื่อให้ DriverQueuePanel เรียกใช้ได้ ***
    public List<Booking> getBookings() {
        return bookingsOnThisSchedule;
    }
}