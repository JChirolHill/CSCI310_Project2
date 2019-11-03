package projects.chirolhill.juliette.csci310_project2.model;

import java.io.Serializable;

public class Drink implements Serializable {
    public static final String EXTRA_DRINK = "extra_drink";

    private String id;
    private String name;
    private boolean isCoffee;
    private String imageURL;
    private float price;
//    private Shop shop;
    private String shopID;
    private int timesOrdered;
    private int caffeine; // stored in milligrams

    public Drink(String id, String shopID) {
        this.id = id;
        this.shopID = shopID;
    }

    public Drink(boolean isCoffee, String shopID) {
        this.isCoffee = isCoffee;
        this.shopID = shopID;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isCoffee() {
        return isCoffee;
    }

    public String getImageURL() {
        return imageURL;
    }

    public float getPrice() {
        return price;
    }

    public String getShopID() {
        return shopID;
    }

    public int getTimesOrdered() {
        return timesOrdered;
    }

    public int getCaffeine() {
        return caffeine;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setIsCoffee(boolean coffee) {
        isCoffee = coffee;
    }

    public void setCaffeine(int caffeine) {
        this.caffeine = caffeine;
    }

    public void setTimesOrdered(int times) {
        this.timesOrdered = times;
    }

    public void increaseTimesOrdered(int times) {
        timesOrdered += times;
    }
}
