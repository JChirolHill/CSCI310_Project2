package projects.chirolhill.juliette.csci310_project2.model;

import org.junit.Before;
import org.junit.Test;

import projects.chirolhill.juliette.csci310_project2.MapsActivity;

import static org.junit.Assert.*;

public class DirectionsFetcherTest {

    private DirectionsFetcher directionsFetcher;
    double originLat, originLng, destLat, destLng;

    @Before
    public void setUp() throws Exception {
        // NOTE: we will not be testing the activity-dependent parts of the fetcher class, so null should be fine
        MapsActivity mapsActivity = new MapsActivity();
        this.directionsFetcher = new DirectionsFetcher(mapsActivity.getApplicationContext(), mapsActivity);

        // ORIGIN: Raloh's near USC campus
        this.originLat = 34.0320;
        this.originLng = -118.2910;

        // DESTINATION: Dulce at USC Village
        this.destLat = 34.0253;
        this.destLng = -118.2854;
    }

    @Test
    public void fetch() {
        // 1: driving
        this.directionsFetcher.fetch(this.originLat, this.originLng, this.destLat, this.destLng, "driving");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(this.directionsFetcher.getResponse());
        assertEquals("driving", this.directionsFetcher.getResponse().getMode());
        assertTrue(this.directionsFetcher.getResponse().getRoutes().size() > 0);

        // 2: walking
        // 1: driving
        this.directionsFetcher.fetch(this.originLat, this.originLng, this.destLat, this.destLng, "walking");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(this.directionsFetcher.getResponse());
        assertEquals("walking", this.directionsFetcher.getResponse().getMode());
        assertTrue(this.directionsFetcher.getResponse().getRoutes().size() > 0);
    }

    @Test
    public void requestJSONParse() {

    }

    @Test
    public void setCallback() {

    }

    @Test
    public void parse() {

    }

    @Test
    public void getResponse() {

    }
}