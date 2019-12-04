package projects.chirolhill.juliette.csci310_project2.model.dbadapters;

import android.support.v4.util.Pair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.Trip;

public class DatabaseOrder implements DatabaseAdapter {
    public String id;
    public Map<String, Integer> drinks; // store drinks and number of orders
    public String shopID;
    public String tripID;
    public String customerID;
    public String date;
    public double totalCostFromCoffee;
    public double totalCostFromTea;
    public double totalCost;
    public int totalCaffeineFromCoffee;
    public int totalCaffeineFromTea;
    public int totalCaffeine;

    public DatabaseOrder() {}

    public DatabaseOrder(Order o) {
        this.id = o.getId();
        this.shopID = o.getShop();
        this.customerID = o.getUser();
        this.tripID = o.getTrip() != null ? o.getTrip().getId() : null;
        this.drinks = new HashMap<>();
        this.totalCostFromCoffee = 0;
        this.totalCostFromTea = 0;
        this.totalCost = 0;
        this.totalCaffeineFromCoffee = 0;
        this.totalCaffeineFromTea = 0;
        this.totalCaffeine = 0;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        this.date = dateFormat.format(o.getDate());

        if(o.getDrinks() != null) {
            for(Map.Entry<String, Pair<Drink, Integer>> entry : o.getDrinks().entrySet()) {
                drinks.put(entry.getKey(), entry.getValue().second);
                Drink drink = entry.getValue().first;
                int quantity = entry.getValue().second;
                if (drink.isCoffee()) {
                    totalCaffeineFromCoffee += drink.getCaffeine() * quantity;
                    totalCostFromCoffee += drink.getPrice() * quantity;
                } else {
                    totalCaffeineFromTea += drink.getCaffeine() * quantity;
                    totalCostFromTea += drink.getPrice() * quantity;
                }
                totalCost = (totalCostFromCoffee + totalCostFromTea);
                totalCaffeine = (totalCaffeineFromCoffee + totalCaffeineFromTea);
            }
        }
    }

    @Override
    public Object revertToOriginal() {
        Order o = new Order(id);
        o.setShop(shopID);
        o.setTrip(new Trip(tripID));
        o.setUser(customerID);
        o.setTotalCostFromCoffee(totalCostFromCoffee);
        o.setTotalCostFromTea(totalCostFromTea);
        o.setTotalCost(totalCost);
        o.setTotalCaffeineFromCoffee(totalCaffeineFromCoffee);
        o.setTotalCaffeineFromTea(totalCaffeineFromTea);
        o.setTotalCaffeine(totalCaffeine);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            o.setDate(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // add all the drinks, if more than once type of drink, add it multiple times
        if(drinks != null) {
            for(Map.Entry<String, Integer> entry : drinks.entrySet()) {
                for(int i=0; i<entry.getValue(); ++i) {
                    o.addDrink(new Drink(entry.getKey(), shopID));
                }
            }
        }

        return o;
    }
}
