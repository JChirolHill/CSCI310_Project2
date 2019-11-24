package projects.chirolhill.juliette.csci310_project2.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class OrderTest {
    private Customer c0;
    private Order o0;
    private Order o1;
    private Order o2;
    private Order o3;
    private Order o4;
    private Drink d0;
    private Drink d1;
    private Drink d2;
    private Trip t0;
    private Date now;

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
        d2 = new Drink(false, "s0");
        d2.setId("d2");
        d2.setCaffeine(200);
        d2.setPrice((float)5.99);

        c0 = new Customer();

        o0 = new Order(null, null, null, null, null);
        o1 = new Order(null, "s0", "t0", c0.getuID(), null);
        o2 = new Order(null, "s0", "t1", c0.getuID(), null);
        o3 = new Order(null, "s0", "t2", c0.getuID(), null);
        o4 = new Order(null, "s0", "t3", c0.getuID(), null);

        t0 = new Trip(null);

        now = new Date();
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

    @Test
    public void setId() {
        assertNull(o0.getId());

        o0.setId("-Lu-q14noX4Sy8YcUKyg");
        assertEquals("-Lu-q14noX4Sy8YcUKyg", o0.getId());
    }

    @Test
    public void setShop() {
        assertNull(o0.getShop());

        o0.setShop("s0");
        assertEquals("s0", o0.getShop());
    }

    @Test
    public void setTrip() {
        assertNull(o0.getTrip());

        o0.setTrip(t0);
        assertEquals(t0, o0.getTrip());
    }

    @Test
    public void setUser() {
        assertNull(o0.getUser());

        o0.setUser(c0.getuID());
        assertEquals(c0.getuID(), o0.getUser());
    }

    @Test
    public void setDate() {
        // before set
        assertNull(o0.getDate());

        // after set
        Date hobbitDay = new Date(-482086800000L);
        o0.setDate(hobbitDay);
        assertEquals(hobbitDay, o0.getDate());
    }

    @Test
    public void setTotalCost() {
        // before set
        assertEquals(-1, o2.getTotalCost(false), 0.009);

        // after set
        o2.setTotalCostFromCoffee(4.50f);
        o2.setTotalCostFromTea(1.25f);
        o2.setTotalCost(14);
        assertEquals(4.50f, o2.getTotalCostFromCoffee(false), 0.009);
        assertEquals(1.25f, o2.getTotalCostFromTea(false), 0.009);
        assertEquals(14, o2.getTotalCost(false), 0.009);
    }

    @Test
    public void setTotalCaffeine() {
        // before set
        assertEquals(-1, o2.getTotalCaffeine(false));

        // after set
        o2.setTotalCaffeineFromCoffee(100);
        o2.setTotalCaffeineFromTea(200);
        o2.setTotalCaffeine(300);
        assertEquals(100, o2.getTotalCaffeineFromCoffee(false));
        assertEquals(200, o2.getTotalCaffeineFromTea(false));
        assertEquals(300, o2.getTotalCaffeine(false));
    }

    @Test
    public void addDrink() {
        // add to empty
        assertFalse(o2.addDrink(d1));
        assertEquals(1, o2.getNumItemsOrdered());
        assertEquals(52, o2.getTotalCaffeine(true));
        assertEquals(52, o2.getTotalCaffeineFromCoffee(true));
        assertEquals(0, o2.getTotalCaffeineFromTea(true));
        assertEquals(15.32f, o2.getTotalCost(true), 0.009);

        // add same order
        assertFalse(o2.addDrink(d1));
        assertEquals(2, o2.getNumItemsOrdered());
        assertEquals(104, o2.getTotalCaffeine(true));
        assertEquals(104, o2.getTotalCaffeineFromCoffee(true));
        assertEquals(0, o2.getTotalCaffeineFromTea(true));
        assertEquals(30.64f, o2.getTotalCost(true), 0.009);

        // add past the limit
        assertFalse(o2.addDrink(d2));
        assertTrue(o2.addDrink(d2));
        assertEquals(4, o2.getNumItemsOrdered());
        assertEquals(504, o2.getTotalCaffeine(true));
        assertEquals(104, o2.getTotalCaffeineFromCoffee(true));
        assertEquals(400, o2.getTotalCaffeineFromTea(true));
        assertEquals(42.62f, o2.getTotalCost(true), 0.009);
    }

    @Test
    public void removeDrink() {
        // remove from empty order
        assertFalse(o3.removeDrink(d1.getId()));
        assertEquals(0, o3.getNumItemsOrdered());
        assertEquals(0, o3.getTotalCaffeine(true));
        assertEquals(0.0f, o3.getTotalCost(true), 0.009);

        assertFalse(o3.addDrink(d1));
        assertFalse(o3.addDrink(d2));
        assertTrue(o3.addDrink(d2));
        assertTrue(o3.addDrink(d2));

        // remove a singular drink
        assertTrue(o3.removeDrink(d1.getId()));
        assertEquals(3, o3.getNumItemsOrdered());
        assertEquals(600, o3.getTotalCaffeine(true));
        assertEquals(17.97f, o3.getTotalCost(true), 0.009);

        // remove a drink with duplicates
        assertFalse(o3.removeDrink(d2.getId()));
        assertEquals(0, o3.getNumItemsOrdered());
        assertEquals(0, o3.getTotalCaffeine(true));
        assertEquals(0.0f, o3.getTotalCost(true), 0.009);
    }

    @Test
    public void getDrinks() {
        // empty
        assertEquals(0, o4.getNumItemsOrdered());
        assertEquals(0, o4.getTotalCaffeine(true));
        assertEquals(0.0f, o4.getTotalCost(true), 0.009);

        // one drink
        o4.addDrink(d1);
        assertEquals(1, o4.getNumItemsOrdered());
        assertEquals(52, o4.getTotalCaffeine(true));
        assertEquals(15.32f, o4.getTotalCost(true), 0.009);

        // one drink multiple times
        o4.addDrink(d1);
        assertEquals(2, o4.getNumItemsOrdered());
        assertEquals(104, o4.getTotalCaffeine(true));
        assertEquals(30.64f, o4.getTotalCost(true), 0.009);

        // multiple drinks
        o4.addDrink(d0);
        assertEquals(3, o4.getNumItemsOrdered());
        assertEquals(104, o4.getTotalCaffeine(true));
        assertEquals(30.64f, o4.getTotalCost(true), 0.009);
    }

    @Test
    public void constructorNoParams() {
        Order o5 = new Order("o5");
        assertEquals("o5", o5.getId());
        assertEquals(-1, o5.getTotalCaffeine(false));
        assertEquals(-1, o5.getTotalCost(false), 0.009);
        assertEquals(0, o5.getTotalCaffeine(true));
        assertEquals(0, o5.getTotalCost(true), 0.009);
        assertEquals(0, o5.getDrinks().size());
        assertNull(o5.getTrip());
        assertNull(o5.getShop());
        assertNull(o5.getUser());
        assertNull(o5.getDate());
    }

    @Test
    public void constructorParams() {
        Order o6 = new Order("o6", "s0", "t4", c0.getuID(), now);
        assertEquals("o6", o6.getId());
        assertEquals(-1, o6.getTotalCaffeine(false));
        assertEquals(-1, o6.getTotalCost(false), 0.009);
        assertEquals(0, o6.getTotalCaffeine(true));
        assertEquals(0, o6.getTotalCost(true), 0.009);
        assertEquals(0, o6.getDrinks().size());
        assertEquals("t4", o6.getTrip().getId());
        assertEquals("s0", o6.getShop());
        assertEquals(c0.getuID(), o6.getUser());
        assertEquals(now, o6.getDate());
    }
}