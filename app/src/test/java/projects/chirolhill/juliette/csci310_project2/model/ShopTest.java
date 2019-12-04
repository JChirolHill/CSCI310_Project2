package projects.chirolhill.juliette.csci310_project2.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShopTest {
    private Drink d0;
    private Drink d1;
    private Drink d2;
    private BasicShop bs0;
    private Shop s0;
    private Shop s1;
    private Shop s2;

    @Before
    public void setUp() throws Exception {
        // create drinks
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

        // create shops
        bs0 = new BasicShop("TEST_BASIC_SHOP_ID_0");
        bs0.setName("Test shop name 0");
        bs0.setAddress("1111 Test Drive, Testown, TS 11111");
        bs0.setImgURL("https://shoptestimgurl0");
        bs0.setPriceRange("$");
        bs0.setRating(4.5);
        s0 = new Shop("m0", bs0);
        s1 = new Shop(null, bs0);
        s2 = new Shop(null, bs0);
    }

    @Test
    public void getOwnerID() {
        assertEquals("m0", s0.getOwnerID());
        assertNull(s1.getOwnerID());
    }

    @Test
    public void getTopDrink() {
        // no drinks
        assertEquals(0, s0.getDrinks().size());
        assertNull(s0.getTopDrink());

        // one drink, no orders
        s0.addDrink(d0);
        assertEquals(1, s0.getDrinks().size());
        assertEquals(d0, s0.getTopDrink());

        // one drink, some orders
        d0.increaseTimesOrdered(4);
        assertEquals(1, s0.getDrinks().size());
        assertEquals(d0, s0.getTopDrink());

        // multiple drinks
        s0.addDrink(d1);
        s0.addDrink(d2);
        d1.increaseTimesOrdered(2);
        d2.increaseTimesOrdered(5);
        d1.increaseTimesOrdered(10);
        assertEquals(3, s0.getDrinks().size());
        assertEquals(d1, s0.getTopDrink());
    }

    @Test
    public void setPendingApproval() {
        // default
        assertTrue(s0.isPendingApproval());

        // set true
        s0.setPendingApproval(true);
        assertTrue(s0.isPendingApproval());

        // set false
        s0.setPendingApproval(false);
        assertFalse(s0.isPendingApproval());
    }

    @Test
    public void changeOwnership() {
        assertFalse(s0.changeOwnership(null));
        assertTrue(s0.changeOwnership("m0"));
        assertEquals("m0", s0.getOwnerID());
    }

    @Test
    public void updateDrink() {
        // null drink
        s1.updateDrink(null);
        assertEquals(0, s1.getDrinks().size());

        // real drink, not in set
        s1.updateDrink(d2);
        assertEquals(0, s1.getDrinks().size());

        // real drink, in set
        Drink d3 = new Drink(true, "s1");
        d3.setId("d3");
        d3.setCaffeine(100);
        d3.setPrice((float)7.99);
        s1.addDrink(d3);
        d3.setPrice(2.34f);
        d3.setCaffeine(23);
        d3.setTimesOrdered(17);
        s1.updateDrink(d3);
        assertEquals(2.34f, s1.getTopDrink().getPrice(), 0.009);
        assertEquals(23, s1.getTopDrink().getCaffeine());
        assertEquals(17, s1.getTopDrink().getTimesOrdered());
        assertEquals(1, s1.getDrinks().size());
    }

    @Test
    public void removeDrink() {
        // remove when no drinks, invalid drink
        s2.removeDrink(null);
        assertEquals(0, s2.getDrinks().size());

        // remove nonexisting drink
        s2.removeDrink(d0);
        assertEquals(0, s2.getDrinks().size());

        // remove existing drink
        s2.addDrink(d1);
        assertEquals(1, s2.getDrinks().size());
        s2.removeDrink(d1);
        assertEquals(0, s2.getDrinks().size());
    }
}