package projects.chirolhill.juliette.csci310_project2.model.dbadapters;

import projects.chirolhill.juliette.csci310_project2.model.Drink;

public class DatabaseDrink implements DatabaseAdapter {
    public String id;
    public String name;
    public boolean isCoffee;
    public String imageURL;
    public float price;
    public String shopID;
    public int timesOrdered;
    public int caffeine;

    public DatabaseDrink() { }

    public DatabaseDrink(Drink d) {
        this.id = d.getId();
        this.name = d.getName();
        this.isCoffee = d.isCoffee();
        this.imageURL = d.getImageURL();
        this.price = d.getPrice();
        this.shopID = d.getShopID();
        this.timesOrdered = d.getTimesOrdered();
        this.caffeine = d.getCaffeine();
    }

    @Override
    public Object revertToOriginal() {
        Drink d = new Drink(isCoffee, shopID);
        d.setId(this.id);
        d.setName(this.name);
        d.setImageURL(this.imageURL);
        d.setPrice(this.price);
        d.setTimesOrdered(this.timesOrdered);
        d.setCaffeine(this.caffeine);
        return d;
    }
}
