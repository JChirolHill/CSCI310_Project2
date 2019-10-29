package projects.chirolhill.juliette.csci310_project2.model;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class BasicShop implements Serializable {
    protected String id;
    protected String name;
    protected double rating;
    protected String priceRange;
    protected LatLng location;
    protected String imgURL;
    protected String address;

    public static final String PREF_BASIC_SHOP_ID = "pref_basic_shop_id";
    public static final String PREF_BASIC_SHOP_NAME = "pref_basic_shop_name";
    public static final String PREF_BASIC_SHOP_RATING = "pref_basic_shop_rating";
    public static final String PREF_BASIC_SHOP_PRICE = "pref_basic_shop_price";
    public static final String PREF_BASIC_SHOP_ADDRESS = "pref_basic_shop_address";
    public static final String PREF_BASIC_SHOP_LATITUDE = "pref_basic_shop_latitude";
    public static final String PREF_BASIC_SHOP_LONGITUDE = "pref_basic_shop_longitude";
    public static final String PREF_BASIC_SHOP_IMAGE = "pref_basic_shop_image";

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
        this.imgURL = bs.imgURL;
        this.address = bs.address;
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
