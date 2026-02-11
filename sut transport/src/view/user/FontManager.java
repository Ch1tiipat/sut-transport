package view.user;

import java.awt.Font;
import java.io.File;

public class FontManager {

    private static Font fontInstance;

    static {
        try {
            // โค้ดโหลดฟอนต์เดิม (ถ้ามี)
            // fontInstance = Font.createFont(Font.TRUETYPE_FONT, new File("path/to/font.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- เมธอดตัวหนา (Bold) ---
    public static Font bold(float size) {
        try {
            if (fontInstance != null) {
                return fontInstance.deriveFont(Font.BOLD, size);
            }
            return new Font("Tahoma", Font.BOLD, (int) size);
        } catch (Exception e) {
            return new Font("SansSerif", Font.BOLD, (int) size);
        }
    }

    // --- เมธอดตัวปกติ (Plain) ---
    public static Font plain(float size) {
        try {
            if (fontInstance != null) {
                return fontInstance.deriveFont(Font.PLAIN, size);
            }
            return new Font("Tahoma", Font.PLAIN, (int) size);
        } catch (Exception e) {
            return new Font("SansSerif", Font.PLAIN, (int) size);
        }
    }

    // *** เพิ่มเมธอดนี้เพื่อแก้ Error ***
    // ให้ regular เรียกใช้ plain ได้เลย เพราะมันคือตัวปกติเหมือนกัน
    public static Font regular(float size) {
        return plain(size);
    }
}