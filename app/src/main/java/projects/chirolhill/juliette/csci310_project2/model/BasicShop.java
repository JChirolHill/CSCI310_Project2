package projects.chirolhill.juliette.csci310_project2.model;

import android.util.Pair;

public class BasicShop {
    protected String id;
    protected String name;
    protected float rating;
    protected Pair<Double, Double> location;

    public BasicShop(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public Pair<Double, Double> getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setLocation(Pair<Double, Double> location) {
        this.location = location;
    }
}
