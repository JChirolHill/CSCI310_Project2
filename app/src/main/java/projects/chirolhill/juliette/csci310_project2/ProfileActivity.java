package projects.chirolhill.juliette.csci310_project2;

import android.content.Intent;
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

    private TextView textTitle;
    private TextView textSubtitle;
    private TextView textUsernamePrompt;
    private TextView textEmailPrompt;
    private TextView textIsMerchantPrompt;
    private EditText editUsername;
    private TextView editEmail;
    private RadioGroup radioIsMerchant;
    private RadioButton radioBtnNo;
    private TextView textError;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textTitle = findViewById(R.id.textProfileTitle);
        textSubtitle = findViewById(R.id.textProfileSubtitle);
        textUsernamePrompt = findViewById(R.id.textUsername);
        textEmailPrompt = findViewById(R.id.textEmail);
        textIsMerchantPrompt = findViewById(R.id.textIsMerchant);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        radioIsMerchant = findViewById(R.id.radioIsMerchant);
        radioBtnNo = findViewById(R.id.radio_no);
        textError = findViewById(R.id.textError);
        btnSave = findViewById(R.id.btnSave);

        Intent i = getIntent();

        textTitle.setText(R.string.createAccountTitle + ", " + i.getStringExtra(User.PREF_USERNAME));
        editUsername.setText(i.getStringExtra(User.PREF_USERNAME));
        editEmail.setText(i.getStringExtra(User.PREF_EMAIL));
        radioBtnNo.setChecked(true);

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
                User u;
                if(radioIsMerchant.getCheckedRadioButtonId() == R.id.radio_yes) {
                    u = new Merchant();
                }
                else {
                    u = new Customer();
                }
                u.setUsername(editUsername.getText().toString());
                u.setEmail(editEmail.getText().toString());

                String addResult = Database.getInstance().addUser(u);
                if(addResult == null) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
                else { // some errors
                    textError.setText(addResult);
                }

            }
        });
    }

}
