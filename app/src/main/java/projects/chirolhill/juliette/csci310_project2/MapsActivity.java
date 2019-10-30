package projects.chirolhill.juliette.csci310_project2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.util.HashMap;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.DirectionsFetcher;
import projects.chirolhill.juliette.csci310_project2.model.YelpFetcher;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {
    private final String TAG = MapsActivity.class.getSimpleName();

//    private Button btnFindShops;
    private ImageButton imgProfile;

    private LocationManager locationManager;
    private Location myLocation;
    private GoogleMap mMap;
    private YelpFetcher yelpFetcher;
    private DirectionsFetcher directionsFetcher;
    private LatLng currLatLng;
    private com.google.maps.model.LatLng currLatLngDirectionsAPI;
    private Map<String, BasicShop> shopListing;
    private GeoApiContext mGeoApiContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        shopListing = new HashMap<>();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        yelpFetcher = new YelpFetcher(getApplicationContext(), this);
        directionsFetcher = new DirectionsFetcher(getApplicationContext(), this);

        imgProfile = findViewById(R.id.imgProfile);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra(ProfileActivity.EXTRA_READONLY, true);
                startActivity(i);
            }
        });
//        btnFindShops = findViewById(R.id.btnFindShops);
//
//        btnFindShops.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMap.clear();
//                yelpFetcher.fetch(currLatLng.latitude, currLatLng.longitude);
//            }
//        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        // needed to access the Directions API in calculateDirections()
//        if (mGeoApiContext == null) {
//            mGeoApiContext = new GeoApiContext.Builder()
//                    .apiKey(getString(R.string.google_maps_key))
//                    .build();
//        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        else{
            // Write you code here if permission already given.
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            mMap.setOnMarkerClickListener(this);

            // Add a marker in current spot and move the camera
            currLatLng = null;

            myLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

            if (myLocation != null) {
                currLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                currLatLngDirectionsAPI = new com.google.maps.model.LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                Log.d(TAG,"Location Info: Location achieved!");
            } else {
                currLatLng = new LatLng(34.0224, 118.2851);
                currLatLngDirectionsAPI = new com.google.maps.model.LatLng(34.0224, 118.2851);
                Log.d(TAG,"Location Info: No location :(");
            }
            MarkerOptions marker = new MarkerOptions()
                    .position(currLatLng)
                    .title("You are here!")
                    .snippet("Your current location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            // Moving Camera to a Location with animation
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currLatLng).zoom(12).build();

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            mMap.addMarker(marker);

            // get coffeeshop data from volley
            Log.d(TAG, "fetching from yelp first time");
            yelpFetcher.fetch(myLocation.getLatitude(), myLocation.getLongitude());

            // needed for OnInfoWindowClickListener() to work
            mMap.setOnInfoWindowClickListener(this);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false; // (the camera animates to the user's current position).
    }

    @Override
    public boolean onMarkerClick (Marker marker) {
//        currLatLng = marker.getPosition();
        if(marker.getPosition().latitude != currLatLng.latitude
                && marker.getPosition().longitude != currLatLng.longitude) {

            final String selectedShopName = marker.getTitle();
            Snackbar.make(findViewById(R.id.map), marker.getTitle(), Snackbar.LENGTH_LONG)
                    .setAction("View Drinks", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get the clicked shop
                            BasicShop selectedShop = shopListing.get(selectedShopName);

                            // launch intent to view shop details here
                            Intent i = new Intent(getApplicationContext(), ShopInfoActivity.class);
                            i.putExtra(ShopInfoActivity.PREF_READ_ONLY, true);
                            i.putExtra(BasicShop.PREF_BASIC_SHOP_NAME, selectedShop.getName());
                            i.putExtra(BasicShop.PREF_BASIC_SHOP_PRICE, selectedShop.getPriceRange());
                            i.putExtra(BasicShop.PREF_BASIC_SHOP_RATING, selectedShop.getRating());
                            i.putExtra(BasicShop.PREF_BASIC_SHOP_IMAGE, selectedShop.getImgURL());
                            i.putExtra(BasicShop.PREF_BASIC_SHOP_ADDRESS, selectedShop.getAddress());
                            startActivity(i);
                        }
                    }).show();
        }

        return false; // moves camera to the selected marker
    }

    /**
     * Allows a click of a given marker's info window to trigger a directions query.
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        calculateDirections(marker);
    }


    /**
     * Calculates directions from current location to specified marker.
     * Currently only providing one route (the fastest).
     */
    public void calculateDirections(Marker marker) {
//        // DEBUG
//        Log.d(TAG, "calculateDirections: calculating directions.");
//
//        LatLng destination = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
//
//        // NOTE: the best practice to do this involves setting up an intermediate proxy server,
//        //       specifically to prevent people from decompiling your application and taking
//        //       advantage of your API key ... but this is a sandboxed app (for now)
//
//        /* METHOD 1: YouTube Tutorial (https://www.youtube.com/watch?v=f47L1SL5S0o&list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi&index=19)
//        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
//        directions.alternatives(false); // restrict results to one route (the fastest) for now
//        directions.origin(currLatLngDirectionsAPI); // NOTE: had to create an add'l "currLatLng" for now cuz there are dif LatLng types
//        */
//
//        // TODO: try setting up an external server ... "using the Google Maps Web Service client libraries from Google App Engine instances"
//        // thus, requests go from *app* --> server --> directions API
//        //                        *app* <-- server <-- directions API
//        // https://cloud.google.com/blog/products/maps-platform/making-most-of-google-maps-web-service
//
//        // DEBUG
//        Log.d(TAG, "calculateDirections: destination is " + destination.toString());
//
//        // TODO make asynchronous or place in proper try-catch
//        GeocodingResult[] results = GeocodingApi.reverseGeocode(mGeoApiContext, currLatLngDirectionsAPI).awaitIgnoreError();
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        Log.i(TAG, gson.toJson(results[0].addressComponents));
//
//        // TODO initiate polyline drawing on actual map

        directionsFetcher.fetch(currLatLng.latitude, currLatLng.longitude,
                marker.getPosition().latitude, marker.getPosition().longitude);
    }

    /**
     * Uses supplied route data to draw the appropriate polyline.
     * Currently non-clickable, single line.
     */
    public void drawPolyline() {

    }

    public void drawUpdatedList() {
        Log.d(TAG, "called drawUpdatedList");
        // add markers for all coffeeshops
        for(BasicShop bs : yelpFetcher.getShops()) {
            String snippet = "Rating: " + Double.toString(bs.getRating());
            shopListing.put(bs.getName(), bs);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(bs.getLocation().latitude, bs.getLocation().longitude))
                    .title(bs.getName())
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

}
