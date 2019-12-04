package projects.chirolhill.juliette.csci310_project2.model;

import android.util.Pair;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserLogTest {
    private Customer c0;
    private UserLog log0;
    private UserLog log1;
    private UserLog log2;
    private UserLog log3;
    private UserLog log4;
    private Order o0;
    private Order o1;
    private Drink d0;
    private Drink d1;

    @Before
    public void setUp() {
        d0 = new Drink(false, "s0");
        d0.setId("d0");
        d0.setCaffeine(200);
        d0.setPrice((float)3.5);
        d1 = new Drink(true, "s0");
        d1.setId("d1");
        d1.setCaffeine(50);
        d1.setPrice((float)1);

        c0 = new Customer();
        log0 = new UserLog(c0);

        o0 = new Order("o0", "s0", "t0", c0.getuID(), null);
        o0.addDrink(d0);
        log1 = new UserLog(c0);
        log1.addOrder(o0);

        o1 = new Order("o1", "s0", "t0", c0.getuID(), null);
        o1.addDrink(d0);
        o1.addDrink(d0);
        o1.addDrink(d1);
        log2 = new UserLog(c0);
        log2.addOrder(o1);

        log3 = new UserLog(c0);
        log4 = new UserLog(c0);
    }

    @Test
    public void getOwner() {
        assertEquals(c0, log0.getOwner());
        assertEquals(c0, log1.getOwner());
        assertEquals(c0, log2.getOwner());
        assertEquals(c0, log3.getOwner());
    }

    @Test
    public void getOrders() {
        // no orders
        assertEquals(0, log3.getOrders().size());

        // multiple orders
        log3.addOrder(o0);
        log3.addOrder(o1);
        assertEquals(2, log3.getOrders().size());
    }

    @Test
    public void removeOrder() {
        log4.addOrder(o1);

        // remove null
        log4.removeOrder(null);
        assertEquals(1, log4.getOrders().size());

        // remove invalid order
        log4.removeOrder("One Ring To Rule Them All");
        assertEquals(1, log4.getOrders().size());

        // remove valid order
        log4.removeOrder("o1");
        assertEquals(0, log4.getOrders().size());
    }

    @Test
    public void getTotalCaffeineLevel() {
        assertEquals(0, log0.getTotalCaffeineLevel());
        assertEquals(200, log1.getTotalCaffeineLevel());
        assertEquals(450, log2.getTotalCaffeineLevel());
    }

    @Test
    public void getTotalMoneySpent() {
        assertEquals((float)0, log0.getTotalMoneySpent(), 0.009);
        assertEquals((float)3.5, log1.getTotalMoneySpent(), 0.009);
        assertEquals((float)8, log2.getTotalMoneySpent(), 0.009);
    }
}