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
    }

    @Test
    public void getTopDrink() {
        // no drinks
        assertNull(s0.getTopDrink());

        // one drink, no orders
        s0.addDrink(d0);
        assertEquals(d0, s0.getTopDrink());

        // one drink, some orders
        d0.increaseTimesOrdered(4);
        assertEquals(d0, s0.getTopDrink());

        // multiple drinks
        s0.addDrink(d1);
        s0.addDrink(d2);
        d1.increaseTimesOrdered(2);
        d2.increaseTimesOrdered(5);
        d1.increaseTimesOrdered(10);
        assertEquals(d1, s0.getTopDrink());
    }
}