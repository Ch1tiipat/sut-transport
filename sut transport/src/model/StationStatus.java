package model;

public class StationStatus {

    private final String name;
    private final String timeRange;
    private boolean departed;

    public StationStatus(String name, String timeRange) {
        this.name = name;
        this.timeRange = timeRange;
        this.departed = false;
    }

    public String getName() {
        return name;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public boolean isDeparted() {
        return departed;
    }

    public void setDeparted(boolean departed) {
        this.departed = departed;
    }
}
