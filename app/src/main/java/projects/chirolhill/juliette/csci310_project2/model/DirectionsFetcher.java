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

public class DirectionsFetcher {
    private final String TAG = DirectionsFetcher.class.getSimpleName();

    protected RequestQueue queue;
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private final String API_KEY = "AIzaSyA3pV-0qvHbr3wRFaFu05UPrkQYdtT6QKM";
    private MapsActivity map;

    public DirectionsFetcher(Context context, MapsActivity map) {
        queue = Volley.newRequestQueue(context);
        this.map = map;
    }

    public void fetch(double originLat, double originLng, double destLat, double destLng) {
        String requestURL = BASE_URL + "origin=" + Double.toString(originLat) + "," + Double.toString(originLng)
                + "&destination=" + Double.toString(destLat) + "," + Double.toString(destLng) + "&key=" + API_KEY;
        Log.i(TAG, "REQUEST URL = " + requestURL);
        requestJSONParse(requestURL);
    }

    // performs the json request
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
                    Log.d(TAG, "ERROR FROM DIRECTIONS API: " + error.getMessage() + error.getCause());
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

    public void parse(JSONObject response) {
        Log.i(TAG, "parse");
        Log.i(TAG, response.toString());

        // TODO acknowledge these results in map
    }

}
