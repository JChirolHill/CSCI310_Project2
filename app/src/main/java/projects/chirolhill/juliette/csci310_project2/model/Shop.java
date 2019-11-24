package projects.chirolhill.juliette.csci310_project2.model;

import java.util.HashSet;
import java.util.Set;

public class Shop extends BasicShop {
    public static final String PREF_SHOP = "pref_shop";

    private Set<Drink> drinks;
    private String ownerID;
    private boolean pendingApproval;

    public Shop(String ownerID, BasicShop basicShop) {
        super(basicShop);
        this.ownerID = ownerID;
        this.drinks = new HashSet<>();
        this.pendingApproval = true;
    }

    public Set<Drink> getDrinks() {
        return drinks;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public boolean isPendingApproval() {
        return pendingApproval;
    }

    public void setPendingApproval(boolean pendingApproval) {
        this.pendingApproval = pendingApproval;
    }

    public void addDrink(Drink d) {
        this.drinks.add(d);
    }

    public void updateDrink(Drink d) {
        // find matching drink
        for(Drink drink : drinks) {
            if(drink.getId().equals(d.getId())) {
                drink.setName(d.getName());
                drink.setCaffeine(d.getCaffeine());
                drink.setIsCoffee(d.isCoffee());
                drink.setPrice(d.getPrice());
                drink.setTimesOrdered(d.getTimesOrdered());
                drink.setImageURL(d.getImageURL());
                break;
            }
        }
    }

    public void removeDrink(Drink d) {
        drinks.remove(d);
    }

    // return true at successful change of ownership
    public boolean changeOwnership(String newOwnerID) {
        if(newOwnerID != null) {
            ownerID = newOwnerID;
            return true;
        }
        return false;
    }

    private Drink calcTopDrink() {
        // check if any drinks
        if(!drinks.isEmpty()) {
            Drink topDrink = drinks.iterator().next();
            for(Drink d : drinks) {
                if(d.getTimesOrdered() > topDrink.getTimesOrdered()) {
                    topDrink = d;
                }
            }
            return topDrink;
        }
        return null;
    }

    public Drink getTopDrink() {
        return calcTopDrink();
    }
}
