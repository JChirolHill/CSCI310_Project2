package projects.chirolhill.juliette.csci310_project2.model.dbadapters;

import android.util.Pair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Order;

// NEEDS TO BE TESTED THOROUGHLY, JUST WROTE AND NO TESTS SO FAR, PLLLLLLEEEEEEEEEEASEEEEEE TEST WELL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class DatabaseOrder implements DatabaseAdapter {
    public String id;
    public Map<String, Integer> drinks; // store drinks and number of orders
    public String shopID;
    public String tripID;
    public String customerID;
    public String date;

    public DatabaseOrder() {}

    public DatabaseOrder(Order o) {
        this.id = o.getId();
        this.shopID = o.getShop();
        this.customerID = o.getUser();
        this.tripID = o.getTrip();
        this.drinks = new HashMap<>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        this.date = dateFormat.format(o.getDate());

        if(o.getDrinks() != null) {
            for(Map.Entry<String, Pair<Drink, Integer>> entry : o.getDrinks().entrySet()) {
                drinks.put(entry.getKey(), entry.getValue().second);
            }
        }
    }

    @Override
    public Object revertToOriginal() {
        Order o = new Order(id);
        o.setShop(shopID);
        o.setTrip(tripID);
        o.setUser(customerID);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            o.setDate(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // add all the drinks, if more than once type of drink, add it multiple times
        for(Map.Entry<String, Integer> entry : drinks.entrySet()) {
            for(int i=0; i<entry.getValue(); ++i) {
                o.addDrink(new Drink(entry.getKey(), shopID));
            }
        }

        return o;
    }
}
