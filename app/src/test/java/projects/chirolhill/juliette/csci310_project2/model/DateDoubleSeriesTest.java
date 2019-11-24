package projects.chirolhill.juliette.csci310_project2.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class DateDoubleSeriesTest {

    private DateDoubleSeries testSeries;
    private String title;
    private final LocalDate ONE_WEEK_AGO = LocalDate.now().minusDays(7);

    @Before
    public void setUp() throws Exception {
        this.title = "testSeries";
        this.testSeries = new DateDoubleSeries(this.title);
        for (int i = 0; i < 8; i++) {
            LocalDate localDate = ONE_WEEK_AGO.plusDays(i);
            Long date =  Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
            this.testSeries.add(date, i + 0.5);
        }
    }

    @Test
    public void add() {

    }

    @Test
    public void getTitle() {

    }

    @Test
    public void getX() {

    }

    @Test
    public void getY() {

    }

    @Test
    public void size() {
        // healthy input
        assertEquals(8, this.testSeries.size());

        // uneven x & y lengths
        testSeries.xSeries = new ArrayList<Long>(2);
        assertEquals(-1, this.testSeries.size());
    }

    @Test
    public void getMaxYValue() {
        // healthy input
        assertEquals(7.5, this.testSeries.getMaxYValue(), 0.01);

        // add a negative, see if changes
        this.testSeries.add(
                Date.from(ONE_WEEK_AGO
                        .plusDays(9)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant())
                        .getTime(), -100.5);
        assertEquals(7.5, this.testSeries.getMaxYValue(), 0.01);

        // add a new max
        this.testSeries.add(
                Date.from(ONE_WEEK_AGO
                        .plusDays(10)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant())
                        .getTime(), 8.5);
        assertEquals(8.5, this.testSeries.getMaxYValue(), 0.01);
    }
}