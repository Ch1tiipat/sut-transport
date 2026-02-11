package model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Route implements Serializable {
    private static final long serialVersionUID = 1L;

    private String routeID;
    private String routeName;
    private List<Stop> stops;

    public Route(String routeID, String routeName) {
        this.routeID = routeID;
        this.routeName = routeName;
        this.stops = new ArrayList<>();
    }
    
    public void addStop(Stop stop) {
        this.stops.add(stop);
    }

    public List<Stop> getStops() { return stops; }
    public String getRouteID() { return routeID; }
    public String getRouteName() { return routeName; }
}