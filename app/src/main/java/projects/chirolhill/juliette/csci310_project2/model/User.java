package projects.chirolhill.juliette.csci310_project2.model;

import java.io.Serializable;

public abstract class User {
    protected String uID;
    protected String username;
    protected String email;
//    protected String password;
    protected boolean isMerchant;

    public static final int CAFFEINE_LIMIT = 400; // in milligrams
    public static final String PREF_USER_ID = "pref_user_id";
    public static final String PREF_USERNAME = "pref_username";
    public static final String PREF_EMAIL = "pref_email";
    public static final String PREF_IS_MERCHANT = "pref_is_merchant";

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
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

//    public void setPassword(String password) {
//        this.password = password;
//    }

    public boolean isMerchant() {
        return isMerchant;
    }

    public void setMerchant(boolean merchant) {
        isMerchant = merchant;
    }

//    public boolean checkPassword(String pw) {
//        return this.password.equals(pw);
//    }
}
