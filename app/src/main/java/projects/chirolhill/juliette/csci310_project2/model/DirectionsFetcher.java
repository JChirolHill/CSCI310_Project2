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
    private DirectionsResponse response;

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
     */
    public void fetch(double originLat, double originLng, double destLat, double destLng) {
        //TODO support parameters for mode (either driving or walking) and multiple routes

        String requestURL = BASE_URL + "origin=" + Double.toString(originLat) + "," + Double.toString(originLng)
                + "&destination=" + Double.toString(destLat) + "," + Double.toString(destLng) + "&key=" + API_KEY;
        Log.i(TAG, "REQUEST URL = " + requestURL);
        requestJSONParse(requestURL);
    }

    /**
     * Sends the HTTP GET request
     * @param reqURL: the prepared Google Directions API URL (see https://developers.google.com/maps/documentation/directions/intro#Introduction_
     */
    public void requestJSONParse(String reqURL) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, reqURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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

    /**
     * Parses the JSON response from the Directions API into Java objects.
     * @param response
     */
    public void parse(JSONObject response) {
        Log.i(TAG, "parse");
        Log.i(TAG, response.toString());

        // TODO create a DirectionsResponse object that has the polylines, etc, extracted out

    }

    public DirectionsResponse getResponse() { return this.response; }
}
