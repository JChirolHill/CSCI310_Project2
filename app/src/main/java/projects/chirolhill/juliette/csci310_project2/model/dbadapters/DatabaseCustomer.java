package projects.chirolhill.juliette.csci310_project2.model.dbadapters;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Customer;
import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Shop;

public class DatabaseCustomer implements DatabaseAdapter {
    public String uID;
    public String username;
    public String email;
    public boolean isMerchant;
    public String logID;

    public DatabaseCustomer() {}

    public DatabaseCustomer(Customer u) {
        this.uID = u.getuID();
        this.username = u.getUsername();
        this.email = u.getEmail();
        this.isMerchant = u.isMerchant();
        if(u.getLog() != null) {
            this.logID = u.getLog().getId();
        }
    }

    @Override
    public Object revertToOriginal() {
        Customer c = new Customer();
        c.setuID(this.uID);
        c.setUsername(this.username);
        c.setEmail(this.email);
        c.setMerchant(this.isMerchant);
        return c;
    }
}
