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
        this.directionsFetcher = new DirectionsFetcher(null, null);

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


        // 2: walking
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