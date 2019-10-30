package projects.chirolhill.juliette.csci310_project2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Shop extends BasicShop {
    private Set<Drink> drinks;
    private String ownerID;
    private List<String> verificationDocs;
    private boolean pendingApproval;

    public Shop(String ownerID, BasicShop basicShop) {
        super(basicShop);
        this.ownerID = ownerID;
        drinks = new HashSet<>();
        verificationDocs = new ArrayList<>();
        pendingApproval = true;
    }

    public Set<Drink> getDrinks() {
        return drinks;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public List<String> getVerificationDocs() {
        return verificationDocs;
    }

    public void addVerificationDoc(String doc) {
        this.verificationDocs.add(doc);
    }

    public void setVerificationDocs(List<String> verificationDocs) {
        this.verificationDocs = verificationDocs;
    }

    public boolean isPendingApproval() {
        return pendingApproval;
    }

    public void setPendingApproval(boolean pendingApproval) {
        this.pendingApproval = pendingApproval;
    }

    public void addDrink(Drink d) {
        drinks.add(d);
    }

    public void removeDrink(Drink d) {
        drinks.remove(d);
    }

    // return true at successful change of ownership
    public boolean changeOwnership(String newOwnerID, List<String> verifDocs) {
        if(newOwnerID != null && !verifDocs.isEmpty()) {
            verificationDocs.clear();
            verificationDocs = verifDocs;
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
