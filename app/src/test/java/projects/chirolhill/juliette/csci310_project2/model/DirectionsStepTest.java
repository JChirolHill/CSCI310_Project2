package projects.chirolhill.juliette.csci310_project2.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DirectionsStepTest {

    private String step;
    private String duration;
    private String distance;
    private DirectionsStep directionsStep;

    @Before
    public void setUp() throws Exception {
        this.step = "Head west toward Elden Ave";
        this.duration = "1 min";
        this.distance = "85 ft";
        this.directionsStep = new DirectionsStep(this.step, this.duration, this.distance);
    }

    @Test
    public void setStep() {
        // healthy input
        String newStep = "STOP! Don't head west toward Elden Ave";
        this.directionsStep.setStep(newStep);
        assertEquals(newStep, this.directionsStep.getStep());
    }

    @Test
    public void setDuration() {
        // healthy input
        String newDuration = "100 mins";
        this.directionsStep.setDuration(newDuration);
        assertEquals(newDuration, this.directionsStep.getDuration());
    }

    @Test
    public void setDistance() {
        // healthy input
        String newDistance = "2 feet";
        this.directionsStep.setDistance(newDistance);
        assertEquals(newDistance, this.directionsStep.getDistance());
    }
}