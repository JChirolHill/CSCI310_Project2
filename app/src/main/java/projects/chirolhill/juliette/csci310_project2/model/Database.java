package projects.chirolhill.juliette.csci310_project2.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Database {
    private static final String TAG = Database.class.getSimpleName();

    public static final String USERS = "users";
    public static final String ORDERS = "orders";
    public static final String DRINKS = "drinks";
    public static final String SHOPS = "shop";
    public static final String TRIPS = "trips";

    private static final Database ourInstance = new Database();

    private FirebaseDatabase firebase;
    private DatabaseReference dbUsersRef;
    private DatabaseReference dbOrdersRef;
    private DatabaseReference dbDrinksRef;
    private DatabaseReference dbShopsRef;
    private DatabaseReference dbTripsRef;

    public static Database getInstance() {
        return ourInstance;
    }

    //  initialize all the references
    private Database() {
        firebase = FirebaseDatabase.getInstance();
        dbUsersRef = firebase.getReference(USERS);
        dbOrdersRef = firebase.getReference(ORDERS);
        dbDrinksRef = firebase.getReference(DRINKS);
        dbShopsRef = firebase.getReference(SHOPS);
        dbTripsRef = firebase.getReference(TRIPS);
    }

//    private void getAtRef(DatabaseReference dbRef, Object result) {
//        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.getValue() == null) {
//                    result = null;
//                }
//                else {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    // returns user if exists, null if does not exist
    public User getUser(String id) {
//        final String userid = id;
        dbUsersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                if(dataSnapshot.getValue() == null) {

                }
                else {

                }
//                if(dataSnapshot.child(userid).exists()) {
//                    User u = null;
//                    if(dataSnapshot.child(userid).child("isMerchant").equals(true)) {
//                        u = new Merchant();
//                        u = dataSnapshot.child(userid).getValue(Merchant.class);
//                    }
//                    else {
//                        u = new Customer();
//                        u = dataSnapshot.child(userid).getValue(Customer.class);
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
        return null;
    }

    // returns error message, null if no problems
    // if pass in null user, will delete the item in database
    public String addUser(User u) {
        try {
            if(dbUsersRef.child(u.getUsername()) != null) {
                return "Someone already has this username, please pick another";
            }
            dbUsersRef.child(u.getUsername()).setValue(u);
        } catch(DatabaseException de) {
            Log.d(TAG, de.getMessage());
            return "Username cannot contain the following characters: . # $ [ ]";
        }
        return null;
    }

    // returns true if new user, false otherwise
    public boolean userExists(User u) {
        if(dbUsersRef.child(u.getUsername()) != null && !dbUsersRef.child(u.getUsername()).child("email").equals(u.getEmail())) {
            return true;
        }
        return false;
    }
}
