package projects.chirolhill.juliette.csci310_project2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Trip implements Serializable {
    private String id;
    private String destination;
    private Date timeDiscover;
    private Date timeArrived;

    public Trip() {

    }

    public Trip(String id) {
        this.id = id;
    }

    public Trip(String destination, int minutes) {
        this.destination = destination;

        // use calendar to add an interval to date
        Calendar cal = Calendar.getInstance();
        timeDiscover = cal.getTime();

        // manipulate date
        cal.add(Calendar.HOUR, minutes / 60);
        cal.add(Calendar.MINUTE, minutes % 60);

        // convert calendar to date
        timeArrived = cal.getTime();
    }

    public Trip(String destination, int hours, int minutes) {
        this.destination = destination;
        timeArrived = new Date();
        // use calendar to add an interval to date
        Calendar cal = Calendar.getInstance();
        cal.setTime(timeDiscover);

        // manipulate date
        cal.add(Calendar.HOUR, hours);
        cal.add(Calendar.MINUTE, minutes);

        // convert calendar to date
        timeArrived = cal.getTime();
    }

    public Trip(String destination, Date timeDiscover) {
        this.destination = destination;
        this.timeDiscover = timeDiscover;
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

    public void setDestination(String shopID) {
        this.destination = shopID;
    }

    public Date getTimeDiscover() {
        return timeDiscover;
    }

    public void setTimeDiscover(Date timeDiscover) {
        this.timeDiscover = timeDiscover;
    }

    public Date getTimeArrived() {
        return timeArrived;
    }

    public void setTimeArrived(Date timeArrived) {
        this.timeArrived = timeArrived;
    }

    // returns travel time in minutes
    private int calcTravelTime() {
        // getTime returns in milliseconds
        if (timeArrived != null && timeDiscover != null) {
            return (int)(timeArrived.getTime() - timeDiscover.getTime()) / 60 / 1000;
        } else {
            return 0;
        }
    }

    public int getTravelTime() {
        return calcTravelTime();
    }
}
