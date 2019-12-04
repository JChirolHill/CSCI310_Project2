package projects.chirolhill.juliette.csci310_project2.model;


import android.support.v4.util.Pair;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Order {
    public static final String PREF_ORDER_ID = "pref_order_id";
    public static final String EXTRA_ORDER_DATE = "extra_order_date";
    public static final String EXTRA_ORDER_TRIP = "extra_order_trip";

    private String id;
    private Map<String, Pair<Drink, Integer>> drinks; // store drinks and number of orders
    private String shopID;
    private Trip trip;
    private String userID;
    private Date date;
    private double totalCostFromCoffee;
    private double totalCostFromTea;
    private double totalCost;
    private int totalCaffeineFromCoffee;
    private int totalCaffeineFromTea;
    private int totalCaffeine;

    public Order(String id) {
        this.id = id;
        this.drinks = new HashMap<>();
        this.shopID = null;
        this.trip = null;
        this.userID = null;
        this.date = null;
        this.totalCostFromCoffee = 0;
        this.totalCostFromTea = 0;
        this.totalCost = -1;
        this.totalCaffeineFromCoffee = 0;
        this.totalCaffeineFromTea = 0;
        this.totalCaffeine = -1;
    }

    public Order(String id, String shopID, String tripID, String userID, Date date) {
        this.id = id;
        this.shopID = shopID;
        if(tripID != null) {
            this.trip = new Trip(tripID);
        }
        else {
            this.trip = null;
        }
        this.totalCost = -1;
        this.totalCaffeine = -1;
        this.userID = userID;
        this.date = date;
        this.drinks = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getShop() {
        return shopID;
    }

    public Trip getTrip() {
        return trip;
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

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public void setUser(String userID) {
        this.userID = userID;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // For calc/getter methods: pass true to calculate val from drink list, false to get attribute val
    // COSTS
    // Coffee
    private double calcTotalCostFromCoffee() {
        double total = 0.0;
        for (Map.Entry<String, Pair<Drink, Integer>> entry : drinks.entrySet()) {
            Drink drink = entry.getValue().first;
            int quantity = entry.getValue().second;
            if (drink.isCoffee()) {
                total += (drink.getPrice() * quantity);
            }
        }
        if (total > 0) {
            totalCostFromCoffee = total; // if the drink data isn't populated, you'll get 0
        }
        return total;
    }
    public double getTotalCostFromCoffee(boolean calc) {
        return (calc ? calcTotalCostFromCoffee() : totalCostFromCoffee);
    }
    public void setTotalCostFromCoffee(double totalCostFromCoffee) {
        this.totalCostFromCoffee = totalCostFromCoffee;
    }
    // Tea
    private double calcTotalCostFromTea() {
        double total = 0.0;
        for (Map.Entry<String, Pair<Drink, Integer>> entry : drinks.entrySet()) {
            Drink drink = entry.getValue().first;
            int quantity = entry.getValue().second;
            if (!drink.isCoffee()) {
                total += (drink.getPrice() * quantity);
            }
        }
        if (total > 0) {
            totalCostFromTea = total;
        }
        return total;
    }
    public double getTotalCostFromTea(boolean calc) {
        return (calc ? calcTotalCostFromTea() : totalCostFromTea);
    }
    public void setTotalCostFromTea(double totalCostFromTea) {
        this.totalCostFromTea = totalCostFromTea;
    }
    // TOTAL
    private double calcTotalCost() {
        double total = calcTotalCostFromCoffee() + calcTotalCostFromTea();
        if (total > 0) {
            totalCost = total;
        }
        return total;
    }
    public double getTotalCost(boolean calc) {
        return (calc ? calcTotalCost() : totalCost);
    }
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    // CAFFEINE
    // Coffee
    private int calcTotalCaffeineFromCoffee() {
        int total = 0;
        for (Map.Entry<String, Pair<Drink, Integer>> entry : drinks.entrySet()) {
            Drink drink = entry.getValue().first;
            int quantity = entry.getValue().second;
            if (drink.isCoffee()) {
                total += (drink.getCaffeine() * quantity);
            }
        }
        if (total > 0) {
            totalCaffeineFromCoffee = total;
        }
        return total;
    }
    public int getTotalCaffeineFromCoffee(boolean calc) {
        return (calc ? calcTotalCaffeineFromCoffee() : totalCaffeineFromCoffee);
    }
    public void setTotalCaffeineFromCoffee(int totalCaffeineFromCoffee) {
        this.totalCaffeineFromCoffee = totalCaffeineFromCoffee;
    }
    // Tea
    private int calcTotalCaffeineFromTea() {
        int total = 0;
        for (Map.Entry<String, Pair<Drink, Integer>> entry : drinks.entrySet()) {
            Drink drink = entry.getValue().first;
            int quantity = entry.getValue().second;
            if (!drink.isCoffee()) {
                total += (drink.getCaffeine() * quantity);
            }
        }
        if (total > 0) {
            totalCaffeineFromTea = total;
        }
        return total;
    }
    public int getTotalCaffeineFromTea(boolean calc) {
        return (calc ? calcTotalCaffeineFromTea() : totalCaffeineFromTea);
    }
    public void setTotalCaffeineFromTea(int totalCaffeineFromTea) {
        this.totalCaffeineFromTea = totalCaffeineFromTea;
    }
    // TOTAL
    private int calcTotalCaffeine() {
        int total = calcTotalCaffeineFromCoffee() + calcTotalCaffeineFromTea();
        if (total > 0) {
            totalCaffeine = total;
        }
        return total;
    }
    public int getTotalCaffeine(boolean calc) {
        return (calc ? calcTotalCaffeine() : totalCaffeine);
    }
    public void setTotalCaffeine(int totalCaffeine) {
        this.totalCaffeine = totalCaffeine;
    }

    public int getNumItemsOrdered() {
        int numItems = 0;
        for(Map.Entry<String, Pair<Drink, Integer>> entry : drinks.entrySet()) {
            numItems += entry.getValue().second;
        }
        return numItems;
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
