package projects.chirolhill.juliette.csci310_project2.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.v4.util.Pair;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.ClaimShopActivity;
import projects.chirolhill.juliette.csci310_project2.model.dbadapters.DatabaseCustomer;
import projects.chirolhill.juliette.csci310_project2.model.dbadapters.DatabaseDrink;
import projects.chirolhill.juliette.csci310_project2.model.dbadapters.DatabaseMerchant;
import projects.chirolhill.juliette.csci310_project2.model.dbadapters.DatabaseOrder;
import projects.chirolhill.juliette.csci310_project2.model.dbadapters.DatabaseShop;
import projects.chirolhill.juliette.csci310_project2.model.dbadapters.DatabaseTrip;

public class Database {
    /**
     * Callback interface
     * does all the work you want to do with the object returned from the database
     */
    public interface Callback {
        void dbCallback(Object o);
    }

    private Callback cb;

    public void setCallback(Callback cb) {
        this.cb = cb;
    }

    /**
     * Database class
     * getUser fetches the user at a particular uID
     * addUser can ADD a user, UPDATE a user, and DELETE a user (pass in null)
     */
    private static final String TAG = Database.class.getSimpleName();

    public static final String USERS = "users";
    public static final String ORDERS = "orders";
    public static final String DRINKS = "drinks";
    public static final String SHOPS = "shops";
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

