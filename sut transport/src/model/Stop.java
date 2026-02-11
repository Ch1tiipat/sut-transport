package model;

import java.io.Serializable;

public class Stop implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String stopID;
    private String stopName;
    
    public Stop(String stopID, String stopName) {
        this.stopID = stopID;
        this.stopName = stopName;
    }

    public String getStopID() { return stopID; }
    public String getStopName() { return stopName; }
}