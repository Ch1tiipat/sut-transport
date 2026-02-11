package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String busID;
    private int capacity;
    private Map<String, Boolean> seatMap; // true=ว่าง, false=ไม่ว่าง

    public Vehicle(String busID, int capacity) {
        this.busID = busID;
        this.capacity = capacity;
        this.seatMap = new HashMap<>();
        
        // สร้างที่นั่ง A1-D4
        for (char row = 'A'; row <= 'D'; row++) {
            for (int i = 1; i <= 4; i++) {
                seatMap.put(row + "" + i, true);
            }
        }
    }
    
    public boolean isSeatAvailable(String seatID) {
        return seatMap.getOrDefault(seatID, false);
    }

    public void reserveSeat(String seatID) {
        if (isSeatAvailable(seatID)) {
            seatMap.put(seatID, false); // ถูกจอง
        }
    }

    public void freeSeat(String seatID) {
        seatMap.put(seatID, true); // กลับมาว่าง
    }
    
    public String getBusID() { return busID; }
    public int getCapacity() { return capacity; }
    public Map<String, Boolean> getSeatMap() { return seatMap; }
}