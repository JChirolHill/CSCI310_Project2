package projects.chirolhill.juliette.csci310_project2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Shop extends BasicShop {
    private Set<Drink> drinks;
    private Merchant owner;
    private List<String> verificationDocs;

    public Shop(String id, Merchant owner, BasicShop basicShop) {
        super(basicShop);
        this.owner = owner;
        drinks = new HashSet<>();
        verificationDocs = new ArrayList<>();
    }

    public Set<Drink> getDrinks() {
        return drinks;
    }

    public Merchant getOwner() {
        return owner;
    }

    public List<String> getVerificationDocs() {
        return verificationDocs;
    }

    public void addDrink(Drink d) {
        drinks.add(d);
    }

    public void removeDrink(Drink d) {
        drinks.remove(d);
    }

    // return true at successful change of ownership
    public boolean changeOwnership(Merchant m, List<String> verifDocs) {
        if(m != null && !verifDocs.isEmpty()) {
            verificationDocs.clear();
            verificationDocs = verifDocs;
            owner = m;
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
