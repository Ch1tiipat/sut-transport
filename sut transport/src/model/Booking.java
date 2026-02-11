package model;

import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String passengerName; 
    private String dateStr, timeStr, sourceStr, destinationStr, seatNumbers;
    private int seatCount;
    private String status;

    private Passenger passenger;
    private Schedule schedule; 
    private Stop startStop, endStop;

    // --- Constructor 1: สำหรับหน้าจอง UI ใหม่ (UserBookingPanel) ---
    public Booking(String passengerName, String source, String dest, String date, String time, List<String> seats, String status) {
        this.passengerName = passengerName;
        this.sourceStr = source;
        this.destinationStr = dest;
        this.dateStr = date;
        this.timeStr = time;
        this.status = status;
        
        if (seats != null) {
            this.seatCount = seats.size();
            this.seatNumbers = String.join(", ", seats);
        } else {
            this.seatCount = 0;
            this.seatNumbers = "-";
        }
    }

    // --- Constructor 2: แบบดั้งเดิม (Legacy Support) ---
    public Booking(String date, String time, String source, String dest, int seatCount, String seatNumbers) {
        this.dateStr = date;
        this.timeStr = time;
        this.sourceStr = source;
        this.destinationStr = dest;
        this.seatCount = seatCount;
        this.seatNumbers = seatNumbers;
        this.status = "สำเร็จ";
    }

    // --- Constructor 3: สำหรับ Logic เชิง Object (Passenger/Schedule) ---
    public Booking(String id, Passenger p, Schedule s, String seat, Stop start, Stop end) {
        this.passenger = p;
        this.schedule = s;
        this.seatNumbers = seat;
        this.startStop = start;
        this.endStop = end;
        this.status = "ตั๋วที่ยังไม่ใช้";
        
        if(s != null) this.timeStr = s.getDepartureTime().toString();
        if(start != null) this.sourceStr = start.getStopName();
        if(end != null) this.destinationStr = end.getStopName();
    }

    // --- Business Methods ---
    public void cancelTicket() {
        this.status = "ยกเลิก";
    }

    public Schedule getSchedule() {
        return schedule; 
    }

    public String getSeatNumber() {
        return seatNumbers;
    }

    // --- Getters & Setters ---
    public String getPassengerName() { return passengerName; }
    public String getDate() { return dateStr; }
    public String getTime() { return timeStr; }
    public String getSource() { return sourceStr; }
    public String getDestination() { return destinationStr; }
    public int getSeatCount() { return seatCount; }
    public String getSeatNumbers() { return seatNumbers; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
}