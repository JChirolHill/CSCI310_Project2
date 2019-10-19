package projects.chirolhill.juliette.csci310_project2.model;

import java.util.Date;

public class Trip {
    private String id;
    private String destination;
    private Date timeDiscover;
    private Date timeArrived;

    public Trip(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDestination() {
        return destination;
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
