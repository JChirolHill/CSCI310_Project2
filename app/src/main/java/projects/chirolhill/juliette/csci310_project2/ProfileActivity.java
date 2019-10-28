package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import projects.chirolhill.juliette.csci310_project2.model.Customer;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Merchant;
import projects.chirolhill.juliette.csci310_project2.model.User;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    public static final String EXTRA_READONLY = "profile_extra_view"; // display as view or update

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
    private TextView textTypeUserDescr;
    private TextView textTypeUser;
    private TextView textError;
    private Button btnSave;

    private boolean readonly;
    private boolean origReadonly;

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
        textTypeUserDescr = findViewById(R.id.textTypeUserDescr);
        textTypeUser = findViewById(R.id.textTypeUser);
        textError = findViewById(R.id.textError);
        btnSave = findViewById(R.id.btnSave);

        // fetch intent and decide whether readonly or editable
        final Intent i = getIntent();
        if(i.getBooleanExtra(EXTRA_READONLY, true)) { // readonly
            readonly = true;
            origReadonly = true;
            renderReadOnly();
        }
        else { // editable
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
                textTitle.setText(R.string.createAccountTitle + ", " + v.getText().toString());
                return false;
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
                else { // save to db
                    User u;
                    if(radioIsMerchant.getCheckedRadioButtonId() == R.id.radio_yes) {
                        u = new Merchant();
                    }
                    else {
                        u = new Customer();
                    }
                    SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                    u.setuID(prefs.getString(User.PREF_USER_ID, ""));
                    u.setUsername(editUsername.getText().toString());
                    u.setEmail(textEmail.getText().toString());

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
    }

    private void renderReadOnly() {
        textUsername.setVisibility(View.VISIBLE);
        textTypeUserDescr.setVisibility(View.VISIBLE);
        textTypeUser.setVisibility(View.VISIBLE);
        textSubtitle.setVisibility(View.GONE);
        editUsername.setVisibility(View.GONE);
        textIsMerchantPrompt.setVisibility(View.GONE);
        radioIsMerchant.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String username = prefs.getString(User.PREF_USERNAME, "Invalid Username");
        String email = prefs.getString(User.PREF_EMAIL, "Invalid Email");
        textUsername.setText(prefs.getString(User.PREF_USERNAME, "Invalid Username"));
        textEmail.setText(prefs.getString(User.PREF_EMAIL, "Invalid Email"));
        textTypeUser.setText(prefs.getBoolean(User.PREF_IS_MERCHANT, false) ? R.string.merchant : R.string.customer);
        textTitle.setText(R.string.viewAccountTitle);
        btnSave.setText(R.string.edit);
    }

    private void renderEditable(User u) {
        textUsername.setVisibility(View.GONE);
        textTypeUserDescr.setVisibility(View.GONE);
        textTypeUser.setVisibility(View.GONE);
        textSubtitle.setVisibility(View.VISIBLE);
        editUsername.setVisibility(View.VISIBLE);
        textIsMerchantPrompt.setVisibility(View.VISIBLE);
        radioIsMerchant.setVisibility(View.VISIBLE);

        textTitle.setText(R.string.createAccountTitle);
        editUsername.setText(u.getUsername());
        textEmail.setText(u.getEmail());
        radioBtnNo.setChecked(!u.isMerchant());
        textTitle.setText(R.string.createAccountTitle);
        btnSave.setText(R.string.save);
    }
}
