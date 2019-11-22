package projects.chirolhill.juliette.csci310_project2.model.dbadapters;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import projects.chirolhill.juliette.csci310_project2.model.Trip;

public class DatabaseTrip implements DatabaseAdapter {
    private final String TAG = DatabaseTrip.class.getSimpleName();

    public String id;
    public String destination;
    public String timeDiscover; // total time as a string format: HH:MM
    public String timeArrived; // total time as a string format: HH:MM

    public DatabaseTrip() { }

    public DatabaseTrip(Trip t) {
        this.id = t.getId();
        this.destination = t.getDestination();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        this.timeDiscover = simpleDateFormat.format(t.getTimeDiscover());
        this.timeArrived = simpleDateFormat.format(t.getTimeArrived());
    }

    @Override
    public Object revertToOriginal() {
        Trip t = null;
        try {
            Date timeDiscDate = new SimpleDateFormat("HH:mm").parse(timeDiscover);
            Date timeArrDate = new SimpleDateFormat("HH:mm").parse(timeArrived);
            t = new Trip(destination);
            t.setTimeDiscover(timeDiscDate);
            t.setTimeArrived(timeArrDate);
            t.setId(id);
        } catch(ParseException pe) {
            Log.d(TAG, pe.getMessage());
            pe.printStackTrace();
        }

        return t;
    }
}
