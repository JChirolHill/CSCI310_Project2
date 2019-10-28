package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import projects.chirolhill.juliette.csci310_project2.model.Customer;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.User;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 0;

    private Button btnSignIn;
    private TextView textMoment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnSignIn = findViewById(R.id.btnSignIn);
        textMoment = findViewById(R.id.textMoment);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignIn.setVisibility(View.GONE);
                textMoment.setVisibility(View.VISIBLE);

                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build());

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // save to shared preferences
                SharedPreferences prefs = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                prefEditor.putString(User.PREF_USER_ID, user.getUid());
                prefEditor.commit();

                // initialize callback
                Database.getInstance().setCallback(new Database.Callback() {
                    @Override
                    public void dbCallback(Object o) {
                        User u = (User)o;
                        if(u == null) { // new user
                            // launch intent to profile page
                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                            i.putExtra(User.PREF_USER_ID, user.getUid());
                            i.putExtra(User.PREF_USERNAME, user.getDisplayName());
                            i.putExtra(User.PREF_EMAIL, user.getEmail());
                            startActivity(i);
                        }
                        else { // user already in database
                            // launch main activity
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    }
                });

                // retrieve user from database to check if exists (see callback above)
                Database.getInstance().getUser(user.getUid());
            }
            else {
                textMoment.setText(R.string.signinfail);
                btnSignIn.setVisibility(View.VISIBLE);
            }
        }
    }

}
