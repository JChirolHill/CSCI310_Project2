package projects.chirolhill.juliette.csci310_project2.model;

import android.util.Pair;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Order implements Serializable {
    private String id;
    private Map<String, Pair<Drink, Integer>> drinks; // store drinks and number of orders
    private Shop shop;
    private Trip trip;
    private Customer user;

    public static final String PREF_ORDER = "pref_order";

    public Order(String id, Shop shop, Trip trip, Customer user) {
        this.id = id;
        this.shop = shop;
        this.trip = trip;
        this.user = user;
        drinks = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public Shop getShop() {
        return shop;
    }

    public Trip getTrip() {
        return trip;
    }

    public Customer getUser() {
        return user;
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
