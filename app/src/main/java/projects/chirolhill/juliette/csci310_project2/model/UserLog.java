package projects.chirolhill.juliette.csci310_project2.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserLog implements Serializable {
    public static final String PREF_TOTAL_CAFFEINE = "pref_total_caffeine";

    private Customer owner;
    private Map<String, Order> orders;
    private int totalCaffeineLevel;
    private double totalMoneySpent;

    public UserLog(Customer owner) {
        this.owner = owner;
        orders = new HashMap<>();
        totalCaffeineLevel = 0;
        totalMoneySpent = 0.0;
    }

    public Customer getOwner() {
        return owner;
    }

    public Map<String, Order> getOrders() {
        return orders;
    }

    public int getTotalCaffeineLevel() {
        return totalCaffeineLevel;
    }

    public double getTotalMoneySpent() {
        return totalMoneySpent;
    }

    // sets totalCaffeineLevel and totalMoneySpent
    private void calcTotals() {
        for(Map.Entry<String, Order> entry : orders.entrySet()) {
            totalCaffeineLevel += entry.getValue().getTotalCaffeine(true);
            totalMoneySpent += entry.getValue().getTotalCost(true);
        }
    }

    public void addOrder(Order o) {
        orders.put(o.getId(), o);
        calcTotals();
    }

    public void removeOrder(String id) {
        orders.remove(id);
        calcTotals();
    }
}
