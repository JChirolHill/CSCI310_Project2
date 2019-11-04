package projects.chirolhill.juliette.csci310_project2.model;

import android.util.Pair;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Order implements Serializable {
    public static final String PREF_ORDER = "pref_order";

    private String id;
    private Map<String, Pair<Drink, Integer>> drinks; // store drinks and number of orders
    private String shopID;
    private String tripID;
    private String userID;
    private Date date;

    public Order(String id) {
        this.id = id;
    }

    public Order(String id, String shopID, String tripID, String userID, Date date) {
        this.id = id;
        this.shopID = shopID;
        this.tripID = tripID;
        this.userID = userID;
        this.date = date;
        drinks = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getShop() {
        return shopID;
    }

    public String getTrip() {
        return tripID;
    }

    public String getUser() {
        return userID;
    }

    public Date getDate() {
        return date;
    }

    public Map<String, Pair<Drink, Integer>> getDrinks() {
        return drinks;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setShop(String shopID) {
        this.shopID = shopID;
    }

    public void setTrip(String tripID) {
        this.tripID = tripID;
    }

    public void setUser(String userID) {
        this.userID = userID;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumItemsOrdered() {
        int numItems = 0;
        for(Map.Entry<String, Pair<Drink, Integer>> entry : drinks.entrySet()) {
            numItems += entry.getValue().second;
        }
        return numItems;
    }

    private double calcTotalCost() {
        double total = 0.0;
        for (Map.Entry<String, Pair<Drink, Integer>> entry : drinks.entrySet()) {
            total += entry.getValue().first.getPrice() * entry.getValue().second;
        }
        return total;
    }

    public double getTotalCost() {
        return calcTotalCost();
    }

    // returns caffeine amount in milligrams
    private int calcTotalCaffeine() {
        int total = 0;
        for (Map.Entry<String, Pair<Drink, Integer>> entry : drinks.entrySet()) {
            total += entry.getValue().first.getCaffeine() * entry.getValue().second;
        }
        return total;
    }

    public int getTotalCaffeine() {
        return calcTotalCaffeine();
    }

    // returns true if go over caffeine limit
    public boolean addDrink(Drink d) {
        if(d != null) {
            String id = d.getId();
            if(drinks.get(id) != null) {
                drinks.put(id, new Pair<>(d, drinks.get(id).second + 1));
            }
            else {
                drinks.put(id, new Pair<>(d, 1));
            }
        }

        if(calcTotalCaffeine() > User.CAFFEINE_LIMIT) {
            return true;
        }
        return false;
    }

    // returns true if still over caffeine limit
    public boolean removeDrink(String id) {
        if(drinks.get(id) != null) {
            drinks.remove(id);
        }

        if(calcTotalCaffeine() > User.CAFFEINE_LIMIT) {
            return true;
        }
        return false;
    }
}
