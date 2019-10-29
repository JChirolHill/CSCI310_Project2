package projects.chirolhill.juliette.csci310_project2.model;

import java.util.HashMap;
import java.util.Map;

public class Merchant extends User {
    private Map<String, Shop> shops;

    public Merchant() {
        this.shops = new HashMap<>();
        this.isMerchant = true;
    }

    public Map<String, Shop> getShops() {
        return shops;
    }

    public void addShop(Shop s) {
        shops.put(s.id, s);
    }

    public void removeShop(String id) {
        shops.remove(id);
    }

    // returns true if successful claim
    public boolean claimShop(BasicShop s) {
        if(shops.get(s.id) == null && !(s instanceof Shop)) {
            addShop(new Shop(this, s));
            return true;
        }
        return false;
    }
}
