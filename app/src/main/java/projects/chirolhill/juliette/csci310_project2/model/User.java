package projects.chirolhill.juliette.csci310_project2.model;

public abstract class User {
    protected String uID;
    protected String username;
    protected String email;
    protected String password;
    protected boolean isMerchant;

    public static final int CAFFEINE_LIMIT = 400; // in milligrams

    public String getuID() {
        return uID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isMerchant() {
        return isMerchant;
    }

    public void setMerchant(boolean merchant) {
        isMerchant = merchant;
    }

    public boolean checkPassword(String pw) {
        return this.password.equals(pw);
    }
}
