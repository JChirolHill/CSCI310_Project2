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
    private String distance; // google api provides string, e.g. "11 mins"
    private String duration; // google api provides string, e.g. "3.4 mi"
    private String encodedPolyline;
    private ArrayList<DirectionsStep> steps; // human-readable steps (e.g. "turn left at ___")

    public DirectionsRoute(String mode, String startAddress, LatLng startLocation, String endAddress,
                           LatLng endLocation, String distance, String duration, String encodedPolyline) {
        this.mode = mode;
        this.startAddress = startAddress;
        this.startLocation = startLocation;
        this.endAddress = endAddress;
        this.endLocation = endLocation;
        this.distance = distance;
        this.duration = duration;
        this.encodedPolyline = encodedPolyline;
        this.steps = new ArrayList<DirectionsStep>();
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEncodedPolyline() {
        return encodedPolyline;
    }

    public void setEncodedPolyline(String encodedPolyline) {
        this.encodedPolyline = encodedPolyline;
    }

    public ArrayList<DirectionsStep> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<DirectionsStep> steps) {
        this.steps = steps;
    }
}
