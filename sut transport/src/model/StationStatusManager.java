package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationStatusManager {

    private static final List<StationStatus> stations = new ArrayList<>();

    private static final PropertyChangeSupport pcs =
            new PropertyChangeSupport(StationStatusManager.class);

    static {
        stations.add(new StationStatus("เทคโนธานี",       "08:30 - 08:35"));
        stations.add(new StationStatus("The Mall",        "09:00 - 09:10"));
        stations.add(new StationStatus("Terminal 21",     "09:15 - 09:20"));
        stations.add(new StationStatus("บขส. ใหม่",       "09:30 - 09:35"));
        stations.add(new StationStatus("Central",         "10:00 - 10:15"));
        stations.add(new StationStatus("ลานท้าวสุรนารี", "10:50 - 11:00"));
        stations.add(new StationStatus("กลับถึงเทคโนธานี","11:40 - 11:50"));
    }

    // ------------------------------------------------------------------------
    // เมทอดพื้นฐาน
    // ------------------------------------------------------------------------

    /** คืนลิสต์สถานี (อ่านได้อย่างเดียว) */
    public static List<StationStatus> getStations() {
        return Collections.unmodifiableList(stations);
    }

    /** จำนวนสถานีทั้งหมด เผื่อเอาไปวนลูปใน UI */
    public static int getStationCount() {
        return stations.size();
    }

    /** ดึงสถานีตาม index แบบปลอดภัย (ถ้าผิดช่วงจะคืน null) */
    public static StationStatus getStation(int index) {
        if (index < 0 || index >= stations.size()) return null;
        return stations.get(index);
    }

    // ------------------------------------------------------------------------
    // เมทอดเกี่ยวกับการออกจากสถานี / รีเซ็ต
    // ------------------------------------------------------------------------

    /**
     * ใช้ตอนคนขับกด "ออกจากสถานี"  
     * (ชื่อเดิม markDeparted แต่ทำ alias เป็น setDepartedStation
     *  เพื่อให้โค้ดฝั่ง UI เรียกได้)
     */
    public static void setDepartedStation(int index) {
        markDeparted(index);
    }

    /** ของเดิม: markDeparted ยังเก็บไว้ให้เรียกได้เหมือนกัน */
    public static void markDeparted(int index) {
        if (index < 0 || index >= stations.size()) return;

        StationStatus s = stations.get(index);
        if (!s.isDeparted()) {
            s.setDeparted(true);
            // แจ้งทุกหน้าจอที่สมัครฟังว่า status เปลี่ยนแล้ว
            pcs.firePropertyChange("stations", null, null);
        }
    }

    /** เช็คว่าสถานี index นี้ ออกแล้วหรือยัง */
    public static boolean isDeparted(int index) {
        if (index < 0 || index >= stations.size()) return false;
        return stations.get(index).isDeparted();
    }

    /** รีเซ็ตทุกสถานีให้กลับมา "ยังไม่ออก" (ใช้ตอนเริ่มรอบถัดไป) */
    public static void resetAll() {
        for (StationStatus s : stations) {
            s.setDeparted(false);
        }
        pcs.firePropertyChange("stations", null, null);
    }

    // ------------------------------------------------------------------------
    // จัดการ listener ให้หน้าจออื่นตามการเปลี่ยนแปลง
    // ------------------------------------------------------------------------

    public static void addChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public static void removeChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

	public static int getCurrentBusIndex() {
		// TODO Auto-generated method stub
		return 0;
	}
}