    // returns user if exists, null if does not exist
    public void getUser(String id) {
        Log.d(TAG, id);
        dbUsersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currUser;
                if(dataSnapshot.getValue() == null) { // new user, not in database
                    currUser = null;
                }
                else { // existing user in database
                    if((boolean)dataSnapshot.child("isMerchant").getValue() == true) {
                        currUser = (Merchant)dataSnapshot.getValue(DatabaseMerchant.class).revertToOriginal();
                    }
                    else {
                        currUser = (Customer)dataSnapshot.getValue(DatabaseCustomer.class).revertToOriginal();
                    }
                }
                cb.dbCallback(currUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    // returns error message, null if no problems
    // if pass in null user, will delete the item in database
    public String addUser(User u) {
        try {
            if(u.isMerchant()) {
                dbUsersRef.child(u.getuID()).setValue(new DatabaseMerchant((Merchant)u));
            }
            else {
                dbUsersRef.child(u.getuID()).setValue(new DatabaseCustomer((Customer)u));
            }
        } catch(DatabaseException de) {
            Log.d(TAG, de.getMessage());
            return "Username cannot contain the following characters: . # $ [ ]";
        }
        return null;
    }

    // returns shop if exists, null if does not exist
    public void getShop(String id) {
        Log.d(TAG, id);
        dbShopsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Shop currShop;
                if(dataSnapshot.getValue() == null) { // new shop, not in database, no need to fill values
                    currShop = null;
                    cb.dbCallback(currShop);
                }
                else { // existing shop in database
                    currShop = (Shop)dataSnapshot.getValue(DatabaseShop.class).revertToOriginal();

                    // fetch drink information
                    dbDrinksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // populate all drinks
                            for(Drink d : currShop.getDrinks()) {
                                currShop.updateDrink((Drink)dataSnapshot.child(d.getId()).getValue(DatabaseDrink.class).revertToOriginal());
                            }

                            cb.dbCallback(currShop);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    // returns error message, null if no problems
    // if pass in null shop, will delete the item in database
    public String addShop(Shop s) {
        try {
            dbShopsRef.child(s.getId()).setValue(new DatabaseShop(s));
        } catch(DatabaseException de) {
            Log.d(TAG, de.getMessage());
            return de.getMessage();
        }
        return null;
    }

    // returns drink if exists, null if does not exist
    public void getDrink(String id) {
        dbDrinksRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Drink currDrink;
                if(dataSnapshot.getValue() == null) { // new drink, not in database
                    currDrink = null;
                }
                else { // existing shop in database
                    currDrink = (Drink)dataSnapshot.getValue(DatabaseDrink.class).revertToOriginal();
                }
                cb.dbCallback(currDrink);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    // returns the key at which drink was added
    public String addDrink(Drink d) {
        try {
            if(d.getId() == null) {
                DatabaseReference newDrinkRef = dbDrinksRef.push();
                d.setId(newDrinkRef.getKey());
            }
            dbDrinksRef.child(d.getId()).setValue(new DatabaseDrink(d));
        } catch(DatabaseException de) {
            Log.d(TAG, de.getMessage());
            return de.getMessage();
        }
        return d.getId();
    }

    public String removeDrink(Drink d) {
        try {
            dbDrinksRef.child(d.getId()).removeValue();
        }
        catch(DatabaseException de) {
            return de.getMessage();
        }
        return null;
    }

    // returns order if exists, null if does not exist
    public void getOrder(String id) {
        dbOrdersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Order currOrder;
                if(dataSnapshot.getValue() == null) { // new order, not in database
                    currOrder = null;
                    cb.dbCallback(currOrder);
                }
                else { // existing shop in database
                    currOrder = (Order)dataSnapshot.getValue(DatabaseOrder.class).revertToOriginal();

                    // retrieve trip information if exists
                    if(currOrder.getTrip().getId() != null) {
                        dbTripsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                currOrder.setTrip((Trip)dataSnapshot.child(currOrder.getTrip().getId()).getValue(DatabaseTrip.class).revertToOriginal());
                                cb.dbCallback(currOrder);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        currOrder.setTrip(null); // NEED TO ADD THIS LINE OF CODE HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        cb.dbCallback(currOrder);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    // returns the key at which order was added
    public String addOrder(Order order) {
        try {
            final boolean newOrder;
            if(order.getId() == null) { // new order
                newOrder = true;
                DatabaseReference newOrderRef = dbOrdersRef.push();
                order.setId(newOrderRef.getKey());
            }
            else {
                newOrder = false;
            }
            dbOrdersRef.child(order.getId()).setValue(new DatabaseOrder(order));

            // update all the drinks based on number ordered in this order
            for(Map.Entry<String, Pair<Drink, Integer>> orderPair : order.getDrinks().entrySet()) {
                final Pair<Drink, Integer> drinkPair = orderPair.getValue();
                dbDrinksRef.child(orderPair.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // retrieve drink
                        Drink currDrink;
                        if(dataSnapshot.getValue() == null) { // drink DNE
                            currDrink = null;
                        }
                        else {
                            currDrink = (Drink)dataSnapshot.getValue(DatabaseDrink.class).revertToOriginal();
                        }

                        // update curr drink based on order
                        if(newOrder) {
                            currDrink.increaseTimesOrdered(drinkPair.second);
                        }
                        else { // update order, update total drinks based on delta
                            //TODO find delta between old and updated order to accurately update total times ordered
                        }

                        // add back the drink
                        addDrink(currDrink);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } catch(DatabaseException de) {
            Log.d(TAG, de.getMessage());
            return de.getMessage();
        }
        return order.getId();
    }

    public String removeOrder(Order o) {
        try {
            dbOrdersRef.child(o.getId()).removeValue();
        }
        catch(DatabaseException de) {
            return de.getMessage();
        }
        return null;
    }

    // returns trip if exists, null if does not exist
    public void getTrip(String id) {
        dbTripsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip currTrip;
                if(dataSnapshot.getValue() == null) { // new trip, not in database
                    currTrip = null;
                }
                else { // existing trip in database
                    currTrip = (Trip)dataSnapshot.getValue(DatabaseTrip.class).revertToOriginal();
                }
                cb.dbCallback(currTrip);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    // returns the key at which trip was added
    public String addTrip(Trip trip) {
        try {
            if(trip.getId() == null) {
                DatabaseReference newTripRef = dbTripsRef.push();
                trip.setId(newTripRef.getKey());
            }
            dbTripsRef.child(trip.getId()).setValue(new DatabaseTrip(trip));
        } catch(DatabaseException de) {
            Log.d(TAG, de.getMessage());
            return de.getMessage();
        }
        return trip.getId();
    }

    // uploads images to firestore
    // returns null if no errors, else returns error message
    public void uploadImages(String shopID, List<Bitmap> docs) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get the data from an ImageView as bytes
        for(int i=0; i<docs.size(); ++i) {
            Bitmap document = docs.get(i);
            StorageReference documentRef = storageRef.child(shopID + "/" + i + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = documentRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    cb.dbCallback(ClaimShopActivity.FAIL);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    cb.dbCallback(ClaimShopActivity.SUCCESS);
                }
            });
        }
    }
}
