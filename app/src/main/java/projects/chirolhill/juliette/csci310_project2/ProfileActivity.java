package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Customer;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Merchant;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.Shop;
import projects.chirolhill.juliette.csci310_project2.model.User;

import static projects.chirolhill.juliette.csci310_project2.R.color.colorAccent;
import static projects.chirolhill.juliette.csci310_project2.R.color.colorPrimaryDark;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    public static final String EXTRA_READONLY = "profile_extra_view"; // display as view or update
    public static final String EXTRA_CREATE_PROFILE = "profile_create_profile"; // called from signin activity

    private TextView textTitle;
    private TextView textSubtitle;
    private TextView textUsernamePrompt;
    private TextView textUsername;
    private TextView textEmailPrompt;
    private TextView textIsMerchantPrompt;
    private EditText editUsername;
    private TextView textEmail;
    private RadioGroup radioIsMerchant;
    private RadioButton radioBtnNo;
    private RadioButton radioBtnYes;
    private TextView textTypeUserDescr;
    private TextView textTypeUser;
    private TextView textError;
    private Button btnDetails;
    private Button btnSave;
    private Button btnLogout;

    private boolean readonly;
    private boolean origReadonly;
    private boolean createProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textTitle = findViewById(R.id.textProfileTitle);
        textSubtitle = findViewById(R.id.textProfileSubtitle);
        textUsernamePrompt = findViewById(R.id.textUsernamePrompt);
        textUsername = findViewById(R.id.textUsername);
        textEmailPrompt = findViewById(R.id.textEmailPrompt);
        textEmail = findViewById(R.id.textEmail);
        textIsMerchantPrompt = findViewById(R.id.textIsMerchantPrompt);
        editUsername = findViewById(R.id.editUsername);
        radioIsMerchant = findViewById(R.id.radioIsMerchant);
        radioBtnNo = findViewById(R.id.radio_no);
        radioBtnYes = findViewById(R.id.radio_yes);
        textTypeUserDescr = findViewById(R.id.textTypeUserDescr);
        textTypeUser = findViewById(R.id.textTypeUser);
        textError = findViewById(R.id.textError);
        btnDetails = findViewById(R.id.btnDetails);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);

        // fetch intent and decide whether readonly or editable
        final Intent i = getIntent();
        createProfile = i.getBooleanExtra(EXTRA_CREATE_PROFILE, false);
        if(i.getBooleanExtra(EXTRA_READONLY, true)) { // readonly: called from maps activity when click on profile icon
            readonly = true;
            origReadonly = true;
            renderReadOnly();
        }
        else { // editable: called when create profile first time from the signin activity
            readonly = false;
            origReadonly = false;
            User u = new Customer();
            u.setUsername(i.getStringExtra(User.PREF_USERNAME));
            u.setEmail(i.getStringExtra(User.PREF_EMAIL));
            renderEditable(u);
        }

        editUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                textTitle.setText(getString(R.string.textTitle, v.getText().toString()));

                return false;
            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                if(prefs.getBoolean(User.PREF_IS_MERCHANT, true)) { // merchant clicks on myshops btn
                    Intent i = new Intent(getApplicationContext(), ShopListing.class);
                    startActivity(i);
                }
                else { // customer clicks on myorders btn
                    Intent i = new Intent(getApplicationContext(), LogActivity.class);
                    startActivity(i);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readonly) { // switch to edit mode
                    User u;
                    SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                    if(prefs.getBoolean(User.PREF_IS_MERCHANT, true)) {
                        u = new Merchant();
                    }
                    else {
                        u = new Customer();
                    }
                    u.setUsername(prefs.getString(User.PREF_USERNAME, "Invalid Username"));
                    u.setEmail(prefs.getString(User.PREF_EMAIL, "Invalid Email"));
                    renderEditable(u);
                }
                else { // save to db and switch to view mode
                    User u;
                    if(radioIsMerchant.getCheckedRadioButtonId() == R.id.radio_yes) {
                        u = new Merchant();
                    }
                    else {
                        u = new Customer();
                    }
                    SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                    u.setuID(prefs.getString(User.PREF_USER_ID, ""));
                    u.setUsername(editUsername.getText().toString().trim());
                    u.setEmail(textEmail.getText().toString().trim());

                    SharedPreferences.Editor prefEditor = prefs.edit();
                    prefEditor.putString(User.PREF_USERNAME, u.getUsername());
                    prefEditor.putString(User.PREF_EMAIL, u.getEmail());
                    prefEditor.putBoolean(User.PREF_IS_MERCHANT, u.isMerchant());
                    prefEditor.commit();

                    String addResult = Database.getInstance().addUser(u);
                    if(addResult == null) { // no errors, successfully added to db
                        if(origReadonly) { // originally readonly, just revert to readonly
                            renderReadOnly();
                        }
                        else { // go to maps activity
                            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(i);
                        }
                    }
                    else { // some errors
                        textError.setText(addResult);
                    }
                }
                readonly = !readonly;
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // return to signin activity
                                Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                                startActivity(i);
                            }
                        });
            }
        });
    }

    private void renderReadOnly() {
        textUsername.setVisibility(View.VISIBLE);
        textTypeUserDescr.setVisibility(View.VISIBLE);
        textTypeUser.setVisibility(View.VISIBLE);
        btnDetails.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.VISIBLE);
        textSubtitle.setVisibility(View.GONE);
        editUsername.setVisibility(View.GONE);
        textIsMerchantPrompt.setVisibility(View.GONE);
        radioIsMerchant.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if(prefs.getBoolean(User.PREF_IS_MERCHANT, true)) {
            btnDetails.setText(getResources().getString(R.string.myshops));
        }
        else {
            btnDetails.setText(getResources().getString(R.string.myorders));
        }
        textUsername.setText(prefs.getString(User.PREF_USERNAME, "Invalid Username"));
        textEmail.setText(prefs.getString(User.PREF_EMAIL, "Invalid Email"));
        textTypeUser.setText(prefs.getBoolean(User.PREF_IS_MERCHANT, false) ? R.string.merchant : R.string.customer);
        textTitle.setText(R.string.viewAccountTitle);
        btnSave.setText(R.string.edit);
        btnSave.setBackground(getResources().getDrawable(R.drawable.button_background_brown));

    }

    private void renderEditable(User u) {
        textUsername.setVisibility(View.GONE);
        textTypeUserDescr.setVisibility(View.GONE);
        textTypeUser.setVisibility(View.GONE);
        btnDetails.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);
        textSubtitle.setVisibility(View.VISIBLE);
        editUsername.setVisibility(View.VISIBLE);

        // don't allow change of user status (merchant/customer) after create profile
        if(createProfile) {
            textIsMerchantPrompt.setVisibility(View.VISIBLE);
            radioIsMerchant.setVisibility(View.VISIBLE);
        }

        textTitle.setText(R.string.createAccountTitle);
        editUsername.setText(u.getUsername());
        textEmail.setText(u.getEmail());
        radioBtnNo.setChecked(!u.isMerchant());
        radioBtnYes.setChecked(u.isMerchant());
        textTitle.setText(R.string.createAccountTitle);
        btnSave.setText(R.string.save);
        btnSave.setBackground(getResources().getDrawable(R.drawable.button_background_green));
    }
}
