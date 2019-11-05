package projects.chirolhill.juliette.csci310_project2.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.MapsActivity;
import projects.chirolhill.juliette.csci310_project2.R;

// TODO: put API key(s) elsewhere, along with other "Fetcher(s)"
public class DirectionsFetcher {
    private final String TAG = DirectionsFetcher.class.getSimpleName();

    protected RequestQueue queue;
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private final String API_KEY = "AIzaSyA3pV-0qvHbr3wRFaFu05UPrkQYdtT6QKM"; // server API key, dif than application one
    private MapsActivity map;

    // MUST BE RESET BETWEEN EACH REQUEST
    private DirectionsResponse mostRecentResponse;
    private String mostRecentRequestURL;

    private Callback cb;

    public DirectionsFetcher(Context context, MapsActivity map) {
        queue = Volley.newRequestQueue(context);
        this.map = map;
    }

    /**
     * Prepares the HTTP GET URL.
     * @param originLat: latitude of origin
     * @param originLng: longitude of origin
     * @param destLat: latitude of destination
     * @param destLng: longitude of destination
     * @param mode: the type of travel, "driving" or "walking"
     */
    public void fetch(double originLat, double originLng, double destLat, double destLng, String mode) {
        // CLEAR RESULTS OF PREVIOUS REQUEST
         this.mostRecentResponse = null;
         this.mostRecentRequestURL = null;

        //TODO support parameters for mode (either driving or walking) and multiple routes
        this.mostRecentRequestURL = BASE_URL + "origin=" + Double.toString(originLat) + "," + Double.toString(originLng)
                + "&destination=" + Double.toString(destLat) + "," + Double.toString(destLng)
                + (mode.equals("walking") ? "&mode=walking" : "&mode=driving") + "&key=" + API_KEY;

        Log.i(TAG, "REQUEST URL = " + this.mostRecentRequestURL);
        requestJSONParse(this.mostRecentRequestURL);
    }

    /**
     * Sends the HTTP GET request
     * @param reqURL: the prepared Google Directions API URL (see https://developers.google.com/maps/documentation/directions/intro#Introduction_
     */
    public void requestJSONParse(String reqURL) {
        Log.i(TAG, "entering requestJSONParse");
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, reqURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "triggering onResponse()");
                parse(response);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "ERROR FROM DIRECTIONS API: " + error.getMessage() + error.getCause());
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.d(TAG, "Error Response: " + jsonError);
                }
            }
        });

        // add the json request to the queue to make it download
        queue.add(req);
    }

    public interface Callback {
        void directionsCallback(Object o);
    }
    public void setCallback(Callback cb) { this.cb = cb; }

    /**
     * Parses the JSON response from the Directions API into Java objects.
     * Uses a callback to set up the polylines.
     * @param response
     */
    public void parse(JSONObject response) {
        Log.i(TAG, "DirectionsFetcher.parse() response = " + response.toString());

        String mode = "driving";
        if (this.mostRecentRequestURL != null) {
            if (this.mostRecentRequestURL.contains("&mode=walking")) mode = "walking";
        }

        // create the response object
        this.mostRecentResponse = new DirectionsResponse(mode);

        /**
         * JSON Object model: https://developers.google.com/maps/documentation/directions/intro#DirectionsResponseElements
         * NOTE: I may have parsed inefficiently. It seemed that anytime I wanted to get to a nested
         * JSON Object, I had to declare and cast that nested section as its own JSONObject.
         */
        JSONArray routes;
        try {
            routes = response.getJSONArray("routes");

            // add each route to the response object
            for (int i = 0; i < routes.length(); i++) {
                JSONObject currentRoute = routes.getJSONObject(i);
                JSONArray legs = currentRoute.getJSONArray("legs");

                // NOTE: route only has one leg, since we don't define waypoints
                JSONObject leg = legs.getJSONObject(0);

                JSONObject distanceObj, durationObj, startLocationObj, endLocationObj, polylineObj;
                distanceObj = leg.getJSONObject("distance");
                durationObj = leg.getJSONObject("duration");
                startLocationObj = leg.getJSONObject("start_location");
                endLocationObj = leg.getJSONObject("end_location");
                polylineObj = currentRoute.getJSONObject("overview_polyline");

                // extract the necessary parameters for a route from the leg
                String startAddress = "", endAddress = "", duration = "", distance = "", encodedPolyline = "";
                LatLng startLocation = null, endLocation = null;

                startAddress = leg.getString("start_address");
                endAddress = leg.getString("end_address");
                startLocation = new LatLng(startLocationObj.getDouble("lat"),
                        startLocationObj.getDouble("lng"));
                endLocation = new LatLng(endLocationObj.getDouble("lat"),
                        endLocationObj.getDouble("lng"));
                distance = distanceObj.getString("text");
                duration = durationObj.getString("text");
                encodedPolyline = polylineObj.getString("points");

                // steps aren't critical yet, so make the object as is for now
                DirectionsRoute route = new DirectionsRoute(mode, startAddress, startLocation,
                        endAddress, endLocation, distance, duration, encodedPolyline);

                // extract the sequence of steps within this route
                ArrayList<DirectionsStep> steps = new ArrayList<DirectionsStep>();
                JSONArray stepsJSONArray = leg.getJSONArray("steps");
                for (int j = 0; j < stepsJSONArray.length(); j++) {
                    JSONObject step = stepsJSONArray.getJSONObject(j);
                    String stepText = "", stepDuration = "", stepDistance = "";

                    JSONObject stepDistanceObj, stepDurationObj;
                    stepDistanceObj = step.getJSONObject("distance");
                    stepDurationObj = step.getJSONObject("duration");

                    stepText = step.getString("html_instructions");
                    stepDistance = stepDistanceObj.getString("text");
                    stepDuration = stepDurationObj.getString("text");

                    steps.add(new DirectionsStep(stepText, stepDistance, stepDuration));
                }

                // add steps to route object
                route.setSteps(steps);

                // add newly created route to response object
                this.mostRecentResponse.addRoute(route);

                Log.i(TAG, "this.mostRecentResponse = " + this.mostRecentResponse.toString());

                // draw polylines via callback
                cb.directionsCallback(this.mostRecentResponse);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO: initiate some sort of error logic for when there are no directions
        }
    }

    public DirectionsResponse getResponse() { return this.mostRecentResponse; }

}
