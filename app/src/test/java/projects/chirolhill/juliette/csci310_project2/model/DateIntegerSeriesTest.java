package projects.chirolhill.juliette.csci310_project2.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class DateIntegerSeriesTest {

    private DateIntegerSeries testSeries;
    private String title;
    private final LocalDate ONE_WEEK_AGO = LocalDate.now().minusDays(7);

    @Before
    public void setUp() throws Exception {
        this.title = "testSeries";
        this.testSeries = new DateIntegerSeries(this.title);
        for (int i = 0; i < 8; i++) {
            LocalDate localDate = ONE_WEEK_AGO.plusDays(i);
            Long date =  Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
            this.testSeries.add(date, i);
        }
    }

    @Test
    public void add() {
        int oldSize = this.testSeries.size();
        this.testSeries.add(
                Date.from(ONE_WEEK_AGO
                        .plusDays(9)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant())
                        .getTime(), 0);
        assertEquals(oldSize+1, this.testSeries.size());
        this.testSeries.add(
                Date.from(ONE_WEEK_AGO
                        .plusDays(10)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant())
                        .getTime(), 10);
        assertEquals(oldSize+2, this.testSeries.size());
    }

    @Test
    public void getTitle() {
        assertEquals(this.title, this.testSeries.getTitle());
    }

    @Test
    public void getX() {
        // healthy input
        assertEquals(
                (long) Date.from(ONE_WEEK_AGO
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant())
                        .getTime(),
                (long) this.testSeries.getX(0)
        );
        assertEquals(
                (long) Date.from(ONE_WEEK_AGO
                        .plusDays(7)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant())
                        .getTime(),
                (long) this.testSeries.getX(7)
        );
    }

    @Test
    public void getY() {
        // healthy input
        assertEquals((int) 0, (int) this.testSeries.getY(0));
        assertEquals((int) 1, (int) this.testSeries.getY(1));
        assertEquals((int) 7, (int) this.testSeries.getY(7));
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
        assertEquals(7, this.testSeries.getMaxYValue());

        // add a negative, see if changes
        this.testSeries.add(
                Date.from(ONE_WEEK_AGO
                        .plusDays(9)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant())
                        .getTime(), -100);
        assertEquals(7, this.testSeries.getMaxYValue());

        // add a new max
        this.testSeries.add(
                Date.from(ONE_WEEK_AGO
                        .plusDays(10)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant())
                        .getTime(), 8);
        assertEquals(8, this.testSeries.getMaxYValue());
    }
}