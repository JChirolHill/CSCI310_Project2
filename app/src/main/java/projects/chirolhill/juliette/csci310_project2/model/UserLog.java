package projects.chirolhill.juliette.csci310_project2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserLog implements Serializable {
    public static final String PREF_TOTAL_CAFFEINE = "pref_total_caffeine";

    private String id;
    private Customer owner;
    private List<Order> orders;
    private int totalCaffeineLevel;
    private double totalMoneySpent;

    public UserLog(Customer owner) {
        this.owner = owner;
        orders = new ArrayList<>();
        totalCaffeineLevel = 0;
        totalMoneySpent = 0.0;
    }

    public String getId() {
        return id;
    }

    public Customer getOwner() {
        return owner;
    }

    public List<Order> getOrders() {
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
        for(Order o : orders) {
            totalCaffeineLevel += o.getTotalCaffeine(true);
            totalMoneySpent += o.getTotalCost(true);
        }
    }

    public void addOrder(Order o) {
        orders.add(o);
        calcTotals();
    }
}
