package projects.chirolhill.juliette.csci310_project2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {
    private final String TAG = MapsActivity.class.getSimpleName();

    private LocationManager locationManager;
    private Location myLocation;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        }
        else{
            // Write you code here if permission already given.
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        // Add a marker in current spot and move the camera
        LatLng currLatLng = null;

        myLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

        if (myLocation != null) {
            currLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            Log.i("Location Info", "Location achieved!");
        } else {
            Log.i("Location Info", "No location :(");
        }
        MarkerOptions marker = new MarkerOptions().position(
                currLatLng).title("You are here!").snippet("Your current location");

        // Moving Camera to a Location with animation
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currLatLng).zoom(12).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        mMap.addMarker(marker);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

//    public void drawUpdatedList() {
//        // add markers for all coffeeshops
//        Log.d(TAG, "size when update: " + volley.getLocations().size());
//        for(Pair<Double, Double> coord : volley.getLocations()) {
//            mMap.addMarker(new MarkerOptions().position(
//                    new LatLng(coord.first, coord.second)).title("A coffeeshop").snippet("coffeeshop description"));
//        }
//    }
}
