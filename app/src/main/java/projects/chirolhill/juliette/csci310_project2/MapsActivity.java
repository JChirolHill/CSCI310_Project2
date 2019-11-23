package projects.chirolhill.juliette.csci310_project2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.DirectionsStep;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.MapShop;
import projects.chirolhill.juliette.csci310_project2.model.Shop;
import projects.chirolhill.juliette.csci310_project2.model.Trip;
import projects.chirolhill.juliette.csci310_project2.model.User;
import projects.chirolhill.juliette.csci310_project2.model.YelpFetcher;
import projects.chirolhill.juliette.csci310_project2.model.DirectionsFetcher;
import projects.chirolhill.juliette.csci310_project2.model.DirectionsResponse;
import projects.chirolhill.juliette.csci310_project2.model.DirectionsRoute;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback
        {
    private final String TAG = MapsActivity.class.getSimpleName();

    //    private Button btnFindShops;
    private ImageButton imgProfile;
    private LocationManager locationManager;
    private Location myLocation;
    private GoogleMap mMap;
    private YelpFetcher yelpFetcher;
    private DirectionsFetcher directionsFetcher;
    private LatLng currLatLng;
    private Map<Marker, BasicShop> shopListing;
    private ArrayList<Polyline> polylines;
    private Marker routeDetailsMarker; // invisible, serves as an anchor for the route details info window
    final Handler handler = new Handler();

    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView shopName;
    private Button btnDrinks;
    private Button btnDrive;
    private Button btnWalk;
    private Button btnStartStopTrip;
    ListView lv;
    List<String> steps_list;
    ArrayAdapter<String> arrayAdapter;

//    private ListView lv;
//    private List<String> steps_list;
//    private ArrayAdapter<String> adapter;
//    private String[] steps_str_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get reference of widgets from XML layout
        lv = (ListView) findViewById(R.id.bottom_sheet_content);

        // Initializing a new String Array
        String[] steps_str_list = new String[] {
                "Cape Gooseberry",
                "Capuli cherry"
        };

        // Create a List from String Array elements
        steps_list = new ArrayList<String>(Arrays.asList(steps_str_list));

        // Create an ArrayAdapter from List
        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, steps_list);

        // DataBind ListView with items from ArrayAdapter
        lv.setAdapter(arrayAdapter);

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

        // PLEASE DON'T DELETE FOR NOW :)
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

        polylines = new ArrayList<Polyline>();

        // bottomSheet stuff
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        shopName = findViewById(R.id.bottom_sheet_shop_name);
        btnStartStopTrip = findViewById(R.id.btn_start_stop_trip);
        // btnStartStopTrip.setOnClickListener(this);
        btnDrinks = findViewById(R.id.btn_drinks);
        // btnDrinks.setOnClickListener(this);
        btnDrive = findViewById(R.id.btn_drive);
        // btnDrive.setOnClickListener(this);
        btnWalk = findViewById(R.id.btn_walk);
        // btnWalk.setOnClickListener(this);

        btnDrinks.setVisibility(View.VISIBLE);
        btnDrive.setVisibility(View.VISIBLE);
        btnWalk.setVisibility(View.VISIBLE);

        btnDrinks.setBackgroundColor(Color.TRANSPARENT);
        btnDrive.setBackgroundColor(Color.TRANSPARENT);
        btnWalk.setBackgroundColor(Color.TRANSPARENT);

