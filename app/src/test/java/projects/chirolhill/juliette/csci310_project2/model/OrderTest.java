package projects.chirolhill.juliette.csci310_project2.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderTest {
    private Customer c0;
    private Order o0;
    private Order o1;
    private Order o2;
    private Drink d0;
    private Drink d1;

    @Before
    public void setUp() throws Exception {
        d0 = new Drink(false, "s0");
        d0.setId("d0");
        d0.setCaffeine(0);
        d0.setPrice((float)0);
        d1 = new Drink(true, "s0");
        d1.setId("d1");
        d1.setCaffeine(52);
        d1.setPrice((float)15.32);

        c0 = new Customer();
        o0 = new Order("o0", "s0", "t0", c0.getuID(), null);
        o1 = new Order("o0", "s0", "t0", c0.getuID(), null);
        o2 = new Order("o1", "s0", "t0", c0.getuID(), null);
    }

    @Test
    public void getNumItemsOrdered() {
        // empty order
        assertEquals(0, o0.getNumItemsOrdered());

        // order 1 drink
        o0.addDrink(d0);
        assertEquals(1, o0.getNumItemsOrdered());

        // order 1 drink, multiple times
        o0.addDrink(d0);
        assertEquals(2, o0.getNumItemsOrdered());

        // order with multiple drinks, diff numerical values for each
        o0.addDrink(d1);
        assertEquals(3, o0.getNumItemsOrdered());
    }

    @Test
    public void getTotalCost() {
        // passing in false
        // empty order
        o1.setTotalCost(0.0f);
        assertEquals(0.0f, o1.getTotalCost(false), 0.009);

        // order with value
        o1.setTotalCost(54.38f);
        assertEquals(54.38f, o1.getTotalCost(false), 0.009);

        // passing in true
        // empty order
        assertEquals(0.0f, o1.getTotalCost(true), 0.009);

        // one drink (0 drink)
        o1.addDrink(d0);
        assertEquals(0.0f, o1.getTotalCost(true), 0.009);

        // multiple drinks
        o1.addDrink(d1);
        o1.addDrink(d1);
        assertEquals(30.64f, o1.getTotalCost(true), 0.009);
    }

    @Test
    public void getTotalCaffeine() {
        // passing in false
        // empty order
        o1.setTotalCaffeine(0);
        assertEquals(0, o1.getTotalCaffeine(false));

        // order with value
        o1.setTotalCaffeine(54);
        assertEquals(54, o1.getTotalCaffeine(false));

        // passing in true
        // empty order
        assertEquals(0, o1.getTotalCaffeine(true));

        // one drink (0 drink)
        o1.addDrink(d0);
        assertEquals(0, o1.getTotalCaffeine(true));

        // multiple drinks
        o1.addDrink(d1);
        o1.addDrink(d1);
        assertEquals(104, o1.getTotalCaffeine(true));
    }
}