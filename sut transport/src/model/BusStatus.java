package model;

import java.util.ArrayList;
import java.util.List;

public class BusStatus {
    
    public static int currentStationIndex = 0; 
    
    // เก็บเวลาของรอบที่กำลังวิ่ง
    public static String currentRoundTime = "-"; 
    
    // ✨ [เพิ่มใหม่] สถานะรถวิ่งอยู่หรือไม่ (true=กำลังวิ่งไปสถานีหน้า, false=จอดอยู่ที่สถานี)
    public static boolean isEnRoute = false;

    private static List<Runnable> listeners = new ArrayList<>();

    // เปลี่ยนสถานี (ใช้เมื่อถึงสถานีแล้ว)
    public static void arriveAtNextStation() {
        currentStationIndex = (currentStationIndex + 1) % 7; 
        isEnRoute = false; // ถึงแล้ว หยุดวิ่ง
        notifyAllScreens(); 
    }
    
    // เริ่มออกรถ (ใช้เมื่อกดออกจากสถานี)
    public static void departFromStation() {
        isEnRoute = true; // กำลังวิ่ง
        notifyAllScreens();
    }

    public static void notifyAllScreens() {
        for (Runnable listener : listeners) {
            listener.run(); 
        }
    }

    public static void addListener(Runnable listener) {
        listeners.add(listener);
    }
}