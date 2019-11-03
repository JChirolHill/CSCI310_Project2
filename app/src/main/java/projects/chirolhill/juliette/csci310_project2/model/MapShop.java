package projects.chirolhill.juliette.csci310_project2.model;

import com.google.android.gms.maps.model.LatLng;

public class MapShop extends BasicShop {
    protected LatLng location;

    public MapShop(String id, double latitude, double longitude) {
        super(id);
        this.location = new LatLng(latitude, longitude);
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }
}