//        lv = findViewById(R.id.bottom_sheet_content);
//        steps_str_list = new String[]{"k1", "k2"};
//
//        steps_list = new ArrayList<String>(Arrays.asList(steps_str_list));
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, steps_list);
//        lv.setAdapter(adapter);
    }

    public void recedeDisplay(){
        if(mBottomSheetBehavior.getState() == 3){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else if(mBottomSheetBehavior.getState() == 4){
            for (Polyline p : polylines) p.remove();
            lv.setVisibility(View.GONE);
            btnStartStopTrip.setVisibility(View.GONE);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        mMap.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onBackPressed() {
        if(mBottomSheetBehavior.getState() != 5) {
            recedeDisplay();
        }
        else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getResources().getString(R.string.suresignout));
            alertDialogBuilder.setCancelable(true);

            // want to log out, redirect to signin page
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.logout), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(i);
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
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
        // mMap.getUiSettings().setScrollGesturesEnabled(false);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            // Write code here if permission already given.
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            mMap.setOnMarkerClickListener(this);

            // Add a marker in current spot and move the camera
            currLatLng = null;

            myLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

            if (myLocation != null) {
                currLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                Log.d(TAG, "Location Info: Location achieved!");
            } else {
                currLatLng = new LatLng(34.0224, 118.2851);
                Log.d(TAG, "Location Info: No location :(");
            }

            /*
            MarkerOptions marker = new MarkerOptions()
                    .position(currLatLng)
                    .title("You are here!")
                    .snippet("Your current location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(marker);
             */

            // Moving Camera to a Location with animation
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currLatLng).zoom(12).build();

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            // get coffeeshop data from volley
            yelpFetcher.fetch(currLatLng.latitude, currLatLng.longitude);

            // needed for OnInfoWindowClickListener() to work
            mMap.setOnInfoWindowClickListener(this);

            // to remove leftover polylines between clicks
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    for (Polyline p : polylines) p.remove();
                    // adjust the bottom margin depending on what's displayed down there
                    switch(mBottomSheetBehavior.getState()) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            mMap.setPadding(0, 0, 0, 0);
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            mMap.setPadding(0, 0, 0, mBottomSheetBehavior.getPeekHeight()*2);
                            break;
                        case BottomSheetBehavior.STATE_HALF_EXPANDED:
                            mMap.setPadding(0, 0, 0, mBottomSheetBehavior.getPeekHeight());
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false; // (the camera animates to the user's current position).
    }

    @Override
    public boolean onMarkerClick (final Marker marker) {
        // if the map is "frozen", user is on a trip and markers shouldn't be clickable either
        if (!mMap.getUiSettings().isScrollGesturesEnabled()) return true;

        // remove old polylines
        for (Polyline p : polylines)

//        lv.setText("");
        btnDrive.setBackgroundColor(Color.TRANSPARENT);
        btnWalk.setBackgroundColor(Color.TRANSPARENT);
        // needed to allow inner-class button listeners to access marker
        final Marker passableMarker = marker;

        if(marker.getPosition().latitude != currLatLng.latitude
                && marker.getPosition().longitude != currLatLng.longitude) {

            SharedPreferences prefs = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
            boolean isMerchant = prefs.getBoolean(User.PREF_IS_MERCHANT, true);

            if (isMerchant) { // merchant: show option to claim shop
                Snackbar.make(findViewById(R.id.map), marker.getTitle(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Claim Shop", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // get the clicked shop
                                BasicShop selectedShop = new BasicShop(shopListing.get(marker));

                                // launch intent to claim the shop
                                Intent i = new Intent(getApplicationContext(), ClaimShopActivity.class);
                                i.putExtra(BasicShop.PREF_BASIC_SHOP, selectedShop);
                                startActivity(i);
                            }
                        }).show();
            }
            else { // customer: view drinks, driving directions, walking directions
                btnDrinks.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        BasicShop selectedShop = new BasicShop(shopListing.get(marker));

                        // launch intent to view shop details here
                        Intent i = new Intent(getApplicationContext(), ShopInfoActivity.class);
                        i.putExtra(ShopInfoActivity.PREF_READ_ONLY, true);
                        i.putExtra(Shop.PREF_BASIC_SHOP, selectedShop);
                        startActivity(i);
                    }
                });
                btnDrive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        btnWalk.setBackgroundColor(Color.TRANSPARENT);
                        btnDrive.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                        BasicShop selectedShop = new BasicShop(shopListing.get(marker));
                        // launch intent to view driving directions to shop here
                        calculateDirectionsForTrip(passableMarker, "driving");
                        mMap.setPadding(0, 0, 0, mBottomSheetBehavior.getPeekHeight()*2);
                    }
                });
                btnWalk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        btnWalk.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                        btnDrive.setBackgroundColor(Color.TRANSPARENT);
                        BasicShop selectedShop = new BasicShop(shopListing.get(marker));
                        // launch intent to view driving directions to shop here
                        calculateDirectionsForTrip(passableMarker, "walking");
                        mMap.setPadding(0, 0, 0, mBottomSheetBehavior.getPeekHeight()*2);
                    }
                });

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                shopName.setText(marker.getTitle());
                mMap.setPadding(0, 0, 0, mBottomSheetBehavior.getPeekHeight());
            }
        }

        return false; // moves camera to the selected marker
    }

    /**
     * For now, just removes leftover polylines.
     * @param marker
     **/
    @Override
    public void onInfoWindowClick(Marker marker) {
        // remove old polylines
        for (Polyline p : polylines) p.remove();
    }

    /**
     * Calculates directions from current location to specified marker, and then create button
     * for user to start a trip if desired.
     * Currently only provides one route (the fastest).
     */
    public void calculateDirectionsForTrip(Marker marker, String mode) {
        // DEBUG
        Log.d(TAG, "calculateDirectionsForTrip: calculating directions.");

        final Marker finalMarker = marker;

        btnStartStopTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStartStopTrip.getText().equals("Start Trip")) {
                    handleTrip(finalMarker, true);

                    // switch to a stop button
                    btnStartStopTrip.setText("Stop Trip");
                    btnStartStopTrip.setBackgroundColor((getResources().getColor(R.color.danger)));
                }
                else {
                    handleTrip(finalMarker, false);

                    // switch to a start button
                    btnStartStopTrip.setText("Start Trip");
                    btnStartStopTrip.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });

        // to trigger UI responses from the API data
        directionsFetcher.setCallback(new DirectionsFetcher.Callback() {
            @Override
            public void directionsCallback(Object o) {
                DirectionsResponse response = (DirectionsResponse) o;
                drawPolyline(response);
                displaySteps(response);
            }
        });

        // refresh the user's location
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            Location currLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            currLatLng = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
        }
        // trigger the HTTP GET request
        directionsFetcher.fetch(currLatLng.latitude, currLatLng.longitude,
                marker.getPosition().latitude, marker.getPosition().longitude, mode);
    }

    /**
     * Sets up the UI/backend logic for a trip. Creates the visual popup to display the start button,
     * allows the user to cancel, and records the data for the trip itself.
     * NOTE: if the trip is cancelled, an empty trip object is still returned.
     * @param marker
     * @return
     */
    public Trip handleTrip(final Marker marker, boolean start) {
        final Trip trip = new Trip();

        // a periodic task for checking the user's distance from the shop. runs every 5 seconds
        final Runnable mapChecker = new Runnable() {
            @Override
            public void run() {
            mMap.getUiSettings().setAllGesturesEnabled(false);

            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                Location currLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                // on-screen: zoom in, follow user's movement
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(currLocation.getLatitude(), currLocation.getLongitude()))
                        .zoom(18)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                // compute the user's current location from the shop's
                float[] distances = new float[1];
                Location.distanceBetween(currLocation.getLatitude(), currLocation.getLongitude(),
                        marker.getPosition().latitude, marker.getPosition().longitude, distances);

                Log.d(TAG, "User is " + distances[0] + " meters from destination.");
                // if user is within 25m of shop, conclude trip
                if (distances[0] < 25) {
                    // TODO: dismiss the 'cancel trip' snackbar?
                    trip.setTimeArrived(new Date(System.currentTimeMillis()));

                    // remove polylines
                    for (Polyline p : polylines) p.remove();
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                    // trip concluded --> display shop details
                    BasicShop selectedShop = new BasicShop(shopListing.get(marker));
                    trip.setDestination(selectedShop.getId());
                    Intent i = new Intent(getApplicationContext(), ShopInfoActivity.class);
                    i.putExtra(ShopInfoActivity.PREF_READ_ONLY, true);
                    i.putExtra(Shop.PREF_BASIC_SHOP, selectedShop);
                    startActivity(i);
                    // TODO create some sort of "trip summary" box to go on bottom of page

                    // TODO: figure out how to connect this trip to whatever orders they make?
                    // TODO check if shop is in DB and add if not?
                    trip.setId(Database.getInstance().addTrip(trip));

                    // make map clickable again
                    mMap.getUiSettings().setAllGesturesEnabled(true);

                    handler.removeCallbacks(this);
                    return;
                }
            }
            handler.postDelayed(this, 5000); // check the user's location every 5 seconds
            }
        };

        if (!start) {
            // remove polylines
            for (Polyline p : polylines) p.remove();
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            // make map clickable again
            mMap.getUiSettings().setAllGesturesEnabled(true);
            handler.removeCallbacks(mapChecker);
        } else {
            trip.setTimeDiscover(new Date(System.currentTimeMillis()));
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            handler.post(mapChecker);
        }

        return trip;
    }

    /**
     * Uses supplied route data to draw the appropriate polyline.
     * Currently non-clickable, single line.
     */
    public void drawPolyline(DirectionsResponse response) {
        for (Polyline p : polylines) p.remove();
        ArrayList<DirectionsRoute> routes = response.getRoutes();
        for (int i = 0; i < routes.size(); i++) { // testing on only ONE route/polyline for now
            List<LatLng> latlngs = PolyUtil.decode(routes.get(i).getEncodedPolyline());

            if (response.getMode().equals("driving")) { // driving line
                Polyline polyline = mMap.addPolyline(new PolylineOptions()
                        .color(Color.parseColor("#1E90FF"))
                        .clickable(true)
                        .addAll(latlngs));
                this.polylines.add(polyline);
            } else { // walking line
                Polyline polyline = mMap.addPolyline(new PolylineOptions()
                        .color(Color.parseColor("#1E90FF"))
                        .clickable(true)
                        .addAll(latlngs)
                        .pattern(Arrays.asList((PatternItem) new Dot())));
                this.polylines.add(polyline);
            }

            // info window to display route details
            if (routeDetailsMarker != null) routeDetailsMarker.remove(); // remove old invisible marker
            routeDetailsMarker = mMap.addMarker(new MarkerOptions()
                    .position(latlngs.get(latlngs.size()/2))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blank_pixel))
                    .title(routes.get(i).getDistance())
                    .snippet(routes.get(i).getDuration()));
            routeDetailsMarker.showInfoWindow();
        }
    }

    public void displaySteps(DirectionsResponse response) {
        ArrayList<DirectionsRoute> routes = response.getRoutes();
        DirectionsRoute route = routes.get(0);
        ArrayList<DirectionsStep> steps = route.getSteps();
        String temp = "";
        String cleanStep = "";
        for (int i = 0; i < steps.size(); i++) {

            cleanStep = steps.get(i).getStep().replaceAll("<[^>]*>", "");
            cleanStep = cleanStep.replaceAll("Destination will be", System.getProperty("line.separator") + "Destination will be");
            temp += cleanStep + System.getProperty("line.separator");
//            Log.d(TAG, steps.get(i).getStep());
            steps_list.add(steps.get(i).getStep());
        }
        lv.setVisibility(View.VISIBLE);
        btnStartStopTrip.setVisibility(View.VISIBLE);
        arrayAdapter.notifyDataSetChanged();

//        lv.setText(temp);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void drawUpdatedList() {
        // TODO: add a button that triggers this method, so that the user can move on map and get updated shops
        // add markers for all coffeeshops
        for (MapShop ms : yelpFetcher.getShops()) {
            String snippet = "Rating: " + Double.toString(ms.getRating());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(ms.getLocation().latitude, ms.getLocation().longitude))
                    .title(ms.getName())
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            // store the marker linked with the map shop
            shopListing.put(marker, ms);
        }
    }
}