package projects.chirolhill.juliette.csci310_project2.model;

public class Drink {
    private String id;
    private String name;
    private boolean isCoffee;
    private String imageURL;
    private float price;
    private Shop shop;
    private int timesOrdered;
    private int caffeine; // stored in milligrams

    public Drink(String id, boolean isCoffee, Shop shop) {
        this.id = id;
        this.isCoffee = isCoffee;
        this.shop = shop;
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

    public Shop getShop() {
        return shop;
    }

    public int getTimesOrdered() {
        return timesOrdered;
    }

    public int getCaffeine() {
        return caffeine;
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

    public void setCaffeine(int caffeine) {
        this.caffeine = caffeine;
    }

    public void increaseTimesOrdered(int times) {
        timesOrdered += times;
    }
}
