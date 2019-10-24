package projects.chirolhill.juliette.csci310_project2.model;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

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

public class YelpFetcher {
    private final String TAG = YelpFetcher.class.getSimpleName();

    protected RequestQueue queue;
    private List<BasicShop> shops;
    private final String BASE_URL = "https://api.yelp.com/v3/businesses/search?term=coffee+tea&open_now=true";
    private final String YELP_API_KEY = "Nyt3b2FngNfZvaimvE5glNIOO9aVLdLNTNKQ2jVlC9dReEY9JVKcYcRm8i5EOT2IHP7GDH8jljJ0Mki8fCeBlSFcQhu8st2pJzFjcT9E5qF44nvoi1yI-KAg_jyZXXYx";
    private MapsActivity map;

    public YelpFetcher(Context context, MapsActivity map) {
        queue = Volley.newRequestQueue(context);
        this.map = map;
        this.shops = new ArrayList<>();
    }

    public List<BasicShop> getShops() {
        return shops;
    }

    public void fetch(double latitude, double longitude) {
        requestJSONParse(BASE_URL + "&latitude=" + latitude + "&longitude=" + longitude);
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
                        Log.d(TAG, "ERROR FROM YELP: " + error.getMessage() + error.getCause());
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            Log.d(TAG, "Error Response: " + jsonError);
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String mApiKey = "Bearer " + YELP_API_KEY;
                headers.put("Authorization", mApiKey);
                return headers;
            }
        };

        // add the json request to the queue to make it download
        queue.add(req);
    }

    public void parse(JSONObject response) {
        try {
            JSONArray jsonshops = response.getJSONArray("businesses");
            for(int i=0; i<jsonshops.length(); i++) {
                JSONObject coord = jsonshops.getJSONObject(i).getJSONObject("coordinates");
                BasicShop bs = null;
                try {
                    Log.d(TAG, jsonshops.getJSONObject(i).toString());

                    bs = new BasicShop("0", coord.getDouble("latitude"), coord.getDouble("longitude"));
                    bs.setName(jsonshops.getJSONObject(i).getString("name"));
                    bs.setRating(jsonshops.getJSONObject(i).getDouble("rating"));
                    bs.setPriceRange(jsonshops.getJSONObject(i).getString("price"));
                    bs.setImgURL(jsonshops.getJSONObject(i).getString("image_url"));
                    String address = jsonshops.getJSONObject(i).getJSONObject("location").getString("address1")
                            + ", " + jsonshops.getJSONObject(i).getJSONObject("location").getString("city");
                    bs.setAddress(address);
                } catch(JSONException e) {
                    Log.d(TAG, e.getMessage());
                }

                // only add if have a name, other params optional
                if(bs != null && bs.getName() != null) {
                    shops.add(bs);
                }
            }
            map.drawUpdatedList();
            shops.clear();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
