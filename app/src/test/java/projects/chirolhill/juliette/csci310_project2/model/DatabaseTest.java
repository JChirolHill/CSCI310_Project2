package projects.chirolhill.juliette.csci310_project2.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseTest {
    @Before
    public void setUp() {
        // create users
        User c0 = new Customer();
        c0.setuID("F9He9bHqtpfDspf0chIyzCMpFZ62");
        c0.setEmail("testcustomer@test.com");
        c0.setUsername("Test Customer");
        User m0 = new Merchant();
        m0.setuID("F5KH8zujGEWQ1wR5nQ6x8Yp8WfG3");
        m0.setEmail("testmerchant@test.com");
        m0.setUsername("Test Merchant");

        // create shops
        BasicShop bs0 = new BasicShop("TEST_BASIC_SHOP_ID_0");
        bs0.setName("Test shop name 0");
        bs0.setAddress("1111 Test Drive, Testown, TS 11111");
        bs0.setImgURL("https://shoptestimgurl0");
        bs0.setPriceRange("$");
        bs0.setRating(4.5);
        BasicShop bs1 = new BasicShop("TEST_BASIC_SHOP_ID_1");
        bs0.setName("Test shop name 1");
        bs0.setAddress("2222 Test Drive, Testown, TS 22222");
        bs0.setImgURL("https://shoptestimgurl1");
        bs0.setPriceRange("$$$");
        bs0.setRating(2);
        Shop s0 = new Shop(m0.getuID(), bs0);

        // create drinks
        Drink d1 = new Drink(false, );

        // create orders
        Order o1 = new Order(null);
        o1.addDrink()
    }

    @Test
    public void getInstance() {
//        Database tempDB = Database.getInstance()
//        assertNotNull(Database.getInstance());
//        assertEquals(Database.class, Database.getInstance().getClass());
    }

    @Test
    public void getUser() {
        // add user

        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {

            }
        });
        Database.getInstance().getUser();
    }

    @Test
    public void addUser() {
    }

    @Test
    public void getShop() {
    }

    @Test
    public void addShop() {
    }

    @Test
    public void getDrink() {
    }

    @Test
    public void addDrink() {
    }

    @Test
    public void removeDrink() {
    }

    @Test
    public void getOrder() {
    }

    @Test
    public void addOrder() {
    }

    @Test
    public void removeOrder() {
    }

    @Test
    public void getTrip() {
    }

    @Test
    public void addTrip() {
    }

    @Test
    public void uploadImages() {
    }
}