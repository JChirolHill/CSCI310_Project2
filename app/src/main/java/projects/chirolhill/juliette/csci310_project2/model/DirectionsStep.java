package projects.chirolhill.juliette.csci310_project2.model;

/**
 * This class represents a single step of a route;
 * e.g. "in 1.4 miles, Turn left at 1600 Amphitheatre Parkway"
 */
public class DirectionsStep {
    private String step;
    private String duration; // in minutes
    private String distance; // in miles

    public DirectionsStep(String step, String duration, String distance) {
        this.step = step;
        this.duration = duration;
        this.distance = distance;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
