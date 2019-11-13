package projects.chirolhill.juliette.csci310_project2.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class DirectionsResponse {
    private String mode; // only supports "driving" or "walking"
    private ArrayList<DirectionsRoute> routes;

    public DirectionsResponse(String mode) {
        this.mode = mode;
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

    public ArrayList<DirectionsRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<DirectionsRoute> routes) {
        this.routes = routes;
    }
}
