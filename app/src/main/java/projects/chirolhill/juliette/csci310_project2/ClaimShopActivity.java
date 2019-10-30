package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Merchant;
import projects.chirolhill.juliette.csci310_project2.model.Shop;
import projects.chirolhill.juliette.csci310_project2.model.User;

public class ClaimShopActivity extends AppCompatActivity {
    private TextView textClaimHeader;
    private Button btnCapture;
    private ImageView imgVerifDocs;
    private Button btnSubmit;
    private ConstraintLayout layoutClaimShop;
    private ConstraintLayout layoutPending;
    private TextView textError;
    private TextView textPending;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String FAIL = "fail";
    public static final String SUCCESS = "success";

    private BasicShop basicShop;
    private List<Bitmap> verificationDocs;
    private String ownerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_shop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textClaimHeader = findViewById(R.id.textClaimHeader);
        btnCapture = findViewById(R.id.btnCapture);
        imgVerifDocs = findViewById(R.id.imageVerifDocs);
        btnSubmit = findViewById(R.id.btnSubmit);
        layoutClaimShop = findViewById(R.id.layoutClaimShop);
        layoutPending = findViewById(R.id.layoutPending);
        textError = findViewById(R.id.textError);
        textPending = findViewById(R.id.textPending);

        verificationDocs = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        ownerID = prefs.getString(User.PREF_USER_ID, "INVALID USER ID");

        Intent i = getIntent();
        basicShop = (BasicShop)i.getSerializableExtra(BasicShop.PREF_BASIC_SHOP);
        textClaimHeader.setText(getResources().getString(R.string.claim) + " " + basicShop.getName());

        // check if shop already claimed
        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                if(o != null) {
                    final Shop s = (Shop)o;
                    if(!s.getOwnerID().equals(ownerID)) { // already has an owner
                        layoutClaimShop.setVisibility(View.GONE);
                        textError.setVisibility(View.VISIBLE);
                        btnSubmit.setEnabled(false);
                    }
                    else {
                        if(s.isPendingApproval()) { // pending approval
                            renderPendingApproval();
                        }
                        else { // shop approved
                            // add this shop to list of shops
                            Database.getInstance().setCallback(new Database.Callback() {
                                @Override
                                public void dbCallback(Object o) {
                                    Merchant m = (Merchant)o;
                                    m.addShop(s);
                                    Database.getInstance().addUser(m);
                                }
                            });
                            Database.getInstance().getUser(ownerID);

                            renderApproved();
                        }
                    }
                }
            }
        });
        Database.getInstance().getShop(basicShop.getId());

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // upload pictures of verification docs to database
                Database.getInstance().setCallback(new Database.Callback() {
                    @Override
                    public void dbCallback(Object o) {
                        if(o.equals(SUCCESS)) {
                            // display pending
                            renderPendingApproval();

                            // create the shop in database
                            String addShopResult = Database.getInstance().addShop(new Shop(ownerID, new BasicShop(basicShop)));
                            if(addShopResult != null) {
                                textError.setText(addShopResult);
                                textError.setVisibility(View.VISIBLE);
                            }
                        }
                        else if(o.equals(FAIL)) {
                            textError.setText(R.string.failedImgUpload);
                            textError.setVisibility(View.VISIBLE);
                        }
                    }
                });
                Database.getInstance().uploadImages(basicShop.getId(), verificationDocs);
            }
        });
    }

    // create intent to use another app to take picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            btnCapture.setVisibility(View.GONE);
            imgVerifDocs.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(true);

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgVerifDocs.setImageBitmap(imageBitmap);
            verificationDocs.add(imageBitmap);
        }
    }

    private void renderPendingApproval() {
        layoutClaimShop.setVisibility(View.GONE);
        layoutPending.setVisibility(View.VISIBLE);
    }

    private void renderApproved() {
        layoutClaimShop.setVisibility(View.GONE);
        textPending.setText(R.string.ownershipApproved);
        layoutPending.setVisibility(View.VISIBLE);
    }
}
