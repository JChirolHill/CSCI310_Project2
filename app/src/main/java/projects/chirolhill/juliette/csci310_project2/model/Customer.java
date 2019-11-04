package projects.chirolhill.juliette.csci310_project2.model;

public class Customer extends User {
    private UserLog log;

    public Customer() {
        this.setMerchant(false);
        log = new UserLog(this);
    }

    public UserLog getLog() {
        return log;
    }

    public void createOrder(Order o) {
        log.addOrder(o);
    }

    public boolean hasExceededCaffeineLimit() {
        return log.getTotalCaffeineLevel() > User.CAFFEINE_LIMIT;
    }
}
