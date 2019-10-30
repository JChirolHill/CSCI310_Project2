package projects.chirolhill.juliette.csci310_project2.model;

import com.google.maps.model.LatLng;

import org.json.JSONObject;

public class DirectionsResponse {

    private JSONObject rawJSON;
    // address = human-readable address, location = lat lng object
    private String startAddress;
    private LatLng startLocation;
    private String endAddress;
    private LatLng endLocation;
    private String polyline

    public DirectionsResponse(JSONObject rawJSON) {
        this.rawJSON = rawJSON;
    }
}
