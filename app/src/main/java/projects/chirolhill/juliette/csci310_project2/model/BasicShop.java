package projects.chirolhill.juliette.csci310_project2.model;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

public class BasicShop {
    protected String id;
    protected String name;
    protected double rating;
    protected String priceRange;
    protected LatLng location;
    protected String imgURL;
    protected String address;

    public BasicShop(String id, double latitude, double longitude) {
        this.id = id;
        this.location = new LatLng(latitude, longitude);
        this.name = null;
    }

    // copy constructor
    public BasicShop(BasicShop bs) {
        this.id = bs.id;
        this.name = bs.name;
        this.rating = bs.rating;
        this.priceRange = bs.priceRange;
        this.location = bs.location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
