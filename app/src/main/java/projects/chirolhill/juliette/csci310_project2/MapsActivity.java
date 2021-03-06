package projects.chirolhill.juliette.csci310_project2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageButton;
import android.os.Handler;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.DirectionsStep;
import projects.chirolhill.juliette.csci310_project2.model.MapShop;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.Shop;
import projects.chirolhill.juliette.csci310_project2.model.Trip;
import projects.chirolhill.juliette.csci310_project2.model.User;
import projects.chirolhill.juliette.csci310_project2.model.YelpFetcher;
import projects.chirolhill.juliette.csci310_project2.model.DirectionsFetcher;
import projects.chirolhill.juliette.csci310_project2.model.DirectionsResponse;
import projects.chirolhill.juliette.csci310_project2.model.DirectionsRoute;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener,
        OnMapReadyCallback
        {
    private final String TAG = MapsActivity.class.getSimpleName();

    private Button btnFindShops;
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
    private TextView bottomSheetContent;
    private Button btnStartStopTrip;

    private LatLng centerPoint;

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

        // refresh button for nearby shops, initially disabled
        btnFindShops = findViewById(R.id.btnFindShops);
        btnFindShops.setVisibility(View.INVISIBLE);
        btnFindShops.setEnabled(false);

        btnFindShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                centerPoint = mMap.getCameraPosition().target;
                yelpFetcher.fetch(centerPoint.latitude, centerPoint.longitude);
                btnFindShops.setVisibility(View.INVISIBLE);
                btnFindShops.setEnabled(false);
            }
        });

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
        btnDrinks = findViewById(R.id.btn_drinks);
        btnDrive = findViewById(R.id.btn_drive);
        btnWalk = findViewById(R.id.btn_walk);

        btnDrinks.setVisibility(View.VISIBLE);
        btnDrive.setVisibility(View.VISIBLE);
        btnWalk.setVisibility(View.VISIBLE);

        btnDrinks.setBackgroundColor(Color.TRANSPARENT);
        btnDrive.setBackgroundColor(Color.TRANSPARENT);
        btnWalk.setBackgroundColor(Color.TRANSPARENT);

        bottomSheetContent = findViewById(R.id.bottom_sheet_content);

    }

    public void recedeDisplay(){
        if(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mMap.setPadding(0, 0, 0, mBottomSheetBehavior.getPeekHeight());
        }
        else if(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            if(btnStartStopTrip.getText()!="Stop Trip") {
                for (Polyline p : polylines) p.remove();
                bottomSheetContent.setVisibility(View.GONE);
                btnStartStopTrip.setVisibility(View.GONE);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            mMap.setPadding(0, 0, 0, 0);
        }
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

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            // Write code here if permission already given.
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.setOnCameraMoveListener(this);

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

            // Moving Camera to a Location with animation
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currLatLng).zoom(12).build();

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            // get coffeeshop data from volley
            yelpFetcher.fetch(currLatLng.latitude, currLatLng.longitude);

            centerPoint = currLatLng;

            // adjust the bottom margin depending on what's displayed down there
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    switch(mBottomSheetBehavior.getState()) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            for (Polyline p : polylines) p.remove();
                            mMap.setPadding(0, 0, 0, 0);
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            mMap.setPadding(0, 0, 0, mBottomSheetBehavior.getPeekHeight()*2);
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            if(btnStartStopTrip.getText()!="Stop Trip") {
                                for (Polyline p : polylines) p.remove();
                                bottomSheetContent.setVisibility(View.GONE);
                                btnStartStopTrip.setVisibility(View.GONE);
                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                            mMap.setPadding(0, 0, 0, mBottomSheetBehavior.getPeekHeight());
                            break;
                    }
                }
            });
        }
    }

    /**
     * Displays the "Find Nearby Shops" button if user has moved screen 2.5kms away from previous centerpoint of screen.
     */
    @Override
    public void onCameraIdle() {
        if (mMap == null || centerPoint == null) return;

        LatLng oldCenterPoint = centerPoint;
        centerPoint = mMap.getCameraPosition().target;
        float[] distances = new float[1];
        Location.distanceBetween(oldCenterPoint.latitude, oldCenterPoint.longitude,
                centerPoint.latitude, centerPoint.longitude, distances);
        if (distances[0] >= 2500) {
            btnFindShops.setVisibility(View.VISIBLE);
            btnFindShops.setEnabled(true);
        }
    }

    /**
     * Keeps the "Find Nearby Shops" button invisible while camera is mid-movement.
     */
    @Override
    public void onCameraMove() {
        btnFindShops.setEnabled(false);
        btnFindShops.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onMarkerClick (final Marker marker) {
        // if the map is "frozen", user is on a trip and markers shouldn't be clickable either
        if (!mMap.getUiSettings().isScrollGesturesEnabled()) return true;
        // disable the find nearby shops button
        btnFindShops.setEnabled(false);
        btnFindShops.setVisibility(View.INVISIBLE);

        // remove old polylines
        for (Polyline p : polylines) p.remove();
        bottomSheetContent.setText("");
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
     * Sets up logic for the trip to the marker, calculates the directions, and triggers
     * the necessary callbacks to update the UI.
     * Currently only provides one route (the fastest).
     */
    public void calculateDirectionsForTrip(Marker marker, String mode) {
        // DEBUG
        Log.d(TAG, "calculateDirectionsForTrip: calculating directions.");

        final Marker finalMarker = marker;
        final String finalMode = mode;

        // TRIP STUFF
        final Trip trip = new Trip();
        // a periodic task for checking the user's distance from the shop. runs every 5 seconds
        final Runnable mapChecker = new Runnable() {
            @Override
            public void run() {
                // prevent the user from messing with map while trip is happening
                mMap.getUiSettings().setAllGesturesEnabled(false);
                if (finalMode == "driving") {
                    btnWalk.setVisibility(View.INVISIBLE);
                    btnWalk.setEnabled(false);
                } else {
                    btnDrive.setVisibility(View.INVISIBLE);
                    btnDrive.setEnabled(false);
                }

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
                            finalMarker.getPosition().latitude, finalMarker.getPosition().longitude, distances);

                    Log.d(TAG, "User is " + distances[0] + " meters from destination.");
                    // if user is within 25m of shop, conclude trip
                    if (distances[0] < 25) {
                        trip.setTimeArrived(new Date(System.currentTimeMillis()));

                        // remove polylines
                        for (Polyline p : polylines) p.remove();
                        finalMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                        // trip concluded --> display shop details
                        BasicShop selectedShop = new BasicShop(shopListing.get(finalMarker));
                        trip.setDestination(selectedShop.getId());
                        Intent i = new Intent(getApplicationContext(), ShopInfoActivity.class);
                        i.putExtra(ShopInfoActivity.PREF_READ_ONLY, true);
                        i.putExtra(Shop.PREF_BASIC_SHOP, selectedShop);

                        // push trip to database AND pass it to the next activity so that it's connected to
                        // whatever order the user makes
                        trip.setId(Database.getInstance().addTrip(trip));
                        i.putExtra(Order.EXTRA_ORDER_TRIP, trip);
                        startActivity(i);

                        // make map clickable again
                        mMap.getUiSettings().setAllGesturesEnabled(true);

                        handler.removeCallbacks(this);
                        return;
                    }
                }
                handler.postDelayed(this, 5000); // check the user's location every 5 seconds
            }
        };
        btnStartStopTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStartStopTrip.getText().equals("Start Trip")) {
                    handleTrip(finalMarker, mapChecker, trip, true);
                    mBottomSheetBehavior.setHideable(false);

                    // switch to a stop button
                    btnStartStopTrip.setText("Stop Trip");
                    btnStartStopTrip.setBackgroundColor((getResources().getColor(R.color.danger)));
                }
                else {
                    handleTrip(finalMarker, mapChecker, trip, false);
                    mBottomSheetBehavior.setHideable(true);

                    // get rid of the bottom bar, trip is over so store is no longer main focus
                    recedeDisplay();

                    // switch to a start button
                    btnStartStopTrip.setText("Start Trip");
                    btnStartStopTrip.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });

        // DIRECTIONS STUFF
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
     * @param start: whether to start the trip or stop it
     * @return
     */
    public void handleTrip(final Marker marker, final Runnable mapChecker, final Trip trip, boolean start) {
        if (!start) { // STOP the trip
            // remove polylines and old ETA popup
            for (Polyline p : polylines) p.remove();
            routeDetailsMarker.remove();
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            // make map clickable again
            mMap.getUiSettings().setAllGesturesEnabled(true);
            if (!btnWalk.isEnabled()) {
                btnWalk.setVisibility(View.VISIBLE);
                btnWalk.setEnabled(true);
            } else {
                btnDrive.setVisibility(View.VISIBLE);
                btnDrive.setEnabled(true);
            }
            handler.removeCallbacks(mapChecker);
            // display a trip summary
            if (trip.getTimeArrived() != null) {
                BasicShop selectedShop = new BasicShop(shopListing.get(marker));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Trip Summary")
                        .setMessage("Destination: " + selectedShop.getName() + "\n"
                                + "Duration: " + trip.getTravelTime() + " minutes");
                AlertDialog tripSummary = builder.create();
                tripSummary.show();
            }
        } else { // START the trip
            trip.setTimeDiscover(new Date(System.currentTimeMillis()));
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            handler.post(mapChecker);
        }
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

    public void drawUpdatedList() {
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

    public void displaySteps(DirectionsResponse response) {
        ArrayList<DirectionsRoute> routes = response.getRoutes();
        DirectionsRoute route = routes.get(0);
        ArrayList<DirectionsStep> steps = route.getSteps();
        String temp = "";
        String cleanStep = "";
        for (int i = 0; i < steps.size(); i++) {
            cleanStep = steps.get(i).getStep().replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ");
            cleanStep = cleanStep.replaceAll("Destination will be", System.getProperty("line.separator") + "Destination will be");
            temp += cleanStep + System.getProperty("line.separator");
            Log.d(TAG, steps.get(i).getStep());
        }
        bottomSheetContent.setVisibility(View.VISIBLE);
        btnStartStopTrip.setVisibility(View.VISIBLE);
        bottomSheetContent.setText(temp);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }
}

