package projects.chirolhill.juliette.csci310_project2.model;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class APIFetcher {
    protected String APIKey;
    protected String endpoint;
    protected RequestQueue queue;

    public APIFetcher(Context context) {
        queue = Volley.newRequestQueue(context);
    }



    public String fetch() {
        return "";
    }

    public void parse() {

    }
}
