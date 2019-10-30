package projects.chirolhill.juliette.csci310_project2.model.dbadapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Merchant;
import projects.chirolhill.juliette.csci310_project2.model.Shop;

public class DatabaseMerchant implements DatabaseAdapter {
    public String uID;
    public String username;
    public String email;
    public boolean isMerchant;
    public List<String> shops;

    public DatabaseMerchant() { }

    public DatabaseMerchant(Merchant u) {
        this.uID = u.getuID();
        this.username = u.getUsername();
        this.email = u.getEmail();
        this.isMerchant = u.isMerchant();
        this.shops = new ArrayList<>();
        for(Map.Entry<String, Shop> entry : u.getShops().entrySet()) {
            this.shops.add(entry.getKey());
        }
    }

    @Override
    public Object revertToOriginal() {
        Merchant m = new Merchant();
        m.setuID(this.uID);
        m.setUsername(this.username);
        m.setEmail(this.email);
        m.setMerchant(this.isMerchant);
        if(shops != null) {
            for(String shopID : shops) {
                m.addShop(new Shop(m.getuID(), new BasicShop(shopID)));
            }
        }
        return m;
    }
}
