package projects.chirolhill.juliette.csci310_project2.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class DirectionsResponse {
    private String mode; // only supports "driving" or "walking"

    // address = human-readable address, location = lat lng object
    private String startAddress;
    private LatLng startLocation;
    private String endAddress;
    private LatLng endLocation;

    private ArrayList<DirectionsRoute> routes;

    public DirectionsResponse(String mode, String startAddress, LatLng startLocation, String endAddress, LatLng endLocation) {
        this.mode = mode;
        this.startAddress = startAddress;
        this.startLocation = startLocation;
        this.endAddress = endAddress;
        this.endLocation = endLocation;
        this.routes = new ArrayList<DirectionsRoute>();
    }

    public boolean addRoute(DirectionsRoute route) {
        if (this.routes == null) return false;
        else {
            this.routes.add(route);
            return true;
        }
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public ArrayList<DirectionsRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<DirectionsRoute> routes) {
        this.routes = routes;
    }
}
