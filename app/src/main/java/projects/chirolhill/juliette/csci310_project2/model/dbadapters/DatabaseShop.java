package projects.chirolhill.juliette.csci310_project2.model.dbadapters;

import java.util.ArrayList;
import java.util.List;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Shop;

public class DatabaseShop implements DatabaseAdapter {
    public String id;
    public String ownerID;
    public String name;
    public double rating;
    public String priceRange;
    public String imgURL;
    public String address;
    public boolean pendingApproval;
    public List<String> verificationDocs;
    public List<String> drinks;

    public DatabaseShop() { }

    public DatabaseShop(Shop s) {
        this.id = s.getId();
        this.ownerID = s.getOwnerID();
        this.name = s.getName();
        this.rating = s.getRating();
        this.priceRange = s.getPriceRange();
        this.imgURL = s.getImgURL();
        this.address = s.getAddress();
        this.pendingApproval = s.isPendingApproval();
        this.verificationDocs = s.getVerificationDocs();
        this.drinks = new ArrayList<>();
        for(Drink d : s.getDrinks()) {
            drinks.add(d.getId());
        }
    }

    @Override
    public Object revertToOriginal() {
        Shop s = new Shop(this.ownerID, new BasicShop(this.id));
        s.setName(this.name);
        s.setRating(this.rating);
        s.setPriceRange(this.priceRange);
        s.setImgURL(this.imgURL);
        s.setAddress(this.address);
        s.setPendingApproval(this.pendingApproval);
        s.setVerificationDocs(this.verificationDocs);
        if(this.drinks != null) {
            for(String drinkID : this.drinks) {
                s.addDrink(new Drink(drinkID, s));
            }
        }
        return s;
    }
}
