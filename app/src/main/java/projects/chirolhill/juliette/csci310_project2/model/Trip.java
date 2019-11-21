package projects.chirolhill.juliette.csci310_project2.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip {
    private String id;
    private String destination;
    private Date timeDiscover;
    private Date timeArrived;
    private List<String> directions;

    public Trip(String id) {
        this.id = id;
        directions = new ArrayList<>();
    }

    public Trip(String destination, Date timeDiscover) {
        this.destination = destination;
        this.timeDiscover = timeDiscover;
        directions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public Date getTimeDiscover() {
        return timeDiscover;
    }

    public Date getTimeArrived() {
        return timeArrived;
    }

    public void setTimeArrived(Date timeArrived) {
        this.timeArrived = timeArrived;
    }

    public List<String> getDirections() {
        return directions;
    }

    public void setDirections(List<String> directions) {
        this.directions = directions;
    }

    // returns travel time in minutes
    private int calcTravelTime() {
        // getTime returns in milliseconds
        return (int)(timeArrived.getTime() - timeDiscover.getTime()) * 60 * 1000;
    }

    public int getTravelTime() {
        return calcTravelTime();
    }
}
