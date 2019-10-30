package projects.chirolhill.juliette.csci310_project2.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * This class represents a given route in a Google Directions API response.
 * NOTE: within com.google.maps.model, a DirectionsRoute class exists (along with lots
 * of other model classes), BUT those are meant to be used with their Java client, which
 * we are NOT using.
 */
public class DirectionsRoute {

    private String mode; // only supports "driving" or "walking"
    private String startAddress;
    private LatLng startLocation;
    private String endAddress;
    private LatLng endLocation;
    private double duration; // in minutes
    private double distance; // in miles
    private ArrayList<DirectionsStep> steps; // human-readable steps (e.g. "turn left at ___")

    /**
     * This class represents a single step of a route;
     * e.g. "in 1.4 miles, Turn left at 1600 Amphitheatre Parkway"
     */
    class DirectionsStep {
        private String step;
        private double duration; // in minutes
        private double distance; // in miles
    }

    public DirectionsRoute(String mode, String startAddress, LatLng startLocation, String endAddress,
                           LatLng endLocation, double duration, double distance, ArrayList<DirectionsStep> steps) {
        this.mode = mode;
        this.startAddress = startAddress;
        this.startLocation = startLocation;
        this.endAddress = endAddress;
        this.endLocation = endLocation;
        this.duration = duration;
        this.distance = distance;
        this.steps = steps;
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public ArrayList<DirectionsStep> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<DirectionsStep> steps) {
        this.steps = steps;
    }
}
