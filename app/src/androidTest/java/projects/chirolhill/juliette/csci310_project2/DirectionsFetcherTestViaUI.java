package projects.chirolhill.juliette.csci310_project2;


import com.google.android.gms.maps.model.LatLng;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import projects.chirolhill.juliette.csci310_project2.model.DirectionsFetcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DirectionsFetcherTestViaUI {

    private DirectionsFetcher directionsFetcher;
    double originLat, originLng, destLat, destLng;

    @Rule
    public ActivityTestRule<MapsActivity> mActivityTestRule = new ActivityTestRule<>(MapsActivity.class, true);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Before
    public void setUp() throws Exception {
        this.directionsFetcher = new DirectionsFetcher(mActivityTestRule.getActivity().getApplicationContext(),
                mActivityTestRule.getActivity());

        // ORIGIN: Ralph's near USC campus
        this.originLat = 34.0320;
        this.originLng = -118.2910;

        // DESTINATION: Dulce at USC Village
        this.destLat = 34.0253;
        this.destLng = -118.2854;
    }

    @Test
    public void fetch() {
        MapsActivity mapsActivity = mActivityTestRule.getActivity();
        mapsActivity.calculateDirections(
                mapsActivity.getMap().addMarker(
                        new MarkerOptions()
                            .position(new LatLng(this.destLat,  this.destLng))),
                "driving");

//        // 1: driving
//        this.directionsFetcher.fetch(this.originLat, this.originLng, this.destLat, this.destLng, "driving");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        assertNotNull(this.directionsFetcher.getResponse());
//        assertEquals("driving", this.directionsFetcher.getResponse().getMode());
//        assertTrue(this.directionsFetcher.getResponse().getRoutes().size() > 0);

        // 2: walking
//        this.directionsFetcher.fetch(this.originLat, this.originLng, this.destLat, this.destLng, "walking");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        assertNotNull(this.directionsFetcher.getResponse());
//        assertEquals("walking", this.directionsFetcher.getResponse().getMode());
//        assertTrue(this.directionsFetcher.getResponse().getRoutes().size() > 0);
    }
}
