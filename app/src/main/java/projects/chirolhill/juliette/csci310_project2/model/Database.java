package projects.chirolhill.juliette.csci310_project2.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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

import projects.chirolhill.juliette.csci310_project2.ClaimShopActivity;

public class Database {
    /** Callback interface
     *  does all the work you want to do with the object returned from the database
     */
    public interface Callback {
        void dbCallback(Object o);
    }

    private Callback cb;

    public void setCallback(Callback cb) {
        this.cb = cb;
    }

    /** Database class
     *  getUser fetches the user at a particular uID
     *  addUser can ADD a user, UPDATE a user, and DELETE a user (pass in null)
     */
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
                    if(dataSnapshot.child("isMerchant").equals(true)) {
                        currUser = new Merchant();
                        currUser = dataSnapshot.getValue(Merchant.class);
                    }
                    else {
                        currUser = new Customer();
                        currUser = dataSnapshot.getValue(Customer.class);
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
            dbUsersRef.child(u.getuID()).setValue(u);
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
                Shop currShop;
                if(dataSnapshot.getValue() == null) { // new shop, not in database
                    currShop = null;
                }
                else { // existing user in database
                    currShop = dataSnapshot.getValue(Shop.class);
                }
                cb.dbCallback(currShop);
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
            dbShopsRef.child(s.getId()).setValue(s);
        } catch(DatabaseException de) {
            Log.d(TAG, de.getMessage());
            return de.getMessage();
        }
        return null;
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
