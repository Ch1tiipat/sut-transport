package model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String username;
    protected String password;
    protected String name;
    protected String email;
    protected String type;

    // 🚩 [มาตรฐาน] ต้องเรียง: (ชื่อ, อีเมล, รหัสผ่าน)
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.username = name; // ให้ username เป็นชื่อไปก่อน
    }

    public User() {}

    public boolean login(String inputId, String inputPassword) {
        return false; 
    }

    // --- Getters / Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}