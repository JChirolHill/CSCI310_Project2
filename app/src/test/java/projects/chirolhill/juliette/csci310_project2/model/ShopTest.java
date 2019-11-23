package projects.chirolhill.juliette.csci310_project2.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShopTest {
    private Drink d0;
    private Drink d1;
    private Drink d2;
    private Shop s0;

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
        d2.setId("d1");
        d2.setCaffeine(52);
        d2.setPrice((float)15.32);
    }

    @Test
    public void getTopDrink() {
    }
}